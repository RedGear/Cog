package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.exception.CogException;
import com.redgear.cog.exception.CogTimeoutException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
