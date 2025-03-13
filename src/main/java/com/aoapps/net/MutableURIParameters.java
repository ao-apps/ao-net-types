/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2011, 2016, 2019, 2021, 2022, 2024, 2025  AO Industries, Inc.
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

import com.aoapps.lang.Coercion;
import com.aoapps.lang.NullArgumentException;

/**
 * Provides read-write access to URI parameters.
 *
 * @author  AO Industries, Inc.
 */
public interface MutableURIParameters extends URIParameters {

  /**
   * Adds a parameter with a single value.
   *
   * @param name   Required when {@code value} is not {@code null}.
   *
   * @param value  When {@code null}, no parameter is added.
   */
  MutableURIParameters add(String name, String value);

  /**
   * Adds a parameter with a single value.
   *
   * <p>The conversion to string may be deferred, or the value may be streamed instead of being
   * converted to a string.  It is incorrect to change the state of the provided value; doing
   * so may or may not affect the value of the resulting parameter.</p>
   *
   * <p>When the value is an {@link Enum}, the parameter value is obtained from {@link Enum#name()} instead of
   * {@link Enum#toString()}.  This is to intuitively use enums as parameters when {@link Enum#toString()} is
   * overridden.</p>
   *
   * @param name   Required when {@code value} is not {@code null}.
   *
   * @param value  When {@code null}, no parameter is added.
   *
   * @see  Coercion
   */
  default MutableURIParameters add(String name, Object value) {
    if (value != null) {
      add(name, (value instanceof Enum) ? ((Enum) value).name() : Coercion.toString(value));
    }
    return this;
  }

  /**
   * Adds a parameter with a single value.
   *
   * @param name   May not be {@code null}.
   * @param value  May not be {@code null}.
   *
   * @deprecated  Please use {@link #add(java.lang.String, java.lang.String)} directly.
   */
  @Deprecated
  default void addParameter(String name, String value) {
    NullArgumentException.checkNotNull(value, "value");
    add(name, value);
  }

  /**
   * Adds a parameter with multiple values.
   *
   * <p>The iteration of values will be performed immediately, but
   * the conversion to string may be deferred, or the value may be streamed instead of being
   * converted to a string.  It is incorrect to change the state of the provided value; doing
   * so may or may not affect the value of the resulting parameter.</p>
   *
   * <p>When a value is an {@link Enum}, the parameter value is obtained from {@link Enum#name()} instead of
   * {@link Enum#toString()}.  This is to intuitively use enums as parameters when {@link Enum#toString()} is
   * overridden.</p>
   *
   * @param name   Required when {@code value} has any element that is not {@code null}.
   *
   * @param values  When {@code null}, no parameters are added.
   *                When an element is {@code null}, no parameter is added.
   *
   * @see  Coercion
   */
  default MutableURIParameters add(String name, Iterable<?> values) {
    if (values != null) {
      for (Object value : values) {
        add(name, value);
      }
    }
    return this;
  }

  /**
   * Adds a parameter with multiple values.
   *
   * <p>The iteration of values will be performed immediately, but
   * the conversion to string may be deferred, or the value may be streamed instead of being
   * converted to a string.  It is incorrect to change the state of the provided value; doing
   * so may or may not affect the value of the resulting parameter.</p>
   *
   * <p>When a value is an {@link Enum}, the parameter value is obtained from {@link Enum#name()} instead of
   * {@link Enum#toString()}.  This is to intuitively use enums as parameters when {@link Enum#toString()} is
   * overridden.</p>
   *
   * @param name   Required when {@code value} has any element that is not {@code null}.
   *
   * @param values  When {@code null}, no parameters are added.
   *                When an element is {@code null}, no parameter is added.
   *
   * @see  Coercion
   */
  default MutableURIParameters add(String name, Object ... values) {
    if (values != null) {
      for (Object value : values) {
        add(name, value);
      }
    }
    return this;
  }

  /**
   * Adds a parameter with multiple values.
   *
   * @param name    Required when {@code values} is not {@code null}.
   * @param values  When {@code null}, no parameters are added.
   *                May not contain any {@code null} elements.
   *
   * @deprecated  Please use {@link #add(java.lang.String, java.lang.Iterable)} directly.
   */
  @Deprecated
  default void addParameters(String name, Iterable<String> values) {
    if (values != null) {
      for (String value : values) {
        addParameter(name, value);
      }
    }
  }
}
