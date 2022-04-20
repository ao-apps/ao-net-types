/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2008, 2009, 2010, 2011, 2016, 2019, 2021, 2022  AO Industries, Inc.
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class URIResolverTest extends TestCase {

  public URIResolverTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    // Do nothing
  }

  @Override
  protected void tearDown() throws Exception {
    // Do nothing
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(URIResolverTest.class);
    return suite;
  }

  public void testGetAbsolutePath() throws Exception {
    // TODO: Test /./ and /../ in middle of URL
    assertEquals("/test/", URIResolver.getAbsolutePath("/test/page.jsp", "./"));
    assertEquals("/test/other.jsp", URIResolver.getAbsolutePath("/test/subdir/page.jsp", "/test/other.jsp"));
    assertEquals("/test/other.jsp", URIResolver.getAbsolutePath("/test/subdir/page.jsp", "../other.jsp"));
    assertEquals("/test/other.jsp", URIResolver.getAbsolutePath("/test/subdir/page.jsp", "./.././other.jsp"));
    assertEquals("/test/subdir/other.jsp", URIResolver.getAbsolutePath("/test/page.jsp", "subdir/other.jsp"));
    assertEquals("/test/other.jsp", URIResolver.getAbsolutePath("/test/page.jsp", "other.jsp"));
    assertEquals("/other.jsp", URIResolver.getAbsolutePath("/page.jsp", "other.jsp"));
  }
}
