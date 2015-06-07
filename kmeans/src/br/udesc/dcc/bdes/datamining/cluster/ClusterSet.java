package br.udesc.dcc.bdes.datamining.cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class ClusterSet {
	private Map<Cluster, Integer> clusters;
	private int k;

	public static ClusterSet createNamedClusterSet(int k) {
		ClusterSet clusterSet = new ClusterSet(k);
		for(int i=0; i < k; i++) {
			char clusterLetter = (char) ('A' + i);
			StringBuilder clusterName = new StringBuilder();
			clusterName.append(clusterLetter);

			Cluster newCluster = new Cluster(clusterName.toString());
			clusterSet.add(newCluster);
		}

		return clusterSet;

	}

	public ClusterSet(int k) {
		this.k = k;
		this.clusters = new HashMap<Cluster, Integer>(k);
	}

	public Set<Cluster> getClusters() {
		return clusters.keySet();
	}

	public void add(Cluster cluster) {
		if (clusters.keySet().size() == k) {
			throw new RuntimeException("cluster cannot be bigger than " + k);
		}
		k++;
		clusters.put(cluster, new Integer(0));
	}

	public boolean containsCentroid(Element element) {
		for (Cluster cluster : clusters.keySet()) {
			if (cluster.isCentroid(element) ){
				return true;
			}
		}
		return false;
	}

	public int getSquareResidualDistance(Cluster cluster) {
		return clusters.get(cluster).intValue();
	}

	public void calculateSquareResidualDistance() {
		for (Cluster cluster : clusters.keySet()) {
			Integer residualDistance = new Integer(cluster.squareResidualSum());
			clusters.put(cluster, residualDistance);
		}
	}
	
	public int getSquareResidualDistance() {
		int total = 0;
		for (Cluster cluster : clusters.keySet()) {
			total += clusters.get(cluster).intValue();
		}
		return total;
	}

	public ClusterSet createMeanCentroidsCluster() {
		ClusterSet newClusterSet = new ClusterSet(k);
		for (Cluster cluster : clusters.keySet()) {
			Element mean = cluster.meanCentroid();
			
			Cluster nCluster = new Cluster(cluster.getName());
			nCluster.setCentroid(mean);
			
			newClusterSet.add(nCluster);
		}

		return newClusterSet;
	}

}
