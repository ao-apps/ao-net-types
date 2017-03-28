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
		// TODO: Special requirements for UNSPECIFIED?
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
	 * Checks if the given address is in this prefix.
	 * Must be of the same {@link AddressFamily address family}; IPv4 addresses will never match IPv6.
	 */
	public boolean contains(InetAddress other) {
		AddressFamily addressFamily = address.getAddressFamily();
		if(addressFamily != other.getAddressFamily()) return false;
		switch(addressFamily) {
			case INET :
				assert address.hi == other.hi;
				return (address.lo >>> (32 - prefix)) == (other.lo >>> (32 - prefix));
			case INET6 :
				if(prefix <= 64) {
					return (address.hi >>> (64 - prefix)) == (other.hi >>> (64 - prefix));
				} else {
					return
						address.hi == other.hi
						&& (address.lo >>> (128 - prefix)) == (other.lo >>> (128 - prefix))
					;
				}
			default :
				throw new AssertionError();
		}
	}
}
