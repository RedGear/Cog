package com.redgear.cog.impl;


import java.sql.ResultSet;

public interface BeanSetter<T> {

    void set(T t, ResultSet result, String label);

}
