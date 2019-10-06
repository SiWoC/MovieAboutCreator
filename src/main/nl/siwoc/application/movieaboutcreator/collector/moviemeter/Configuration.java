package nl.siwoc.application.movieaboutcreator.collector.moviemeter;

import nl.siwoc.application.movieaboutcreator.Properties;

public class Configuration {

	public static final String IdType = "moviemeter";
	public static String ApiKey = Properties.getProperty("moviemeter.apikey");
	public static String BaseUrl = Properties.getProperty("moviemeter.rest.baseurl");
	
}
