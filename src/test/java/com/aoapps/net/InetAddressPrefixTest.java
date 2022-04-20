/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.validation.ValidationException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @see InetAddressPrefix
 *
 * @author  AO Industries, Inc.
 */
public class InetAddressPrefixTest {

  @Test(expected = ValidationException.class)
  public void testValueOf_IPv4_lowPrefix() throws ValidationException {
    InetAddressPrefix.valueOf(
      InetAddress.LOOPBACK_IPV4,
      -1
    );
  }

  @Test(expected = ValidationException.class)
  public void testValueOf_IPv6_lowPrefix() throws ValidationException {
    InetAddressPrefix.valueOf(
      InetAddress.LOOPBACK_IPV6,
      -1
    );
  }

  @Test(expected = ValidationException.class)
  public void testValueOf_IPv4_highPrefix() throws ValidationException {
    InetAddressPrefix.valueOf(
      InetAddress.LOOPBACK_IPV4,
      33
    );
  }

  @Test(expected = ValidationException.class)
  public void testValueOf_IPv6_highPrefix() throws ValidationException {
    InetAddressPrefix.valueOf(
      InetAddress.LOOPBACK_IPV6,
      129
    );
  }

  @Test
  public void test_IPv4_singleAddress() throws ValidationException {
    assertEquals(
      "192.0.2.127",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        32
      ).toString()
    );
  }

  @Test(expected = ValidationException.class)
  public void test_IPv4_singleAddress_bracketed() throws ValidationException {
    InetAddress.valueOf("[192.0.2.127]");
  }

  @Test
  public void test_IPv6_singleAddress() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:4",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        128
      ).toString()
    );
  }

  @Test
  public void test_IPv6_singleAddress_bracketed() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:4",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("[2001:db8::1:2:3:4]"),
        128
      ).toString()
    );
  }

  @Test
  public void test_IPv4_singleAddress_zeroPrefix() throws ValidationException {
    assertEquals(
      "192.0.2.127/0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        0
      ).toString()
    );
  }

  @Test
  public void test_IPv6_singleAddress_zeroPrefix() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:4/0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        0
      ).toString()
    );
  }

  @Test
  public void test_IPv4_singleAddress_minPrefixWithSlash() throws ValidationException {
    assertEquals(
      "192.0.2.127/31",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        31
      ).toString()
    );
  }

  @Test
  public void test_IPv6_singleAddress_minPrefixWithSlash() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:4/127",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        127
      ).toString()
    );
  }

  @Test
  public void test_equals_IPv4_sameRange_sameAddress() throws ValidationException {
    assertEquals(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        24
      ),
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        24
      )
    );
  }

  @Test
  public void test_equals_IPv6_sameRange_sameAddress() throws ValidationException {
    assertEquals(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        64
      ),
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        64
      )
    );
  }

  @Test
  public void test_notEquals_IPv4_sameRange_differentAddress() throws ValidationException {
    assertNotEquals(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.126"),
        24
      ),
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        24
      )
    );
  }

  @Test
  public void test_notEquals_IPv6_sameRange_differentAddress() throws ValidationException {
    assertNotEquals(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        64
      ),
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        64
      )
    );
  }

  @Test
  public void test_notEquals_IPv4_differentRange_sameAddress() throws ValidationException {
    assertNotEquals(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        23
      ),
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        24
      )
    );
  }

  @Test
  public void test_notEquals_IPv6_differentRange_sameAddress() throws ValidationException {
    assertNotEquals(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        63
      ),
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        64
      )
    );
  }

  @Test
  public void test_compareTo_addressBeforePrefix() throws ValidationException {
    assertTrue(
      "Address must be ordered before prefix",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        65
      ).compareTo(InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:4"),
        64
      )) < 0
    );
  }

  @Test
  public void test_getFrom_IPv4_sameAsAddress_1() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("192.0.2.127"),
      32
    );
    assertSame(
      iap.getAddress(),
      iap.getFrom()
    );
  }

  @Test
  public void test_getFrom_IPv4_sameAsAddress_2() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("192.0.2.0"),
      24
    );
    assertSame(
      iap.getAddress(),
      iap.getFrom()
    );
  }

  @Test
  public void test_getFrom_IPv4_1() throws ValidationException {
    assertEquals(
      "192.0.2.127",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        32
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv4_2() throws ValidationException {
    assertEquals(
      "192.0.2.126",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        31
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv4_3() throws ValidationException {
    assertEquals(
      "192.0.2.0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        24
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv4_4() throws ValidationException {
    assertEquals(
      "128.0.0.0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        1
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv4_5() throws ValidationException {
    assertEquals(
      "0.0.0.0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        0
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv6_sameAsAddress_1() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("2001:db8::1:2:3:3"),
      128
    );
    assertSame(
      iap.getAddress(),
      iap.getFrom()
    );
  }

  @Test
  public void test_getFrom_IPv6_sameAsAddress_2() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("2001:db8::1:2:3:0"),
      112
    );
    assertSame(
      iap.getAddress(),
      iap.getFrom()
    );
  }

  @Test
  public void test_getFrom_IPv6_1() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:3",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        128
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv6_2() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:2",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        127
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv6_3() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        112
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv6_4() throws ValidationException {
    assertEquals(
      "2000::",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        4
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv6_5() throws ValidationException {
    assertEquals(
      "::",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        0
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv6_6() throws ValidationException {
    assertEquals(
      "2001:db8:3:5::",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:3:5:1:2:3:4"),
        64
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getFrom_IPv6_6_bracketed() throws ValidationException {
    assertEquals(
      "2001:db8:3:5::",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("[2001:db8:3:5:1:2:3:4]"),
        64
      ).getFrom().toString()
    );
  }

  @Test
  public void test_getTo_IPv4_sameAsAddress_1() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("192.0.2.127"),
      32
    );
    assertSame(
      iap.getAddress(),
      iap.getTo()
    );
  }

  @Test
  public void test_getTo_IPv4_sameAsAddress_2() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("192.0.2.255"),
      24
    );
    assertSame(
      iap.getAddress(),
      iap.getTo()
    );
  }

  @Test
  public void test_getTo_IPv4_1() throws ValidationException {
    assertEquals(
      "192.0.2.127",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        32
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv4_2() throws ValidationException {
    assertEquals(
      "192.0.2.127",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        31
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv4_3() throws ValidationException {
    assertEquals(
      "192.0.2.255",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        24
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv4_4() throws ValidationException {
    assertEquals(
      "255.255.255.255",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        1
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv4_5() throws ValidationException {
    assertEquals(
      "255.255.255.255",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        0
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv6_sameAsAddress_1() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("2001:db8::1:2:3:3"),
      128
    );
    assertSame(
      iap.getAddress(),
      iap.getTo()
    );
  }

  @Test
  public void test_getTo_IPv6_sameAsAddress_2() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("2001:db8::1:2:3:ffff"),
      112
    );
    assertSame(
      iap.getAddress(),
      iap.getTo()
    );
  }

  @Test
  public void test_getTo_IPv6_sameAsAddress_2_bracketed() throws ValidationException {
    InetAddressPrefix iap = InetAddressPrefix.valueOf(
      InetAddress.valueOf("[2001:db8::1:2:3:ffff]"),
      112
    );
    assertSame(
      iap.getAddress(),
      iap.getTo()
    );
  }

  @Test
  public void test_getTo_IPv6_1() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:3",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        128
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv6_2() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:3",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        127
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv6_3() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:ffff",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        112
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv6_4() throws ValidationException {
    assertEquals(
      "2fff:ffff:ffff:ffff:ffff:ffff:ffff:ffff",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        4
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv6_5() throws ValidationException {
    assertEquals(
      "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        0
      ).getTo().toString()
    );
  }

  @Test
  public void test_getTo_IPv6_6() throws ValidationException {
    assertEquals(
      "2001:db8:3:5:ffff:ffff:ffff:ffff",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:3:5:1:2:3:4"),
        64
      ).getTo().toString()
    );
  }

  @Test
  public void test_normalize_IPv4_1() throws ValidationException {
    assertEquals(
      "192.0.2.127",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        32
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv4_2() throws ValidationException {
    assertEquals(
      "192.0.2.126/31",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        31
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv4_3() throws ValidationException {
    assertEquals(
      "192.0.2.0/24",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        24
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv4_4() throws ValidationException {
    assertEquals(
      "128.0.0.0/1",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        1
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv4_5() throws ValidationException {
    assertEquals(
      "0.0.0.0/0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("192.0.2.127"),
        0
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv6_1() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:3",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        128
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv6_2() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:2/127",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        127
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv6_3() throws ValidationException {
    assertEquals(
      "2001:db8::1:2:3:0/112",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        112
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv6_4() throws ValidationException {
    assertEquals(
      "2000::/4",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        4
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv6_5() throws ValidationException {
    assertEquals(
      "::/0",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:3"),
        0
      ).normalize().toString()
    );
  }

  @Test
  public void test_normalize_IPv6_6() throws ValidationException {
    assertEquals(
      "2001:db8:3:5::/64",
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:3:5:1:2:3:4"),
        64
      ).normalize().toString()
    );
  }

  @Test
  public void test_contains_InetAddress_IPv4_1() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        32
      ).contains(
        InetAddress.valueOf("1.2.3.5")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv4_1_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        32
      ).contains(
        InetAddress.valueOf("1.2.3.4")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv4_1_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        32
      ).contains(
        InetAddress.valueOf("1.2.3.6")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv4_2_min() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddress.valueOf("1.2.3.0")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv4_2_max() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddress.valueOf("1.2.3.255")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv4_2_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddress.valueOf("1.2.2.255")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv4_2_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddress.valueOf("1.2.4.0")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_1() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:5"),
        128
      ).contains(
        InetAddress.valueOf("2001:db8::1:2:3:5")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv6_1_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:5"),
        128
      ).contains(
        InetAddress.valueOf("2001:db8::1:2:3:4")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv6_1_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:5"),
        128
      ).contains(
        InetAddress.valueOf("2001:db8::1:2:3:6")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_2_min() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:5"),
        112
      ).contains(
        InetAddress.valueOf("2001:db8::1:2:3:0")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_2_max() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:5"),
        112
      ).contains(
        InetAddress.valueOf("2001:db8::1:2:3:ffff")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_2_max_bracketed() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("[2001:db8::1:2:3:5]"),
        112
      ).contains(
        InetAddress.valueOf("[2001:db8::1:2:3:ffff]")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv6_2_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:5"),
        112
      ).contains(
        InetAddress.valueOf("2001:db8::1:2:2:ffff")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv6_2_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8::1:2:3:5"),
        112
      ).contains(
        InetAddress.valueOf("2001:db8::1:2:4:0")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_3_min() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        64
      ).contains(
        InetAddress.valueOf("2001:db8:1:ff::")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_3_min_bracketed() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("[2001:db8:1:ff:3:4:5:6]"),
        64
      ).contains(
        InetAddress.valueOf("[2001:db8:1:ff::]")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_3_max() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        64
      ).contains(
        InetAddress.valueOf("2001:db8:1:ff:ffff:ffff:ffff:ffff")
      )
    );
  }

  @Test
  public void test_contains_InetAddress_IPv6_3_max_bracketed() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("[2001:db8:1:ff:3:4:5:6]"),
        64
      ).contains(
        InetAddress.valueOf("[2001:db8:1:ff:ffff:ffff:ffff:ffff]")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv6_3_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        64
      ).contains(
        InetAddress.valueOf("2001:db8:1:fe:ffff:ffff:ffff:ffff")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv6_3_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        64
      ).contains(
        InetAddress.valueOf("2001:db8:1:100:0:0:0:0")
      )
    );
  }

  @Test
  public void test_notContains_InetAddress_IPv6_3_above_bracketed() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("[2001:db8:1:ff:3:4:5:6]"),
        64
      ).contains(
        InetAddress.valueOf("[2001:db8:1:100:0:0:0:0]")
      )
    );
  }

  @Test
  public void test_IPv6_notContains_InetAddress_IPv4() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("::"),
        0
      ).contains(
        InetAddress.valueOf("1.2.3.4")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv4_1() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        32
      ).contains(
        InetAddressPrefix.valueOf("1.2.3.5")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv4_1_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        32
      ).contains(
        InetAddressPrefix.valueOf("1.2.3.4")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv4_1_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        32
      ).contains(
        InetAddressPrefix.valueOf("1.2.3.6")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv4_2_min() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddressPrefix.valueOf("1.2.3.0")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv4_2_max() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddressPrefix.valueOf("1.2.3.255")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv4_2_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddressPrefix.valueOf("1.2.2.255")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv4_2_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddressPrefix.valueOf("1.2.4.0")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv4_2_sameSize() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddressPrefix.valueOf("1.2.3.0/24")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv4_2_bigger() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("1.2.3.5"),
        24
      ).contains(
        InetAddressPrefix.valueOf("1.2.3.0/23")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv6_1() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        128
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:5:6")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv6_1_bracketed() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("[2001:db8:1:ff:3:4:5:6]"),
        128
      ).contains(
        InetAddressPrefix.valueOf("[2001:db8:1:ff:3:4:5:6]")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv6_1_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        128
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:5:5")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv6_1_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        128
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:5:7")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv6_2_min() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        112
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:5:0")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv6_2_max() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        112
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:5:ffff")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv6_2_below() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        112
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:4:ffff")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv6_2_above() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        112
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:6:0")
      )
    );
  }

  @Test
  public void test_contains_InetAddressPrefix_IPv6_2_sameSize() throws ValidationException {
    assertTrue(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        112
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:5:0/112")
      )
    );
  }

  @Test
  public void test_notContains_InetAddressPrefix_IPv6_2_bigger() throws ValidationException {
    assertFalse(
      InetAddressPrefix.valueOf(
        InetAddress.valueOf("2001:db8:1:ff:3:4:5:6"),
        112
      ).contains(
        InetAddressPrefix.valueOf("2001:db8:1:ff:3:4:5:0/111")
      )
    );
  }

  @Test
  public void test_coalesce_IPv4_1() throws ValidationException {
    assertEquals(
      InetAddressPrefix.valueOf("1.2.3.126/31"),
      InetAddressPrefix.valueOf("1.2.3.126").coalesce(
        InetAddressPrefix.valueOf("1.2.3.127")
      )
    );
  }

  @Test
  public void test_notCoalesce_IPv4_1_below() throws ValidationException {
    assertNull(
      InetAddressPrefix.valueOf("1.2.3.126").coalesce(
        InetAddressPrefix.valueOf("1.2.3.125")
      )
    );
  }

  @Test
  public void test_notCoalesce_IPv4_1_above() throws ValidationException {
    assertNull(
      InetAddressPrefix.valueOf("1.2.3.127").coalesce(
        InetAddressPrefix.valueOf("1.2.3.128")
      )
    );
  }

  @Test
  public void test_coalesce_IPv4_2() throws ValidationException {
    assertEquals(
      InetAddressPrefix.valueOf("1.2.3.0/24"),
      InetAddressPrefix.valueOf("1.2.3.1/25").coalesce(
        InetAddressPrefix.valueOf("1.2.3.129/25")
      )
    );
  }

  @Test
  public void test_notCoalesce_IPv4_2_below() throws ValidationException {
    assertNull(
      InetAddressPrefix.valueOf("1.2.3.1/25").coalesce(
        InetAddressPrefix.valueOf("1.2.2.129/25")
      )
    );
  }

  @Test
  public void test_notCoalesce_IPv4_2_above() throws ValidationException {
    assertNull(
      InetAddressPrefix.valueOf("1.2.3.129/25").coalesce(
        InetAddressPrefix.valueOf("1.2.4.1/25")
      )
    );
  }
}
