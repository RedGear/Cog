package com.redgear.cog.impl;

import java.sql.PreparedStatement;

public interface BeanGetter<T> {
    void write(T obj, PreparedStatement statement, int index);
}
