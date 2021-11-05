/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019, 2021  AO Industries, Inc.
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

/**
 * URI parsing utilities.
 *
 * @author  AO Industries, Inc.
 */
public abstract class URIParser {

	/** Make no instances. */
	private URIParser() {throw new AssertionError();}

	/**
	 * Gets the length of a scheme.
	 *
	 * @return  The index of the ':' or {@code -1} on empty scheme, invalid character in scheme, or no colon found.
	 */
	public static int getSchemeLength(CharSequence uri) {
		int len = uri.length();
		if(len == 0) {
			return -1;
		}
		// First character
		if(!RFC3986.isSchemeBeginning(uri.charAt(0))) {
			return -1;
		}
		// Remaining characters
		for(int i = 1; i < len; i++) {
			char ch = uri.charAt(i);
			if(ch == ':') {
				return i;
			} else if(!RFC3986.isSchemeRemaining(ch)) {
				return -1;
			}
		}
		// No colon found
		return -1;
	}

	/**
	 * Checks if a URI starts with the given scheme.
	 *
	 * @param scheme  The scheme to look for, not including colon.
	 *                For example {@code "http"}.
	 *
	 * @throws IllegalArgumentException when {@code scheme} is determined to be invalid.
	 *         Please note that this determination is not guaranteed as shortcuts may
	 *         skip individual character comparisons.
	 */
	public static boolean isScheme(String uri, String scheme) throws IllegalArgumentException {
		if(uri == null) return false;
		int len = scheme.length();
		if(len == 0) {
			throw new IllegalArgumentException("Invalid scheme: " + scheme);
		}
		if((len + 1) > uri.length()) return false;
		for(int i = 0; i < len; i++) {
			char ch1 = scheme.charAt(i);
			boolean isValid = (i == 0) ? RFC3986.isSchemeBeginning(ch1) : RFC3986.isSchemeRemaining(ch1);
			if(!isValid) {
				throw new IllegalArgumentException("Invalid scheme: " + scheme);
			}
			char ch2 = uri.charAt(i);
			// Convert to lower-case, ASCII-only
			ch1 = RFC3986.normalizeScheme(ch1);
			ch2 = RFC3986.normalizeScheme(ch2);
			if(ch1 != ch2) return false;
		}
		// Must be followed by a colon
		return uri.charAt(len) == ':';
	}

	/**
	 * Checks if a URI has a scheme, not including any empty scheme (starts with ':')
	 */
	public static boolean hasScheme(String uri) {
		if(uri == null) return false;
		int len = uri.length();
		if(len == 0) return false;
		// First character
		if(!RFC3986.isSchemeBeginning(uri.charAt(0))) return false;
		// Remaining characters
		for(int i = 1; i < len; i++) {
			char ch = uri.charAt(i);
			if(ch == ':') {
				return true;
			} else if(!RFC3986.isSchemeRemaining(ch)) {
				return false;
			}
		}
		// No colon found
		return false;
	}

	/**
	 * Gets the scheme for a URI, or {@code null} when no scheme found.
	 * The scheme must start the URI, and match {@code ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )}
	 * before the first colon (:) found.  The scheme is normalized to lower-case.
	 * An empty scheme will never be returned (if the URI starts with ':').
	 *
	 * @return  The scheme, not including colon, or {@code null} when not found.
	 *          For example {@code "http"}.
	 */
	public static final String getScheme(String uri) {
		if(uri == null) return null;
		int len = uri.length();
		if(len == 0) return null;
		// First character
		if(!RFC3986.isSchemeBeginning(uri.charAt(0))) return null;

		// Find the colon, returning null if any non-A-Z,a-z is found on the way
		int colonPos = -1;
		for(int i = 1; i < len; i++) {
			char ch = uri.charAt(i);
			if(ch == ':') {
				colonPos = i;
				break;
			} else if(!RFC3986.isSchemeRemaining(ch)) {
				return null;
			}
		}
		// No colon found
		if(colonPos == -1) return null;
		// No empty scheme
		assert colonPos != 0 : "No empty scheme";
		// Normalize to lower-case
		char[] scheme = new char[colonPos];
		for(int i = 0; i < colonPos; i++) {
			// Convert to lower-case, ASCII-only
			scheme[i] = RFC3986.normalizeScheme(uri.charAt(i));
		}
		return String.valueOf(scheme);
	}

	/**
	 * Find the first of '?' or '#' from the given starting index.
	 *
	 * @return  The index of the first '?' or '#' or {@code uri.length()} when not found.
	 */
	public static int getPathEnd(String uri, int fromIndex) {
		int len = uri.length();
		for(int i = fromIndex; i < len; i++) {
			char ch = uri.charAt(i);
			if(ch == '?' || ch == '#') return i;
		}
		return len;
	}

	/**
	 * Find the first of '?' or '#'.
	 *
	 * @return  The index of the first '?' or '#' or {@code uri.length()} when not found.
	 */
	public static int getPathEnd(String uri) {
		return getPathEnd(uri, 0);
	}
}
