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
import java.net.URLDecoder;

/**
 * URI decoding utilities.
 *
 * @author  AO Industries, Inc.
 */
public class URIDecoder {

	private URIDecoder() {}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String)
	 */
	public static String decodeURIComponent(String s, String encoding) throws UnsupportedEncodingException {
		return (s == null) ? null : URLDecoder.decode(s, encoding);
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String)
	 */
	public static String decodeURIComponent(String s) {
		try {
			return decodeURIComponent(s, IRI.ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURIComponent(String s, String encoding, Appendable out) throws UnsupportedEncodingException, IOException {
		if(s != null) out.append(URLDecoder.decode(s, encoding));
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURIComponent(String s, Appendable out) throws IOException {
		try {
			decodeURIComponent(s, IRI.ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURIComponent(String s, String encoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(s != null) {
			if(encoder == null) {
				decodeURIComponent(s, encoding, out);
			} else {
				encoder.append(URLDecoder.decode(s, encoding), out);
			}
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURIComponent(String s, Appendable out, Encoder encoder) throws IOException {
		try {
			decodeURIComponent(s, IRI.ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURIComponent(String s, String encoding, StringBuilder sb) throws UnsupportedEncodingException {
		if(s != null) sb.append(URLDecoder.decode(s, encoding));
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURIComponent(String s, StringBuilder sb) {
		try {
			decodeURIComponent(s, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURIComponent(String s, String encoding, StringBuffer sb) throws UnsupportedEncodingException {
		if(s != null) sb.append(URLDecoder.decode(s, encoding));
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURIComponent(String s, StringBuffer sb) {
		try {
			decodeURIComponent(s, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @return  The decoded URI or {@code url} when not modified
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String)
	 */
	public static String decodeURI(String uri, String documentEncoding) throws UnsupportedEncodingException {
		if(uri == null) return null;
		StringBuilder sb = new StringBuilder(uri.length());
		decodeURI(uri, documentEncoding, sb);
		if(sb.length() == uri.length()) {
			assert uri.equals(sb.toString());
			return uri;
		} else {
			return sb.toString();
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @return  The decoded URI or {@code url} when not modified
	 *
	 * @see #encodeURI(java.lang.String)
	 */
	public static String decodeURI(String uri) {
		try {
			return decodeURI(uri, IRI.ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURI(String uri, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException {
		decodeURI(uri, documentEncoding, out, null);
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURI(java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURI(String uri, Appendable out) throws IOException {
		try {
			decodeURI(uri, IRI.ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
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
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURI(String uri, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(uri != null) {
			int len = uri.length();
			int pos = 0;
			URIComponent stage = URIComponent.BASE;
			while(pos < len) {
				int nextPos = StringUtility.indexOf(uri, RFC3986.RESERVED, pos);
				if(nextPos == -1) {
					stage.decodeUnreserved(uri, pos, len, documentEncoding, out, encoder);
					pos = len;
				} else {
					if(nextPos != pos) {
						stage.decodeUnreserved(uri, pos, nextPos, documentEncoding, out, encoder);
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
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encodeURI(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURI(String uri, Appendable out, Encoder encoder) throws IOException {
		try {
			decodeURI(uri, IRI.ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURI(String uri, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException {
		try {
			decodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuilder", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURI(java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURI(String uri, StringBuilder sb) {
		try {
			decodeURI(uri, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURI(String uri, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException {
		try {
			decodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuffer", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURI(java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURI(String uri, StringBuffer sb) {
		try {
			decodeURI(uri, IRI.ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
		}
	}
}
