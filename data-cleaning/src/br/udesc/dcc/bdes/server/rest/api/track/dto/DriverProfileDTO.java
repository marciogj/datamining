package br.udesc.dcc.bdes.server.rest.api.track.dto;


public class DriverProfileDTO {
	public String driverId;
	public String deviceId;
	public double traveledDistance;
	public String traveledTime;
	public double aggressiveIndex = 0;
	public int alerts = 0;
}
