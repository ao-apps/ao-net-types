/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2018, 2019, 2021  AO Industries, Inc.
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
 * @see Path
 *
 * @author  AO Industries, Inc.
 */
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.validation.ValidationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class PathTest {

	@Test
	public void testSubPath() throws ValidationException {
		assertSame(
			Path.ROOT,
			Path.valueOf("/test").subPath(0, 1)
		);
	}

	@Test
	public void testSubPathTestIsSelf() throws ValidationException {
		Path testPath = Path.valueOf("/test");
		assertSame(
			testPath,
			testPath.subPath(0, 5)
		);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSubPathInvalidBegin() throws ValidationException {
		Path.valueOf("/test").subPath(-1, 5);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSubPathInvalidEnd() throws ValidationException {
		Path.valueOf("/test").subPath(0, 6);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubPathBeginIsEnd() throws ValidationException {
		Path.valueOf("/test").subPath(0, 0);
		Path.valueOf("/test/test").subPath(5, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubPathBeginAfterEnd() throws ValidationException {
		Path.valueOf("/test/test").subPath(5, 4);
		Path.valueOf("/test/test").subPath(5, 0);
	}

	@Test
	public void testPrefix() throws ValidationException {
		assertSame(
			Path.ROOT,
			Path.valueOf("/test").prefix(1)
		);
		assertEquals(
			Path.valueOf("/t"),
			Path.valueOf("/test").prefix(2)
		);
		assertEquals(
			Path.valueOf("/test/"),
			Path.valueOf("/test/path").prefix(6)
		);
	}

	@Test(expected = LocalizedIllegalArgumentException.class)
	public void testPrefixInvalidBegin1() throws ValidationException {
		Path.valueOf("/test").prefix(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testPrefixInvalidBegin2() throws ValidationException {
		Path.valueOf("/test").prefix(6);
	}

	@Test(expected = LocalizedIllegalArgumentException.class)
	public void testSuffixInvalidBegin1() throws ValidationException {
		Path.valueOf("/test").suffix(1);
	}

	@Test(expected = LocalizedIllegalArgumentException.class)
	public void testSuffixInvalidBegin2() throws ValidationException {
		Path.valueOf("/test").suffix(4);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSuffixInvalidBegin3() throws ValidationException {
		Path.valueOf("/test").suffix(5);
	}
}
