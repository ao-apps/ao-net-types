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
	// TODO: Swap order of out/encoder for cosistency with other APIs
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
	 * (or '%' for already percent-encoded).
	 * <p>
	 * Any existing lower-case percent-encoded values are normalized to upper-case.
	 * </p>
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
		String encoded = sb.toString();
		// Hex case may have changed during encode, leaving altered but same length
		return uri.equals(encoded) ? uri : encoded;
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * Any existing lower-case percent-encoded values are normalized to upper-case.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see URIDecoder#decodeURI(java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURI(String uri, Appendable out) throws IOException {
		encodeURI(uri, out, null);
	}

	static final BitSet rfc3986ReservedCharacters_and_percent;
	static {
		rfc3986ReservedCharacters_and_percent = new BitSet(128);
		rfc3986ReservedCharacters_and_percent.or(RFC3986.RESERVED);
		rfc3986ReservedCharacters_and_percent.set('%');
	}

	static String encodeRfc3986ReservedCharacters_and_percent(char ch) {
		// TODO: Benchmark switch versus BitSet (this might help all encoders that are switch-based)
		switch(ch) {
			// gen-delims
			case ':' : return "%3A";
			case '/' : return "%2F";
			case '?' : return "%3F";
			case '#' : return "%23";
			case '[' : return "%5B";
			case ']' : return "%5D";
			case '@' : return "%40";
			// sub-delims
			case '!' : return "%21";
			case '$' : return "%24";
			case '&' : return "%26";
			case '\'' : return "%27";
			case '(' : return "%28";
			case ')' : return "%29";
			case '*' : return "%2A";
			case '+' : return "%2B";
			case ',' : return "%2C";
			case ';' : return "%3B";
			case '=' : return "%3D";
			// already percent-encoded
			case '%' : return "%25";
			default : return null;
		}
	}

	private static boolean assertEncodeRfc3986ReservedCharacters_and_percentConsistent() {
		for(int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; i++) {
			char ch = (char)i;
			boolean isInBitSet = rfc3986ReservedCharacters_and_percent.get(ch);
			String replacement = encodeRfc3986ReservedCharacters_and_percent(ch);
			if(isInBitSet) {
				if(replacement == null) {
					throw new AssertionError("Character is in encodeRfc3986ReservedCharacters_and_percent but is not encoded: " + ch);
				}
			} else {
				if(replacement != null) {
					throw new AssertionError("Character is not in encodeRfc3986ReservedCharacters_and_percent but is encoded: " + ch + " -> " + replacement);
				}
			}
		}
		return true;
	}

	static {
		assert assertEncodeRfc3986ReservedCharacters_and_percentConsistent();
	}

	private static final BitSet rfc3986ReservedCharacters_percent_and_space;
	static {
		rfc3986ReservedCharacters_percent_and_space = new BitSet(128);
		rfc3986ReservedCharacters_percent_and_space.or(rfc3986ReservedCharacters_and_percent);
		rfc3986ReservedCharacters_percent_and_space.set(' ');
	}

	private static boolean isHex(char ch) {
		return
			(ch >= '0' && ch <= '9')
			|| (ch >= 'a' && ch <= 'f')
			|| (ch >= 'A' && ch <= 'F');
	}

	private static boolean isLowerHex(char ch) {
		return (ch >= 'a' && ch <= 'f');
	}

	private static char upperHex(char ch) {
		if(isLowerHex(ch)) {
			return (char)(ch - ('a' - 'A'));
		} else {
			return ch;
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * Any existing lower-case percent-encoded values are normalized to upper-case.
	 * </p>
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
			try {
				int len = uri.length();
				int pos = 0;
				while(pos < len) {
					int nextPos = StringUtility.indexOf(uri, rfc3986ReservedCharacters_percent_and_space, pos);
					if(nextPos == -1) {
						// TODO: Avoid substring?
						String encoded = URLEncoder.encode(uri.substring(pos), IRI.ENCODING.name());
						if(encoder == null) {
							out.append(encoded);
						} else {
							encoder.append(encoded, out);
						}
						// Old with second check for space -> '+' -> "%20":
						// encodeURIComponent(uri.substring(pos), out, encoder);
						pos = len;
					} else {
						if(nextPos != pos) {
							// TODO: Avoid substring?
							String encoded = URLEncoder.encode(uri.substring(pos, nextPos), IRI.ENCODING.name());
							if(encoder == null) {
								out.append(encoded);
							} else {
								encoder.append(encoded, out);
							}
							// Old with second check for space -> '+' -> "%20":
							// encodeURIComponent(uri.substring(pos, nextPos), out, encoder);
						}
						char reserved = uri.charAt(nextPos);
						char ch2, ch3;
						if(
							reserved == '%'
							&& (nextPos + 2) < len
							&& isHex(ch2 = uri.charAt(nextPos + 1))
							&& isHex(ch3 = uri.charAt(nextPos + 2))
						) {
							// Short-cut already percent-encoded
							pos = nextPos + 3;
							if(isLowerHex(ch2) || isLowerHex(ch3)) {
								// Convert to uppercase hex
								if(encoder == null) {
									out.append(reserved);
									out.append(upperHex(ch2));
									out.append(upperHex(ch3));
								} else {
									encoder.append(reserved, out);
									encoder.append(upperHex(ch2), out);
									encoder.append(upperHex(ch3), out);
								}
							} else {
								if(encoder == null) {
									out.append(uri, nextPos, pos);
								} else {
									encoder.append(uri, nextPos, pos, out);
								}
							}
						} else if(reserved == ' ') {
							pos = nextPos + 1;
							if(encoder == null) {
								out.append("%20");
							} else {
								encoder.append("%20", out);
							}
						} else {
							pos = nextPos + 1;
							if(encoder == null) {
								out.append(reserved);
							} else {
								encoder.append(reserved, out);
							}
						}
					}
				}
			} catch(UnsupportedEncodingException e) {
				throw new AssertionError("Standard encoding (" + IRI.ENCODING + ") should always exist", e);
			}
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link IRI#ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * Any existing lower-case percent-encoded values are normalized to upper-case.
	 * </p>
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
	 * Any existing lower-case percent-encoded values are normalized to upper-case.
	 * </p>
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
