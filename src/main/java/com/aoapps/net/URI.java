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
 * along with ao-net-types.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.net;

import com.aoapps.lang.io.Encoder;
import java.io.IOException;

/**
 * Implementation of {@link AnyURI} that is restricted to <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a> only.
 * <p>
 * This may have additional, unnecessary percent encodings, if they were present
 * in the {@code anyUri} provided to the constructor.  If consistent formatting
 * is required, use {@link IRI}.{@link IRI#toURI() toURI()}.  See {@link #isEncodingNormalized()}.
 * </p>
 * <p>
 * Furthermore, there is no assumption about the query parameter encodings, and
 * the query could, in theory, contain any arbitrary encoded data.  Existing
 * encoded query data is maintained, as-is.
 * </p>
 * <p>
 * When a strict ASCII-only representation of a <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a>
 * is required, use {@link URI}.  When a Unicode representation of a <a href="https://datatracker.ietf.org/doc/html/rfc3987">RFC 3987 IRI</a>
 * is preferred, use {@link IRI}.  Otherwise, to support both, use {@link AnyURI}, which should also perform
 * the best since it performs fewer conversions.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
// TODO: Add tests
public class URI extends AnyURI {

	private final boolean isEncodingNormalized;

	/**
	 * {@linkplain URIEncoder#encodeURI(java.lang.String) Encodes} the given
	 * {@code anyUri} for this {@link URI}.
	 */
	// TODO: Thread-local AnyURI/URI/IRI LRU instance caches, getIRI(String) instead of constructor?
	public URI(String anyUri) {
		this(anyUri, false);
	}

	URI(String anyUri, boolean isEncodingNormalized) {
		super(URIEncoder.encodeURI(anyUri));
		this.isEncodingNormalized = isEncodingNormalized;
		this.toURICache = this;
		if(isEncodingNormalized) {
			assert this.uri.equals(URIEncoder.encodeURI(URIDecoder.decodeURI(anyUri))) : "URI is not already percent-encoding normalized: " + anyUri;
		}
	}

	private URI(String uri, boolean isEncodingNormalized, int schemeLength, int queryIndex, int fragmentIndex) {
		super(uri, schemeLength, queryIndex, fragmentIndex);
		this.isEncodingNormalized = isEncodingNormalized;
		this.toURICache = this;
		assert uri.equals(URIEncoder.encodeURI(uri)) : "uri is not already encoded: " + uri;
		if(isEncodingNormalized) {
			assert this.uri.equals(URIEncoder.encodeURI(URIDecoder.decodeURI(uri))) : "URI is not already percent-encoding normalized: " + uri;
		}
	}

	@Override
	URI newAnyURI(String uri, boolean isEncodingNormalized, int schemeLength, int queryIndex, int fragmentIndex) {
		return new URI(uri, isEncodingNormalized, schemeLength, queryIndex, fragmentIndex);
	}

	/**
	 * Gets the full URI in <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a>
	 * US-ASCII format.
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

	@Override
	public String toASCIIString() {
		return uri;
	}

	@Override
	public URI appendScheme(Appendable out) throws IOException {
		super.appendScheme(out);
		return this;
	}

	@Override
	public URI appendScheme(Encoder encoder, Appendable out) throws IOException {
		super.appendScheme(encoder, out);
		return this;
	}

	@Override
	public URI appendHierPart(Appendable out) throws IOException {
		super.appendHierPart(out);
		return this;
	}

	@Override
	public URI appendHierPart(Encoder encoder, Appendable out) throws IOException {
		super.appendHierPart(encoder, out);
		return this;
	}

	@Override
	public URI appendQueryString(Appendable out) throws IOException {
		super.appendQueryString(out);
		return this;
	}

	@Override
	public URI appendQueryString(Encoder encoder, Appendable out) throws IOException {
		super.appendQueryString(encoder, out);
		return this;
	}

	@Override
	public URI appendFragment(Appendable out) throws IOException {
		super.appendFragment(out);
		return this;
	}

	@Override
	public URI appendFragment(Encoder encoder, Appendable out) throws IOException {
		super.appendFragment(encoder, out);
		return this;
	}

	/**
	 * @return  {@code true} when this was derived from an {@link IRI}, which are always encoding normalized.
	 *          {@code false} otherwise, since the percent-encoding normalization status is unknown.
	 */
	@Override
	public boolean isEncodingNormalized() {
		return isEncodingNormalized;
	}

	/**
	 * @return  {@code this}
	 */
	@Override
	public URI toURI() {
		return this;
	}

	/**
	 * @return  The {@link IRI}.
	 */
	@Override
	public IRI toIRI() {
		return super.toIRI();
	}

	@Override
	public URI setHierPart(String hierPart) {
		return (URI)super.setHierPartImpl(URIEncoder.encodeURI(hierPart), false);
	}

	@Override
	public URI setQueryString(String query) {
		return (URI)super.setQueryStringImpl(URIEncoder.encodeURI(query), false);
	}

	@Override
	public URI addQueryString(String query) {
		return (URI)super.addQueryStringImpl(URIEncoder.encodeURI(query), false);
	}

	@Override
	public URI addEncodedParameter(String encodedName, String encodedValue) {
		return (URI)super.addEncodedParameterImpl(
			URIEncoder.encodeURI(encodedName),
			URIEncoder.encodeURI(encodedValue),
			false
		);
	}

	@Override
	public URI addParameter(String name, String value) {
		return (URI)super.addParameter(name, value);
	}

	@Override
	public URI addParameters(URIParameters params) {
		return (URI)super.addParameters(params);
	}

	@Override
	public URI setEncodedFragment(String encodedFragment) {
		return (URI)super.setEncodedFragmentImpl(URIEncoder.encodeURI(encodedFragment), false);
	}

	@Override
	public URI setFragment(String fragment) {
		return (URI)super.setFragment(fragment);
	}
}
