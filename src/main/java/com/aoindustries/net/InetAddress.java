/*
 * ao-net-types - Networking-related value types for Java.
 * Copyright (C) 2010-2013, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.io.IoUtils;
import com.aoindustries.math.LongLong;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.Serializable;
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
 *   <li><a href="http://en.wikipedia.org/wiki/IPv4#Address_representations">http://en.wikipedia.org/wiki/IPv4#Address_representations</a></li>
 *   <li><a href="http://en.wikipedia.org/wiki/IPv6_Addresses#Notation">http://en.wikipedia.org/wiki/IPv6_Addresses#Notation</a></li>
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

	private static final long serialVersionUID = -5667211900395977633L;

	private static final long
		// Bits for IPv4 representations
		IPV4_HI                              = 0x0000000000000000L,
		IPV4_NET_COMPAT_LO                   = 0x0000000000000000L, // ::a.b.c.d
		IPV4_NET_MAPPED_LO                   = 0x0000ffff00000000L, // ::ffff:a.b.c.d
		// Network prefix masks
		IPV6_NET_MASK_7_HI                   = 0xfe00000000000000L, // IPv6 /7
		IPV6_NET_MASK_8_HI                   = 0xff00000000000000L, // IPv6 /8
		IPV6_NET_MASK_10_HI                  = 0xffc0000000000000L, // IPv6 /10
		IPV6_NET_MASK_16_HI                  = 0xffff000000000000L, // IPv6 /16
		IPV6_NET_MASK_28_HI                  = 0xfffffff000000000L, // IPv6 /28
		IPV6_NET_MASK_32_HI                  = 0xffffffff00000000L, // IPv6 /32
		IPV6_NET_MASK_48_HI                  = 0xffffffffffff0000L, // IPv6 /48
		IPV6_NET_MASK_96_LO                  = 0xffffffff00000000L, // IPv6 /96
		IPV4_NET_MASK_4_LO                   = 0xfffffffff0000000L, // IPv4 /4
		IPV4_NET_MASK_8_LO                   = 0xffffffffff000000L, // IPv4 /8
		IPV4_NET_MASK_10_LO                  = 0xffffffffffc00000L, // IPv4 /10
		IPV4_NET_MASK_12_LO                  = 0xfffffffffff00000L, // IPv4 /12
		IPV4_NET_MASK_15_LO                  = 0xfffffffffffe0000L, // IPv4 /15
		IPV4_NET_MASK_16_LO                  = 0xffffffffffff0000L, // IPv4 /16
		IPV4_NET_MASK_24_LO                  = 0xffffffffffffff00L, // IPv4 /24
		// Unspecified
		UNSPECIFIED_HI                       = 0x0000000000000000L,
		IPV4_MAPPED_UNSPECIFIED_LO           = 0x0000ffff00000000L, // 0.0.0.0/32
		IPV6_UNSPECIFIED_LO                  = 0x0000000000000000L, // ::
		// Bits for loopback
		LOOPBACK_HI                          = 0x0000000000000000L,
		IPV4_NET_COMPAT_LOOPBACK_LO          = 0x000000007f000000L, // 127.0.0.0/8
		IPV4_NET_MAPPED_LOOPBACK_LO          = 0x0000ffff7f000000L, // 127.0.0.0/8
		//IPV4_COMPAT_LOOPBACK_LO              = 0x000000007f000001L, // 127.0.0.1
		IPV4_MAPPED_LOOPBACK_LO              = 0x0000ffff7f000001L, // 127.0.0.1
		IPV6_LOOPBACK_LO                     = 0x0000000000000001L, // ::1
		// Link Local
		IPV4_NET_COMPAT_LINK_LOCAL_LO        = 0x00000000a9fe0000L, // 169.254.0.0/16
		IPV4_NET_MAPPED_LINK_LOCAL_LO        = 0x0000ffffa9fe0000L, // 169.254.0.0/16
		IPV6_NET_LINK_LOCAL_HI               = 0xfe80000000000000L, // fe80::/10
		// Multicast
		IPV4_NET_COMPAT_MULTICAST_LO         = 0x00000000e0000000L, // 224.0.0.0/4
		IPV4_NET_MAPPED_MULTICAST_LO         = 0x0000ffffe0000000L, // 224.0.0.0/4
		IPV6_NET_MULTICAST_HI                = 0xff00000000000000L, // ff00::/8
		// Unique Local
		IPV4_NET_COMPAT_UNIQUE_LOCAL_8_LO    = 0x000000000a000000L, // 10.0.0.0/8
		IPV4_NET_MAPPED_UNIQUE_LOCAL_8_LO    = 0x0000ffff0a000000L, // 10.0.0.0/8
		IPV4_NET_COMPAT_UNIQUE_LOCAL_12_LO   = 0x00000000ac100000L, // 172.16.0.0/12
		IPV4_NET_MAPPED_UNIQUE_LOCAL_12_LO   = 0x0000ffffac100000L, // 172.16.0.0/12
		IPV4_NET_COMPAT_UNIQUE_LOCAL_16_LO   = 0x00000000c0a80000L, // 192.168.0.0/16
		IPV4_NET_MAPPED_UNIQUE_LOCAL_16_LO   = 0x0000ffffc0a80000L, // 192.168.0.0/16
		IPV6_NET_UNIQUE_LOCAL_HI             = 0xfc00000000000000L, // fc00::/7
		// 6to4
		IPV4_NET_COMPAT_6TO4_RELAY           = 0x00000000c0586300L, // 192.88.99.0/24
		IPV4_NET_MAPPED_6TO4_RELAY           = 0x0000ffffc0586300L, // 192.88.99.0/24
		IPV6_NET_6TO4_HI                     = 0x2002000000000000L, // 2002::/16
		// Teredo
		IPV6_NET_TEREDO_HI                   = 0x2001000000000000L, // 2001::/32
		// Documentation
		IPV4_NET_COMPAT_TEST_NET_1           = 0x00000000c0000200L, // 192.0.2.0/24
		IPV4_NET_MAPPED_TEST_NET_1           = 0x0000ffffc0000200L, // 192.0.2.0/24
		IPV4_NET_COMPAT_TEST_NET_2           = 0x00000000c6336400L, // 198.51.100.0/24
		IPV4_NET_MAPPED_TEST_NET_2           = 0x0000ffffc6336400L, // 198.51.100.0/24
		IPV4_NET_COMPAT_TEST_NET_3           = 0x00000000cb007100L, // 203.0.113.0/24
		IPV4_NET_MAPPED_TEST_NET_3           = 0x0000ffffcb007100L, // 203.0.113.0/24
		IPV6_NET_DOCUMENTATION_HI            = 0x20010db800000000L, // 2001:db8::/32
		// Network Benchmark
		IPV4_NET_COMPAT_BENCHMARK            = 0x00000000c6120000L, // 198.18.0.0/15
		IPV4_NET_MAPPED_BENCHMARK            = 0x0000ffffc6120000L, // 198.18.0.0/15
		IPV6_NET_BENCHMARK                   = 0x2001000200000000L, // 2001:2::/48
		// IPv4 Broadcast
		IPV4_COMPAT_BROADCAST_LO             = 0x00000000ffffffffL, // 255.255.255.255/32
		IPV4_MAPPED_BROADCAST_LO             = 0x0000ffffffffffffL, // 255.255.255.255/32
		// Orchid
		IPV6_NET_ORCHID_HI                   = 0x2001002000000000L, // 2001:20::/28
		// Carrier-grade NAT
		IPV4_NET_COMPAT_CARRIER_GRADE_NET_LO = 0x0000000064400000L, // 100.64.0.0/10
		IPV4_NET_MAPPED_CARRIER_GRADE_NET_LO = 0x0000ffff64400000L  // 100.64.0.0/10
	;

	/**
	 * Checks if the address is valid by calling <code>parse(String)</code> and discarding the result.
	 *
	 * @see  #parse(String)
	 */
	public static ValidationResult validate(String address) {
		// Be non-null
		if(address==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.validate.isNull");
		try {
			parse(address);
			return ValidResult.getInstance();
		} catch(ValidationException exc) {
			return exc.getResult();
		}
	}

	private static final ConcurrentMap<LongLong,InetAddress> interned = new ConcurrentHashMap<LongLong,InetAddress>();
	private static final ConcurrentMap<String,InetAddress> internedByAddress = new ConcurrentHashMap<String,InetAddress>();

	/**
	 * Parses either an IPv4 or IPv6 address.
	 *
	 * @param address  when {@code null}, returns {@code null}
	 */
	public static InetAddress valueOf(String address) throws ValidationException {
		if(address == null) return null;
		// If found in interned, it is valid
		//InetAddress existing = internedByAddress.get(address);
		//return existing!=null ? existing : valueOf(parse(address));
		return valueOf(parse(address));
	}

	/**
	 * Gets an IPv6 address from its numerical representation.
	 *
	 * @param  ip  If ip is null, returns null.
	 */
	public static InetAddress valueOf(LongLong ip) {
		if(ip == null) return null;
		//InetAddress existing = interned.get(ip);
		//return existing!=null ? existing : new InetAddress(ip);
		return new InetAddress(ip);
	}

	private static int parseOctet(String address, int start, int end) throws ValidationException {
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
			if(len==0) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseOctet.empty"));
			else throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseOctet.tooLong"));
		}
		// Must each be 0-9
		if(ch1<'0' || ch1>'9') throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseOctet.nonDecimal", ch1));
		if(ch2<'0' || ch2>'9') throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseOctet.nonDecimal", ch2));
		if(ch3<'0' || ch3>'9') throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseOctet.nonDecimal", ch3));
		int octet =
			(ch1-'0')*100
			+ (ch2-'0')*10
			+ (ch3-'0')
		;
		if(octet>255) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseOctet.tooBig"));
		return octet;
	}

	private static int getHexValue(char ch) throws ValidationException {
		if(ch>='0' && ch<='9') return ch-'0';
		if(ch>='a' && ch<='f') return ch-'a'+10;
		if(ch>='A' && ch<='F') return ch-'A'+10;
		throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.getHexValue.badCharacter", ch));
	}

	private static int parseHexWord(String address, int start, int end) throws ValidationException {
		// Must each be 0-9 or a-f or A-F
		int len = end-start;
		if(len == 4) {
			return
				  (getHexValue(address.charAt(start    )) << 12)
				| (getHexValue(address.charAt(start + 1)) << 8)
				| (getHexValue(address.charAt(start + 2)) << 4)
				|  getHexValue(address.charAt(start + 3))
			;
		} else if(len == 3) {
			return
				  (getHexValue(address.charAt(start    )) << 8)
				| (getHexValue(address.charAt(start + 1)) << 4)
				|  getHexValue(address.charAt(start + 2))
			;
		} else if(len == 2) {
			return
				  (getHexValue(address.charAt(start    )) << 4)
				|  getHexValue(address.charAt(start + 1))
			;
		} else if(len == 1) {
			return getHexValue(address.charAt(start));
		} else if(len == 0) {
			throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseHexWord.empty"));
		} else {
			throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseHexWord.tooLong"));
		}
	}

	/**
	 * Supports the following formats:
	 * <ol>
	 *   <li><code>ddd.ddd.ddd.ddd</code> - IPv4</li>
	 *   <li><code>hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh</code> (with single :: shortcut) - IPv6</li>
	 *   <li><code>hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:ddd.ddd.ddd.ddd</code> (with single :: shortcut) - IPv6,
	 *     unless resolves to an IPv4-mapped address (::ffff:a.b.c.d) it will be considered IPv4.</li>
	 * </ol>
	 */
	private static LongLong parse(String address) throws ValidationException {
		// Be non-empty
		int len = address.length();
		if(len==0) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.empty"));
		final int maxLen = "hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:ddd.ddd.ddd.ddd".length();
		if(len > maxLen) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.tooLong"));
		// Look for any dot, stopping at a colon
		int dot3Pos = -1;
		for(int c=len-1; c>=0; c--) {
			char ch = address.charAt(c);
			if(ch=='.') {
				dot3Pos = c;
				break;
			}
			if(ch==':') break;
		}
		long ipLow;
		int rightColonPos;
		int rightWord;
		if(dot3Pos!=-1) {
			// May be either IPv4 or IPv6 with : and . mix
			int dot2Pos = address.lastIndexOf('.', dot3Pos-1);
			if(dot2Pos==-1) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.oneDot"));
			int dot1Pos = address.lastIndexOf('.', dot2Pos-1);
			if(dot1Pos==-1) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.twoDots"));
			rightColonPos = address.lastIndexOf(':', dot1Pos-1);
			// Must be all [0-9] between dots and beginning/colon
			ipLow =
				(long)parseOctet(address, rightColonPos+1, dot1Pos)<<24
				| (long)parseOctet(address, dot1Pos+1, dot2Pos)<<16
				| (long)parseOctet(address, dot2Pos+1, dot3Pos)<<8
				| (long)parseOctet(address, dot3Pos+1, len)
			;
			if(rightColonPos==-1) {
				// IPv4
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
			rightColonPos = len;
			rightWord = 8;
		}
		long ipHigh = 0;
		while(rightWord>0) {
			int prevColonPos = address.lastIndexOf(':', rightColonPos-1);
			if(prevColonPos==-1) {
				if(rightWord!=1) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.notEnoughColons"));
			} else {
				if(rightWord==1) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.tooManyColons"));
			}
			// This address ends with :: - don't confuse with shortcut, just leave as zero
			if(prevColonPos==(len-1)) {
				if(len>=2 && address.charAt(len-2)==':') {
					rightColonPos = len-2;
					break;
				} else {
					// Ends in : but doesn't end in ::
					throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseHexWord.empty"));
				}
			} else {
				// Check for shortcut
				if(prevColonPos==(rightColonPos-1)) {
					rightColonPos = prevColonPos;
					break;
				}
				int wordValue = parseHexWord(address, prevColonPos+1, rightColonPos);
				rightWord--;
				if(rightWord<4) {
					ipHigh |= (long)wordValue << ((3-rightWord)<<4);
				} else {
					ipLow |= (long)wordValue << ((7-rightWord)<<4);
				}
				rightColonPos = prevColonPos;
			}
		}
		int leftColonPos = -1;
		int leftWord = 0;
		while(leftColonPos<rightColonPos) {
			if(leftWord>=rightWord) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.tooManyColons"));
			int nextColonPos = address.indexOf(':', leftColonPos+1);
			if(nextColonPos==-1) {
				if(leftWord!=7) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.notEnoughColons"));
				nextColonPos = len;
			} else {
				if(leftWord==7) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.tooManyColons"));
			}
			// Handle beginning ::
			if(nextColonPos==0) {
				// should have been caught be pass from right above and should align
				if(rightColonPos==0) {
					// OK - we match the scan from right
					break;
				} else {
					throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseHexWord.empty"));
				}
			} else {
				int wordValue = parseHexWord(address, leftColonPos+1, nextColonPos);
				if(leftWord<4) {
					ipHigh |= (long)wordValue << ((3-leftWord)<<4);
				} else {
					ipLow |= (long)wordValue << ((7-leftWord)<<4);
				}
				leftWord++;
				leftColonPos = nextColonPos;
			}
		}
		return LongLong.valueOf(ipHigh, ipLow);
	}

	public static final InetAddress UNSPECIFIED_IPV4 = valueOf(
		new LongLong(UNSPECIFIED_HI, IPV4_MAPPED_UNSPECIFIED_LO)
	).intern();

	public static final InetAddress UNSPECIFIED_IPV6 = valueOf(
		new LongLong(UNSPECIFIED_HI, IPV6_UNSPECIFIED_LO)
	).intern();

	public static final InetAddress LOOPBACK_IPV4 = valueOf(
		new LongLong(LOOPBACK_HI, IPV4_MAPPED_LOOPBACK_LO)
	).intern();

	public static final InetAddress LOOPBACK_IPV6 = valueOf(
		new LongLong(LOOPBACK_HI, IPV6_LOOPBACK_LO)
	).intern();

	// TODO: Store hi, lo fields directly?
	final private LongLong ip;

	private InetAddress(LongLong ip) {
		this.ip = ip;
	}

	@Override
	public boolean equals(Object O) {
		return
			(O instanceof InetAddress)
			&& ip.equals(((InetAddress)O).ip)
		;
	}

	@Override
	public int hashCode() {
		return ip.hashCode();
	}

	/**
	 * Sorts by address family, address.
	 */
	@Override
	public int compareTo(InetAddress other) {
		int diff = getAddressFamily().compareTo(other.getAddressFamily());
		if(diff != 0) return diff;
		return ip.compareToUnsigned(other.ip);
	}

	/**
	 * Converts this IP address to its String representation.
	 */
	@Override
	public String toString() {
		long hi = ip.getHigh();
		long lo = ip.getLow();
		if(hi == UNSPECIFIED_HI && lo == IPV6_UNSPECIFIED_LO) return "::";
		if(hi == LOOPBACK_HI    && lo == IPV6_LOOPBACK_LO   ) return "::1";
		if(hi == IPV4_HI) {
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
					.toString()
				;
			}
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
					.toString()
				;
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
		StringBuilder SB = new StringBuilder(39);
		if(longestFirstZero<=0 || (longestFirstZero+longestNumZeros)>=16) {
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
		switch(getAddressFamily()) {
			case INET6 :
				return '[' + toString() + ']';
			case INET :
				return toString();
			default :
				throw new AssertionError();
		}
	}

	/**
	 * Interns this IP much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public InetAddress intern() {
		InetAddress existing = interned.get(ip);
		if(existing==null) {
			existing = interned.putIfAbsent(ip, this);
			if(existing==null) existing = this;
			InetAddress existing2 = internedByAddress.putIfAbsent(existing.toString(), existing);
			if(existing2!=null && existing2!=existing) throw new AssertionError("existing2!=null && existing2!=existing");
		}
		return existing;
	}

	public LongLong getIp() {
		return ip;
	}

	@Override
	public com.aoindustries.net.dto.InetAddress getDto() {
		return new com.aoindustries.net.dto.InetAddress(toString());
	}

	public boolean isUnspecified() {
		return
			ip.getHigh() == UNSPECIFIED_HI
			&& (
				   ip.getLow() == IPV6_UNSPECIFIED_LO
				|| ip.getLow() == IPV4_NET_MAPPED_LO
			)
		;
	}

	public boolean isLooback() {
		return
			ip.getHigh() == LOOPBACK_HI
			&& (
				ip.getLow() == IPV6_LOOPBACK_LO
				|| (ip.getLow() & IPV4_NET_MASK_8_LO) == IPV4_NET_COMPAT_LOOPBACK_LO
				|| (ip.getLow() & IPV4_NET_MASK_8_LO) == IPV4_NET_MAPPED_LOOPBACK_LO
			)
		;
	}

	public boolean isLinkLocal() {
		return
			(ip.getHigh() & IPV6_NET_MASK_10_HI) == IPV6_NET_LINK_LOCAL_HI
			|| (
				ip.getHigh() == IPV4_HI
				&& (
					   (ip.getLow() & IPV4_NET_MASK_16_LO) == IPV4_NET_COMPAT_LINK_LOCAL_LO
					|| (ip.getLow() & IPV4_NET_MASK_16_LO) == IPV4_NET_MAPPED_LINK_LOCAL_LO
				)
			)
		;
	}

	public boolean isMulticast() {
		return
			(ip.getHigh() & IPV6_NET_MASK_8_HI) == IPV6_NET_MULTICAST_HI
			|| (
				ip.getHigh() == IPV4_HI && (
					   (ip.getLow() & IPV4_NET_MASK_4_LO) == IPV4_NET_COMPAT_MULTICAST_LO
					|| (ip.getLow() & IPV4_NET_MASK_4_LO) == IPV4_NET_MAPPED_MULTICAST_LO
				)
			)
		;
	}

	public boolean isUniqueLocal() {
		return
			(ip.getHigh() & IPV6_NET_MASK_7_HI) == IPV6_NET_UNIQUE_LOCAL_HI
			|| (
				ip.getHigh() == IPV4_HI
				&& (
					   (ip.getLow() & IPV4_NET_MASK_8_LO ) == IPV4_NET_COMPAT_UNIQUE_LOCAL_8_LO
					|| (ip.getLow() & IPV4_NET_MASK_8_LO ) == IPV4_NET_MAPPED_UNIQUE_LOCAL_8_LO
					|| (ip.getLow() & IPV4_NET_MASK_12_LO) == IPV4_NET_COMPAT_UNIQUE_LOCAL_12_LO
					|| (ip.getLow() & IPV4_NET_MASK_12_LO) == IPV4_NET_MAPPED_UNIQUE_LOCAL_12_LO
					|| (ip.getLow() & IPV4_NET_MASK_16_LO) == IPV4_NET_COMPAT_UNIQUE_LOCAL_16_LO
					|| (ip.getLow() & IPV4_NET_MASK_16_LO) == IPV4_NET_MAPPED_UNIQUE_LOCAL_16_LO
				)
			)
		;
	}

	public boolean is6to4() {
		return
			(ip.getHigh() & IPV6_NET_MASK_16_HI) == IPV6_NET_6TO4_HI
			|| (
				ip.getHigh() == IPV4_HI
				&& (
					   (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_COMPAT_6TO4_RELAY
					|| (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_MAPPED_6TO4_RELAY
				)
			)
		;
	}

	/**
	 * Is Teredo tunneling (<code>2001::/32</code>)?
	 */
	public boolean isTeredo() {
		return (ip.getHigh() & IPV6_NET_MASK_32_HI) == IPV6_NET_TEREDO_HI;
	}

	/**
	 * See <a href="https://tools.ietf.org/html/rfc5737">RFC 5737</a>.
	 */
	public boolean isDocumentation() {
		return
			(ip.getHigh() & IPV6_NET_MASK_32_HI) == IPV6_NET_DOCUMENTATION_HI
			|| (
				ip.getHigh() == IPV4_HI
				&& (
					   (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_COMPAT_TEST_NET_1
					|| (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_MAPPED_TEST_NET_1
					|| (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_COMPAT_TEST_NET_2
					|| (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_MAPPED_TEST_NET_2
					|| (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_COMPAT_TEST_NET_3
					|| (ip.getLow() & IPV4_NET_MASK_24_LO) == IPV4_NET_MAPPED_TEST_NET_3
				)
			)
		;
	}

	/**
	 * See <a href="https://tools.ietf.org/html/rfc5180">RFC 5180</a>.
	 */
	public boolean isNetworkBenchmark() {
		return
			(ip.getHigh() & IPV6_NET_MASK_48_HI) == IPV6_NET_BENCHMARK
			|| (
				ip.getHigh() == IPV4_HI
				&& (
					   (ip.getLow() & IPV4_NET_MASK_15_LO) == IPV4_NET_COMPAT_BENCHMARK
					|| (ip.getLow() & IPV4_NET_MASK_15_LO) == IPV4_NET_MAPPED_BENCHMARK
				)
			)
		;
	}

	public boolean isBroadcast() {
		return
			ip.getHigh() == IPV4_HI
			&& (
				   ip.getLow() == IPV4_COMPAT_BROADCAST_LO
				|| ip.getLow() == IPV4_MAPPED_BROADCAST_LO
			)
		;
	}

	public boolean isOrchid() {
		return (ip.getHigh() & IPV6_NET_MASK_28_HI) == IPV6_NET_ORCHID_HI;
	}

	/**
	 * Is an IPv4 carrier-grade NAT (<code>100.64.0.0/10</code>)?  See <a href="https://tools.ietf.org/html/rfc6598">RFC 5698</a>.
	 */
	public boolean isCarrierGradeNat() {
		return
			ip.getHigh() == IPV4_HI
			&& (
				   (ip.getLow() & IPV4_NET_MASK_10_LO) == IPV4_NET_COMPAT_CARRIER_GRADE_NET_LO
				|| (ip.getLow() & IPV4_NET_MASK_10_LO) == IPV4_NET_MAPPED_CARRIER_GRADE_NET_LO
			)
		;
	}

	public AddressFamily getAddressFamily() {
		if(
			ip.getHigh() == IPV4_HI
			&& (ip.getLow() & IPV6_NET_MASK_96_LO) == IPV4_NET_MAPPED_LO
		) {
			return AddressFamily.INET;
		} else {
			return AddressFamily.INET6;
		}
	}

	/**
	 * @deprecated  Please use {@link #getAddressFamily()}
	 */
	@Deprecated
	public boolean isIPv4() {
		return getAddressFamily() == AddressFamily.INET;
	}

	/**
	 * @deprecated  Please use {@link #getAddressFamily()}
	 */
	@Deprecated
	public boolean isIPv6() {
		return getAddressFamily() == AddressFamily.INET6;
	}

	/*
	public static void main(String[] args) {
		System.out.println(LOOPBACK_IPV6.toString());
	}
	 */
}
