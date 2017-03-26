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

import com.aoindustries.util.ComparatorUtils;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.Serializable;

/**
 * Something that can give a port range.
 * <p>
 * A single port must be represented by {@link Port}
 * while a port range must use {@link PortRange}.
 * </p>
 * <p>
 * {@link Port} and {@link PortRange} are compatible with
 * {@link #equals(java.lang.Object)}, {@link #hashCode()},
 * and {@link #compareTo(com.aoindustries.net.IPortRange)}.
 * </p>
 *
 * Java 1.8: Make this an interface with default methods.
 *
 * @author  AO Industries, Inc.
 */
abstract public class IPortRange implements
	Comparable<IPortRange>,
	Serializable
{

	private static final long serialVersionUID = 1L;

	public static final int MIN_PORT = 1;
	public static final int MAX_PORT = 65535;

	public static ValidationResult validate(int from, int to, Protocol protocol) {
		if(from == to) {
			return Port.validate(from, protocol);
		} else {
			return PortRange.validate(from, to, protocol);
		}
	}

	public static IPortRange valueOf(int from, int to, Protocol protocol) throws ValidationException {
		if(from == to) {
			return Port.valueOf(from, protocol);
		} else {
			return PortRange.valueOf(from, to, protocol);
		}
	}

	final Protocol protocol;

	IPortRange(Protocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * {@link Port ports} and {@link PortRange port ranges} will never equal each other because
	 * port range is forced to have a range larger than one port.
	 */
	@Override
	abstract public boolean equals(Object obj);

	@Override
	abstract public int hashCode();

	/**
	 * Ordered by from, to, protocol.
	 * The fact that is ordered by "from" is used to break loops, this ordering
	 * must not be changed without adjusting other code.
	 */
	@Override
	final public int compareTo(IPortRange other) {
		// Java 1.8: Use Integer.compare instead
		int diff = ComparatorUtils.compare(getFrom(), other.getFrom());
		if(diff != 0) return diff;
		diff = ComparatorUtils.compare(getTo(), other.getTo());
		if(diff != 0) return diff;
		return protocol.compareTo(other.protocol);
	}

	@Override
	abstract public String toString();

	final public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Gets the first port number in the range.
	 */
	abstract public int getFrom();

	/**
	 * Gets the first port number in the range as a {@link Port}.
	 */
	abstract public Port getFromPort();

	/**
	 * Gets the last port number in the range.
	 */
	abstract public int getTo();

	/**
	 * Gets the last port number in the range as a {@link Port}.
	 */
	abstract public Port getToPort();

	/**
	 * Checks if this port range is of the same protocol and overlaps the given port range.
	 */
	final public boolean overlaps(IPortRange other) {
		// See http://stackoverflow.com/questions/3269434/whats-the-most-efficient-way-to-test-two-integer-ranges-for-overlap
		return
			protocol == other.protocol
			&& getFrom() <= other.getTo()
			&& other.getFrom() <= getTo()
		;
	}

	/**
	 * @return  The part of this port range below, and not including, the given port or {@code null} if none.
	 */
	abstract public IPortRange splitBelow(int below);

	/**
	 * @return  The part of this port range above, and not including, the given port or {@code null} if none.
	 */
	abstract public IPortRange splitAbove(int above);
}
