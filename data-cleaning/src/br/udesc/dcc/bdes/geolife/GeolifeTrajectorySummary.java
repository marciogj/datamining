package br.udesc.dcc.bdes.geolife;

public class GeolifeTrajectorySummary {
	GeolifeTrajectory trajectory;
	double totalDistanceMeters;
	double totalTimeSeconds;
	double maxSpeedMS;
	double maxAccelerationMS;
	long stoppedTime = 0;
	
	public GeolifeTrajectorySummary(GeolifeTrajectory trajectory) {
		super();
		this.trajectory = trajectory;
	}
	
	public GeolifeTrajectory getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(GeolifeTrajectory trajectory) {
		this.trajectory = trajectory;
	}
	
	public double getTotalDistanceInMeters() {
		return totalDistanceMeters;
	}
	
	public void setTotalDistanceInMeters(double totalDistanceMeters) {
		this.totalDistanceMeters = totalDistanceMeters;
	}
	
	public double getTotalTimeInSeconds() {
		return totalTimeSeconds;
	}
	
	public void setTotalTimeInSeconds(double totalTimeSeconds) {
		this.totalTimeSeconds = totalTimeSeconds;
	}
	
	public double getMaxSpeedInMetersPerSecond() {
		return maxSpeedMS;
	}
	
	public void setMaxSpeedInMetersPerSecond(double maxSpeedMS) {
		this.maxSpeedMS = maxSpeedMS;
	}
	
	public double getMaxAccelerationInMetersPerSecond() {
		return maxAccelerationMS;
	}
	
	public void setMaxAccelerationInMetersPerSecond(double maxAccelerationMS) {
		this.maxAccelerationMS = maxAccelerationMS;
	}

	public long getStoppedTime() {
		return stoppedTime;
	}

	public void setStoppedTime(long stoppedTime) {
		this.stoppedTime = stoppedTime;
	}

	public double getAvgSpeedInMetersPerSecond() {
		return totalDistanceMeters / totalTimeSeconds;
	}
}
