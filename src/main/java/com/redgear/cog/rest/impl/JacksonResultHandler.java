package com.redgear.cog.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redgear.cog.rest.CogRestResultHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

public class JacksonResultHandler implements CogRestResultHandler {

    private static final Logger log = LoggerFactory.getLogger(JacksonResultHandler.class);
    private final HttpServletResponse resp;
    private final ObjectMapper mapper;
    private final boolean debug;

    public JacksonResultHandler(HttpServletResponse resp, ObjectMapper mapper, boolean debug) {
        this.resp = resp;
        this.mapper = mapper;
        this.debug = debug;
    }

    @Override
    public void sendBody(Object body) throws Exception {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), body);
    }

    @Override
    public void sendError(Throwable err) throws Exception  {
        if (debug) {
            resp.sendError(500, ExceptionUtils.getStackTrace(err));
        } else {
            resp.sendError(500, "Internal Server Error");
        }

    }

}
