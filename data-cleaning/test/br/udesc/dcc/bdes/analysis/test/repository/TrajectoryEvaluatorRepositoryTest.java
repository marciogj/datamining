package br.udesc.dcc.bdes.analysis.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.repository.mongo.MongoDBStatic;
import br.udesc.dcc.bdes.repository.mongo.TrajectoryEvaluationRepository;

public class TrajectoryEvaluatorRepositoryTest {
	TrajectoryEvaluationRepository repository = TrajectoryEvaluationRepository.get();
	
	@Before
	public void setUp() throws Exception {
		MongoCollection collection = MongoDBStatic.getCollection(TrajectoryEvaluationRepository.COLLECTION);
		collection.drop();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void itShouldInsertAndLoad() {
		TrajectoryEvaluator evaluation = new TrajectoryEvaluator(new DeviceId("abc"), new DriverId("123"));
		Trajectory trajectory = new Trajectory();
		trajectory.add(randomCoordinate());
		evaluation.evaluate(trajectory);
		repository.save(evaluation);
		assertEquals(1, repository.count());
		
		Optional<TrajectoryEvaluator> optEvaluation = repository.load(evaluation.getId());
		assertTrue(optEvaluation.isPresent());
		assertEquals(1, optEvaluation.get().getTrajectory().size()); 
	}
	
	private Coordinate randomCoordinate() {
		return new Coordinate(Math.random(), Math.random(), Math.random(), LocalDateTime.now());
	}
	
}
