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

package com.aoapps.net;

/**
 * See <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.4">RFC 4291, Section 2.4. Address Type Identification</a>.
 *
 * @author  AO Industries, Inc.
 */
public enum AddressType {

	/**
	 * See <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.5.2">Section 2.5.2. The Unspecified Address</a>.
	 *
	 * @see  InetAddress#isUnspecified()
	 */
	UNSPECIFIED,

	/**
	 * See <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.5.3">Section 2.5.3. The Loopback Address</a>.
	 *
	 * @see  InetAddress#isLoopback()
	 */
	LOOPBACK,

	/**
	 * See <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.7">Section 2.7. Multicast Addresses</a>.
	 *
	 * @see  InetAddress#isMulticast()
	 */
	MULTICAST,

	/**
	 * See <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.5.6">Section 2.5.6. Link-Local IPv6 Unicast Addresses</a>.
	 *
	 * @see  InetAddress#isLinkLocal()
	 */
	LINK_LOCAL_UNICAST,

	/**
	 * (everything else)
	 */
	GLOBAL_UNICAST
}
