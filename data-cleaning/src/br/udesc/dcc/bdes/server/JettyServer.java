package br.udesc.dcc.bdes.server;

//import static spark.Spark.get;
//import static spark.Spark.port;
//import static spark.Spark.post;

import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import br.udesc.dcc.bdes.server.rest.Track;

//import com.google.gson.Gson;

public class JettyServer {
	public static final int SERVICES_PORT = 8080;
	public static final int WEBSOCKET_PORT = 9090;
	
	private final Logger logger = Logger.getLogger("GPSSparkServer");
	//private final Gson gson = new Gson();
	
	public static void main(String[] args) {
		JettyServer server = new JettyServer();
		server.startWebSocketServer();
	}
	

	public JettyServer() {
	}

	
	
	
	public void startWebSocketServer() {
		logger.info("Initializing WebSocket server on port " + WEBSOCKET_PORT);
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(WEBSOCKET_PORT);
        server.addConnector(connector);
        
        

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/ws");
        //server.setHandler(context);
        
        
        //***** REST
        ServletContextHandler restContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        restContext.setContextPath("/services");
        
        
        ServletHolder jerseyServlet = restContext.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
           jerseyServlet.setInitOrder(0);
           
           jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                Track.class.getCanonicalName());

        //**** REST
        
        
        //*************** HTTP ***********
        ResourceHandler resource_handler = new ResourceHandler();
        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase(".");
        
        // Add the ResourceHandler to the server.
        //GzipHandler gzip = new GzipHandler();
        
        //server.setHandler(gzip);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { context, resource_handler, restContext, new DefaultHandler() });
        //gzip.setHandler(handlers);
 
        server.setHandler(handlers);
        
        
        //**************************
        
        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");
        
        
       
        

        try{
            server.start();
            //server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
        	server.destroy();
        }
    }

}
