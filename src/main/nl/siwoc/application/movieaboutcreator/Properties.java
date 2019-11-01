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
package nl.siwoc.application.movieaboutcreator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class Properties {

	private static final Logger LOGGER = Logger.getLogger(Properties.class.getName());
	private static java.util.Properties mainProperties = new java.util.Properties();
	
	static {
		 try (FileInputStream is = new FileInputStream("movieaboutcreator.properties")) {
			 mainProperties.load(new InputStreamReader(is, Charset.forName("UTF-8")));
		 } catch (IOException e) {
			LOGGER.severe("Could not load properties file");
		}
	}

	public static String getProperty(String propertyName) {
		return mainProperties.getProperty(propertyName);
	}

}
