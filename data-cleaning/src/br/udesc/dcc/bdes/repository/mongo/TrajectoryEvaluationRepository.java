package br.udesc.dcc.bdes.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluatorId;
import br.udesc.dcc.bdes.model.DeviceId;


public class TrajectoryEvaluationRepository {
	
	
	public final static String COLLECTION = "trajectory-evaluation";
	private static final MongoCollection repository = MongoDBStatic.getCollection(COLLECTION);
	
	private static TrajectoryEvaluationRepository instance = new TrajectoryEvaluationRepository();
	
	public static TrajectoryEvaluationRepository get() {
		return instance;
	}
	
	public void save(TrajectoryEvaluator entity) {
		Optional<TrajectoryEvaluator> optEntity = load(entity.getId());
		if (optEntity.isPresent()) {
			update(entity);
		} else {
			insert(entity);
		}
	}
	
	public Optional<TrajectoryEvaluator> loadLatestTrajectoryEvaluationById(DeviceId id) {
		String latestTimestampDesc = "latestTimestamp: -1";
		String byDeviceIdAndTimestampDesc = "{deviceId: '" + id.getValue() + ", " + latestTimestampDesc + "'}";
		TrajectoryEvaluator entity = repository.findOne(byDeviceIdAndTimestampDesc).as(TrajectoryEvaluator.class);
		return Optional.ofNullable(entity);
	}
	
	
	public void updateLatest(TrajectoryEvaluator trajectoryEval) {
		Optional<TrajectoryEvaluator> optEvaluation = loadLatestTrajectoryEvaluationById(trajectoryEval.getDeviceId());
		if (!optEvaluation.isPresent()) {
			System.out.println("Error: Could not find latest eval from deviceId " + trajectoryEval.getDeviceId().getValue());
		}
		update(trajectoryEval);	
	}	
	
	private void update(TrajectoryEvaluator entity) {
		repository.update(getDeviceIdQuery(entity.getDeviceId())).with(entity);
	}
	
	private void insert(TrajectoryEvaluator entity) {
		repository.save(entity);
	}
	
	public Optional<TrajectoryEvaluator> load(TrajectoryEvaluatorId id) {
		TrajectoryEvaluator entity = repository.findOne(getIdQuery(id)).as(TrajectoryEvaluator.class);
		return Optional.ofNullable(entity);
	}
	
	public Collection<TrajectoryEvaluator> loadTrajectoriesEvaluationById(DeviceId id) {
		MongoCursor<TrajectoryEvaluator> all = repository.find(getDeviceIdQuery(id)).as(TrajectoryEvaluator.class);
		Collection<TrajectoryEvaluator> allItems = new ArrayList<>();
		for (TrajectoryEvaluator entity : all) {
			allItems.add(entity);
		}
		return allItems;
	}
	
	public void remove(TrajectoryEvaluatorId id) {
		repository.remove(getIdQuery(id));
	}
	
	public long count() {
		return repository.count();
	}
	
	private String getIdQuery(TrajectoryEvaluatorId id) {
		return "{_id: '" + id.getValue() + "'}";
	}
	
	private String getDeviceIdQuery(DeviceId id) {
		return "{deviceId: '" + id.getValue() + "'}";
	}
	
	public Collection<TrajectoryEvaluator> loadAll() {
		MongoCursor<TrajectoryEvaluator> all = repository.find().as(TrajectoryEvaluator.class);
		Collection<TrajectoryEvaluator> allItems = new ArrayList<>();
		for (TrajectoryEvaluator entity : all) {
			allItems.add(entity);
		}
		return allItems;
	}
	
	//private static Map<String, TrajectoryTelemetry> telemetryRepository = new HashMap<>();
	
	/*
	 
	
	
	
	*/
}

