/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Several network resources on a <code>Server</code> require a unique
 * port.  All of the possible network ports are represented by
 * {@link Port Port}.
 *
 * @author  AO Industries, Inc.
 */
public final class Port extends IPortRange implements
	Serializable,
	DtoFactory<com.aoapps.net.dto.Port>
{

	static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Port.class);

	private static final long serialVersionUID = 2L;

	public static ValidationResult validate(int port, Protocol protocol) {
		if(port < MIN_PORT) {
			return new InvalidResult(RESOURCES, "validate.lessThanOne", port);
		}
		if(port > MAX_PORT) {
			return new InvalidResult(RESOURCES, "validate.greaterThan64k", port);
		}
		if(protocol != Protocol.TCP && protocol != Protocol.UDP && protocol != Protocol.SCTP) {
			return new InvalidResult(RESOURCES, "validate.unsupportedProtocol", protocol);
		}
		return ValidResult.getInstance();
	}

	// TODO: Worth making this weak references?
	private static final AtomicReferenceArray<Port> tcpCache = new AtomicReferenceArray<>(MAX_PORT - MIN_PORT + 1);
	private static final AtomicReferenceArray<Port> udpCache = new AtomicReferenceArray<>(MAX_PORT - MIN_PORT + 1);
	private static final AtomicReferenceArray<Port> sctpCache = new AtomicReferenceArray<>(MAX_PORT - MIN_PORT + 1);

	public static Port valueOf(int port, Protocol protocol) throws ValidationException {
		ValidationResult result = validate(port, protocol);
		if(!result.isValid()) throw new ValidationException(result);
		return valueOfNoValidate(port, protocol);
	}

	/**
	 * @param  port  Does not validate, should only be used with a known valid value.
	 * @param  protocol  Does not validate, should only be used with a known valid value.
	 */
	static Port valueOfNoValidate(int port, Protocol protocol) {
		AtomicReferenceArray<Port> cache;
		switch(protocol) {
			case TCP :
				cache = tcpCache;
				break;
			case UDP :
				cache = udpCache;
				break;
			case SCTP :
				cache = sctpCache;
				break;
			default :
				throw new AssertionError(new ValidationException(new InvalidResult(RESOURCES, "validate.unsupportedProtocol", protocol)));
		}
		int cacheIndex = port - MIN_PORT;
		Port np = cache.get(cacheIndex);
		if(np == null) {
			np = new Port(port, protocol);
			if(!cache.compareAndSet(cacheIndex, null, np)) np = cache.get(cacheIndex);
		}
		return np;
	}

	private final int port;

	/* Unused
	private Port(int port, Protocol protocol, boolean validate) throws ValidationException {
		super(protocol);
		this.port = port;
		if(validate) validate();
	}
	 */

	/**
	 * @param  port  Does not validate, should only be used with a known valid value.
	 * @param  protocol  Does not validate, should only be used with a known valid value.
	 */
	private Port(int port, Protocol protocol) {
		super(protocol);
		ValidationResult result;
		assert (result = validate(port, protocol)).isValid() : result.toString();
		this.port = port;
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(port, protocol);
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

	private Object readResolve() throws InvalidObjectException {
		try {
			return valueOf(port, protocol);
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	/**
	 * {@link Port Port} instances are cached and may be safely compared by identity.
	 */
	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		// enum pattern, this is safe:
		return (this == obj);
		/* enum pattern, don't need this:
		return
			obj instanceof Port
			&& ((Port)obj).port == port
			&& ((Port)obj).protocol == protocol
		;
		 */
	}

	@Override
	public int hashCode() {
		return protocol.getDecimal() * 31 + port;
	}

	/**
	 * @return The port and protocol, such as 110/TCP.
	 */
	@Override
	public String toString() {
		return Integer.toString(port) + '/' + protocol;
	}

	@Override
	public int getFrom() {
		return port;
	}

	@Override
	public Port getFromPort() {
		return this;
	}

	@Override
	public int getTo() {
		return port;
	}

	@Override
	public Port getToPort() {
		return this;
	}

	public int getPort() {
		return port;
	}

	/**
	 * Determines if this is a port that may be bound by non-root processes.
	 *
	 * @return {@code true} when the port is {@code >= 1024}.
	 */
	public boolean isUser() {
		return port >= 1024;
	}

	@Override
	public com.aoapps.net.dto.Port getDto() {
		return new com.aoapps.net.dto.Port(port, protocol.name());
	}

	@Override
	public IPortRange splitBelow(int below) {
		return null;
	}

	@Override
	public IPortRange splitAbove(int above) {
		return null;
	}
}
