package com.redgear.cog;

public interface CogResultMapperFactory<T, C> {

    Class<C> type();

    boolean isAsync();

    CogResultMapper<T, C> builder();

}
