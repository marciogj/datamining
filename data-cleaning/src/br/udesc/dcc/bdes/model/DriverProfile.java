package br.udesc.dcc.bdes.model;

public class DriverProfile {
	private UDriverId driverId;
	private DeviceId deviceId;
	private long traveledDistance = 0;
	private long traveledTime = 0;
	private double aggressiveIndexSum = 0;
	private double aggressiveIndexCount = 0;
	
	public DriverProfile(UDriverId driverId, DeviceId deviceId) {
		super();
		this.driverId = driverId;
		this.deviceId = deviceId;
	}

	public UDriverId getDriverId() {
		return driverId;
	}

	public void setDriverId(UDriverId driverId) {
		this.driverId = driverId;
	}

	public DeviceId getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(DeviceId deviceId) {
		this.deviceId = deviceId;
	}

	public long getTraveledDistance() {
		return traveledDistance;
	}

	public void setTraveledDistance(long traveledDistance) {
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
	
}