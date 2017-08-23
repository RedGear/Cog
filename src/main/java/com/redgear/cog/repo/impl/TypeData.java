package com.redgear.cog.repo.impl;

import java.sql.ResultSet;
import java.util.List;

public interface TypeData<T> {
    T create(List<String> columnNames, ResultSet rs);
}
