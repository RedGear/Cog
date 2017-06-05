package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import java.util.stream.StreamSupport;

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
