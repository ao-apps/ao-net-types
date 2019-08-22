/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019  AO Industries, Inc.
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

/**
 * Java helper for <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax</a>.
 * <p>
 * TODO: Find something that does this well already. 
 * <a href="https://jena.apache.org/documentation/notes/iri.html">jena-iri</a>?
 * <a href="https://github.com/xbib/net>org.xbib:net-url</a>?
 * <a href="https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/utils/URIBuilder.html">URIBuilder</a>?
 * </p>
 *
 * @author  AO Industries, Inc.
 */
class RFC3986 {

	private RFC3986() {
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">Reserved Characters</a>.
	 */
	static final char[] GEN_DELIMS = {
		':' , '/' , '?' , '#' , '[' , ']' , '@'
	};

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">Reserved Characters</a>.
	 */
	static boolean isGenDelim(char ch) {
		switch(ch) {
			case ':' :
			case '/' :
			case '?' :
			case '#' :
			case '[' :
			case ']' :
			case '@' :
				return true;
			default :
				return false;
		}
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">Reserved Characters</a>.
	 */
	static final char[] SUB_DELIMS = {
		'!' , '$' , '&' , '\'' , '(' , ')',
		'*' , '+' , ',' , ';' , '='
	};

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">Reserved Characters</a>.
	 */
	static boolean isSubDelim(char ch) {
		switch(ch) {
			case '!' :
			case '$' :
			case '&' :
			case '\'' :
			case '(' :
			case ')' :
			case '*' :
			case '+' :
			case ',' :
			case ';' :
			case '=' :
				return true;
			default :
				return false;
		}
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">Reserved Characters</a>.
	 */
	static final char[] RESERVED;
	static {
		RESERVED = new char[GEN_DELIMS.length + SUB_DELIMS.length];
		System.arraycopy(GEN_DELIMS, 0, RESERVED, 0,                 GEN_DELIMS.length);
		System.arraycopy(SUB_DELIMS, 0, RESERVED, GEN_DELIMS.length, SUB_DELIMS.length);
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">Reserved Characters</a>.
	 */
	static boolean isReserved(char ch) {
		return isGenDelim(ch) || isSubDelim(ch);
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.3">Unreserved Characters</a>.
	 */
	static boolean isUnreserved(char ch) {
		return
			(ch >= 'A' && ch <= 'Z')
			|| (ch >= 'a' && ch <= 'z')
			|| (ch >= '0' && ch <= '9')
			|| ch == '-'
			|| ch == '.'
			|| ch == '_'
			|| ch == '~';
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-3.1">Scheme</a>:
	 * <blockquote>
	 * Scheme names consist of a sequence of characters beginning with a
	 * letter […]
	 * </blockquote>
	 */
	static boolean isSchemeBeginning(char ch) {
		return
			(ch >= 'A' && ch <= 'Z')
			|| (ch >= 'a' && ch <= 'z');
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-3.1">Scheme</a>:
	 * <blockquote>
	 * Scheme names consist of a sequence of characters […]
	 * followed by any combination of letters, digits, plus
	 * ("+"), period ("."), or hyphen ("-").
	 * </blockquote>
	 */
	static boolean isSchemeRemaining(char ch) {
		return
			(ch >= 'A' && ch <= 'Z')
			|| (ch >= 'a' && ch <= 'z')
			|| (ch >= '0' && ch <= '9')
			|| ch == '+'
			|| ch == '-'
			|| ch == '.';
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-3.1">Scheme</a>:
	 * <blockquote>
	 * An implementation should accept uppercase letters as equivalent to
	 * lowercase in scheme names […] but should only produce lowercase scheme
	 * names for consistency.
	 * </blockquote>
	 */
	static char normalizeScheme(char ch) {
		if(ch >= 'A' && ch <= 'Z') ch += 'a' - 'A';
		return ch;
	}

	/**
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.3">Unreserved Characters</a>:
	 * <blockquote>
	 * […] when found in a URI, should be decoded to their
	 * corresponding unreserved characters by URI normalizers.
	 * </blockquote>
	 *
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.4">When to Encode or Decode</a>:
	 * <blockquote>
	 * The only exception is for
	 * percent-encoded octets corresponding to characters in the unreserved
	 * set, which can be decoded at any time.
	 * </blockquote>
	 *
	 * <a href="https://tools.ietf.org/html/rfc3986#section-3.1">Scheme</a>:
	 * <blockquote>
	 * An implementation should accept uppercase letters as equivalent to
	 * lowercase in scheme names […] but should only produce lowercase scheme
	 * names for consistency.
	 * </blockquote>
	 *
	 * <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">Host</a>:
	 * <blockquote>
	 * Although host
	 * is case-insensitive, producers and normalizers should use lowercase
	 * for registered names and hexadecimal addresses for the sake of
	 * uniformity, while only using uppercase letters for percent-encodings.
	 * </blockquote>
	 *
	 * <a href="https://tools.ietf.org/html/rfc3986#section-3.2.3">Port</a>:
	 * <blockquote>
	 * URI producers and
	 * normalizers should omit the port component and its ":" delimiter if
	 * port is empty or if its value would be the same as that of the
	 * scheme's default.
	 * </blockquote>
	 */
	@SuppressWarnings("deprecation")
	static String normalize(String uri) {
		throw new com.aoindustries.lang.NotImplementedException("TODO: We're probably not going to implement full normalization");
	}

	// TODO: https://tools.ietf.org/html/rfc3986#section-3.2.2: This is the only place where
	// square bracket characters are allowed in the URI syntax.
}
