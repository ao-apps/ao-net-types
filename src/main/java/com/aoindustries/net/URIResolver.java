/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-net-types.
 *
 * ao-net-types is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-net-types is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-net-types.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.net;

import com.aoindustries.lang.NullArgumentException;
import java.net.MalformedURLException;

/**
 * Utilities for resolving URI.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Add tests
public class URIResolver {

	private URIResolver() {}

	/**
	 * Resolves a possibly relative path to a context-absolute path.
	 * Resolves ./ and ../ at the beginning of the URL but not in the middle of the URL. (TODO: Resolve in middle, too)
	 * If the URL begins with http:, https:, javascript:, mailto:, telnet:, tel:, cid:, file:, or data:, (case-insensitive) it is not altered.
	 *
	 * @param  servletPath  Required when path might be altered.
	 */
	// TODO: new version of this method with a boolean "detectScheme", which would enable the detection of
	// TODO: schemes in the URL prefix.  Some contexts may be expecting relative URLs that might also look
	// TODO: like a scheme.  This could result in an external URL where one was otherwise not expected.
	// TODO: One example could be user-provided upload filenames.
	// TODO: Other versions of this method, such as in HttpServletUtil, would have the same change.
	public static String getAbsolutePath(String servletPath, String relativeUrlPath) throws MalformedURLException {
		char firstChar;
		if(
			relativeUrlPath.length() > 0
			&& (firstChar=relativeUrlPath.charAt(0)) != '/'
			&& firstChar != '#' // Skip anchor-only paths
			&& !URIParser.isScheme(relativeUrlPath, "http")
			&& !URIParser.isScheme(relativeUrlPath, "https")
			&& !URIParser.isScheme(relativeUrlPath, "javascript")
			&& !URIParser.isScheme(relativeUrlPath, "mailto")
			&& !URIParser.isScheme(relativeUrlPath, "telnet")
			&& !URIParser.isScheme(relativeUrlPath, "tel")
			&& !URIParser.isScheme(relativeUrlPath, "cid")
			&& !URIParser.isScheme(relativeUrlPath, "file")
			&& !URIParser.isScheme(relativeUrlPath, "data")
		) {
			NullArgumentException.checkNotNull(servletPath, "servletPath");
			int slashPos = servletPath.lastIndexOf('/');
			if(slashPos==-1) throw new MalformedURLException("No slash found in servlet path: "+servletPath);
			final String newPath = relativeUrlPath;
			final int newPathLen = newPath.length();
			int newPathStart = 0;
			boolean modified;
			do {
				modified = false;
				if(
					newPathLen >= (newPathStart+2)
					&& newPath.regionMatches(newPathStart, "./", 0, 2)
				) {
					newPathStart += 2;
					modified = true;
				}
				if(
					newPathLen >= (newPathStart+3)
					&& newPath.regionMatches(newPathStart, "../", 0, 3)
				) {
					slashPos = servletPath.lastIndexOf('/', slashPos-1);
					if(slashPos==-1) throw new MalformedURLException("Too many ../ in relativeUrlPath: "+relativeUrlPath);

					newPathStart += 3;
					modified = true;
				}
			} while(modified);
			relativeUrlPath =
				new StringBuilder((slashPos+1) + (newPathLen-newPathStart))
				.append(servletPath, 0, slashPos+1)
				.append(newPath, newPathStart, newPathLen)
				.toString();
		}
		return relativeUrlPath;
	}
}
