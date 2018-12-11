/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.net;

import com.aoindustries.validation.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @see PortRange
 *
 * @author  AO Industries, Inc.
 */
public class PortRangeTest {

	@Test
	public void testToString1() throws ValidationException {
		assertEquals(
			"1-65535/UDP",
			PortRange.valueOf(1, 65535, Protocol.UDP).toString()
		);
	}

	@Test
	public void testMinFrom() throws ValidationException {
		assertNotNull( // Using this assertion to avoid editor warnings about return value not used
			PortRange.valueOf(1, 10, Protocol.TCP)
		);
	}

	@Test(expected = ValidationException.class)
	public void testLowFrom() throws ValidationException {
		assertNotNull( // Using this assertion to avoid editor warnings about return value not used
			PortRange.valueOf(0, 10, Protocol.TCP)
		);
	}

	@Test(expected = ValidationException.class)
	public void testHighFrom() throws ValidationException {
		assertNotNull( // Using this assertion to avoid editor warnings about return value not used
			PortRange.valueOf(65536, 10, Protocol.TCP)
		);
	}

	@Test
	public void testMaxTo() throws ValidationException {
		assertNotNull( // Using this assertion to avoid editor warnings about return value not used
			PortRange.valueOf(10, 65535, Protocol.TCP)
		);
	}

	@Test(expected = ValidationException.class)
	public void testLowTo() throws ValidationException {
		assertNotNull( // Using this assertion to avoid editor warnings about return value not used
			PortRange.valueOf(10, 0, Protocol.TCP)
		);
	}

	@Test(expected = ValidationException.class)
	public void testHighTo() throws ValidationException {
		assertNotNull( // Using this assertion to avoid editor warnings about return value not used
			PortRange.valueOf(10, 65536, Protocol.TCP)
		);
	}

	@Test(expected = ValidationException.class)
	public void testFromBiggerTo() throws ValidationException {
		assertNotNull( // Using this assertion to avoid editor warnings about return value not used
			PortRange.valueOf(10, 1, Protocol.TCP)
		);
	}
}
