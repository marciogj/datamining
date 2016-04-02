package br.udesc.dcc.bdes.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.gzip.GzipHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import br.udesc.dcc.bdes.server.ws.EventServlet;


public class JettyServer {
	public static final int HTTP_PORT = 9090;
	private static final String SERVICES_CONTEXT = "/services";
	private static final String WEBSOCKET_CONTEXT = "/ws";
		
	private final Logger logger = Logger.getLogger("GPSSparkServer");
	private static final JettyServer server = new JettyServer();
	
	private Properties properties = new Properties();
	private final Map<String, Session> wsSesseions = new HashMap<>();
	
	/**
	 * Resource configuration performs an auto discovery over specified packages.
	 * More details: https://jersey.java.net/nonav/documentation/2.0/deployment.html 
	 */
	public class ServicesResourceConfig extends ResourceConfig {
	    public ServicesResourceConfig() {
	    	//Register servcies using semiclon: //package1;packeage2
	    	packages("br.udesc.dcc.bdes.server.rest.api");
	    	register(CORSResponseFilter.class);
	    }
	}
	
	public static void main(String[] args) {
		server.startServer();
	}
	
	public JettyServer() {}
	
	public static JettyServer get() {
		return server;
	}
	
	public String registerWSSession(Session session) {
		String uuid = UUID.randomUUID().toString();
		wsSesseions.put(uuid, session);
		return uuid;
		
	}
	
	public void unregisterWSSession(String uuid) {
		wsSesseions.remove(uuid);
	}
	
	public void unregisterSession(Session session) {
		wsSesseions.forEach( (k,v)-> { if(v.equals(session)) { wsSesseions.remove(k);} } );
	}
	
	public List<Session> getRegisteredSessions() {
		List<Session> sessions = new ArrayList<>(wsSesseions.size());
		wsSesseions.forEach( (k,v)-> sessions.add(v) );
		return sessions;
	}
	
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
        properties = loadServerProperties();
		
        
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(HTTP_PORT);
        server.addConnector(connector);
        
        ServletContextHandler wsHandler = createWebSocketHandler();
        ServletContextHandler restHandler = createRestHandler();
        ResourceHandler htmlHandler = createHTMLHandler();
        
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { htmlHandler, wsHandler, restHandler, new DefaultHandler() });
        //server.setHandler(handlers); //Might be used instead of Gzip
        
        GzipHandler gzip = new GzipHandler();
        gzip.setHandler(handlers);
        server.setHandler(gzip);
        
        try{
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
        	server.destroy();
        }
    }
	
	public Optional<String> getOpenWeatherKey() {
		String key = properties.getProperty("open-weather-key");
		if(key != null) {
			return Optional.of(key);
		}
		return Optional.empty();
	}
	
	public static Properties loadServerProperties() {
		Properties fileProperties = new Properties();
		File keyFile = new File("server.properties");
		try ( BufferedReader reader = new BufferedReader(new FileReader(keyFile))) {
			fileProperties.load(reader);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fileProperties;
	}

}

//Based on http://www.codingpedia.org/ama/how-to-add-cors-support-on-the-server-side-in-java-with-jersey/
class CORSResponseFilter implements ContainerResponseFilter {

	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");			
		headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia");
	}

}
