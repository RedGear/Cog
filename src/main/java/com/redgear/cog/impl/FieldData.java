package com.redgear.cog.impl;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FieldData<T> {

    private final String name;
    private final Class<T> type;
    private final BiConsumer<ResultContext, T> setter;


    public FieldData(String name, Class<T> type, BiConsumer<ResultContext, T> setter) {
        this.name = name;
        this.type = type;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public BiConsumer<ResultContext, T> getSetter() {
        return setter;
    }
}
