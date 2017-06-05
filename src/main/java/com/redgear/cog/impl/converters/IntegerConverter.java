package com.redgear.cog.impl.converters;

import com.redgear.cog.impl.TypeConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerConverter implements TypeConverter<Integer> {

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public void write(Integer obj, PreparedStatement statement, int index) throws SQLException {
        if (obj == null) {
            statement.setString(index, null);
        } else {
            statement.setInt(index, obj);
        }
    }

    @Override
    public Integer read(ResultSet result, String label) throws SQLException {
        return result.getInt(label);
    }
}
