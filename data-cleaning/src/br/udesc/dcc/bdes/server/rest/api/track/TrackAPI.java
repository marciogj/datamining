package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import br.udesc.dcc.bdes.server.JettyServer;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;

@Path(APIPath.TRACK)
public class TrackAPI {
	private final Gson gson = new Gson();
	/**
	 * 
	 * curl -X POST -H "Content-Type: application/json" http://localhost:9090/services/track -d 
	 *   '{ "deviceId": "aaaa", "userId": "bbb", "coordinates": [ 
	 *      { "latitude": "0", "longitude": "1" }, 
	 *      { "latitude": "3", "longitude": "4"}  
	 *    ]}'
	 * 
	 * @param trackDto
	 */
	@POST
    public void postTrack(TrackDTO trackDto) {
		System.out.println("DeviceId: " + trackDto.deviceId + " - Coordinates: " + trackDto.coordinates.size());
		
		JettyServer server = JettyServer.get();
		List<Session> sessions = server.getRegisteredSessions();
		sessions.forEach( session -> {
			System.out.println("Sendin message to session");
			RemoteEndpoint remote = session.getRemote();
			remote.sendStringByFuture(gson.toJson(trackDto));
		});
					
    }
	
} 