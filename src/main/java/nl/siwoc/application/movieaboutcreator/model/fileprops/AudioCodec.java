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
package nl.siwoc.application.movieaboutcreator.model.fileprops;

public enum AudioCodec implements FileProp {

	AAC("AAC","audiocodec_aac.png"),
	MP4A("MP4A","audiocodec_aac.png"),
	AC3("AC3","audiocodec_dolby.png"),
	EAC3("EAC3","audiocodec_dolby.png"),
	DOLBY("DOLBY","audiocodec_dolby.png"),
	DOLBY20("DOLBY20","audiocodec_dolby20.png"),
	DOLBY30("DOLBY30","audiocodec_dolby30.png"),
	DOLBY51("DOLBY51","audiocodec_dolby51.png"),
	DTS("DTS","audiocodec_dts.png"),
	DTSHD("DTSHD","audiocodec_dtshd.png"),
	DTSHD51("DTSHD51","audiocodec_dtshd51.png"),
	DTSHD61("DTSHD61","audiocodec_dtshd61.png"),
	DTSHD71("DTSHD71","audiocodec_dtshd71.png"),
	DTS_TEST("DTS_TEST","audiocodec_dts_test.png"),
	FLAC("FLAC","audiocodec_flac.png"),
	MP3("MP3","audiocodec_mp3.png"),
	OGG("OGG","audiocodec_ogg.png"),
	PCM("PCM","audiocodec_pcm.png"),
	SOWT("SOWT","audiocodec_pcm.png"),
	TRUEHD("TRUEHD","audiocodec_truehd.png"),
	TRUEHD51("TRUEHD51","audiocodec_truehd51.png"),
	TRUEHD61("TRUEHD61","audiocodec_truehd61.png"),
	TRUEHD71("TRUEHD71","audiocodec_truehd71.png");
	
	private String name;
	private String logo;
	
	private AudioCodec(String _name, String _logo) {
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
