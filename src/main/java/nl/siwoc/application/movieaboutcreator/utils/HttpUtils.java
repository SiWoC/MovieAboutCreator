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
package nl.siwoc.application.movieaboutcreator.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {

	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

	public static HttpURLConnection getConnection(String url) throws Exception {
		URL resourceUrl, base, next;
		HashMap<String, Integer> visited = new HashMap<String, Integer>();;
		HttpURLConnection conn;
		String location;
		int times;
	
		while (true)
		{
			times = visited.compute(url, (key, count) -> count == null ? 1 : count + 1);
	
			if (times > 3)
				throw new Exception("Stuck in redirect loop");
	
			LOG.debug("HTTP call: " + url);
			resourceUrl = new URL(url);
			conn        = (HttpURLConnection) resourceUrl.openConnection();
	
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setInstanceFollowRedirects(false);   // Make the logic below easier to detect redirections
			conn.setRequestProperty("User-Agent", Properties.getUserAgent());
	
			switch (conn.getResponseCode())
			{
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
				location = conn.getHeaderField("Location");
				//location = URLDecoder.decode(location, "UTF-8");
				base     = new URL(url);               
				next     = new URL(base, location);  // Deal with relative URLs
				url      = next.toExternalForm();
				continue;
			}
	
			break;
		}
		return conn;
	}

}
