package br.udesc.dcc.bdes.datamining.cluster.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.udesc.dcc.bdes.datamining.cluster.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class KMeans {

	public KMeans() { }

	public ClusterSet findClusterDistribuition(int k, List<Element> data, int maxIterations) {
		ClusterSet newCluster = createClusterWithRandmonCentroids(k, data);
		distribuiteDataIntoClusters(data, newCluster);
		newCluster.calculateSquareResidualDistance();
		//System.out.println(Printer.clusterSetToString(newCluster));
		ClusterSet lastCluster = newCluster;

		int i = 0;
		while (i < maxIterations) {
			//System.out.println("----"+ i +"----");
			newCluster = newCluster.createMeanCentroidsCluster();
			distribuiteDataIntoClusters(data, newCluster);
			newCluster.calculateSquareResidualDistance();
			//System.out.println(Printer.clusterSetToString(newCluster));

			if (lastCluster.getAllSquareResidualDistance() == newCluster.getAllSquareResidualDistance()) {
				return newCluster;
			}
			lastCluster = newCluster;
			i++;
		}

		return newCluster;
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

	private void distribuiteDataIntoClusters(List<Element> data, ClusterSet clusterSet) {
		for (Element element : data) {
			Cluster shorterDistanceCluster = null;
			double shorteDistance = Integer.MAX_VALUE;
			double currentDistance = 0;

			for (Cluster cluster : clusterSet.getClusters()) {
				currentDistance = element.distance(cluster.getCentroid());
				if (currentDistance < shorteDistance ) {
					shorterDistanceCluster = cluster;
					shorteDistance = currentDistance;
				}
			}
			shorterDistanceCluster.add(element);	
		}

	}

}
