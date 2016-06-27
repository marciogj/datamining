package br.udesc.dcc.bdes.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import br.udesc.dcc.bdes.model.Device;
import br.udesc.dcc.bdes.model.DeviceId;


public class DeviceRepository {
	public final static String COLLECTION = "device";
	private static final MongoCollection repository = MongoDBStatic.getCollection(COLLECTION);
	
	private static DeviceRepository instance = new DeviceRepository();
	
	public static DeviceRepository get() {
		return instance;
	}
	
	public void save(Device entity) {
		Optional<Device> optEntity = load(entity.getId());
		if (optEntity.isPresent()) {
			update(entity);
		} else {
			insert(entity);
		}
	}
	
	private void update(Device entity) {
		repository.update(getIdQuery(entity.getId())).with(entity);
	}
	
	private void insert(Device entity) {
		repository.save(entity);
	}
	
	public Optional<Device> load(DeviceId id) {
		Device entity = repository.findOne(getIdQuery(id)).as(Device.class);
		return Optional.ofNullable(entity);
	}
	
	public void remove(DeviceId id) {
		repository.remove(getIdQuery(id));
	}
	
	public long count() {
		return repository.count();
	}
	
	private String getIdQuery(DeviceId id) {
		return "{_id: '" + id.getValue() + "'}";
	}
	
	public Collection<Device> loadAll() {
		MongoCursor<Device> all = repository.find().as(Device.class);
		Collection<Device> allItems = new ArrayList<>();
		for (Device entity : all) {
			allItems.add(entity);
		}
		return allItems;
	}
	
}

