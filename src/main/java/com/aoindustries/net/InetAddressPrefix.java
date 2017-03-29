/*
 * ao-net-types - Networking-related value types for Java.
 * Copyright (C) 2017  AO Industries, Inc.
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
import com.aoindustries.util.ComparatorUtils;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Combines an {@link InetAddress} and an associated prefix.
 * <p>
 * See <a href="https://tools.ietf.org/html/rfc4291#section-2.3">RFC 4291, Section 2.3. Text Representation of Address Prefixes</a>.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
final public class InetAddressPrefix implements
	Comparable<InetAddressPrefix>,
	Serializable,
	DtoFactory<com.aoindustries.net.dto.InetAddressPrefix>
{

	/**
	 * Checks if the address and prefix are valid.
	 *
	 * @param  address  must be non-null
	 * @param  prefix   must be between zero and {@link AddressFamily#getMaxPrefix()}, inclusive
	 */
	public static ValidationResult validate(InetAddress address, int prefix) {
		// Be non-null
		if(address==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddressPrefix.validate.address.isNull");
		if(prefix < 0) return new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddressPrefix.validate.prefix.lessThanZero", prefix);
		int maxPrefix = address.getAddressFamily().getMaxPrefix();
		if(prefix > maxPrefix) return new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddressPrefix.validate.prefix.tooBig", prefix, maxPrefix);
		// TODO: Special requirements for UNSPECIFIED, such as prefix forced to be zero?
		return ValidResult.getInstance();
	}

	/**
	 * Gets an IPv6 address prefix from an address and prefix.
	 *
	 * @param  address  If address is null, returns null.
	 *
	 * @throws  ValidationException  See {@link #validate(com.aoindustries.net.InetAddress, int)}
	 */
	public static InetAddressPrefix valueOf(InetAddress address, int prefix) throws ValidationException {
		if(address == null) return null;
		return new InetAddressPrefix(address, prefix);
	}

	/**
	 * Parses an IP address with optional prefix.
	 *
	 * @param address  The address and optional prefix as <samp><i>address</i>[/<i>prefix</i>]</samp>.
	 *
	 * @see  #toString()  for the inverse function
	 */
	public static InetAddressPrefix valueOf(String address) throws ValidationException {
		int slashPos = address.indexOf('/');
		if(slashPos == -1) {
			InetAddress ia = InetAddress.valueOf(address);
			return InetAddressPrefix.valueOf(
				ia,
				ia.getAddressFamily().getMaxPrefix()
			);
		} else {
			int prefix;
			{
				String prefixStr = address.substring(slashPos + 1);
				try {
					prefix = Integer.parseInt(prefixStr);
				} catch(NumberFormatException e) {
					throw new ValidationException(
						e,
						new InvalidResult(ApplicationResourcesAccessor.accessor, "InetAddressPrefix.valueOf.prefix.parseError", prefixStr)
					);
				}
			}
			return InetAddressPrefix.valueOf(
				InetAddress.valueOf(address.substring(0, slashPos)),
				prefix
			);
		}
	}

	private static final long serialVersionUID = 1L;

	final private InetAddress address;
	final private int prefix;

	private InetAddressPrefix(InetAddress address, int prefix) throws ValidationException {
		this.address = address;
		this.prefix = prefix;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(address, prefix);
		if(!result.isValid()) throw new ValidationException(result);
	}

	/**
	 * Perform same validation as constructor on readObject.
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		try {
			validate();
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	/**
	 * Equal when has equal address and prefix.  This means two prefixes that
	 * represent the same address range, but have different addresses, are not
	 * considered equal.  To check if represent the same range, {@link #normalize() normalize}
	 * each address prefix.
	 *
	 * @see  #normalize()
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof InetAddressPrefix)) return false;
		InetAddressPrefix other = (InetAddressPrefix)obj;
		return
			address.equals(other.address)
			&& prefix == other.prefix
		;
	}

	@Override
	public int hashCode() {
		return address.hashCode() * 31 + prefix;
	}

	/**
	 * @return  The address and optional prefix as <samp><i>address</i>[/<i>prefix</i>]</samp>.
	 *
	 * @see  #valueOf(String)  for the inverse function
	 */
	@Override
	public String toString() {
		if(prefix != address.getAddressFamily().getMaxPrefix()) {
			return address.toString() + '/' + prefix;
		} else {
			return address.toString();
		}
	}

	/**
	 * Ordered by address, prefix.
	 *
	 * @see  InetAddress#compareTo(com.aoindustries.net.InetAddress)
	 */
	@Override
	public int compareTo(InetAddressPrefix other) {
		int diff = address.compareTo(other.address);
		if(diff != 0) return diff;
		return ComparatorUtils.compare(prefix, other.prefix);
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPrefix() {
		return prefix;
	}

	@Override
	public com.aoindustries.net.dto.InetAddressPrefix getDto() {
		return new com.aoindustries.net.dto.InetAddressPrefix(address.getDto(), prefix);
	}

	/**
	 * Gets the first address in the network range represented by this address and prefix.
	 *
	 * @return  {@link #getAddress()} when from == address, otherwise new address.
	 */
	public InetAddress getFrom() {
		switch(address.getAddressFamily()) {
			case INET : {
				assert address.hi == InetAddress.IPV4_HI;
				assert (address.lo & InetAddress.IPV4_NET_MAPPED_LO) == InetAddress.IPV4_NET_MAPPED_LO;
				long netmask = (0xffffffffL << (32 - prefix)) & 0xffffffffL;
				long fromLo = (address.lo & netmask) | InetAddress.IPV4_NET_MAPPED_LO;
				if(fromLo == address.lo) { // TODO: Need extra mapped bits
					return address;
				} else {
					return InetAddress.valueOf(
						InetAddress.IPV4_HI,
						fromLo
					);
				}
			}
			case INET6 : {
				// Note: Careful of Java's left shift modulo 64 behavior
				if(prefix == 128) return address;
				long fromHi, fromLo;
				if(prefix == 0) {
					fromHi = fromLo = 0;
				} else if(prefix < 64) {
					long netmask = (0xffffffffffffffffL << (64 - prefix));
					fromHi = address.hi & netmask;
					fromLo = 0x0000000000000000L;
				} else if(prefix == 64) {
					fromHi = address.hi;
					fromLo = 0;
				} else {
					assert prefix > 64 && prefix < 128;
					long netmask = (0xffffffffffffffffL << (128 - prefix));
					fromHi = address.hi;
					fromLo = address.lo & netmask;
				}
				if(
					fromHi == address.hi
					&& fromLo == address.lo
				) {
					return address;
				} else {
					return InetAddress.valueOf(
						fromHi,
						fromLo
					);
				}
			}
			default :
				throw new AssertionError();
		}
	}

	/**
	 * Gets the last address in the network range represented by this address and prefix.
	 *
	 * @return  {@link #getAddress()} when to == address, otherwise new address.
	 */
	public InetAddress getTo() {
		switch(address.getAddressFamily()) {
			case INET : {
				assert address.hi == InetAddress.IPV4_HI;
				assert (address.lo & InetAddress.IPV4_NET_MAPPED_LO) == InetAddress.IPV4_NET_MAPPED_LO;
				long netmask = (0xffffffffL << (32 - prefix)) & 0xffffffffL;
				long toLo = (address.lo & netmask) | (0xffffffffL ^ netmask) | InetAddress.IPV4_NET_MAPPED_LO;
				if(toLo == address.lo) {
					return address;
				} else {
					return InetAddress.valueOf(
						InetAddress.IPV4_HI,
						toLo
					);
				}
			}
			case INET6 : {
				// Note: Careful of Java's left shift modulo 64 behavior
				if(prefix == 128) return address;
				long toHi, toLo;
				if(prefix == 0) {
					toHi = toLo = 0xffffffffffffffffL;
				} else if(prefix < 64) {
					long netmask = (0xffffffffffffffffL << (64 - prefix));
					toHi = (address.hi & netmask) | (0xffffffffffffffffL ^ netmask);
					toLo = 0xffffffffffffffffL;
				} else if(prefix == 64) {
					toHi = address.hi;
					toLo = 0xffffffffffffffffL;
				} else {
					assert prefix > 64 && prefix < 128;
					long netmask = (0xffffffffffffffffL << (128 - prefix));
					toHi = address.hi;
					toLo = (address.lo & netmask) | (0xffffffffffffffffL ^ netmask);
				}
				if(
					toHi == address.hi
					&& toLo == address.lo
				) {
					return address;
				} else {
					return InetAddress.valueOf(
						toHi,
						toLo
					);
				}
			}
			default :
				throw new AssertionError();
		}
	}

	/**
	 * Normalizes this address prefix, where all bits not in {@code prefix} are zeroed.
	 * This means the address will be the first address in the network range.
	 *
	 * @see #getFrom()
	 */
	public InetAddressPrefix normalize() {
		InetAddress from = getFrom();
		if(from == address) return this;
		try {
			return valueOf(from, prefix);
		} catch(ValidationException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Shared static contains to avoid object allocation on {@link #coalesce(com.aoindustries.net.InetAddressPrefix)}.
	 * The addresses must have already had their {@link InetAddress#getAddressFamily() families} checked as equal.
	 */
	private static boolean containsInetAddress(
		AddressFamily addressFamily,
		long thisHi,
		long thisLo,
		int thisPrefix,
		long otherHi,
		long otherLo
	) {
		switch(addressFamily) {
			case INET : {
				assert thisHi == otherHi;
				long netmask = (0xffffffffL << (32 - thisPrefix)) & 0xffffffffL;
				return (thisLo & netmask) == (otherLo & netmask);
			}
			case INET6 : {
				// Note: Careful of Java's left shift modulo 64 behavior
				if(thisPrefix == 128) return thisHi == otherHi && thisLo == otherLo;
				if(thisPrefix == 0) {
					return true;
				} else if(thisPrefix < 64) {
					long netmask = (0xffffffffffffffffL << (64 - thisPrefix));
					return (thisHi & netmask) == (otherHi & netmask);
				} else if(thisPrefix == 64) {
					return thisHi == otherHi;
				} else {
					assert thisPrefix > 64 && thisPrefix < 128;
					if(thisHi != otherHi) return false;
					long netmask = (0xffffffffffffffffL << (128 - thisPrefix));
					return (thisLo & netmask) == (otherLo & netmask);
				}
			}
			default :
				throw new AssertionError();
		}
	}

	/**
	 * Checks if the given address is in this prefix.
	 * Must be of the same {@link AddressFamily address family}; IPv4 addresses will never match IPv6.
	 */
	public boolean contains(InetAddress other) {
		AddressFamily addressFamily = address.getAddressFamily();
		if(addressFamily != other.getAddressFamily()) return false;
		return containsInetAddress(
			addressFamily,
			address.hi,
			address.lo,
			prefix,
			other.hi,
			other.lo
		);
	}

	/**
	 * Shared static contains to avoid object allocation on {@link #coalesce(com.aoindustries.net.InetAddressPrefix)}.
	 * The addresses must have already had their {@link InetAddress#getAddressFamily() families} checked as equal.
	 */
	private static boolean containsInetAddressPrefix(
		AddressFamily addressFamily,
		long thisHi,
		long thisLo,
		int thisPrefix,
		long otherHi,
		long otherLo,
		int otherPrefix
	) {
		return
			thisPrefix <= otherPrefix
			&& containsInetAddress(
				addressFamily,
				thisHi,
				thisLo,
				thisPrefix,
				otherHi,
				otherLo
			)
		;
	}

	/**
	 * Checks if the given address prefix is in this prefix.
	 * Must be of the same {@link AddressFamily address family}; IPv4 addresses will never match IPv6.
	 */
	public boolean contains(InetAddressPrefix other) {
		AddressFamily addressFamily = address.getAddressFamily();
		if(addressFamily != other.address.getAddressFamily()) return false;
		return containsInetAddressPrefix(
			addressFamily,
			address.hi,
			address.lo,
			prefix,
			other.address.hi,
			other.address.lo,
			other.prefix
		);
	}

	/**
	 * Combines this address prefix with the given address prefix if possible.
	 * Will only combine address prefixes of the same {@link AddressFamily}.
	 * Returns a {@link #normalize() normalized} network range.
	 * <ol>
	 * <li>If different address prefixes are non-overlapping and non-adjacent along prefix boundaries, returns {@code null}.
	 * <li>If the combined address prefix equals {@code this}, returns {@code this}.</li>
	 * <li>If the combined address prefix equals {@code other}, returns {@code other}.</li>
	 * <li>Otherwise, returns a new address prefix covering the full range.</li>
	 * </ol>
	 *
	 * @return  When the address prefix hae been combined, returns an address prefix spanning both.
	 *          {@code null} when they cannot be combined.
	 */
	public InetAddressPrefix coalesce(InetAddressPrefix other) {
		AddressFamily addressFamily = this.address.getAddressFamily();
		if(addressFamily != other.address.getAddressFamily()) {
			// Different address families
			return null;
		}
		if(this.contains(other)) {
			// This contains other, return this
			return this.normalize();
		}
		if(other.contains(this)) {
			// Other contains this, return other
			return other.normalize();
		}
		if(this.prefix == other.prefix) {
			// If the network of prefix - 1 contains both, they are successfully combined
			if(
				containsInetAddressPrefix(
					addressFamily,
					address.hi,
					address.lo,
					prefix - 1,
					other.address.hi,
					other.address.lo,
					other.prefix
				)
			) {
				try {
					InetAddressPrefix biggerPrefix = valueOf(address, prefix - 1).normalize();
					assert biggerPrefix.contains(this);
					assert biggerPrefix.contains(other);
					return biggerPrefix;
				} catch(ValidationException e) {
					throw new AssertionError(e);
				}
			} else {
				// Bigger range doesn't include other
				return null;
			}
		} else {
			// Network ranges of different prefixes cannot be combined
			return null;
		}
	}
}
