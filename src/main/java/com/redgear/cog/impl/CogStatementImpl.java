package com.redgear.cog.impl;


import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.CogStatement;
import com.redgear.cog.exception.CogConversionException;
import com.redgear.cog.exception.CogSqlException;
import com.redgear.cog.impl.resultmapper.SingletonResultMapperFactory;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class CogStatementImpl<T> implements CogStatement<T> {

    private static final Logger log = LoggerFactory.getLogger(CogStatement.class);

    private final Config config;
    private final String query;
    private final List<String> keys;
    private final TypeData<T> typeData;

    public CogStatementImpl(Config config, String query, List<String> keys, TypeData<T> typeData) {
        this.config = config;
        this.query = query;
        this.keys = keys;
        this.typeData = typeData;
    }

    @Override
    public List<T> exec() {
        return exec(Collections.emptyMap());
    }

    @Override
    public List<T> exec(Map<String, ?> params) {
        return (List<T>) exec(params, config.getResultMapperFactory(List.class));
    }

    @Override
    public <C> C exec(Map<String, ?> params, CogResultMapperFactory<T, C> mapper) {
        Objects.requireNonNull(mapper, "CogStatement.exec cannot accept a null mapper. ");
        Object[] values = keys.stream().map(params::get).toArray();

        CogResultMapper<T, C> builder = mapper.builder();
        ResultSetHandler<String> handler = buildHandler(builder);

        try {
            if (mapper.isAsync()) {
                config.getAsyncRunner().query(query, handler, values);
            } else {
                config.getRunner().query(query, handler, values);
            }

            C result = builder.build();

            checkResultType(mapper, result);

            return result;
        } catch (SQLException e) {
            throw new CogSqlException(e);
        }
    }

    private <C> void checkResultType(CogResultMapperFactory<T, C> mapper, C result) {
        if (mapper instanceof SingletonResultMapperFactory) {
            return;
        }

        if (result == null || !mapper.type().isAssignableFrom(result.getClass())) {
            throw new CogConversionException("CogResultMapperFactory " + mapper.getClass() +
                    " returned a CogResultMapper that did NOT implement the type specified in the CogResultMapperFactory! Expected: "
                    + mapper.type() + " but found: " + (result == null ? null : result.getClass()));
        }
    }

    private <C> ResultSetHandler<String> buildHandler(CogResultMapper<T, C> builder) {
        return (ResultSet rs) -> {
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();

            List<String> columnNames = new ArrayList<>(columnCount);

            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnLabel(i));
            }

            log.debug("Columns: {}", columnNames);

            while (rs.next()) {
                T obj = typeData.create();

                for (int i = 1; i <= columnNames.size(); i++) {
                    String key = columnNames.get(i - 1);
                    FieldData field = typeData.getField(Caseifier.camelCase(key));
                    if (field != null) {
                        field.getSetter().accept(new ResultContext(rs, key), obj);
                    }
                }

                builder.add(obj);
            }

            builder.complete();
            return "";
        };
    }

}
