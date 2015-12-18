package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;

public class InfoWriter {

	public static void write(String filename, String... content) {
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			for(String text : content) {
				writer.println(text);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
