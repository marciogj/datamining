package br.udesc.dcc.bdes.datamining.cluster.fuzzy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.udesc.dcc.bdes.datamining.cluster.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.Printer;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class FuzzyCMeans {
	
	
	public ClusterSet findClusterDistribuition(int k, List<Element> data, int maxIterations) {
		int fuzzyIndex = 2;
		ClusterSet initialCluster = createClusterWithRandmonCentroids(k,  data);
		Printer.clusterSetToString(initialCluster);
		CMeansMatrix matrix = new CMeansMatrix(data, initialCluster.getCentroids(), fuzzyIndex);
		
		int i = 0;
		while (i < maxIterations) {
			matrix.updateMembershipIndex();
			matrix.updateCentroids();
			i++;
		}
				
		return matrix.getBestClusterSet();
	}
	
	private ClusterSet createClusterWithRandmonCentroids(int k, List<Element> data) {
		List<Element> centroids = pickRandomCentroids(k, data);
		ClusterSet initialCluster = ClusterSet.createNamedClusterSet(k);
		for (Cluster cluster : initialCluster.getClusters()) {
			System.out.println("Centroid selected " + centroids.get(0));
			cluster.setCentroid(centroids.remove(0));
		}
		return initialCluster;
	}
	
	private List<Element> pickRandomCentroids(int clusters, List<Element> data) {
		List<Element> centroids = new ArrayList<Element>(clusters);
		int dataSize = data.size();

		List<Integer> picked = new ArrayList<Integer>(); 
		Random random = new Random();
		Integer randomIndex; 
		for (int i=0; i < clusters; i++) {
			//Just ensure that same seed will not be selected twice
			do {
				randomIndex = new Integer(random.nextInt(dataSize));
			} while (picked.contains(randomIndex));

			picked.add(randomIndex);
			centroids.add(data.get(randomIndex.intValue()));
		}
		return centroids;
	}

}
