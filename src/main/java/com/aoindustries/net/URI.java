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
 * Implementation of {@link AnyURI} that is restricted to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 URI</a> only.
 * <p>
 * When a strict ASCII-only representation of a <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 URI</a>
 * is required, use {@link URI}.  When a Unicode representation of a <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 IRI</a>
 * is preferred, use {@link IRI}.  Otherwise, to support both, use {@link AnyURI}, which should also perform
 * the best since it performs fewer conversions.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
// TODO: Add tests
public class URI extends AnyURI {

	public URI(String anyUri) {
		super(URIEncoder.encodeURI(anyUri));
	}

	private URI(String uri, int schemeLength, int queryIndex, int fragmentIndex) {
		super(uri, schemeLength, queryIndex, fragmentIndex);
		assert uri.equals(URIEncoder.encodeURI(uri)) : "uri is not already encoded: " + uri;
	}

	@Override
	URI newAnyURI(String uri, int schemeLength, int queryIndex, int fragmentIndex) {
		return new URI(uri, schemeLength, queryIndex, fragmentIndex);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return  {@code this}
	 */
	@Override
	public URI toURI() {
		return this;
	}

	@Override
	public URI setHierPart(String hierPart) {
		return (URI)super.setHierPart(URIEncoder.encodeURI(hierPart));
	}

	@Override
	public URI setQueryString(String query) {
		return (URI)super.setQueryString(URIEncoder.encodeURI(query));
	}

	@Override
	public URI addQueryString(String query) {
		return (URI)super.addQueryStringImpl(URIEncoder.encodeURI(query));
	}

	@Override
	public URI addEncodedParameter(String encodedName, String encodedValue) {
		return (URI)super.addEncodedParameterImpl(
			URIEncoder.encodeURI(encodedName),
			URIEncoder.encodeURI(encodedValue)
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
		return (URI)super.setEncodedFragmentImpl(URIEncoder.encodeURI(encodedFragment));
	}

	@Override
	public URI setFragment(String fragment) {
		return (URI)super.setFragment(fragment);
	}
}
