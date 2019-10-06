package nl.siwoc.application.movieaboutcreator.model.fileprops;

public enum AudioChannels implements FileProp {

	ASF("ASF","container_asf.png"),
	AVI("AVI","container_avi.png"),
	DVD("DVD","container_dvd.png"),
	ISO("ISO","container_iso.png"),
	M2TS("M2TS","container_m2ts.png"),
	MKV("MKV","container_mkv.png"),
	MOV("MOV","container_mov.png"),
	MP4("MP4","container_mp4.png"),
	MPG("MPG","container_mpg.png");
	
	private String name;
	private String logo;
	
	private AudioChannels(String _name, String _logo) {
		this.setName(_name);
		this.setLogo(_logo);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
