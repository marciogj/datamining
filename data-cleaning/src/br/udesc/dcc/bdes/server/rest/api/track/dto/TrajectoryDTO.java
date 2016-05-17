package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.util.LinkedList;
import java.util.List;

public class TrajectoryDTO {
	public String id;
	public List<CoordinateDTO> coordinates = new LinkedList<>();
	public String sourceProvider; 
	public String userId;
	public String deviceId; 
	public String transportMean;
	
}
