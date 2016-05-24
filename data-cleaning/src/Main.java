import java.util.Optional;

import br.udesc.dcc.bdes.google.GeocodeAddress;
import br.udesc.dcc.bdes.google.GeocodeAddressDTO;
import br.udesc.dcc.bdes.google.InverseGeocodingClient;


public class Main {

	public static void main(String[] args) {
		
		//Ibirama
		//double lat = -27.0586472;
		//double lon = -49.52839998;
		
		//Apiuna
		//double lat = -27.05239619;
		//double lon = -49.39019279;
		
		double lat = -26.94951323;
		double lon = -49.36797419;
		 	
		
		Optional<GeocodeAddressDTO> address = InverseGeocodingClient.address(lat, lon, "AIzaSyDQD30bsMMQZ_QgkAa2qlNXROTEToa1wZY");
		if(address.isPresent()) {
			GeocodeAddress geoAddress = new GeocodeAddress(address.get());
			System.out.println(geoAddress.getStreetAddress());
			
		} else {
			System.err.println("Did not work :(");
		}
		

	}

}
