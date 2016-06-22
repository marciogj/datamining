package br.udesc.dcc.bdes.analysis.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import br.udesc.dcc.bdes.analysis.MeanTransportSplitter;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Speed;
import br.udesc.dcc.bdes.model.Trajectory;

public class MeanTransportSplitterTest {

	@Test
	public void evaluateWalkBetweenMotorizedTransport() {
		Trajectory trajectory = new Trajectory();
		trajectory.addAll(createSpeedCoord(10, 20, 100)); //motorized
		trajectory.addAll(createSpeedCoord(20, 0, 10)); //walk
		trajectory.addAll(createSpeedCoord(30, 20, 60)); //motorized
		List<Trajectory> subTrajectories = MeanTransportSplitter.subBySpeed(trajectory);
		assertEquals(3, subTrajectories.size());
	}
	
	@Test 
	public void evaluateWalkOnly() {
		Trajectory trajectory = new Trajectory();
		trajectory.addAll(createSpeedCoord(180, 0, 10)); //walk
		List<Trajectory> subTrajectories = MeanTransportSplitter.subBySpeed(trajectory);
		assertEquals(1, subTrajectories.size());
		assertEquals(180, subTrajectories.get(0).size());
	}
	
	@Test
	public void evaluateMotorOnly() {
		Trajectory trajectory = new Trajectory();
		trajectory.addAll(createSpeedCoord(50, 15, 100)); 
		List<Trajectory> subTrajectories = MeanTransportSplitter.subBySpeed(trajectory);
		assertEquals(1, subTrajectories.size());
		assertEquals(50, subTrajectories.get(0).size());
	}
	
	@Test
	public void evaluateMotorWithTraffic() {
		Trajectory trajectory = new Trajectory();
		trajectory.addAll(createSpeedCoord(50, 15, 100)); 
		trajectory.addAll(createSpeedCoord(10, 3, 15)); 
		trajectory.addAll(createSpeedCoord(20, 15, 100)); 
		trajectory.addAll(createSpeedCoord(12, 5, 15)); 
		
		List<Trajectory> subTrajectories = MeanTransportSplitter.subBySpeed(trajectory);
		assertEquals(1, subTrajectories.size());
		assertEquals(92, subTrajectories.get(0).size());
	}
	
	@Test
	public void evaluateWalkWithNoise() {
		Trajectory trajectory = new Trajectory();
		trajectory.addAll(createSpeedCoord(20, 5, 10)); 
		trajectory.addAll(createSpeedCoord(1, 11, 15)); //noise
		trajectory.addAll(createSpeedCoord(20, 5, 10)); 
		
		List<Trajectory> subTrajectories = MeanTransportSplitter.subBySpeed(trajectory);
		assertEquals(1, subTrajectories.size());
		assertEquals(41, subTrajectories.get(0).size());
	}
	
	
	private List<Coordinate> createSpeedCoord(int size, int min, int max) {
		List<Coordinate> coords = new ArrayList<>(size);
		Random rand = new Random();
		for (int i=0; i < size; i++) {
			coords.add(new Coordinate(Speed.fromKmh(rand.nextInt(max)+min)));
		}
		return coords;
	}


}

//carro redução de velocidade, retomada
//1 trajetória

//caminhada, ruido, caminhada

//estava de carro e passou a caminhar

//caminhada, carro, caminhada


//carro, caminhada, carro
//Três Trajetórias
//30 40 50 40 30 20 10 9 8 7 5 5 4 5 3 5 5 5 3 6 7 8 9 20 30 40 50 50 50



//estava caminhando e pegou o carro
//Duas Trajetórias
//4 3 4 3 2 5 5 5 5 5 3 4 7 6 5 18 27 29 30 
//a cada duas coordenadas verifica se é caminhada, se sim, vai acumulando o contador...
//
