package com.redgear.cog.rest.impl;

import com.redgear.cog.rest.CogRestResultHandlerFactory;
import com.redgear.cog.rest.ParamMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfig {


    private final int port;

    private final CogRestResultHandlerFactory handlerFactory;
    private final ControllerFactory controllerFactory;
    private final Map<Class, ParamMapper> mappers = new HashMap<>();


    public ServerConfig(int port, CogRestResultHandlerFactory handlerFactory, List<ParamMapper<?>> mappers) {
        this.port = port;
        this.handlerFactory = handlerFactory;
        this.controllerFactory = new ControllerFactory(this);

        for (ParamMapper mapper : mappers) {
            this.mappers.put(mapper.getType(), mapper);
        }
    }


    public int getPort() {
        return port;
    }

    public CogRestResultHandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    public ControllerFactory getControllerFactory() {
        return controllerFactory;
    }

    public Map<Class, ParamMapper> getMappers() {
        return mappers;
    }
}
