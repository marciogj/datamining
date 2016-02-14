package br.udesc.dcc.bdes.analysis.deprecated;

import br.udesc.dcc.bdes.gis.Trajectory;


/**
 * 
 * 
 * Speed will always return m/s (meter per second)
 * 
 * @author marciogj
 *
 */
public class EvaluatedTrajectory {
	protected Trajectory trajectory;
	protected double totalDistance;
	protected long totalTime;
	protected double rate;
	protected double maxSpeed;
	protected double avgSpeed;
	protected double maxSpeedUp;
	protected double maxSlowdown;
	protected double speedUpDownOscilations;
	
	public EvaluatedTrajectory() {}
	
	public EvaluatedTrajectory(Trajectory aTrajectory) {
		trajectory = aTrajectory;
	}

	public int getTotalCoordinates() {
		return trajectory.size();
	}

	public Trajectory getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(Trajectory trajectory) {
		this.trajectory = trajectory;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public double getCoordinateRate() {
		return rate;
	}

	public void setCoordinateRate(double rate) {
		this.rate = rate;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(double avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public double getMaxSpeedUp() {
		return maxSpeedUp;
	}

	public void setMaxSpeedUp(double maxSpeedUp) {
		this.maxSpeedUp = maxSpeedUp;
	}

	public double getMaxSlowdown() {
		return maxSlowdown;
	}

	public void setMaxSlowdown(double maxSlowdown) {
		this.maxSlowdown = maxSlowdown;
	}

	public double getSpeedUpDownOscilations() {
		return speedUpDownOscilations;
	}

	public void setSpeedUpDownOscilations(double speedUpDownOscilations) {
		this.speedUpDownOscilations = speedUpDownOscilations;
	}
	
}


