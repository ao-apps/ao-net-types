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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * URI helper utilities.
 * <p>
 * This only deals with three parts of the URL:
 * </p>
 * <ol>
 *   <li>TODO: Split base into scheme + hierPart</li>
 *   <li>base - everything before the first '?' or '#' (exclusive).  This may
 *       include scheme, hier-part (host, port, path, and such), which this
 *       class is not concerned with.
 *   </li>
 *   <li>query - everything after the first '?' (exclusive) and the fragment '#' (exclusive)</li>
 *   <li>fragment - everything after the first '#' (exclusive)</li>
 * </p>
 * <p>
 * When creating documents in character encodings other than UTF-8, the query strings must be encoded
 * in the document encoding.  This, in turn, limits the ability of <a href="https://tools.ietf.org/html/rfc3987">RFC 3987: Internationalized Resource Identifiers</a>
 * to be able to represent the query in a user-friendly and concise IRI format.
 * We determine this based on the following information:
 * </p>
 * <ol>
 * <li><a href="https://tools.ietf.org/html/rfc3987#section-6.4">RFC 3987: 6.4.  Use of UTF-8 for Encoding Original Characters</a>:
 *   <blockquote>
 *     Similar considerations apply to query parts.  The functionality of
 *     IRIs (namely, to be able to include non-ASCII characters) can only be
 *     used if the query part is encoded in UTF-8.
 *   </blockquote>
 * </li>
 * <li><a href="https://dev.w3.org/html5/spec-LC/urls.html#terminology-0">HTML 5: 2.6.1 Terminology</a>:
 *   <blockquote>
 *     The URL is a valid IRI reference and its query component contains no unescaped non-ASCII characters. [RFC3987]<br />
 *     The URL is a valid IRI reference and the character encoding of the URL's Document is UTF-8 or UTF-16. [RFC3987]
 *   </blockquote>
 * </li>
 * <li><a href="https://dev.w3.org/html5/spec-LC/urls.html#resolving-urls">HTML 5: 2.6.3 Resolving URLs</a>:
 *   <blockquote>
 *     Let encoding be determined as follows:<br />
 *     If the URL came from a DOM node (e.g. from an element)<br />
 *     The node has a Document, and the URL character encoding is the document's character encoding.
 *   </blockquote>
 * </li>
 * </ol>
 * <p>
 * To avoid all this nonsense, just do everything, everywhere, in UTF-8, always,
 * which is what we do.
 * </p>
 * <p>
 * TODO: Find something that does this well already. 
 * <a href="https://jena.apache.org/documentation/notes/iri.html">jena-iri</a>?
 * <a href="https://github.com/xbib/net>org.xbib:net-url</a>?
 * <a href="https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/utils/URIBuilder.html">URIBuilder</a>?
 * </p>
 * 
 * @see URI
 * @see URIParser
 *
 * @author  AO Industries, Inc.
 */
public enum URIComponent {

