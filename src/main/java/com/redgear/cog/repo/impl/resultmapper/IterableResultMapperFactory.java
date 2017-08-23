package com.redgear.cog.repo.impl.resultmapper;

import com.redgear.cog.repo.CogResultMapper;
import com.redgear.cog.repo.CogResultMapperFactory;

import java.util.ArrayList;

public class IterableResultMapperFactory<T> implements CogResultMapperFactory<T, Iterable> {

    @Override
    public Class<Iterable> type() {
        return Iterable.class;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public CogResultMapper<T, Iterable> builder() {
        return new CogResultMapper<T, Iterable>() {

            ArrayList<T> items = new ArrayList<>();

            @Override
            public void add(T next) {
                items.add(next);
            }

            @Override
            public void complete() {
                items.trimToSize();
            }

            @Override
            public Iterable build() {
                return items;
            }
        };
    }
}
