package nl.siwoc.application.movieaboutcreator.collector;

import nl.siwoc.application.movieaboutcreator.model.Movie;

public interface MovieInfoFolderCollector {
	
	public byte[] getFolder(Movie movie);

	public String getName();

}
