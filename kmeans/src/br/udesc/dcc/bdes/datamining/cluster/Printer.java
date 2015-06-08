package br.udesc.dcc.bdes.datamining.cluster;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class Printer {
	private static final String lineBreak = System.getProperty("line.separator");

	public static String clusterSetToString(ClusterSet clusterSet) {
		double distance = 0;
		String str = "";
		for (Cluster cluster : clusterSet.getClusters()) {
			distance = clusterSet.getSquareResidualDistance(cluster);
			str += "{RD" + ": " + distance + ", Cluster: " + clusterToString(cluster) + "}"+lineBreak;
		}
		str += "ClusterSet Residual Sum " + clusterSet.getAllSquareResidualDistance();
		return str;
	}

	public static String clusterToString(Cluster cluster) {
		String str = cluster.getName() + " Mean: " + cluster.getCentroid() + "";
		String strElements = "";
		for (Element element : cluster.getElements()) {
			strElements += strElements.length() == 0 ? element : ", " + element;
		}
		return str + " - ["+ strElements +"]";
	}

}
