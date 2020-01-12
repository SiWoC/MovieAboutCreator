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
package nl.siwoc.application.movieaboutcreator.collector.themoviedb.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetails {
	private boolean adult;
	private String backdrop_path;
	private Belongs_to_collection belongsToCollection;
	private float budget;
	private ArrayList<Genre> genres = new ArrayList<Genre>();
	private String homepage;
	private long id;
	private String imdb_id;
	private String original_language;
	private String original_title;
	private String overview;
	private float popularity;
	private String poster_path;
	private ArrayList<Company> production_companies = new ArrayList<Company>();
	private ArrayList<Country> production_countries = new ArrayList<Country>();
	private String release_date;
	private int year;
	private float revenue;
	private int runtime;
	private ArrayList<Language> spoken_languages = new ArrayList<Language>();
	private String status;
	private String tagline;
	private String title;
	private boolean video;
	private float vote_average;
	private long vote_count;
	private Credits credits;
	private ArrayList<String> directors = new ArrayList<String>();

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Belongs_to_collection {
		private long id;
		private String name;
		private String poster_path;
		private String backdrop_path;

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getPoster_path() {
			return poster_path;
		}

		public String getBackdrop_path() {
			return backdrop_path;
		}

		public void setId(long id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setPoster_path(String poster_path) {
			this.poster_path = poster_path;
		}

		public void setBackdrop_path(String backdrop_path) {
			this.backdrop_path = backdrop_path;
		}

		public String toString() {
			return getName();
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Genre {
		private long id;
		private String name;

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setId(long id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			return getName();
		}

	}	

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Company {
		private long id;
		private String logo_path;
		private String name;
		private String origin_country;

		public long getId() {
			return id;
		}

		public String getLogo_path() {
			return logo_path;
		}

		public String getName() {
			return name;
		}

		public String getOrigin_country() {
			return origin_country;
		}

		public void setId(long id) {
			this.id = id;
		}

		public void setLogo_path(String logo_path) {
			this.logo_path = logo_path;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setOrigin_country(String origin_country) {
			this.origin_country = origin_country;
		}

		public String toString() {
			return getName();
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Country {
		private String iso_3166_1;
		private String name;

		public String getIso_3166_1() {
			return iso_3166_1;
		}

		public String getName() {
			return name;
		}

		public void setIso_3166_1(String iso_3166_1) {
			this.iso_3166_1 = iso_3166_1;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			return getName() + " (" + getIso_3166_1() + ")";
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Language {
		private String iso_639_1;
		private String name;

		public String getIso_639_1() {
			return iso_639_1;
		}

		public String getName() {
			return name;
		}

		public void setIso_639_1(String iso_639_1) {
			this.iso_639_1 = iso_639_1;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			return getName() + " (" + getIso_639_1() + ")";
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Credits {
		private ArrayList<CastMember> cast = new ArrayList<CastMember>();
		private ArrayList<CrewMember> crew = new ArrayList<CrewMember>();

		public ArrayList<CastMember> getCast() {
			return cast;
		}

		public void setCast(ArrayList<CastMember> cast) {
			this.cast = cast;
		}

		public ArrayList<CrewMember> getCrew() {
			return crew;
		}

		public void setCrew(ArrayList<CrewMember> crew) {
			this.crew = crew;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CrewMember {
		private String credit_id;
		private String department;
		private int gender;
		private long id;
		private String job;
		private String name;
		private String profile_path;


		public String getCredit_id() {
			return credit_id;
		}

		public String getDepartment() {
			return department;
		}

		public int getGender() {
			return gender;
		}

		public long getId() {
			return id;
		}

		public String getJob() {
			return job;
		}

		public String getName() {
			return name;
		}

		public String getProfile_path() {
			return profile_path;
		}

		public void setCredit_id(String credit_id) {
			this.credit_id = credit_id;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public void setGender(int gender) {
			this.gender = gender;
		}

		public void setId(long id) {
			this.id = id;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setProfile_path(String profile_path) {
			this.profile_path = profile_path;
		}
	
		public String toString() {
			return getName() + " (" + getJob() + ")";
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CastMember {
		private long cast_id;
		private String character;
		private String credit_id;
		private int gender;
		private long id;
		private String name;
		private int order;
		private String profile_path;


		public long getCast_id() {
			return cast_id;
		}

		public String getCharacter() {
			return character;
		}

		public String getCredit_id() {
			return credit_id;
		}

		public int getGender() {
			return gender;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getOrder() {
			return order;
		}

		public String getProfile_path() {
			return profile_path;
		}

		public void setCast_id(long cast_id) {
			this.cast_id = cast_id;
		}

		public void setCharacter(String character) {
			this.character = character;
		}

		public void setCredit_id(String credit_id) {
			this.credit_id = credit_id;
		}

		public void setGender(int gender) {
			this.gender = gender;
		}

		public void setId(long id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		public void setProfile_path(String profile_path) {
			this.profile_path = profile_path;
		}

		public String toString() {
			return getName() + " (" + getCharacter() + ")";
		}
	}

	public boolean getAdult() {
		return adult;
	}

	public String getBackdrop_path() {
		return backdrop_path;
	}

	public Belongs_to_collection getBelongs_to_collection() {
		return belongsToCollection;
	}

	public float getBudget() {
		return budget;
	}

	public String getHomepage() {
		return homepage;
	}

	public long getId() {
		return id;
	}

	public String getImdb_id() {
		return imdb_id;
	}

	public String getOriginal_language() {
		return original_language;
	}

	public String getOriginal_title() {
		return original_title;
	}

	public String getOverview() {
		return overview;
	}

	public float getPopularity() {
		return popularity;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public String getRelease_date() {
		return release_date;
	}

	public float getRevenue() {
		return revenue;
	}

	public int getRuntime() {
		return runtime;
	}

	public String getStatus() {
		return status;
	}

	public String getTagline() {
		return tagline;
	}

	public String getTitle() {
		return title;
	}

	public boolean getVideo() {
		return video;
	}

	public float getVote_average() {
		return vote_average;
	}

	public long getVote_count() {
		return vote_count;
	}

	public ArrayList<Genre> getGenres() {
		return genres;
	}

	public ArrayList<Company> getProduction_companies() {
		return production_companies;
	}

	public ArrayList<Country> getProduction_countries() {
		return production_countries;
	}

	public ArrayList<Language> getSpoken_languages() {
		return spoken_languages;
	}

	public Credits getCredits() {
		return credits;
	}

	public void setCredits(Credits credits) {
		this.credits = credits;
	}

	public void setSpoken_languages(ArrayList<Language> spoken_languages) {
		this.spoken_languages = spoken_languages;
	}

	public void setProduction_countries(ArrayList<Country> production_countries) {
		this.production_countries = production_countries;
	}

	public void setProduction_companies(ArrayList<Company> production_companies) {
		this.production_companies = production_companies;
	}

	public void setGenres(ArrayList<Genre> genres) {
		this.genres = genres;
	}

	public void setAdult(boolean adult) {
		this.adult = adult;
	}

	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}

	public void setBelongs_to_collection(Belongs_to_collection belongs_to_collectionObject) {
		this.belongsToCollection = belongs_to_collectionObject;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setImdb_id(String imdb_id) {
		this.imdb_id = imdb_id;
	}

	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}

	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public void setPopularity(float popularity) {
		this.popularity = popularity;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public void setRelease_date(String _release_date) {
		this.release_date = _release_date;
		if (_release_date != null) {
			this.setYear(Integer.parseInt(_release_date.substring(0, 4)));
		}
	}

	public void setRevenue(float revenue) {
		this.revenue = revenue;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}

	public void setVote_average(float vote_average) {
		this.vote_average = vote_average;
	}

	public void setVote_count(long vote_count) {
		this.vote_count = vote_count;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String toString() {
		return getTitle() + " (" + getYear() + ")";
	}

	public ArrayList<String> getDirectors() {
		ArrayList<String> directors = new ArrayList<String>();
		if (this.directors.size() == 0) {
			for (CrewMember crewMember : getCredits().getCrew()) {
				if ("Director".equals(crewMember.getJob())) {
					directors.add(crewMember.getName());
				}
			}
			setDirectors(directors);
		}
		return this.directors;
	}
	
	public void setDirectors(ArrayList<String> _directors) {
		this.directors = _directors;
	}
}
