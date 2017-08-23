package com.redgear.cog.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redgear.cog.rest.CogRestResultHandler;
import com.redgear.cog.rest.CogRestResultHandlerFactory;

import javax.servlet.http.HttpServletResponse;

public class JacksonResultHandlerFactory implements CogRestResultHandlerFactory {

    private final ObjectMapper mapper;
    private final boolean debug;

    public JacksonResultHandlerFactory(ObjectMapper mapper, boolean debug) {
        this.mapper = mapper;
        this.debug = debug;
    }


    @Override
    public CogRestResultHandler build(HttpServletResponse resp) {
        return new JacksonResultHandler(resp, mapper, debug);
    }
}
