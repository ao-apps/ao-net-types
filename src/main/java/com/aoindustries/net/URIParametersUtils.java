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

import java.util.List;
import java.util.Map;

/**
 * Utilities using {@link URIParameters}.
 *
 * @author  AO Industries, Inc.
 */
final public class URIParametersUtils {

	/**
	 * Adds all of the parameters to a URI.
	 *
	 * @return  The new URI or {@code uri} when not modified
	 */
	public static String addParams(String uri, URIParameters params) {
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
				{
					// Find first of '?' or '#'
					int pathEnd = URIParser.getPathEnd(uri);
					if(pathEnd >= uriLen) {
						// First parameter to end
						newUri.append(uri).append('?');
						anchorStart = -1;
					} else if(uri.charAt(pathEnd) == '?') {
						anchorStart = uri.indexOf('#', pathEnd + 1);
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
						newUri.append(uri, 0, anchorStart).append('?');
					}
				}
				appendQueryString(paramsMap.entrySet(), newUri);
				if(anchorStart != -1) newUri.append(uri, anchorStart, uriLen);
				assert newUri.length() > uriLen : "Parameters must have been added";
				uri = newUri.toString();
			}
		}
		return uri;
	}

	/**
	 * Appends the query string encoded, not including the '?' prefix.
	 */
	private static void appendQueryString(Iterable<Map.Entry<String,List<String>>> params, StringBuilder sb) {
		boolean didOne = false;
		for(Map.Entry<String,List<String>> entry : params) {
			String name = entry.getKey();
			List<String> values = entry.getValue();
			if(values.size() == 1) {
				// Optimize common single-value case
				String value = values.get(0);
				assert value != null : "null values no longer supported to be consistent with servlet environment";
				if(didOne) {
					sb.append('&');
				} else {
					didOne = true;
				}
				URIEncoder.encodeURIComponent(name, sb);
				sb.append('=');
				URIEncoder.encodeURIComponent(value, sb);
			} else {
				String encodedName = URIEncoder.encodeURIComponent(name);
				for(String value : values) {
					assert value != null : "null values no longer supported to be consistent with servlet environment";
					if(didOne) {
						sb.append('&');
					} else {
						didOne = true;
					}
					sb.append(encodedName);
					sb.append('=');
					URIEncoder.encodeURIComponent(value, sb);
				}
			}
		}
		assert didOne;
	}

	/**
	 * Appends the query string encoded, not including the '?' prefix.
	 */
	public static void appendQueryString(URIParameters params, StringBuilder sb) {
		if(params == null) return;
		Map<String,List<String>> map = params.getParameterMap();
		if(map.isEmpty()) return;
		appendQueryString(map.entrySet(), sb);
	}

	/**
	 * Gets the query string encoded, not including the '?' prefix.
	 *
	 * @return  The query string or {@code null} for none.
	 */
	// TODO: StringBuilder variant?
	public static String toQueryString(URIParameters params) {
		if(params == null) return null;
		Map<String,List<String>> map = params.getParameterMap();
		if(map.isEmpty()) return null;
		StringBuilder sb = new StringBuilder();
		appendQueryString(map.entrySet(), sb);
		return sb.toString();
	}

	/**
	 * Returns the optimal type of parameters.
	 * <ol>
	 * <li>When @{code queryString} is {@code null} or {@code ""}: {@link EmptyURIParameters}</li>
	 * <li>Otherwise {@link URIParametersMap}.
	 * </ol>
	 */
	public static URIParameters of(String queryString) {
		if(queryString == null || queryString.isEmpty()) {
			return EmptyURIParameters.getInstance();
		} else {
			return new URIParametersMap(queryString);
		}
	}

	/**
	 * Make no instances.
	 */
	private URIParametersUtils() {
	}
}
