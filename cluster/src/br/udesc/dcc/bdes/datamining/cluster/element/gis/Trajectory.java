package br.udesc.dcc.bdes.datamining.cluster.element.gis;

import java.util.ArrayList;
import java.util.List;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class Trajectory implements Element {
	protected List<Coordinate> track;
	
	public Trajectory() {
		track = new ArrayList<Coordinate>();
	}

	@Override
	public double distance(Element element) {
		double distance = 0;
		Trajectory another = (Trajectory) element;
		if (another.track.size() != track.size()) {
			throw new RuntimeException("Trajectories must be normalized first to sum. Current size: " + track.size() + " received size " + + another.track.size());
		}
		for (int i=0; i < track.size(); i++) {
			Coordinate c1 = track.get(i);
			Coordinate c2 = another.track.get(i);
			
			distance += c1.distance(c2);
		}
		
		return distance;
	}

	@Override
	public Element plus(Element element) {
		Trajectory another = (Trajectory) element;
		//sum with zero will always return the other element
		if (another.track.size() == 0) return this;
		if (track.size() == 0) return element;
		
		Trajectory sum = new Trajectory();		
		if (another.track.size() != track.size()) {
			throw new RuntimeException("Trajectories must be normalized to sum them. Current size: " + track.size() + " received size " + + another.track.size());
		}
		for (int i=0; i < track.size(); i++) {
			Coordinate c1 = track.get(i);
			Coordinate c2 = another.track.get(i);
			sum.add((Coordinate) c1.plus(c2));
		}
		return sum;
	}

	public void add(Coordinate coordinate) {
		track.add(coordinate);
	}

	@Override
	public Element divide(double dividend) {
		Trajectory divided = new Trajectory();
		for (Coordinate coordinate : track) {
			divided.add((Coordinate)coordinate.divide(dividend));
		}
		return divided;
	}
	
	@Override
	public Element multiply(double value) {
		Trajectory divided = new Trajectory();
		for (Coordinate coordinate : track) {
			divided.add((Coordinate)coordinate.multiply(value));
		}
		return divided;
	}

	@Override
	public Element zero() {
		return new Trajectory();
	}
	
	@Override
	public String toString() {
		String strData = null; 
		for (Coordinate coordinate : track) {
			strData = strData == null ? coordinate.toString() : strData + ", " + coordinate;
		}
		return "{ size: " + track.size() + ", data: [" + strData + "]}";
	}
	
	@Override
	public double euclideanDistance(Element another){
		Trajectory otherTrajectory = (Trajectory) another;
		List<Coordinate> otherTrack = otherTrajectory.track;
		double sum = 0;
		for (int i=0; i < track.size();i++) {
			sum += track.get(i).euclideanDistance(otherTrack.get(i)); 
		}
		return sum;
	}

}
