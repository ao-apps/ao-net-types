/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2011, 2016, 2019  AO Industries, Inc.
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides read-only access to URI parameters.
 *
 * @author  AO Industries, Inc.
 */
public interface URIParameters {

	/**
	 * Gets the query string encoded in the default encoding {@link URI#ENCODING},
	 * not including the '?' prefix.
	 *
	 * @see URIParametersUtils#toQueryString(com.aoindustries.net.URIParameters)
	 */
	@Override
	String toString();

	/**
	 * Gets the value for the provided parameter name or <code>null</code> if doesn't exist.
	 * If the parameter has multiple values, the first value is returned.
	 */
	String getParameter(String name);

	/**
	 * Gets an unmodifiable iterator of the parameter names.
	 */
	Iterator<String> getParameterNames();

	/**
	 * Gets an unmodifiable view of all values for a multi-value parameter or <code>null</code> if has no values.
	 */
	List<String> getParameterValues(String name);

	/**
	 * Gets an unmodifiable map view of all parameters.
	 */
	Map<String,List<String>> getParameterMap();
}
