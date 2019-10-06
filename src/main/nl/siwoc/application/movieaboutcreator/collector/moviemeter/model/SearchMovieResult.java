package nl.siwoc.application.movieaboutcreator.collector.moviemeter.model;

public class SearchMovieResult {
	private long id;
	private String title;
	private int year;
	private long votes;
	private float average;
	private String info;
	private String alternativeTitle = null;


	// Getter Methods 

	public long getId() {
		return id;
	}

	public void setId(long _id) {
		this.id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String _title) {
		this.title = _title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int _year) {
		this.year = _year;
	}

	public long getVotes() {
		return votes;
	}

	public void setVotes(long _votes) {
		this.votes = _votes;
	}

	public float getAverage() {
		return average;
	}

	public void setAverage(float _average) {
		this.average = _average;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String _info) {
		this.info = _info;
	}

	public String getAlternative_title() {
		return alternativeTitle;
	}

	public void setAlternative_title(String _alternativeTitle) {
		this.alternativeTitle = _alternativeTitle;
	}

	public String toString() {
		return getTitle() + " (" + getYear() + ")";
	}
	
}