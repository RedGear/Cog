package com.redgear.cog.rest;

import com.redgear.cog.rest.impl.CogServerBuilderImpl;

public interface CogServer {

    static CogServerBuilder builder() {
        return new CogServerBuilderImpl();
    }

    void addController(String contextPath, Object raw);

    void start() throws Exception;


    void stop() throws Exception;
}
