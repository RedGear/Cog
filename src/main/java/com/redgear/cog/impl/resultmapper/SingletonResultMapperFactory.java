package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.exception.CogQueryException;

public class SingletonResultMapperFactory<T> implements CogResultMapperFactory<T, T> {

    @Override
    public Class<T> type() {
        return null;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public CogResultMapper<T, T> builder() {
        return new CogResultMapper<T, T>() {

            T value;

            @Override
            public void add(T next) {
                if (value != null) {
                    throw new CogQueryException("Query expected single result but got more than one.");
                } else {
                    value = next;
                }
            }

            @Override
            public void complete() {

            }

            @Override
            public T build() {
                return value;
            }
        };
    }
}
