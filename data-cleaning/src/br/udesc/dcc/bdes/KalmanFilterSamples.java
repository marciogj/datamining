package br.udesc.dcc.bdes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import br.udesc.dcc.bdes.analysis.deprecated.DeprecatedTrajectoryEvaluator;
import br.udesc.dcc.bdes.filter.SimpleKalmanFilter;
import br.udesc.dcc.bdes.io.InfoWriter;
import br.udesc.dcc.bdes.io.PltFileReader;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;


public class KalmanFilterSamples {

	public static void main(String[] args) {
		double measurementNoise = 0.1d;
		double processNoise = 1d;
		// A = [ 1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
		// B = null
		RealMatrix B = new Array2DRowRealMatrix(new double[] { 1d });
		// H = [ 1 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
		// x = [ 10 ]
		RealVector x = new ArrayRealVector(new double[] { 0d });
		// Q = [ 1e-5 ]
		RealMatrix Q = new Array2DRowRealMatrix(new double[] { processNoise });
		// P = [ 1 ]
		RealMatrix P0 = new Array2DRowRealMatrix(new double[] { 1d });
		// R = [ 0.1 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { measurementNoise });

		ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
		MeasurementModel mm = new DefaultMeasurementModel(H, R);
		KalmanFilter filter = new KalmanFilter(pm, mm);  


		StringBuffer strXValues = new StringBuffer();
		StringBuffer strPValues = new StringBuffer();
		StringBuffer strKValues = new StringBuffer();


		String beaconPath = "C:\\Users\\marcio.jasinski\\tmp\\2015.12.16\\medicoes\\2 min\\motog\\beacon_A_2.000_350.000.log";
		File beaconFile = new File(beaconPath);
		List<Double> zks = new LinkedList<>();
		try ( BufferedReader reader = new BufferedReader(new FileReader(beaconFile))) {			
			String line = reader.readLine();
			while( line != null ) {
				line = reader.readLine();
				if (line == null || line.length() == 0) continue; 
				String[] parts = line.split(",");
				double signal = Double.parseDouble(parts[2].trim());
				zks.add(signal);

			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		RealVector pNoise = new ArrayRealVector(1);
		RealVector mNoise = new ArrayRealVector(1);
		RealVector z = new ArrayRealVector(1); 
		
		for (double zk : zks) {
			filter.predict();
			mNoise.setEntry(0,  zk - x.getEntry(0));
			
			z.setEntry(0, zk);

			
			
			double signal = filter.getStateEstimation()[0];
			filter.correct(z);

			signal = filter.getStateEstimation()[0];
			x.setEntry(0, signal);

			strXValues.append(x.getEntry(0) + "  ");
			strPValues.append(filter.getErrorCovarianceMatrix().getEntry(0, 0)+"  ");
			
			pNoise.setEntry(0, filter.getErrorCovarianceMatrix().getEntry(0, 0));
		}
		
		
		
		
		System.out.println("^x\t" + strXValues);
		System.out.println("P\t" + strPValues);
		System.out.println("K\t" + strKValues);		



	}

	public static void simpleKalmanFileFilter() {
		StringBuffer strXValues = new StringBuffer();
		StringBuffer strPValues = new StringBuffer();
		StringBuffer strKValues = new StringBuffer();


		String beaconPath = "C:\\Users\\marcio.jasinski\\tmp\\2015.12.16\\medicoes\\2 min\\motog\\beacon_A_2.000_350.000.log";
		File beaconFile = new File(beaconPath);
		List<Double> zks = new LinkedList<>();
		try ( BufferedReader reader = new BufferedReader(new FileReader(beaconFile))) {			
			String line = reader.readLine();
			while( line != null ) {
				line = reader.readLine();
				if (line == null || line.length() == 0) continue; 
				String[] parts = line.split(",");
				double signal = Double.parseDouble(parts[2].trim());
				zks.add(signal);

			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		double noiseDeviation = 0.1d;
		SimpleKalmanFilter filter = new SimpleKalmanFilter(noiseDeviation, zks.get(0));
		for (double zk : zks) {
			filter.iterate(zk);
			strKValues.append(filter.getCurrentKalmanGain()+"  ");
			strXValues.append(filter.getCurrentX()+"  ");
			strPValues.append(filter.getCurrentCovarianceError()+"  ");
		}		
		System.out.println("^x\t" + strXValues);
		System.out.println("P\t" + strPValues);
		System.out.println("K\t" + strKValues);		
	}


	/**
	 * Increasing Speed Vehicle Example
	 * The following example creates a Kalman filter for a simple linear process: a vehicle driving along a street with a 
	 * velocity increasing at a constant rate. The process state is modeled as (position, velocity) and we only observe the position. 
	 * A measurement noise of 10m is imposed on the simulated measurement.
	 * 
	 */
	public static void kalmanFilter() {
		//double eps = 25.0;
		//int minPts = 4;		
		//Trajectory trajectory = PltFileReader.read("20081023055305.plt");			
		//print(TrajectoryEvaluator.evaluate(trajectory));

		// discrete time interval
		double dt = 0.1d;

		// position measurement noise (meter)
		double positionNoise = 10d;

		// acceleration noise (meter/sec^2)
		double accelNoise = 0.2d;

		// A - state transition matrix A = [ 1 dt ]
		//		     [ 0  1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[][] { { 1, dt }, { 0, 1 } });

		// B - control input matrix B = [ dt^2/2 ]
		//		     [ dt     ]
		RealMatrix B = new Array2DRowRealMatrix(new double[][] { { Math.pow(dt, 2d) / 2d }, { dt } });

		// H - measurement matrix H = [ 1 0 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[][] { { 1d, 0d } });

		// x = [ 0 0 ]
		RealVector x = new ArrayRealVector(new double[] { 0, 0 });

		RealMatrix tmp = new Array2DRowRealMatrix(new double[][] {
				{ Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d },
				{ Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) } });

		// Q - process noise covariance matrix Q = [ dt^4/4 dt^3/2 ]
		//		     [ dt^3/2 dt^2   ]
		RealMatrix Q = tmp.scalarMultiply(Math.pow(accelNoise, 2));

		// P - error covariance matrix P0 = [ 1 1 ]
		//		      [ 1 1 ]
		RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });

		// R - measurement noise covariance matrix R = [ measurementNoise^2 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { Math.pow(positionNoise, 2) });

		// constant control input, increase velocity by 0.1 m/s per cycle
		RealVector u = new ArrayRealVector(new double[] { 0.1d });

		ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
		MeasurementModel mm = new DefaultMeasurementModel(H, R);
		KalmanFilter filter = new KalmanFilter(pm, mm);

		RandomGenerator rand = new JDKRandomGenerator();

		RealVector tmpPNoise = new ArrayRealVector(new double[] { Math.pow(dt, 2d) / 2d, dt });
		RealVector mNoise = new ArrayRealVector(1);

		// iterate 60 steps
		for (int i = 0; i < 60; i++) {
			filter.predict(u);

			// simulate the process
			RealVector pNoise = tmpPNoise.mapMultiply(accelNoise * rand.nextGaussian());

			// x = A * x + B * u + pNoise
			x = A.operate(x).add(B.operate(u)).add(pNoise);

			// simulate the measurement
			mNoise.setEntry(0, positionNoise * rand.nextGaussian());

			// z = H * x + m_noise
			RealVector z = H.operate(x).add(mNoise);

			filter.correct(z);



			double position = filter.getStateEstimation()[0];
			double velocity = filter.getStateEstimation()[1];
			System.out.println(i + " postion: " + position  + " speed " + velocity);
		}



		//print(TrajectoryEvaluator.evaluate(cleanedTrajectory));
	}


	/**
	 * The following example creates a Kalman filter for a static process: a system with a constant voltage as internal state. 
	 * We observe this process with an artificially imposed measurement noise of 0.1V and assume an internal process noise of 1e-5V
	 */
	public static void apacheKalmanFilterVoltageExample() {
		double constantVoltage = 0.5d;
		double measurementNoise = 0.1d;
		double processNoise = 1;

		// A = [ 1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
		// B = null
		RealMatrix B = new Array2DRowRealMatrix(new double[] { 1d });;
		// H = [ 1 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
		// x = [ 10 ]
		RealVector x = new ArrayRealVector(new double[] { constantVoltage });
		// Q = [ 1e-5 ]
		RealMatrix Q = new Array2DRowRealMatrix(new double[] { processNoise });
		// P = [ 1 ]
		RealMatrix P0 = new Array2DRowRealMatrix(new double[] { 1d });
		// R = [ 0.1 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { measurementNoise });

		ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
		MeasurementModel mm = new DefaultMeasurementModel(H, R);
		KalmanFilter filter = new KalmanFilter(pm, mm);  

		// process and measurement noise vectors
		RealVector pNoise = new ArrayRealVector(1);
		RealVector mNoise = new ArrayRealVector(1);

		RandomGenerator rand = new JDKRandomGenerator();
		// iterate 60 steps
		for (int i = 0; i < 10; i++) {

			filter.predict();

			// simulate the process
			pNoise.setEntry(0, processNoise * rand.nextGaussian());

			// x = A * x + p_noise
			x = A.operate(x).add(pNoise);

			// simulate the measurement
			mNoise.setEntry(0, measurementNoise * rand.nextGaussian());

			// z = H * x + m_noise
			RealVector z = H.operate(x).add(mNoise);



			double voltage = filter.getStateEstimation()[0];
			System.out.println("--- " + i + " ---");
			System.out.println("z="+z);
			System.out.println("Time Update");
			System.out.println("^-xk=" + x);
			System.out.println("-Pk="+pNoise);


			filter.correct(z);

			System.out.println("Measurement Update");
			System.out.println("Kk=");
			System.out.println("Pk=");
			System.out.println("xk=" + voltage);
			System.out.println("------");
		}

	}


	public static void apacheKalmanFilterVoltage() {
		//double constantVoltage = 0.5d;
		double measurementNoise = 0.1d;
		double processNoise = 1d;

		// A = [ 1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
		// B = null
		RealMatrix B = new Array2DRowRealMatrix(new double[] { 1d });
		// H = [ 1 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
		// x = [ 10 ]
		RealVector x = new ArrayRealVector(new double[] { 0d });
		// Q = [ 1e-5 ]
		RealMatrix Q = new Array2DRowRealMatrix(new double[] { processNoise });
		// P = [ 1 ]
		RealMatrix P0 = new Array2DRowRealMatrix(new double[] { 1d });
		// R = [ 0.1 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { measurementNoise });

		ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
		MeasurementModel mm = new DefaultMeasurementModel(H, R);
		KalmanFilter filter = new KalmanFilter(pm, mm);  

		// process and measurement noise vectors
		RealVector pNoise = new ArrayRealVector(1);
		RealVector mNoise = new ArrayRealVector(1);

		//RandomGenerator rand = new JDKRandomGenerator();
		double[] zks = {0.39, 0.5, 0.48, 0.29, 0.25, 0.32, 0.34, 0.48, 0.41, 0.45};
		// iterate 60 steps
		//for (int i = 0; i < 10; i++) {
		RealVector z = new ArrayRealVector(1) ;
		StringBuffer strTable = new StringBuffer("i\tz\t^-xk\t-Pk\txk\tPk\n");
		pNoise.setEntry(0, processNoise);
		for (int i = 0; i < zks.length; i++) {
			filter.predict();

			// simulate the process
			//pNoise.setEntry(0, processNoise * rand.nextGaussian());
			// x = A * x + p_noise
			//x = A.operate(x).add(pNoise);
			//x = xk-1

			// simulate the measurement
			//mNoise.setEntry(0, measurementNoise * rand.nextGaussian());
			mNoise.setEntry(0,  zks[i] - x.getEntry(0));
			// z = H * x + m_noise			
			z.setEntry(0, zks[i]);

			double voltage = filter.getStateEstimation()[0];

			strTable.append(i+"\t");
			strTable.append(z+"\t");

			//System.out.println("Time Update");
			strTable.append(x+"\t");

			//System.out.println("^-xk=" + x);
			//System.out.println("-Pk="+pNoise);
			strTable.append(pNoise+"\t");

			filter.correct(z);


			voltage = filter.getStateEstimation()[0];
			x.setEntry(0, voltage);

			//System.out.println("Measurement Update");
			//System.out.println("xk=" + voltage);
			//System.out.println("Pk=" + filter.getErrorCovarianceMatrix().getEntry(0, 0));
			//P0 = filter.getErrorCovarianceMatrix();

			strTable.append(voltage+"\t");
			strTable.append(filter.getErrorCovarianceMatrix().getEntry(0, 0)+"\n");

			pNoise.setEntry(0, filter.getErrorCovarianceMatrix().getEntry(0, 0));
		}

		System.out.println(strTable.toString());

	}


	/**
	 * Increasing Speed Vehicle Example
	 * The following example creates a Kalman filter for a simple linear process: a vehicle driving along a street with a 
	 * velocity increasing at a constant rate. The process state is modeled as (position, velocity) and we only observe the position. 
	 * A measurement noise of 10m is imposed on the simulated measurement.
	 * 
	 */
	public static void apacheKalmanFilterCoordinate() {
		//double eps = 25.0;
		//int minPts = 4;		
		Trajectory trajectory = PltFileReader.read("20081023055305.plt");			
		TrajectoryUtils.print(DeprecatedTrajectoryEvaluator.evaluate(trajectory));

		System.out.println("-------------");
		Coordinate first = trajectory.getCoordinates().iterator().next();
		Trajectory cleanedTrajectory = new Trajectory();

		// discrete time interval
		double dt = 1.0d;

		// position measurement noise (meter)
		double positionNoise = 10d;

		// acceleration noise (meter/sec^2)
		double accelNoise = 0.1d;


		// A - state transition matrix A = [ 1 dt ]
		//		     [ 0  1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[][] { { 1, dt }, { 0, 1 } });

		// B - control input matrix B = [ dt^2/2 ]
		//		     [ dt     ]
		//RealMatrix B = new Array2DRowRealMatrix(new double[][] { { Math.pow(dt, 2d) / 2d }, { dt } });
		RealMatrix B = null;

		// H - measurement matrix H = [ 1 0 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[][] { { 1d, 0d } });

		// x = [ 0 0 ]
		RealVector x = new ArrayRealVector(new double[] { first.getLatitude(), 0 });
		RealVector xLon = new ArrayRealVector(new double[] { first.getLongitude(), 0 });

		RealMatrix tmp = new Array2DRowRealMatrix(new double[][] {
				{ Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d },
				{ Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) } });

		// Q - process noise covariance matrix Q = [ dt^4/4 dt^3/2 ]
		//		     [ dt^3/2 dt^2   ]
		RealMatrix Q = tmp.scalarMultiply(Math.pow(accelNoise, 2));

		// P - error covariance matrix P0 = [ 1 1 ]
		//		      [ 1 1 ]
		RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });

		// R - measurement noise covariance matrix R = [ measurementNoise^2 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { Math.pow(positionNoise, 2) });

		// constant control input, increase velocity by 0.1 m/s per cycle
		//RealVector u = new ArrayRealVector(new double[] { 0.1d });

		ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
		MeasurementModel mm = new DefaultMeasurementModel(H, R);
		KalmanFilter filter = new KalmanFilter(pm, mm);

		KalmanFilter filterLon = new KalmanFilter(pm, mm);

		RandomGenerator rand = new JDKRandomGenerator();

		RealVector tmpPNoise = new ArrayRealVector(new double[] { Math.pow(dt, 2d) / 2d, dt });
		RealVector mNoise = new ArrayRealVector(1);

		// iterate 60 steps
		int i = 0;
		for (Coordinate coordinate : trajectory.getCoordinates()) {
			filter.predict();

			// simulate the process
			RealVector pNoise = tmpPNoise.mapMultiply(accelNoise * rand.nextGaussian());

			// x = A * x + B * u + pNoise
			x = A.operate(x).add(pNoise);



			double vkLat = coordinate.getLatitude() - x.getEntry(0);



			// simulate the measurement
			mNoise.setEntry(0, vkLat);

			// z = H * x + m_noise
			RealVector z = H.operate(x).add(mNoise);

			filter.correct(z);


			xLon = A.operate(xLon).add(pNoise);
			double vkLon = coordinate.getLongitude() - xLon.getEntry(0);
			mNoise.setEntry(0, vkLon);
			RealVector zLon = H.operate(xLon).add(mNoise);
			filterLon.correct(zLon);



			double position = filter.getStateEstimation()[0];
			double velocity = filter.getStateEstimation()[1];
			System.out.println(i + " n_postion: " + position  + " r_postion: " + coordinate.getLatitude()  + " speed " + velocity);
			i++;


			double lonPosition = coordinate.getLongitude();
			System.out.println(i + " lon_postion: " + lonPosition  + " rlon_postion: " + coordinate.getLongitude());

			cleanedTrajectory.add(new Coordinate(position, lonPosition, coordinate.getAltitude(), coordinate.getDateTime()));
		}



		TrajectoryUtils.print(DeprecatedTrajectoryEvaluator.evaluate(cleanedTrajectory));

		//save("apache_lat_kalman", cleanedTrajectory.getCoordinates());
	}


	public static void kalmanFilterSimple() {
		float q = 1.0f;
		float accuracy = 150.0f;
		br.udesc.dcc.bdes.filter.KalmanFilter kalman = new  br.udesc.dcc.bdes.filter.KalmanFilter(q);

		StringBuffer algorithmInfo = new StringBuffer("== Input ==");
		algorithmInfo.append("\n" + kalman.getClass().getName());
		algorithmInfo.append("\nQ: " + q + " m/s");
		algorithmInfo.append("\nAccuracy: " + accuracy + " m");

		Trajectory rawTrajectory = PltFileReader.read("20081023055305.plt");
		String rawEvaluation = "=== Raw Trajectory Evaluation ===\n";
		rawEvaluation += TrajectoryUtils.evaluatedTrajectoryToString(DeprecatedTrajectoryEvaluator.evaluate(rawTrajectory));

		System.out.println(algorithmInfo);
		System.out.println(rawEvaluation);

		int i = 0;
		Trajectory cleanedTrajectory = new Trajectory();
		for(Coordinate coordinate : rawTrajectory.getCoordinates()) {

			if (i == 0) {
				kalman.setState(coordinate.getLatitude(), coordinate.getLongitude(), accuracy, coordinate.getDateTimeInMillis());
				i++;
				cleanedTrajectory.add(coordinate);
				continue;
			}

			kalman.process(coordinate.getLatitude(), coordinate.getLongitude(), accuracy, coordinate.getDateTimeInMillis());


			Coordinate newCoordinate = new Coordinate(kalman.getLatitude(), kalman.getLongitude(), 0.0d, coordinate.getDateTime());
			cleanedTrajectory.add(newCoordinate);

			//System.out.println(i + " postion: " + position  + " speed " + velocity);
			i++;
		}


		String cleanedEvaluation = "=== Cleaned Trajectory Evaluation ===\n";
		cleanedEvaluation += TrajectoryUtils.evaluatedTrajectoryToString(DeprecatedTrajectoryEvaluator.evaluate(cleanedTrajectory));
		System.out.println(cleanedEvaluation);

		System.out.println(cleanedEvaluation);

		TrajectoryUtils.save("simple_kalman", cleanedTrajectory.getCoordinates());
		String datetime = new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(new Date());
		InfoWriter.write("simple_kalman_"+datetime+".txt", algorithmInfo.toString(), rawEvaluation, cleanedEvaluation);

	}




}
