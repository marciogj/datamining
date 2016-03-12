package br.udesc.dcc.bdes.server.model;

import java.util.Optional;

/**
 * Device represents the source of GPS data.
 * It might be associated within a vehicle (embeeded device) or a smarphone (external device)
 */
public class Device {
	private DeviceId id;
	private String model;
	
	public Device() {
		super();
	}
	
	public Device(DeviceId id) {
		super();
		this.id = id;
	}

	public Device(DeviceId id,String model) {
		super();
		this.id = id;
		this.model = model;
	}



	public Optional<DeviceId> getId() {
		return id == null ? Optional.empty() : Optional.of(id);
	}
	
	public void setId(DeviceId id) {
		this.id = id;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}	
}
