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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link AnyURI} that prefers <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 IRI</a>.
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
public class IRI extends AnyURI {

	/**
	 * The default encoding is <code>{@link StandardCharsets#UTF_8}</code> per
	 * <a href="https://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">B.2.1 Non-ASCII characters in URI attribute values</a>.
	 */
	public static final Charset ENCODING = StandardCharsets.UTF_8;

	public IRI(String anyUri) {
		// TODO: Should we just verify here, and error if not already IRI formatted?
		// TODO: Can we verify without knowing document encoding?
		super(URIDecoder.decodeURI(anyUri));
	}

	private IRI(String iri, int schemeLength, int queryIndex, int fragmentIndex) {
		super(iri, schemeLength, queryIndex, fragmentIndex);
	}

	@Override
	IRI newAnyURI(String uri, int schemeLength, int queryIndex, int fragmentIndex) {
		return new IRI(uri, schemeLength, queryIndex, fragmentIndex);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return  {@code this}
	 */
	@Override
	public IRI toIRI() {
		return this;
	}

	@Override
	public IRI setHierPart(String hierPart) {
		// TODO: decode to IRI
		return (IRI)super.setHierPart(hierPart);
	}

	@Override
	public IRI setQueryString(String query) {
		// TODO: decode to IRI
		return (IRI)super.setQueryString(query);
	}

	@Override
	public IRI addQueryString(String query) {
		// TODO: decode to IRI
		return (IRI)super.addQueryString(query);
	}

	@Override
	public IRI addEncodedParameter(String encodedName, String encodedValue) {
		// TODO: decode to IRI
		return (IRI)super.addEncodedParameter(encodedName, encodedValue);
	}

	@Override
	public IRI addParameter(String name, String value) {
		// TODO: decode to IRI
		return (IRI)super.addParameter(name, value);
	}

	@Override
	public IRI addParameters(URIParameters params) {
		// TODO: decode to IRI
		return (IRI)super.addParameters(params);
	}

	@Override
	public IRI setEncodedFragment(String encodedFragment) {
		// TODO: decode to IRI
		return (IRI)super.setEncodedFragment(encodedFragment);
	}

	@Deprecated
	@Override
	public IRI setFragment(String fragment) {
		// TODO: decode to IRI
		return (IRI)super.setFragment(fragment);
	}
}
