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
