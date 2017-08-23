package com.redgear.cog.repo.impl.resultmapper;

import com.redgear.cog.repo.CogResultMapper;
import com.redgear.cog.repo.CogResultMapperFactory;
import com.redgear.cog.core.ResultSource;
import com.redgear.cog.core.impl.ResultSourceImpl;

public class ResultSourceResultMapperFactory<T> implements CogResultMapperFactory<T, ResultSource> {
    @Override
    public Class<ResultSource> type() {
        return ResultSource.class;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public CogResultMapper<T, ResultSource> builder() {
        return new CogResultMapper<T, ResultSource>() {

            ResultSourceImpl<T> out = new ResultSourceImpl<>();

            @Override
            public void add(T next) {
                out.next(next);
            }

            @Override
            public void error(Throwable t) {
                out.error(t);
            }

            @Override
            public void complete() {
                out.complete();
            }

            @Override
            public ResultSource build() {
                return out;
            }
        };
    }
}
