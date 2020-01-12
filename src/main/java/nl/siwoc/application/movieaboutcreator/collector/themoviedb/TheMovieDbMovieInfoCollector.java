/*******************************************************************************
 * Copyright (c) 2019-2020 Niek Knijnenburg
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
package nl.siwoc.application.movieaboutcreator.collector.themoviedb;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
import nl.siwoc.application.movieaboutcreator.utils.HttpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheMovieDbMovieInfoCollector implements MovieInfoDetailsCollector,MovieInfoFolderCollector,MovieInfoBackgroundCollector {
	
	protected static final Logger LOG = LoggerFactory.getLogger(TheMovieDbMovieInfoCollector.class);

	final ObjectMapper mapper = new ObjectMapper();
	
	public boolean getDetails(Movie movie) throws Exception {
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

	public byte[] getFolder(Movie movie) throws Exception {
		byte[] image = null;
		String theMovieDbId = getTheMovieDbId(movie);
		if (theMovieDbId != null) {
			MovieDetails movieDetails = getDetailsFromApi(movie);
			if (movieDetails != null && movieDetails.getPoster_path() != null) {
				image = getImageFromURL(Configuration.ImageBaseUrl + "w342" + movieDetails.getPoster_path());
			}
		}
		return image;
	}

	public byte[] getBackground(Movie movie) throws Exception {
		byte[] image = null;
		String theMovieDbId = getTheMovieDbId(movie);
		if (theMovieDbId != null) {
			MovieDetails movieDetails = getDetailsFromApi(movie);
			if (movieDetails != null && movieDetails.getBackdrop_path() != null) {
				image = getImageFromURL(Configuration.ImageBaseUrl + "w1280" + movieDetails.getBackdrop_path());
			}
		}
		return image;
	}
	
	private byte[] getImageFromURL(String imageUrl) throws Exception {
		byte[] image = null;
		if (imageUrl != null) {
			HttpURLConnection conn = null;

			// call image api
			try {
				conn = HttpUtils.getConnection(imageUrl);
				if (conn.getResponseCode() != 200) {
					throw new Exception("Get Image failed : HTTP error code : " + conn.getResponseCode() + " " + conn.getResponseMessage());
				}

				InputStream is = conn.getInputStream();
				image = IOUtils.toByteArray(is);

			} catch (Exception e) {
				throw e;
			} finally {
				conn.disconnect();
			}
		}
		return image;

	}

	private MovieDetails getDetailsFromApi(Movie movie) throws Exception {
		String theMovieDbId = movie.getId(getIdType());
		MovieDetails movieDetails = null;
		HttpURLConnection conn = null;
		
		if (theMovieDbId == null) {
			return null;
		}
		
		// call movie api
		try {
			conn = HttpUtils.getConnection(Configuration.BaseUrl + "movie/" + theMovieDbId + "?api_key=" + Configuration.ApiKey + "&language=en-US&append_to_response=credits");
			if (conn.getResponseCode() != 200) {
				if (conn.getResponseCode() == 404) {
					return movieDetails;
				}
				throw new Exception("Get Details failed : HTTP error code : " + conn.getResponseCode() + " " + conn.getResponseMessage());
			}
			
			movieDetails = mapper.readValue(conn.getInputStream(), MovieDetails.class);
			
		} catch (Exception e) {
			throw e;
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

	public String getTheMovieDbId(Movie movie) throws Exception
	{
		String theMovieDbId = movie.getId(getIdType());
		if (!StringUtils.isNumeric(theMovieDbId))
		{
			theMovieDbId = getMovieId(searchMovie(movie), movie);
			if (theMovieDbId != null) {
				movie.setId(getIdType(), theMovieDbId);
			}
		}
		return theMovieDbId;
	}

	public ArrayList<SearchMovieResult> searchMovie(Movie movie) throws Exception {
		String query = createQuery(movie);
		SearchMovieResponse searchMovieResponse = null;
		ArrayList<SearchMovieResult> results = new ArrayList<SearchMovieResult>();
		HttpURLConnection conn = null;
		
		// call search movie api
		try {
			conn = HttpUtils.getConnection(Configuration.BaseUrl + "search/movie?api_key=" + Configuration.ApiKey + "&include_adult=false&query=" + query);
			if (conn.getResponseCode() != 200) {
				throw new Exception("Search failed : HTTP error code : " + conn.getResponseCode() + " " + conn.getResponseMessage());
			}
			
			searchMovieResponse = mapper.readValue(conn.getInputStream(), SearchMovieResponse.class);
			if (searchMovieResponse.getTotal_pages() > 0) {
				results = searchMovieResponse.getResults();
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}

		return results;
		
	}
	
	private String createQuery(Movie movie) {
		String query = null;
		try {
			query = URLEncoder.encode(movie.getQuery(), "UTF-8");
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
		String movieTitle = movie.getQuery().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
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

	public static void main(String[] args) throws Exception {
		Movie movie; 
		TheMovieDbMovieInfoCollector mic = new TheMovieDbMovieInfoCollector();
		movie = new Movie(new File("Battleship 2012 720p BRRiP XViD AC3-LEGi0N_NL ingebakken.avi"));
		movie = new Movie(new File("Battleship (2012).avi"));
		System.out.println(" getDetails returned: " + mic.getDetails(movie));
		System.out.println(" id " + mic.getTheMovieDbId(movie));
		movie.setQuery("Battleship");
		movie.setYear(2012);
		System.out.println(" getDetails returned: " + mic.getDetails(movie));
		System.out.println(" id " + mic.getTheMovieDbId(movie));
	}

	public String getName() {
		return "TheMovieDb";
	}

	public String getIdType() {
		return "themoviedb";
	}

	public String toString() {
		return getName();
	}
	
}
