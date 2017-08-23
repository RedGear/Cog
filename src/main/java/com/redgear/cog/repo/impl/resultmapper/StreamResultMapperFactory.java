package com.redgear.cog.repo.impl.resultmapper;

import com.redgear.cog.repo.CogResultMapper;
import com.redgear.cog.repo.CogResultMapperFactory;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

public class StreamResultMapperFactory<T> implements CogResultMapperFactory<T, Stream> {

    @Override
    public Class<Stream> type() {
        return Stream.class;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public CogResultMapper<T, Stream> builder() {
        return new CogResultMapper<T, Stream>(){

            Builder<T> builder = Stream.builder();

            public void add(T next) {
                builder.accept(next);
            }

            @Override
            public void complete() {

            }

            @Override
            public Stream build() {
                return builder.build();
            }
        };
    }
}
