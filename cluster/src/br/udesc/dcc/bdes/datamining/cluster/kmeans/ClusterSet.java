package br.udesc.dcc.bdes.datamining.cluster.kmeans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class ClusterSet {
	private Map<Cluster, Double> clusters;
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
		this.clusters = new HashMap<Cluster, Double>(k);
	}

	public Set<Cluster> getClusters() {
		return clusters.keySet();
	}

	public void add(Cluster cluster) {
		if (clusters.keySet().size() == k) {
			throw new RuntimeException("cluster cannot be bigger than " + k);
		}
		k++;
		clusters.put(cluster, new Double(0.0));
	}

	public boolean containsCentroid(Element element) {
		for (Cluster cluster : clusters.keySet()) {
			if (cluster.isCentroid(element) ){
				return true;
			}
		}
		return false;
	}

	public double getSquareResidualDistance(Cluster cluster) {
		return clusters.get(cluster).doubleValue();
	}

	public void calculateSquareResidualDistance() {
		for (Cluster cluster : clusters.keySet()) {
			Double residualDistance = new Double(cluster.squareResidualSum());
			clusters.put(cluster, residualDistance);
		}
	}
	
	public double getAllSquareResidualDistance() {
		double total = 0;
		for (Cluster cluster : clusters.keySet()) {
			total += clusters.get(cluster).doubleValue();
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
