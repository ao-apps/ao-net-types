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

import java.util.Locale;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @see Protocol
 *
 * @author  AO Industries, Inc.
 */
public class ProtocolTest {

  @Test
  public void testValueOfConsistentWithGetDecimal() {
    for (Protocol p1 : Protocol.values()) {
      if (p1 != Protocol.UNASSIGNED) {
        Protocol p2 = Protocol.valueOf(p1.getDecimal());
        if (
            // TTP is only allowed duplicate
            !(
                p1 == Protocol.TTP
                    && p2 == Protocol.IPTM
            )
        ) {
          assertEquals(p1, p2);
        }
      }
    }
  }

  @Test
  public void testValueOfNoNulls() {
    for (short decimal = 0; decimal <= 255; decimal++) {
      assertNotNull("decimal=" + decimal, Protocol.valueOf(decimal));
    }
  }

  @Test
  public void testNamesCapital() {
    for (Protocol p : Protocol.values()) {
      String name = p.name();
      assertEquals(
          "Protocol enum names must be all upper-case in root locale",
          name,
          name.toUpperCase(Locale.ROOT)
      );
    }
  }

  @Test
  public void testGetProtocolByKeyword() {
    for (Protocol p : Protocol.values()) {
      assertEquals(
          "Protocol toString() must be found in getProtocolByKeyword(String)",
          p,
          Protocol.getProtocolByKeyword(p.toString())
      );
    }
  }

  @Test
  public void testToString() {
    for (Protocol p : Protocol.values()) {
      String s = p.toString();
      assertNotNull("toString must not be null", s);
      String trimmed = s.trim();
      assertEquals("toString must already be trimmed", trimmed, s);
      assertNotEquals("toString must not be empty", "", s);
    }
  }

  @Test
  public void testGetDecimal() {
    for (Protocol p : Protocol.values()) {
      short decimal = p.getDecimal();
      if (decimal == -1) {
        assertEquals("Only UNASSIGNED may be -1", Protocol.UNASSIGNED, p);
      } else {
        assertTrue(decimal >= 0 && decimal <= 255);
      }
    }
  }

  @Test
  public void testGetKeyword() {
    for (Protocol p : Protocol.values()) {
      String keyword = p.getKeyword();
      assertNotNull("keyword must not be null", keyword);
      String trimmed = keyword.trim();
      assertEquals("keyword must already be trimmed", trimmed, keyword);
    }
  }

  @Test
  public void testGetProtocol() {
    for (Protocol p : Protocol.values()) {
      String protocol = p.getProtocol();
      assertNotNull("protocol must not be null", protocol);
      String trimmed = protocol.trim();
      assertEquals("protocol must already be trimmed", trimmed, protocol);
    }
  }
}
