package nl.siwoc.application.movieaboutcreator.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
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
import nl.siwoc.application.movieaboutcreator.Properties;
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
import nl.siwoc.mediainfo.FileProber;
import nl.siwoc.mediainfo.MediaInfo;
import nl.siwoc.application.movieaboutcreator.model.MovieFileFilter;
import nl.siwoc.application.movieaboutcreator.model.XmlDetails;

public class MovieService {

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
	private File folderImageFile = new File("./generated/folder.jpg");
	private MovieInfoBackgroundCollector backgroundCollector;
	private static FileProber fp;

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
			if (bc.toString().equals(Properties.getProperty("backgroundcollector"))) {
				setBackgroundCollector(bc);
			}
		}
		for (MovieInfoFolderCollector fc : folderCollectors) {
			if (fc.toString().equals(Properties.getProperty("foldercollector"))) {
				setFolderCollector(fc);
			}
		}
		for (MovieInfoDetailsCollector dc : detailsCollectors) {
			if (dc.toString().equals(Properties.getProperty("detailscollector"))) {
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
			writeSetValues(movie);
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
				movie.setResolution(Resolution.valueOf("RESH" + mediaInfo.getFrameHeight()));
			} catch (IllegalArgumentException eh) {
				try {
					movie.setResolution(Resolution.valueOf("RESW" + mediaInfo.getFrameWidth()));
				} catch (IllegalArgumentException ew) {
					controller.setStatusLine("Unknown Resolution: [RES" + mediaInfo.getFrameWidth() + "x" + mediaInfo.getFrameHeight() + "]");
				}
			}
			try {
				movie.setAudioCodec(AudioCodec.valueOf(mediaInfo.getAudioCodec().toUpperCase()));
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
	
	private boolean writeSetValues(Movie movie) {
		if (detailsCollector != null) {
			detailsCollector.getDetails(movie);
			File setValuesFile = new File("./generated/setvalues.js");
			if (!setValuesFile.exists() || setValuesFile.canWrite()) {
				try {
					// start
					FileUtils.writeStringToFile(setValuesFile, SET_VALUES_BASE, "UTF-8", false);
					// values
					appendSetTextLine(setValuesFile, "title", movie.getTitle());
					appendSetTextLine(setValuesFile, "year", movie.getYear());
					appendSetTextLine(setValuesFile, "duration", getDurationString(movie.getDuration()));
					appendSetTextLine(setValuesFile, "rating", getRatingString(movie.getRating()));
					appendSetTextLine(setValuesFile, "plot", getPlotString(movie.getPlot()));
					appendSetTextLine(setValuesFile, "directors", getDirectorsString(movie.getDirectors()));
					appendSetTextLine(setValuesFile, "genres", getGenresString(movie.getGenres()));
					appendSetTextLine(setValuesFile, "actors", getActorsString(movie.getActors()));
					appendSetTextLine(setValuesFile, "fileprops", getFilePropsString(movie));
					// end
					FileUtils.writeStringToFile(setValuesFile, "}\r\n", "UTF-8", true);
					controller.setStatusLine("Writen setvalues script for movie: " + movie.toString());
					return true;
				} catch (IOException e) {
					controller.setStatusLine("IOException while writing setvalues script for movie: " + movie.toString());
					throw new RuntimeException(e);
				}
			}
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
		if (plot.length() > 485) {
			return StringEscapeUtils.escapeHtml4(plot.substring(0, 480) + "...");
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
		if (folderImageFile.exists() && folderImageFile.canWrite()) {
			folderImageFile.delete();
		}
		if (folderCollector != null) {
			byte[] folderImage = folderCollector.getFolder(movie);
			if (folderImage != null) {
				if (!folderImageFile.exists() || folderImageFile.canWrite()) {
					try {
						FileUtils.writeByteArrayToFile(folderImageFile, folderImage);
						controller.setStatusLine("Writen folder image for movie: " + movie.toString());
						return true;
					} catch (IOException e) {
						controller.setStatusLine("IOException while writing folder image for movie: " + movie.toString());
						throw new RuntimeException(e);
					}
				}
			}
		}
		controller.setStatusLine("Unable to write folder image for movie: " + movie.toString());
		return false;
	}

	public boolean writeBackgroundImage(Movie movie) {
		File backgroundImageFile = new File("./generated/background.jpg");
		if (backgroundImageFile.exists() && backgroundImageFile.canWrite()) {
			backgroundImageFile.delete();
		}
		if (backgroundCollector != null) {
			byte[] backgroundImage = backgroundCollector.getBackground(movie);
			if (backgroundImage != null) {
				if (!backgroundImageFile.exists() || backgroundImageFile.canWrite()) {
					try {
						FileUtils.writeByteArrayToFile(backgroundImageFile, backgroundImage);
						controller.setStatusLine("Writen background image for movie: " + movie.toString());
						return true;
					} catch (IOException e) {
						controller.setStatusLine("IOException while writing background image for movie: " + movie.toString());
						throw new RuntimeException(e);
					}
				}
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
			controller.setStatusLine("Unable to write xml-file for movie: " + movie.toString());
		}
	}
	
	public void generateAndCopy(Movie movie) {
		writeXmlFile(movie);
		if (folderImageFile != null) {
			try {
				Files.copy(folderImageFile.toPath(), new File(movie.getFile().getParentFile(), "folder.jpg").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				controller.setStatusLine("Unable to write folder-file for movie: " + movie.toString());
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
		File file = new File("O:/downloads/Shazam (2019)/Shazam (2019).avi");
		Movie movie = new Movie(file);
		model.getMovieInfo(movie);
		model.writeXmlFile(movie);
		model.writeSetValues(movie);
		System.out.println(movie.getYear());
	}
}
