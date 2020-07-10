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
package nl.siwoc.application.movieaboutcreator.model;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import nl.siwoc.application.movieaboutcreator.model.fileprops.AudioChannels;
import nl.siwoc.application.movieaboutcreator.model.fileprops.AudioCodec;
import nl.siwoc.application.movieaboutcreator.model.fileprops.Container;
import nl.siwoc.application.movieaboutcreator.model.fileprops.Resolution;
import nl.siwoc.application.movieaboutcreator.model.fileprops.VideoCodec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonRootName(value="movie")
@JsonPropertyOrder({ "title", "year", "rating", "plot", "runtime", "container", "genres"})
public class Movie {
	
	protected static final Logger LOG = LoggerFactory.getLogger(Movie.class);
	
	@JsonIgnore
	private File file;

	@JsonIgnore
	private String name;

	@JsonProperty
	private String title;

	@JsonIgnore
	private String query;

	@JsonProperty
	private int year;

	@JsonIgnore
	private Hashtable<String,String> ids = new Hashtable<>();

	@JsonIgnore
	private float rating;
	
	@JsonProperty("rating")
	private int xmlRating;

	@JsonProperty
	private String plot;

	@JsonIgnore
	private long duration;
	
	@JsonProperty
	private Container container;
	
	@JsonProperty
	private VideoCodec videoCodec;
	
	@JsonProperty
	private Resolution resolution;
	
	@JsonProperty
	private AudioCodec audioCodec;
	
	@JsonProperty
	private AudioChannels audioChannels;
	
    @JacksonXmlElementWrapper(localName = "genres")
    @JacksonXmlProperty(localName = "genre")
	private ArrayList<String> genres = new ArrayList<String>();
	
	@JsonIgnore
	private ArrayList<String> directors = new ArrayList<String>();
	
    @JacksonXmlElementWrapper(localName = "cast")
    @JacksonXmlProperty(localName = "actor")
	private ArrayList<Actor> actors = new ArrayList<Actor>();

	public static class Actor {
		@JsonIgnore
		private String name;
		@JsonIgnore
		private String character;

		public Actor(String _name) {
			setName(_name);
		}
		
		public Actor(String _name, String _character) {
			setName(_name);
			setCharacter(_character);
		}

		public String getName() {
			return name;
		}
		
		public void setName(String _name) {
			this.name = _name;
		}

		public String getCharacter() {
			return character;
		}

		public void setCharacter(String _character) {
			this.character = _character;
		}

		@JsonValue
		public String toString() {
			if (getCharacter() == null || getCharacter().isEmpty()) {
				return getName();
			} else {
				return getName() + " (" + getCharacter() + ")";
			}
		}
	}

	public Movie(File _file) {
		this.setFile(_file);
		if (_file.isDirectory()) {
			setName(_file.getParentFile().getName());
		} else {
			setName(FilenameUtils.getBaseName(_file.getName()));
		}
		// only in Constructor, might get changed separately
		setQuery(getTitle());
	}

	public Movie() {
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public void renameFile(String newName) throws Exception {
		File newFile;
		if (getFile().isDirectory()) {
			// DVD rename parent folder of file
			newFile = new File(getFile().getParentFile().getParentFile(), newName);
			Files.move(getFile().getParentFile().toPath(), newFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
		} else {
			// rename srt files and maybe old.xml which might be overwritten later
			List<File> movies = Arrays.asList(getFile().getParentFile().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					// BaseName is the same, but extension is different 
					// the actual movie we do later because we need that new File
					return FilenameUtils.getBaseName(name).equals(FilenameUtils.getBaseName(getFile().getName()))
							&& !FilenameUtils.getExtension(name).equals(FilenameUtils.getExtension(getFile().getName()));
				}
			}));
			for (File movieFile : movies) {
				LOG.info("Renaming file: " + movieFile.getName());
				Files.move(movieFile.toPath(), new File(movieFile.getParentFile(), newName + "." + FilenameUtils.getExtension(movieFile.getName())).toPath(), StandardCopyOption.ATOMIC_MOVE);
			}
			LOG.info("Renaming file: " + getFile().getName());
			newFile = new File(getFile().getParentFile(), newName + "." + FilenameUtils.getExtension(getFile().getName()));
			Files.move(getFile().toPath(), newFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
		}
		setFile(newFile);
	}

