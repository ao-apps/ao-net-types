/*
 * ao-net-types - Networking-related value types for Java.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Several network resources on a <code>Server</code> require a unique
 * port.  All of the possible network ports are represented by
 * {@link Port Port}.
 *
 * @author  AO Industries, Inc.
 */
final public class Port extends IPortRange implements
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.Port>
{

	private static final long serialVersionUID = 2L;

	public static ValidationResult validate(int port, Protocol protocol) {
		if(port < MIN_PORT) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "Port.validate.lessThanOne", port);
		}
		if(port > MAX_PORT) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "Port.validate.greaterThan64k", port);
		}
		if(protocol != Protocol.TCP && protocol != Protocol.UDP) {
			return new InvalidResult(ApplicationResourcesAccessor.accessor, "Port.validate.unsupportedProtocol", protocol);
		}
		return ValidResult.getInstance();
	}

	private static final AtomicReferenceArray<Port> tcpCache = new AtomicReferenceArray<Port>(MAX_PORT - MIN_PORT + 1);
	private static final AtomicReferenceArray<Port> udpCache = new AtomicReferenceArray<Port>(MAX_PORT - MIN_PORT + 1);

	public static Port valueOf(int port, Protocol protocol) throws ValidationException {
		ValidationResult result = validate(port, protocol);
		if(!result.isValid()) throw new ValidationException(result);
		AtomicReferenceArray<Port> cache;
		switch(protocol) {
			case TCP :
				cache = tcpCache;
				break;
			case UDP :
				cache = udpCache;
				break;
			default :
				throw new AssertionError();
		}
		int cacheIndex = port - MIN_PORT;
		Port np = cache.get(cacheIndex);
		if(np == null) {
			np = new Port(port, protocol);
			if(!cache.compareAndSet(cacheIndex, null, np)) np = cache.get(cacheIndex);
		}
		return np;
	}

	final private int port;

	private Port(int port, Protocol protocol) throws ValidationException {
		super(protocol);
		this.port = port;
		validate();
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
	public boolean equals(Object obj) {
		// enum pattern, this is safe:
		return (this == obj);
		/* enum pattern, don't need this:
		return
			obj != null
			&& obj instanceof Port
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
		return Integer.toString(port) + '/' + protocol.name();
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
	 * @return {@code true} when the port is >= 1024.
	 */
	public boolean isUser() {
		return port >= 1024;
	}

	@Override
	public com.aoindustries.net.dto.Port getDto() {
		return new com.aoindustries.net.dto.Port(port, protocol.name());
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
