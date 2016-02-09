package br.udesc.dcc.bdes.server.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import br.udesc.dcc.bdes.server.JettyServer;

public class EventSocket extends WebSocketAdapter {
	
    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        System.out.println("Socket Connected: " + session);
        JettyServer.get().registerWSSession(session);
    }
    
    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        
        //TODO: from which session this message is from?
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
        
        //JettyServer.get().unregisterWSSession(session);
        //TODO: find out which session is closed...
    }
    
    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}