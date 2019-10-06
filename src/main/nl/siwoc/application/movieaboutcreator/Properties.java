package nl.siwoc.application.movieaboutcreator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Properties {

	private static java.util.Properties mainProperties = new java.util.Properties();
	
	static {
		 try (FileInputStream is = new FileInputStream("movieaboutcreator.properties")) {
			 mainProperties.load(new InputStreamReader(is, Charset.forName("UTF-8")));
		 } catch (IOException e) {
			System.out.println("Could not load properties file");
		}
	}

	public static String getProperty(String propertyName) {
		return mainProperties.getProperty(propertyName);
	}

}
