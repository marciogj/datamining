package br.udesc.dcc.bdes.model;

import java.util.UUID;

public class DeviceTrajectoryId {
private String id;
	
	public DeviceTrajectoryId() {
		super();
		this.id = UUID.randomUUID().toString();
	}
	
	public DeviceTrajectoryId(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
