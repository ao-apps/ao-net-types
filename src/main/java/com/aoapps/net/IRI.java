/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link AnyURI} that prefers <a href="https://datatracker.ietf.org/doc/html/rfc3987">RFC 3987 IRI</a>.
 *
 * <p>This has consistently formatted percent encodings.</p>
 *
 * <p>Furthermore, this assumes UTF-8 encoding for the query parameters.
 * If the query parameters include any arbitrary encoded data, use
 * {@link AnyURI} or {@link URI} instead.</p>
 *
 * <p>When a strict ASCII-only representation of a <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a>
 * is required, use {@link URI}.  When a Unicode representation of a <a href="https://datatracker.ietf.org/doc/html/rfc3987">RFC 3987 IRI</a>
 * is preferred, use {@link IRI}.  Otherwise, to support both, use {@link AnyURI}, which should also perform
 * the best since it performs fewer conversions.</p>
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

  // TODO: Thread-local AnyURI/URI/IRI LRU instance caches, getIRI(String) instead of constructor?
  public IRI(String anyUri) {
    super(URIDecoder.decodeURI(anyUri));
    this.toIRICache = this;
  }

  private IRI(String iri, int schemeLength, int queryIndex, int fragmentIndex) {
    super(iri, schemeLength, queryIndex, fragmentIndex);
    this.toIRICache = this;
    assert iri.equals(URIDecoder.decodeURI(iri)) : "iri is not already decoded: " + iri;
  }

  @Override
  IRI newAnyURI(String uri, boolean isEncodingNormalized, int schemeLength, int queryIndex, int fragmentIndex) {
    assert isEncodingNormalized : IRI.class.getSimpleName() + " are always encoding normalized";
    return new IRI(uri, schemeLength, queryIndex, fragmentIndex);
  }

  /**
   * Gets the full IRI in <a href="https://datatracker.ietf.org/doc/html/rfc3987">RFC 3987 IRI</a>
   * Unicode format.
   *
   * <p>This will be {@linkplain #isEncodingNormalized() percent-encoding normalized}
   * and contain consistently formatted percent encodings.</p>
   */
  @Override
  public String toString() {
    return super.toString();
  }

  /**
   * Gets the full URI in <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a>
   * US-ASCII format.
   *
   * <p>This will be {@linkplain #isEncodingNormalized() percent-encoding normalized}
   * and contain consistently formatted percent encodings.</p>
   */
  @Override
  public String toASCIIString() {
    return super.toASCIIString();
  }

  @Override
  public IRI appendScheme(Appendable out) throws IOException {
    super.appendScheme(out);
    return this;
  }

  @Override
  public IRI appendScheme(Encoder encoder, Appendable out) throws IOException {
    super.appendScheme(encoder, out);
    return this;
  }

  @Override
  public IRI appendHierPart(Appendable out) throws IOException {
    super.appendHierPart(out);
    return this;
  }

  @Override
  public IRI appendHierPart(Encoder encoder, Appendable out) throws IOException {
    super.appendHierPart(encoder, out);
    return this;
  }

  @Override
  public IRI appendQueryString(Appendable out) throws IOException {
    super.appendQueryString(out);
    return this;
  }

  @Override
  public IRI appendQueryString(Encoder encoder, Appendable out) throws IOException {
    super.appendQueryString(encoder, out);
    return this;
  }

  @Override
  public IRI appendFragment(Appendable out) throws IOException {
    super.appendFragment(out);
    return this;
  }

  @Override
  public IRI appendFragment(Encoder encoder, Appendable out) throws IOException {
    super.appendFragment(encoder, out);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @return  {@code true} - {@link IRI} are always encoding normalized.
   */
  @Override
  public boolean isEncodingNormalized() {
    return true;
  }

  /**
   * Gets this URI encoded in <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a>
   * US-ASCII format.
   *
   * <p>This will be {@linkplain #isEncodingNormalized() percent-encoding normalized}
   * and contain consistently formatted percent encodings.</p>
   *
   * @return  The {@link URI}.
   */
  @Override
  public URI toURI() {
    return super.toURI();
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
    return (IRI) super.setHierPartImpl(URIDecoder.decodeURI(hierPart), true);
  }

  @Override
  public IRI setQueryString(String query) {
    return (IRI) super.setQueryStringImpl(URIDecoder.decodeURI(query), true);
  }

  @Override
  public IRI addQueryString(String query) {
    return (IRI) super.addQueryStringImpl(URIDecoder.decodeURI(query), true);
  }

  @Override
  public IRI addEncodedParameter(String encodedName, String encodedValue) {
    return (IRI) super.addEncodedParameterImpl(
        URIDecoder.decodeURI(encodedName),
        URIDecoder.decodeURI(encodedValue),
        true
    );
  }

  @Override
  public IRI addParameter(String name, String value) {
    return (IRI) addEncodedParameterImpl(
        // TODO: encodeIRIComponent to do this in one shot?
        URIDecoder.decodeURI(URIEncoder.encodeURIComponent(name)),
        URIDecoder.decodeURI(URIEncoder.encodeURIComponent(value)),
        true
    );
  }

  @Override
  public IRI addParameters(URIParameters params) {
    if (params == null) {
      return this;
    } else {
      return (IRI) addQueryStringImpl(
          URIDecoder.decodeURI(URIParametersUtils.toQueryString(params)),
          true
      );
    }
  }

  @Override
  public IRI setEncodedFragment(String encodedFragment) {
    return (IRI) super.setEncodedFragmentImpl(URIDecoder.decodeURI(encodedFragment), true);
  }

  @Override
  public IRI setFragment(String fragment) {
    return (IRI) setEncodedFragmentImpl(
        // TODO: encodeIRIComponent to do this in one shot?
        URIDecoder.decodeURI(URIEncoder.encodeURIComponent(fragment)),
        true
    );
  }
}
