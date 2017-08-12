package com.redgear.cog.impl;


import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.CogStatement;
import com.redgear.cog.ResultSource;
import com.redgear.cog.exception.CogConversionException;
import com.redgear.cog.exception.CogQueryException;
import com.redgear.cog.exception.CogReflectionException;
import com.redgear.cog.exception.CogSqlException;
import com.redgear.cog.impl.resultmapper.SingletonResultMapperFactory;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CogStatementImpl<T> implements CogStatement<T> {

    private static final Logger log = LoggerFactory.getLogger(CogStatement.class);

    private final Config config;
    private final String sql;
    private final List<String> keys;
    private final TypeData<T> typeData;

    public CogStatementImpl(Config config, String sql, List<String> keys, TypeData<T> typeData) {
        this.config = config;
        this.sql = sql;
        this.keys = keys;
        this.typeData = typeData;
    }

    @Override
    public List<T> query() {
        return query(Collections.emptyMap());
    }

    @Override
    public List<T> query(Map<String, ?> params) {
        return (List<T>) query(params, config.getResultMapperFactory(List.class));
    }

    @Override
    public ResultSource<T> queryAsync() {
        return queryAsync(Collections.emptyMap());
    }

    @Override
    public ResultSource<T> queryAsync(Map<String, ?> params) {
        return ((ResultSource<T>) query(params, config.getResultMapperFactory(ResultSource.class)));
    }

    @Override
    public <C> C query(Map<String, ?> params, CogResultMapperFactory<T, C> mapper) {
        Objects.requireNonNull(mapper, "CogStatement.query cannot accept a null mapper. ");
        Object[] values = extractParams(params);

        CogResultMapper<T, C> builder = mapper.builder();
        ResultSetHandler<String> handler = buildHandler(builder);

        try {
            if (mapper.isAsync()) {
                config.getIoPool().execute(() -> {
                    try {
                        config.getRunner().query(sql, handler, values);
                    } catch (SQLException e) {
                        builder.error(e);
                    }
                });
            } else {
                config.getRunner().query(sql, handler, values);
            }

            C result = builder.build();

            checkResultType(mapper, result);

            return result;
        } catch (SQLException e) {
            throw new CogSqlException(e);
        }
    }

    @Override
    public int execute(Map<String, ?> params) {
        Object[] values = extractParams(params);

        try {
            return config.getRunner().update(sql, values);
        } catch (SQLException e) {
            throw new CogSqlException(e);
        }
    }

    @Override
    public CompletableFuture<Integer> executeAsync(Map<String, ?> params) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        Object[] values = extractParams(params);

        try {
            result.complete(config.getRunner().update(sql, values));
        } catch (SQLException e) {
            result.completeExceptionally(new CogSqlException(e));
        }

        return result;
    }

    @Override
    public <C> C execute(Map<String, ?> params, CogResultMapperFactory<Integer, C> mapper) {
        Objects.requireNonNull(mapper, "CogStatement.execute cannot accept a null mapper. ");
        Object[] values = extractParams(params);

        CogResultMapper<Integer, C> builder = mapper.builder();

        try {
            if (mapper.isAsync()) {
                config.getIoPool().execute(() -> {
                    try {
                        log.debug("Running query: {}, with values: {}", sql, values);
                        builder.add(config.getRunner().update(sql, values));
                    } catch (SQLException e) {
                        builder.error(e);
                    }
                });

            } else {
                builder.add(config.getRunner().update(sql, values));
            }

            C result = builder.build();

            checkResultType(mapper, result);

            return result;
        } catch (SQLException e) {
            throw new CogSqlException(e);
        }
    }

    private <C> void checkResultType(CogResultMapperFactory<?, C> mapper, C result) {
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

            while (rs.next()) {
                builder.add(typeData.create(columnNames, rs));
            }

            builder.complete();
            return "";
        };
    }

    @NotNull
    private Object[] extractParams(Map<String, ?> params) {
        return keys.stream().map(key -> {
            if (key.contains(".")) {
                List<String> chain = Arrays.asList(key.split("\\."));

                Object value = params.get(chain.get(0));

                for (int index = 1; index < chain.size(); index++) {
                    if (value == null) {
                        String path = chain.stream().limit(index).collect(Collectors.joining("."));
                        throw new NullPointerException("Parameter value " + path + " is null running sql " + sql);
                    }

                    if (value instanceof Map) {
                        Map<String, ?> m = (Map<String, ?>) value;
                        value = m.get(chain.get(index));
                    } else {
                        try {
                            Field f = value.getClass().getDeclaredField(chain.get(index));
                            f.setAccessible(true);
                            value = f.get(value);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            String path = chain.stream().limit(index + 1).collect(Collectors.joining("."));
                            throw new CogReflectionException("Parameter field " + path + " does not exist running sql " + sql, e);
                        }
                    }
                }

                return value;
            } else {
                Object p = params.get(key);

                if (p == null) {
                    throw new CogQueryException("Parameter '" + key + "' has no matching argument: Args: " + params);
                }

                return p;
            }
        }).toArray();
    }

    void validateArguments(List<String> args) {
        keys.stream()
                .map(key -> key.contains(".") ? StringUtils.substringBefore(key, ".") : key)
                .forEach(key -> {
                if (!args.contains(key)) {
                    throw new CogQueryException("Parameter '" + key + "' has no matching argument: Args: " + args);
                }
        });
    }

}
