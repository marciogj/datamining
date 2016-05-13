package br.udesc.dcc.bdes.server;

import br.udesc.dcc.bdes.server.model.Device;
import br.udesc.dcc.bdes.server.model.DeviceId;
import br.udesc.dcc.bdes.server.model.UDriver;
import br.udesc.dcc.bdes.server.repository.MemoryRepository;
import br.udesc.dcc.bdes.server.repository.UDriverDAO;

public class InitialSetup {
	
	public static void main(String[] args) {
		createDrivers();
	}
	
	public static void  createDrivers() {
		UDriver d = new UDriver("Marcio Jasinski", new DeviceId("moto-x"));
		UDriverDAO repository = new UDriverDAO();
		repository.add(d);
		
	}
	
	public static void  createVehicles() {
		MemoryRepository repository = MemoryRepository.get();
		repository.save(new Device(new DeviceId("RXT-1024"), "Mazda RX8"));
		repository.save(new Device(new DeviceId("MXN-8814"), "Mustang GT"));
		repository.save(new Device(new DeviceId("PQY-4789"), "Charger SXT"));
		repository.save(new Device(new DeviceId("GKS-7895"), "Corvette C7"));
	}
	
	public static void  createTrajectories() {
		MemoryRepository repository = MemoryRepository.get();
	
		repository.getVehicles().forEach( v -> {
			//repository.save(v.getId().get(), );	
		});
	}
	

}
