package com.redgear.cog.rest;

import javax.servlet.http.HttpServletResponse;

public interface CogRestResultHandlerFactory {

    CogRestResultHandler build(HttpServletResponse resp);

}
