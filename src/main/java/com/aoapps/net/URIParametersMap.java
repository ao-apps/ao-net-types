/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2011, 2013, 2016, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with ao-net-types.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.net;

import com.aoapps.lang.NullArgumentException;
import com.aoapps.lang.Strings;
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

	public static URIParametersMap of() {
		return new URIParametersMap();
	}

	public static URIParametersMap of(String name, String value) {
		return new URIParametersMap()
			.add(name, value);
	}

	public static URIParametersMap of(String name, Object value) {
		return new URIParametersMap()
			.add(name, value);
	}

	public static URIParametersMap of(
		String name1, String value1,
		String name2, String value2
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2);
	}

	public static URIParametersMap of(
		String name1, Object value1,
		String name2, Object value2
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2);
	}

	public static URIParametersMap of(
		String name1, String value1,
		String name2, String value2,
		String name3, String value3
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2)
			.add(name3, value3);
	}

	public static URIParametersMap of(
		String name1, Object value1,
		String name2, Object value2,
		String name3, Object value3
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2)
			.add(name3, value3);
	}

	public static URIParametersMap of(
		String name1, String value1,
		String name2, String value2,
		String name3, String value3,
		String name4, String value4
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2)
			.add(name3, value3)
			.add(name4, value4);
	}

	public static URIParametersMap of(
		String name1, Object value1,
		String name2, Object value2,
		String name3, Object value3,
		String name4, Object value4
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2)
			.add(name3, value3)
			.add(name4, value4);
	}

	public static URIParametersMap of(
		String name1, String value1,
		String name2, String value2,
		String name3, String value3,
		String name4, String value4,
		String name5, String value5
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2)
			.add(name3, value3)
			.add(name4, value4)
			.add(name5, value5);
	}

	public static URIParametersMap of(
		String name1, Object value1,
		String name2, Object value2,
		String name3, Object value3,
		String name4, Object value4,
		String name5, Object value5
	) {
		return new URIParametersMap()
			.add(name1, value1)
			.add(name2, value2)
			.add(name3, value3)
			.add(name4, value4)
			.add(name5, value5);
	}

	private final Map<String, List<String>> map = new TreeMap<>();
	private final Map<String, List<String>> unmodifiableMap = Collections.unmodifiableMap(map);
	private String toString;

	/**
	 * Creates an empty set of parameters.
	 */
	public URIParametersMap() {
	}

	/**
	 * Parses the provided URL-Encoded parameter string.
	 *
	 * @param queryString  The URL-encoded parameter string or {@code null} or {@code ""} for none
	 */
	public URIParametersMap(String queryString) {
		if(queryString != null) {
			// TODO: We could defer this conversion until needed, which would mean wrapping then calling toString would
			//       never need to perform full parsing
			for(String nameValue : Strings.split(queryString, '&')) {
				int pos = nameValue.indexOf('=');
				String name;
				String value;
				if(pos == -1) {
					name = URIDecoder.decodeURIComponent(nameValue);
					value = ""; // Servlet environment treats no equal sign same as value equal empty string - matching here
				} else {
					// TODO: Avoid substring?
					name = URIDecoder.decodeURIComponent(nameValue.substring(0, pos));
					value = URIDecoder.decodeURIComponent(nameValue.substring(pos + 1));
				}
				assert name != null;
				assert value != null;
				add(name, value);
			}
			toString = queryString;
		}
	}

	/**
	 * @see  URIParameters#toString()
	 */
	@Override
	public String toString() {
		String s = toString;
		if(s == null) toString = s = Objects.toString(URIParametersUtils.toQueryString(this), "");
		return s;
	}

	@Override
	public String getParameter(String name) {
		List<String> values = map.get(name);
		if(values==null) return null;
		assert !values.isEmpty();
		return values.get(0);
		// TODO: Defer conversion to String
		//Object value = values.get(0);
		//String str;
		//if(value instanceof String) {
		//	str = (String)value;
		//} else {
		//	// Coerce to string now
		//	str = Coercion.toString(value);
		//	values.set(0, str);
		//}
		//return str;
	}

	@Override
	public Iterator<String> getParameterNames() {
		return unmodifiableMap.keySet().iterator();
	}

	@Override
	public List<String> getParameterValues(String name) {
		List<String> values = unmodifiableMap.get(name);
		if(values == null) {
			return null;
		} else if(values.size() == 1) {
			// Already unmodifiable from Collections.singletonList
			return values;
		} else {
			return Collections.unmodifiableList(values);
		}
	}

	// TODO: Individual elements are modifiable when have more than one value
	@Override
	public Map<String, List<String>> getParameterMap() {
		return unmodifiableMap;
	}

	@Override
	public boolean isFastToString() {
		return toString != null;
	}

	@Override
	public final URIParametersMap add(String name, String value) {
		if(value != null) {
			NullArgumentException.checkNotNull(name, "name");
			List<String> values = map.get(name);
			if(values == null) {
				map.put(name, Collections.singletonList(value));
			} else if(values.size() == 1) {
				List<String> newValues = new ArrayList<>();
				newValues.add(values.get(0));
				newValues.add(value);
				map.put(name, newValues);
			} else {
				values.add(value);
			}
			toString = null;
		}
		return this;
	}

	@Override
	public URIParametersMap add(String name, Object value) {
		MutableURIParameters.super.add(name, value);
		return this;
	}

	@Override
	public URIParametersMap add(String name, Iterable<?> values) {
		MutableURIParameters.super.add(name, values);
		return this;
	}

	@Override
	public URIParametersMap add(String name, Object ... values) {
		MutableURIParameters.super.add(name, values);
		return this;
	}
}
