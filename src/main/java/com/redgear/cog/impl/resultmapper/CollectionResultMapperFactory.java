package com.redgear.cog.impl.resultmapper;

import com.redgear.cog.CogResultMapper;
import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.exception.CogReflectionException;

import java.util.Collection;
import java.util.function.Supplier;

public class CollectionResultMapperFactory {

    public static <T, S extends Collection<T>, C extends S> CogResultMapperFactory<T, S> build(Class<C> collection, Class<S> superCollection) {
        Supplier<S> constructor = () -> {
            try {
                return collection.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new CogReflectionException("Exception creating collection " + collection, e);
            }
        };

        return new CogResultMapperFactory<T, S>() {
            @Override
            public Class<S> type() {
                return superCollection;
            }

            @Override
            public boolean isAsync() {
                return false;
            }

            @Override
            public CogResultMapper<T, S> builder() {
                return new CogResultMapper<T, S>(){

                    S col = constructor.get();

                    @Override
                    public void add(T next) {
                        col.add(next);
                    }

                    @Override
                    public void complete() {

                    }

                    @Override
                    public S build() {
                        return col;
                    }
                };
            }
        };

    }


}
