/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2011, 2016, 2019, 2021  AO Industries, Inc.
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Empty parameters singleton.
 *
 * @author  AO Industries, Inc.
 */
final public class EmptyURIParameters implements URIParameters {

	private static final EmptyURIParameters instance = new EmptyURIParameters();

	public static EmptyURIParameters getInstance() {
		return instance;
	}

	private EmptyURIParameters() {
	}

	/**
	 * @see  URIParameters#toString()
	 */
	@Override
	public String toString() {
		return "";
	}

	@Override
	public String getParameter(String name) {
		return null;
	}

	@Override
	public Iterator<String> getParameterNames() {
		return Collections.emptyIterator();
	}

	@Override
	public List<String> getParameterValues(String name) {
		return null;
	}

	@Override
	public Map<String, List<String>> getParameterMap() {
		return Collections.emptyMap();
	}

	@Override
	public boolean isFastToString() {
		return true;
	}
}
