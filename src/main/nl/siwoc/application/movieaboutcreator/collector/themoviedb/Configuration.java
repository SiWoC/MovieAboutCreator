package nl.siwoc.application.movieaboutcreator.collector.themoviedb;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.siwoc.application.movieaboutcreator.Properties;
import nl.siwoc.application.movieaboutcreator.collector.themoviedb.model.GetConfigurationResponse;

public class Configuration {

	public static final String IdType = "themoviedb";
	public static String ApiKey = Properties.getProperty("themoviedb.apikey");
	public static String BaseUrl = Properties.getProperty("themoviedb.rest.baseurl");
	public static String ImageBaseUrl = null;
	
	final static ObjectMapper mapper = new ObjectMapper();
	
	static {

		GetConfigurationResponse getConfigurationResponse = null;
		HttpURLConnection conn = null;
		
		// call themoviedb configuration api
		try {
			URL url = new URL(Configuration.BaseUrl + "configuration?api_key=" + Configuration.ApiKey);
			System.out.println("HTTP configuration call: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			getConfigurationResponse = mapper.readValue(conn.getInputStream(), GetConfigurationResponse.class);
			ImageBaseUrl = getConfigurationResponse.getImages().getSecure_base_url();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

	}

	
	public static void main(String[] args) {
		System.out.println(Configuration.ImageBaseUrl);
	}

}
