/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.net;

import com.aoapps.lang.io.Encoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Extremely minimal representation of an <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 URI</a>
 * or <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 IRI</a>,
 * optimized for altering the path, query, or fragment for URI rewriting.
 * <p>
 * This only deals with four parts of the URI:
 * </p>
 * <ol>
 *   <li>scheme - everything before the first ':' (exclusive).</li>
 *   <li>hier-part - everything after the scheme and before the first '?' or '#' (exclusive).
 *       This may include host, port, path, and such, which this class is not concerned with.
 *   </li>
 *   <li>query - everything after the first '?' (exclusive) and the fragment '#' (exclusive)</li>
 *   <li>fragment - everything after the first '#' (exclusive)</li>
 * </ol>
 * <p>
 * This class specifically:
 * </p>
 * <ol>
 * <li>Does not do significant amounts of normalization</li>
 * <li>Does not support any relative path resolution</li>
 * <li>Does not do any scheme-specific validation</li>
 * <li>Does not thoroughly detect malformed URIs</li>
 * </ol>
 * <p>
 * Instances of this class are immutable and thus thread-safe.  Mutating
 * operations return a new instance.
 * </p>
 * <p>
 * When a strict ASCII-only representation of a <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 URI</a>
 * is required, use {@link URI}.  When a Unicode representation of a <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 IRI</a>
 * is preferred, use {@link IRI}.  Otherwise, to support both, use {@link AnyURI}, which should also perform
 * the best since it performs fewer conversions.
 * </p>
 * <hr>
 * <p>
 * Encoding and decoding is always done in UTF-8.  This choice is supported by
 * <a href="https://www.w3.org/International/wiki/IRIStatus">IRIStatus - Query encoding</a>,
 * and is consistent with {@link java.net.URI}.
 * </p>
 * <p>
 * This simplification allows us to no longer pass encoding around and no longer
 * throw any {@link UnsupportedEncodingException} as UTF-8 is a
 * {@linkplain StandardCharsets#UTF_8 standard character set}.
 * </p>
 * <p>
 * We do not support the use of any encoding other than UTF-8, which allows us
 * to avoid all the gray zones of the various protocol specifications, versions,
 * and implementations.
 * </p>
 * <hr>
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
 * <li><a href="https://github.com/xbib/net">org.xbib:net-url</a></li>
 * </ol>
 * <hr>
 * <p>Further reading:</p>
 * <ol>
 * <li><a href="https://www.w3.org/International/wiki/IRIStatus">IRIStatus - Query encoding</a>:
 *   <blockquote>
 *     Update 2015-08-25: The URL spec defines this formally. By default the query string
 *     uses UTF-8. X-Form's (defined by HTML) allows the page author to supply the override
 *     legacy character encoding if needed (UTF-8 is encouraged). If an override is used,
 *     there may be nothing in the URL itself that indicates what this override encoding
 *     is: the receiver just has to know.
 *   </blockquote>
 * </li>
 * <li><a href="https://tools.ietf.org/html/rfc3987#section-6.4">RFC 3987: 6.4.  Use of UTF-8 for Encoding Original Characters</a>:
 *   <blockquote>
 *     Similar considerations apply to query parts.  The functionality of
 *     IRIs (namely, to be able to include non-ASCII characters) can only be
 *     used if the query part is encoded in UTF-8.
 *   </blockquote>
 * </li>
 * <li><a href="https://dev.w3.org/html5/spec-LC/urls.html#terminology-0">HTML 5: 2.6.1 Terminology</a>:
 *   <blockquote>
 *     The URL is a valid IRI reference and its query component contains no unescaped non-ASCII characters. [RFC3987]<br>
 *     The URL is a valid IRI reference and the character encoding of the URL's Document is UTF-8 or UTF-16. [RFC3987]
 *   </blockquote>
 * </li>
 * <li><a href="https://dev.w3.org/html5/spec-LC/urls.html#resolving-urls">HTML 5: 2.6.3 Resolving URLs</a>:
 *   <blockquote>
 *     Let encoding be determined as follows:<br>
 *     If the URL came from a DOM node (e.g. from an element)<br>
 *     The node has a Document, and the URL character encoding is the document's character encoding.
 *   </blockquote>
 * </li>
 * </ol>
 * 
 * @see URI
 * @see URIParser
 * @see java.net.URI
 *
 * @author  AO Industries, Inc.
 */
public class AnyURI {

	final String uri;

	/**
	 * The length of the scheme or {@code -1} when there is no scheme.
	 */
	private final int schemeLength;

	/**
	 * The index of the {@code '?'} marking the query or {@code -1} when there is no query.
	 */
	private final int queryIndex;

	/**
	 * The index of the {@code '#'} marking the fragment or {@code -1} when there is no fragment.
	 */
	private final int fragmentIndex;

	// TODO: Thread-local AnyURI/URI/IRI LRU instance caches, getIRI(String) instead of constructor?
	public AnyURI(String uri) {
		this.uri = uri;
		schemeLength = URIParser.getSchemeLength(uri);
		// Find first of '?' or '#'
		int pathEnd = URIParser.getPathEnd(
			uri,
			schemeLength + 1 // Works for -1 (no colon) or index of colon itself
		);
		if(pathEnd >= uri.length()) {
			queryIndex = -1;
			fragmentIndex = -1;
		} else if(uri.charAt(pathEnd) == '?') {
			queryIndex = pathEnd;
			fragmentIndex = uri.indexOf('#', pathEnd + 1);
		} else {
			assert uri.charAt(pathEnd) == '#';
			queryIndex = -1;
			fragmentIndex = pathEnd;
		}
	}

	AnyURI(String uri, int schemeLength, int queryIndex, int fragmentIndex) {
		this.uri = uri;
		this.schemeLength = schemeLength;
		this.queryIndex = queryIndex;
		this.fragmentIndex = fragmentIndex;
		assert equals(new AnyURI(uri)) : "Split after mutations must be equal to splitting in public constructor";
	}

	/**
	 * Create a new object of this same type as this object.  This is used
	 * by modifiers.
	 */
	AnyURI newAnyURI(String uri, boolean isEncodingNormalized, int schemeLength, int queryIndex, int fragmentIndex) {
		return new AnyURI(uri, schemeLength, queryIndex, fragmentIndex);
	}

	/**
	 * Gets the full URI.
	 * <p>
	 * This may be a mixture of <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 URI</a>
	 * US-ASCII and <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 IRI</a> Unicode
	 * formats.
	 * </p>
	 * <p>
	 * This might not be {@linkplain #isEncodingNormalized() percent-encoding normalized}.
	 * Use {@link #toIRI()}.{@link IRI#toString() toString()} or {@link #toIRI()}.{@link IRI#toURI() toURI()}.{@link URI#toString() toString()}
	 * if consistent formatting is required.
	 * </p>
	 */
	@Override
	public String toString() {
		return uri;
	}

	/**
	 * Gets the full URI in <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 URI</a>
	 * US-ASCII format.
	 * <p>
	 * This might not be {@linkplain #isEncodingNormalized() percent-encoding normalized}.
	 * Use {@link #toIRI()}.{@link IRI#toASCIIString() toASCIIString()}
	 * if consistent formatting is required.
	 * </p>
	 */
	public String toASCIIString() {
		return toURI().toASCIIString();
	}

	/**
	 * Compares the {@link #uri URI} directly.  No encoding or decoding
	 * is performed.  This does not compare URIs semantically.
	 */
	@Override
	final public boolean equals(Object obj) {
		if(!(obj instanceof AnyURI)) return false;
		AnyURI other = (AnyURI)obj;
		if(this == other) {
			return true;
		} else if(uri.equals(other.uri)) {
			assert schemeLength == other.schemeLength : "uri equal with schemeLength mismatch: uri = " + uri + ", this.schemeLength = " + this.schemeLength + ", other.schemeLength = " + other.schemeLength;
			assert queryIndex == other.queryIndex : "uri equal with queryIndex mismatch: uri = " + uri + ", this.queryIndex = " + this.queryIndex + ", other.queryIndex = " + other.queryIndex;
			assert fragmentIndex == other.fragmentIndex : "uri equal with fragmentIndex mismatch: uri = " + uri + ", this.fragmentIndex = " + this.fragmentIndex + ", other.fragmentIndex = " + other.fragmentIndex;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The hash code is the same as the hash code of the uri.
	 *
	 * @see  #toString()
	 * @see  String#hashCode()
	 */
	@Override
	final public int hashCode() {
		return uri.hashCode();
	}

	/**
	 * Gets the length of the scheme or {@code -1} when there is no scheme.
	 * This is also the index of the colon (':') that ends the scheme.
	 *
	 * @return  the index of the ':' marking the end of the scheme or {@code -1} when there is no scheme.
	 */
	public int getSchemeLength() {
		return schemeLength;
	}

	/**
	 * Checks if this has a scheme.
	 */
	public boolean hasScheme() {
		return schemeLength != -1;
	}

	/**
	 * Checks if a URI starts with the given scheme.
	 *
	 * @param scheme  The scheme to look for, not including colon.
	 *                For example {@code "http"}.  When {@code null},
	 *                with match a URI without a scheme.
	 *
	 * @throws IllegalArgumentException when {@code scheme} is determined to be invalid.
	 *         Please note that this determination is not guaranteed as shortcuts may
	 *         skip individual character comparisons.
	 */
	public boolean isScheme(String scheme) throws IllegalArgumentException {
		if(scheme == null) {
			return schemeLength == -1;
		}
		int len = scheme.length();
		if(len == 0) {
			throw new IllegalArgumentException("Invalid scheme: " + scheme);
		}
		if(len != schemeLength) {
			return false;
		}
		for(int i = 0; i < schemeLength; i++) {
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
		return true;
	}

	/**
	 * Gets the scheme for a URI, or {@code null} when has no scheme.
	 * An empty scheme will never be returned (if the URI starts with ':').
	 * <p>
	 * This method may involve string manipulation, favor the <code>writeScheme(…)</code>
	 * and <code>appendScheme(…)</code> methods when appropriate.
	 * </p>
	 *
	 * @return  The scheme, not including colon, or {@code null} when there is no scheme.
	 *          For example {@code "http"}.
	 */
	public String getScheme() {
		if(schemeLength == -1) return null;
		return uri.substring(0, schemeLength);
	}

	/**
	 * Writes the scheme (not including the ':').
	 */
	public void writeScheme(Writer out) throws IOException {
		if(schemeLength != -1) {
			out.write(uri, 0, schemeLength);
		}
	}

	/**
	 * Writes the scheme (not including the ':').
	 */
	public void writeScheme(Writer out, Encoder encoder) throws IOException {
		if(schemeLength != -1) {
			if(encoder == null) {
				writeScheme(out);
			} else {
				encoder.write(uri, 0, schemeLength, out);
			}
		}
	}

	/**
	 * Appends the scheme (not including the ':').
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendScheme(Appendable out) throws IOException {
		if(schemeLength != -1) {
			out.append(uri, 0, schemeLength);
		}
		return this;
	}

	/**
	 * Appends the scheme (not including the ':').
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendScheme(Encoder encoder, Appendable out) throws IOException {
		if(schemeLength != -1) {
			if(encoder == null) {
				appendScheme(out);
			} else {
				encoder.append(uri, 0, schemeLength, out);
			}
		}
		return this;
	}

	/**
	 * Appends the scheme (not including the ':').
	 *
	 * @return  The {@link StringBuilder} {@code sb}
	 */
	public StringBuilder appendScheme(StringBuilder sb) {
		if(schemeLength != -1) {
			sb.append(uri, 0, schemeLength);
		}
		return sb;
	}

	/**
	 * Appends the scheme (not including the ':').
	 *
	 * @return  The {@link StringBuffer} {@code sb}
	 */
	public StringBuffer appendScheme(StringBuffer sb) {
		if(schemeLength != -1) {
			sb.append(uri, 0, schemeLength);
		}
		return sb;
	}

	// TODO: More details between scheme and path end?

	/**
	 * Gets the path end within this URI.
	 *
	 * @return  the index of the first '?' or '#' (exclusive), or the length of the URI when neither found.
	 */
	public int getPathEnd() {
		if(queryIndex != -1) return queryIndex;
		else if(fragmentIndex != -1) return fragmentIndex;
		else return uri.length();
	}

	/**
	 * Checks if the path ends with the given value.
	 *
	 * @see String#regionMatches(int, java.lang.String, int, int)
	 */
	public boolean pathEndsWith(String suffix) {
		int suffixLen = suffix.length();
		int pathStart = getPathEnd() - suffixLen;
		return
			pathStart > schemeLength // Handles both -1 and colon position
			&& uri.regionMatches(pathStart, suffix, 0, suffixLen);
	}

	/**
	 * Checks if the path ends with the given value, case-insensitive.
	 *
	 * @see String#regionMatches(boolean, int, java.lang.String, int, int)
	 */
	public boolean pathEndsWithIgnoreCase(String suffix) {
		int suffixLen = suffix.length();
		int pathStart = getPathEnd() - suffixLen;
		return
			pathStart > schemeLength // Handles both -1 and colon position
			&& uri.regionMatches(true, pathStart, suffix, 0, suffixLen);
	}

	/**
	 * Gets the hier-part - everything after the scheme and before the first '?' or '#' (exclusive).  This may
	 * include host, port, path, and such, which this class is not concerned with.
	 * <p>
	 * This method may involve string manipulation, favor the <code>writeHierPart(…)</code>
	 * and <code>appendHierPart(…)</code> methods when appropriate.
	 * </p>
	 *
	 * @return  the part of the URI after the scheme and up to the first '?' or '#' (exclusive), or the full URI when neither found.
	 */
	public String getHierPart() {
		if(queryIndex != -1) return uri.substring(schemeLength + 1, queryIndex);
		else if(fragmentIndex != -1) return uri.substring(schemeLength + 1, fragmentIndex);
		else if(schemeLength == -1) return uri;
		else return uri.substring(schemeLength + 1);
	}

	/**
	 * Writes the part of the URI after the scheme and up to the first '?' or '#' (exclusive), or the full URI when neither found.
	 */
	public void writeHierPart(Writer out) throws IOException {
		if(queryIndex != -1) {
			int off = schemeLength + 1;
			out.write(uri, off, queryIndex - off);
		} else if(fragmentIndex != -1) {
			int off = schemeLength + 1;
			out.write(uri, off, fragmentIndex - off);
		} else if(schemeLength == -1) {
			out.write(uri);
		} else {
			int off = schemeLength + 1;
			out.write(uri, off, uri.length() - off);
		}
	}

	/**
	 * Writes the part of the URI after the scheme and up to the first '?' or '#' (exclusive), or the full URI when neither found.
	 */
	public void writeHierPart(Writer out, Encoder encoder) throws IOException {
		if(encoder == null) {
			writeHierPart(out);
		} else {
			if(queryIndex != -1) {
				int off = schemeLength + 1;
				encoder.write(uri, off, queryIndex - off, out);
			} else if(fragmentIndex != -1) {
				int off = schemeLength + 1;
				encoder.write(uri, off, fragmentIndex - off, out);
			} else if(schemeLength == -1) {
				encoder.write(uri, out);
			} else {
				int off = schemeLength + 1;
				encoder.write(uri, off, uri.length() - off, out);
			}
		}
	}

	/**
	 * Appends the part of the URI after the scheme and up to the first '?' or '#' (exclusive), or the full URI when neither found.
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendHierPart(Appendable out) throws IOException {
		if(queryIndex != -1) out.append(uri, schemeLength + 1, queryIndex);
		else if(fragmentIndex != -1) out.append(uri, schemeLength + 1, fragmentIndex);
		else if(schemeLength == -1) out.append(uri);
		else out.append(uri, schemeLength + 1, uri.length());
		return this;
	}

	/**
	 * Appends the part of the URI after the scheme and up to the first '?' or '#' (exclusive), or the full URI when neither found.
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendHierPart(Encoder encoder, Appendable out) throws IOException {
		if(encoder == null) {
			appendHierPart(out);
		} else {
			if(queryIndex != -1) encoder.append(uri, schemeLength + 1, queryIndex, out);
			else if(fragmentIndex != -1) encoder.append(uri, schemeLength + 1, fragmentIndex, out);
			else if(schemeLength == -1) encoder.append(uri, out);
			else encoder.append(uri, schemeLength + 1, uri.length(), out);
		}
		return this;
	}

	/**
	 * Appends the part of the URI after the scheme and up to the first '?' or '#' (exclusive), or the full URI when neither found.
	 *
	 * @return  The {@link StringBuilder} {@code sb}
	 */
	public StringBuilder appendHierPart(StringBuilder sb) {
		if(queryIndex != -1) return sb.append(uri, schemeLength + 1, queryIndex);
		else if(fragmentIndex != -1) return sb.append(uri, schemeLength + 1, fragmentIndex);
		else if(schemeLength == -1) return sb.append(uri);
		else return sb.append(uri, schemeLength + 1, uri.length());
	}

	/**
	 * Appends the part of the URI after the scheme and up to the first '?' or '#' (exclusive), or the full URI when neither found.
	 *
	 * @return  The {@link StringBuffer} {@code sb}
	 */
	public StringBuffer appendHierPart(StringBuffer sb) {
		if(queryIndex != -1) return sb.append(uri, schemeLength + 1, queryIndex);
		else if(fragmentIndex != -1) return sb.append(uri, schemeLength + 1, fragmentIndex);
		else if(schemeLength == -1) return sb.append(uri);
		else return sb.append(uri, schemeLength + 1, uri.length());
	}

	/**
	 * Gets the index of the query marker ('?').
	 *
	 * @return  the index of the '?' marking the query string or {@code -1} when there is no query string.
	 */
	public int getQueryIndex() {
		return queryIndex;
	}

	/**
	 * Checks if this has a query.
	 */
	public boolean hasQuery() {
		return queryIndex != -1;
	}

	/**
	 * Gets the query string.
	 * <p>
	 * This method may involve string manipulation, favor the <code>writeQueryString(…)</code>
	 * and <code>appendQuery(…)</code> methods when appropriate.
	 * </p>
	 *
	 * @return  the query string (not including the '?') or {@code null} when there is no query.
	 */
	public String getQueryString() {
		if(queryIndex == -1) return null;
		int queryStart = queryIndex + 1;
		if(fragmentIndex == -1) {
			return uri.substring(queryStart);
		} else {
			return uri.substring(queryStart, fragmentIndex);
		}
	}

	/**
	 * Writes the query string (not including the '?').
	 */
	public void writeQueryString(Writer out) throws IOException {
		if(queryIndex != -1) {
			int queryStart = queryIndex + 1;
			out.write(
				uri,
				queryStart,
				(fragmentIndex == -1 ? uri.length() : fragmentIndex) - queryStart
			);
		}
	}

	/**
	 * Writes the query string (not including the '?').
	 */
	public void writeQueryString(Writer out, Encoder encoder) throws IOException {
		if(queryIndex != -1) {
			if(encoder == null) {
				writeQueryString(out);
			} else {
				int queryStart = queryIndex + 1;
				encoder.write(
					uri,
					queryStart,
					(fragmentIndex == -1 ? uri.length() : fragmentIndex) - queryStart,
					out
				);
			}
		}
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendQueryString(Appendable out) throws IOException {
		if(queryIndex != -1) {
			out.append(
				uri,
				queryIndex + 1,
				fragmentIndex == -1 ? uri.length() : fragmentIndex
			);
		}
		return this;
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendQueryString(Encoder encoder, Appendable out) throws IOException {
		if(queryIndex != -1) {
			if(encoder == null) {
				appendQueryString(out);
			} else {
				encoder.append(
					uri,
					queryIndex + 1,
					fragmentIndex == -1 ? uri.length() : fragmentIndex,
					out
				);
			}
		}
		return this;
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  The {@link StringBuilder} {@code sb}
	 */
	public StringBuilder appendQueryString(StringBuilder sb) {
		if(queryIndex != -1) {
			sb.append(
				uri,
				queryIndex + 1,
				fragmentIndex == -1 ? uri.length() : fragmentIndex
			);
		}
		return sb;
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  The {@link StringBuffer} {@code sb}
	 */
	public StringBuffer appendQueryString(StringBuffer sb) {
		if(queryIndex != -1) {
			sb.append(
				uri,
				queryIndex + 1,
				fragmentIndex == -1 ? uri.length() : fragmentIndex
			);
		}
		return sb;
	}

	/**
	 * Gets the index of the fragment marker ('#').
	 *
	 * @return  the index of the '#' marking the fragment or {@code -1} when there is no fragment.
	 */
	public int getFragmentIndex() {
		return fragmentIndex;
	}

	/**
	 * Checks if this has an fragment.
	 */
	public boolean hasFragment() {
		return fragmentIndex != -1;
	}

	/**
	 * Gets the fragment.
	 * <p>
	 * This method may involve string manipulation, favor the <code>writeFragment(…)</code>
	 * and <code>appendFragment(…)</code> methods when appropriate.
	 * </p>
	 *
	 * @return  the fragment (not including the '#') or {@code null} when there is no fragment.
	 */
	public String getFragment() {
		return (fragmentIndex == -1) ? null : uri.substring(fragmentIndex + 1);
	}

	/**
	 * Writes the fragment (not including the '#').
	 */
	public void writeFragment(Writer out) throws IOException {
		if(fragmentIndex != -1) {
			int fragmentStart = fragmentIndex + 1;
			out.write(uri, fragmentStart, uri.length() - fragmentStart);
		}
	}

	/**
	 * Writes the fragment (not including the '#').
	 */
	public void writeFragment(Writer out, Encoder encoder) throws IOException {
		if(fragmentIndex != -1) {
			if(encoder == null) {
				AnyURI.this.writeFragment(out);
			} else {
				int fragmentStart = fragmentIndex + 1;
				encoder.write(uri, fragmentStart, uri.length() - fragmentStart, out);
			}
		}
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendFragment(Appendable out) throws IOException {
		if(fragmentIndex != -1) {
			out.append(uri, fragmentIndex + 1, uri.length());
		}
		return this;
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  {@code this}
	 */
	public AnyURI appendFragment(Encoder encoder, Appendable out) throws IOException {
		if(fragmentIndex != -1) {
			if(encoder == null) {
				AnyURI.this.appendFragment(out);
			} else {
				encoder.append(uri, fragmentIndex + 1, uri.length(), out);
			}
		}
		return this;
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  The {@link StringBuilder} {@code sb}
	 */
	public StringBuilder appendFragment(StringBuilder sb) {
		if(fragmentIndex != -1) {
			sb.append(uri, fragmentIndex + 1, uri.length());
		}
		return sb;
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  The {@link StringBuffer} {@code sb}
	 */
	public StringBuffer appendFragment(StringBuffer sb) {
		if(fragmentIndex != -1) {
			sb.append(uri, fragmentIndex + 1, uri.length());
		}
		return sb;
	}

	/**
	 * Is this URI percent-encoding normalized?
	 * Normalized percent encoding means it will have only the required percent encodings,
	 * and the encodings are capitalized hexadecimal.
	 * <p>
	 * Note: This only refers to the percent encodings.  This is not related to
	 * full {@linkplain java.net.URI#normalize() URI normalization}.
	 * </p>
	 */
	public boolean isEncodingNormalized() {
		return false;
	}

	@SuppressWarnings("PackageVisibleField")
	volatile URI toURICache;

	/**
	 * Gets this URI encoded in <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 URI</a>
	 * US-ASCII format.
	 * <p>
	 * This might not be {@linkplain #isEncodingNormalized() percent-encoding normalized}.
	 * Use {@link #toIRI()}.{@link IRI#toURI() toURI()} if consistent formatting is required.
	 * </p>
	 *
	 * @return  The {@link URI} or {@code this} when unmodified.
	 *
	 * @see  URIEncoder#encodeURI(java.lang.String)
	 */
	public URI toURI() {
		URI toUri = toURICache;
		if(toUri != null) return toUri;
		toUri = new URI(uri, isEncodingNormalized());
		toUri.toIRICache = toIRICache;
		this.toURICache = toUri;
		return toUri;
	}

	@SuppressWarnings("PackageVisibleField")
	volatile IRI toIRICache;

	/**
	 * Gets this URI encoded in <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 IRI</a>
	 * Unicode format.
	 *
	 * @return  The {@link IRI} or {@code this} when unmodified.
	 *
	 * @see  URIDecoder#decodeURI(java.lang.String)
	 */
	public IRI toIRI() {
		IRI toIri = toIRICache;
		if(toIri != null) return toIri;
		toIri = new IRI(uri);
		// IRI.toURI() gives a more normalized output, which may be different than previously cached calls to AnyURI.toURI()
		URI toUri = toURICache;
		if(toUri != null && toUri.isEncodingNormalized()) {
			toIri.toURICache = toUri;
		}
		this.toIRICache = toIri;
		return toIri;
	}

	// TODO: setScheme?

	AnyURI setHierPartImpl(String hierPart, boolean hierPartEncodingNormalized) {
		final AnyURI newAnyURI;
		int hierPartLen = hierPart.length();
		int pathEnd = getPathEnd();
		// Look for not changed
		if(
			hierPartLen == (pathEnd - (schemeLength + 1))
			&& uri.regionMatches(schemeLength + 1, hierPart, 0, hierPartLen)
		) {
			// Not changed
			newAnyURI = this;
		} else {
			if(queryIndex == -1) {
				if(fragmentIndex == -1) {
					if(schemeLength == -1) {
						// Hier-part only
						newAnyURI = newAnyURI(
							hierPart,
							hierPartEncodingNormalized, // May promote to encoding normalized
							-1,
							-1,
							-1
						);
					} else {
						// Schema and hier-part
						int newUriLen = schemeLength + 1 + hierPartLen;
						StringBuilder newUri = new StringBuilder(newUriLen);
						newUri.append(uri, 0, schemeLength + 1).append(hierPart);
						assert newUri.length() == newUriLen;
						newAnyURI = newAnyURI(
							newUri.toString(),
							hierPartEncodingNormalized, // May promote to encoding normalized; assuming schemas never percent-encoded
							schemeLength,
							-1,
							-1
						);
					}
				} else {
					// Fragment only
					int uriLen = uri.length();
					int newUriLen = schemeLength + 1 + hierPartLen + (uriLen - fragmentIndex);
					StringBuilder newUri = new StringBuilder(newUriLen);
					newUri.append(uri, 0, schemeLength + 1).append(hierPart).append(uri, fragmentIndex, uriLen);
					assert newUri.length() == newUriLen;
					newAnyURI = newAnyURI(
						newUri.toString(),
						hierPartEncodingNormalized && isEncodingNormalized(),
						schemeLength,
						-1,
						schemeLength + 1 + hierPartLen
					);
				}
			} else {
				int uriLen = uri.length();
				int newUriLen = schemeLength + 1 + hierPartLen + (uriLen - queryIndex);
				StringBuilder newUri = new StringBuilder(newUriLen);
				newUri.append(uri, 0, schemeLength + 1).append(hierPart).append(uri, queryIndex, uriLen);
				assert newUri.length() == newUriLen;
				if(fragmentIndex == -1) {
					// Query only
					newAnyURI = newAnyURI(
						newUri.toString(),
						hierPartEncodingNormalized && isEncodingNormalized(),
						schemeLength,
						schemeLength + 1 + hierPartLen,
						-1
					);
				} else {
					// Query and fragment
					newAnyURI = newAnyURI(
						newUri.toString(),
						hierPartEncodingNormalized && isEncodingNormalized(),
						schemeLength,
						schemeLength + 1 + hierPartLen,
						fragmentIndex + (schemeLength + 1 + hierPartLen - queryIndex)
					);
				}
			}
		}
		assert newAnyURI.getHierPart().equals(hierPart);
		return newAnyURI;
	}

	/**
	 * Replaces the hier-part.
	 *
	 * @param hierPart  The hier-part may not contain the query marker '?' or fragment marker '#'
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 */
	public AnyURI setHierPart(String hierPart) {
		return setHierPartImpl(hierPart, false);
	}

	AnyURI setQueryStringImpl(String query, boolean queryEncodingNormalized) {
		final AnyURI newAnyURI;
		if(query == null) {
			// Removing query
			if(queryIndex == -1) {
				// Already has no query
				newAnyURI = this;
			} else {
				// Remove the existing query
				if(fragmentIndex == -1) {
					newAnyURI = newAnyURI(
						uri.substring(0, queryIndex),
						isEncodingNormalized(), // null query is always effectively normalized
						schemeLength,
						-1,
						-1
					);
				} else {
					int uriLen = uri.length();
					int newUriLen = queryIndex + (uriLen - fragmentIndex);
					StringBuilder newUri = new StringBuilder(newUriLen);
					newUri.append(uri, 0, queryIndex).append(uri, fragmentIndex, uriLen);
					assert newUri.length() == newUriLen;
					newAnyURI = newAnyURI(
						newUri.toString(),
						isEncodingNormalized(), // null query is always effectively normalized
						schemeLength,
						-1,
						queryIndex
					);
				}
			}
		} else {
			if(query.indexOf('#') != -1) throw new IllegalArgumentException("query string may not contain fragment marker (#): " + query);
			// Setting query
			if(queryIndex == -1) {
				// Add query
				int uriLen = uri.length();
				int queryLen = query.length();
				int newUriLen = uriLen + 1 + queryLen;
				StringBuilder newUri = new StringBuilder(newUriLen);
				if(fragmentIndex == -1) {
					newUri.append(uri).append('?').append(query);
					assert newUri.length() == newUriLen;
					newAnyURI = newAnyURI(
						newUri.toString(),
						queryEncodingNormalized && isEncodingNormalized(),
						schemeLength,
						uriLen,
						-1
					);
				} else {
					newUri.append(uri, 0, fragmentIndex).append('?').append(query).append(uri, fragmentIndex, uriLen);
					assert newUri.length() == newUriLen;
					newAnyURI = newAnyURI(
						newUri.toString(),
						queryEncodingNormalized && isEncodingNormalized(),
						schemeLength,
						fragmentIndex,
						fragmentIndex + 1 + queryLen
					);
				}
			} else {
				// Replace query
				int uriLen = uri.length();
				int queryLen = query.length();
				int queryStart = queryIndex + 1;
				int currentQueryLen = (fragmentIndex == -1 ? uriLen : fragmentIndex) - queryStart;
				if(
					currentQueryLen == queryLen
					&& uri.regionMatches(queryStart, query, 0, queryLen)
				) {
					// Already has this query
					newAnyURI = this;
				} else {
					int newUriLen = uriLen - currentQueryLen + queryLen;
					StringBuilder newUri = new StringBuilder(newUriLen);
					newUri.append(uri, 0, queryStart).append(query);
					if(fragmentIndex == -1) {
						assert newUri.length() == newUriLen;
						newAnyURI = newAnyURI(
							newUri.toString(),
							queryEncodingNormalized && isEncodingNormalized(),
							schemeLength,
							queryIndex,
							-1
						);
					} else {
						newUri.append(uri, fragmentIndex, uriLen);
						assert newUri.length() == newUriLen;
						newAnyURI = newAnyURI(
							newUri.toString(),
							queryEncodingNormalized && isEncodingNormalized(),
							schemeLength,
							queryIndex,
							fragmentIndex - currentQueryLen + queryLen
						);
					}
				}
			}
		}
		assert Objects.equals(query, newAnyURI.getQueryString());
		return newAnyURI;
	}

	/**
	 * Replaces the query string.
	 *
	 * @param query  The query (not including the first '?') - it is added without additional encoding.
	 *               The query is removed when the query is {@code null}.
	 *               The query may not contain the fragment marker '#'
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 */
	public AnyURI setQueryString(String query) {
		return setQueryStringImpl(query, false);
	}

	final AnyURI addQueryStringImpl(String query, boolean queryEncodingNormalized) {
		final AnyURI newAnyURI;
		if(query == null) {
			newAnyURI = this;
		} else {
			if(query.indexOf('#') != -1) throw new IllegalArgumentException("query string may not contain fragment marker (#): " + query);
			int uriLen = uri.length();
			int queryLen = query.length();
			int newUriLen = uriLen + 1 + queryLen;
			StringBuilder newUri = new StringBuilder(newUriLen);
			int newQueryIndex;
			int newFragmentIndex;
			if(queryIndex == -1) {
				if(fragmentIndex == -1) {
					// First parameter to end
					newUri.append(uri).append('?').append(query);
					newQueryIndex = uri.length();
					newFragmentIndex = -1;
				} else {
					// First parameter before fragment
					newUri.append(uri, 0, fragmentIndex).append('?').append(query).append(uri, fragmentIndex, uriLen);
					newQueryIndex = fragmentIndex;
					newFragmentIndex = fragmentIndex + 1 + queryLen;
				}
			} else {
				newQueryIndex = queryIndex;
				if(fragmentIndex == -1) {
					// Additional parameter to end
					newUri.append(uri).append('&').append(query);
					newFragmentIndex = -1;
				} else {
					// Additional parameter before fragment
					newUri.append(uri, 0, fragmentIndex).append('&').append(query).append(uri, fragmentIndex, uriLen);
					newFragmentIndex = fragmentIndex + 1 + queryLen;
				}
			}
			assert newUri.length() == newUriLen;
			newAnyURI = newAnyURI(
				newUri.toString(),
				queryEncodingNormalized && isEncodingNormalized(),
				schemeLength,
				newQueryIndex,
				newFragmentIndex
			);
		}
		assert
			(query == null) ? Objects.equals(this.getQueryString(), newAnyURI.getQueryString())
			: hasQuery() ? newAnyURI.getQueryString().endsWith('&' + query)
			: newAnyURI.getQueryString().equals(query);
		return newAnyURI;
	}

	/**
	 * Adds a query string.
	 *
	 * @param query  The query (not including the first '?' / '&amp;') - it is added without additional encoding.
	 *               Nothing is added when the query is {@code null}.
	 *               The query may not contain the fragment marker '#'
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 */
	public AnyURI addQueryString(String query) {
		return addQueryStringImpl(query, false);
	}

	final AnyURI addEncodedParameterImpl(String encodedName, String encodedValue, boolean queryEncodingNormalized) {
		final AnyURI newAnyURI;
		if(encodedName == null) {
			if(encodedValue != null) throw new IllegalArgumentException("non-null value provided with null name: " + encodedValue);
			newAnyURI = this;
		} else {
			if(encodedValue == null) {
				newAnyURI = addQueryStringImpl(encodedName, queryEncodingNormalized);
			} else {
				if(encodedName.indexOf('#') != -1) throw new IllegalArgumentException("name may not contain fragment marker (#): " + encodedName);
				if(encodedValue.indexOf('#') != -1) throw new IllegalArgumentException("value may not contain fragment marker (#): " + encodedValue);
				int uriLen = uri.length();
				int nameLen = encodedName.length();
				int valueLen = encodedValue.length();
				int newUriLen = uriLen + 1 + nameLen + 1 + valueLen;
				StringBuilder newUri = new StringBuilder(newUriLen);
				int newQueryIndex;
				int newFragmentIndex;
				if(queryIndex == -1) {
					if(fragmentIndex == -1) {
						// First parameter to end
						newUri.append(uri).append('?').append(encodedName).append('=').append(encodedValue);
						newQueryIndex = uri.length();
						newFragmentIndex = -1;
					} else {
						// First parameter before fragment
						newUri.append(uri, 0, fragmentIndex).append('?').append(encodedName).append('=').append(encodedValue).append(uri, fragmentIndex, uriLen);
						newQueryIndex = fragmentIndex;
						newFragmentIndex = fragmentIndex + 1 + nameLen + 1 + valueLen;
					}
				} else {
					newQueryIndex = queryIndex;
					if(fragmentIndex == -1) {
						// Additional parameter to end
						newUri.append(uri).append('&').append(encodedName).append('=').append(encodedValue);
						newFragmentIndex = -1;
					} else {
						// Additional parameter before fragment
						newUri.append(uri, 0, fragmentIndex).append('&').append(encodedName).append('=').append(encodedValue).append(uri, fragmentIndex, uriLen);
						newFragmentIndex = fragmentIndex + 1 + nameLen + 1 + valueLen;
					}
				}
				assert newUri.length() == newUriLen;
				newAnyURI = newAnyURI(
					newUri.toString(),
					queryEncodingNormalized && isEncodingNormalized(),
					schemeLength,
					newQueryIndex,
					newFragmentIndex
				);
			}
		}
		// TODO: What do we assert here?
		return newAnyURI;
	}

	/**
	 * Adds an already-encoded parameter.
	 *
	 * @param encodedName  The parameter name - it is added without additional encoding.
	 *                     Nothing is added when the name is {@code null}.
	 *                     The name may not contain the fragment marker '#'
	 * @param encodedValue  The parameter value - it is added without additional encoding.
	 *                      When {@code null}, the parameter is added without any '='.
	 *                      Must be {@code null} when {@code name} is {@code null}.
	 *                      The value may not contain the fragment marker '#'
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 */
	public AnyURI addEncodedParameter(String encodedName, String encodedValue) {
		return addEncodedParameterImpl(encodedName, encodedValue, false);
	}

	/**
	 * Encodes and adds a parameter.
	 *
	 * @param name  The parameter name.
	 *              Nothing is added when the name is {@code null}.
	 * @param value  The parameter value.
	 *               When {@code null}, the parameter is added without any '='.
	 *               Must be {@code null} when {@code name} is {@code null}.
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 *
	 * @see  URIEncoder#encodeURIComponent(java.lang.String)
	 */
	public AnyURI addParameter(String name, String value) {
		return addEncodedParameterImpl(
			URIEncoder.encodeURIComponent(name),
			URIEncoder.encodeURIComponent(value),
			true
		);
	}

	/**
	 * Adds all of the parameters.
	 *
	 * @param params  The parameters to add.
	 *                Nothing is added when {@code null} or empty.
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 *
	 * @see  URIParametersUtils#addParams(java.lang.String, com.aoapps.net.URIParameters)
	 */
	public AnyURI addParameters(URIParameters params) {
		if(params == null) {
			return this;
		} else {
			return addQueryStringImpl(
				URIParametersUtils.toQueryString(params),
				true
			);
		}
	}

	final AnyURI setEncodedFragmentImpl(String encodedFragment, boolean fragmentEncodingNormalized) {
		final AnyURI newAnyURI;
		if(encodedFragment == null) {
			// Removing fragment
			if(fragmentIndex == -1) {
				// Already has no fragment
				newAnyURI = this;
			} else {
				// Remove the existing fragment
				newAnyURI = newAnyURI(
					uri.substring(0, fragmentIndex),
					isEncodingNormalized(), // null fragment is always effectively normalized
					schemeLength,
					queryIndex,
					-1
				);
			}
		} else {
			// Setting fragment
			if(fragmentIndex == -1) {
				// Add fragment
				int uriLen = uri.length();
				int newUriLen = uriLen + 1 + encodedFragment.length();
				StringBuilder newUri = new StringBuilder(newUriLen);
				newUri.append(uri).append('#').append(encodedFragment);
				assert newUri.length() == newUriLen;
				newAnyURI = newAnyURI(
					newUri.toString(),
					fragmentEncodingNormalized && isEncodingNormalized(),
					schemeLength,
					queryIndex,
					uriLen
				);
			} else {
				// Replace fragment
				int uriLen = uri.length();
				int fragmentLen = encodedFragment.length();
				int fragmentStart = fragmentIndex + 1;
				int currentFragmentLen = uriLen - fragmentStart;
				if(
					currentFragmentLen == fragmentLen
					&& uri.regionMatches(fragmentStart, encodedFragment, 0, fragmentLen)
				) {
					// Already has this fragment
					newAnyURI = this;
				} else {
					int newUriLen = uriLen - currentFragmentLen + fragmentLen;
					StringBuilder newUri = new StringBuilder(newUriLen);
					newUri.append(uri, 0, fragmentStart).append(encodedFragment);
					assert newUri.length() == newUriLen;
					newAnyURI = newAnyURI(
						newUri.toString(),
						fragmentEncodingNormalized && isEncodingNormalized(),
						schemeLength,
						queryIndex,
						fragmentIndex - currentFragmentLen + fragmentLen
					);
				}
			}
		}
		assert Objects.equals(encodedFragment, newAnyURI.getFragment());
		return newAnyURI;
	}

	/**
	 * Replaces the fragment.
	 *
	 * @param encodedFragment  The fragment (not including the '#') - it is added without additional encoding.
	 *                         Removes fragment when {@code null}.
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 */
	public AnyURI setEncodedFragment(String encodedFragment) {
		return setEncodedFragmentImpl(encodedFragment, false);
	}

	/**
	 * Replaces the fragment in the default encoding {@link IRI#ENCODING}.
	 * <p>
	 * TODO: Implement specification of <a href="https://dev.w3.org/html5/spec-LC/urls.html#url-manipulation-and-creation">fragment-escape</a>.
	 * </p>
	 *
	 * @param fragment  The fragment (not including the '#') or {@code null} for no fragment.
	 *
	 * @return  The new {@link AnyURI} or {@code this} when unmodified.
	 */
	public AnyURI setFragment(String fragment) {
		return setEncodedFragmentImpl(
			URIEncoder.encodeURIComponent(fragment),
			true
		);
	}
}
