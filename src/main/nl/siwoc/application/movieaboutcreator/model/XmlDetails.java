package nl.siwoc.application.movieaboutcreator.model;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value="details")
public class XmlDetails {
	public Movie movie;
	
	public XmlDetails (Movie _movie) {
		movie = _movie;
	}

}
