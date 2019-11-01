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
