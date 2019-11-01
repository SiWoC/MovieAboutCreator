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
