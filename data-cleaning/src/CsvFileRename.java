import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class CsvFileRename {

	public static void main(String[] args) {
		String baseDir = "C:\\Users\\marciogj\\SkyDrive\\GPS_DATA\\AnaliseMestrado2";	
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
			System.out.println("Filename did not match expected pattern: " + path);
			return;
		}
		long timestamp = Long.parseLong(parts[2].replace(".csv", ""));
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
