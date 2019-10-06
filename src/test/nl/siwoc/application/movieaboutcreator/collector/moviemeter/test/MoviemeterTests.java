package nl.siwoc.application.movieaboutcreator.collector.moviemeter.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import nl.siwoc.application.movieaboutcreator.collector.moviemeter.MoviemeterMovieInfoCollector;
import nl.siwoc.application.movieaboutcreator.model.Movie;

class MoviemeterTests {

	Movie movie; 
	MoviemeterMovieInfoCollector mic = new MoviemeterMovieInfoCollector();
	
	@Test
	void testGetMoviemeterId() {
		movie = new Movie(new File("Soof (2013).avi"));
		assertEquals("90303", mic.getMoviemeterId(movie), "Wrong id found");
		movie = new Movie(new File("Brave (2012).avi"));
		assertEquals("77133", mic.getMoviemeterId(movie), "Wrong id found");
		movie = new Movie(new File("Wolf (2013).avi"));
		assertEquals("95242", mic.getMoviemeterId(movie), "Wrong id found");
		movie = new Movie(new File("Spider-man 3 (2007).avi"));
		assertEquals("33473", mic.getMoviemeterId(movie), "Wrong id found");
		movie = new Movie(new File("Spider-man 3 (2017).avi"));
		assertEquals("33473", mic.getMoviemeterId(movie), "Wrong id found");
	}
	
}
