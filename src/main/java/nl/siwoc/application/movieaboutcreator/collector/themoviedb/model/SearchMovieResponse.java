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
public class SearchMovieResponse {
	private int page;
	private int total_results;
	private int total_pages;
	private ArrayList<SearchMovieResult> results = new ArrayList<SearchMovieResult>();


	// Getter Methods 

	public int getPage() {
		return page;
	}

	public int getTotal_results() {
		return total_results;
	}

	public int getTotal_pages() {
		return total_pages;
	}

	// Setter Methods 

	public ArrayList<SearchMovieResult> getResults() {
		return results;
	}

	public void setResults(ArrayList<SearchMovieResult> results) {
		this.results = results;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setTotal_results(int total_results) {
		this.total_results = total_results;
	}

	public void setTotal_pages(int total_pages) {
		this.total_pages = total_pages;
	}
}