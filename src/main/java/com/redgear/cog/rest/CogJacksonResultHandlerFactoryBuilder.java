package com.redgear.cog.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface CogJacksonResultHandlerFactoryBuilder {

    CogJacksonResultHandlerFactoryBuilder setObjectMapper(ObjectMapper mapper);

    CogJacksonResultHandlerFactoryBuilder setDebug(boolean debug);

    CogRestResultHandlerFactory build();


}
