package br.udesc.dcc.bdes.analysis;

import br.udesc.dcc.bdes.model.Speed;

public class SpeedLimit {

	
	public static Speed getSpeedByAddress(String address) {
		if (address == null){
			return Speed.fromKmh(40);
		}
		
		if (address.contains("BR-470")){
			return Speed.fromKmh(80);
		}

		if (address.contains("BR-101")){
			return Speed.fromKmh(110);
		}
	
		//Rua ...
		if (address.contains("R. ")){
			return Speed.fromKmh(50);
		}
		
		
		System.out.println("Unmapped " + address + " will be assumed for  40km/h");
		
		return Speed.fromKmh(40);
	}
}
