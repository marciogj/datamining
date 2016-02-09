package br.udesc.dcc.bdes.server.rest.api.track;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;

@Path(APIPath.TRACK)
public class TrackAPI {
	
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
		System.out.println("DeviceId: " + trackDto.deviceId);
		System.out.println("UserId: " + trackDto.userId);
		System.out.println("Coordinates: " + trackDto.coordinates.size());
    }
	
} 