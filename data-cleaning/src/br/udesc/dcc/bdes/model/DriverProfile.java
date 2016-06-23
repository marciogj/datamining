package br.udesc.dcc.bdes.model;


public class DriverProfile {
	private String driverId;
	private String deviceId;
	private double traveledDistance = 0;
	private long traveledTime = 0;
	private int alerts = 0;
	private double aggressiveIndexSum = 0;
	private double aggressiveIndexCount = 0;
	private double maxAggressiveIndex = 0;
	private int trajectoryCount = 0;
	
	public DriverProfile() {}
	
	public DriverProfile(DriverId driverId, DeviceId deviceId) {
		super();
		this.driverId = driverId.id;
		this.deviceId = deviceId.id;
	}

	public DriverId getDriverId() {
		return new DriverId(driverId);
	}

	public DeviceId getDeviceId() {
		return new DeviceId(deviceId);
	}

	public double getTraveledDistance() {
		return traveledDistance;
	}

	public void setTraveledDistance(double traveledDistance) {
		this.traveledDistance = traveledDistance;
	}

	public long getTraveledTime() {
		return traveledTime;
	}

	public void setTraveledTime(long traveledTime) {
		this.traveledTime = traveledTime;
	}

	public void increaseTraveledDistance(double totalDistance) {
		this.traveledDistance += totalDistance;
		
	}

	public void increaseTraveledTime(long totalTime) {
		this.traveledTime += totalTime;
	}
	
	public void addAggressiveIndex(double value) {
		this.aggressiveIndexSum += value;
		this.aggressiveIndexCount++;
	}
	
	public double getAggressiveIndex() {
		return aggressiveIndexSum/aggressiveIndexCount;
	}

	public int getAlerts() {
		return alerts;
	}

	public void setAlerts(int alerts) {
		this.alerts = alerts;
	}

	public void increaseAlerts(int value) {
		this.alerts += value;
	}

	public void updateMaxAggressiveIndex(double aggressiveIndex) {
		if (aggressiveIndex > maxAggressiveIndex) {
			maxAggressiveIndex = aggressiveIndex;
		}
	}

	public double getMaxAggressiveIndex() {
		return maxAggressiveIndex;
	}

	public void setMaxAggressiveIndex(double maxAggressiveIndex) {
		this.maxAggressiveIndex = maxAggressiveIndex;
	}

	public int getTrajectoryCount() {
		return trajectoryCount;
	}
	
	public void increaseTrajectory() {
		trajectoryCount++;
	}
	
}