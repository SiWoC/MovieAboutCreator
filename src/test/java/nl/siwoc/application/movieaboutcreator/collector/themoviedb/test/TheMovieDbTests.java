package nl.siwoc.application.movieaboutcreator.collector.themoviedb.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import nl.siwoc.application.movieaboutcreator.collector.themoviedb.TheMovieDbMovieInfoCollector;
import nl.siwoc.application.movieaboutcreator.model.Movie;

class TheMovieDbTests {

	Movie movie; 
	TheMovieDbMovieInfoCollector mic = new TheMovieDbMovieInfoCollector();
	
	@Test
	void testGetTheMovieDbId() throws Exception {
		movie = new Movie(new File("Soof (2013).avi"));
		assertEquals("230217", mic.getTheMovieDbId(movie), "Wrong id found");
		movie = new Movie(new File("Brave (2012).avi"));
		assertEquals("62177", mic.getTheMovieDbId(movie), "Wrong id found");
		movie = new Movie(new File("Wolf (2013).avi"));
		assertEquals("191104", mic.getTheMovieDbId(movie), "Wrong id found");
		movie = new Movie(new File("Spider-man 3 (2007).avi"));
		assertEquals("559", mic.getTheMovieDbId(movie), "Wrong id found");
		movie = new Movie(new File("Spiderman 3 (2007).avi"));
		assertEquals("559", mic.getTheMovieDbId(movie), "Wrong id found");
	}
	
}
