package com.redgear.cog;

import java.util.List;
import java.util.Map;

/**
 * Created by LordBlackHole on 2017-06-05.
 */
public interface CogStatement<T> {
    List<T> exec();

    List<T> exec(Map<String, ?> params);

    <C> C exec(Map<String, ?> params, CogResultMapperFactory<T, C> mapper);
}
