package com.redgear.cog.impl.converters;

import com.redgear.cog.impl.TypeConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringConverter implements TypeConverter<String> {


    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public void write(String obj, PreparedStatement statement, int index) throws SQLException {
        statement.setString(index, obj);
    }

    @Override
    public String read(ResultSet result, String label) throws SQLException {
        return result.getString(label);
    }
}
