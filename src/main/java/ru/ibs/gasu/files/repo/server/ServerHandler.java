package ru.ibs.gasu.files.repo.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServerHandler extends AbstractHandler {

    private static Logger log = LoggerFactory.getLogger(ServerHandler.class);
    private Server server = null;
    private boolean restartPlease = false;

    private int port;

    public ServerHandler(Server server, int port) {
        this.server = server;
        this.port = port;
    }

    private boolean stopServer(HttpServletResponse response) throws IOException {
        log.warn("Stopping Jetty");
        response.setStatus(202);
        response.setContentType("text/plain");
        response.flushBuffer();
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        log.info("Shutting down Jetty...");
                        server.stop();
                        log.info("Jetty has stopped.");
                    } catch (Exception ex) {
                        log.error("Error when stopping Jetty: " + ex.getMessage(), ex);
                    }
                }
            }.start();
        } catch (Exception ex) {
            log.error("Unable to stop Jetty: " + ex);
            return false;
        }
        return true;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String pathInfo = request.getPathInfo();
        if ("/stop".equals(pathInfo)) {
            stopServer(response);
            return;
        }
        if ("/restart".equals(pathInfo)) {
            restartPlease = true;
            stopServer(response);
            return;
        }

    }

    public boolean isRestartPlease() {
        return restartPlease;
    }
}