package br.udesc.dcc.bdes.repository.mongo;

import java.util.Optional;

import org.jongo.MongoCollection;

import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.DriverProfile;


public class DriverProfileRepository {
	public static final String COLLECTION = "driver-profile";
	private static final MongoCollection repository = MongoDBStatic.getCollection(COLLECTION);
	
	private static DriverProfileRepository instance = new DriverProfileRepository();
	
	public static DriverProfileRepository get() {
		return instance;
	}
		
	public void save(DriverProfile entity) {
		Optional<DriverProfile> optEntity = load(entity.getDriverId());
		if (optEntity.isPresent()) {
			update(entity);
		} else {
			insert(entity);
		}
	}
	
	private void update(DriverProfile entity) {
		repository.update(getIdQuery(entity.getDriverId())).with(entity);
	}
	
	private void insert(DriverProfile entity) {
		repository.save(entity);
	}
	
	public Optional<DriverProfile> load(DriverId id) {
		DriverProfile entity = repository.findOne(getIdQuery(id)).as(DriverProfile.class);
		return Optional.ofNullable(entity);
	}
	
	public void remove(DriverId id) {
		repository.remove(getIdQuery(id));
	}
	
	public long count() {
		return repository.count();
	}
	
	private String getIdQuery(DriverId id) {
		return "{driverId: '" + id.getValue() + "'}";
	}
		
}

