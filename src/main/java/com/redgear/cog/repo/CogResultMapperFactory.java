package com.redgear.cog.repo;

public interface CogResultMapperFactory<T, C> {

    Class<C> type();

    boolean isAsync();

    CogResultMapper<T, C> builder();

}
