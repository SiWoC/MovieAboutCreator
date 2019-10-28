package nl.siwoc.application.movieaboutcreator.model.fileprops;

public enum VideoCodec implements FileProp {

	MPEG("MPEG","videocodec_mpeg.png"),
	MPEG2("MPEG2","videocodec_mpeg.png"),
	MPEG4("MPEG4","videocodec_mpeg.png"),
	MP41("MP41","videocodec_mpeg.png"),
	MP42("MP42","videocodec_mpeg.png"),
	DIVX("DIVX","videocodec_divx.png"),
	XVID("XVID","videocodec_xvid.png"),
	H264("H264","videocodec_h264.png"),
	MS("MS","videocodec_ms.png");
	
	private String name;
	private String logo;
	
	private VideoCodec(String _name, String _logo) {
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
