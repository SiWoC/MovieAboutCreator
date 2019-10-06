package nl.siwoc.application.movieaboutcreator.collector.moviemeter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.siwoc.application.movieaboutcreator.collector.MovieInfoFolderCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
import nl.siwoc.application.movieaboutcreator.collector.moviemeter.model.MovieDetails;
import nl.siwoc.application.movieaboutcreator.collector.moviemeter.model.SearchMovieResult;
import nl.siwoc.application.movieaboutcreator.model.Movie;

public class MoviemeterMovieInfoCollector implements MovieInfoDetailsCollector,MovieInfoFolderCollector {

	final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public boolean getDetails(Movie movie) {
		String moviemeterId = getMoviemeterId(movie);
		if (moviemeterId != null) {
			MovieDetails movieDetails = getDetailsFromApi(movie);
			if (movieDetails != null) {
				setDetails(movie,movieDetails);
				return true;
			}
		}
		return false;
	}

	@Override
	public byte[] getFolder(Movie movie) {
		String moviemeterId = getMoviemeterId(movie);
		byte[] image = null;
		if (moviemeterId != null) {
			MovieDetails movieDetails = getDetailsFromApi(movie);
			if (movieDetails != null && movieDetails.getPosters() != null && movieDetails.getPosters().getRegular() != null) {
				String folderURL = movieDetails.getPosters().getRegular();
				System.out.println("folder: " + folderURL);
				byte[] result = null;
				HttpURLConnection conn = null;
				
				// call moviemeter api
				try {
					URL url = new URL(folderURL);
					System.out.println("HTTP folderImage call: " + url);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() != 200) {
						if (conn.getResponseCode() == 404) {
							return result;
						}
						throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
					}
					
					InputStream is = conn.getInputStream();
					image = IOUtils.toByteArray(is);
					
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}
		return image;
	}

	private MovieDetails getDetailsFromApi(Movie movie) {
		String moviemeterId = movie.getId(Configuration.IdType);
		MovieDetails movieDetails = null;
		HttpURLConnection conn = null;
		
		if (moviemeterId == null) {
			return null;
		}
		
		// call moviemeter api
		try {
			URL url = new URL(Configuration.BaseUrl + moviemeterId + "?api_key=" + Configuration.ApiKey);
			System.out.println("HTTP details call: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				if (conn.getResponseCode() == 404) {
					return movieDetails;
				}
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			movieDetails = mapper.readValue(conn.getInputStream(), MovieDetails.class);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return movieDetails;
	}

	private void setDetails(Movie movie, MovieDetails movieDetails) {
		movie.setDuration(movieDetails.getDuration());
		movie.setRating(movieDetails.getAverage() * 2);
		movie.setPlot(movieDetails.getPlot());
		movie.setGenres(movieDetails.getGenres());
		movie.setDirectors(movieDetails.getDirectors());
		ArrayList<Movie.Actor> actors = new ArrayList<Movie.Actor>();
		for (MovieDetails.Actor actor : movieDetails.getActors()) {
			actors.add(new Movie.Actor(actor.getName()));
		}
		movie.setActors(actors);
	}


	public String getMoviemeterId(Movie movie)
	{
		String moviemeterId = movie.getId(Configuration.IdType);
		if (!StringUtils.isNumeric(moviemeterId))
		{
			moviemeterId = getMovieId(searchMovie(movie), movie);
			if (moviemeterId != null) {
				movie.setId(Configuration.IdType, moviemeterId);
			}
		}
		return moviemeterId;
	}

	public ArrayList<SearchMovieResult> searchMovie(Movie movie) {
		String query = createQuery(movie);
		ArrayList<SearchMovieResult> result = new ArrayList<SearchMovieResult>();
		HttpURLConnection conn = null;
		
		// call moviemeter api
		try {
			URL url = new URL(Configuration.BaseUrl + "?q=" + query + "&api_key=" + Configuration.ApiKey);
			System.out.println("HTTP search call: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				if (conn.getResponseCode() == 404) {
					return result;
				}
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			result = new ArrayList<SearchMovieResult>(Arrays.asList(mapper.readValue(conn.getInputStream(), SearchMovieResult[].class)));
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return result;
		
	}
	
	private String createQuery(Movie movie) {
		try {
			return URLEncoder.encode(movie.getTitle(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never occurs, UTF-8 is hardcoded
			throw new RuntimeException(e);
		}
	}

	public String getMovieId(ArrayList<SearchMovieResult> searchMovieResultSet, Movie movie)
	{
		String moviemeterId = null;
		if (searchMovieResultSet != null)
		{
			// only 1 film found, assume that name is correct and year doesn't matter
			if (searchMovieResultSet.size() == 1) {
				return String.valueOf(searchMovieResultSet.get(0).getId());
			}
			
			// multiple films found, filter on year
			int yearToFind = movie.getYear();
			ArrayList<SearchMovieResult> haveYearResults = new ArrayList<SearchMovieResult>();
			for (SearchMovieResult result : searchMovieResultSet) {
				if (result.getYear() == yearToFind) {
					haveYearResults.add(result);
				}
			}
			if (haveYearResults.size() == 1) {
				// 1 result with requested year
				return String.valueOf(haveYearResults.get(0).getId());
			} else {
				// multipe films with searchterm in the name and requested year (like Wolf (2013) or Brave (2012))
				// find name equality
				for (SearchMovieResult hasYear : haveYearResults) {
					if (matchTitle(hasYear,movie)) {
						return String.valueOf(hasYear.getId());
					}
				}
			}
		}
		return moviemeterId;
	}

	private boolean matchTitle(SearchMovieResult searchMovieResult, Movie movie) {
		String searchTitle = searchMovieResult.getTitle().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
		String movieTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
		if (searchTitle.equals(movieTitle)) {
			return true;
		}
		if (searchMovieResult.getAlternative_title() != null) {
			searchTitle = searchMovieResult.getAlternative_title().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
			if (searchTitle.equals(movieTitle)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		Movie movie; 
		//MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		//mmr.setDataSource("O:\\Series\\Red Dwarf\\S01\\Red Dwarf S01E01 - The End.avi");
		movie = new Movie(new File("Soof (2013).avi"));
		MoviemeterMovieInfoCollector mic = new MoviemeterMovieInfoCollector();
		System.out.println(" id " + mic.getMoviemeterId(movie));
		mic.getFolder(movie);
		movie = new Movie(new File("Brave (2012).avi"));
		mic.getFolder(movie);
		System.out.println(" id " + mic.getMoviemeterId(movie));
		movie = new Movie(new File("Wolf (2013).avi"));
		System.out.println(" id " + mic.getMoviemeterId(movie));
		mic.getFolder(movie);
		mic.getDetails(movie);
		System.out.println(" plot: " + movie.getPlot());
	}

	@Override
	public String getName() {
		return "Moviemeter";
	}
	
	public String toString() {
		return getName();
	}

}
