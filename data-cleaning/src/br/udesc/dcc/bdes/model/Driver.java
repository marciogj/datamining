package br.udesc.dcc.bdes.model;

public class Driver {
	protected DriverId id;
	protected String name;
	protected DeviceId deviceId;
	
	public Driver(String name, DeviceId deviceId) {
		this.id = new DriverId();
		this.name = name;
		this.deviceId = deviceId;
	}

	public Driver() {
	}

	public DriverId getId() {
		return id;
	}

	public void setId(DriverId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DeviceId getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(DeviceId deviceId) {
		this.deviceId = deviceId;
	}
	
}
