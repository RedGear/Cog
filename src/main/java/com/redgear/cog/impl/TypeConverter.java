package com.redgear.cog.impl;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeConverter<T> {

    Class<T> getType();

    T read(ResultContext context) throws SQLException;

}
