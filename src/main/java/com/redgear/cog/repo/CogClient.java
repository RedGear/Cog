package com.redgear.cog.repo;

import com.redgear.cog.repo.impl.CogClientBuilderImpl;
import org.intellij.lang.annotations.Language;

/**
 * Created by LordBlackHole on 2017-06-05.
 */
public interface CogClient {
    static CogClientBuilder builder() {
        return new CogClientBuilderImpl();
    }

    <T> T buildRepository(Class<T> clazz);

    <T> CogStatement<T> prepareStatement(@Language("SQL") String query, Class<T> resultType);
}
