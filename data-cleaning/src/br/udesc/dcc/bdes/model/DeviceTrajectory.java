package br.udesc.dcc.bdes.model;

import java.util.Optional;

public class DeviceTrajectory {
	private DeviceTrajectoryId id;
	private Trajectory trajectory;
	
	public DeviceTrajectory(DeviceTrajectoryId id) {
		super();
		this.id = id;
	}

	public Optional<DeviceTrajectoryId> getId() {
		return id == null ? Optional.empty() : Optional.of(id);
	}
	
	public void setId(DeviceTrajectoryId id) {
		this.id = id;
	}
	
	public Trajectory getTrajectory() {
		return trajectory;
	}
	
	public void setTrajectory(Trajectory trajectory) {
		this.trajectory = trajectory;
	}
	
}
