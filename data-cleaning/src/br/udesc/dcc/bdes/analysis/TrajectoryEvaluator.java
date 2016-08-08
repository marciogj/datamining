package br.udesc.dcc.bdes.analysis;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jongo.marshall.jackson.oid.MongoId;

import br.udesc.dcc.bdes.analysis.AccInterval;
import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;
import br.udesc.dcc.bdes.analysis.SpeedLimit;
import br.udesc.dcc.bdes.analysis.TrackEvaluator;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluatorId;
import br.udesc.dcc.bdes.analysis.newapp.AccDist;
import br.udesc.dcc.bdes.analysis.newapp.Index;
import br.udesc.dcc.bdes.analysis.newapp.SpeedDist;
import br.udesc.dcc.bdes.google.geocoding.GeocodeAddress;
import br.udesc.dcc.bdes.google.geocoding.InverseGeocodingClient;
import br.udesc.dcc.bdes.google.places.ImportantPlace;
import br.udesc.dcc.bdes.model.Acceleration;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.Distance;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.PenaltyAlert;
import br.udesc.dcc.bdes.model.PenaltyType;
import br.udesc.dcc.bdes.model.Speed;
import br.udesc.dcc.bdes.model.SpeedIndexEval;
import br.udesc.dcc.bdes.model.Time;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TrajectoryEvaluation;
import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.server.JettyServer;
import br.udesc.dcc.bdes.server.rest.api.track.dto.PenaltySeverity;


public class TrajectoryEvaluator {
	@MongoId
	private String _id;
	private String deviceId;
	private String driverId;
	//Used for sorting and helping to get latest evaluation from database
	private long latestTimestamp;
	
	private final Trajectory trajectory = new Trajectory();
	private Coordinate previousCoordinate = null;
	
	private double MAX_ALLOWED_SPEED = 80/3.6; //80 km/h

	private double totalDistance;
	private long totalTime;
	private double speedSum;
	private double maxSpeed;
	private double maxAccecelration;
	private double maxDeceleration;
	private double accecelerationSum;
	private int accecelerationCount;
	private int decelerationCount;
	private Collection<AccInterval> accelerations = new LinkedList<>();


	private int overMaxSpeedCount;
	private int overMaxAccelerationCount;
	private int overMaxDecelerationCount;

	private Coordinate accelerationStart = null;
	private double accDistance;
	private long accTime;
	private double accSpeedSum;
	private int accCoordinateCount;

	private Map<String, Double> streets = new HashMap<>();
	private List<PenaltyAlert> alerts = new LinkedList<>();

	private SpeedDist speedDist = null;
	private AccDist accDist = new AccDist();

	private SpeedIndexEval speedEvaluator = new SpeedIndexEval();

	private Distance segmentDistance = new Distance();
	private List<Double> segmentSpeedIndexes = new LinkedList<>();
	private List<Double> segmentAccelerationIndexes = new LinkedList<>();
	
	private double speedIndexSum = 0.0;
	private double accIndexSum = 0.0;
	
	
	private Index accIndex = new Index();
	
	//private List<Double> segmentDeccelerationIndexes = new LinkedList<>();

	
	private double maxAggressiveIndex = 0.0;
	private int speedSegmentSize = 1;
	private int accSegmentSize = 1;

	private AccelerationEvaluator accEvaluator = new AccelerationEvaluator();

	private OpenWeatherConditionDTO currentWeather = null; 

	private PenaltyAlert speedAlert = null;
	private PenaltyAlert accAlert = null;
	private int newAlertsCount = 0;
	


	//tmp
	public static int sequence = 1;
	
	public Double previousAngle = null;
	public int angleConsecutiveChange = 0;
	
	private GeocodeAddress currentAddress;
	private Map<String, ImportantPlace> importantPlaces = new HashMap<>(); 
	
	
	public static synchronized int nextSequence() {
		return ++sequence;
	}
	
	public TrajectoryEvaluator(){
		//this._id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		this._id = "" + nextSequence();
		speedDist = new SpeedDist(MAX_ALLOWED_SPEED);
	}
	
	public TrajectoryEvaluator(DeviceId deviceId, DriverId driverId) {
		this();
		this.deviceId = deviceId.getValue();
		this.driverId = driverId.getValue();
	}
	
