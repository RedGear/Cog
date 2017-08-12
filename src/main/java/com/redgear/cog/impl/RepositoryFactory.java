package com.redgear.cog.impl;

import com.redgear.cog.*;
import com.redgear.cog.exception.CogReflectionException;
import com.redgear.cog.impl.resultmapper.SingletonResultMapperFactory;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RepositoryFactory {

    private static final Logger log = LoggerFactory.getLogger(RepositoryFactory.class);
    private static final SingletonResultMapperFactory singletonMapper = new SingletonResultMapperFactory();
    private final CogClient client;
    private final Config config;

    public RepositoryFactory(CogClient client, Config config) {
        this.client = client;
        this.config = config;
    }


    public <T> T create(Class<T> source) {

        if(!source.isInterface()) {
            throw new CogReflectionException("Class " + source + " is not an interface. Only interfaces can be turned into Repositories");
        }

        Map<Method, RepositoryCall> methods = new HashMap<>();

        for (Method method : source.getMethods()) {

            CogQuery cogQuery = method.getAnnotation(CogQuery.class);

            if (cogQuery == null) {
                throw new CogReflectionException("Missing a @CogQuery annotation for method: " + method);
            }

            @Language("SQL")
            String query = cogQuery.value();

            String[] args = Arrays.stream(method.getParameters()).map(param -> {
                for (Annotation annotation : param.getAnnotations()) {
                    if(annotation instanceof CogParam) {
                        CogParam cogAnnotation = (CogParam) annotation;
                        return cogAnnotation.value();
                    }
                }
                // No annotation
                return param.getName();
            }).toArray(String[]::new);

            log.debug("Repository method: {} args: {}", method, args);

            Type returnType = method.getGenericReturnType();

            if (returnType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) returnType;

                Class outerType = (Class) paramType.getRawType();
                Type[] typeArguments = paramType.getActualTypeArguments();

                if (typeArguments.length != 1) {
                    throw new CogReflectionException("Cannot build repository with method " + method + " as it has an illegal return type " + returnType + ". Return types cannot use generic types with more than one parameter. ");
                }

                Type firstType = typeArguments[0];


                if(firstType instanceof ParameterizedType) {
                    throw new CogReflectionException("Cannot build repository with method " + method + " as it has an illegal return type " + returnType + ". Return types cannot have nested generic parameter. ");
                }

                Class innerType = (Class) firstType;

                CogResultMapperFactory mapperFactory = config.getResultMapperFactory(outerType);

                if(mapperFactory == null) {
                    throw new CogReflectionException("Cannot build repository with method " + method + " as it has an illegal return type " + returnType + ". No CogResultMapperFactory for type: " + outerType + " has been registered. ");
                }

                CogStatement<?> statement = client.prepareStatement(query, innerType);
                methods.put(method, new RepositoryCall(statement, args, mapperFactory));
            } else if (returnType instanceof Class) {
                Class<?> result = (Class<?>) returnType;

                CogStatement<?> statement = client.prepareStatement(query, result);
                methods.put(method, new RepositoryCall(statement, args, singletonMapper));
            } else {
                throw new CogReflectionException("Cannot build repository with method " + method + " as it has an illegal return type " + returnType);
            }
        }

        @SuppressWarnings("unchecked")
        T result = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{source}, (proxy, method, args) -> {
            RepositoryCall call = methods.get(method);

            if(call == null) {
                throw new CogReflectionException("Attempt to call unknown proxy method " + method + " on repository " + source);
            }

            return call.call(args);
        });

        return result;
    }

    private static class RepositoryCall {

        private final CogStatement statement;
        private final String[] params;
        private final CogResultMapperFactory mapperFactory;

        private RepositoryCall(CogStatement statement, String[] params, CogResultMapperFactory mapperFactory) {
            this.statement = statement;
            this.params = params;
            this.mapperFactory = mapperFactory;
        }

        public Object call(Object[] args) {
            return statement.exec(Lambda.zipMap(Lambda.arrayIterator(params), Lambda.arrayIterator(args)), mapperFactory);
        }

    }

}
