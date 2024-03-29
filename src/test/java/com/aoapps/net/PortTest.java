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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.aoapps.lang.validation.ValidationException;
import org.junit.Test;

/**
 * @see Port
 *
 * @author  AO Industries, Inc.
 */
public class PortTest {

  @Test
  public void testToString2() throws ValidationException {
    assertEquals(
        "167/TCP",
        Port.valueOf(167, Protocol.TCP).toString()
    );
  }

  @Test
  public void testToString3() throws ValidationException {
    assertEquals(
        "67/UDP",
        Port.valueOf(67, Protocol.UDP).toString()
    );
  }

  @Test
  public void testMaxFrom() throws ValidationException {
    assertNotNull(
        "Using this assertion to avoid editor warnings about return value not used",
        Port.valueOf(65535, Protocol.TCP)
    );
  }

  @Test
  public void testMinTo() throws ValidationException {
    assertNotNull(
        "Using this assertion to avoid editor warnings about return value not used",
        Port.valueOf(1, Protocol.TCP)
    );
  }
}
