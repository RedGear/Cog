package com.redgear.cog.impl;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FieldData<T> {

    private final String name;
    private final Class<T> type;
    private final BeanGetter<T> getter;
    private final BeanSetter<T> setter;


    public FieldData(String name, Class<T> type, BeanGetter<T> getter, BeanSetter<T> setter) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public BeanGetter<T> getGetter() {
        return getter;
    }

    public BeanSetter<T> getSetter() {
        return setter;
    }
}
