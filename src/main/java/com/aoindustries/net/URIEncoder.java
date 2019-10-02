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

import com.aoindustries.io.Encoder;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.BitSet;

/**
 * URI encoding utilities.
 * <p>
 * TODO: These methods are for highest performance and are consistent with the JavaScript methods.
 * They are not meant for general purpose URL manipulation, and are not trying to replace
 * any full-featured URI tools.
 * <p>
 * Consider the following if needing more than what this provides (in no particular order):
 * </p>
 * <ol>
 * <li>{@link URL}</li>
 * <li>{@link java.net.URI}</li>
 * <li><a href="https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/utils/URIBuilder.html">URIBuilder</a></li>
 * <li><a href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/util/UriUtils.html">UriUtils</a></li>
 * <li><a href="https://guava.dev/releases/19.0/api/docs/com/google/common/net/UrlEscapers.html">UrlEscapers</a></li>
 * <li><a href="https://jena.apache.org/documentation/notes/iri.html">jena-iri</a></li>
 * <li><a href="https://github.com/xbib/net>org.xbib:net-url</a></li>
 * </ol>
 *
 * @see  URLEncoder
 *
 * @author  AO Industries, Inc.
 */
public class URIEncoder {

	private URIEncoder() {}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURIComponent(java.lang.String)
	 */
	public static String encodeURIComponent(String s) {
		try {
			return (s == null) ? null : StringUtility.replace(URLEncoder.encode(s, IRI.ENCODING.name()), '+', "%20");
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURIComponent(java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURIComponent(String s, Appendable out) throws IOException {
		try {
			if(s != null) StringUtility.replace(URLEncoder.encode(s, IRI.ENCODING.name()), '+', "%20", out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see URIDecoder#decodeURIComponent(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURIComponent(String s, Appendable out, Encoder encoder) throws IOException {
		try {
			if(s != null) {
				if(encoder == null) {
					encodeURIComponent(s, out);
				} else {
					StringUtility.replace(URLEncoder.encode(s, IRI.ENCODING.name()), '+', "%20", out, encoder);
				}
			}
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURIComponent(java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURIComponent(String s, StringBuilder sb) {
		try {
			encodeURIComponent(s, (Appendable)sb);
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuilder", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURIComponent(java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURIComponent(String s, StringBuffer sb) {
		try {
			encodeURIComponent(s, (Appendable)sb);
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuffer", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (and '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @return  The encoded URI or {@code url} when not modified
	 *
	 * @see URIDecoder#decodeURI(java.lang.String)
	 */
	public static String encodeURI(String uri) {
		if(uri == null) return null;
		StringBuilder sb = new StringBuilder(uri.length() + 16);
		encodeURI(uri, sb);
		if(sb.length() == uri.length()) {
			assert uri.equals(sb.toString());
			return uri;
		} else {
			return sb.toString();
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURI(java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURI(String uri, Appendable out) throws IOException {
		encodeURI(uri, out, null);
	}

	private static final BitSet rfc3986ReservedCharacters_and_percent;
	static {
		rfc3986ReservedCharacters_and_percent = new BitSet(128);
		rfc3986ReservedCharacters_and_percent.or(RFC3986.RESERVED);
		rfc3986ReservedCharacters_and_percent.set('%');
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see URIDecoder#decodeURI(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURI(String uri, Appendable out, Encoder encoder) throws IOException {
		if(uri != null) {
			int len = uri.length();
			int pos = 0;
			while(pos < len) {
				int nextPos = StringUtility.indexOf(uri, rfc3986ReservedCharacters_and_percent, pos);
				if(nextPos == -1) {
					// TODO: Avoid substring?
					encodeURIComponent(uri.substring(pos), out, encoder);
					pos = len;
				} else {
					if(nextPos != pos) {
						// TODO: Avoid substring?
						encodeURIComponent(uri.substring(pos, nextPos), out, encoder);
					}
					char reserved = uri.charAt(nextPos++);
					if(encoder == null) {
						out.append(reserved);
					} else {
						encoder.append(reserved, out);
					}
					pos = nextPos;
				}
			}
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURI(java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURI(String uri, StringBuilder sb) {
		try {
			encodeURI(uri, sb, null);
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuilder", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURI(java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURI(String uri, StringBuffer sb) {
		try {
			encodeURI(uri, sb, null);
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuffer", e);
		}
	}
}
