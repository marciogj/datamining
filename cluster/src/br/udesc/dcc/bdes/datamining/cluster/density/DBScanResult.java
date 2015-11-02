package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.ArrayList;
import java.util.Collection;

public class DBScanResult<T> {
	Collection<Cluster<T>> clusters = new ArrayList<>();
	Collection<T> noises = new ArrayList<>();
	
	public void addNoise(T noise) {
		noises.add(noise);
	}
	
	public void addCluster(Cluster<T> cluster) {
		clusters.add(cluster);
	}
	
	public boolean contains(T element) {
		for (Cluster<T> cluster : clusters) {
			if (cluster.contains(element) ){
				return true;
			}
		}
		return false;
	}

	public Collection<Cluster<T>> getClusters() {
		return clusters;
	}

	public Collection<T> getNoises() {
		return noises;
	}
	
	

}

