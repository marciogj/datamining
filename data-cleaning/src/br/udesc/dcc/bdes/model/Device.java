package br.udesc.dcc.bdes.model;

import org.jongo.marshall.jackson.oid.MongoObjectId;


/**
 * Device represents the source of GPS data.
 * It might be associated within a vehicle (embeeded device) or a smarphone (external device)
 */
public class Device {
	@MongoObjectId
	private String _id;
	private String model;
	
	public Device() {
		super();
	}
	
	public Device(DeviceId id) {
		super();
		this._id = id.id;
	}

	public Device(DeviceId id,String model) {
		super();
		this._id = id.id;
		this.model = model;
	}

	public DeviceId getId() {
		return new DeviceId(_id);
	}
	
	public void setId(DeviceId id) {
		this._id = id.id;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}	
}
