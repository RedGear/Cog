package com.redgear.cog.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redgear.cog.rest.CogJacksonResultHandlerFactoryBuilder;
import com.redgear.cog.rest.CogRestResultHandlerFactory;

public class CogJacksonResultHandlerFactoryBuilderImpl implements CogJacksonResultHandlerFactoryBuilder {

    private ObjectMapper mapper;
    private boolean debug;

    @Override
    public CogJacksonResultHandlerFactoryBuilder setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    @Override
    public CogJacksonResultHandlerFactoryBuilder setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    @Override
    public CogRestResultHandlerFactory build() {
        return new JacksonResultHandlerFactory(mapper, debug);
    }
}
