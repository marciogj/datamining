package br.udesc.dcc.bdes.model;

public class UDriver {
	protected UDriverId id;
	protected String name;
	protected DeviceId deviceId;
	
	public UDriver(String name, DeviceId deviceId) {
		this.id = new UDriverId();
		this.name = name;
		this.deviceId = deviceId;
	}

	public UDriver() {
	}

	public UDriverId getId() {
		return id;
	}

	public void setId(UDriverId id) {
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
