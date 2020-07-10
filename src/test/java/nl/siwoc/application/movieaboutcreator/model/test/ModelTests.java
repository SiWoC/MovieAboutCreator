package nl.siwoc.application.movieaboutcreator.model.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import nl.siwoc.application.movieaboutcreator.model.Movie;

class ModelTests {

	Movie movie; 
	
	@Test
	void testSetYearFromName() throws Exception {
		movie = new Movie(new File("Soof (2013).avi"));
		assertEquals(2013, movie.getYear(), "Wrong year found");
		movie = new Movie(new File("Brave (2012).avi"));
		assertEquals(2012, movie.getYear(), "Wrong year found");
		movie = new Movie(new File("Wolf (2013).avi"));
		assertEquals(2013, movie.getYear(), "Wrong year found");
		movie = new Movie(new File("Spider-man 3 (2007).avi"));
		assertEquals(2007, movie.getYear(), "Wrong year found");
		movie = new Movie(new File("Spiderman 3 (2007).avi"));
		assertEquals(2007, movie.getYear(), "Wrong year found");
		movie = new Movie(new File("1917 (2019).avi"));
		assertEquals(2019, movie.getYear(), "Wrong year found");
	}
	
}
