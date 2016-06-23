import java.util.Optional;

import org.jongo.MongoCollection;

import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;
import br.udesc.dcc.bdes.google.GeocodeAddress;
import br.udesc.dcc.bdes.google.InverseGeocodingClient;
import br.udesc.dcc.bdes.google.dto.GeocodeAddressDTO;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.repository.mongo.DriverProfileRepository;
import br.udesc.dcc.bdes.repository.mongo.MongoDBStatic;


public class Main {

	public static void main(String[] args) {
		mongoDB();
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


		Optional<GeocodeAddressDTO> address = InverseGeocodingClient.geAddress(lat, lon, "key");
		if(address.isPresent()) {
			GeocodeAddress geoAddress = new GeocodeAddress(address.get());
			System.out.println(geoAddress.getStreetAddress());

		} else {
			System.err.println("Did not work :(");
		}

	}

}
