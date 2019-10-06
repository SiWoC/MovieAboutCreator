package nl.siwoc.application.movieaboutcreator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

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

@JsonRootName(value="movie")
@JsonPropertyOrder({ "title", "year", "rating", "plot", "runtime", "container", "genres"})
public class Movie {
	
	@JsonIgnore
	private File file;

	@JsonIgnore
	private String name;

	@JsonProperty
	private String title;

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
			createDVD(_file);
		} else {
			setName(FilenameUtils.getBaseName(_file.getName()));
			try {
				setContainer(Container.valueOf(FilenameUtils.getExtension(_file.getName()).toUpperCase()));
			} catch (IllegalArgumentException e) {}
		}
	}

	private void createDVD(File movie) {
		setName(movie.getParentFile().getName());
		setContainer(Container.DVD);
		setVideoCodec(VideoCodec.MPEG);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public long getDuration() {
		return duration;
	}

	public String getRuntime() {
		return String.valueOf(duration/60) + "h " + String.valueOf(duration % 60) + "m";
	}

	public float getRating() {
		return rating;
	}

	public int getXmlRating() {
		return Math.round(rating * 10);
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public void setName(String _name) {
		this.name = _name;
		int lastBracket = _name.lastIndexOf('(');
		if (lastBracket > 0) {
			setTitle(_name.substring(0, lastBracket).trim());
			setYear(Integer.parseInt(_name.substring(lastBracket + 1,lastBracket + 5)));
		} else {
			setTitle(_name);
		}
	}
	
	public String getPlot() {
		return plot;
	}

	public ArrayList<String> getGenres() {
		return genres;
	}

	public ArrayList<String> getDirectors() {
		return directors;
	}

	public ArrayList<Actor> getActors() {
		return actors;
	}

	public void setActors(ArrayList<Actor> _actors) {
		this.actors = _actors;
	}

	public void setDirectors(ArrayList<String> _directors) {
		this.directors = _directors;
	}

	public void setGenres(ArrayList<String> genres) {
		this.genres = genres;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String _title) {
		this.title = _title;
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
		ids.put(idType,id);
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
		File f = new File("O:\\downloads\\Aladdin (1992)\\Aladdin (1992).iso");
		Movie movie = new Movie(f);
		System.out.println(movie.getYear());
		System.out.println(movie.getContainer().getLogo());
	}

}