package br.udesc.dcc.bdes.filter;

/**
 * Simple Kalman Filter implementation according to Bilgin Esme which is simplified approach.
 * @see <a href="http://bilgin.esme.org/BitsBytes/KalmanFilterforDummies.aspx">Kalman Filter for Dummies</a>
 *  
 * @author Marcio.Jasinski
 *
 */
public class SimpleKalmanFilter {
	private double xPreviousEstimate; 			//^x_{k-1}
	private double pPreviousCovarianceError; 	//P_{k-1}
	
	private double xPriorEstimate;				//^x`_{k}
	private double pPriorCovarianceError;		//P`_{k}
	
	private double kalmanGain;					//K_{k}
	private double stateXk; 					//^x_{k}
	private double Pk;							//P_{k}
	
	private double R;
	
	/**
	 * Creates a Kalman Filter receiving expected measurement noise and using zero as initial measurement value.
	 * 
	 * @param noiseDeviation standard deviation of the measurement noise
	 */
	public SimpleKalmanFilter(double noiseDeviation) {
		this(noiseDeviation, 0);
	}
	
	/**
 	 * Creates a Kalman Filter receiving expected measurement noise and a initial measurement value.
 	 * 
	 * @param noiseDeviation
	 * @param initialX
	 */
	public SimpleKalmanFilter(double noiseDeviation, double initialX) {
		R = noiseDeviation;
		xPreviousEstimate = initialX;
		pPreviousCovarianceError = 1;
		kalmanGain = 0;
	}
	
	/**
	 * Time Update Estimation will predict the new values based on previous values.
	 * Since this is a very simple Kalman Filter, new prediction is equals to last adjusted value.
	 * Same applies to covariance error which is assigned to adjusted covariance using Kalman gain.
	 */
	public void prediction() {
		xPriorEstimate = xPreviousEstimate;
		pPriorCovarianceError = pPreviousCovarianceError;
	}
	
	/**
	 * Measurement Update is responsible to adjust predicted values using Kalman gain and real values.  
	 */
	public void correction(double zk) {
		kalmanGain = pPriorCovarianceError / (pPriorCovarianceError + R);
		stateXk = xPriorEstimate + (kalmanGain * (zk - xPriorEstimate)) ;
		Pk = (1- kalmanGain) * pPriorCovarianceError;
		
		
		xPreviousEstimate = stateXk;
		pPreviousCovarianceError = Pk;
	}
	
	/**
	 * Iteration over a measurement value will perform a prediction followed by a correction.
	 * @param zk The measurement value
	 */
	public void iterate(double zk) {
			prediction();
			correction(zk);
	}
	
	public double getPriorX() {
		return xPriorEstimate;
	}

	public double getPriorCovarianceError() {
		return pPriorCovarianceError;
	}

	public double getCurrentKalmanGain() {
		return kalmanGain;
	}

	public double getCurrentX() {
		return stateXk;
	}

	public double getCurrentCovarianceError() {
		return Pk;
	}

	public double getStandardDeviationNoise() {
		return R;
	}
	
}
