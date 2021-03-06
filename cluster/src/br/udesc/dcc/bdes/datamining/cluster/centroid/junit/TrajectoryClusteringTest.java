package br.udesc.dcc.bdes.datamining.cluster.centroid.junit;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.udesc.dcc.bdes.datamining.cluster.centroid.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.centroid.Printer;
import br.udesc.dcc.bdes.datamining.cluster.centroid.element.Element;
import br.udesc.dcc.bdes.datamining.cluster.centroid.element.gis.Coordinate;
import br.udesc.dcc.bdes.datamining.cluster.centroid.element.gis.Trajectory;
import br.udesc.dcc.bdes.datamining.cluster.centroid.fuzzy.FuzzyCMeans;
import br.udesc.dcc.bdes.datamining.cluster.centroid.kmeans.KMeans;

public class TrajectoryClusteringTest {
	List<Element> data;
	int k;
	int maxIterations;
	
	@Before
	public void setup() {
		data = createSampleData();
		maxIterations = 100;
		k = 12;
	}
	
	/**
	 * Check details for straight line here:
	 * http://www.gpsvisualizer.com/map?format=google&units=metric&lat1=-26.8997445&lon1=-49.2358981&lat2=-27.5953778&lon2=-48.5480499&name1=-26.8997445%2C+-49.235898099999986&name2=-27.5953778%2C+-48.548049900000024&desc1=-26.8997445%2C+-49.2358981&desc2=-27.5953778%2C+-48.5480499&convert_format=&gc_segments=&gc_altitude=&tickmark_interval=&show_wpt=3&add_elevation=&trk_colorize=
	 */
	@Test
	public void kMeansTrajectoryTest() {
		System.out.println("=== KMeans ===");
		System.out.println("Solution for k=" + k);
		
		long startTime = System.nanoTime();
		KMeans kmeans = new KMeans();
		ClusterSet solution = kmeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		
		System.out.println(Printer.clusterSetToString(solution));
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
				
		assertTrue(true);
		System.out.println("=========================");
	}
	
	@Test
	public void fuzzyCMeansTrajectoryTest() {
		System.out.println("=== FuzzyCMeans ===");
		System.out.println("Solution for k=" + k);
		
		long startTime = System.nanoTime();
		FuzzyCMeans fuzzyCMeans = new FuzzyCMeans();
		ClusterSet solution = fuzzyCMeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		
		System.out.println(Printer.clusterSetToString(solution));
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
				
		assertTrue(true);
		System.out.println("=========================");
	}

	public List<Element> createSampleData() {
		List<Element> data = new ArrayList<Element>();
		//Random GPS data might be generated here http://www.geomidpoint.com/random/
		Coordinate blumenau = new Coordinate(-26.9165792, -49.07173310000002);
		Coordinate florianopolis = new Coordinate(-27.5953778, -48.548049900000024);
		Coordinate indaial  = new Coordinate(-26.8997445, -49.235898099999986);
		Coordinate tijucas = new Coordinate(-27.240063, -48.633618299999966);

		Coordinate[] cities = {blumenau, indaial, florianopolis, tijucas};
		int trajectorySamples = 50;
		int coordinateCount = 150;
		for (Coordinate from : cities) {
			for (Coordinate to : cities) {
				if (!from.equals(to)){
					for(int i=0; i < trajectorySamples;i++) {
						data.add(createRandomTrajectory(from, to, coordinateCount));
					}
				}
			}
		}
		return data;
	}


	public Trajectory createRandomTrajectory(Coordinate from, Coordinate to, int coordinates) {
		Trajectory trajectory = new Trajectory();
		double fromLatitude = from.getLatitude();
		double toLatitude = to.getLatitude();
		double latitudeSample = (toLatitude - fromLatitude)/coordinates;

		double fromLongitude = from.getLongitude();
		double toLongitude = to.getLongitude();
		double longitudeSample = (toLongitude - fromLongitude)/coordinates;

		double currentLatitude = fromLatitude;
		double currentLongitude = fromLongitude;
		trajectory.add(from);
		for(int i=0; i < (coordinates-2); i++) {
			currentLatitude += latitudeSample + randomVariation(10000);
			currentLongitude += longitudeSample + randomVariation(10000);
			trajectory.add(new Coordinate(currentLatitude, currentLongitude));
		}
		trajectory.add(to);
		
		return trajectory;
	}

	private double randomVariation(int decimalScale) {
		double randomVariation = Math.random()/decimalScale;
		randomVariation *= ( (int) (randomVariation * 10) ) % 2 == 0 ? 1 : -1;
		return randomVariation;
	}

}
