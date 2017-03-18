package com.aoindustries.net;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProtocolTest {
	
	public ProtocolTest() {
	}

	@Test
	public void testValueOfConsistentWithGetDecimal() {
		for(Protocol p1 : Protocol.values()) {
			if(p1 != Protocol.UNASSIGNED) {
				Protocol p2 = Protocol.valueOf(p1.getDecimal());
				if(
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
		for(short decimal = 0; decimal <= 255; decimal++) {
			assertNotNull("decimal=" + decimal, Protocol.valueOf(decimal));
		}
	}

	@Test
	public void testToString() {
		for(Protocol p : Protocol.values()) {
			String s = p.toString();
			assertNotNull("toString must not be null", s);
			String trimmed = s.trim();
			assertEquals("toString must already be trimmed", trimmed, s);
			assertNotEquals("toString must not be empty", "", s);
		}
	}

	@Test
	public void testGetDecimal() {
		for(Protocol p : Protocol.values()) {
			short decimal = p.getDecimal();
			if(decimal == -1) {
				assertEquals("Only UNASSIGNED may be -1", Protocol.UNASSIGNED, p);
			} else {
				assertTrue(decimal >= 0 && decimal <= 255);
			}
		}
	}

	@Test
	public void testGetKeyword() {
		for(Protocol p : Protocol.values()) {
			String keyword = p.getKeyword();
			assertNotNull("keyword must not be null", keyword);
			String trimmed = keyword.trim();
			assertEquals("keyword must already be trimmed", trimmed, keyword);
		}
	}

	@Test
	public void testGetProtocol() {
		for(Protocol p : Protocol.values()) {
			String protocol = p.getProtocol();
			assertNotNull("protocol must not be null", protocol);
			String trimmed = protocol.trim();
			assertEquals("protocol must already be trimmed", trimmed, protocol);
		}
	}
}
