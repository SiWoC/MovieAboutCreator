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

public enum Container implements FileProp {

	ASF("ASF","container_asf.png"),
	AVI("AVI","container_avi.png"),
	DVD("DVD","container_dvd.png"),
	ISO("ISO","container_iso.png"),
	BRISO("BRISO","container_iso.png"),
	DVDISO("DVDISO","container_iso.png"),
	M2TS("M2TS","container_m2ts.png"),
	MKV("MKV","container_mkv.png"),
	MOV("MOV","container_mov.png"),
	MP4("MP4","container_mp4.png"),
	MPG("MPG","container_mpg.png");
	
	private String name;
	private String logo;
	
	private Container(String _name, String _logo) {
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
