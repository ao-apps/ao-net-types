/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2016, 2019, 2021  AO Industries, Inc.
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class URIEncoderTest {

	public URIEncoderTest() {
	}

	@Test
	public void testEncodeURI() {
		assertEquals(
			"https://aointernet.net/shared/%E3%83%9B%E3%82%B9%E3%83%86%E3%82%A3%E3%83%B3%E3%82%B0.do?%E8%8A%B1=true",
			URIEncoder.encodeURI("https://aointernet.net/shared/ホスティング.do?花=true")
		);
		assertEquals(
			"https://aointernet.net/shared/%E3%83%9B%E3%82%B9%E3%83%86%E3%82%A3%E3%83%B3%E3%82%B0.do?param=%E8%8A%B1",
			URIEncoder.encodeURI("https://aointernet.net/shared/ホスティング.do?param=花")
		);
		assertEquals(
			"Checking not double-encoding after #",
			"https://search.maven.org/#search%7Cgav%7C1%7Cg:%22@com.aoapps%22%20AND%20a:%22@ao-hodgepodge%22",
			URIEncoder.encodeURI("https://search.maven.org/#search|gav|1|g:%22@com.aoapps%22%20AND%20a:%22@ao-hodgepodge%22")
		);
	}
}
