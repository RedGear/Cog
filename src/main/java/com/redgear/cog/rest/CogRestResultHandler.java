package com.redgear.cog.rest;

import java.io.IOException;

public interface CogRestResultHandler {

    void sendBody(Object body) throws IOException, Exception;

    void sendError(Throwable err) throws Exception;

}
