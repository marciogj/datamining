package br.udesc.dcc.bdes.datamining.cluster.centroid;

import br.udesc.dcc.bdes.datamining.cluster.centroid.element.Element;

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
	
	public static String elementsToString(Element[] elements) {
		String str = "";
		for (Element element : elements) {
			str += element + " |\t";
		}
		return str;
	}
	
	public static String matrixToString(double[][] matrix) {
		String strMatrix = "";
		String strRow = "";
		
		for (int row = 0; row < matrix.length; row++) {
			strRow = "";
			for (int column = 0; column < matrix[0].length; column++) {
				strRow += matrix[row][column] + " |\t";
			}
			strMatrix += strRow + System.getProperty("line.separator");
		}
		return strMatrix;
	}

}
