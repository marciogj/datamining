package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.util.Collection;
import java.util.LinkedList;

public class TrackDTO {
	public String deviceId;
	public String userId;
	public Collection<CoordinateDTO> coordinates = new LinkedList<>();
	
}
