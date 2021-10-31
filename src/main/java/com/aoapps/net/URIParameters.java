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
 * along with ao-net-types.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.net;

import com.aoapps.lang.Strings;
import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.io.Writable;
import com.aoapps.lang.math.SafeMath;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides read-only access to URI parameters.
 *
 * @author  AO Industries, Inc.
 */
public interface URIParameters extends Writable {

	/**
	 * Gets the query string encoded in the default encoding {@link IRI#ENCODING},
	 * not including the '?' prefix.
	 *
	 * @see URIParametersUtils#toQueryString(com.aoapps.net.URIParameters)
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
	Map<String, List<String>> getParameterMap();

	@Override
	default long getLength() throws IOException {
		return toString().length();
	}

	// TODO: Remove this default in next major release
	@Override
	default boolean isFastToString() {
		return false;
	}

	@Override
	default void writeTo(Writer out) throws IOException {
		appendTo(out);
	}

	@Override
	default void writeTo(Writer out, long off, long len) throws IOException {
		appendTo(out, off, off + len);
	}

	@Override
	default void writeTo(Encoder encoder, Writer out) throws IOException {
		appendTo(encoder, out);
	}

	@Override
	default void writeTo(Encoder encoder, Writer out, long off, long len) throws IOException {
		appendTo(encoder, out, off, off + len);
	}

	/**
	 * @see URIParametersUtils#appendQueryString(com.aoapps.net.URIParameters, java.lang.Appendable)
	 */
	@Override
	default void appendTo(Appendable out) throws IOException {
		URIParametersUtils.appendQueryString(this, out);
	}

	@Override
	default void appendTo(Appendable out, long start, long end) throws IOException {
		out.append(toString(), SafeMath.castInt(start), SafeMath.castInt(end));
	}

	/**
	 * @see URIParametersUtils#appendQueryString(com.aoapps.net.URIParameters, com.aoapps.lang.io.Encoder, java.lang.Appendable)
	 */
	@Override
	default void appendTo(Encoder encoder, Appendable out) throws IOException {
		URIParametersUtils.appendQueryString(this, encoder, out);
	}

	@Override
	default void appendTo(Encoder encoder, Appendable out, long start, long end) throws IOException {
		if(encoder == null) {
			appendTo(out, start, end);
		} else {
			encoder.append(toString(), SafeMath.castInt(start), SafeMath.castInt(end), out);
		}
	}

	@Override
	@SuppressWarnings({"AssertWithSideEffects", "StringEquality"})
	default URIParameters trim() {
		String toString;
		assert Strings.trim(toString = toString()) == toString : "query string should never have whitespace to trim";
		return this;
	}
}
