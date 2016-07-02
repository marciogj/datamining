package br.udesc.dcc.bdes.server.rest.api.track.dto;


public class CoordinateDTO {
	public Double latitude; 
	public Double longitude; 
	public Double altitude;
	public Double speed;
	public Double acceleration;
	
	//Both types of date are supported for input/output in order to keep backward compatibility
	public String dateTime;
	public Long timestamp;
	
	public Double accuracy;
	public Double bearing;
	
	//----
	public boolean isNoise;
	protected double maxSpeed;
	public String meanType;
}
