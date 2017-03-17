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
import com.aoindustries.math.LongLong;
import com.aoindustries.util.Internable;
import com.aoindustries.util.persistent.PersistentCollections;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents either an IPv4 or an IPv6 IP address.  The internal storage is always
 * that of an IPv6 address.
 *
 * {@link http://en.wikipedia.org/wiki/IPv4#Address_representations}
 * {@link http://en.wikipedia.org/wiki/IPv6_Addresses#Notation}
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
			return exc.result;
		}
	}

	private static final ConcurrentMap<LongLong,InetAddress> interned = new ConcurrentHashMap<>();
	private static final ConcurrentMap<String,InetAddress> internedByAddress = new ConcurrentHashMap<>();

	/**
	 * Parses either an IPv4 or IPv6 address.
	 *
	 * If address is null, returns null.
	 */
	public static InetAddress valueOf(String address) throws ValidationException {
		if(address==null) return null;
		// If found in interned, it is valid
		//InetAddress existing = internedByAddress.get(address);
		//return existing!=null ? existing : valueOf(parse(address));
		return valueOf(parse(address));
	}

	/**
	 * Gets an IPv6 address from its numerical representation.
	 *
	 * If ip is null, returns null.
	 */
	public static InetAddress valueOf(LongLong ip) {
		if(ip==null) return null;
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
		int len = end-start;
		char ch1, ch2, ch3, ch4;
		if(len==4) {
			ch1 = address.charAt(start);
			ch2 = address.charAt(start+1);
			ch3 = address.charAt(start+2);
			ch4 = address.charAt(start+3);
		} else if(len==3) {
			ch1 = '0';
			ch2 = address.charAt(start);
			ch3 = address.charAt(start+1);
			ch4 = address.charAt(start+2);
		} else if(len==2) {
			ch1 = '0';
			ch2 = '0';
			ch3 = address.charAt(start);
			ch4 = address.charAt(start+1);
		} else if(len==1) {
			ch1 = '0';
			ch2 = '0';
			ch3 = '0';
			ch4 = address.charAt(start);
		} else {
			if(len==0) {
				throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseHexWord.empty"));
			}
			else {
				throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parseHexWord.tooLong"));
			}
		}
		// Must each be 0-9 or a-f or A-F
		return
			(getHexValue(ch1)<<12)
			| (getHexValue(ch2)<<8)
			| (getHexValue(ch3)<<4)
			| getHexValue(ch4)
		;
	}

	/**
	 * Supports the following formats:
	 * <ol>
	 *   <li><code>ddd.ddd.ddd.ddd</code> - IPv4</li>
	 *   <li><code>hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:hhhh</code> (with single :: shortcut) - IPv6</li>
	 *   <li><code>hhhh:hhhh:hhhh:hhhh:hhhh:hhhh:ddd.ddd.ddd.ddd</code> (with single :: shortcut) - IPv6, unless all hex codes are zero it will be considered IPv4</li>
	 * </ol>
	 */
	private static LongLong parse(String address) throws ValidationException {
		// Be non-empty
		int len = address.length();
		if(len==0) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.empty"));
		if(len>45) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddress.parse.tooLong"));
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
				return LongLong.valueOf(0, ipLow);
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

	public static final InetAddress UNSPECIFIED = valueOf(
		new LongLong(0, 0)
	).intern();
	public static final InetAddress LOOPBACK_IPV4 = valueOf(
		new LongLong(0, 0x000000007f000001L)
	).intern();
	public static final InetAddress LOOPBACK_IPV6 = valueOf(
		new LongLong(0, 1)
	).intern();

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

	@Override
	public int compareTo(InetAddress other) {
		return this==other ? 0 : ip.compareToUnsigned(other.ip);
	}

	/**
	 * Converts this IP address to its String representation.
	 */
	@Override
	public String toString() {
		long hi = ip.getHigh();
		long lo = ip.getLow();
		if(hi==0) {
			if(lo==0) return "::";
			if(lo==1) return "::1";
			if((lo&0xffffffff00000000L)==0x0000000000000000L) {
				// IPv4-compatible address (used to store IPv4 addresses)
				int loInt = (int)lo;
				return
					((loInt>>>24)&255)
					+ "."
					+ ((loInt>>>16)&255)
					+ "."
					+ ((loInt>>>8)&255)
					+ "."
					+ (loInt&255)
				;
			}
			if((lo&0xffffffff00000000L)==0x0000ffff00000000L) {
				// IPv4-mapped
				int loInt = (int)lo;
				return
					"::ffff:"
					+ ((loInt>>>24)&255)
					+ "."
					+ ((loInt>>>16)&255)
					+ "."
					+ ((loInt>>>8)&255)
					+ "."
					+ (loInt&255)
				;
			}
		}
		// Find the longest string of zeros
		byte[] bytes = new byte[16];
		PersistentCollections.longToBuffer(hi, bytes);
		PersistentCollections.longToBuffer(lo, bytes, 8);
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
		if(isIPv6()) return '[' + toString() + ']';
		else return toString();
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
	public com.aoindustries.aoserv.client.dto.InetAddress getDto() {
		return new com.aoindustries.aoserv.client.dto.InetAddress(toString());
	}

	public boolean isUnspecified() {
		return
			ip.getHigh()==0
			&& (
				   ip.getLow()==0x0000000000000000L
				|| ip.getLow()==0x0000ffff00000000L // 0.0.0.0/32
			)
		;
	}

	public boolean isLooback() {
		return
			ip.getHigh()==0 && (
				ip.getLow()==1
				|| (ip.getLow()&0xffffffffff000000L)==0x000000007f000000L // 127.0.0.0/8
				|| (ip.getLow()&0xffffffffff000000L)==0x0000ffff7f000000L // 127.0.0.0/8
			)
		;
	}

	public boolean isLinkLocal() {
		return
			(ip.getHigh()&0xffc0000000000000L)==0xfe80000000000000L
			|| (
				ip.getHigh()==0 && (
					   (ip.getLow()&0xffffffffffff0000L)==0x00000000a9fe0000L // 169.254.0.0/16
					|| (ip.getLow()&0xffffffffffff0000L)==0x0000ffffa9fe0000L // 169.254.0.0/16
				)
			)
		;
	}

	public boolean isMulticast() {
		return
			(ip.getHigh()&0xf000000000000000L)==0xf000000000000000L
			|| (
				ip.getHigh()==0 && (
					   (ip.getLow()&0xffffffffe0000000L)==0x00000000e0000000L // 224.0.0.0/4
					|| (ip.getLow()&0xffffffffe0000000L)==0x0000ffffe0000000L // 224.0.0.0/4
				)
			)
		;
	}

	public boolean isUniqueLocal() {
		return
			(ip.getHigh()&0xfe00000000000000L)==0xfc00000000000000L
			|| (
				ip.getHigh()==0 && (
					   (ip.getLow()&0xffffffffff000000L)==0x000000000a000000L // 10.0.0.0/8
					|| (ip.getLow()&0xffffffffff000000L)==0x0000ffff0a000000L // 10.0.0.0/8
					|| (ip.getLow()&0xfffffffffff00000L)==0x00000000ac100000L // 172.16.0.0/12
					|| (ip.getLow()&0xfffffffffff00000L)==0x0000ffffac100000L // 172.16.0.0/12
					|| (ip.getLow()&0xffffffffffff0000L)==0x00000000c0a80000L // 192.168.0.0/16
					|| (ip.getLow()&0xffffffffffff0000L)==0x0000ffffc0a80000L // 192.168.0.0/16
				)
			)
		;
	}

	public boolean is6to4() {
		return
			(ip.getHigh()&0xffff000000000000L)==0x2002000000000000L
			|| (ip.getHigh()&0xffffffff00000000L)==0x2001000000000000L
			|| (
				ip.getHigh()==0 && (
					   (ip.getLow()&0xffffffffffffff00L)==0x00000000c0586300L // 192.88.99.0/24
					|| (ip.getLow()&0xffffffffffffff00L)==0x0000ffffc0586300L // 192.88.99.0/24
				)
			)
		;
	}

	public boolean isDocumentation() {
		return
			(ip.getHigh()&0xffffffff00000000L)==0x20010db800000000L
			|| (
				ip.getHigh()==0 && (
					   (ip.getLow()&0xffffffffffffff00L)==0x00000000c0000200L // 192.0.2.0/24
					|| (ip.getLow()&0xffffffffffffff00L)==0x0000ffffc0000200L // 192.0.2.0/24
				)
			)
		;
	}

	public boolean isNetworkBenchmark() {
		return
			(ip.getHigh()&0xffffffffffff0000L)==0x2001000200000000L
			|| (
				ip.getHigh()==0 && (
					   (ip.getLow()&0xfffffffffffe0000L)==0x00000000c6120000L // 198.18.0.0/15
					|| (ip.getLow()&0xfffffffffffe0000L)==0x0000ffffc6120000L // 198.18.0.0/15
				)
			)
		;
	}

	public boolean isBroadcast() {
		return
			ip.getHigh()==0 && (
				   ip.getLow()==0x00000000ffffffffL // 255.255.255.255/32
				|| ip.getLow()==0x0000ffffffffffffL // 255.255.255.255/32
			)
		;
	}

	public boolean isOrchid() {
		return (ip.getHigh()&0xfffffff000000000L)==0x2001001000000000L;
	}

	public boolean isIPv4() {
		if(ip.getHigh()!=0) return false;
		long lo = ip.getLow();
		return
			lo!=0
			&& lo!=1
			&& (lo&0xffffffff00000000L)==0x0000000000000000L
		;
	}

	public boolean isIPv6() {
		return !isIPv4();
	}

	/*
	public static void main(String[] args) {
		System.out.println(LOOPBACK_IPV6.toString());
	}
	 */
}
