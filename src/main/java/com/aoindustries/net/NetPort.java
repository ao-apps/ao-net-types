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
import com.aoindustries.util.ComparatorUtils;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Several network resources on a <code>Server</code> require a unique
 * port.  All of the possible network ports are represented by
 * <code>NetPort</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class NetPort implements
	Comparable<NetPort>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.NetPort>
{

	private static final long serialVersionUID = -29372775620060200L;

	public static ValidationResult validate(int port) {
		if(port<1) return new InvalidResult(ApplicationResources.accessor, "NetPort.validate.lessThanOne", port);
		if(port>65535) return new InvalidResult(ApplicationResources.accessor, "NetPort.validate.greaterThan64k", port);
		return ValidResult.getInstance();
	}

	private static final AtomicReferenceArray<NetPort> cache = new AtomicReferenceArray<>(65536);

	public static NetPort valueOf(int port) throws ValidationException {
		ValidationResult result = validate(port);
		if(!result.isValid()) throw new ValidationException(result);
		NetPort np = cache.get(port);
		if(np==null) {
			np = new NetPort(port);
			if(!cache.compareAndSet(port, null, np)) np = cache.get(port);
		}
		return np;
	}

	final private int port;

	private NetPort(int port) throws ValidationException {
		this.port=port;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(port);
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
			return valueOf(port);
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	@Override
	public boolean equals(Object O) {
		return
			O!=null
			&& O instanceof NetPort
			&& ((NetPort)O).port==port
		;
	}

	@Override
	public int hashCode() {
		return port;
	}

	@Override
	public int compareTo(NetPort other) {
		return this==other ? 0 : ComparatorUtils.compare(port, other.port);
	}

	@Override
	public String toString() {
		return Integer.toString(port);
	}

	public int getPort() {
		return port;
	}

	public boolean isUser() {
		return port>=1024;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.NetPort getDto() {
		return new com.aoindustries.aoserv.client.dto.NetPort(port);
	}
}
