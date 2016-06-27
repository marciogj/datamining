package br.udesc.dcc.bdes.repository.memory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluatorId;
import br.udesc.dcc.bdes.model.Device;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.DriverProfile;


public class MemoryRepository {
	//private static Map<String, TrajectoryTelemetry> telemetryRepository = new HashMap<>();
	private final static Map<DeviceId, TrajectoryHistory> evaluationRepository = new HashMap<>();
	private final static Map<DeviceId, Device> deviceRepository = new HashMap<>();
	private final static Map<DriverId, DriverProfile> driverProfileRepository = new HashMap<>();
	
	private static MemoryRepository instance = new MemoryRepository();
	
	public static MemoryRepository get() {
		return instance;
	}
	
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
	
	public void save(DriverProfile profile) {
		driverProfileRepository.put(profile.getDriverId(), profile);
	}
	
	public void save(Device vehicle) {
		if (vehicle.getId().getValue() == null) {
			vehicle.setId(new DeviceId());
		}
		deviceRepository.put(vehicle.getId(), vehicle);
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

	public Optional<TrajectoryEvaluator> loadTrajectoryEvaluationById(TrajectoryEvaluatorId evaluationId) {
		for(TrajectoryHistory history : evaluationRepository.values() ) {
			Optional<TrajectoryEvaluator> eval = history.findById(evaluationId);
			if (eval.isPresent()) {
				return eval;
			}
		}
		return Optional.empty();
	}

	public Optional<DriverProfile> loadDriverProfile(DriverId userId) {
		DriverProfile profile = driverProfileRepository.get(userId);
		if (profile != null) {
			return Optional.of(profile);
		}
		return Optional.empty();
	}
	
}

