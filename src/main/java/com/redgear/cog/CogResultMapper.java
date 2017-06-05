package com.redgear.cog;

public interface CogResultMapper<T, C> {

    void add(T next);

    void complete();

    C build();

}
