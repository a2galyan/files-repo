package ru.ibs.gasu.files.repo;

import org.apache.cxf.helpers.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ibs.gasu.files.repo.server.ServerHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.TimeZone;

public class Main {

    private static int PORT;
    private static String LOGS_PATH;
    private static String WAR_LOCATION = "";

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        InputStream stream = Main.class.getClassLoader().getResourceAsStream("application.properties");
        properties.load(stream);
        stream.close();
        PropertyConfigurator.configure(properties);

        PORT = Integer.valueOf(properties.getProperty("base.port"));
        LOGS_PATH = properties.getProperty("logs.path");

        if (args.length == 1 && ("stop".equalsIgnoreCase(args[0]) || "restart".equalsIgnoreCase(args[0]))) {
            URL url = new URL("http", "localhost", PORT, "/" + args[0]);
            try  {
                InputStream in = url.openStream();
                logger.info(IOUtils.toString(in));
            } catch (IOException ex) {
                logger.error("stop Jetty failed: " + ex.getMessage());
            }
            return;
        }

        WAR_LOCATION = Main.class.getClassLoader().getResource("application.properties").getFile().replace("classes/application.properties", "");
        if (new File(WAR_LOCATION + "frgu-smev-1.0.war").exists()) {
            WAR_LOCATION += "frgu-smev-1.0.war";
        } else {
            WAR_LOCATION = WAR_LOCATION.replace("!/application.properties", "");
        }

        while (true) {
            logger.info("Starting Jetty on port " + PORT);
            Server server = new Server();
            WebAppContext context = new WebAppContext();
            SocketConnector connector = new SocketConnector();

            setupConnector(connector);
            setupContext(server, context);
            ServerHandler serverHandler = new ServerHandler(server, PORT);
            setupServer(server, context, connector, serverHandler);

            startServer(server);

            logger.info("Jetty stopped");
            if (!serverHandler.isRestartPlease()) {
                break;
            }
            logger.warn("Restarting Jetty");
        }
    }

    private static void startServer(Server server) throws Exception, InterruptedException {
        server.start();
        server.join();
    }

    private static void setupServer(Server server, WebAppContext context, SocketConnector connector, ServerHandler serverHandler) {
        server.setConnectors(new Connector[]{connector});

        HandlerCollection handlers = new HandlerCollection();
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(LOGS_PATH + "/file-repo-access_yyyy_MM_dd.log");
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(10);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone(TimeZone.getDefault().getID());
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);

        handlers.addHandler(requestLogHandler);
        handlers.addHandler(context);
        handlers.addHandler(serverHandler);

        server.setHandler(handlers);
    }

    private static void setupConnector(SocketConnector connector) {
        connector.setPort(PORT);
    }

    private static void setupContext(Server server, WebAppContext context) throws Exception {
        context.setServer(server);
        context.setContextPath("/");
        context.setWar(WAR_LOCATION);
    }

}