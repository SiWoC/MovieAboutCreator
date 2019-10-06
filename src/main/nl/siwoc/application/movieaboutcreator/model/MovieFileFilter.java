package nl.siwoc.application.movieaboutcreator.model;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import nl.siwoc.application.movieaboutcreator.Properties;

public class MovieFileFilter implements FileFilter {

	private static List<String> movieExtensions = Arrays.asList(Properties.getProperty("movies.extensions").split(",")); 
	
	@Override
	public boolean accept(File file) {
		return file.isDirectory() && file.getName().equals("VIDEO_TS") || movieExtensions.contains(FilenameUtils.getExtension(file.getName()));
	}

}
