package br.udesc.dcc.bdes.server;

import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import br.udesc.dcc.bdes.server.ws.EventServlet;


public class JettyServer {
	public static final int HTTP_PORT = 9090;
	private final Logger logger = Logger.getLogger("GPSSparkServer");
	
	private static final String SERVICES_CONTEXT = "/services";
	private static final String WEBSOCKET_CONTEXT = "/ws";

	/**
	 * Resource configuration performs an auto discovery over specified packages.
	 * More details: https://jersey.java.net/nonav/documentation/2.0/deployment.html 
	 */
	public class ServicesResourceConfig extends ResourceConfig {
	    public ServicesResourceConfig() {
	    	//Register servcies using semiclon: //package1;packeage2
	        packages("br.udesc.dcc.bdes.server.rest.api"); 
	    }
	}
	
	
	public static void main(String[] args) {
		JettyServer server = new JettyServer();
		server.startServer();
	}
	

	public JettyServer() {}

	
	public ServletContextHandler createRestHandler() {
		logger.info("Initializing REST handler on " + SERVICES_CONTEXT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	    context.setContextPath(SERVICES_CONTEXT);
	    ServletHolder holder =  new ServletHolder(new ServletContainer(new ServicesResourceConfig()));
	    context.addServlet(holder, "/*");
	    
	    //Another way to register services is bind them manually
	    //ServletHolder jerseyServlet = restContext.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
	    //jerseyServlet.setInitOrder(0);
	    //jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", 
	    //		TrackAPI.class.getCanonicalName()+ "," + 
	    //		ServerAPI.class.getCanonicalName()
	    //);
		return context;
	}
	
	public ServletContextHandler createWebSocketHandler() {
		logger.info("Initializing WebSocker handler on " + WEBSOCKET_CONTEXT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(WEBSOCKET_CONTEXT);
        ServletHolder holder = new ServletHolder("web-socket", EventServlet.class);
        context.addServlet(holder, "/events/*");
        return context;
	}

	
	public ResourceHandler createHTMLHandler() {
		logger.info("Initializing HTML handler on . context");
		//Based on http://www.eclipse.org/jetty/documentation/current/embedded-examples.html
		ResourceHandler handler = new ResourceHandler();
        handler.setDirectoriesListed(true);
        handler.setWelcomeFiles(new String[]{ "index.html" });
        handler.setResourceBase(".");
        return handler;
	}
	
	
	public void startServer() {
		logger.info("Initializing WebSocket server on port " + HTTP_PORT);
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(HTTP_PORT);
        server.addConnector(connector);
        
        ServletContextHandler wsHandler = createWebSocketHandler();
        ServletContextHandler restHandler = createRestHandler();
        ResourceHandler htmlHandler = createHTMLHandler();
        

        //GzipHandler gzip = new GzipHandler();
        //server.setHandler(gzip);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { htmlHandler, wsHandler, restHandler, new DefaultHandler() });
        //gzip.setHandler(handlers);
 
        server.setHandler(handlers);
        
        
        try{
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
        	server.destroy();
        }
    }

}
