/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2010-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a 48-bit MAC address in HH:HH:HH:HH:HH:HH format.  Parses case insensitive, produces uppercase.
 *
 * <ul>
 *   <li><a href="https://wikipedia.org/wiki/MAC_address">https://wikipedia.org/wiki/MAC_address</a></li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
public final class MacAddress implements
	Comparable<MacAddress>,
	Serializable,
	DtoFactory<com.aoapps.net.dto.MacAddress>,
	Internable<MacAddress>
{

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, MacAddress.class);

	private static final long serialVersionUID = 893218935616001329L;

	private static ValidationResult checkHexValue(char ch) {
		if(
			(ch<'0' || ch>'9')
			&& (ch<'A' || ch>'F')
			&& (ch<'a' || ch>'f')
		) return new InvalidResult(RESOURCES, "checkHexValue.badCharacter", ch);
		return ValidResult.getInstance();
	}

	/**
	 * Checks if the address is valid.
	 */
	public static ValidationResult validate(String address) {
		// Be non-null
		if(address==null) return new InvalidResult(RESOURCES, "validate.isNull");
		// Be non-empty
		int len = address.length();
		if(len!=17) return new InvalidResult(RESOURCES, "parse.incorrectLength", len);
		ValidationResult result = checkHexValue(address.charAt(0));
		if(!result.isValid()) return result;
		result = checkHexValue(address.charAt(1));
		if(!result.isValid()) return result;
		if(address.charAt(2)!=':') return new InvalidResult(RESOURCES, "parse.notColon", 2);
		result = checkHexValue(address.charAt(3));
		if(!result.isValid()) return result;
		result = checkHexValue(address.charAt(4));
		if(!result.isValid()) return result;
		if(address.charAt(5)!=':') return new InvalidResult(RESOURCES, "parse.notColon", 5);
		result = checkHexValue(address.charAt(6));
		if(!result.isValid()) return result;
		result = checkHexValue(address.charAt(7));
		if(!result.isValid()) return result;
		if(address.charAt(8)!=':') return new InvalidResult(RESOURCES, "parse.notColon", 8);
		result = checkHexValue(address.charAt(9));
		if(!result.isValid()) return result;
		result = checkHexValue(address.charAt(10));
		if(!result.isValid()) return result;
		if(address.charAt(11)!=':') return new InvalidResult(RESOURCES, "parse.notColon", 11);
		result = checkHexValue(address.charAt(12));
		if(!result.isValid()) return result;
		result = checkHexValue(address.charAt(13));
		if(!result.isValid()) return result;
		if(address.charAt(14)!=':') return new InvalidResult(RESOURCES, "parse.notColon", 14);
		result = checkHexValue(address.charAt(15));
		if(!result.isValid()) return result;
		result = checkHexValue(address.charAt(16));
		if(!result.isValid()) return result;
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String, MacAddress> interned = new ConcurrentHashMap<>();

	/**
	 * @param address  when {@code null}, returns {@code null}
	 */
	public static MacAddress valueOf(String address) throws ValidationException {
		if(address == null) return null;
		//MacAddress existing = interned.get(address);
		//return existing!=null ? existing : new MacAddress(address);
		return new MacAddress(address, true);
	}

	private final String address;

	private MacAddress(String address, boolean validate) throws ValidationException {
		this.address = address.toUpperCase(Locale.ROOT);
		if(validate) validate();
	}

	/**
	 * @param  address  Does not validate, should only be used with a known valid value.
	 */
	private MacAddress(String address) {
		ValidationResult result;
		assert (result = validate(address)).isValid() : result.toString();
		assert address.toUpperCase(Locale.ROOT).equals(address);
		this.address = address;
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(address);
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
	public boolean equals(Object O) {
		return
			(O instanceof MacAddress)
			&& address.equals(((MacAddress)O).address)
		;
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public int compareTo(MacAddress other) {
		return this==other ? 0 : address.compareTo(other.address);
	}

	@Override
	public String toString() {
		return address;
	}

	/**
	 * Interns this IP much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public MacAddress intern() {
		MacAddress existing = interned.get(address);
		if(existing==null) {
			String internedAddress = address.intern();
			@SuppressWarnings("StringEquality")
			MacAddress addMe = (address == internedAddress) ? this : new MacAddress(internedAddress);
			existing = interned.putIfAbsent(internedAddress, addMe);
			if(existing==null) existing = addMe;
		}
		return existing;
	}

	@Override
	public com.aoapps.net.dto.MacAddress getDto() {
		return new com.aoapps.net.dto.MacAddress(address);
	}

	public boolean isBroadcast() {
		return address.equals("FF:FF:FF:FF:FF:FF");
	}
}
