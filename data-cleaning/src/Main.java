import java.util.Optional;

import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;
import br.udesc.dcc.bdes.google.GeocodeAddress;
import br.udesc.dcc.bdes.google.InverseGeocodingClient;
import br.udesc.dcc.bdes.google.dto.GeocodeAddressDTO;


public class Main {

	public static void main(String[] args) {
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
