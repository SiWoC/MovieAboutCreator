package nl.siwoc.application.movieaboutcreator.collector;

import nl.siwoc.application.movieaboutcreator.model.Movie;

public interface MovieInfoBackgroundCollector {
	
	public byte[] getBackground(Movie movie);

	public String getName();

}