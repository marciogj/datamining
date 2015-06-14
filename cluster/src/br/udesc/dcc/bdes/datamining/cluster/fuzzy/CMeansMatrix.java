package br.udesc.dcc.bdes.datamining.cluster.fuzzy;

import java.util.List;

import br.udesc.dcc.bdes.datamining.cluster.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.Printer;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class CMeansMatrix {
	private Element[] centroids;
	private List<Element> elements;
	private double[][] membershipIndex;
	private double m;
	
	CMeansMatrix(List<Element> data, Element[] centroids, int m) {
		this.elements = data;
		this.centroids = centroids;
		this.m = m;
		this.membershipIndex = new double[elements.size()][centroids.length];		
	}
	
	//http://home.deib.polimi.it/matteucc/Clustering/tutorial_html/cmeans.html
	public void updateMembershipIndex() {
		int row = 0;
		
		double expoent = 2/(m -1);
		for (Element xi : elements) {
			int column = 0;
			for (int j=0; j < centroids.length; j++) {
				Element cj = centroids[j];
				
				double xi_cj = Math.abs( xi.euclideanDistance(cj) );
				double xi_ck = 0;
				Element ck = null;
				double denominator = 0;
				for (int k=0; k < centroids.length; k++) {
					ck = centroids[k];
					xi_ck =  Math.abs( xi.euclideanDistance(ck) );
					denominator += Math.pow(xi_cj/xi_ck, expoent);
				}
				
				double index = 1/denominator;
				//TODO: This is something I need a better understanding from math involved
				//When centroid is equal to the element, we should have association equal 1
				//However, the formula I'm using causes a NaN and also a division by zero chance:
				//xi - cj / xi - ck might turn out to be 0/0
				//I think I'm missing something but could not figure out what...
				if (cj == xi) {
					index = 1;
				}
				membershipIndex[row][column] = index;
				column++;
			}
			row++;
		}
		
		//printtable();
	}
	
	public void updateCentroids() {
		for (int j=0; j < centroids.length; j++) {
			centroids[j] = cj(j,m);
		}
	}
	
	private Element cj(int j, double m) {
		int N = elements.size();
		Element nominator = centroids[j].zero(); //TODO: Avoid such thing with template classes
		double denominator = 0;
		for (int i=0; i < N; i++) {
			Element xi = elements.get(i);
			double index = membershipIndex[i][j];
			double indexPowered = Math.pow(index, m); 
			nominator = nominator.plus(xi.multiply(indexPowered));
			denominator += Math.pow(membershipIndex[i][j], m);
		}
		
		return nominator.divide(denominator);
	}

	public ClusterSet getBestClusterSet() {
		ClusterSet clusterSet = ClusterSet.createNamedClusterSet(centroids);
		
		double higherIndex = 0;
		Element nearestCentroid = null;
				
		System.out.println("### INDEX RESULT ###");
		for (int row = 0; row < membershipIndex.length; row++) {
			higherIndex = 0;
			for (int column = 0; column < membershipIndex[0].length; column++) {
				double index = membershipIndex[row][column];
				System.out.print(index + "|\t");
				if (index >= higherIndex) {
					nearestCentroid = centroids[column];
					higherIndex = index;
				}				
			}
			Cluster cluster = clusterSet.getClusterByCentroid(nearestCentroid);
			cluster.add(elements.get(row));
			System.out.println("");	
		}
		clusterSet.calculateSquareResidualDistance();
		return clusterSet;
	}
	
	private void printtable() {
		System.out.println("----- Means ----");
		System.out.println(Printer.elementsToString(centroids));
		System.out.println("");
		
		System.out.println("----- INDEX TABLE ----");
		System.out.println(Printer.matrixToString(membershipIndex));
		System.out.println("-----------------");
	}
	
}
