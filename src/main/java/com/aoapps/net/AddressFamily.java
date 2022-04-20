/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2019, 2021, 2022  AO Industries, Inc.
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

import java.net.StandardProtocolFamily;

/**
 * The supported address families, such as IPv4 and IPv6.
 *
 * @deprecated  Please use {@link StandardProtocolFamily} as of Java 1.7.
 *
 * @author  AO Industries, Inc.
 */
@Deprecated
public enum AddressFamily {

  /**
   * Internet Protocol Version 4 (IPv4).
   *
   * @deprecated  Please use {@link StandardProtocolFamily#INET} as of Java 1.7.
   */
  @Deprecated
  INET(32),

  /**
   * Internet Protocol Version 6 (IPv6).
   *
   * @deprecated  Please use {@link StandardProtocolFamily#INET6} as of Java 1.7.
   */
  @Deprecated
  INET6(128);

  private final int maxPrefix;

  private AddressFamily(int maxPrefix) {
    this.maxPrefix = maxPrefix;
  }

  /**
   * Gets the maximum size of a prefix for addresses in this family.
   */
  public int getMaxPrefix() {
    return maxPrefix;
  }
}
