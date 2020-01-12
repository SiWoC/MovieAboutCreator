/*******************************************************************************
 * Copyright (c) 2019-2020 Niek Knijnenburg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package nl.siwoc.application.movieaboutcreator.collector.themoviedb;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.siwoc.application.movieaboutcreator.collector.themoviedb.model.GetConfigurationResponse;
import nl.siwoc.application.movieaboutcreator.utils.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	
	protected static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

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
			LOG.trace("HTTP configuration call: " + url);
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
