import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.jongo.MongoCollection;

import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;
import br.udesc.dcc.bdes.google.geocoding.GeocodeAddress;
import br.udesc.dcc.bdes.google.geocoding.InverseGeocodingClient;
import br.udesc.dcc.bdes.google.geocoding.dto.GeocodeAddressDTO;
import br.udesc.dcc.bdes.google.places.ImportantPlace;
import br.udesc.dcc.bdes.google.places.ImportantPlacesClient;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.repository.mongo.DriverProfileRepository;
import br.udesc.dcc.bdes.repository.mongo.MongoDBStatic;
import br.udesc.dcc.bdes.server.JettyServer;


public class Main {

	public static void main(String[] args) {
		//mongoDB();
		//placesEval();
		renameCSVFiles();
	}
	
	
	
	public static void mongoDB() {
		MongoCollection driverProfile = MongoDBStatic.getCollection("driver-profile");
		driverProfile.drop();
		System.out.println("Initial count: " + driverProfile.count());
		
		DriverProfileRepository repository = DriverProfileRepository.get();
		DriverProfile profile = new DriverProfile(new DriverId("123"), new DeviceId("abc"));
		repository.save(profile);
		System.out.println("Save count: " + driverProfile.count());
		
		Optional<DriverProfile> profile2 = repository.load(new DriverId("123"));
		System.out.println("Device Id " + profile2.get().getDeviceId().getValue());
		
		DriverProfile profile3 = new DriverProfile(new DriverId("123"), new DeviceId("abc"));
		repository.save(profile3);
		System.out.println("Update count: " + driverProfile.count());

		
		DriverProfile profile4 = new DriverProfile(new DriverId("111"), new DeviceId("aaa"));
		repository.save(profile4);
		System.out.println("NewSave count: " + driverProfile.count());
		
		
		repository.remove(new DriverId("123"));
		System.out.println(driverProfile.count());
		System.out.println("Cleanup count: " + driverProfile.count());
		
	}
	
	public static void accEval() {
		AccelerationEvaluator accEval = new AccelerationEvaluator();
		double acc = -12.3;
		System.out.println("Evaluating acc index");
		while (acc < 8) {
			double index = accEval.evaluate(acc);
			
			System.out.println("Acc " +  String.format("%.2f ", acc) + " - index "  + String.format("%.2f ", index));
			acc += 0.5;
		}
	}

	public static void geocodeEval() {
		//Ibirama
		//double lat = -27.0586472;
		//double lon = -49.52839998;

		//Apiuna
		//double lat = -27.05239619;
		//double lon = -49.39019279;
		double lat = -26.94951323;
		double lon = -49.36797419;
		Properties properties = JettyServer.loadServerProperties();
		Optional<GeocodeAddressDTO> address = InverseGeocodingClient.getAddress(lat, lon,  properties.getProperty("google-key"));
		if(address.isPresent()) {
			GeocodeAddress geoAddress = new GeocodeAddress(address.get());
			System.out.println(geoAddress.getStreetAddress());
		} else {
			System.err.println("Did not work :(");
		}

	}
	
	public static void placesEval() {
		//double lat = -33.8670522;
		//double lon = 151.1957362;
		
		double lat = -26.9059024;
		double lon = -49.2466274;
		Properties properties = JettyServer.loadServerProperties();
		int radiusMeters = 1000;
		
		List<ImportantPlace> places = ImportantPlacesClient.getPlaces(lat, lon, radiusMeters, properties.getProperty("google-key"));
		for(ImportantPlace place : places) {
			System.out.println(place.getName() + ". Type  " + place.getType() + ". Address: " + place.getAddress());
		}
		//	Gson gson = new Gson();
		//	System.out.println(gson.toJson(places));
	}
	
	public static void renameCSVFiles() {
		String baseDir = "C:\\Users\\marciogj\\SkyDrive\\GPS_DATA\\AnaliseMestrado";
		renameTimestampToReadbleDate(new File(baseDir));
	}
	
	public static void renameTimestampToReadbleDate(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				renameTimestampToReadbleDate(f);
			}
			return;
		}
		String path = file.getName();
		String[] parts = path.split("_");
		if (parts.length != 3) {
			System.out.println("Filename did not match expected pattern");
			return;
		}
		long timestamp = Long.parseLong(parts[1]);
		LocalDateTime ldt = toLocalDateTime(timestamp);
		String newPath = file.getParentFile().getAbsolutePath() + System.getProperty("file.separator") + parts[0] + "_" + parts[1] + "_" + toFilename(ldt) + "_" + parts[2];
		file.renameTo(new File(newPath));
	}
	
	public static LocalDateTime toLocalDateTime(long value){
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault());
	}
	
	public static String toFilename(LocalDateTime ldt) {
		return ldt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss"));
	}

}
