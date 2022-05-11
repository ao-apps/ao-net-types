/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2013, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.collections.AoCollections;
import com.aoapps.lang.io.Encoder;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Protects a set of parameters from modification.
 *
 * @author  AO Industries, Inc.
 */
public class UnmodifiableURIParameters implements URIParameters {

  /**
   * Wraps the given parameters to ensure they are unmodifiable.
   *
   * @return  {@code null} when wrapped is {@code null}, otherwise unmodifiable parameters
   */
  public static URIParameters wrap(URIParameters wrapped) {
    // null remains null
    if (wrapped == null) {
      return null;
    }
    // Empty are unmodifiable
    if (wrapped == EmptyURIParameters.getInstance()) {
      return wrapped;
    }
    // ServletRequest parameters are unmodifiable already
    // TODO: Some sort of registry for this, where other package registers itself as unmodifiable?  ServiceLoader?
    // TODO: if (wrapped instanceof ServletRequestParameters) {
    //         return wrapped;
    //       }
    // Already wrapped
    if (wrapped instanceof UnmodifiableURIParameters) {
      return wrapped;
    }
    // Wrapping necessary
    return new UnmodifiableURIParameters(wrapped);
  }

  private final URIParameters wrapped;

  private UnmodifiableURIParameters(URIParameters wrapped) {
    this.wrapped = wrapped;
  }

  /**
   * {@inheritDoc}
   *
   * @see  URIParameters#toString()
   */
  @Override
  public String toString() {
    return wrapped.toString();
  }

  @Override
  public String getParameter(String name) {
    return wrapped.getParameter(name);
  }

  @Override
  public Iterator<String> getParameterNames() {
    return AoCollections.unmodifiableIterator(wrapped.getParameterNames());
  }

  @Override
  public List<String> getParameterValues(String name) {
    return Collections.unmodifiableList(wrapped.getParameterValues(name));
  }

  @Override
  public Map<String, List<String>> getParameterMap() {
    Map<String, List<String>> wrappedMap = wrapped.getParameterMap();
    Map<String, List<String>> map = AoCollections.newLinkedHashMap(wrappedMap.size());
    for (Map.Entry<String, List<String>> entry : wrappedMap.entrySet()) {
      map.put(
          entry.getKey(),
          Collections.unmodifiableList(
              entry.getValue()
          )
      );
    }
    return Collections.unmodifiableMap(map);
  }

  @Override
  public long getLength() throws IOException {
    return wrapped.getLength();
  }

  @Override
  public boolean isFastToString() {
    return wrapped.isFastToString();
  }

  @Override
  public void writeTo(Writer out) throws IOException {
    wrapped.writeTo(out);
  }

  @Override
  public void writeTo(Writer out, long off, long len) throws IOException {
    wrapped.writeTo(out, off, len);
  }

  @Override
  public void writeTo(Encoder encoder, Writer out) throws IOException {
    wrapped.writeTo(encoder, out);
  }

  @Override
  public void writeTo(Encoder encoder, Writer out, long off, long len) throws IOException {
    wrapped.writeTo(encoder, out, off, len);
  }

  @Override
  public void appendTo(Appendable out) throws IOException {
    wrapped.appendTo(out);
  }

  @Override
  public void appendTo(Appendable out, long start, long end) throws IOException {
    wrapped.appendTo(out, start, end);
  }

  @Override
  public void appendTo(Encoder encoder, Appendable out) throws IOException {
    wrapped.appendTo(encoder, out);
  }

  @Override
  public void appendTo(Encoder encoder, Appendable out, long start, long end) throws IOException {
    wrapped.appendTo(encoder, out, start, end);
  }
}