	public TrajectoryEvaluator(double maxAllowedSpeed, double maxAcceleration, double maxDeceleration) {
		super();
		MAX_ALLOWED_SPEED = maxAllowedSpeed;
		speedDist = new SpeedDist(MAX_ALLOWED_SPEED);
	}
	
	public void evaluate(Collection<Coordinate> coordinates) {
		for (Coordinate coordinate : coordinates) {
			evaluate(coordinate);
		}
	}

	public Coordinate evaluate(Coordinate coordinate) {
		trajectory.add(coordinate);
		latestTimestamp = coordinate.getDateTimeInMillis();
		accCoordinateCount++;
		if (previousCoordinate == null) {
			previousCoordinate = coordinate;
			accelerationStart = coordinate;
			return coordinate;
		}

		Coordinate currentCoordinate = this.updateMomentum(previousCoordinate, coordinate);
		double distanceFromPrevious = coordinate.distanceInMeters(previousCoordinate);
		double elapsedTime = (currentCoordinate.getDateTimeInMillis() - previousCoordinate.getDateTimeInMillis())/1000;
		double currentSpeed = currentCoordinate.getSpeed().isPresent() ? currentCoordinate.getSpeed().get() : 0;
		double currentAcceleration = currentCoordinate.getAcceleration();
		double previousAcceleration = previousCoordinate.getAcceleration();

		
		//double currentAccIndex = accEvaluator.evaluate(currentAcceleration);
		double currentAccIndex = accDist.getAccIndex(currentAcceleration); //accEvaluator.evaluate(currentAcceleration);
		if (currentAccIndex >= 10) {
			accIndex.add(currentAccIndex);
		}
		
		
		coordinate.setAcceleration(currentCoordinate.getAcceleration());
		
		totalTime += elapsedTime;
		totalDistance += distanceFromPrevious;
		speedSum += currentSpeed;

		//new way
		speedDist.countSpeed(currentSpeed);
		
		
		double currentAngle = previousCoordinate.getBearing();
		//System.out.println("Angle " + currentAngle + " vs " + currentCoordinate.getBearing());
		if (previousAngle != null) {
			double angleDiff = Math.abs(previousAngle - currentAngle);
			if (angleDiff > 10) {
				//System.out.println("Angle Diff" + angleDiff);
				angleConsecutiveChange++;
			} else {
				angleConsecutiveChange = 0;
			}
			
		}
		
		if (angleConsecutiveChange >= 5) {
			System.err.println("Mudança de pista?");
			coordinate.setLaneChange(true);
		}
		
		previousAngle = currentAngle;
		
		
		if (currentSpeed > MAX_ALLOWED_SPEED) {
			overMaxSpeedCount++;
		}

		if(currentSpeed > maxSpeed) {
			maxSpeed = currentSpeed;
		}

		
		//Update max acceleration/deceleration 
		if (currentAcceleration > maxAccecelration) {
			maxAccecelration = currentAcceleration;
		} else if (currentAcceleration < maxDeceleration) {
			maxDeceleration = currentAcceleration;
		}

		boolean isAccelerationInverted = (previousAcceleration > 0) && (currentAcceleration < 0);
		boolean isDecelerationInverted = (previousAcceleration < 0) && (currentAcceleration > 0);

		accecelerationSum += Math.abs(currentAcceleration);
		if (isAccelerationInverted) {
			decelerationCount++;
		} else if (isDecelerationInverted) {
			accecelerationCount++;
		} 

		if (isAccelerationInverted || isDecelerationInverted) {
			AccInterval acceleration = new AccInterval(accelerationStart, currentCoordinate);
			acceleration.setDistance(accDistance);
			acceleration.setTime(accTime);
			acceleration.setSpeedAvg(accSpeedSum/accCoordinateCount);
			accelerations.add(acceleration);

			accelerationStart = currentCoordinate;
			accDistance = 0;
			accSpeedSum = 0;
			accTime = 0;
			
		} else {
			accDistance += distanceFromPrevious;
			accSpeedSum += currentAcceleration;
			accTime += elapsedTime;
			// It should not increase acc count again
			//if (currentAcceleration >= 0) {
			//	accecelerationCount++;
			//} else {
			//	decelerationCount++;
			//}
		}
		
		coordinate.setSpeedLimit(MAX_ALLOWED_SPEED);
		
		previousCoordinate = currentCoordinate;
		
		segmentDistance.increase(distanceFromPrevious);
		double aggressiveSpeedIndex = speedEvaluator.evaluate(currentSpeed); 
		if (aggressiveSpeedIndex > 0) {
			segmentSpeedIndexes.add(aggressiveSpeedIndex);
		}

		double aggressiveAccIndex = accEvaluator.evaluate(currentAcceleration);
		accEvaluator.count(currentAcceleration);
		if (aggressiveAccIndex > 0) {
			segmentAccelerationIndexes.add(aggressiveAccIndex);
		}
		if (segmentDistance.getKilometers() >= 1) {
			Optional<GeocodeAddress> optCurrentAddress = Optional.empty();
			boolean externalData = false;
			if (externalData) {
				List<ImportantPlace> places = TrackEvaluator.getImportantPlaces(coordinate.getLatitude(), coordinate.getLongitude());
				optCurrentAddress = getAddress(coordinate.getLatitude(), coordinate.getLongitude());
				//TODO: Update
				//Optional<OpenWeatherConditionDTO> optWeather = TrackEvaluator.getWeather(coord.getLatitude(), coord.getLongitude());
				this.addAll(places);
			}
			
			
			
			if (optCurrentAddress.isPresent()) {
				currentAddress = optCurrentAddress.get();
				String streetName = currentAddress.getStreetName();
				Double previous = streets.get(streetName);
				previous = previous == null ? 0 : previous;

				streets.put(streetName, previous + segmentDistance.getMeters());
				Speed currentSpeedLimit = SpeedLimit.getSpeedByAddress(streetName);
				//double factor25 = currentSpeedLimit.getMs() * 0.25;
				//MAX_ALLOWED_SPEED = currentSpeedLimit.getMs() - factor25;
				MAX_ALLOWED_SPEED = currentSpeedLimit.getMs();
				speedEvaluator.changeMax(new Speed(MAX_ALLOWED_SPEED));
				
				speedDist.setMaxSpeed(MAX_ALLOWED_SPEED);
			} else if (currentAddress != null) {
				String streetName = currentAddress.getStreetName();
				Speed currentSpeedLimit = SpeedLimit.getSpeedByAddress(streetName);
				MAX_ALLOWED_SPEED = currentSpeedLimit.getMs();
			}
				 
			
			
			updateAggressiveIndex();
			clearSegment();
		}
		
		Optional<PenaltyAlert> newSpeedAlert = evaluatePenalty(PenaltyType.SPEEDING, aggressiveSpeedIndex);
		Optional<PenaltyAlert> optSpeedAlert = updateEvaluationPenalties(newSpeedAlert, Optional.ofNullable(speedAlert), coordinate, distanceFromPrevious, currentSpeed);
		speedAlert = optSpeedAlert.isPresent() ? optSpeedAlert.get() : null;
		
		Optional<PenaltyAlert> newAccAlert = evaluatePenalty(PenaltyType.ACCELERATING, aggressiveAccIndex);
		Optional<PenaltyAlert> optAccAlert = updateEvaluationPenalties(newAccAlert, Optional.ofNullable(accAlert), coordinate, distanceFromPrevious, currentAcceleration);
		accAlert = optAccAlert.isPresent() ? optAccAlert.get() : null;

		return currentCoordinate;
	}
	
