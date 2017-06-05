package com.redgear.cog.impl.converters;

import com.redgear.cog.impl.TypeConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongConverter implements TypeConverter<Long> {
    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public void write(Long obj, PreparedStatement statement, int index) throws SQLException {
        statement.setLong(index, obj);
    }

    @Override
    public Long read(ResultSet result, String label) throws SQLException {
        return result.getLong(label);
    }
}
