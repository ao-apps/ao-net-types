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

import com.aoindustries.lang.NullArgumentException;
import com.aoindustries.util.StringUtility;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A modifiable parameter map.
 *
 * @author  AO Industries, Inc.
 */
public class URIParametersMap implements MutableURIParameters {

	private final Map<String,List<String>> map = new TreeMap<>();
	private final Map<String,List<String>> unmodifiableMap = Collections.unmodifiableMap(map);

	/**
	 * Creates an empty set of parameters.
	 */
	public URIParametersMap() {
	}

	/**
	 * Parses the provided URL-Encoded parameter string in a given encoding.
	 *
	 * @param queryString  The URL-encoded parameter string or {@code null} or {@code ""} for none
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 */
	public URIParametersMap(String queryString, String documentEncoding) throws UnsupportedEncodingException {
		if(queryString != null) {
			for(String nameValue : StringUtility.splitString(queryString, '&')) {
				int pos = nameValue.indexOf('=');
				String name;
				String value;
				if(pos == -1) {
					name = URIComponent.QUERY.decode(nameValue, documentEncoding);
					value = ""; // Servlet environment treats no equal sign same as value equal empty string - matching here
				} else {
					name = URIComponent.QUERY.decode(nameValue.substring(0, pos), documentEncoding);
					value = URIComponent.QUERY.decode(nameValue.substring(pos + 1), documentEncoding);
				}
				addParameter(name, value);
			}
		}
	}

	@Override
	public String toString() {
		try {
			return Objects.toString(URIParametersUtils.toQueryString(this, IRI.ENCODING.name()), "");
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	@Override
	public String getParameter(String name) {
		List<String> values = map.get(name);
		if(values==null) return null;
		assert !values.isEmpty();
		return values.get(0);
	}

	@Override
	public Iterator<String> getParameterNames() {
		return unmodifiableMap.keySet().iterator();
	}

	@Override
	public List<String> getParameterValues(String name) {
		return unmodifiableMap.get(name);
	}

	@Override
	public Map<String, List<String>> getParameterMap() {
		return unmodifiableMap;
	}

	@Override
	final public void addParameter(String name, String value) {
		NullArgumentException.checkNotNull(name, "name");
		NullArgumentException.checkNotNull(value, "value");
		List<String> values = map.get(name);
		if(values==null) map.put(name, values = new ArrayList<>());
		values.add(value);
	}

	@Override
	public void addParameters(String name, Iterable<? extends String> values) {
		if(values != null) {
			for(String value : values) {
				addParameter(name, value);
			}
		}
	}
}
