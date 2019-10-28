package nl.siwoc.application.movieaboutcreator.model.fileprops;

public enum AudioChannels implements FileProp {

	CHANNELS2("Stereo","audiochannels_20.png"),
	CHANNELS3("2.1 Stereo","audiochannels_21.png"),
	CHANNELS6("5.1 Surround","audiochannels_51.png"),
	CHANNELS7("6.1 Surround","audiochannels_61.png"),
	CHANNELS8("7.1 Surround","audiochannels_71.png");
	
	private String name;
	private String logo;
	
	private AudioChannels(String _name, String _logo) {
		this.setName(_name);
		this.setLogo(_logo);
	}

	public String getName() {
		return name;
	}

	public void setName(String _name) {
		this.name = _name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
