package com.redgear.cog.impl;

import com.redgear.cog.TypeConverter;
import com.redgear.cog.exception.CogConversionException;
import com.redgear.cog.exception.CogQueryException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SimpleTypeData<T> implements TypeData<T> {

    private final TypeConverter<T> converter;

    public SimpleTypeData(TypeConverter<T> converter) {
        this.converter = converter;
    }

    @Override
    public T create(List<String> columnNames, ResultSet rs) {
        if (columnNames.size() != 1) {
            throw new CogQueryException("Expected query to return only single value of type " + converter.getType() + " but found '" + columnNames + "'");
        }

        try {
            return converter.read(new ResultContext(rs, columnNames.get(0)));
        } catch (SQLException e) {
            throw new CogConversionException("Could not read simple value of type " + converter.getType(), e);
        }
    }
}
