package nl.siwoc.application.movieaboutcreator.collector.themoviedb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.siwoc.application.movieaboutcreator.collector.MovieInfoBackgroundCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoFolderCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
import nl.siwoc.application.movieaboutcreator.collector.themoviedb.model.MovieDetails;
import nl.siwoc.application.movieaboutcreator.collector.themoviedb.model.MovieDetails.CastMember;
import nl.siwoc.application.movieaboutcreator.collector.themoviedb.model.MovieDetails.Genre;
import nl.siwoc.application.movieaboutcreator.collector.themoviedb.model.SearchMovieResponse;
import nl.siwoc.application.movieaboutcreator.collector.themoviedb.model.SearchMovieResult;
import nl.siwoc.application.movieaboutcreator.model.Movie;
import nl.siwoc.application.movieaboutcreator.model.Movie.Actor;

public class TheMovieDbMovieInfoCollector implements MovieInfoDetailsCollector,MovieInfoFolderCollector,MovieInfoBackgroundCollector {

	final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public boolean getDetails(Movie movie) {
		String theMovieDbId = getTheMovieDbId(movie);
		if (theMovieDbId != null) {
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
		byte[] image = null;
		String theMovieDbId = getTheMovieDbId(movie);
		if (theMovieDbId != null) {
			MovieDetails movieDetails = getDetailsFromApi(movie);
			if (movieDetails != null && movieDetails.getPoster_path() != null) {
				image = getImage(Configuration.ImageBaseUrl + "w500" + movieDetails.getPoster_path());
			}
		}
		return image;
	}

	@Override
	public byte[] getBackground(Movie movie) {
		byte[] image = null;
		String theMovieDbId = getTheMovieDbId(movie);
		if (theMovieDbId != null) {
			MovieDetails movieDetails = getDetailsFromApi(movie);
			if (movieDetails != null && movieDetails.getBackdrop_path() != null) {
				image = getImage(Configuration.ImageBaseUrl + "w1280" + movieDetails.getBackdrop_path());
			}
		}
		return image;
	}
	
	private byte[] getImage(String imageUrl) {
		byte[] image = null;
		if (imageUrl != null) {
			HttpURLConnection conn = null;

			// call image api
			try {
				URL url = new URL(imageUrl);
				System.out.println("HTTP imageUrl call: " + url);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				InputStream is = conn.getInputStream();
				image = IOUtils.toByteArray(is);

			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				conn.disconnect();
			}
		}
		return image;

	}

	private MovieDetails getDetailsFromApi(Movie movie) {
		String theMovieDbId = movie.getId(Configuration.IdType);
		MovieDetails movieDetails = null;
		HttpURLConnection conn = null;
		
		if (theMovieDbId == null) {
			return null;
		}
		
		// call movie api
		try {
			URL url = new URL(Configuration.BaseUrl + "movie/" + theMovieDbId + "?api_key=" + Configuration.ApiKey + "&language=en-US&append_to_response=credits");
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
			conn.disconnect();
		}

		return movieDetails;
	}

	private void setDetails(Movie movie, MovieDetails movieDetails) {
		movie.setDuration(movieDetails.getRuntime());
		movie.setRating(movieDetails.getVote_average());
		movie.setPlot(movieDetails.getOverview());
		movie.setDirectors(movieDetails.getDirectors());
		ArrayList<String> genres = new ArrayList<String>();
		for (Genre genre : movieDetails.getGenres()) {
			genres.add(genre.getName());
		}
		movie.setGenres(genres);
		ArrayList<Actor> actors = new ArrayList<Actor>();
		for (CastMember castMember : movieDetails.getCredits().getCast()) {
			actors.add(new Actor(castMember.getName(), castMember.getCharacter()));
		}
		movie.setActors(actors);
	}

	public String getTheMovieDbId(Movie movie)
	{
		String theMovieDbId = movie.getId(Configuration.IdType);
		if (!StringUtils.isNumeric(theMovieDbId))
		{
			theMovieDbId = getMovieId(searchMovie(movie), movie);
			if (theMovieDbId != null) {
				movie.setId(Configuration.IdType, theMovieDbId);
			}
		}
		return theMovieDbId;
	}

	public ArrayList<SearchMovieResult> searchMovie(Movie movie) {
		String query = createQuery(movie);
		SearchMovieResponse searchMovieResponse = null;
		ArrayList<SearchMovieResult> results = new ArrayList<SearchMovieResult>();
		HttpURLConnection conn = null;
		
		// call search movie api
		try {
			URL url = new URL(Configuration.BaseUrl + "search/movie?api_key=" + Configuration.ApiKey + "&include_adult=false&query=" + query);
			System.out.println("HTTP search call: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			searchMovieResponse = mapper.readValue(conn.getInputStream(), SearchMovieResponse.class);
			if (searchMovieResponse.getTotal_pages() > 0) {
				results = searchMovieResponse.getResults();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			conn.disconnect();
		}

		return results;
		
	}
	
	private String createQuery(Movie movie) {
		String query = null;
		try {
			query = URLEncoder.encode(movie.getTitle(), "UTF-8");
			if (movie.getYear() > 0) {
				query = query + "&year=" + movie.getYear();
			}
		} catch (UnsupportedEncodingException e) {
			// never occurs, UTF-8 is hardcoded
			throw new RuntimeException(e);
		}
		return query;
	}

	public String getMovieId(ArrayList<SearchMovieResult> searchMovieResultSet, Movie movie)
	{
		String theMovieDbId = null;
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
				// multipe films with searchterm in the name and requested year (like Spider-Man 3 (2007) or Brave (2012))
				// find name equality
				for (SearchMovieResult hasYear : haveYearResults) {
					if (matchTitle(hasYear,movie)) {
						return String.valueOf(hasYear.getId());
					}
				}
			}
		}
		return theMovieDbId;
	}
	
	private boolean matchTitle(SearchMovieResult searchMovieResult, Movie movie) {
		String searchTitle = searchMovieResult.getTitle().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
		String movieTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
		if (searchTitle.equals(movieTitle)) {
			return true;
		}
		if (searchMovieResult.getOriginal_title() != null) {
			searchTitle = searchMovieResult.getOriginal_title().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
			if (searchTitle.equals(movieTitle)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		Movie movie; 
		movie = new Movie(new File("Spiderman 3 (2007).avi"));
		TheMovieDbMovieInfoCollector mic = new TheMovieDbMovieInfoCollector();
		System.out.println(" id " + mic.getTheMovieDbId(movie));
		mic.getFolder(movie);
		movie = new Movie(new File("I Am Mother (2019).avi"));
		mic.getFolder(movie);
		System.out.println(" id " + mic.getTheMovieDbId(movie));
		movie = new Movie(new File("Hotel Transylvania 3  Summer Vacation (2018).iso"));
		System.out.println(" id " + mic.getTheMovieDbId(movie));
		mic.getBackground(movie);
		mic.getFolder(movie);
		mic.getDetails(movie);
		System.out.println(" plot: " + movie.getPlot());
	}

	@Override
	public String getName() {
		return "TheMovieDb";
	}

	public String toString() {
		return getName();
	}
	
}
