/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2020, 2021  AO Industries, Inc.
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

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @see InetAddressPrefixes
 *
 * @author  AO Industries, Inc.
 */
public class InetAddressPrefixesTest {

	@Test
	public void testUNSPECIFIED_IPV4() {
		assertEquals(
			"0.0.0.0/0",
			InetAddressPrefixes.UNSPECIFIED_IPV4.toString()
		);
	}

	@Test
	public void testUNSPECIFIED_IPV6() {
		assertEquals(
			"::/0",
			InetAddressPrefixes.UNSPECIFIED_IPV6.toString()
		);
	}

	@Test
	public void testLOOPBACK_IPV4() {
		assertEquals(
			"127.0.0.0/8",
			InetAddressPrefixes.LOOPBACK_IPV4.toString()
		);
	}

	@Test
	public void testLINK_LOCAL_IPV4() {
		assertEquals(
			"169.254.0.0/16",
			InetAddressPrefixes.LINK_LOCAL_IPV4.toString()
		);
	}

	@Test
	public void testLINK_LOCAL_IPV6() {
		assertEquals(
			"fe80::/10",
			InetAddressPrefixes.LINK_LOCAL_IPV6.toString()
		);
	}

	@Test
	public void testMULTICAST_IPV4() {
		assertEquals(
			"224.0.0.0/4",
			InetAddressPrefixes.MULTICAST_IPV4.toString()
		);
	}

	@Test
	public void testMULTICAST_IPV6() {
		assertEquals(
			"ff00::/8",
			InetAddressPrefixes.MULTICAST_IPV6.toString()
		);
	}

	@Test
	public void testUNIQUE_LOCAL_IPV4_8() {
		assertEquals(
			"10.0.0.0/8",
			InetAddressPrefixes.UNIQUE_LOCAL_IPV4_8.toString()
		);
	}

	@Test
	public void testUNIQUE_LOCAL_IPV4_12() {
		assertEquals(
			"172.16.0.0/12",
			InetAddressPrefixes.UNIQUE_LOCAL_IPV4_12.toString()
		);
	}

	@Test
	public void testUNIQUE_LOCAL_IPV4_16() {
		assertEquals(
			"192.168.0.0/16",
			InetAddressPrefixes.UNIQUE_LOCAL_IPV4_16.toString()
		);
	}

	@Test
	public void testUNIQUE_LOCAL_IPV6() {
		assertEquals(
			"fc00::/7",
			InetAddressPrefixes.UNIQUE_LOCAL_IPV6.toString()
		);
	}

	@Test
	public void test_6TO4_IPV4() {
		assertEquals(
			"192.88.99.0/24",
			InetAddressPrefixes._6TO4_IPV4.toString()
		);
	}

	@Test
	public void test_6TO4_IPV6() {
		assertEquals(
			"2002::/16",
			InetAddressPrefixes._6TO4_IPV6.toString()
		);
	}

	@Test
	public void testTEREDO_IPV6() {
		assertEquals(
			"2001::/32",
			InetAddressPrefixes.TEREDO_IPV6.toString()
		);
	}

	@Test
	public void testDOCUMENTATION_IPV4_1() {
		assertEquals(
			"192.0.2.0/24",
			InetAddressPrefixes.DOCUMENTATION_IPV4_1.toString()
		);
	}

	@Test
	public void testDOCUMENTATION_IPV4_2() {
		assertEquals(
			"198.51.100.0/24",
			InetAddressPrefixes.DOCUMENTATION_IPV4_2.toString()
		);
	}

	@Test
	public void testDOCUMENTATION_IPV4_3() {
		assertEquals(
			"203.0.113.0/24",
			InetAddressPrefixes.DOCUMENTATION_IPV4_3.toString()
		);
	}

	@Test
	public void testDOCUMENTATION_IPV6() {
		assertEquals(
			"2001:db8::/32",
			InetAddressPrefixes.DOCUMENTATION_IPV6.toString()
		);
	}

	@Test
	public void testBENCHMARK_IPV4() {
		assertEquals(
			"198.18.0.0/15",
			InetAddressPrefixes.BENCHMARK_IPV4.toString()
		);
	}

	@Test
	public void testBENCHMARK_IPV6() {
		assertEquals(
			"2001:2::/48",
			InetAddressPrefixes.BENCHMARK_IPV6.toString()
		);
	}

	@Test
	public void testORCHID_IPV6() {
		assertEquals(
			"2001:20::/28",
			InetAddressPrefixes.ORCHID_IPV6.toString()
		);
	}

	@Test
	public void testCARRIER_GRADE_NAT_IPV4() {
		assertEquals(
			"100.64.0.0/10",
			InetAddressPrefixes.CARRIER_GRADE_NAT_IPV4.toString()
		);
	}
}
