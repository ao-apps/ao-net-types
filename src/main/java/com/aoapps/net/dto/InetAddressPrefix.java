/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.net.dto;

/**
 * @author  AO Industries, Inc.
 */
public class InetAddressPrefix {

  private InetAddress address;
  private int prefix;

  public InetAddressPrefix() {
    // Do nothing
  }

  public InetAddressPrefix(InetAddress address, int prefix) {
    this.address = address;
    this.prefix = prefix;
  }

  public InetAddress getAddress() {
    return address;
  }

  public void setAddress(InetAddress address) {
    this.address = address;
  }

  public int getPrefix() {
    return prefix;
  }

  public void setPrefix(int prefix) {
    this.prefix = prefix;
  }
}
