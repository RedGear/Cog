package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;

import java.util.Iterator;

public class IterableResultMapperFactory<T> implements CogResultMapperFactory<T, Iterable> {

    private final IteratorResultMapperFactory<T> source = new IteratorResultMapperFactory<>();

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

            CogResultMapper<T, Iterator> iteratorSource = source.builder();

            @Override
            public void add(T next) {
                iteratorSource.add(next);
            }

            @Override
            public void complete() {
                iteratorSource.complete();
            }

            @Override
            public Iterable build() {
                return () -> iteratorSource.build();
            }
        };
    }
}
