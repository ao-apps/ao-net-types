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
import java.net.URLEncoder;

/**
 * URI encoding utilities.
 *
 * @author  AO Industries, Inc.
 */
public class URIEncoder {

	private URIEncoder() {}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String)
	 */
	public static String encodeURIComponent(String s, String encoding) throws UnsupportedEncodingException {
		return (s == null) ? null : StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20");
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
	 * @see #decodeURIComponent(java.lang.String)
	 */
	public static String encodeURIComponent(String s) {
		try {
			return encodeURIComponent(s, IRI.ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURIComponent(String s, String encoding, Appendable out) throws UnsupportedEncodingException, IOException {
		if(s != null) StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", out);
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
	 * @see #decodeURIComponent(java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURIComponent(String s, Appendable out) throws IOException {
		try {
			encodeURIComponent(s, IRI.ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURIComponent(String s, String encoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(s != null) {
			if(encoder == null) {
				encodeURIComponent(s, encoding, out);
			} else {
				StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", out, encoder);
			}
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
	 * @see #decodeURIComponent(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURIComponent(String s, Appendable out, Encoder encoder) throws IOException {
		try {
			encodeURIComponent(s, IRI.ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURIComponent(String s, String encoding, StringBuilder sb) throws UnsupportedEncodingException {
		if(s != null) {
			try {
				StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", sb);
			} catch(UnsupportedEncodingException e) {
				throw e;
			} catch(IOException e) {
				throw new AssertionError("IOException should not occur on StringBuilder", e);
			}
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
	 * @see #decodeURIComponent(java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURIComponent(String s, StringBuilder sb) {
		try {
			encodeURIComponent(s, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURIComponent(String s, String encoding, StringBuffer sb) throws UnsupportedEncodingException {
		if(s != null) {
			try {
				StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", sb);
			} catch(UnsupportedEncodingException e) {
				throw e;
			} catch(IOException e) {
				throw new AssertionError("IOException should not occur on StringBuffer", e);
			}
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
	 * @see #decodeURIComponent(java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURIComponent(String s, StringBuffer sb) {
		try {
			encodeURIComponent(s, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (and '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @return  The encoded URI or {@code url} when not modified
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String)
	 */
	public static String encodeURI(String uri, String documentEncoding) throws UnsupportedEncodingException {
		if(uri == null) return null;
		StringBuilder sb = new StringBuilder(uri.length() + 16);
		encodeURI(uri, documentEncoding, sb);
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
	 * (and '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @return  The encoded URI or {@code url} when not modified
	 *
	 * @see #decodeURI(java.lang.String)
	 */
	public static String encodeURI(String uri) {
		try {
			return encodeURI(uri, IRI.ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURI(String uri, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException {
		encodeURI(uri, documentEncoding, out, null);
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
	 * @see #decodeURI(java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURI(String uri, Appendable out) throws IOException {
		try {
			encodeURI(uri, IRI.ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	private static final char[] rfc3986ReservedCharacters_and_percent;
	static {
		rfc3986ReservedCharacters_and_percent = new char[RFC3986.RESERVED.length + 1];
		System.arraycopy(RFC3986.RESERVED, 0, rfc3986ReservedCharacters_and_percent, 0, RFC3986.RESERVED.length);
		rfc3986ReservedCharacters_and_percent[RFC3986.RESERVED.length] = '%';
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 * <p>
	 * TODO: Support <a href="https://tools.ietf.org/html/rfc2368">mailto:</a> scheme specifically?
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURI(String uri, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(uri != null) {
			int len = uri.length();
			int pos = 0;
			URIComponent stage = URIComponent.BASE;
			while(pos < len) {
				int nextPos = StringUtility.indexOf(uri, rfc3986ReservedCharacters_and_percent, pos);
				if(nextPos == -1) {
					stage.encodeUnreserved(uri, pos, len, documentEncoding, out, encoder);
					pos = len;
				} else {
					if(nextPos != pos) {
						stage.encodeUnreserved(uri, pos, nextPos, documentEncoding, out, encoder);
					}
					char reserved = uri.charAt(nextPos++);
					stage = stage.nextStage(reserved);
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
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decodeURI(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURI(String uri, Appendable out, Encoder encoder) throws IOException {
		try {
			encodeURI(uri, IRI.ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURI(String uri, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException {
		try {
			encodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
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
	 * @see #decodeURI(java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURI(String uri, StringBuilder sb) {
		try {
			encodeURI(uri, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURI(String uri, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException {
		try {
			encodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuffer", e);
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
	 * @see #decodeURI(java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURI(String uri, StringBuffer sb) {
		try {
			encodeURI(uri, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}
}
