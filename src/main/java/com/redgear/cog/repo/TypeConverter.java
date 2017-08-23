package com.redgear.cog.repo;


import com.redgear.cog.repo.impl.ResultContext;

import java.sql.SQLException;

public interface TypeConverter<T> {

    Class<T> getType();

    T read(ResultContext context) throws SQLException;

}
