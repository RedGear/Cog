package com.redgear.cog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Created by LordBlackHole on 2017-06-05.
 */
public interface CogStatement<T> {
    List<T> query();

    List<T> query(Map<String, ?> params);

    ResultSource<T> queryAsync();

    ResultSource<T> queryAsync(Map<String, ?> params);

    <C> C query(Map<String, ?> params, CogResultMapperFactory<T, C> mapper);

    int execute(Map<String, ?> params);

    CompletableFuture<Integer> executeAsync(Map<String, ?> params);

    <C> C execute(Map<String, ?> params, CogResultMapperFactory<Integer, C> mapper);
}
