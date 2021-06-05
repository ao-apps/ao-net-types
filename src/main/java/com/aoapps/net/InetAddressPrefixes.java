/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2021  AO Industries, Inc.
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

/**
 * A set of standard network prefixes.
 *
 * @author  AO Industries, Inc.
 */
final public class InetAddressPrefixes {

	/**
	 * The IPv4 unspecified network (<code>0.0.0.0/0</code>).
	 */
	public static final InetAddressPrefix UNSPECIFIED_IPV4 = InetAddressPrefix.valueOfNoValidate(InetAddress.UNSPECIFIED_IPV4, 0);

	/**
	 * The IPv6 unspecified network (<code>::/0</code>).
	 */
	public static final InetAddressPrefix UNSPECIFIED_IPV6 = InetAddressPrefix.valueOfNoValidate(InetAddress.UNSPECIFIED_IPV6, 0);

	/**
	 * The IPv4 loopback network (<code>127.0.0.0/8</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc1122#section-3.2.1.3">RFC 1122, Section 3.2.1.3 Addressing</a>.
	 * </p>
	 */
	public static final InetAddressPrefix LOOPBACK_IPV4 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffff7f000000L
		).intern(),
		8
	);

	/**
	 * The IPv4 link local network (<code>169.254.0.0/16</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc3927">RFC 3927</a>.
	 * </p>
	 */
	public static final InetAddressPrefix LINK_LOCAL_IPV4 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffa9fe0000L
		).intern(),
		16
	);

	/**
	 * The IPv6 link local network (<code>fe80::/10</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc4291#section-2.5.6">RFC 4291, Section 2.5.6. Link-Local IPv6 Unicast Addresses</a>.
	 * </p>
	 */
	public static final InetAddressPrefix LINK_LOCAL_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0xfe80000000000000L,
			0x0000000000000000L
		).intern(),
		10
	);

	/**
	 * The IPv4 multicast network (<code>224.0.0.0/4</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc3171">RFC 3171</a>.
	 * </p>
	 */
	public static final InetAddressPrefix MULTICAST_IPV4 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffe0000000L
		).intern(),
		4
	);

	/**
	 * The IPv6 multicast network (<code>ff00::/8</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc4291#section-2.7">RFC 4291, Section 2.7. Multicast Addresses</a>.
	 * </p>
	 */
	public static final InetAddressPrefix MULTICAST_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0xff00000000000000L,
			0x0000000000000000L
		).intern(),
		8
	);

	/**
	 * The IPv4 private network (<code>10.0.0.0/8</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc1918">RFC 1918</a>.
	 * </p>
	 */
	public static final InetAddressPrefix UNIQUE_LOCAL_IPV4_8 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffff0a000000L
		).intern(),
		8
	);

	/**
	 * The IPv4 private network (<code>172.16.0.0/12</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc1918">RFC 1918</a>.
	 * </p>
	 */
	public static final InetAddressPrefix UNIQUE_LOCAL_IPV4_12 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffac100000L
		).intern(),
		12
	);

	/**
	 * The IPv4 private network (<code>192.168.0.0/16</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc1918">RFC 1918</a>.
	 * </p>
	 */
	public static final InetAddressPrefix UNIQUE_LOCAL_IPV4_16 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffc0a80000L
		).intern(),
		16
	);

	/**
	 * The IPv6 unique local network (<code>fc00::/7</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc4193">RFC 4193</a>.
	 * </p>
	 */
	public static final InetAddressPrefix UNIQUE_LOCAL_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0xfc00000000000000L,
			0x0000000000000000L
		).intern(),
		7
	);

	/**
	 * The IPv4 6to4 Relay network (<code>192.88.99.0/24</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc3068">RFC 3068</a>.
	 * </p>
	 */
	public static final InetAddressPrefix _6TO4_IPV4 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffc0586300L
		).intern(),
		24
	);

	/**
	 * The IPv6 multicast network (<code>2002::/16</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc3056">RFC 3056</a>.
	 * </p>
	 */
	public static final InetAddressPrefix _6TO4_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x2002000000000000L,
			0x0000000000000000L
		).intern(),
		16
	);

	/**
	 * The Teredo tunneling network (<code>2001::/32</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc4380">RFC 4380</a>.
	 * </p>
	 */
	public static final InetAddressPrefix TEREDO_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x2001000000000000L,
			0x0000000000000000L
		).intern(),
		32
	);

	/**
	 * The IPv4 documentation network (<code>192.0.2.0/24</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc5737">RFC 5737</a>.
	 * </p>
	 */
	public static final InetAddressPrefix DOCUMENTATION_IPV4_1 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffc0000200L
		).intern(),
		24
	);

	/**
	 * The IPv4 documentation network (<code>198.51.100.0/24</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc5737">RFC 5737</a>.
	 * </p>
	 */
	public static final InetAddressPrefix DOCUMENTATION_IPV4_2 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffc6336400L
		).intern(),
		24
	);

	/**
	 * The IPv4 documentation network (<code>203.0.113.0/24</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc5737">RFC 5737</a>.
	 * </p>
	 */
	public static final InetAddressPrefix DOCUMENTATION_IPV4_3 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffcb007100L
		).intern(),
		24
	);

	/**
	 * The IPv6 documentation network (<code>2001:db8::/32</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc5737">RFC 5737</a>.
	 * </p>
	 */
	public static final InetAddressPrefix DOCUMENTATION_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x20010db800000000L,
			0x0000000000000000L
		),
		32
	);

	/**
	 * The IPv4 benchmark network (<code>198.18.0.0/15</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc2544">RFC 2544</a>.
	 * </p>
	 */
	public static final InetAddressPrefix BENCHMARK_IPV4 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffffc6120000L
		).intern(),
		15
	);

	/**
	 * The IPv6 benchmark network (<code>2001:2::/48</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc5180">RFC 5180</a>.
	 * </p>
	 */
	public static final InetAddressPrefix BENCHMARK_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x2001000200000000L,
			0x0000000000000000L
		).intern(),
		48
	);

	/**
	 * The IPv6 ORCHIDv2 network (<code>2001:20::/28</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc7343">RFC 7343</a>.
	 * </p>
	 */
	public static final InetAddressPrefix ORCHID_IPV6 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x2001002000000000L,
			0x0000000000000000L
		).intern(),
		28
	);

	/**
	 * The IPv4 Carrier-grade NAT (<code>100.64.0.0/10</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc6598">RFC 5698</a>.
	 * </p>
	 */
	public static final InetAddressPrefix CARRIER_GRADE_NAT_IPV4 = InetAddressPrefix.valueOfNoValidate(
		InetAddress.valueOf(
			0x0000000000000000L,
			0x0000ffff64400000L
		).intern(),
		10
	);

	private InetAddressPrefixes() {}
}
