/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2011, 2013, 2016, 2019  AO Industries, Inc.
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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Utilities using {@link URIParameters}.
 *
 * @author  AO Industries, Inc.
 */
final public class URIParametersUtils {

	/**
	 * Adds all of the parameters to a URI in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @return  The new URI or {@code uri} when not modified
	 */
	public static String addParams(String uri, URIParameters params, String documentEncoding) throws UnsupportedEncodingException {
		if(params != null) {
			Map<String, List<String>> paramsMap = params.getParameterMap();
			int mapSize = paramsMap.size();
			if(mapSize != 0) {
				int uriLen = uri.length();
				StringBuilder newUri = new StringBuilder(
					uriLen
					// 20 is arbitrary, just some default size scaled to number of parameters as a starting point
					+ mapSize * 20
				);
				int anchorStart;
				boolean hasQuestion;
				{
					// Find first of '?' or '#'
					int pathEnd = URIParser.getPathEnd(uri);
					if(pathEnd >= uriLen) {
						// First parameter to end
						newUri.append(uri);
						anchorStart = -1;
						hasQuestion = false;
					} else if(uri.charAt(pathEnd) == '?') {
						anchorStart = uri.indexOf('#', pathEnd + 1);
						hasQuestion = true;
						if(anchorStart == -1) {
							// Additional parameter to end
							newUri.append(uri);
						} else {
							// Additional parameter before anchor
							newUri.append(uri, 0, anchorStart);
						}
					} else {
						// First parameter before anchor
						assert uri.charAt(pathEnd) == '#';
						anchorStart = pathEnd;
						newUri.append(uri, 0, anchorStart);
						hasQuestion = false;
					}
				}
				for(Map.Entry<String,List<String>> entry : paramsMap.entrySet()) {
					String name = entry.getKey();
					String encodedName = URIEncoder.encodeURIComponent(name, documentEncoding);
					for(String value : entry.getValue()) {
						assert value != null : "null values no longer supported to be consistent with servlet environment";
						if(hasQuestion) {
							newUri.append('&');
						} else {
							newUri.append('?');
							hasQuestion = true;
						}
						newUri.append(encodedName).append('=');
						URIEncoder.encodeURIComponent(value, documentEncoding, newUri);
					}
				}
				if(anchorStart != -1) newUri.append(uri, anchorStart, uriLen);
				assert newUri.length() > uriLen : "Parameters must have been added";
				uri = newUri.toString();
			}
		}
		return uri;
	}

	/**
	 * Gets the query string encoded in a given encoding,
	 * not including the '?' prefix.
	 *
	 * @return  The query string or {@code null} for none.
	 */
	public static String toQueryString(URIParameters params, String documentEncoding) throws UnsupportedEncodingException {
		if(params == null) return null;
		Map<String,List<String>> map = params.getParameterMap();
		if(map.isEmpty()) return null;
		StringBuilder sb = new StringBuilder();
		boolean didOne = false;
		for(Map.Entry<String,List<String>> entry : map.entrySet()) {
			String encodedName = URIEncoder.encodeURIComponent(entry.getKey(), documentEncoding);
			for(String value : entry.getValue()) {
				if(didOne) {
					sb.append('&');
				} else {
					didOne = true;
				}
				sb.append(encodedName);
				assert value != null : "null values no longer supported to be consistent with servlet environment";
				sb.append('=');
				URIEncoder.encodeURIComponent(value, documentEncoding, sb);
			}
		}
		assert didOne;
		return sb.toString();
	}

	/**
	 * Returns the optimal type of parameters.
	 * <ol>
	 * <li>When @{code queryString} is {@code null} or {@code ""}: {@link EmptyParameters}</li>
	 * <li>Otherwise {@link URIParametersMap}.
	 * </ol>
	 */
	public static URIParameters of(String queryString, String documentEncoding) throws UnsupportedEncodingException {
		if(queryString == null || queryString.isEmpty()) {
			return EmptyURIParameters.getInstance();
		} else {
			return new URIParametersMap(queryString, documentEncoding);
		}
	}

	/**
	 * Make no instances.
	 */
	private URIParametersUtils() {
	}
}
