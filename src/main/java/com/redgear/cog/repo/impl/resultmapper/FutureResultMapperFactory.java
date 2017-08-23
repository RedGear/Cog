package com.redgear.cog.repo.impl.resultmapper;

import com.redgear.cog.repo.CogResultMapper;
import com.redgear.cog.repo.CogResultMapperFactory;
import com.redgear.cog.exception.CogQueryException;
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
                if (result.isDone()) {
                    result.obtrudeException(new CogQueryException("Query expected single result but got more than one."));
                } else {
                    result.complete(next);
                }
            }

            @Override
            public void error(Throwable t) {
                result.completeExceptionally(t);
            }

            @Override
            public void complete() {
                if(!result.isDone()) {
                    result.completeExceptionally(new CogQueryException("Query expected single result but found none."));
                }
            }

            @Override
            public Future build() {
                return result;
            }
        };
    }

}
