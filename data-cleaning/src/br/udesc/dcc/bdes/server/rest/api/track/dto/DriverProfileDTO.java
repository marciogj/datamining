package br.udesc.dcc.bdes.server.rest.api.track.dto;


public class DriverProfileDTO {
	public String driverId;
	public String deviceId;
	public Double traveledDistance;
	public Double avgTrajectoryDistance;
	public String traveledTime;
	public String avgTrajectoryTime;
	public Double aggressiveIndex;
	public Double maxAggressiveIndex;
	public Integer alerts;
	public Integer trajectoryCount;
	public Long traveledTimeSeconds;
}
