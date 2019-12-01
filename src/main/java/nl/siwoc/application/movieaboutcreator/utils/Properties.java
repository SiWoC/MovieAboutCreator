/*******************************************************************************
 * Copyright (c) 2019 Niek Knijnenburg
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
package nl.siwoc.application.movieaboutcreator.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Properties {

	private static final String PROPERTIES_FILENAME = "movieaboutcreator.properties";
	public static final String MOVIES_FOLDERNAME = "movies.foldername";
	public static final String DETAILS_COLLECTOR_NAME = "detailscollector";
	public static final String FOLDER_COLLECTOR_NAME = "foldercollector";
	public static final String BACKGROUND_COLLECTOR_NAME = "backgroundcollector";
	private static final String MAX_PLOT_LENGTH = "max.plot.length";


	private static java.util.Properties mainProperties = new java.util.Properties();

	static {
		try (FileInputStream is = new FileInputStream(PROPERTIES_FILENAME)) {
			mainProperties.load(new InputStreamReader(is, Charset.forName("UTF-8")));
		} catch (IOException e) {
			throw new RuntimeException("Could not load properties file",e);
		}
	}

	public static String getProperty(String propertyName) {
		return mainProperties.getProperty(propertyName);
	}

	public static void setProperty(String propertyName, String value) {
		mainProperties.setProperty(propertyName, value);
	}

	public static void save() {
		try {
			mainProperties.store(new FileOutputStream(PROPERTIES_FILENAME), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static String getMoviesFolderName() {
		return getProperty(MOVIES_FOLDERNAME);
	}

	public static void setMoviesFolderName(String value) {
		setProperty(MOVIES_FOLDERNAME, value);
	}

	public static String getBackgroudCollectorName() {
		return getProperty(BACKGROUND_COLLECTOR_NAME);
	}

	public static void setBackgroundCollectorName(String value) {
		setProperty(BACKGROUND_COLLECTOR_NAME, value);
	}

	public static String getFolderCollectorName() {
		return getProperty(FOLDER_COLLECTOR_NAME);
	}

	public static void setFolderCollectorName(String value) {
		setProperty(FOLDER_COLLECTOR_NAME, value);
	}

	public static String getDetailsCollectorName() {
		return getProperty(DETAILS_COLLECTOR_NAME);
	}

	public static void setDetailsCollectorName(String value) {
		setProperty(DETAILS_COLLECTOR_NAME, value);
	}

	public static int getMaxPlotLength() {
		return Integer.valueOf(getProperty(MAX_PLOT_LENGTH));
	}

}
