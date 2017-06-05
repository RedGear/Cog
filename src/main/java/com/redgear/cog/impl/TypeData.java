package com.redgear.cog.impl;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

public class TypeData<T> {

    private static final Logger log = LoggerFactory.getLogger(TypeData.class);

    private final Class<T> type;
    private final Supplier<T> constructor;
    private final Map<String, FieldData> fieldData = new HashMap<>();

    public TypeData(Class<T> type, Supplier<T> constructor, List<FieldData> fieldData) {
        this.type = type;
        this.constructor = constructor;
        fieldData.forEach(data -> {
            this.fieldData.put(data.getName(), data);
        });
    }

    public T create() {
        return constructor.get();
    }

    public FieldData getField(String key) {
        return fieldData.get(key);
    }



}
