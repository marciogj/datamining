package br.udesc.dcc.bdes.server.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
import br.udesc.dcc.bdes.analysis.TrajectoryTelemetry;


public class MemoryRepository {
	private static Map<String, TrajectoryTelemetry> telemetryRepository = new HashMap<>();
	private static Map<String, TrajectoryEvaluation> evaluationRepository = new HashMap<>();
	
	private static MemoryRepository instance = new MemoryRepository();
	
	public static MemoryRepository get() {
		return instance;
	}
	
	public Optional<TrajectoryTelemetry> findById(String id) {
		TrajectoryTelemetry object = telemetryRepository.get(id);
		if (object == null) {
			return Optional.empty();
		}
		return Optional.of(object);
	}

	public void save(String id, TrajectoryEvaluation trajectoryEval) {
		evaluationRepository.put(id, trajectoryEval);
	}

	public Optional<TrajectoryEvaluation> findTrajectoryEvaluationById(String userId) {
		TrajectoryEvaluation evaluation = evaluationRepository.get(userId);
		if (evaluation == null) {
			return Optional.empty();
		}
		return Optional.of(evaluation);
	}
	
	
	
	
}
