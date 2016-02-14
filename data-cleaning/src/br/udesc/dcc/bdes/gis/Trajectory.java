package br.udesc.dcc.bdes.gis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Trajectory {
	protected Collection<Coordinate> coordinates = new ArrayList<>();
	protected String sourceProvider; //Geolife, UDESC
	protected String userId; //taxi id, user
	protected String deviceId; 
	protected String transportMean;
	protected LocalDateTime start;
	protected LocalDateTime end;
	
	public Trajectory() {}

	public Trajectory(Collection<Coordinate> elements) {
		coordinates.addAll(elements);
	}

	public void add(Coordinate coordinate) {
		if (coordinate != null) {
			coordinates.add(coordinate);
		}
	}

	public Collection<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public int size() {
		return coordinates.size();
	}

	public void addAll(Collection<Coordinate> elements) {
		coordinates.addAll(elements);
	}

	public String getSourceProvider() {
		return sourceProvider;
	}

	public void setSourceProvider(String sourceProvider) {
		this.sourceProvider = sourceProvider;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String id) {
		this.userId = id;
	}

	public String getTransportMean() {
		return transportMean;
	}

	public void setTransportMean(String transportMean) {
		this.transportMean = transportMean;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
