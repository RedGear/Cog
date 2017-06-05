package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureResultMapperFactory<T> implements CogResultMapperFactory<T, CompletableFuture> {

    @Override
    public Class<CompletableFuture> type() {
        return CompletableFuture.class;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public CogResultMapper<T, CompletableFuture> builder() {
        return new CogResultMapper<T, CompletableFuture>(){

            CompletableFuture<T> result = new CompletableFuture<>();

            @Override
            public void add(T next) {
                result.complete(next);
            }

            @Override
            public void complete() {

            }

            @Override
            public CompletableFuture build() {
                return result;
            }
        };
    }
}
