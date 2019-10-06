package nl.siwoc.application.movieaboutcreator.collector.themoviedb.model;

import java.util.ArrayList;

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