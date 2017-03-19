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
 * The range must represent at least two ports.
 *
 * @author  AO Industries, Inc.
 */
final public class PortRange extends IPortRange implements
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.PortRange>
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
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "PortRange.validate.toLessThanFrom", to, from);
		}
		if(from == to) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "PortRange.validate.fromEqualsTo", from, to);
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

	private PortRange(int from, int to, Protocol protocol) throws ValidationException {
		super(protocol);
		this.from = from;
		this.to = to;
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
	 * @return The port range and protocol, such as <samp>53/UDP</samp>
	 * or <samp>8080-8087/TCP</samp>.
	 */
	@Override
	public String toString() {
		assert to > from;
		return Integer.toString(from) + '-' + Integer.toString(to) + '/' + protocol.name();
	}

	@Override
	public int getFrom() {
		return from;
	}

	@Override
	public Port getFromPort() {
		try {
			return Port.valueOf(from, protocol);
		} catch(ValidationException e) {
			AssertionError ae = new AssertionError("Validation rules are compatible between PortRange and Port.");
			ae.initCause(e);
			throw ae;
		}
	}

	@Override
	public int getTo() {
		return to;
	}

	@Override
	public Port getToPort() {
		try {
			return Port.valueOf(to, protocol);
		} catch(ValidationException e) {
			AssertionError ae = new AssertionError("Validation rules are compatible between PortRange and Port.");
			ae.initCause(e);
			throw ae;
		}
	}

	@Override
	public com.aoindustries.net.dto.PortRange getDto() {
		return new com.aoindustries.net.dto.PortRange(from, to, protocol.name());
	}

	@Override
	public IPortRange splitBelow(int below) {
		int newTo = Math.min(to, below - 1);
		if(newTo >= from) {
			try {
				return IPortRange.valueOf(from, newTo, protocol);
			} catch(ValidationException e) {
				AssertionError ae = new AssertionError("Previously valid PortRange should still be valid.");
				ae.initCause(e);
				throw ae;
			}
		} else {
			return null;
		}
	}

	@Override
	public IPortRange splitAbove(int above) {
		int newFrom = Math.max(from, above + 1);
		if(newFrom <= to) {
			try {
				return IPortRange.valueOf(newFrom, to, protocol);
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
