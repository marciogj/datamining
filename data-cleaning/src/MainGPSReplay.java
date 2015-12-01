import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.io.UdescCSVFileReader;


public class MainGPSReplay {
	
	public static void main(String[] args) {
		System.out.println("Simulating GPS coordinates from recorded files...");
		String dirPath = "C:\\Users\\marcio.jasinski\\OneDrive\\GPS_DATA\\udesc\\";
		String file = "Dados_Coletados_20130130_13490087.csv";
		Trajectory trajectory = UdescCSVFileReader.read(dirPath+"\\"+file);
		for (Coordinate coordinate : trajectory.getCoordinates()) {
			System.out.println(coordinate);
		}
		
	}
	

}
