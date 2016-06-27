package br.udesc.dcc.bdes.repository.memory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluatorId;

public class TrajectoryHistory {
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
	

	public Optional<TrajectoryEvaluator> findById(TrajectoryEvaluatorId id) {
		return history.stream().filter(trajectoryEval -> trajectoryEval.getId().equals(id)).findFirst();	
	}
	
	public int size() {
		return history.size();
	}
	
}
