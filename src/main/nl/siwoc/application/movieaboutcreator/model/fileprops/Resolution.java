package nl.siwoc.application.movieaboutcreator.model.fileprops;

public enum Resolution implements FileProp {

	RESH1080("RESH1080","output_1080p.png"),
	RESW1920("RESW1920","output_1080p.png"),
	RESH720("RESH720","output_720p.png"),
	RESW1280("RESW1280","output_720p.png"),
	RESH480("RESH480","output_ntsc.png"),
	RESH576("RESH576","output_pal.png");
	
	private String name;
	private String logo;
	
	private Resolution(String _name, String _logo) {
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
