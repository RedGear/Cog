package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.exception.CogException;
import com.redgear.cog.exception.CogTimeoutException;

import java.util.ArrayDeque;
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
        return true;
    }

    @Override
    public CogResultMapper<T, Iterator> builder() {
        return new CogResultMapper<T, Iterator>() {

            BlockingQueue<T> items = new LinkedBlockingQueue<>();
            boolean isDone = false;

            @Override
            public void add(T next) {
                items.add(next);
            }

            @Override
            public void complete() {
                isDone = true;
            }

            @Override
            public Iterator build() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return !items.isEmpty() || !isDone;
                    }

                    @Override
                    public T next() {
                        try {
                            return items.take();
                        } catch (InterruptedException e) {
                            throw new CogTimeoutException("Gave up waiting on async result", e);
                        }
                    }
                };
            }
        };
    }
}
