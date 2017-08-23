package com.redgear.cog.rest.impl;

import com.redgear.cog.repo.CogClient;
import com.redgear.cog.repo.impl.UrlUtils;
import com.redgear.cog.rest.CogRestResultHandler;
import com.redgear.cog.rest.CogRestResultHandlerFactory;
import com.redgear.cog.rest.CogServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class CogJettyServerImpl implements CogServer {

    private static final Logger log = LoggerFactory.getLogger(CogJettyServerImpl.class);
    private final Server server = new Server();
    private final ServletHandler servletHandler = new ServletHandler();

    private final ServerConfig config;

    public CogJettyServerImpl(ServerConfig config) {
        this.config = config;
    }

    @Override
    public void addController(String contextPath, Object raw) {
        Map<String, Function<Map<String, String>, ?>> handlers = config.getControllerFactory().build(raw);

        servletHandler.addServletWithMapping(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                log.debug("Got request: {}", req.getPathInfo());
                CogRestResultHandler resultHandler = config.getHandlerFactory().build(resp);

                String path = StringUtils.substringAfterLast(req.getPathInfo(), "/");

                String query = req.getQueryString();
                Map<String, String> queryParams = query == null ? Collections.emptyMap() : UrlUtils.parseQueryParams(query);

                log.debug("Path: {}, Query: {}", path, query);
                try {
                    Function<Map<String, String>, ?> handler = handlers.get(path);

                    log.debug("Handler: {}", handler);
                    if (handler == null) {
                        resp.sendError(404);
                        return;
                    }


                    Object result = handler.apply(queryParams);
                    log.debug("Result: {}", result);
                    resultHandler.sendBody(result);
                } catch (Exception e) {
                    try {
                        log.debug("Error: ", e);
                        resultHandler.sendError(e);
                    } catch (Exception e1) {
                        log.debug("Double Error: ", e);
                        resp.sendError(500);
                    }
                }
            }
        }), contextPath);
    }

    @Override
    public void start() throws Exception {
        server.setHandler(servletHandler);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.getPort());
        server.setConnectors(new Connector[] {connector});
        server.start();
        server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }
}
