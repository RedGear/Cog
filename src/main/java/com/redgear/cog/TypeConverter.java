package com.redgear.cog;


import com.redgear.cog.impl.ResultContext;

import java.sql.SQLException;

public interface TypeConverter<T> {

    Class<T> getType();

    T read(ResultContext context) throws SQLException;

}
