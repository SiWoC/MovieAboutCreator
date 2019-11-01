/*******************************************************************************
 * Copyright (c) 2019 Niek Knijnenburg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package nl.siwoc.application.movieaboutcreator.collector.moviemeter;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.siwoc.application.movieaboutcreator.collector.MovieInfoFolderCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
import nl.siwoc.application.movieaboutcreator.collector.moviemeter.model.MovieDetails;
import nl.siwoc.application.movieaboutcreator.collector.moviemeter.model.SearchMovieResult;
import nl.siwoc.application.movieaboutcreator.model.Movie;

public class MoviemeterMovieInfoCollector implements MovieInfoDetailsCollector,MovieInfoFolderCollector {

	private static final Logger LOGGER = Logger.getLogger(MoviemeterMovieInfoCollector.class.getName());
	final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public boolean getDetails(Movie movie) throws Exception {
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
	public byte[] getFolder(Movie movie) throws Exception {
		String moviemeterId = getMoviemeterId(movie);
		byte[] image = null;
		if (moviemeterId != null) {
			MovieDetails movieDetails = getDetailsFromApi(movie);
			if (movieDetails != null && movieDetails.getPosters() != null && movieDetails.getPosters().getRegular() != null) {
				String folderURL = movieDetails.getPosters().getRegular();
				LOGGER.finer("folder: " + folderURL);
				byte[] result = null;
				HttpURLConnection conn = null;
				
				// call moviemeter api
				try {
					URL url = new URL(folderURL);
					LOGGER.finer("HTTP folderImage call: " + url);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() != 200) {
						if (conn.getResponseCode() == 404) {
							return result;
						}
						throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
					}
					
					InputStream is = conn.getInputStream();
					image = IOUtils.toByteArray(is);
					
				} catch (Exception e) {
					throw e;
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}
		return image;
	}

	private MovieDetails getDetailsFromApi(Movie movie) throws Exception {
		String moviemeterId = movie.getId(Configuration.IdType);
		MovieDetails movieDetails = null;
		HttpURLConnection conn = null;
		
		if (moviemeterId == null) {
			return null;
		}
		
		// call moviemeter api
		try {
			URL url = new URL(Configuration.BaseUrl + moviemeterId + "?api_key=" + Configuration.ApiKey);
			LOGGER.finer("HTTP details call: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				if (conn.getResponseCode() == 404) {
					return movieDetails;
				}
				throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			movieDetails = mapper.readValue(conn.getInputStream(), MovieDetails.class);
			
		} catch (Exception e) {
			throw e;
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


	public String getMoviemeterId(Movie movie) throws Exception
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

	public ArrayList<SearchMovieResult> searchMovie(Movie movie) throws Exception {
		String query = createQuery(movie);
		ArrayList<SearchMovieResult> result = new ArrayList<SearchMovieResult>();
		HttpURLConnection conn = null;
		
		// call moviemeter api
		try {
			URL url = new URL(Configuration.BaseUrl + "?q=" + query + "&api_key=" + Configuration.ApiKey);
			LOGGER.finer("HTTP search call: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				if (conn.getResponseCode() == 404) {
					return result;
				}
				throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			result = new ArrayList<SearchMovieResult>(Arrays.asList(mapper.readValue(conn.getInputStream(), SearchMovieResult[].class)));
			
		} catch (Exception e) {
			throw e;
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

	public static void main(String[] args) throws Exception {
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
