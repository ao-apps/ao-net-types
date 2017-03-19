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
import java.io.ObjectInputValidation;
import java.io.Serializable;

/**
 * A port range and associated protocol.
 * The range may not be empty; it must represent at least one port.
 *
 * @author  AO Industries, Inc.
 */
final public class PortRange implements
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.PortRange>,
	IPortRange
{

	private static final long serialVersionUID = 1L;

	public static ValidationResult validate(int from, int to, Protocol protocol) {
		if(from < Port.MIN_PORT) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "PortRange.validate.from.lessThanOne", from);
		}
		if(from > Port.MAX_PORT) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "PortRange.validate.from.greaterThan64k", from);
		}
		if(to < Port.MIN_PORT) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "PortRange.validate.to.lessThanOne", to);
		}
		if(to > Port.MAX_PORT) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "PortRange.validate.to.greaterThan64k", to);
		}
		if(to < from) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "PortRange.validate.toLessThanFrom", to, to);
		}
		if(protocol != Protocol.TCP && protocol != Protocol.UDP) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "Port.validate.unsupportedProtocol", protocol);
		}
		return ValidResult.getInstance();
	}

	public static PortRange valueOf(int from, int to, Protocol protocol) throws ValidationException {
		ValidationResult result = validate(from, to, protocol);
		if(!result.isValid()) throw new ValidationException(result);
		return new PortRange(from, to, protocol);
	}

	final private int from;
	final private int to;
	final private Protocol protocol;

	private PortRange(int from, int to, Protocol protocol) throws ValidationException {
		this.from = from;
		this.to = to;
		this.protocol = protocol;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(from, to, protocol);
		if(!result.isValid()) throw new ValidationException(result);
	}

	/**
	 * Perform same validation as constructor on readObject.
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		validateObject();
	}

	@Override
	public void validateObject() throws InvalidObjectException {
		try {
			validate();
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	private Object readResolve() throws InvalidObjectException {
		try {
			return valueOf(from, to, protocol);
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	/**
	 * Unlike {@link Port Port}, port ranges are not cached and may not be safely compared by identity.
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PortRange)) return false;
		PortRange other = (PortRange)obj;
		return
			protocol == other.protocol
			&& from == other.from
			&& to == other.to
		;
	}

	@Override
	public int hashCode() {
		int hash = protocol.getDecimal();
		hash = hash * 31 + from;
		hash = hash * 31 + to;
		return hash;
	}

	/**
	 * @see  IPortRange#compareTo(com.aoindustries.net.IPortRange)
	 */
	@Override
	public int compareTo(IPortRange other) {
		return compareTo(other.getPortRange());
	}

	/**
	 * @see  IPortRange#compareTo(com.aoindustries.net.IPortRange)
	 */
	public int compareTo(PortRange other) {
		// Java 1.8: Use Integer.compare instead
		int diff = ComparatorUtils.compare(from, other.from);
		if(diff != 0) return diff;
		diff = ComparatorUtils.compare(to, other.to);
		if(diff != 0) return diff;
		return protocol.compareTo(other.protocol);
	}

	/**
	 * @return The port range and protocol, such as <samp>53/UDP</samp>
	 * or <samp>8080-8087/TCP</samp>.
	 */
	@Override
	public String toString() {
		if(from == to) {
			return Integer.toString(from) + '/' + protocol.name();
		} else {
			return Integer.toString(from) + '-' + Integer.toString(to) + '/' + protocol.name();
		}
	}

	/**
	 * Gets the first port number in the range.
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * Gets the first port number in the range as a {@link Port}.
	 */
	public Port getFromPort() {
		try {
			return Port.valueOf(from, protocol);
		} catch(ValidationException e) {
			AssertionError ae = new AssertionError("Validation rules are compatible between PortRange and Port.");
			ae.initCause(e);
			throw ae;
		}
	}

	/**
	 * Gets the last port number in the range.
	 */
	public int getTo() {
		return to;
	}

	/**
	 * Gets the last port number in the range as a {@link Port}.
	 */
	public Port getToPort() {
		try {
			return Port.valueOf(to, protocol);
		} catch(ValidationException e) {
			AssertionError ae = new AssertionError("Validation rules are compatible between PortRange and Port.");
			ae.initCause(e);
			throw ae;
		}
	}

	public Protocol getProtocol() {
		return protocol;
	}

	@Override
	public com.aoindustries.net.dto.PortRange getDto() {
		return new com.aoindustries.net.dto.PortRange(from, to, protocol.name());
	}

	/**
	 * For {@link IPortRange}.
	 *
	 * @return {@code this}
	 */
	@Override
	public PortRange getPortRange() {
		return this;
	}

	/**
	 * Checks if this port range is of the same protocol and overlaps the given port range.
	 */
	public boolean overlaps(PortRange other) {
		// See http://stackoverflow.com/questions/3269434/whats-the-most-efficient-way-to-test-two-integer-ranges-for-overlap
		return
			protocol == other.protocol
			&& from <= other.to
			&& other.from <= to
		;
	}

	/**
	 * @return  The part of this port range below, and not including, the given port or {@code null} if none.
	 */
	public PortRange splitBelow(int below) {
		int newTo = Math.min(to, below - 1);
		if(newTo >= from) {
			try {
				return PortRange.valueOf(from, newTo, protocol);
			} catch(ValidationException e) {
				AssertionError ae = new AssertionError("Previously valid PortRange should still be valid.");
				ae.initCause(e);
				throw ae;
			}
		} else {
			return null;
		}
	}

	/**
	 * @return  The part of this port range above, and not including, the given port or {@code null} if none.
	 */
	public PortRange splitAbove(int above) {
		int newFrom = Math.max(from, above + 1);
		if(newFrom <= to) {
			try {
				return PortRange.valueOf(newFrom, to, protocol);
			} catch(ValidationException e) {
				AssertionError ae = new AssertionError("Previously valid PortRange should still be valid.");
				ae.initCause(e);
				throw ae;
			}
		} else {
			return null;
		}
	}
}
