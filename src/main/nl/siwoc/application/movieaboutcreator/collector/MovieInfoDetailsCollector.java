package nl.siwoc.application.movieaboutcreator.collector;

import nl.siwoc.application.movieaboutcreator.model.Movie;

public interface MovieInfoDetailsCollector {
	
	public boolean getDetails(Movie movie) throws Exception;

	public String getName();
	
}