	public static Optional<GeocodeAddress> getAddress(double latitude, double longitude) {
		try {
			Optional<String> googleKey =  JettyServer.get().getGoogleMapsKey();
			if (googleKey.isPresent()) {
				return InverseGeocodingClient.getAddresses(latitude, longitude, JettyServer.get().getGoogleMapsKey().get());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public double getAccelerationAvg() {
		return accecelerationSum/accCoordinateCount;
	}
		

	private Optional<PenaltyAlert> updateEvaluationPenalties(Optional<PenaltyAlert> newAlert, Optional<PenaltyAlert> currentAlert, Coordinate coordinate, double distanceFromPrevious, double value ) {
		//case 1. The alert is continuous from previous coordinate
		if (currentAlert.isPresent() && newAlert.isPresent()) {
			PenaltyAlert alert = currentAlert.get();
			//PenaltyAlert newAlert = currentAlert.get();
			alert.increaseDistance(distanceFromPrevious);
			alert.setEnd(coordinate.getDateTime());
			alert.setFinalValue(value);
			PenaltySeverity newSeverity = newAlert.get().getSeverity();
			alert.setSeverity(alert.getSeverity().getValue() > newSeverity.getValue() ? alert.getSeverity() : newSeverity);
			return Optional.of(alert);
			//case 2. There's no new previous alert. Close the current one
		} else if (currentAlert.isPresent() && !newAlert.isPresent()){
			PenaltyAlert alert = currentAlert.get();
			alert.increaseDistance(distanceFromPrevious);
			alert.setEnd(coordinate.getDateTime());
			alert.setFinalValue(value);
			return Optional.empty();
			//case 3. There's no previous alert but this coordinate rise one. Create a new alert.
		} else if (newAlert.isPresent()) {
			PenaltyAlert alert = newAlert.get();
			alert.setStart(coordinate.getDateTime());
			alert.setDistance(0);
			alert.setInitialValue(value);
			alert.setEnd(coordinate.getDateTime());
			alerts.add(alert);
			newAlertsCount++;
			return Optional.of(alert);
		}
		return Optional.empty();
	}

	private Optional<PenaltyAlert> evaluatePenalty(PenaltyType type, double index) {
		PenaltyAlert newAlert = null;
		if (index >= PenaltySeverity.MEDIUM.getValue() && index < PenaltySeverity.SEVERE.getValue()) {
			newAlert = new PenaltyAlert(PenaltySeverity.MEDIUM, type);
		}
		if (index >= PenaltySeverity.SEVERE.getValue() && index < PenaltySeverity.VERY_SEVERE.getValue()) {
			newAlert = new PenaltyAlert(PenaltySeverity.SEVERE, type);
		}
		if (index > PenaltySeverity.VERY_SEVERE.getValue()) {
			newAlert = new PenaltyAlert(PenaltySeverity.VERY_SEVERE, type);
		}
		return Optional.ofNullable(newAlert);
	}

	private double avgIndex(List<Double> list) {
		if (list.size() == 0) return 0;
		double sum = 0;
		for (Double value : list) {
			sum += value;
		}
		return sum/list.size();
	}

	private void updateAggressiveIndex() {
		double speedIndex = avgIndex(segmentSpeedIndexes);
		double accIndex = avgIndex(segmentAccelerationIndexes);
		
		if (speedIndex > 0) {
			speedSegmentSize++;
			speedIndexSum += speedIndex;
		}
		if (accIndex > 0) {
			accSegmentSize++;
			accIndexSum += accIndex;
		}
				
		
		double segmentIndex = (speedIndex + accIndex)/2;
		maxAggressiveIndex = segmentIndex > maxAggressiveIndex ? segmentIndex : maxAggressiveIndex;
	}
	
//	public double getAggressiveIndex() {
//		double speedIndex = speedIndexSum/speedSegmentSize;
//		double accIndex = accIndexSum/accSegmentSize;
//		//double decIndex = avgIndex(trajectoryDeccelerationIndexes);
//		//return (speedIndex + accIndex + decIndex)/3;
//		return (speedIndex + accIndex)/2;
//	}
	public double getAggressiveIndex() {
		double speedIndex = getSpeedAgressiveIndex() + getSpeedIndex();
		double accIndex =  getAccIndex() + getAccAggressiveIndex();
		//double accIndex = accIndexSum/accSegmentSize;
		//double decIndex = avgIndex(trajectoryDeccelerationIndexes);
		//return (speedIndex + accIndex + decIndex)/3;
		return (speedIndex + accIndex)/4;
	}
	
//	public double getSpeedIndex() {
//		return speedIndexSum/speedSegmentSize;
//	}
	
	public double getSpeedIndex() {
		return speedDist.getWeightEval2();
	}
	
	public double getSpeedAgressiveIndex() {
		return speedDist.getWeightAgressiveEval2();
	}
	
	public Double getAccAggressiveIndex() {
		// TODO Auto-generated method stub
		return accIndex.get();
	}

	
	public double getAccIndex() {	
		return accIndexSum/accSegmentSize;
	}
	
	
	
	private void clearSegment() {
		segmentDistance.reset();
		segmentSpeedIndexes.clear();
		segmentAccelerationIndexes.clear();
	}

	private Coordinate updateMomentum(final Coordinate previousCoordinate, final Coordinate currentCoordinate) {
		final long coordinateTime = currentCoordinate.getDateTimeInMillis()/1000;
		final long previousCoordinateTime = previousCoordinate.getDateTimeInMillis()/1000;
		final double deltaTSeconds = Math.abs(coordinateTime - previousCoordinateTime);
		final double distance = Math.abs(currentCoordinate.distanceInMeters(previousCoordinate));

		double speed = deltaTSeconds != 0 ?  distance/deltaTSeconds : distance;

		speed = currentCoordinate.getSpeed().isPresent() ? currentCoordinate.getSpeed().get() : speed; 
		double previousSpeed = previousCoordinate.getSpeed().isPresent() ? previousCoordinate.getSpeed().get() : 0; 
		final double deltaV = speed - previousSpeed;
		final double acceleration = deltaTSeconds == 0 ? 0 : deltaV/deltaTSeconds;

		Coordinate coordinate = new Coordinate(currentCoordinate);
		coordinate.setAcceleration(acceleration);
		coordinate.setSpeed(speed);

		return coordinate;
	}

	public TrajectoryEvaluation getCurrentTelemetry() {
		TrajectoryEvaluation telemetry = new TrajectoryEvaluation();
		telemetry.accCount = accecelerationCount;
		telemetry.decCount = decelerationCount;
		telemetry.avgSpeed = new Speed(speedSum/trajectory.size());
		telemetry.coordRate = totalTime == 0 ? 0 : trajectory.size() / totalTime;
		telemetry.maxAcc = new Acceleration(maxAccecelration);
		//telemetry.maxAllowedSpeed = MAX_ALLOWED_SPEED;
		//telemetry.maxSecureAcc =

		telemetry.maxDec = new Acceleration(maxDeceleration);
		telemetry.maxSpeed = new Speed(maxSpeed);
		telemetry.overMaxAllowedSpeedCount = overMaxSpeedCount;
		telemetry.overMaxSecureAccCount = overMaxAccelerationCount;
		telemetry.overMaxSecureDecCount = overMaxDecelerationCount;
		telemetry.trajectoryDistance = new Distance(totalDistance);
		telemetry.trajectoryTime = new Time(totalTime);
		telemetry.vehicleId = trajectory.getUserId();

		return telemetry;
	}

	public Optional<OpenWeatherConditionDTO> getCurrentWeather() {
		return Optional.ofNullable(currentWeather);
	}

	public Trajectory getTrajectory(){
		return trajectory;
	}



	public List<Trajectory> subtrajectoriesByStop(Trajectory trajectory) {
		List<Trajectory> subTrajectories = new LinkedList<>();
		double distance = 0;
		double speedSum = 0;
		long time = 0;
		int count = 0;
		Coordinate previous = null;


		long fiveMinutesMiliSecs = 5 * 60 * 1000;
		double stopSpeedAvg = 1; //3,6 kmh -> 1 ms
		double stopDistance = 300; //300 meters in 300 seconds
		List<Coordinate> stopCoords = new LinkedList<>();
		List<Coordinate> moveCoords = new LinkedList<>();
		List<Coordinate> tmpCoords = new LinkedList<>();

		for(Coordinate coord : trajectory.getCoordinates()) {
			tmpCoords.add(coord);
			if (previous == null) {
				previous = coord;
				continue;
			}
			
			distance += Math.abs(coord.distanceInMeters(previous));
			time += coord.getDateTimeInMillis() - previous.getDateTimeInMillis();
			speedSum += coord.getSpeed().isPresent() ? coord.getSpeed().get() : coord.speedFrom(previous);
			count++;

			//usually 10 coordinates of moving should be provided at max 50 seconds interval
			if (count == 10) {
				boolean stopDetectedByTime = time > fiveMinutesMiliSecs;
				boolean stopDetectedBySpeed = (speedSum/count) < stopSpeedAvg;
				boolean stopDetectedByDistance = time > fiveMinutesMiliSecs && distance < stopDistance;

				if(stopDetectedByTime || stopDetectedBySpeed || stopDetectedByDistance) {
					stopCoords.addAll(tmpCoords);
				} else {
					moveCoords.addAll(tmpCoords);
				}
				tmpCoords.clear();
				distance = 0;
				time = 0;
				speedSum = 0;
				count = 0;
			}
			previous = coord;
		}

		//Prepare a list of subtrajectories
		Trajectory subTrajectory = Trajectory.sub(trajectory);
		previous = null;
		for (Coordinate coord : moveCoords) {
			if (previous == null) {
				previous = coord;
				subTrajectory.add(coord);
				continue;
			}
			long timeDiff = coord.getDateTimeInMillis() - previous.getDateTimeInMillis();
			if (timeDiff > fiveMinutesMiliSecs) {
				subTrajectories.add(subTrajectory);
				subTrajectory = Trajectory.sub(trajectory);
				subTrajectory.add(coord);
			} else {
				subTrajectory.add(coord);
			}
			previous = coord;
		}
		if (!subTrajectory.isEmpty()) {
			subTrajectories.add(subTrajectory);
		}

		return subTrajectories;
	}
	
	public List<Trajectory> subtrajectoriesByTime(Trajectory trajectory, long timeToleranceMilis) {
		Trajectory subtrajectory = new Trajectory();
		List<Trajectory> subtrajectories = new LinkedList<>();
		subtrajectories.add(subtrajectory);

		Coordinate previousCoord = null;
		for (Coordinate currentCoord : trajectory.getCoordinates()) {
			if(previousCoord == null) {
				previousCoord = currentCoord;
				subtrajectory.add(currentCoord);
				continue;
			}

			long difference = currentCoord.getDateTimeInMillis() - previousCoord.getDateTimeInMillis();
			if (difference <= timeToleranceMilis) {
				subtrajectory.add(currentCoord);
			} else {
				subtrajectory = new Trajectory();
				subtrajectory.add(currentCoord);
				subtrajectories.add(subtrajectory);
			}
			previousCoord = currentCoord;
		}
		return subtrajectories;
	}

	public Optional<LocalDateTime> getStartDate() {
		return trajectory.getStart();
	}

	public Optional<LocalDateTime> getEndDate() {
		return trajectory.getEnd();
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public long getTotalTime() {
		return totalTime;
	}

	

	public List<PenaltyAlert> getAlerts() {
		return alerts;
	}

	public int getNewAlerts() {
		return newAlertsCount;
	}

	public void resetNewAlerts() {
		newAlertsCount = 0;
	}

	public void evaluate(Trajectory t) {
		trajectory.setSourceProvider(t.getSourceProvider());
		trajectory.setDeviceId(t.getDeviceId());
		trajectory.setTransportMean(t.getTransportMean());
		trajectory.setTransportType(t.getTransportType());
		trajectory.setUserId(t.getUserId());
		evaluate(t.getCoordinates());
	}

	public Double getMaxAggressiveIndex() {
		return maxAggressiveIndex;
	}

	public void addAll(List<ImportantPlace> places) {
		for (ImportantPlace place : places) {
			importantPlaces.put(place.getName(), place);
		}
	}

	public List<ImportantPlace> getImportantPlaces() {
		return importantPlaces.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}
	
	public DeviceId getDeviceId() {
		return new DeviceId(deviceId);
	}

	public void setDeviceId(DeviceId deviceId) {
		this.deviceId = deviceId.getValue();
	}

	public DriverId getDriverId() {
		return new DriverId(driverId);
	}

	public void setDriverId(DriverId driverId) {
		this.driverId = driverId.getValue();
	}

	public TrajectoryEvaluatorId getId() {
		return new TrajectoryEvaluatorId(_id);
	}

	public long getLatestTimestamp() {
		return latestTimestamp;
	}
	
	public List<String> getStreets() {
		return streets.keySet().stream().collect(Collectors.toList());
	}
	
	public String getMainStreet() {
		double max = 0;
		String mainStreet = "";
		for(Entry<String, Double> entry : streets.entrySet()) {
			if (entry.getValue() > max) {
				mainStreet = entry.getKey();
				max = entry.getValue();
			}
		}

		return mainStreet;
	}
	
	public String getTimeInfo() {
		if (trajectory.isEmpty()) return "-";
		LocalDateTime startTime = trajectory.getStart().get();
		int hour = startTime.getHour();
		if (hour > 7 && hour < 19 ) {
			return "Horário Comercial";
		}

		if (hour > 0 && hour < 5 ) {
			return "Madrugada";
		}

		return "Noite"; 
	}

	public String getTrafficInfo() {
		if (trajectory.isEmpty()) return "-";
		LocalDateTime startTime = trajectory.getStart().get();
		int hour = startTime.getHour();
		if (hour > 7 && hour < 19 ) {
			return "Trânsito Intenso";
		}

		if (hour > 0 && hour < 5 ) {
			return "Trânsito Livre";
		}

		return "Trânsito Tranquilo"; 
	}

	public AccelerationEvaluator getAccEvaluator() {
		return accEvaluator;
	}

	public SpeedDist getSpeedDist() {
		return speedDist;
	}	

}
