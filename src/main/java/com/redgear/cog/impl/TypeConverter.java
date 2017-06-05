package com.redgear.cog.impl;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeConverter<T> {

    Class<T> getType();

    void write(T obj, PreparedStatement statement, int index) throws SQLException;

    T read(ResultSet result, String label) throws SQLException;

}
