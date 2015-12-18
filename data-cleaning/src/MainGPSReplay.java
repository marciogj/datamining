import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.SeniorCSVFileReader;


public class MainGPSReplay {
	
	public static void main(String[] args) {
		System.out.println("Simulating GPS coordinates from recorded files...");
		
		//String dirPath = "C:\\Users\\marcio.jasinski\\OneDrive\\GPS_DATA\\udesc\\marcio\\";
		//String file = "Dados_Coletados_20150825_181920711.csv";
		//Trajectory trajectory = UdescCSVFileReader.read(dirPath+"\\"+file);
		
		String dirPath = "C:\\Users\\marcio.jasinski\\OneDrive\\GPS_DATA\\gps-tracker-service\\";
		String file = "marcio.jasinski_1447100515293_1.csv";
		Trajectory trajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);
		for (Coordinate coordinate : trajectory.getCoordinates()) {
			System.out.println(coordinate);
		}
		
	}
	

}
