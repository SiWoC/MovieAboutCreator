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
package nl.siwoc.application.movieaboutcreator.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoFolderCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoBackgroundCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
import nl.siwoc.application.movieaboutcreator.controller.MainController;
import nl.siwoc.application.movieaboutcreator.model.Movie;
import nl.siwoc.application.movieaboutcreator.model.Movie.Actor;
import nl.siwoc.application.movieaboutcreator.model.fileprops.AudioChannels;
import nl.siwoc.application.movieaboutcreator.model.fileprops.AudioCodec;
import nl.siwoc.application.movieaboutcreator.model.fileprops.Container;
import nl.siwoc.application.movieaboutcreator.model.fileprops.FileProp;
import nl.siwoc.application.movieaboutcreator.model.fileprops.Resolution;
import nl.siwoc.application.movieaboutcreator.model.fileprops.VideoCodec;
import nl.siwoc.application.movieaboutcreator.utils.ImageUtils;
import nl.siwoc.application.movieaboutcreator.utils.Properties;
import nl.siwoc.mediainfo.FileProber;
import nl.siwoc.mediainfo.MediaInfo;
import nl.siwoc.application.movieaboutcreator.model.MovieFileFilter;
import nl.siwoc.application.movieaboutcreator.model.XmlDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieService {
	
	protected static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

	private Comparator<Movie> comparator = Comparator.comparing(Movie::toString); 
	
	private String moviesFolderName = "";
	private ObservableList<Movie> observableMoviesList = FXCollections.observableArrayList();
	
	private static ServiceLoader<MovieInfoDetailsCollector> detailsCollectorLoader = 
			ServiceLoader.load(MovieInfoDetailsCollector.class);
	private static ServiceLoader<MovieInfoFolderCollector> folderCollectorLoader = 
			ServiceLoader.load(MovieInfoFolderCollector.class);
	private static ServiceLoader<MovieInfoBackgroundCollector> backgroundCollectorLoader = 
			ServiceLoader.load(MovieInfoBackgroundCollector.class);
	
	private static ObservableList<MovieInfoFolderCollector> folderCollectors = FXCollections.observableArrayList();
	private static ObservableList<MovieInfoDetailsCollector> detailsCollectors = FXCollections.observableArrayList();
	private static ObservableList<MovieInfoBackgroundCollector> backgroundCollectors = FXCollections.observableArrayList();
	
	private MainController controller;
	private MovieInfoDetailsCollector detailsCollector;
	private MovieInfoFolderCollector folderCollector;
	private MovieInfoBackgroundCollector backgroundCollector;
	private static FileProber fp;

	// start/base of the setValues.js file
	private static final String SET_VALUES_BASE = "function setText(id,newvalue) {\r\n" + 
			"	var s= document.getElementById(id);\r\n" + 
			"	if(typeof(s) != 'undefined' && s != null){\r\n" +
			"		s.innerHTML = newvalue;\r\n" + 
			"	}\r\n" + 
			"}\r\n" + 
			"window.onload=function() {\r\n";
	
	static {
		for (MovieInfoFolderCollector fc : folderCollectorLoader) {
			folderCollectors.add(fc);
		}
		for (MovieInfoDetailsCollector dc : detailsCollectorLoader) {
			detailsCollectors.add(dc);
		}
		for (MovieInfoBackgroundCollector bc : backgroundCollectorLoader) {
			backgroundCollectors.add(bc);
		}
	}
	
	public static ObservableList<MovieInfoBackgroundCollector> getBackgroundCollectors() {
		return backgroundCollectors;
	}
	
	public static ObservableList<MovieInfoFolderCollector> getFolderCollectors() {
		return folderCollectors;
	}
	
	public static ObservableList<MovieInfoDetailsCollector> getDetailsCollectors() {
		return detailsCollectors;
	}
	
	public MovieService() {
		for (MovieInfoBackgroundCollector bc : backgroundCollectors) {
			if (bc.toString().equals(Properties.getBackgroudCollectorName())) {
				setBackgroundCollector(bc);
			}
		}
		for (MovieInfoFolderCollector fc : folderCollectors) {
			if (fc.toString().equals(Properties.getFolderCollectorName())) {
				setFolderCollector(fc);
			}
		}
		for (MovieInfoDetailsCollector dc : detailsCollectors) {
			if (dc.toString().equals(Properties.getDetailsCollectorName())) {
				setDetailsCollector(dc);
			}
		}
	}
	
	public MovieInfoBackgroundCollector getBackgroundCollector() {
		return backgroundCollector;
	}
	
	public void setBackgroundCollector(MovieInfoBackgroundCollector bc) {
		this.backgroundCollector = bc;
	}

	public MovieInfoFolderCollector getFolderCollector() {
		return folderCollector;
	}
	
	public void setFolderCollector(MovieInfoFolderCollector fc) {
		this.folderCollector = fc;
	}

	public MovieInfoDetailsCollector getDetailsCollector() {
		return detailsCollector;
	}
	
	public void setDetailsCollector(MovieInfoDetailsCollector dc) {
		this.detailsCollector = dc;
	}

	public String getMoviesFolderName() {
		return moviesFolderName;
	}

	public void setMoviesFolderName(String _moviesFolderName) {
		this.moviesFolderName = _moviesFolderName;
	}

	public void refreshMovies() {
		observableMoviesList.clear();
		File moviesFolder = new File(moviesFolderName);
		if (moviesFolder.isDirectory()) {
			File[] movieFolders = moviesFolder.listFiles(File::isDirectory);
			for (File movieFolder : movieFolders) {
				boolean processFolder = true;
				if ("true".equals(Properties.getProperty("use.mjbignore"))) {
					File mjbignore = new File(movieFolder, ".mjbignore");
					if (mjbignore.exists()) {
						processFolder = false;
					}
				}
				if (processFolder) {
					List<File> movies = Arrays.asList(movieFolder.listFiles(new MovieFileFilter()));
					for (File movie : movies) {
						observableMoviesList.add(new Movie(movie));
					}
				}
			}
			FXCollections.sort(observableMoviesList, comparator);
		} else {
			controller.setStatusLine("[" + moviesFolderName + "] is not a folder");
		}
		controller.setStatusLine("Movies refreshed");
	}
	
	public ObservableList<Movie> getMovies() {
		if (observableMoviesList.isEmpty()) {
			refreshMovies();
		}
		return observableMoviesList;
	}

	public void addMovie(Movie movie) {
		observableMoviesList.add(movie);
		
	}

	public void setController(MainController _mainController) {
		this.controller = _mainController;
	}
	
	public void getMovieInfo(Movie movie) {
		if (movie != null) {
			writeBackgroundImage(movie);
			writeFolderImage(movie);
			getFileProperties(movie);
			writeSetValues(movie, true);
		}
	}
	
	private void getFileProperties(Movie movie) {
		MediaInfo mediaInfo;
		try {
			fp = new FileProber();
			mediaInfo = fp.getMediaInfo(movie.getFile().getAbsolutePath());
			try {
				movie.setContainer(Container.valueOf(mediaInfo.getContainer().toUpperCase()));
			} catch (IllegalArgumentException e) {
				controller.setStatusLine("Unknown Container: [" + mediaInfo.getContainer().toUpperCase() + "]");
			}
			try {
				movie.setVideoCodec(VideoCodec.valueOf(mediaInfo.getVideoCodec().toUpperCase()));
			} catch (IllegalArgumentException e) {
				controller.setStatusLine("Unknown VideoCodec: [" + mediaInfo.getVideoCodec().toUpperCase() + "]");
			}
			try {
				movie.setResolution(Resolution.getResolution(mediaInfo.getFrameWidth(), mediaInfo.getFrameHeight()));
			} catch (IllegalArgumentException ew) {
				controller.setStatusLine("Unknown Resolution: [RES" + mediaInfo.getFrameWidth() + "x" + mediaInfo.getFrameHeight() + "]");
			}
			try {
				movie.setAudioCodec(AudioCodec.valueOf(mediaInfo.getAudioCodec().toUpperCase().trim()));
			} catch (IllegalArgumentException e) {
				controller.setStatusLine("Unknown AudioCodec: [" + mediaInfo.getAudioCodec().toUpperCase() + "]");
			}
			try {
				movie.setAudioChannels(AudioChannels.valueOf("CHANNELS" + mediaInfo.getAudioChannels()));
			} catch (IllegalArgumentException e) {
				controller.setStatusLine("Unknown AudioChannels: [CHANNELS" + mediaInfo.getAudioChannels() + "]");
			}
		} catch (Exception e) {
			controller.setStatusLine(e.getLocalizedMessage());
		}
		
	}
	
	public boolean writeSetValues(Movie movie, boolean getDetails) {
		try {
			if (getDetails && detailsCollector != null) {
				detailsCollector.getDetails(movie);
			}
			if (!controller.getSetValuesFile().exists() || controller.getSetValuesFile().canWrite()) {
				// start
				FileUtils.writeStringToFile(controller.getSetValuesFile(), SET_VALUES_BASE, "UTF-8", false);
				// values
				appendSetTextLine(controller.getSetValuesFile(), "title", movie.getTitle());
				appendSetTextLine(controller.getSetValuesFile(), "year", movie.getYear());
				appendSetTextLine(controller.getSetValuesFile(), "duration", getDurationString(movie.getDuration()));
				appendSetTextLine(controller.getSetValuesFile(), "rating", getRatingString(movie.getRating()));
				appendSetTextLine(controller.getSetValuesFile(), "plot", getPlotString(movie.getPlot()));
				appendSetTextLine(controller.getSetValuesFile(), "directors", getDirectorsString(movie.getDirectors()));
				appendSetTextLine(controller.getSetValuesFile(), "genres", getGenresString(movie.getGenres()));
				appendSetTextLine(controller.getSetValuesFile(), "actors", getActorsString(movie.getActors()));
				appendSetTextLine(controller.getSetValuesFile(), "fileprops", getFilePropsString(movie));
				// end
				FileUtils.writeStringToFile(controller.getSetValuesFile(), "}\r\n", "UTF-8", true);
				controller.setStatusLine("Writen setvalues script for movie: " + movie.toString());
				return true;
			}
		} catch (Exception e) {
			controller.setStatusLine("Exception while writing setvalues script for movie: " + movie.toString(), e);
		}
		controller.setStatusLine("Unable to write setvalues script for movie: " + movie.toString());
		return false;
		
	}
	
	private static String getDurationString(long duration) {
		return String.valueOf(duration/60) + "h " + String.valueOf(duration % 60) + "m";
	}
	
	private static String getRatingString(float rating) {
		return "<img src=\\\"../pictures/rating_" + Math.round(rating) * 10 + ".png\\\"/> (" + rating + "/10)";
	}
	
	private String getPlotString(String plot) {
		if (plot == null) return "";
		int maxPlotLength = Properties.getMaxPlotLength();
		if (plot.length() > maxPlotLength) {
			return StringEscapeUtils.escapeHtml4(plot.substring(0, maxPlotLength - 5) + "...");
		} else {
			return StringEscapeUtils.escapeHtml4(plot);
		}
	}
	
	private String getDirectorsString(ArrayList<String> directors) {
		if (directors.size() < 1) {
			return "";
		} else if (directors.size() == 1) {
			return StringEscapeUtils.escapeHtml4(directors.get(0));
		} else if (directors.size() == 2) {
			return StringEscapeUtils.escapeHtml4(directors.get(0)) + "<BR>" + StringEscapeUtils.escapeHtml4(directors.get(1));
		} else {
			return StringEscapeUtils.escapeHtml4(directors.get(0)) + "<BR>and others";
		}
	}

	private String getGenresString(ArrayList<String> genres) {
		StringBuilder genreString = new StringBuilder();
		int i = 0;
		for (String genre : genres) {
			i++;
			if (i == 1)	{
				genreString.append(StringEscapeUtils.escapeHtml4(genre));
			}
			if (i == 2) {
				genreString.append(" / ").append(StringEscapeUtils.escapeHtml4(genre)).append("<BR>");
				i = 0;
			}
		}
		return genreString.toString();
	}
	
	private String getActorsString(ArrayList<Actor> actors) {
		StringBuilder actorString = new StringBuilder();
		for (Actor actor : actors) {
			actorString.append(StringEscapeUtils.escapeHtml4(actor.getName()));
			if (actor.getCharacter() != null && !actor.getCharacter().isEmpty()) {
				actorString.append(" (").append(StringEscapeUtils.escapeHtml4(actor.getCharacter())).append(")");
			}
			actorString.append("<BR>");
		}
		return actorString.toString();
		
	}
	
	private static String getFilePropsString(Movie movie) {
		StringBuilder filepropsString = new StringBuilder();
		appendFileProp(filepropsString, movie.getContainer());
		appendFileProp(filepropsString, movie.getVideoCodec());
		appendFileProp(filepropsString, movie.getResolution());
		appendFileProp(filepropsString, movie.getAudioCodec());
		appendFileProp(filepropsString, movie.getAudioChannels());
		return filepropsString.toString();
	}
	
	private static void appendFileProp(StringBuilder filepropsString, FileProp fileProp) {
		if (fileProp != null) {
			filepropsString.append("<img src=\\\"../pictures/" + fileProp.getLogo() + "\\\"/>");
		}
	}

	private void appendSetTextLine(File setValuesFile, String field, Object value) throws IOException {
		FileUtils.writeStringToFile(setValuesFile, getSetTextLine(field, value), "UTF-8", true);
	}
	
	private String getSetTextLine(String field, Object value) {
		return "	setText(\"" + field + "\", \"" + value + "\");\r\n";
	}

	public boolean writeFolderImage(Movie movie) {
		if (controller.getFolderImageFile().exists() && controller.getFolderImageFile().canWrite()) {
			controller.getFolderImageFile().delete();
		}
		if (folderCollector != null) {
			try {
				byte[] folderImage = folderCollector.getFolder(movie);
				if (folderImage != null) {
					if (!controller.getFolderImageFile().exists() || controller.getFolderImageFile().canWrite()) {
						ImageUtils.writeByteArrayToJpgFile(folderImage, controller.getFolderImageFile());
						controller.setStatusLine("Writen folder image for movie: " + movie.toString());
						return true;
					}
				}
			} catch (Exception e) {
				controller.setStatusLine("Exception while writing folder image for movie: " + movie.toString(), e);
			}
		}
		controller.setStatusLine("Unable to write folder image for movie: " + movie.toString());
		return false;
	}

	public boolean writeBackgroundImage(Movie movie) {
		if (controller.getBackgroundImageFile().exists() && controller.getBackgroundImageFile().canWrite()) {
			controller.getBackgroundImageFile().delete();
		}
		if (backgroundCollector != null) {
			try {
				byte[] backgroundImage = backgroundCollector.getBackground(movie);
				if (backgroundImage != null) {
					if (!controller.getBackgroundImageFile().exists() || controller.getBackgroundImageFile().canWrite()) {
						ImageUtils.writeByteArrayToJpgFile(backgroundImage, controller.getBackgroundImageFile());
						controller.setStatusLine("Writen background image for movie: " + movie.toString());
						return true;
					}
				}
			} catch (Exception e) {
				controller.setStatusLine("Exception while writing background image for movie: " + movie.toString(), e);
			}
		}
		controller.setStatusLine("Unable to write background image for movie: " + movie.toString());
		return false;
	}
	
	public void writeXmlFile(Movie movie) {
		File xmlFile = new File(movie.getFile().getParentFile(), movie.getName() + ".xml");
		if (xmlFile.exists() && xmlFile.canWrite()) {
			xmlFile.delete();
		}
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
		xmlMapper.configure(Feature.WRITE_XML_DECLARATION, true );
		try {
			XmlDetails details = new XmlDetails (movie);
			xmlMapper.writeValue(xmlFile, details);
		} catch (IOException e) {
			controller.setStatusLine("Unable to write xml-file for movie: " + movie.toString(), e);
		}
	}
	
	public void generateAndCopy(Movie movie) {
		writeXmlFile(movie);
		LOG.info("Copying folderImageFile with length: " + controller.getFolderImageFile().length());
		if (controller.getFolderImageFile().length() > 0) {
			try {
				Files.copy(controller.getFolderImageFile().toPath(), new File(movie.getFile().getParentFile(), "folder.jpg").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				controller.setStatusLine("Unable to write folder-file for movie: " + movie.toString(), e);
			}
		}
		LOG.info("Copying aboutImageFile with length: " + controller.getAboutImageFile().length());
		if (controller.getAboutImageFile().length() > 0) {
			try {
				// We leave this a png with jpg extension
				// Mede8er can read the png just fine and quality is better than the jpg
				Files.copy(controller.getAboutImageFile().toPath(), new File(movie.getFile().getParentFile(), "about.jpg").toPath(), StandardCopyOption.REPLACE_EXISTING);
				//ImageUtils.convertToJpg(controller.getAboutImageFile(), new File(movie.getFile().getParentFile(), "about.jpg"));
			} catch (Exception e) {
				controller.setStatusLine("Unable to write about-file for movie: " + movie.toString(), e);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(getDurationString(133));
		System.out.println(getRatingString(6.49F));
		MovieService model = new MovieService();
		model.setController(new MainController());
		//model.setDetailsCollector("Moviemeter");
		//model.setFolderCollector("Moviemeter");
		//model.setBackgroundCollector("TheMovieDb");
		model.setMoviesFolderName("O:/downloads");
		model.refreshMovies();
		File file = new File("O:\\Kinder films\\Monsters Versus Aliens - Cloning Around (2014)\\Monsters Versus Aliens - Cloning Around (2014) [ID imdb tt2782214].avi");
		Movie movie = new Movie(file);
		model.getMovieInfo(movie);
		model.writeXmlFile(movie);
		model.writeSetValues(movie, true);
		System.out.println(getFilePropsString(movie));
	}
}
