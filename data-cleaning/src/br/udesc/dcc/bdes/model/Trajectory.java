package br.udesc.dcc.bdes.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Trajectory {
	protected TrajectoryId id;
	protected List<Coordinate> coordinates = new LinkedList<>();
	protected String sourceProvider; //Geolife, UDESC
	protected String userId; //taxi id, user
	protected String deviceId; 
	protected String transportMean;
	
	public Trajectory() {
		this.id = new TrajectoryId();
	}

	public Trajectory(Collection<Coordinate> elements) {
		coordinates.addAll(elements);
	}

	public void add(Coordinate coordinate) {
		if (coordinate != null) {
			coordinates.add(coordinate);
		}
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public int size() {
		//TODO: Linked list enhance incoming coordinates but punish size call.
		//Consider a cache in case of performance issues with big trajectories
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

	public Optional<LocalDateTime> getStart() {
		if(coordinates.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(coordinates.get(0).getDateTime());
	}

	public Optional<LocalDateTime> getEnd() {
		if(coordinates.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(coordinates.get(coordinates.size()-1).getDateTime());
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public Optional<Coordinate> getLastestCoordinate() {
		if (coordinates.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(coordinates.get(coordinates.size() -1));
	}

	public Optional<Coordinate> getFirstCoordintae() {
		if (coordinates.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(coordinates.get(0));
	}
	
	/*
	public List<Coordinate> getLastestCoordinates(int max) {
		List<Coordinate> latest = new LinkedList<Coordinate>();
		int totalCoordinates = this.size(); 
		for(int i = max; i > 0; i--) {
			int index = coordinates.size()-i;
			if (index < totalCoordinates) {
				latest.add(coordinates.get(index));
			}
		}
		return latest;
	}*/
	
	public TrajectoryId getId() {
		return id;
	}

	public void setId(TrajectoryId trajectoryId) {
		this.id = trajectoryId;
	}

}
