/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2010-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.dto.DtoFactory;
import com.aoindustries.i18n.Resources;
import com.aoindustries.io.IoUtils;
import com.aoindustries.math.LongLong;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.Serializable;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents either an IPv4 or an IPv6 IP address.
 * <p>
 * The internal storage is always that of an IPv6 address.
 * IPv4 addresses are stored as IPv4-mapped addresses (<code>::ffff:a.b.c.d</code>),
 * but their external representation is always dotted-decimal IPv4 notation (<code>a.b.c.d</code>).
 * </p>
 * <ul>
 *   <li><a href="https://wikipedia.org/wiki/IPv4#Address_representations">https://wikipedia.org/wiki/IPv4#Address_representations</a></li>
 *   <li><a href="https://wikipedia.org/wiki/IPv6_Addresses#Notation">https://wikipedia.org/wiki/IPv6_Addresses#Notation</a></li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class InetAddress implements
	Comparable<InetAddress>,
	Serializable,
	DtoFactory<com.aoindustries.net.dto.InetAddress>,
	Internable<InetAddress>
{

	private static final Resources RESOURCES = Resources.getResources(InetAddress.class);

	static final long
		// Bits for IPv4 representations
		IPV4_HI                       = 0x0000000000000000L,
		IPV4_NET_MAPPED_LO            = 0x0000ffff00000000L;  // ::ffff:a.b.c.d
	private static final long
		IPV4_NET_COMPAT_LO            = 0x0000000000000000L, // ::a.b.c.d
		// Network prefix masks
		IPV6_NET_MASK_96_LO           = 0xffffffff00000000L, // IPv6 /96
		// Unspecified
		UNSPECIFIED_HI                = 0x0000000000000000L,
		IPV4_UNSPECIFIED_LO           = 0x0000ffff00000000L, // 0.0.0.0/32
		IPV6_UNSPECIFIED_LO           = 0x0000000000000000L, // ::
		// Bits for loopback
		LOOPBACK_HI                   = 0x0000000000000000L,
		IPV4_LOOPBACK_LO              = 0x0000ffff7f000001L, // 127.0.0.1
		IPV6_LOOPBACK_LO              = 0x0000000000000001L, // ::1
		// IPv4 Broadcast
		IPV4_BROADCAST_LO             = 0x0000ffffffffffffL  // 255.255.255.255/32
	;

	/**
	 * Checks if the address is valid by calling <code>parse(String)</code> and discarding the result.
	 * <p>
	 * When enclosed in brackets <code>"[...]"</code>, will be parsed as an IPv6 {@link InetAddress}
	 * (see {@link #toBracketedString()}).
	 * </p>
	 *
	 * @see  #parse(String)
	 */
	public static ValidationResult validate(String address) {
		// Be non-null
		if(address==null) return new InvalidResult(RESOURCES, "validate.isNull");
		Object result = parse(address);
		if(result instanceof InvalidResult) return (InvalidResult)result;
		assert result instanceof LongLong;
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<LongLong,InetAddress> interned = new ConcurrentHashMap<>();
	private static final ConcurrentMap<String,InetAddress> internedByAddress = new ConcurrentHashMap<>();

	/**
	 * Parses either an IPv4 or IPv6 address.
	 * <p>
	 * When enclosed in brackets <code>"[...]"</code>, will be parsed as an IPv6 {@link InetAddress}
	 * (see {@link #toBracketedString()}).
	 * </p>
	 *
	 * @param address  when {@code null}, returns {@code null}
	 */
	public static InetAddress valueOf(String address) throws ValidationException {
		if(address == null) return null;
		// If found in interned, it is valid
		//InetAddress existing = internedByAddress.get(address);
		//return existing!=null ? existing : valueOf(parse(address));
		Object result = parse(address);
		if(result instanceof LongLong) return valueOf((LongLong)result);
		throw new ValidationException((InvalidResult)result);
	}

	/**
	 * Gets an IPv6 address from its numerical representation.
	 *
	 * @param  ip  If ip is null, returns null.
	 *
	 * @see  #valueOf(long, long)
	 */
	public static InetAddress valueOf(LongLong ip) {
		if(ip == null) return null;
		//InetAddress existing = interned.get(ip);
		//return existing!=null ? existing : new InetAddress(ip);
		return valueOf(ip.getHigh(), ip.getLow());
	}

	/**
	 * Gets an IPv6 address from its numerical representation.
	 *
	 * @see  #valueOf(com.aoindustries.math.LongLong)
	 */
	public static InetAddress valueOf(long hi, long lo) {
		//InetAddress existing = interned.get(ip);
		//return existing!=null ? existing : new InetAddress(ip);
		return new InetAddress(hi, lo);
	}

	/**
	 * @param octet The result is added at index zero (an "out" parameter)
	 *
	 * @return  A message why invalid or {@code null} when valid and octet placed in {@code octet} parameter
	 */
	private static InvalidResult parseOctet(String address, int start, int end, int[] octet) {
		int len = end-start;
		char ch1, ch2, ch3;
		if(len==3) {
			ch1 = address.charAt(start);
			ch2 = address.charAt(start+1);
			ch3 = address.charAt(start+2);
		} else if(len==2) {
			ch1 = '0';
			ch2 = address.charAt(start);
			ch3 = address.charAt(start+1);
		} else if(len==1) {
			ch1 = '0';
			ch2 = '0';
			ch3 = address.charAt(start);
		} else {
			if(len==0) return new InvalidResult(RESOURCES, "parseOctet.empty");
			else return new InvalidResult(RESOURCES, "parseOctet.tooLong");
		}
		// Must each be 0-9
		if(ch1<'0' || ch1>'9') return new InvalidResult(RESOURCES, "parseOctet.nonDecimal", ch1);
		if(ch2<'0' || ch2>'9') return new InvalidResult(RESOURCES, "parseOctet.nonDecimal", ch2);
		if(ch3<'0' || ch3>'9') return new InvalidResult(RESOURCES, "parseOctet.nonDecimal", ch3);
		int o =
			(ch1-'0')*100
			+ (ch2-'0')*10
			+ (ch3-'0');
		if(o > 255) return new InvalidResult(RESOURCES, "parseOctet.tooBig");
		octet[0] = o;
		return null;
	}

	/**
	 * @param value The result is added at index zero (an "out" parameter)
	 *
	 * @return  A message why invalid or {@code null} when valid and result placed in {@code value} parameter
	 */
	private static InvalidResult getHexValue(char ch, int[] value) {
		if(ch>='0' && ch<='9') {
			value[0] = ch-'0';
			return null;
		}
		if(ch>='a' && ch<='f') {
			value[0] = ch-'a'+10;
			return null;
		}
		if(ch>='A' && ch<='F') {
			value[0] = ch-'A'+10;
			return null;
		}
		return new InvalidResult(RESOURCES, "getHexValue.badCharacter", ch);
	}

	/**
	 * @param value The result is added at index zero (an "out" parameter)
	 *
	 * @return  A message why invalid or {@code null} when valid and result placed in {@code value} parameter
	 */
	private static InvalidResult parseHexWord(String address, int start, int end, int[] value) {
		// Must each be 0-9 or a-f or A-F
		int len = end-start;
		if(len == 4) {
			InvalidResult result = getHexValue(address.charAt(start), value);
			if(result != null) return result;
			int h1 = value[0];
			result = getHexValue(address.charAt(start + 1), value);
			if(result != null) return result;
			int h2 = value[0];
			result = getHexValue(address.charAt(start + 2), value);
			if(result != null) return result;
			int h3 = value[0];
			result = getHexValue(address.charAt(start + 3), value);
			if(result != null) return result;
			int h4 = value[0];
			value[0] =
				  (h1 << 12)
				| (h2 << 8)
				| (h3 << 4)
				|  h4;
			return null;
		} else if(len == 3) {
			InvalidResult result = getHexValue(address.charAt(start), value);
			if(result != null) return result;
			int h1 = value[0];
			result = getHexValue(address.charAt(start + 1), value);
			if(result != null) return result;
			int h2 = value[0];
			result = getHexValue(address.charAt(start + 2), value);
			if(result != null) return result;
			int h3 = value[0];
			value[0] =
				  (h1 << 8)
				| (h2 << 4)
				|  h3;
			return null;
		} else if(len == 2) {
			InvalidResult result = getHexValue(address.charAt(start), value);
			if(result != null) return result;
			int h1 = value[0];
			result = getHexValue(address.charAt(start + 1), value);
			if(result != null) return result;
			int h2 = value[0];
			value[0] =
				  (h1 << 4)
				|  h2;
			return null;
		} else if(len == 1) {
			return getHexValue(address.charAt(start), value);
		} else if(len == 0) {
			return new InvalidResult(RESOURCES, "parseHexWord.empty");
		} else {
			return new InvalidResult(RESOURCES, "parseHexWord.tooLong");
		}
	}

	/**
	 * Supports the following formats:
	 * <ol>
	 *   <li><code>ddd.ddd.ddd.ddd</code> - IPv4</li>
	 *   <li><code>hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh</code> (with single :: shortcut) - IPv6</li>
	 *   <li><code>[hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh]</code> (with single :: shortcut) - IPv6</li>
	 *   <li><code>hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:ddd.ddd.ddd.ddd</code> (with single :: shortcut) - IPv6,
	 *     unless resolves to an IPv4-mapped address (::ffff:a.b.c.d) it will be considered IPv4.</li>
	 *   <li><code>[hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:ddd.ddd.ddd.ddd]</code> (with single :: shortcut) - IPv6,
	 *     unless resolves to an IPv4-mapped address (::ffff:a.b.c.d) it will be considered IPv4.</li>
	 * </ol>
	 *
	 * @return  Either the resulting {@link LongLong} or an {@link InvalidResult} explaining why can't parse.
	 */
	private static Object parse(String address) {
		int[] outValue = new int[1];
		boolean requireIPv6;
		int start;
		int end;
		{
			// Be non-empty
			int len = address.length();
			if(len == 0) return new InvalidResult(RESOURCES, "parse.empty");
			{
				final int maxLen = "[hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:ddd.ddd.ddd.ddd]".length();
				if(len > maxLen) return new InvalidResult(RESOURCES, "parse.tooLong");
			}
			if(
				len >= 2
				&& address.charAt(0) == '['
				&& address.charAt(len - 1) == ']'
			) {
				requireIPv6 = true;
				start = 1;
				end = len - 1;
				if(start == end) return new InvalidResult(RESOURCES, "parse.bracketsEmptyIPv6");
				final int maxLen = "hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:ddd.ddd.ddd.ddd".length();
				assert (end - start) == (len - 2);
				if((end - start) > maxLen) return new InvalidResult(RESOURCES, "parse.tooLong");
			} else {
				requireIPv6 = false;
				start = 0;
				end = len;
			}
		}
		// Look for any dot, stopping at a colon
		int dot3Pos = -1;
		for(int c = end - 1; c >= start; c--) {
			char ch = address.charAt(c);
			if(ch == '.') {
				dot3Pos = c;
				break;
			}
			if(ch == ':') break;
		}
		long ipLow;
		int rightColonPos;
		int rightWord;
		if(dot3Pos != -1) {
			// May be either IPv4 or IPv6 with : and . mix
			int dot2Pos = address.lastIndexOf('.', dot3Pos - 1);
			if(dot2Pos == -1) return new InvalidResult(RESOURCES, "parse.oneDot");
			int dot1Pos = address.lastIndexOf('.', dot2Pos - 1);
			if(dot1Pos == -1) return new InvalidResult(RESOURCES, "parse.twoDots");
			rightColonPos = address.lastIndexOf(':', dot1Pos - 1);
			// Must be all [0-9] between dots and beginning/colon
			InvalidResult result = parseOctet(address, (rightColonPos == -1) ? start : (rightColonPos + 1), dot1Pos, outValue);
			if(result != null) return result;
			int o1 = outValue[0];
			result = parseOctet(address, dot1Pos + 1, dot2Pos, outValue);
			if(result != null) return result;
			int o2 = outValue[0];
			result = parseOctet(address, dot2Pos + 1, dot3Pos, outValue);
			if(result != null) return result;
			int o3 = outValue[0];
			result = parseOctet(address, dot3Pos + 1, end, outValue);
			if(result != null) return result;
			int o4 = outValue[0];
			ipLow =
				(long)o1 << 24
				| (long)o2 << 16
				| (long)o3 << 8
				| (long)o4;
			if(rightColonPos == -1) {
				// IPv4
				if(requireIPv6) return new InvalidResult(RESOURCES, "parse.bracketsNotIPv6");
				return LongLong.valueOf(
					IPV4_HI,
					IPV4_NET_MAPPED_LO | ipLow
				);
			} else {
				// IPv6 with : and . mix
				rightWord = 6;
			}
		} else {
			// Must be IPv6 with : only
			ipLow = 0;
			rightColonPos = end;
			rightWord = 8;
		}
		long ipHigh = 0;
		while(rightWord > 0) {
			int prevColonPos = address.lastIndexOf(':', rightColonPos - 1);
			if(prevColonPos == -1) {
				if(rightWord != 1) return new InvalidResult(RESOURCES, "parse.notEnoughColons");
			} else {
				if(rightWord == 1) return new InvalidResult(RESOURCES, "parse.tooManyColons");
			}
			// This address ends with :: - don't confuse with shortcut, just leave as zero
			if(prevColonPos == (end - 1)) {
				if(end >= (start + 2) && address.charAt(end - 2) == ':') {
					rightColonPos = end - 2;
					break;
				} else {
					// Ends in : but doesn't end in ::
					return new InvalidResult(RESOURCES, "parseHexWord.empty");
				}
			} else {
				// Check for shortcut
				if(prevColonPos == (rightColonPos - 1)) {
					rightColonPos = prevColonPos;
					break;
				}
				InvalidResult result = parseHexWord(address, (prevColonPos == -1) ? start : (prevColonPos + 1), rightColonPos, outValue);
				if(result != null) return result;
				int wordValue = outValue[0];
				rightWord--;
				if(rightWord < 4) {
					ipHigh |= (long)wordValue << ((3 - rightWord) << 4);
				} else {
					ipLow |= (long)wordValue << ((7 - rightWord) << 4);
				}
				rightColonPos = prevColonPos;
			}
		}
		int leftColonPos = start - 1;
		int leftWord = 0;
		while(leftColonPos < rightColonPos) {
			if(leftWord >= rightWord) return new InvalidResult(RESOURCES, "parse.tooManyColons");
			int nextColonPos = address.indexOf(':', leftColonPos + 1);
			if(nextColonPos == -1) {
				if(leftWord != 7) return new InvalidResult(RESOURCES, "parse.notEnoughColons");
				nextColonPos = end;
			} else {
				if(leftWord == 7) return new InvalidResult(RESOURCES, "parse.tooManyColons");
			}
			// Handle beginning ::
			if(nextColonPos == start) {
				// should have been caught be pass from right above and should align
				if(rightColonPos == start) {
					// OK - we match the scan from right
					break;
				} else {
					return new InvalidResult(RESOURCES, "parseHexWord.empty");
				}
			} else {
				InvalidResult result = parseHexWord(address, leftColonPos + 1, nextColonPos, outValue);
				if(result != null) return result;
				int wordValue = outValue[0];
				if(leftWord < 4) {
					ipHigh |= (long)wordValue << ((3 - leftWord) << 4);
				} else {
					ipLow |= (long)wordValue << ((7 - leftWord) << 4);
				}
				leftWord++;
				leftColonPos = nextColonPos;
			}
		}
		return LongLong.valueOf(ipHigh, ipLow);
	}

	public static final InetAddress UNSPECIFIED_IPV4 = valueOf(
		UNSPECIFIED_HI, IPV4_UNSPECIFIED_LO
	).intern();

	public static final InetAddress UNSPECIFIED_IPV6 = valueOf(
		UNSPECIFIED_HI, IPV6_UNSPECIFIED_LO
	).intern();

	public static final InetAddress LOOPBACK_IPV4 = valueOf(
		LOOPBACK_HI, IPV4_LOOPBACK_LO
	).intern();

	public static final InetAddress LOOPBACK_IPV6 = valueOf(
		LOOPBACK_HI, IPV6_LOOPBACK_LO
	).intern();

	private static final long serialVersionUID = 2L;

	final long hi, lo;

	private InetAddress(long hi, long lo) {
		this.hi = hi;
		this.lo = lo;
	}

	@Override
	public boolean equals(Object O) {
		if(!(O instanceof InetAddress)) return false;
		InetAddress other = (InetAddress)O;
		return
			hi == other.hi
			&& lo == other.lo
		;
	}

	@Override
	public int hashCode() {
		return LongLong.hashCode(hi, lo);
	}

	/**
	 * Sorts by address family then numeric address.
	 */
	@Override
	public int compareTo(InetAddress other) {
		int diff = getAddressFamily().compareTo(other.getAddressFamily());
		if(diff != 0) return diff;
		diff = Long.compareUnsigned(hi, other.hi);
		if(diff != 0) return diff;
		return Long.compareUnsigned(lo, other.lo);
	}

	/**
	 * Converts this IP address to its String representation.
	 */
	@Override
	public String toString() {
		if(hi == UNSPECIFIED_HI && lo == IPV6_UNSPECIFIED_LO) return "::";
		if(hi == LOOPBACK_HI    && lo == IPV6_LOOPBACK_LO   ) return "::1";
		if(hi == IPV4_HI) {
			if((lo & IPV6_NET_MASK_96_LO) == IPV4_NET_MAPPED_LO) {
				// IPv4-mapped (used to store IPv4 addresses)
				int loInt = (int)lo;
				return new StringBuilder("aaa.bbb.ccc.ddd".length())
					.append((loInt >>> 24) & 255)
					.append('.')
					.append((loInt >>> 16) & 255)
					.append('.')
					.append((loInt >>> 8) & 255)
					.append('.')
					.append(loInt & 255)
					.toString();
			}
			if((lo & IPV6_NET_MASK_96_LO) == IPV4_NET_COMPAT_LO) {
				// IPv4-compatible address
				int loInt = (int)lo;
				return new StringBuilder("::aaa.bbb.ccc.ddd".length())
					.append("::")
					.append((loInt >>> 24) & 255)
					.append('.')
					.append((loInt >>> 16) & 255)
					.append('.')
					.append((loInt >>> 8) & 255)
					.append('.')
					.append(loInt & 255)
					.toString();
			}
		}
		// Find the longest string of zeros
		byte[] bytes = new byte[16];
		IoUtils.longToBuffer(hi, bytes);
		IoUtils.longToBuffer(lo, bytes, 8);
		int longestFirstZero = -1;
		int longestNumZeros = 0;
		int currentFirstZero = -1;
		int currentNumZeros = 0;
		for(int c=0; c<16; c+=2) {
			if(bytes[c]==0 && bytes[c+1]==0) {
				if(currentFirstZero==-1) {
					currentFirstZero = c;
					currentNumZeros = 2;
				} else {
					currentNumZeros += 2;
				}
			} else {
				if(currentNumZeros>longestNumZeros) {
					longestFirstZero = currentFirstZero;
					longestNumZeros = currentNumZeros;
				}
				currentFirstZero = -1;
				currentNumZeros = 0;
			}
		}
		if(currentNumZeros>longestNumZeros) {
			longestFirstZero = currentFirstZero;
			longestNumZeros = currentNumZeros;
		}
		StringBuilder SB = new StringBuilder("aaaa:bbbb:cccc:dddd:eeee:ffff:gggg:hhhh".length());
		if(longestFirstZero == -1) {
			for(int c=0; c<16; c+=2) {
				if(c>0) SB.append(':');
				SB.append(
					Integer.toHexString(
						((bytes[c]&255)<<8)
						| (bytes[c+1]&255)
					)
				);
			}
		} else {
			for(int c=0; c<longestFirstZero; c+=2) {
				if(c>0) SB.append(':');
				SB.append(
					Integer.toHexString(
						((bytes[c]&255)<<8)
						| (bytes[c+1]&255)
					)
				);
			}
			SB.append("::");
			for(int c=longestFirstZero+longestNumZeros; c<16; c+=2) {
				if(c>(longestFirstZero+longestNumZeros)) SB.append(':');
				SB.append(
					Integer.toHexString(
						((bytes[c]&255)<<8)
						| (bytes[c+1]&255)
					)
				);
			}
		}
		return SB.toString();
	}

	/**
	 * Gets an optionally-bracketed String representation of this IP address.
	 * If IPv6, the address is surrounded by [...]
	 */
	public String toBracketedString() {
		@SuppressWarnings("deprecation")
		AddressFamily family = getAddressFamily();
		switch(family) {
			case INET6 :
				return '[' + toString() + ']';
			case INET :
				return toString();
			default :
				throw new AssertionError("Unexpected address family: " + family);
		}
	}

	/**
	 * Interns this IP much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public InetAddress intern() {
		LongLong key = getIp();
		InetAddress existing = interned.get(key);
		if(existing==null) {
			existing = interned.putIfAbsent(key, this);
			if(existing==null) existing = this;
			InetAddress existing2 = internedByAddress.putIfAbsent(existing.toString(), existing);
			if(existing2!=null && existing2!=existing) throw new AssertionError("existing2!=null && existing2!=existing");
		}
		return existing;
	}

	/**
	 * Gets the high-order 64 bits of the numeric address.
	 *
	 * @see #getLow()
	 * @see #getIp()
	 */
	public long getHigh() {
		return hi;
	}

	/**
	 * Gets the low-order 64 bits of the numeric address.
	 *
	 * @see #getHigh()
	 * @see #getIp()
	 */
	public long getLow() {
		return lo;
	}

	/**
	 * Gets the numeric address as a 128-bit integer.
	 *
	 * @see #getHigh()
	 * @see #getLow()
	 */
	public LongLong getIp() {
		return LongLong.valueOf(hi, lo);
	}

	@Override
	public com.aoindustries.net.dto.InetAddress getDto() {
		return new com.aoindustries.net.dto.InetAddress(toString());
	}

	public boolean isUnspecified() {
		return
			hi == UNSPECIFIED_HI
			&& (
				   lo == IPV6_UNSPECIFIED_LO
				|| lo == IPV4_UNSPECIFIED_LO
			)
		;
	}

	/**
	 * @see  #LOOPBACK_IPV6
	 * @see  InetAddressPrefixes#LOOPBACK_IPV4
	 */
	public boolean isLoopback() {
		return
			   (hi == LOOPBACK_HI && lo == IPV6_LOOPBACK_LO)
			|| InetAddressPrefixes.LOOPBACK_IPV4.contains(this)
		;
	}

	/**
	 * The IPv4 Broadcast (<code>255.255.255.255/32</code>).
	 * <p>
	 * See <a href="https://tools.ietf.org/html/rfc922#section-7">RFC 922, Section 7</a>.
	 * </p>
	 *
	 * @see  #IPV4_BROADCAST_LO
	 */
	public boolean isBroadcast() {
		return hi == IPV4_HI && lo == IPV4_BROADCAST_LO;
	}

	/**
	 * @see  InetAddressPrefixes#LINK_LOCAL_IPV4
	 * @see  InetAddressPrefixes#LINK_LOCAL_IPV6
	 */
	public boolean isLinkLocal() {
		return
			   InetAddressPrefixes.LINK_LOCAL_IPV4.contains(this)
			|| InetAddressPrefixes.LINK_LOCAL_IPV6.contains(this)
		;
	}

	/**
	 * @see  InetAddressPrefixes#MULTICAST_IPV4
	 * @see  InetAddressPrefixes#MULTICAST_IPV6
	 */
	public boolean isMulticast() {
		return
			   InetAddressPrefixes.MULTICAST_IPV4.contains(this)
			|| InetAddressPrefixes.MULTICAST_IPV6.contains(this)
		;
	}

	/**
	 * @see  InetAddressPrefixes#UNIQUE_LOCAL_IPV4_8
	 * @see  InetAddressPrefixes#UNIQUE_LOCAL_IPV4_12
	 * @see  InetAddressPrefixes#UNIQUE_LOCAL_IPV4_16
	 * @see  InetAddressPrefixes#UNIQUE_LOCAL_IPV6
	 */
	public boolean isUniqueLocal() {
		return
			   InetAddressPrefixes.UNIQUE_LOCAL_IPV4_8.contains(this)
			|| InetAddressPrefixes.UNIQUE_LOCAL_IPV4_12.contains(this)
			|| InetAddressPrefixes.UNIQUE_LOCAL_IPV4_16.contains(this)
			|| InetAddressPrefixes.UNIQUE_LOCAL_IPV6.contains(this)
		;
	}

	/**
	 * @see  InetAddressPrefixes#_6TO4_IPV4
	 * @see  InetAddressPrefixes#_6TO4_IPV6
	 */
	public boolean is6to4() {
		return
			   InetAddressPrefixes._6TO4_IPV4.contains(this)
			|| InetAddressPrefixes._6TO4_IPV6.contains(this)
		;
	}

	/**
	 * @see  InetAddressPrefixes#TEREDO_IPV6
	 */
	public boolean isTeredo() {
		return InetAddressPrefixes.TEREDO_IPV6.contains(this);
	}

	/**
	 * @see  InetAddressPrefixes#DOCUMENTATION_IPV4_1
	 * @see  InetAddressPrefixes#DOCUMENTATION_IPV4_2
	 * @see  InetAddressPrefixes#DOCUMENTATION_IPV4_3
	 * @see  InetAddressPrefixes#DOCUMENTATION_IPV6
	 */
	public boolean isDocumentation() {
		return
			   InetAddressPrefixes.DOCUMENTATION_IPV4_1.contains(this)
			|| InetAddressPrefixes.DOCUMENTATION_IPV4_2.contains(this)
			|| InetAddressPrefixes.DOCUMENTATION_IPV4_3.contains(this)
			|| InetAddressPrefixes.DOCUMENTATION_IPV6.contains(this)
		;
	}

	/**
	 * @see  InetAddressPrefixes#BENCHMARK_IPV4
	 * @see  InetAddressPrefixes#BENCHMARK_IPV6
	 */
	public boolean isNetworkBenchmark() {
		return
			   InetAddressPrefixes.BENCHMARK_IPV4.contains(this)
			|| InetAddressPrefixes.BENCHMARK_IPV6.contains(this)
		;
	}

	/**
	 * @see  InetAddressPrefixes#ORCHID_IPV6
	 */
	public boolean isOrchid() {
		return InetAddressPrefixes.ORCHID_IPV6.contains(this);
	}

	/**
	 * @see  InetAddressPrefixes#CARRIER_GRADE_NAT_IPV4
	 */
	public boolean isCarrierGradeNat() {
		return InetAddressPrefixes.CARRIER_GRADE_NAT_IPV4.contains(this);
	}

	/**
	 * @deprecated  Please use {@link #getProtocolFamily()} as of Java 1.7.
	 */
	@Deprecated
	public AddressFamily getAddressFamily() {
		if(
			hi == IPV4_HI
			&& (lo & IPV6_NET_MASK_96_LO) == IPV4_NET_MAPPED_LO
		) {
			return AddressFamily.INET;
		} else {
			return AddressFamily.INET6;
		}
	}

	public ProtocolFamily getProtocolFamily() {
		if(
			hi == IPV4_HI
			&& (lo & IPV6_NET_MASK_96_LO) == IPV4_NET_MAPPED_LO
		) {
			return StandardProtocolFamily.INET;
		} else {
			return StandardProtocolFamily.INET6;
		}
	}

	/**
	 * Gets the type for this address.
	 */
	public AddressType getAddressType() {
		if(isUnspecified()) return AddressType.UNSPECIFIED;
		if(isLoopback())    return AddressType.LOOPBACK;
		if(isMulticast())   return AddressType.MULTICAST;
		if(isLinkLocal())   return AddressType.LINK_LOCAL_UNICAST;
		// (everything else)
		return AddressType.GLOBAL_UNICAST;
	}

	/**
	 * @deprecated  Please use {@link #getProtocolFamily()}
	 */
	@Deprecated
	public boolean isIPv4() {
		return getAddressFamily() == AddressFamily.INET;
	}

	/**
	 * @deprecated  Please use {@link #getProtocolFamily()}
	 */
	@Deprecated
	public boolean isIPv6() {
		return getAddressFamily() == AddressFamily.INET6;
	}
}
