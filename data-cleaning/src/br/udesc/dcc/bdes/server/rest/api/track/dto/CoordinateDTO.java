package br.udesc.dcc.bdes.server.rest.api.track.dto;


public class CoordinateDTO {
	public double latitude; 
	public double longitude; 
	public double altitude;
	public String speed;
	public String acceleration;
	
	//Both types of date are supported for input/output in order to keep backward compatibility
	public String dateTime;
	public Long timestamp;
	
	public String accuracy;
	public String bearing;
		
}
