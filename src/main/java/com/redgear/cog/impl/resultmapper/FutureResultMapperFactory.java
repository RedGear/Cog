package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class FutureResultMapperFactory<T> implements CogResultMapperFactory<T, Future> {

    private static final Logger log = LoggerFactory.getLogger(FutureResultMapperFactory.class);

    @Override
    public Class<Future> type() {
        return Future.class;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public CogResultMapper<T, Future> builder() {
        return new CogResultMapper<T, Future>() {

            CompletableFuture<T> result = new CompletableFuture<>();

            @Override
            public void add(T next) {
                result.complete(next);
            }

            @Override
            public void complete() {
                if(!result.isDone()) {
                    result.complete(null);
                }
            }

            @Override
            public Future build() {
                return result;
            }
        };
    }

}
