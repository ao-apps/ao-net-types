/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019, 2020, 2021  AO Industries, Inc.
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

import static com.aoapps.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoapps.lang.io.Encoder;
import java.io.IOException;
import java.io.StringWriter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AnyURITest {

	public AnyURITest() {
	}

	@Test
	public void testToString() {
		String href="test";
		assertSame(href, new AnyURI(href).toString());
	}

	@Test
	@SuppressWarnings("RedundantStringConstructorCall") // Intentional for testing
	public void testEquals() {
		assertTrue(new AnyURI("test").equals(new AnyURI(new String("test"))));
		assertTrue(new AnyURI("test").equals(new AnyURI("test")));
		assertFalse(new AnyURI("test").equals(new AnyURI("test?")));
	}

	@Test
	public void testHashCode() {
		for(String url : new String[] {"test", "", "BLARG", "http://", "test?", "blarg?test", "blarg?test=sdf"}) {
			assertEquals(url.hashCode(), new AnyURI(url).hashCode());
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Test Scheme">
	@Test
	public void testIsScheme() {
		assertTrue(new AnyURI("htTP:").isScheme("http"));
		assertTrue(new AnyURI("htTPs:").isScheme("https"));

		assertFalse(new AnyURI("htTP:").isScheme("https"));
		assertFalse(new AnyURI("htTPs:").isScheme("http"));

		assertFalse(new AnyURI("htTP").isScheme("http"));
		assertFalse(new AnyURI("htTPs").isScheme("https"));

		assertFalse(new AnyURI("/path").isScheme("http"));
		assertFalse(new AnyURI("./path").isScheme("https"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSchemeEmptyScheme1() {
		new AnyURI(":blarg").isScheme("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSchemeEmptyScheme2() {
		new AnyURI(":").isScheme("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSchemeInvalidScheme() {
		new AnyURI("http:").isScheme("<<>>");
	}

	public void testIsSchemeInvalidSchemeTooLongToValidate() {
		assertFalse(
			"Scheme will not validate when longer than possible value, due to short-cut",
			new AnyURI("http:").isScheme("<<.>>")
		);
	}

	@Test
	public void testGetScheme() {
		assertEquals("htTP", new AnyURI("htTP:").getScheme());
		assertEquals("htTPs", new AnyURI("htTPs:").getScheme());
		assertEquals("htTP", new AnyURI("htTP:?").getScheme());
		assertEquals("htTP", new AnyURI("htTP:?param?&param2=value#anc?h&or").getScheme());
		assertEquals("htTP", new AnyURI("htTP:#fragment#").getScheme());
		assertEquals("htTP", new AnyURI("htTP:#fragment?notParam").getScheme());


		assertNull(new AnyURI("htTP").getScheme());
		assertNull(new AnyURI("htTPs").getScheme());

		assertNull(new AnyURI("/path").getScheme());
		assertNull(new AnyURI("./path").getScheme());

		assertNull(new AnyURI(":blarg").getScheme());
		assertNull(new AnyURI(":").getScheme());
	}

	// TODO: Test more scheme methods like write/append

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test HierPart">
	@Test
	public void testGetPathEnd() {
		assertEquals(5, new AnyURI("htTP:").getPathEnd());
		assertEquals(5, new AnyURI("htTP:?").getPathEnd());
		assertEquals(5, new AnyURI("htTP:?param?&param2=value#anc?h&or").getPathEnd());
		assertEquals(5, new AnyURI("htTP:#fragment#").getPathEnd());
		assertEquals(5, new AnyURI("htTP:#fragment?notParam").getPathEnd());

		assertEquals(2, new AnyURI("./").getPathEnd());
		assertEquals(2, new AnyURI("./?").getPathEnd());
		assertEquals(2, new AnyURI("./?param?&param2=value#anc?h&or").getPathEnd());
		assertEquals(2, new AnyURI("./#fragment#").getPathEnd());
		assertEquals(2, new AnyURI("./#fragment?notParam").getPathEnd());

		assertEquals(0, new AnyURI("").getPathEnd());
		assertEquals(0, new AnyURI("?").getPathEnd());
		assertEquals(0, new AnyURI("?param?&param2=value#anc?h&or").getPathEnd());
		assertEquals(0, new AnyURI("#fragment#").getPathEnd());
		assertEquals(0, new AnyURI("#fragment?notParam").getPathEnd());
	}

	@Test
	public void testGetHierPart() {
		assertEquals("", new AnyURI("htTP:").getHierPart());
		assertEquals("", new AnyURI("htTP:?").getHierPart());
		assertEquals("", new AnyURI("htTP:?param?&param2=value#anc?h&or").getHierPart());
		assertEquals("", new AnyURI("htTP:#fragment#").getHierPart());
		assertEquals("", new AnyURI("htTP:#fragment?notParam").getHierPart());

		assertSame("./", new AnyURI("./").getHierPart());
		assertEquals("./", new AnyURI("./?").getHierPart());
		assertEquals("./", new AnyURI("./?param?&param2=value#anc?h&or").getHierPart());
		assertEquals("./", new AnyURI("./#fragment#").getHierPart());
		assertEquals("./", new AnyURI("./#fragment?notParam").getHierPart());

		assertSame("", new AnyURI("").getHierPart());
		assertEquals("", new AnyURI("?").getHierPart());
		assertEquals("", new AnyURI("?param?&param2=value#anc?h&or").getHierPart());
		assertEquals("", new AnyURI("#fragment#").getHierPart());
		assertEquals("", new AnyURI("#fragment?notParam").getHierPart());
	}

	private static String captureWriteHierPart(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).writeHierPart(out);
		return out.toString();
	}

	@Test
	public void testWriteHierPart() throws IOException {
		assertEquals("", captureWriteHierPart("htTP:"));
		assertEquals("", captureWriteHierPart("htTP:?"));
		assertEquals("", captureWriteHierPart("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteHierPart("htTP:#fragment#"));
		assertEquals("", captureWriteHierPart("htTP:#fragment?notParam"));

		assertEquals("./", captureWriteHierPart("./"));
		assertEquals("./", captureWriteHierPart("./?"));
		assertEquals("./", captureWriteHierPart("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureWriteHierPart("./#fragment#"));
		assertEquals("./", captureWriteHierPart("./#fragment?notParam"));

		assertEquals("", captureWriteHierPart(""));
		assertEquals("", captureWriteHierPart("?"));
		assertEquals("", captureWriteHierPart("?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteHierPart("#fragment#"));
		assertEquals("", captureWriteHierPart("#fragment?notParam"));
	}

	private static String captureWriteHierPart(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).writeHierPart(out, encoder);
		return out.toString();
	}

	@Test
	public void testWriteHierPartNullEncoder() throws IOException {
		assertEquals("&", captureWriteHierPart("htTP:&", null));
		assertEquals("&", captureWriteHierPart("htTP:&?", null));
		assertEquals("&", captureWriteHierPart("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("&", captureWriteHierPart("htTP:&#fragment#", null));
		assertEquals("&", captureWriteHierPart("htTP:&#fragment?notParam", null));

		assertEquals("./<>", captureWriteHierPart("./<>", null));
		assertEquals("./<>", captureWriteHierPart("./<>?", null));
		assertEquals("./<>", captureWriteHierPart("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("./<>", captureWriteHierPart("./<>#fragment#", null));
		assertEquals("./<>", captureWriteHierPart("./<>#fragment?notParam", null));

		assertEquals("", captureWriteHierPart("", null));
		assertEquals("", captureWriteHierPart("?", null));
		assertEquals("", captureWriteHierPart("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteHierPart("#fragment#", null));
		assertEquals("", captureWriteHierPart("#fragment?notParam", null));
	}

	@Test
	public void testWriteHierPartXhtmlEncoder() throws IOException {
		assertEquals("&amp;", captureWriteHierPart("htTP:&", textInXhtmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&?", textInXhtmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&#fragment#", textInXhtmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&#fragment?notParam", textInXhtmlEncoder));

		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>?", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>#fragment#", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureWriteHierPart("", textInXhtmlEncoder));
		assertEquals("", captureWriteHierPart("?", textInXhtmlEncoder));
		assertEquals("", captureWriteHierPart("?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureWriteHierPart("#fragment#", textInXhtmlEncoder));
		assertEquals("", captureWriteHierPart("#fragment?notParam", textInXhtmlEncoder));
	}

	private static String captureAppendHierPartOut(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).appendHierPart(out);
		return out.toString();
	}

	@Test
	public void testAppendHierPartOut() throws IOException {
		assertEquals("", captureAppendHierPartOut("htTP:"));
		assertEquals("", captureAppendHierPartOut("htTP:?"));
		assertEquals("", captureAppendHierPartOut("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartOut("htTP:#fragment#"));
		assertEquals("", captureAppendHierPartOut("htTP:#fragment?notParam"));

		assertEquals("./", captureAppendHierPartOut("./"));
		assertEquals("./", captureAppendHierPartOut("./?"));
		assertEquals("./", captureAppendHierPartOut("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureAppendHierPartOut("./#fragment#"));
		assertEquals("./", captureAppendHierPartOut("./#fragment?notParam"));

		assertEquals("", captureAppendHierPartOut(""));
		assertEquals("", captureAppendHierPartOut("?"));
		assertEquals("", captureAppendHierPartOut("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartOut("#fragment#"));
		assertEquals("", captureAppendHierPartOut("#fragment?notParam"));
	}

	private static String captureAppendHierPartOut(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).appendHierPart(encoder, out);
		return out.toString();
	}

	@Test
	public void testAppendHierPartOutNullEncoder() throws IOException {
		assertEquals("&", captureAppendHierPartOut("htTP:&", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&?", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&#fragment#", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&#fragment?notParam", null));

		assertEquals("./<>", captureAppendHierPartOut("./<>", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>?", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>#fragment#", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>#fragment?notParam", null));

		assertEquals("", captureAppendHierPartOut("", null));
		assertEquals("", captureAppendHierPartOut("?", null));
		assertEquals("", captureAppendHierPartOut("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendHierPartOut("#fragment#", null));
		assertEquals("", captureAppendHierPartOut("#fragment?notParam", null));
	}

	@Test
	public void testAppendHierPartOutXhtmlEncoder() throws IOException {
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&", textInXhtmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&?", textInXhtmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&#fragment#", textInXhtmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&#fragment?notParam", textInXhtmlEncoder));

		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>?", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>#fragment#", textInXhtmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureAppendHierPartOut("", textInXhtmlEncoder));
		assertEquals("", captureAppendHierPartOut("?", textInXhtmlEncoder));
		assertEquals("", captureAppendHierPartOut("?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureAppendHierPartOut("#fragment#", textInXhtmlEncoder));
		assertEquals("", captureAppendHierPartOut("#fragment?notParam", textInXhtmlEncoder));
	}

	private static String captureAppendHierPartStringBuilder(String url) throws IOException {
		StringBuilder sb = new StringBuilder(url.length());
		new AnyURI(url).appendHierPart(sb);
		return sb.toString();
	}

	@Test
	public void testAppendHierPartStringBuilder() throws IOException {
		assertEquals("", captureAppendHierPartStringBuilder("htTP:"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:?"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:#fragment#"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:#fragment?notParam"));

		assertEquals("./", captureAppendHierPartStringBuilder("./"));
		assertEquals("./", captureAppendHierPartStringBuilder("./?"));
		assertEquals("./", captureAppendHierPartStringBuilder("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureAppendHierPartStringBuilder("./#fragment#"));
		assertEquals("./", captureAppendHierPartStringBuilder("./#fragment?notParam"));

		assertEquals("", captureAppendHierPartStringBuilder(""));
		assertEquals("", captureAppendHierPartStringBuilder("?"));
		assertEquals("", captureAppendHierPartStringBuilder("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuilder("#fragment#"));
		assertEquals("", captureAppendHierPartStringBuilder("#fragment?notParam"));
	}

	private static String captureAppendHierPartStringBuffer(String url) throws IOException {
		StringBuffer sb = new StringBuffer(url.length());
		new AnyURI(url).appendHierPart(sb);
		return sb.toString();
	}

	@Test
	public void testAppendHierPartStringBuffer() throws IOException {
		assertEquals("", captureAppendHierPartStringBuffer("htTP:"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:?"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:#fragment#"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:#fragment?notParam"));

		assertEquals("./", captureAppendHierPartStringBuffer("./"));
		assertEquals("./", captureAppendHierPartStringBuffer("./?"));
		assertEquals("./", captureAppendHierPartStringBuffer("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureAppendHierPartStringBuffer("./#fragment#"));
		assertEquals("./", captureAppendHierPartStringBuffer("./#fragment?notParam"));

		assertEquals("", captureAppendHierPartStringBuffer(""));
		assertEquals("", captureAppendHierPartStringBuffer("?"));
		assertEquals("", captureAppendHierPartStringBuffer("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuffer("#fragment#"));
		assertEquals("", captureAppendHierPartStringBuffer("#fragment?notParam"));
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Query">
	@Test
	public void testGetQueryIndex() {
		assertEquals(-1, new AnyURI("htTP:").getQueryIndex());
		assertEquals(5, new AnyURI("htTP:?").getQueryIndex());
		assertEquals(5, new AnyURI("htTP:?param?&param2=value#anc?h&or").getQueryIndex());
		assertEquals(-1, new AnyURI("htTP:#fragment#").getQueryIndex());
		assertEquals(-1, new AnyURI("htTP:#fragment?notParam").getQueryIndex());

		assertEquals(-1, new AnyURI("./").getQueryIndex());
		assertEquals(2, new AnyURI("./?").getQueryIndex());
		assertEquals(2, new AnyURI("./?param?&param2=value#anc?h&or").getQueryIndex());
		assertEquals(-1, new AnyURI("./#fragment#").getQueryIndex());
		assertEquals(-1, new AnyURI("./#fragment?notParam").getQueryIndex());

		assertEquals(-1, new AnyURI("").getQueryIndex());
		assertEquals(0, new AnyURI("?").getQueryIndex());
		assertEquals(0, new AnyURI("?param?&param2=value#anc?h&or").getQueryIndex());
		assertEquals(-1, new AnyURI("#fragment#").getQueryIndex());
		assertEquals(-1, new AnyURI("#fragment?notParam").getQueryIndex());
	}

	@Test
	public void testHasQuery() {
		assertFalse(new AnyURI("htTP:").hasQuery());
		assertTrue(new AnyURI("htTP:?").hasQuery());
		assertTrue(new AnyURI("htTP:?param?&param2=value#anc?h&or").hasQuery());
		assertFalse(new AnyURI("htTP:#fragment#").hasQuery());
		assertFalse(new AnyURI("htTP:#fragment?notParam").hasQuery());

		assertFalse(new AnyURI("./").hasQuery());
		assertTrue(new AnyURI("./?").hasQuery());
		assertTrue(new AnyURI("./?param?&param2=value#anc?h&or").hasQuery());
		assertFalse(new AnyURI("./#fragment#").hasQuery());
		assertFalse(new AnyURI("./#fragment?notParam").hasQuery());

		assertFalse(new AnyURI("").hasQuery());
		assertTrue(new AnyURI("?").hasQuery());
		assertTrue(new AnyURI("?param?&param2=value#anc?h&or").hasQuery());
		assertFalse(new AnyURI("#fragment#").hasQuery());
		assertFalse(new AnyURI("#fragment?notParam").hasQuery());
	}

	@Test
	public void testGetQueryString() {
		assertNull(new AnyURI("htTP:").getQueryString());
		assertEquals("", new AnyURI("htTP:?").getQueryString());
		assertEquals("param?&param2=value", new AnyURI("htTP:?param?&param2=value#anc?h&or").getQueryString());
		assertNull(new AnyURI("htTP:#fragment#").getQueryString());
		assertNull(new AnyURI("htTP:#fragment?notParam").getQueryString());

		assertNull(new AnyURI("./").getQueryString());
		assertEquals("", new AnyURI("./?").getQueryString());
		assertEquals("param?&param2=value", new AnyURI("./?param?&param2=value#anc?h&or").getQueryString());
		assertNull(new AnyURI("./#fragment#").getQueryString());
		assertNull(new AnyURI("./#fragment?notParam").getQueryString());

		assertNull(new AnyURI("").getQueryString());
		assertEquals("", new AnyURI("?").getQueryString());
		assertEquals("param?&param2=value", new AnyURI("?param?&param2=value#anc?h&or").getQueryString());
		assertNull(new AnyURI("#fragment#").getQueryString());
		assertNull(new AnyURI("#fragment?notParam").getQueryString());
	}

	private static String captureWriteQueryString(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).writeQueryString(out);
		return out.toString();
	}

	@Test
	public void testWriteQueryString() throws IOException {
		assertEquals("", captureWriteQueryString("htTP:"));
		assertEquals("", captureWriteQueryString("htTP:?"));
		assertEquals("param?&param2=value", captureWriteQueryString("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteQueryString("htTP:#fragment#"));
		assertEquals("", captureWriteQueryString("htTP:#fragment?notParam"));

		assertEquals("", captureWriteQueryString("./"));
		assertEquals("", captureWriteQueryString("./?"));
		assertEquals("param?&param2=value", captureWriteQueryString("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteQueryString("./#fragment#"));
		assertEquals("", captureWriteQueryString("./#fragment?notParam"));

		assertEquals("", captureWriteQueryString(""));
		assertEquals("", captureWriteQueryString("?"));
		assertEquals("param?&param2=value", captureWriteQueryString("?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteQueryString("#fragment#"));
		assertEquals("", captureWriteQueryString("#fragment?notParam"));
	}

	private static String captureWriteQueryString(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).writeQueryString(out, encoder);
		return out.toString();
	}

	@Test
	public void testWriteQueryStringNullEncoder() throws IOException {
		assertEquals("", captureWriteQueryString("htTP:&", null));
		assertEquals("", captureWriteQueryString("htTP:&?", null));
		assertEquals("param?&param2=value", captureWriteQueryString("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteQueryString("htTP:&#fragment#", null));
		assertEquals("", captureWriteQueryString("htTP:&#fragment?notParam", null));

		assertEquals("", captureWriteQueryString("./<>", null));
		assertEquals("", captureWriteQueryString("./<>?", null));
		assertEquals("param?&param2=value", captureWriteQueryString("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteQueryString("./<>#fragment#", null));
		assertEquals("", captureWriteQueryString("./<>#fragment?notParam", null));

		assertEquals("", captureWriteQueryString("", null));
		assertEquals("", captureWriteQueryString("?", null));
		assertEquals("param?&param2=value", captureWriteQueryString("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteQueryString("#fragment#", null));
		assertEquals("", captureWriteQueryString("#fragment?notParam", null));
	}

	@Test
	public void testWriteQueryStringXhtmlEncoder() throws IOException {
		assertEquals("", captureWriteQueryString("htTP:&", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("htTP:&?", textInXhtmlEncoder));
		assertEquals("param?&amp;param2=value", captureWriteQueryString("htTP:&?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("htTP:&#fragment#", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("htTP:&#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureWriteQueryString("./<>", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("./<>?", textInXhtmlEncoder));
		assertEquals("param?&amp;param2=value", captureWriteQueryString("./<>?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("./<>#fragment#", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("./<>#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureWriteQueryString("", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("?", textInXhtmlEncoder));
		assertEquals("param?&amp;param2=value", captureWriteQueryString("?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("#fragment#", textInXhtmlEncoder));
		assertEquals("", captureWriteQueryString("#fragment?notParam", textInXhtmlEncoder));
	}

	private static String captureAppendQueryStringOut(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).appendQueryString(out);
		return out.toString();
	}

	@Test
	public void testAppendQueryStringOut() throws IOException {
		assertEquals("", captureAppendQueryStringOut("htTP:"));
		assertEquals("", captureAppendQueryStringOut("htTP:?"));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringOut("htTP:#fragment#"));
		assertEquals("", captureAppendQueryStringOut("htTP:#fragment?notParam"));

		assertEquals("", captureAppendQueryStringOut("./"));
		assertEquals("", captureAppendQueryStringOut("./?"));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringOut("./#fragment#"));
		assertEquals("", captureAppendQueryStringOut("./#fragment?notParam"));

		assertEquals("", captureAppendQueryStringOut(""));
		assertEquals("", captureAppendQueryStringOut("?"));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringOut("#fragment#"));
		assertEquals("", captureAppendQueryStringOut("#fragment?notParam"));
	}

	private static String captureAppendQueryStringOut(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).appendQueryString(encoder, out);
		return out.toString();
	}

	@Test
	public void testAppendQueryStringOutNullEncoder() throws IOException {
		assertEquals("", captureAppendQueryStringOut("htTP:&", null));
		assertEquals("", captureAppendQueryStringOut("htTP:&?", null));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment#", null));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment?notParam", null));

		assertEquals("", captureAppendQueryStringOut("./<>", null));
		assertEquals("", captureAppendQueryStringOut("./<>?", null));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment#", null));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment?notParam", null));

		assertEquals("", captureAppendQueryStringOut("", null));
		assertEquals("", captureAppendQueryStringOut("?", null));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendQueryStringOut("#fragment#", null));
		assertEquals("", captureAppendQueryStringOut("#fragment?notParam", null));
	}

	@Test
	public void testAppendQueryStringOutXhtmlEncoder() throws IOException {
		assertEquals("", captureAppendQueryStringOut("htTP:&", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("htTP:&?", textInXhtmlEncoder));
		assertEquals("param?&amp;param2=value", captureAppendQueryStringOut("htTP:&?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment#", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureAppendQueryStringOut("./<>", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("./<>?", textInXhtmlEncoder));
		assertEquals("param?&amp;param2=value", captureAppendQueryStringOut("./<>?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment#", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureAppendQueryStringOut("", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("?", textInXhtmlEncoder));
		assertEquals("param?&amp;param2=value", captureAppendQueryStringOut("?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("#fragment#", textInXhtmlEncoder));
		assertEquals("", captureAppendQueryStringOut("#fragment?notParam", textInXhtmlEncoder));
	}

	private static String captureAppendQueryStringStringBuilder(String url) throws IOException {
		StringBuilder sb = new StringBuilder(url.length());
		new AnyURI(url).appendQueryString(sb);
		return sb.toString();
	}

	@Test
	public void testAppendQueryStringStringBuilder() throws IOException {
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:"));
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuilder("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuilder("./"));
		assertEquals("", captureAppendQueryStringStringBuilder("./?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuilder("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuilder("./#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuilder("./#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuilder(""));
		assertEquals("", captureAppendQueryStringStringBuilder("?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuilder("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuilder("#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuilder("#fragment?notParam"));
	}

	private static String captureAppendQueryStringStringBuffer(String url) throws IOException {
		StringBuffer sb = new StringBuffer(url.length());
		new AnyURI(url).appendQueryString(sb);
		return sb.toString();
	}

	@Test
	public void testAppendQueryStringStringBuffer() throws IOException {
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:"));
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuffer("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuffer("./"));
		assertEquals("", captureAppendQueryStringStringBuffer("./?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuffer("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuffer("./#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuffer("./#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuffer(""));
		assertEquals("", captureAppendQueryStringStringBuffer("?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuffer("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuffer("#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuffer("#fragment?notParam"));
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Fragment">
	@Test
	public void testGetFragmentIndex() {
		assertEquals(-1, new AnyURI("htTP:").getFragmentIndex());
		assertEquals(-1, new AnyURI("htTP:?").getFragmentIndex());
		assertEquals(25, new AnyURI("htTP:?param?&param2=value#anc?h&or").getFragmentIndex());
		assertEquals(5, new AnyURI("htTP:#fragment#").getFragmentIndex());
		assertEquals(5, new AnyURI("htTP:#fragment?notParam").getFragmentIndex());

		assertEquals(-1, new AnyURI("./").getFragmentIndex());
		assertEquals(-1, new AnyURI("./?").getFragmentIndex());
		assertEquals(22, new AnyURI("./?param?&param2=value#anc?h&or").getFragmentIndex());
		assertEquals(2, new AnyURI("./#fragment#").getFragmentIndex());
		assertEquals(2, new AnyURI("./#fragment?notParam").getFragmentIndex());

		assertEquals(-1, new AnyURI("").getFragmentIndex());
		assertEquals(-1, new AnyURI("?").getFragmentIndex());
		assertEquals(20, new AnyURI("?param?&param2=value#anc?h&or").getFragmentIndex());
		assertEquals(0, new AnyURI("#fragment#").getFragmentIndex());
		assertEquals(0, new AnyURI("#fragment?notParam").getFragmentIndex());
	}

	@Test
	public void testHasFragment() {
		assertFalse(new AnyURI("htTP:").hasFragment());
		assertFalse(new AnyURI("htTP:?").hasFragment());
		assertTrue(new AnyURI("htTP:?param?&param2=value#anc?h&or").hasFragment());
		assertTrue(new AnyURI("htTP:#fragment#").hasFragment());
		assertTrue(new AnyURI("htTP:#fragment?notParam").hasFragment());

		assertFalse(new AnyURI("./").hasFragment());
		assertFalse(new AnyURI("./?").hasFragment());
		assertTrue(new AnyURI("./?param?&param2=value#anc?h&or").hasFragment());
		assertTrue(new AnyURI("./#fragment#").hasFragment());
		assertTrue(new AnyURI("./#fragment?notParam").hasFragment());

		assertFalse(new AnyURI("").hasFragment());
		assertFalse(new AnyURI("?").hasFragment());
		assertTrue(new AnyURI("?param?&param2=value#anc?h&or").hasFragment());
		assertTrue(new AnyURI("#fragment#").hasFragment());
		assertTrue(new AnyURI("#fragment?notParam").hasFragment());
	}

	@Test
	public void testGetFragment() {
		assertNull(new AnyURI("htTP:").getFragment());
		assertNull(new AnyURI("htTP:?").getFragment());
		assertEquals("anc?h&or", new AnyURI("htTP:?param?&param2=value#anc?h&or").getFragment());
		assertEquals("fragment#", new AnyURI("htTP:#fragment#").getFragment());
		assertEquals("fragment?notParam", new AnyURI("htTP:#fragment?notParam").getFragment());

		assertNull(new AnyURI("./").getFragment());
		assertNull(new AnyURI("./?").getFragment());
		assertEquals("anc?h&or", new AnyURI("./?param?&param2=value#anc?h&or").getFragment());
		assertEquals("fragment#", new AnyURI("./#fragment#").getFragment());
		assertEquals("fragment?notParam", new AnyURI("./#fragment?notParam").getFragment());

		assertNull(new AnyURI("").getFragment());
		assertNull(new AnyURI("?").getFragment());
		assertEquals("anc?h&or", new AnyURI("?param?&param2=value#anc?h&or").getFragment());
		assertEquals("fragment#", new AnyURI("#fragment#").getFragment());
		assertEquals("fragment?notParam", new AnyURI("#fragment?notParam").getFragment());
	}

	private static String captureWriteFragment(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).writeFragment(out);
		return out.toString();
	}

	@Test
	public void testWriteFragment() throws IOException {
		assertEquals("", captureWriteFragment("htTP:"));
		assertEquals("", captureWriteFragment("htTP:?"));
		assertEquals("anc?h&or", captureWriteFragment("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureWriteFragment("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureWriteFragment("htTP:#fragment?notParam"));

		assertEquals("", captureWriteFragment("./"));
		assertEquals("", captureWriteFragment("./?"));
		assertEquals("anc?h&or", captureWriteFragment("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureWriteFragment("./#fragment#"));
		assertEquals("fragment?notParam", captureWriteFragment("./#fragment?notParam"));

		assertEquals("", captureWriteFragment(""));
		assertEquals("", captureWriteFragment("?"));
		assertEquals("anc?h&or", captureWriteFragment("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureWriteFragment("#fragment#"));
		assertEquals("fragment?notParam", captureWriteFragment("#fragment?notParam"));
	}

	private static String captureWriteFragment(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).writeFragment(out, encoder);
		return out.toString();
	}

	@Test
	public void testWriteFragmentNullEncoder() throws IOException {
		assertEquals("", captureWriteFragment("htTP:&", null));
		assertEquals("", captureWriteFragment("htTP:&?", null));
		assertEquals("anc?h&or", captureWriteFragment("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureWriteFragment("htTP:&#fragment#", null));
		assertEquals("fragment?notParam", captureWriteFragment("htTP:&#fragment?notParam", null));

		assertEquals("", captureWriteFragment("./<>", null));
		assertEquals("", captureWriteFragment("./<>?", null));
		assertEquals("anc?h&or", captureWriteFragment("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureWriteFragment("./<>#fragment#", null));
		assertEquals("fragment?notParam", captureWriteFragment("./<>#fragment?notParam", null));

		assertEquals("", captureWriteFragment("", null));
		assertEquals("", captureWriteFragment("?", null));
		assertEquals("anc?h&or", captureWriteFragment("?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureWriteFragment("#fragment#", null));
		assertEquals("fragment?notParam", captureWriteFragment("#fragment?notParam", null));
	}

	@Test
	public void testWriteFragmentXhtmlEncoder() throws IOException {
		assertEquals("", captureWriteFragment("htTP:&", textInXhtmlEncoder));
		assertEquals("", captureWriteFragment("htTP:&?", textInXhtmlEncoder));
		assertEquals("anc?h&amp;or", captureWriteFragment("htTP:&?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("fragment#", captureWriteFragment("htTP:&#fragment#", textInXhtmlEncoder));
		assertEquals("fragment?notParam", captureWriteFragment("htTP:&#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureWriteFragment("./<>", textInXhtmlEncoder));
		assertEquals("", captureWriteFragment("./<>?", textInXhtmlEncoder));
		assertEquals("anc?h&amp;or", captureWriteFragment("./<>?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("fragment#", captureWriteFragment("./<>#fragment#", textInXhtmlEncoder));
		assertEquals("fragment?notParam", captureWriteFragment("./<>#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureWriteFragment("", textInXhtmlEncoder));
		assertEquals("", captureWriteFragment("?", textInXhtmlEncoder));
		assertEquals("anc?h&amp;or", captureWriteFragment("?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("fragment#", captureWriteFragment("#fragment#", textInXhtmlEncoder));
		assertEquals("fragment?notParam", captureWriteFragment("#fragment?notParam", textInXhtmlEncoder));
	}

	private static String captureAppendFragmentOut(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).appendFragment(out);
		return out.toString();
	}

	@Test
	public void testAppendFragmentOut() throws IOException {
		assertEquals("", captureAppendFragmentOut("htTP:"));
		assertEquals("", captureAppendFragmentOut("htTP:?"));
		assertEquals("anc?h&or", captureAppendFragmentOut("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentOut("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentOut("htTP:#fragment?notParam"));

		assertEquals("", captureAppendFragmentOut("./"));
		assertEquals("", captureAppendFragmentOut("./?"));
		assertEquals("anc?h&or", captureAppendFragmentOut("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentOut("./#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentOut("./#fragment?notParam"));

		assertEquals("", captureAppendFragmentOut(""));
		assertEquals("", captureAppendFragmentOut("?"));
		assertEquals("anc?h&or", captureAppendFragmentOut("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentOut("#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentOut("#fragment?notParam"));
	}

	private static String captureAppendFragmentOut(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new AnyURI(url).appendFragment(encoder, out);
		return out.toString();
	}

	@Test
	public void testAppendFragmentOutNullEncoder() throws IOException {
		assertEquals("", captureAppendFragmentOut("htTP:&", null));
		assertEquals("", captureAppendFragmentOut("htTP:&?", null));
		assertEquals("anc?h&or", captureAppendFragmentOut("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureAppendFragmentOut("htTP:&#fragment#", null));
		assertEquals("fragment?notParam", captureAppendFragmentOut("htTP:&#fragment?notParam", null));

		assertEquals("", captureAppendFragmentOut("./<>", null));
		assertEquals("", captureAppendFragmentOut("./<>?", null));
		assertEquals("anc?h&or", captureAppendFragmentOut("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureAppendFragmentOut("./<>#fragment#", null));
		assertEquals("fragment?notParam", captureAppendFragmentOut("./<>#fragment?notParam", null));

		assertEquals("", captureAppendFragmentOut("", null));
		assertEquals("", captureAppendFragmentOut("?", null));
		assertEquals("anc?h&or", captureAppendFragmentOut("?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureAppendFragmentOut("#fragment#", null));
		assertEquals("fragment?notParam", captureAppendFragmentOut("#fragment?notParam", null));
	}

	@Test
	public void testAppendFragmentOutXhtmlEncoder() throws IOException {
		assertEquals("", captureAppendFragmentOut("htTP:&", textInXhtmlEncoder));
		assertEquals("", captureAppendFragmentOut("htTP:&?", textInXhtmlEncoder));
		assertEquals("anc?h&amp;or", captureAppendFragmentOut("htTP:&?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("fragment#", captureAppendFragmentOut("htTP:&#fragment#", textInXhtmlEncoder));
		assertEquals("fragment?notParam", captureAppendFragmentOut("htTP:&#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureAppendFragmentOut("./<>", textInXhtmlEncoder));
		assertEquals("", captureAppendFragmentOut("./<>?", textInXhtmlEncoder));
		assertEquals("anc?h&amp;or", captureAppendFragmentOut("./<>?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("fragment#", captureAppendFragmentOut("./<>#fragment#", textInXhtmlEncoder));
		assertEquals("fragment?notParam", captureAppendFragmentOut("./<>#fragment?notParam", textInXhtmlEncoder));

		assertEquals("", captureAppendFragmentOut("", textInXhtmlEncoder));
		assertEquals("", captureAppendFragmentOut("?", textInXhtmlEncoder));
		assertEquals("anc?h&amp;or", captureAppendFragmentOut("?param?&param2=value#anc?h&or", textInXhtmlEncoder));
		assertEquals("fragment#", captureAppendFragmentOut("#fragment#", textInXhtmlEncoder));
		assertEquals("fragment?notParam", captureAppendFragmentOut("#fragment?notParam", textInXhtmlEncoder));
	}

	private static String captureAppendFragmentStringBuilder(String url) throws IOException {
		StringBuilder sb = new StringBuilder(url.length());
		new AnyURI(url).appendFragment(sb);
		return sb.toString();
	}

	@Test
	public void testAppendFragmentStringBuilder() throws IOException {
		assertEquals("", captureAppendFragmentStringBuilder("htTP:"));
		assertEquals("", captureAppendFragmentStringBuilder("htTP:?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuilder("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuilder("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuilder("htTP:#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuilder("./"));
		assertEquals("", captureAppendFragmentStringBuilder("./?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuilder("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuilder("./#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuilder("./#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuilder(""));
		assertEquals("", captureAppendFragmentStringBuilder("?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuilder("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuilder("#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuilder("#fragment?notParam"));
	}

	private static String captureAppendFragmentStringBuffer(String url) throws IOException {
		StringBuffer sb = new StringBuffer(url.length());
		new AnyURI(url).appendFragment(sb);
		return sb.toString();
	}

	@Test
	public void testAppendFragmentStringBuffer() throws IOException {
		assertEquals("", captureAppendFragmentStringBuffer("htTP:"));
		assertEquals("", captureAppendFragmentStringBuffer("htTP:?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuffer("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuffer("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuffer("htTP:#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuffer("./"));
		assertEquals("", captureAppendFragmentStringBuffer("./?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuffer("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuffer("./#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuffer("./#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuffer(""));
		assertEquals("", captureAppendFragmentStringBuffer("?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuffer("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuffer("#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuffer("#fragment?notParam"));
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Encode/Decode URI">
	private static void testEncodeURI(String message, String asciiUrl, String unicodeUrl) {
		assertSame(message, asciiUrl, new AnyURI(asciiUrl).toURI().toString());
		assertEquals(message, asciiUrl, new AnyURI(unicodeUrl).toURI().toString());
	}

	@Test
	public void testEncodeURI() {
		testEncodeURI(
			null,
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A",
			"http://localhost/かおり"
		);
		testEncodeURI(
			null,
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?",
			"http://localhost/かおり?"
		);
		testEncodeURI(
			null,
			"://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?param?&param2=value#anc?h&or",
			"://localhost/かおり?param?&param2=value#anc?h&or"
		);
		testEncodeURI(
			null,
			"//localhost/%E3%81%8B%E3%81%8A%E3%82%8A#fragment#",
			"//localhost/かおり#fragment#"
		);
		testEncodeURI(
			null,
			"/%E3%81%8B%E3%81%8A%E3%82%8A#fragment?notParam",
			"/かおり#fragment?notParam"
		);
		testEncodeURI(
			null,
			"%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#fragment?notParam%%%",
			"かおり BBB#fragment?notParam%%%"
		);
		testEncodeURI(
			"Plus (+) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"%E3%81%8B%E3%81%8A%E3%82%8A+BBB#fragment?notParam%%%",
			"かおり+BBB#fragment?notParam%%%"
		);
		testEncodeURI(
			"Encoded plus (%2B) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2BBBB#fragment?notParam%%%",
			"かおり%2BBBB#fragment?notParam%%%"
		);
		testEncodeURI(
			"Encoded slash (%2F) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2FBBB#fragment?notParam%%%",
			"かおり%2FBBB#fragment?notParam%%%"
		);
		testEncodeURI(
			null,
			"?",
			"?"
		);
		testEncodeURI(
			null,
			"#",
			"#"
		);
		testEncodeURI(
			null,
			"",
			""
		);
		testEncodeURI(
			"Invalid US-ASCII characters must remain invalid",
			"/\u000c",
			"/\u000c"
		);
	}

	private static void testDecodeURI(String message, String unicodeUrl, String asciiUrl) {
		assertSame(message, unicodeUrl, new AnyURI(unicodeUrl).toIRI().toString());
		assertEquals(message, unicodeUrl, new AnyURI(asciiUrl).toIRI().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeURIInvalidEncoded() {
		testDecodeURI(
			null,
			"かおり BBB#fragment?notParam%%%",
			"%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#fragment?notParam%%%"
		);
	}

	@Test
	public void testDecodeURI() {
		testDecodeURI(
			null,
			"http://localhost/かおり",
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A"
		);
		testDecodeURI(
			null,
			"http://localhost/かおり?",
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?"
		);
		testDecodeURI(
			null,
			"://localhost/かおり?param?&param2=value#anc?h&or",
			"://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?param?&param2=value#anc?h&or"
		);
		testDecodeURI(
			null,
			"//localhost/かおり#fragment#",
			"//localhost/%E3%81%8B%E3%81%8A%E3%82%8A#fragment#"
		);
		testDecodeURI(
			null,
			"/かおり#fragment?notParam",
			"/%E3%81%8B%E3%81%8A%E3%82%8A#fragment?notParam"
		);
		testDecodeURI(
			null,
			"かおり BBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			"Plus (+) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"かおり+BBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A+BBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			"Encoded plus (%2B) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"かおり%2BBBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2BBBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			"Encoded slash (%2F) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"かおり%2FBBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2FBBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			null,
			"?",
			"?"
		);
		testDecodeURI(
			null,
			"#",
			"#"
		);
		testDecodeURI(
			null,
			"",
			""
		);
		testDecodeURI(
			"Invalid US-ASCII characters must not be decoded",
			"/%0C",
			"/%0C"
		);
		assertEquals(
			"Invalid US-ASCII characters must remain invalid",
			"/\u000c",
			"/\u000c"
		);
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Query String Mutators">
	private static void testSetQueryStringSame(String url, String newQuery) {
		AnyURI anyURI = new AnyURI(url);
		assertSame(anyURI, anyURI.setQueryString(newQuery));
	}

	@Test
	public void testSetQueryStringSame() {
		testSetQueryStringSame("", null);
		testSetQueryStringSame("?", "");
		testSetQueryStringSame("#", null);
		testSetQueryStringSame("?#", "");

		testSetQueryStringSame("htTP:", null);
		testSetQueryStringSame("htTP:?", "");
		testSetQueryStringSame("htTP:?param?&param2=value#anc?h&or", "param?&param2=value");
		testSetQueryStringSame("htTP:#fragment#", null);
		testSetQueryStringSame("htTP:#fragment?notParam", null);
	}

	private static void testSetQueryStringRemoveQuery(String expected, String url) {
		assertEquals(expected, new AnyURI(url).setQueryString(null).toString());
	}

	@Test
	public void testSetQueryStringRemoveQuery() {
		testSetQueryStringRemoveQuery("", "?");
		testSetQueryStringRemoveQuery("#", "?#");

		testSetQueryStringRemoveQuery("htTP:", "htTP:?");
		testSetQueryStringRemoveQuery("htTP:#anc?h&or", "htTP:?param?&param2=value#anc?h&or");
	}

	private static void testSetQueryStringReplaced(String expected, String url, String newQuery) {
		assertEquals(expected, new AnyURI(url).setQueryString(newQuery).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetQueryStringReplacedNotAllowFragment() {
		testSetQueryStringReplaced("?##", "#", "#");
	}

	@Test
	public void testSetQueryStringReplaced() {
		testSetQueryStringReplaced("?new", "", "new");
		testSetQueryStringReplaced("??", "?", "?");
		testSetQueryStringReplaced("?new&other?other#", "?#", "new&other?other");

		testSetQueryStringReplaced("htTP:?new", "htTP:", "new");
		testSetQueryStringReplaced("htTP:?new", "htTP:?", "new");
		testSetQueryStringReplaced("htTP:?new#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new");
		testSetQueryStringReplaced("htTP:?new#fragment#", "htTP:#fragment#", "new");
		testSetQueryStringReplaced("htTP:?new#fragment?notParam", "htTP:#fragment?notParam", "new");
	}

	private static void testAddQueryStringSame(String url, String newQuery) {
		AnyURI anyURI = new AnyURI(url);
		assertSame(anyURI, anyURI.addQueryString(newQuery));
	}

	public void testAddQueryStringSame() {
		testAddQueryStringSame("", null);
		testAddQueryStringSame("?", null);
		testAddQueryStringSame("#", null);
		testAddQueryStringSame("?#", null);

		testAddQueryStringSame("htTP:", null);
		testAddQueryStringSame("htTP:?", null);
		testAddQueryStringSame("htTP:?param?&param2=value#anc?h&or", null);
		testAddQueryStringSame("htTP:#fragment#", null);
		testAddQueryStringSame("htTP:#fragment?notParam", null);
	}

	private static void testAddQueryString(String expected, String url, String newQuery) {
		assertEquals(expected, new AnyURI(url).addQueryString(newQuery).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddQueryStringNotAllowFragment() {
		testAddQueryString("?##", "#", "#");
	}

	@Test
	public void testAddQueryString() {
		testAddQueryString("?new", "", "new");
		testAddQueryString("?&?", "?", "?");
		testAddQueryString("?&new&other?other#", "?#", "new&other?other");

		testAddQueryString("htTP:?new", "htTP:", "new");
		testAddQueryString("htTP:?&new", "htTP:?", "new");
		testAddQueryString("htTP:?param?&param2=value&new#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new");
		testAddQueryString("htTP:?new#fragment#", "htTP:#fragment#", "new");
		testAddQueryString("htTP:?new#fragment?notParam", "htTP:#fragment?notParam", "new");
	}

	private static void testAddEncodedParameterSame(String url, String encodedName, String encodedValue) {
		AnyURI anyURI = new AnyURI(url);
		assertSame(anyURI, anyURI.addEncodedParameter(encodedName, encodedValue));
	}

	public void testAddEncodedParameterSame() {
		testAddEncodedParameterSame("", null, null);
		testAddEncodedParameterSame("?", null, null);
		testAddEncodedParameterSame("#", null, null);
		testAddEncodedParameterSame("?#", null, null);

		testAddEncodedParameterSame("htTP:", null, null);
		testAddEncodedParameterSame("htTP:?", null, null);
		testAddEncodedParameterSame("htTP:?param?&param2=value#anc?h&or", null, null);
		testAddEncodedParameterSame("htTP:#fragment#", null, null);
		testAddEncodedParameterSame("htTP:#fragment?notParam", null, null);
	}

	private static void testAddEncodedParameter(String expected, String url, String encodedName, String encodedValue) {
		assertEquals(expected, new AnyURI(url).addEncodedParameter(encodedName, encodedValue).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEncodedParameterNameNotAllowFragment() {
		testAddEncodedParameter("?##", "#", "#", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEncodedParameterValueNotAllowFragment() {
		testAddEncodedParameter("?name=##", "#", "name", "#");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEncodedParameterNullNameWithNonNullValue() {
		testAddEncodedParameter("/test?=value", "/test", null, "value");
	}

	@Test
	public void testAddEncodedParameter() {
		testAddEncodedParameter("?new", "", "new", null);
		testAddEncodedParameter("?new=", "", "new", "");
		testAddEncodedParameter("?new=val", "", "new", "val");
		testAddEncodedParameter("?&?", "?", "?", null);
		testAddEncodedParameter("?&?=", "?", "?", "");
		testAddEncodedParameter("?&?=val", "?", "?", "val");
		testAddEncodedParameter("?plo&other?other&new#", "?plo&other?other#", "new", null);
		testAddEncodedParameter("?plo&other?other&new=#", "?plo&other?other#", "new", "");
		testAddEncodedParameter("?plo&other?other&new=val#", "?plo&other?other#", "new", "val");

		testAddEncodedParameter("htTP:?new", "htTP:", "new", null);
		testAddEncodedParameter("htTP:?new=", "htTP:", "new", "");
		testAddEncodedParameter("htTP:?new=val", "htTP:", "new", "val");
		testAddEncodedParameter("htTP:?&new", "htTP:?", "new", null);
		testAddEncodedParameter("htTP:?&new=", "htTP:?", "new", "");
		testAddEncodedParameter("htTP:?&new=val", "htTP:?", "new", "val");
		testAddEncodedParameter("htTP:?param?&param2=value&new#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new", null);
		testAddEncodedParameter("htTP:?param?&param2=value&new=#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new", "");
		testAddEncodedParameter("htTP:?param?&param2=value&new=val#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new", "val");
		testAddEncodedParameter("htTP:?new#fragment#", "htTP:#fragment#", "new", null);
		testAddEncodedParameter("htTP:?new=#fragment#", "htTP:#fragment#", "new", "");
		testAddEncodedParameter("htTP:?new=val#fragment#", "htTP:#fragment#", "new", "val");
		testAddEncodedParameter("htTP:?new#fragment?notParam", "htTP:#fragment?notParam", "new", null);
		testAddEncodedParameter("htTP:?new=#fragment?notParam", "htTP:#fragment?notParam", "new", "");
		testAddEncodedParameter("htTP:?new=val#fragment?notParam", "htTP:#fragment?notParam", "new", "val");
	}

	private static void testAddParameterSame(String url, String name, String value) {
		AnyURI anyURI = new AnyURI(url);
		assertSame(anyURI, anyURI.addParameter(name, value));
	}

	public void testAddParameterSame() {
		testAddParameterSame("", null, null);
		testAddParameterSame("?", null, null);
		testAddParameterSame("#", null, null);
		testAddParameterSame("?#", null, null);

		testAddParameterSame("htTP:", null, null);
		testAddParameterSame("htTP:?", null, null);
		testAddParameterSame("htTP:?param?&param2=value#anc?h&or", null, null);
		testAddParameterSame("htTP:#fragment#", null, null);
		testAddParameterSame("htTP:#fragment?notParam", null, null);
	}

	private static void testAddParameter(String expected, String url, String name, String value) {
		assertEquals(expected, new AnyURI(url).addParameter(name, value).toString());
	}

	public void testAddParameterNameEncodesFragment() {
		testAddParameter("?%23#", "#", "#", null);
	}

	public void testAddParameterValueEncodesFragment() {
		testAddParameter("?name=%23#", "#", "name", "#");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddParameterNullNameWithNonNullValue() {
		testAddParameter("/test?=value", "/test", null, "value");
	}

	@Test
	public void testAddParameter() {
		testAddParameter("?%E3%81%8B%E3%81%8A%E3%82%8A", "", "かおり", null);
		testAddParameter("?%E3%81%8B%E3%81%8A%E3%82%8A=", "", "かおり", "");
		testAddParameter("?%E3%81%8B%E3%81%8A%E3%82%8A=val", "", "かおり", "val");
		testAddParameter("?&%3F", "?", "?", null);
		testAddParameter("?&%3F=", "?", "?", "");
		testAddParameter("?&%3F=val", "?", "?", "val");
		testAddParameter("?plo&other?other&%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#", "?plo&other?other#", "かおり BBB", null);
		testAddParameter("?plo&other?other&%E3%81%8B%E3%81%8A%E3%82%8A%2BBBB=#", "?plo&other?other#", "かおり+BBB", "");
		testAddParameter("?plo&other?other&%E3%81%8B%E3%81%8A%E3%82%8A%3DBBB=%E8%8A%B1#", "?plo&other?other#", "かおり=BBB", "花");

		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A", "htTP:", "かおり", null);
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=", "htTP:", "かおり", "");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1", "htTP:", "かおり", "花");
		testAddParameter("htTP:?&%E3%81%8B%E3%81%8A%E3%82%8A", "htTP:?", "かおり", null);
		testAddParameter("htTP:?&%E3%81%8B%E3%81%8A%E3%82%8A=", "htTP:?", "かおり", "");
		testAddParameter("htTP:?&%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1", "htTP:?", "かおり", "花");
		testAddParameter("htTP:?param?&param2=value&%E3%81%8B%E3%81%8A%E3%82%8A#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "かおり", null);
		testAddParameter("htTP:?param?&param2=value&%E3%81%8B%E3%81%8A%E3%82%8A=#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "かおり", "");
		testAddParameter("htTP:?param?&param2=value&%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "かおり", "花");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A#fragment#", "htTP:#fragment#", "かおり", null);
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=#fragment#", "htTP:#fragment#", "かおり", "");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1#fragment#", "htTP:#fragment#", "かおり", "花");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A#fragment?notParam", "htTP:#fragment?notParam", "かおり", null);
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=#fragment?notParam", "htTP:#fragment?notParam", "かおり", "");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1#fragment?notParam", "htTP:#fragment?notParam", "かおり", "花");
	}
	// </editor-fold>
}
