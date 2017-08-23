package com.redgear.cog.repo.impl;

import com.redgear.cog.core.impl.Caseifier;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

public class BeanTypeData<T> implements TypeData<T> {

    private static final Logger log = LoggerFactory.getLogger(BeanTypeData.class);

    private final Class<T> type;
    private final Supplier<T> constructor;
    private final Map<String, FieldData> fieldData = new HashMap<>();

    public BeanTypeData(Class<T> type, Supplier<T> constructor, List<FieldData> fieldData) {
        this.type = type;
        this.constructor = constructor;
        fieldData.forEach(data -> {
            this.fieldData.put(data.getName(), data);
        });
    }

    @Override
    public T create(List<String> columnNames, ResultSet rs) {
        T obj = constructor.get();

        for (int i = 1; i <= columnNames.size(); i++) {
            String key = columnNames.get(i - 1);
            FieldData field = fieldData.get(Caseifier.camelCase(key));
            if (field != null) {
                field.getSetter().accept(new ResultContext(rs, key), obj);
            }
        }

        return obj;
    }
}
