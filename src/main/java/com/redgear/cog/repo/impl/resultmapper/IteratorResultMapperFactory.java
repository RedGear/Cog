package com.redgear.cog.repo.impl.resultmapper;

import com.redgear.cog.repo.CogResultMapper;
import com.redgear.cog.repo.CogResultMapperFactory;

import java.util.ArrayList;
import java.util.Iterator;

public class IteratorResultMapperFactory<T> implements CogResultMapperFactory<T, Iterator> {

    @Override
    public Class<Iterator> type() {
        return Iterator.class;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public CogResultMapper<T, Iterator> builder() {
        return new CogResultMapper<T, Iterator>() {

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
            public Iterator build() {
                return items.iterator();
            }
        };
    }
}
