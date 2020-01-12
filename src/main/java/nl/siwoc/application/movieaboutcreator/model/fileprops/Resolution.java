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

public enum Resolution implements FileProp {

	RES1080("RES1080","output_1080p.png"),
	RES720("RES720","output_720p.png"),
	PAL("PAL","output_pal.png"),
	NTSC("NTSC","output_ntsc.png");
	
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
	
	public static Resolution getResolution(int width, int height) throws IllegalArgumentException {
		if (height > 720 || width > 1280) {
			return RES1080;
		} else if (height > 576 || width > 720) {
			return RES720;
		} else if (height > 480) {
			return PAL;
		} else if (width > 640) {
			return NTSC;
		}
		throw new IllegalArgumentException("Unknown Resolution: [RES" + width + "x" + height + "]");
	}

}