	public String getName() {
		return name;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getRuntime() {
		return String.valueOf(duration/60) + "h " + String.valueOf(duration % 60) + "m";
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public int getXmlRating() {
		return Math.round(rating * 10);
	}

	public void setName(String _name) {
		LOG.trace("Parsing moviename: " + _name);
		this.name = _name;
		setTitleFromName(_name);
		setYearFromName(_name);
		setIdFromName(_name);
	}
	
	private void setTitleFromName(String _name) {
		int lastParenthesis = _name.lastIndexOf('(');
		int lastSquare = _name.lastIndexOf('[');
		int lastBracket = 0;
		if (lastParenthesis > 0) {
			lastBracket = lastParenthesis;
		}
		if (lastSquare > 0) {
			if (lastParenthesis > 0) {
				lastBracket = Math.min(lastParenthesis, lastSquare);
			} else {
				lastBracket = lastSquare;
			}
		}
		
		if (lastBracket > 0) {
			setTitle(_name.substring(0, lastBracket).trim());
		} else {
			setTitle(_name);
		}
	}

	private void setIdFromName(String _name) {
		// TODO parse info like [moviemeter 12345][themoviedb 67890]
		// trying to get the id from the name
		int idStart = _name.lastIndexOf('[');
		if (idStart > 0) {
			int idEnd = _name.lastIndexOf(']');
			String[] idParts = _name.substring(idStart + 1,idEnd).split(" ");
			if (idParts.length == 2) {
				setId(idParts[0], idParts[1]);
			} else if (idParts.length > 2) {
				if ("ID".equalsIgnoreCase(idParts[0])) {
					setId(idParts[1], idParts[2]);
				} else {
					setId(idParts[0], idParts[1]);
				}
			}
		}
	}

	private void setYearFromName(String _name) {
		// parsing names like "Some Movie (NL) (2019)" or "Some Movie (2019) (720p DTS)"
		// nothing I want to do about "Some Movie (1080p DTS) (2019)", just rename your file
		String[] nameParts = _name.split("\\(");
		int year = 0;
		for (int i = 1 ; i < nameParts.length ; i++) {
			if (nameParts[i].length() > 3) {
				try {
					year = Integer.parseInt(nameParts[i].substring(0, 4));
					if (year > 0) {
						setYear(year);
						break;
					}
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public ArrayList<String> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<String> genres) {
		this.genres = genres;
	}

	public ArrayList<String> getDirectors() {
		return directors;
	}

	public void setDirectors(ArrayList<String> _directors) {
		this.directors = _directors;
	}

	public ArrayList<Actor> getActors() {
		return actors;
	}

	public void setActors(ArrayList<Actor> _actors) {
		this.actors = _actors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String _title) {
		this.title = _title;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int _year) {
		this.year = _year;
	}

	public String getId(String idType) {
		return ids.get(idType);
	}

	public void setId(String idType, String id) {
		ids.put(idType.toLowerCase(), id);
	}
	
	public void clearIds() {
		ids.clear();
	}

	public void setContainer(Container _container) {
		this.container = _container;
	}

	public Container getContainer() {
		return this.container;
	}

	public VideoCodec getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(VideoCodec videoCodec) {
		this.videoCodec = videoCodec;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	public AudioCodec getAudioCodec() {
		return audioCodec;
	}

	public void setAudioCodec(AudioCodec audioCodec) {
		this.audioCodec = audioCodec;
	}

	public AudioChannels getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(AudioChannels audioChannels) {
		this.audioChannels = audioChannels;
	}

	public String toString() {
		return getName();
	}
	
	public static void main (String[] args) {
		//File f = new File("O:/Films/Winter's Tale (2014)/Winter's Tale 2014 [ID moviemeter 97163].mkv");
		File f = new File("Mirror Mirror Sneeuwwitje (2012) [ID moviemeter 80552]");
		Movie movie = new Movie(f);
		System.out.println(movie.getTitle());
		System.out.println(movie.getYear());
		System.out.println(movie.ids);
	}

}
