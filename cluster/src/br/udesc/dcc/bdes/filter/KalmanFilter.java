package br.udesc.dcc.bdes.filter;


/**
 * Based on code from http://stackoverflow.com/questions/1134579/smooth-gps-data#
 *
 */
public class KalmanFilter {
	private final float MinAccuracy = 1;

	private float Q_metres_per_second;    
	private long timestamp;
	private double latitude;
	private double longitude;
	private float variance; // P matrix.  Negative means object uninitialised.  NB: units irrelevant, as long as same units used throughout

	public KalmanFilter(float Q_metres_per_second) {
		this.Q_metres_per_second = Q_metres_per_second; 
		variance = -1; 
	}

	public void process(double lat_measurement, double lng_measurement, float accuracy, long timestampMilis) {
		if (accuracy < MinAccuracy) accuracy = MinAccuracy;
		if (variance < 0) {
			// if variance < 0, object is unitialised, so initialise with current values
			this.timestamp = timestampMilis;
			latitude=lat_measurement; 
			longitude = lng_measurement; 
			variance = accuracy*accuracy;
		} else {
			// else apply Kalman filter methodology

			long TimeInc_milliseconds = timestampMilis - this.timestamp;
			if (TimeInc_milliseconds > 0) {
				// time has moved on, so the uncertainty in the current position increases
				variance += TimeInc_milliseconds * Q_metres_per_second * Q_metres_per_second / 1000;
				// uncertainty here is being modelled by a Wiener process (see en.wikipedia.org/wiki/Wiener_process )
				//and with a Wiener process the variance grows linearly with time. The variable Q_metres_per_second corresponds 
				//to the variable sigma in the section "Related processes" in that Wikipedia article. 
				//Q_metres_per_second is a standard deviation and it's measured in metres, so metres and not metres/seconds are its units. 
				//It corresponds to the standard deviation of the distribution after 1 second has elapse
				
				this.timestamp = timestampMilis;
				// TO DO: USE VELOCITY INFORMATION HERE TO GET A BETTER ESTIMATE OF CURRENT POSITION
			}

			// Kalman gain matrix K = Covarariance * Inverse(Covariance + MeasurementVariance)
			// NB: because K is dimensionless, it doesn't matter that variance has different units to lat and lng
			float K = variance / (variance + accuracy * accuracy);
			// apply K
			//stateXk = xPriorEstimate + (kalmanGain * (zk - xPriorEstimate)) ;
			latitude += K * (lat_measurement - latitude);
			longitude += K * (lng_measurement - longitude);
			// new Covarariance  matrix is (IdentityMatrix - K) * Covarariance 
			variance = (1 - K) * variance;
		}
	}
	
	public void setState(double lat, double lng, float accuracy, long timestampMilis) {
		this.latitude=lat; 
		this.longitude=lng; 
		this.variance = accuracy * accuracy; 
		this.timestamp= timestampMilis;
	}

	public long getTimetamp() {
		return timestamp; 
	}

	public double getLatitude() {
		return latitude; 
	}

	public double getLongitude() {
		return longitude; 
	}

	public float getAccuracy() {
		return (float)Math.sqrt(variance); 
	}

	
}