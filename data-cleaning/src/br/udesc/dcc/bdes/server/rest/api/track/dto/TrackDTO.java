package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.util.LinkedList;
import java.util.List;

public class TrackDTO {
	public String deviceId;
	public String userId;
	public List<CoordinateDTO> coordinates = new LinkedList<>();
	
}
