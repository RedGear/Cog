package com.redgear.cog.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redgear.cog.rest.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CogServerBuilderImpl implements CogServerBuilder {

    private static final ArrayList<ParamMapper<?>> baseMappers = new ArrayList<>();

    static {
        addBaseMapper(String.class, Function.identity());
        addBaseMapper(Byte.class, Byte::parseByte);
        addBaseMapper(Short.class, Short::parseShort);
        addBaseMapper(Integer.class, Integer::parseInt);
        addBaseMapper(Long.class, Long::parseLong);
        addBaseMapper(Float.class, Float::parseFloat);
        addBaseMapper(Double.class, Double::parseDouble);

        addBaseMapper(byte.class, Byte::parseByte);
        addBaseMapper(short.class, Short::parseShort);
        addBaseMapper(int.class, Integer::parseInt);
        addBaseMapper(long.class, Long::parseLong);
        addBaseMapper(float.class, Float::parseFloat);
        addBaseMapper(double.class, Double::parseDouble);
        baseMappers.trimToSize();
    }

    private static <T> void addBaseMapper(Class<T> type, Function<String, T> mapper) {
        baseMappers.add(new ParamMapper<T>() {

            @Override
            public Class<T> getType() {
                return type;
            }

            @Override
            public T parse(String input) throws Exception {
                return mapper.apply(input);
            }
        });
    }

    private CogServerType serverType = CogServerType.JETTY;
    private int port = 8080;

    private CogRestResultHandlerFactory handlerFactory;
    private List<ParamMapper<?>> mappers = new ArrayList<>(baseMappers);

    @Override
    public CogServerBuilder useJetty() {
        this.serverType = CogServerType.JETTY;
        return this;
    }

    @Override
    public CogServerBuilder setServerType(CogServerType serverType) {
        this.serverType = serverType;
        return this;
    }

    @Override
    public CogServerBuilder setPort(int port) {
        this.port = port;
        return this;
    }


    @Override
    public CogServer build() {
        if (handlerFactory == null) {
            handlerFactory = new JacksonResultHandlerFactory(new ObjectMapper(), false);
        }

        return new CogJettyServerImpl(new ServerConfig(port, handlerFactory, mappers));
    }

}
