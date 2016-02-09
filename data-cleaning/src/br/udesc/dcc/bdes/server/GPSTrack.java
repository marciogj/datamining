package br.udesc.dcc.bdes.server;

import java.util.Collection;
import java.util.LinkedList;

import br.udesc.dcc.bdes.gis.Coordinate;

public class GPSTrack {
	private String deviceId;
	private String userId;
	private Collection<Coordinate> coordinates = new LinkedList<>();
	
	public GPSTrack(){}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Collection<Coordinate> getCoordinates() {
		return coordinates;
	}
	
	public void add(Coordinate coordinate) {
		coordinates.add(coordinate);
	}
}
