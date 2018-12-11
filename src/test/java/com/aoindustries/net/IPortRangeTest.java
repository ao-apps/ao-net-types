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
 * @see IPortRange
 *
 * @author  AO Industries, Inc.
 */
public class IPortRangeTest {

	@Test
	public void testCompareTo1() throws ValidationException {
		assertTrue(
			Port.valueOf(1, Protocol.TCP).compareTo(
				Port.valueOf(1, Protocol.TCP)
			)
			== 0
		);
	}

	@Test
	public void testCompareTo2() throws ValidationException {
		assertTrue(
			Port.valueOf(1, Protocol.TCP).compareTo(
				PortRange.valueOf(1, 2, Protocol.TCP)
			)
			< 0
		);
	}

	@Test
	public void testCompareTo3() throws ValidationException {
		assertTrue(
			Port.valueOf(1, Protocol.TCP).compareTo(
				Port.valueOf(1, Protocol.UDP)
			)
			< 0
		);
	}

	@Test
	public void testCompareTo4() throws ValidationException {
		assertTrue(
			"Detected from sorting before to",
			PortRange.valueOf(10, 15, Protocol.TCP).compareTo(
				PortRange.valueOf(11, 14, Protocol.TCP)
			)
			< 0
		);
	}
	@Test
	public void testOverlaps1() throws ValidationException {
		assertTrue(
			Port.valueOf(10, Protocol.UDP).overlaps(
				Port.valueOf(10, Protocol.UDP)
			)
		);
	}

	@Test
	public void testOverlaps2() throws ValidationException {
		assertFalse(
			Port.valueOf(10, Protocol.TCP).overlaps(
				Port.valueOf(10, Protocol.UDP)
			)
		);
	}

	@Test
	public void testOverlaps3() throws ValidationException {
		assertTrue(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				Port.valueOf(10, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps4() throws ValidationException {
		assertTrue(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				Port.valueOf(5, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps5() throws ValidationException {
		assertFalse(
			Port.valueOf(5, Protocol.TCP).overlaps(
				Port.valueOf(11, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps6() throws ValidationException {
		assertFalse(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				Port.valueOf(11, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps7() throws ValidationException {
		assertFalse(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				Port.valueOf(4, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps8() throws ValidationException {
		assertTrue(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				PortRange.valueOf(1, 5, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps9() throws ValidationException {
		assertTrue(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				PortRange.valueOf(10, 15, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps10() throws ValidationException {
		assertFalse(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				PortRange.valueOf(1, 4, Protocol.TCP)
			)
		);
	}

	@Test
	public void testOverlaps11() throws ValidationException {
		assertFalse(
			PortRange.valueOf(5, 10, Protocol.TCP).overlaps(
				PortRange.valueOf(11, 15, Protocol.TCP)
			)
		);
	}

	@Test
	public void testCoalesce1() throws ValidationException {
		assertEquals(
			"1-10/TCP",
			IPortRange.valueOf(1, 10, Protocol.TCP).coalesce(
				IPortRange.valueOf(1, 10, Protocol.TCP)
			).toString()
		);
	}

	@Test
	public void testCoalesce2() throws ValidationException {
		assertEquals(
			"1-10/TCP",
			IPortRange.valueOf(1, 1, Protocol.TCP).coalesce(
				IPortRange.valueOf(1, 10, Protocol.TCP)
			).toString()
		);
	}

	@Test
	public void testCoalesce3() throws ValidationException {
		assertEquals(
			"1-10/TCP",
			IPortRange.valueOf(1, 10, Protocol.TCP).coalesce(
				IPortRange.valueOf(1, 1, Protocol.TCP)
			).toString()
		);
	}

	@Test
	public void testCoalesce4() throws ValidationException {
		assertEquals(
			"1-10/TCP",
			IPortRange.valueOf(1, 5, Protocol.TCP).coalesce(
				IPortRange.valueOf(6, 10, Protocol.TCP)
			).toString()
		);
	}

	@Test
	public void testCoalesce5() throws ValidationException {
		assertEquals(
			"1-10/TCP",
			IPortRange.valueOf(6, 10, Protocol.TCP).coalesce(
				IPortRange.valueOf(1, 5, Protocol.TCP)
			).toString()
		);
	}

	@Test
	public void testCoalesce6() throws ValidationException {
		assertEquals(
			"1/TCP",
			IPortRange.valueOf(1, 1, Protocol.TCP).coalesce(
				IPortRange.valueOf(1, 1, Protocol.TCP)
			).toString()
		);
	}

	@Test
	public void testCoalesce7() throws ValidationException {
		assertNull(
			IPortRange.valueOf(1, 1, Protocol.TCP).coalesce(
				IPortRange.valueOf(1, 1, Protocol.UDP)
			)
		);
	}

	@Test
	public void testCoalesce8() throws ValidationException {
		assertNull(
			IPortRange.valueOf(1, 5, Protocol.TCP).coalesce(
				IPortRange.valueOf(6, 10, Protocol.UDP)
			)
		);
	}

	@Test
	public void testCoalesce9() throws ValidationException {
		assertNull(
			IPortRange.valueOf(1, 5, Protocol.TCP).coalesce(
				IPortRange.valueOf(7, 10, Protocol.TCP)
			)
		);
	}

	@Test
	public void testCoalesce10() throws ValidationException {
		assertNull(
			IPortRange.valueOf(7, 10, Protocol.TCP).coalesce(
				IPortRange.valueOf(1, 5, Protocol.TCP)
			)
		);
	}
}
