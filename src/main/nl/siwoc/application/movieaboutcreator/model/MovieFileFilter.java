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
package nl.siwoc.application.movieaboutcreator.model;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import nl.siwoc.application.movieaboutcreator.utils.Properties;

public class MovieFileFilter implements FileFilter {

	private static List<String> movieExtensions = Arrays.asList(Properties.getProperty("movies.extensions").split(",")); 
	
	@Override
	public boolean accept(File file) {
		return file.isDirectory() && file.getName().equals("VIDEO_TS") || movieExtensions.contains(FilenameUtils.getExtension(file.getName()));
	}

}
