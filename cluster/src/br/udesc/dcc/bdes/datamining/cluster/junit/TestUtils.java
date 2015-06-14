package br.udesc.dcc.bdes.datamining.cluster.junit;

public class TestUtils {
	
	public static boolean isBalanced(int expectedSize, int actualSize, int acceptedPercentageError) {
		double actualErrorPercentage = Math.abs(100 - (actualSize*100)/expectedSize);
		System.out.println("Expected: " + expectedSize + " Actual: " + actualSize + " Error: " + actualErrorPercentage + "% Max Error: " + acceptedPercentageError+"%");
		return actualErrorPercentage <= acceptedPercentageError;
	}

}