	/**
	 * The base - everything before the first '?' or '#' (exclusive).  This may
	 * include scheme, hier-part (host, port, path, and such), which this
	 * class is not concerned with.
	 * <p>
	 * UTF-8 encoding always.
	 * </p>
	 */
	BASE {
		@Override
		@SuppressWarnings("deprecation")
		public String encode(String s, String documentEncoding) {
			return URIEncoder.encodeURIComponent(s);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String s, String documentEncoding, Appendable out) throws IOException {
			URIEncoder.encodeURIComponent(s, out);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String s, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			URIEncoder.encodeURIComponent(s, out, encoder);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String s, String documentEncoding, StringBuilder sb) {
			URIEncoder.encodeURIComponent(s, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String s, String documentEncoding, StringBuffer sb) {
			URIEncoder.encodeURIComponent(s, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public String decode(String s, String documentEncoding) {
			return URIDecoder.decodeURIComponent(s);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String s, String documentEncoding, Appendable out) throws IOException {
			URIDecoder.decodeURIComponent(s, out);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String s, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			URIDecoder.decodeURIComponent(s, out, encoder);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String s, String documentEncoding, StringBuilder sb) {
			URIDecoder.decodeURIComponent(s, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String s, String documentEncoding, StringBuffer sb) {
			URIDecoder.decodeURIComponent(s, sb);
		}

		@Override
		public void encodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			encode(uri.substring(start, end), documentEncoding, out, encoder);
		}

		@Override
		public void decodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			// TODO: A specialized form of decode that skips decoding to reserved characters would be better than decode/re-encode.
			//       This implementation is less precise, such as converting lower-case percent-encoded to upper-case.
			encodeRfc3968ReservedCharacters_and_percent(decode(uri.substring(start, end), documentEncoding), out, encoder);
		}

		@Override
		public URIComponent nextStage(char reserved) {
			if(reserved == '?') return QUERY;
			if(reserved == '#') return FRAGMENT;
			return BASE;
		}
	},

	/**
	 * The query - everything after the first '?' (exclusive) and the fragment '#' (exclusive).
	 * <p>
	 * Encoded in the document encoding.
	 * </p>
	 */
	QUERY {
		@Override
		@SuppressWarnings("deprecation")
		public String encode(String param, String documentEncoding) throws UnsupportedEncodingException {
			return URIEncoder.encodeURIComponent(param, documentEncoding);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String param, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException {
			URIEncoder.encodeURIComponent(param, documentEncoding, out);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String param, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
			URIEncoder.encodeURIComponent(param, documentEncoding, out, encoder);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String param, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException {
			URIEncoder.encodeURIComponent(param, documentEncoding, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String param, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException {
			URIEncoder.encodeURIComponent(param, documentEncoding, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public String decode(String param, String documentEncoding) throws UnsupportedEncodingException {
			return URIDecoder.decodeURIComponent(param, documentEncoding);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String param, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException {
			URIDecoder.decodeURIComponent(param, documentEncoding, out);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String param, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
			URIDecoder.decodeURIComponent(param, documentEncoding, out, encoder);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String param, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException {
			URIDecoder.decodeURIComponent(param, documentEncoding, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String param, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException {
			URIDecoder.decodeURIComponent(param, documentEncoding, sb);
		}

		private boolean isUTF8(String documentEncoding) {
			if(documentEncoding.equals(StandardCharsets.UTF_8.name())) return true;
			Charset queryCharset = Charset.forName(documentEncoding);
			return
				queryCharset == StandardCharsets.UTF_8
				|| queryCharset.name().equals(StandardCharsets.UTF_8.name());
		}

		/**
		 * UTF-8 encoding when document encoding is UTF-8, otherwise query left unaltered.
		 */
		@Override
		public void encodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
			if(isUTF8(documentEncoding)) {
				encode(uri.substring(start, end), documentEncoding, out, encoder);
			} else {
				// leave unaltered
				if(encoder == null) {
					out.append(uri, start, end);
				} else {
					encoder.append(uri, start, end, out);
				}
			}
		}

		/**
		 * UTF-8 encoding when document encoding is UTF-8, otherwise query left unaltered.
		 */
		@Override
		public void decodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
			if(isUTF8(documentEncoding)) {
				// TODO: A specialized form of decode that skips decoding to reserved characters would be better than decode/re-encode.
				//       This implementation is less precise, such as converting lower-case percent-encoded to upper-case.
				encodeRfc3968ReservedCharacters_and_percent(decode(uri.substring(start, end), documentEncoding), out, encoder);
			} else {
				// leave unaltered
				if(encoder == null) {
					out.append(uri, start, end);
				} else {
					encoder.append(uri, start, end, out);
				}
			}
		}

		@Override
		public URIComponent nextStage(char reserved) {
			if(reserved == '#') return FRAGMENT;
			return QUERY;
		}
	},

	/**
	 * The fragment - everything after the first '#' (exclusive)
	 * <p>
	 * UTF-8 encoding always.
	 * </p>
	 * <p>
	 * TODO: Implement specification of <a href="https://dev.w3.org/html5/spec-LC/urls.html#url-manipulation-and-creation">fragment-escape</a>.
	 * </p>
	 */
	FRAGMENT {
		@Override
		@SuppressWarnings("deprecation")
		public String encode(String fragment, String documentEncoding) {
			return URIEncoder.encodeURIComponent(fragment);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String fragment, String documentEncoding, Appendable out) throws IOException {
			URIEncoder.encodeURIComponent(fragment, out);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String fragment, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			URIEncoder.encodeURIComponent(fragment, out, encoder);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String fragment, String documentEncoding, StringBuilder sb) {
			URIEncoder.encodeURIComponent(fragment, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void encode(String fragment, String documentEncoding, StringBuffer sb) {
			URIEncoder.encodeURIComponent(fragment, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public String decode(String fragment, String documentEncoding) {
			return URIDecoder.decodeURIComponent(fragment);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String fragment, String documentEncoding, Appendable out) throws IOException {
			URIDecoder.decodeURIComponent(fragment, out);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String fragment, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			URIDecoder.decodeURIComponent(fragment, out, encoder);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String fragment, String documentEncoding, StringBuilder sb) {
			URIDecoder.decodeURIComponent(fragment, sb);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void decode(String fragment, String documentEncoding, StringBuffer sb) {
			URIDecoder.decodeURIComponent(fragment, sb);
		}

		@Override
		public void encodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			encode(uri.substring(start, end), documentEncoding, out, encoder);
		}

		@Override
		public void decodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws IOException {
			// TODO: A specialized form of decode that skips decoding to reserved characters would be better than decode/re-encode.
			//       This implementation is less precise, such as converting lower-case percent-encoded to upper-case.
			encodeRfc3968ReservedCharacters_and_percent(decode(uri.substring(start, end), documentEncoding), out, encoder);
		}

		@Override
		public URIComponent nextStage(char reserved) {
			return FRAGMENT;
		}
	};

	/**
	 * Percent-encodes reserved characters (and '%' for already percent-encoded) only.
	 *
	 * @param encoder  An optional encoder the output is applied through
	 */
	// TODO: Implement as streaming encoder
	private static void encodeRfc3968ReservedCharacters_and_percent(String value, Appendable out, Encoder encoder) throws IOException {
		int len = value.length();
		for(int i = 0; i < len; i++) {
			char ch = value.charAt(i);
			String replacement;
			switch(ch) {
				// gen-delims
				case ':' :
					replacement = "%3A";
					break;
				case '/' :
					replacement = "%2F";
					break;
				case '?' :
					replacement = "%3F";
					break;
				case '#' :
					replacement = "%23";
					break;
				case '[' :
					replacement = "%5B";
					break;
				case ']' :
					replacement = "%5D";
					break;
				case '@' :
					replacement = "%40";
					break;
				// sub-delims
				case '!' :
					replacement = "%21";
					break;
				case '$' :
					replacement = "%24";
					break;
				case '&' :
					replacement = "%26";
					break;
				case '\'' :
					replacement = "%27";
					break;
				case '(' :
					replacement = "%28";
					break;
				case ')' :
					replacement = "%29";
					break;
				case '*' :
					replacement = "%2A";
					break;
				case '+' :
					replacement = "%2B";
					break;
				case ',' :
					replacement = "%2C";
					break;
				case ';' :
					replacement = "%3B";
					break;
				case '=' :
					replacement = "%3D";
					break;
				// already percent-encoded
				case '%' :
					replacement = "%25";
					break;
				default :
					replacement = null;
			}
			if(replacement != null) {
				if(encoder == null) {
					out.append(replacement);
				} else {
					encoder.append(replacement, out);
				}
			} else {
				if(encoder == null) {
					out.append(ch);
				} else {
					encoder.append(ch, out);
				}
			}
		}
	}

	/**
	 * Encodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decode(java.lang.String, java.lang.String)
	 */
	abstract public String encode(String s, String documentEncoding) throws UnsupportedEncodingException;

	/**
	 * Encodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decode(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	abstract public void encode(String s, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException;

	/**
	 * Encodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decode(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	abstract public void encode(String s, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException;

	/**
	 * Encodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decode(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	abstract public void encode(String s, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException;

	/**
	 * Encodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decode(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	abstract public void encode(String s, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException;

	/**
	 * Decodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encode(java.lang.String, java.lang.String)
	 */
	abstract public String decode(String s, String documentEncoding) throws UnsupportedEncodingException;

	/**
	 * Decodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encode(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	abstract public void decode(String s, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException;

	/**
	 * Decodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encode(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	abstract public void decode(String s, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException;

	/**
	 * Decodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encode(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	abstract public void decode(String s, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException;

	/**
	 * Decodes a value in a given encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encode(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	abstract public void decode(String s, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException;

	/**
	 * Encodes for the current stage and the given document encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 */
	abstract public void encodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException;

	/**
	 * Decodes for the current stage and the given document encoding.
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 */
	abstract public void decodeUnreserved(String uri, int start, int end, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException;

	/**
	 * Gets the next stage for the given reserved character.
	 */
	abstract public URIComponent nextStage(char reserved);
}
