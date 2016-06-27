package br.udesc.dcc.bdes.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import br.udesc.dcc.bdes.model.Device;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.Driver;
import br.udesc.dcc.bdes.repository.memory.MemoryRepository;
import br.udesc.dcc.bdes.repository.sql.DBPool;
import br.udesc.dcc.bdes.repository.sql.UDriverDAO;

public class InitialSetup {
	
	public static void main(String[] args) {
		createDrivers();
	}
	
	public static void  createDrivers() {
		try {
			Connection conn = DBPool.getConnection().orElseThrow( () -> new RuntimeException("Could not allocate db connection"));
			UDriverDAO repository = new UDriverDAO(conn);
			//repository.add(new UDriver("Marcio Jasinski", new DeviceId("moto-x")));
			//repository.add(new UDriver("Anderson Torres", new DeviceId("123")));
			//repository.add(new UDriver("Taxi 547", new DeviceId("121")));
			
			List<Driver> all = repository.loadAll();
			System.out.println(all.size());
			
			DBPool.release(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
