package br.udesc.dcc.bdes.server.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.server.model.Device;
import br.udesc.dcc.bdes.server.model.DeviceId;


public class MemoryRepository {
	//private static Map<String, TrajectoryTelemetry> telemetryRepository = new HashMap<>();
	private static Map<DeviceId, TrajectoryHistory> evaluationRepository = new HashMap<>();
	
	private static Map<DeviceId, Device> deviceRepository = new HashMap<>();
	

	
	private static MemoryRepository instance = new MemoryRepository();
	
	public static MemoryRepository get() {
		return instance;
	}
	
//	public Optional<TrajectoryTelemetry> findById(String id) {
//		TrajectoryTelemetry object = telemetryRepository.get(id);
//		if (object == null) {
//			return Optional.empty();
//		}
//		return Optional.of(object);
//	}
//
	public void save(DeviceId id, TrajectoryEvaluator trajectoryEval) {
		if (!deviceRepository.containsKey(id)) {
			deviceRepository.put(id, new Device(id));
		}
		TrajectoryHistory history = evaluationRepository.get(id);
		if (history == null) {
			history = new TrajectoryHistory();
			evaluationRepository.put(id, history);
		}
		history.add(trajectoryEval);		
	}

	public Optional<TrajectoryEvaluator> loadLatestTrajectoryEvaluationById(DeviceId id) {
		TrajectoryHistory history = evaluationRepository.get(id);
		if (history == null) {
			return Optional.empty();
		}
		return history.getLatest();
	}
	
	public List<TrajectoryEvaluator> loadTrajectoriesEvaluationById(DeviceId id) {
		TrajectoryHistory history = evaluationRepository.get(id);
		if (history == null) {
			return new LinkedList<>();
		}
		return history.history;
	}
	
	
	public void save(Device vehicle) {
		if (!vehicle.getId().isPresent()) {
			vehicle.setId(new DeviceId());
		}
		deviceRepository.put(vehicle.getId().get(), vehicle);
	}

	public Collection<Device> getVehicles() {
		return deviceRepository.values();
		
	}

	public int count(DeviceId id) {
		TrajectoryHistory history = evaluationRepository.get(id);
		if (history == null) {
			return 0;
		}
		return history.size();
	}

	public void updateLatest(DeviceId id,	TrajectoryEvaluator trajectoryEval) {
		TrajectoryHistory trajectoryHistory = evaluationRepository.get(id);
		trajectoryHistory.replaceLatest(trajectoryEval);	
	}
	
}

class TrajectoryHistory {
	List<TrajectoryEvaluator> history = new LinkedList<>();
	
	public Optional<TrajectoryEvaluator> getLatest() {
		if (history.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(history.get(history.size()-1));
	}

	public void replaceLatest(TrajectoryEvaluator trajectoryEval) {
		if(!history.isEmpty()) {
			history.remove(history.size()-1);
			history.add(trajectoryEval);
		}
	}

	public void add(TrajectoryEvaluator trajectoryEval) {
		history.add(trajectoryEval);
	}
	
	public int size() {
		return history.size();
	}
	
}
