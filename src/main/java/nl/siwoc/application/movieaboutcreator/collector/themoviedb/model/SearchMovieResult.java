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
package nl.siwoc.application.movieaboutcreator.collector.themoviedb.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchMovieResult {
	private float popularity;
	private long vote_count;
	private boolean video;
	private String poster_path;
	private long id;
	private boolean adult;
	private String backdrop_path;
	private String original_language;
	private String original_title;
	private ArrayList<Long> genre_ids = new ArrayList<Long> ();
	private String title;
	private float vote_average;
	private String overview;
	private String release_date;
	private int year;

	// Getter Methods 

	public float getPopularity() {
		return popularity;
	}

	public long getVote_count() {
		return vote_count;
	}

	public boolean getVideo() {
		return video;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public long getId() {
		return id;
	}

	public boolean getAdult() {
		return adult;
	}

	public String getBackdrop_path() {
		return backdrop_path;
	}

	public String getOriginal_language() {
		return original_language;
	}

	public String getOriginal_title() {
		return original_title;
	}

	public String getTitle() {
		return title;
	}

	public float getVote_average() {
		return vote_average;
	}

	public String getOverview() {
		return overview;
	}

	public String getRelease_date() {
		return release_date;
	}

	// Setter Methods 

	public ArrayList<Long> getGenre_ids() {
		return genre_ids;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setGenre_ids(ArrayList<Long> genre_ids) {
		this.genre_ids = genre_ids;
	}

	public void setPopularity(float popularity) {
		this.popularity = popularity;
	}

	public void setVote_count(long vote_count) {
		this.vote_count = vote_count;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setAdult(boolean adult) {
		this.adult = adult;
	}

	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}

	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}

	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVote_average(float vote_average) {
		this.vote_average = vote_average;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public void setRelease_date(String _release_date) {
		this.release_date = _release_date;
		if (_release_date != null) {
			this.setYear(Integer.parseInt(_release_date.substring(0, 4)));
		}
	}

	public String toString() {
		return getTitle() + " (" + getYear() + ") [" + getOriginal_title() + "]";
	}
}