/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2019, 2021, 2022, 2024, 2025  AO Industries, Inc.
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

import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.Serializable;

/**
 * Something that can give a port range.
 *
 * <p>A single port must be represented by {@link Port}
 * while a port range must use {@link PortRange}.</p>
 *
 * <p>{@link Port} and {@link PortRange} are compatible with
 * {@link IPortRange#equals(java.lang.Object)}, {@link IPortRange#hashCode()},
 * and {@link IPortRange#compareTo(com.aoapps.net.IPortRange)}.</p>
 *
 * <p>TODO: Java 1.8: Make this an interface with default methods.</p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class IPortRange implements
    Comparable<IPortRange>,
    Serializable {

  private static final long serialVersionUID = 1L;

  public static final int MIN_PORT = 1;
  public static final int MAX_PORT = 65535;

  /**
   * @see  Port#validate(int, com.aoapps.net.Protocol)  Used when {@code from == to}.
   * @see  PortRange#validate(int, int, com.aoapps.net.Protocol)  Used when {@code from != to}.
   */
  public static ValidationResult validate(int from, int to, Protocol protocol) {
    if (from == to) {
      return Port.validate(from, protocol);
    } else {
      return PortRange.validate(from, to, protocol);
    }
  }

  /**
   * @see  Port#valueOf(int, com.aoapps.net.Protocol)  Used when {@code from == to}.
   * @see  PortRange#valueOf(int, int, com.aoapps.net.Protocol)  Used when {@code from != to}.
   */
  public static IPortRange valueOf(int from, int to, Protocol protocol) throws ValidationException {
    if (from == to) {
      return Port.valueOf(from, protocol);
    } else {
      return PortRange.valueOf(from, to, protocol);
    }
  }

  /**
   * @see  Port#valueOfNoValidate(int, com.aoapps.net.Protocol)  Used when {@code from == to}.
   * @see  PortRange#valueOfNoValidate(int, int, com.aoapps.net.Protocol)  Used when {@code from != to}.
   */
  static IPortRange valueOfNoValidate(int from, int to, Protocol protocol) {
    if (from == to) {
      return Port.valueOfNoValidate(from, protocol);
    } else {
      return PortRange.valueOfNoValidate(from, to, protocol);
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
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  /**
   * Ordered by from, to, protocol.
   * The fact that is ordered by "from" is used to break loops, this ordering
   * must not be changed without adjusting other code.
   */
  @Override
  public final int compareTo(IPortRange other) {
    int diff = Integer.compare(getFrom(), other.getFrom());
    if (diff != 0) {
      return diff;
    }
    diff = Integer.compare(getTo(), other.getTo());
    if (diff != 0) {
      return diff;
    }
    return protocol.compareTo(other.protocol);
  }

  /**
   * {@inheritDoc}
   *
   * @see  Port#toString()
   * @see  PortRange#toString()
   */
  @Override
  public abstract String toString();

  public final Protocol getProtocol() {
    return protocol;
  }

  /**
   * Gets the first port number in the range.
   */
  public abstract int getFrom();

  /**
   * Gets the first port number in the range as a {@link Port}.
   */
  public abstract Port getFromPort();

  /**
   * Gets the last port number in the range.
   */
  public abstract int getTo();

  /**
   * Gets the last port number in the range as a {@link Port}.
   */
  public abstract Port getToPort();

  /**
   * Checks if this port range is of the same protocol and overlaps the given port range.
   */
  public final boolean overlaps(IPortRange other) {
    // See http://stackoverflow.com/questions/3269434/whats-the-most-efficient-way-to-test-two-integer-ranges-for-overlap
    return
        protocol == other.protocol
            && getFrom() <= other.getTo()
            && other.getFrom() <= getTo();
  }

  /**
   * @return  The part of this port range below, and not including, the given port or {@code null} if none.
   */
  public abstract IPortRange splitBelow(int below);

  /**
   * @return  The part of this port range above, and not including, the given port or {@code null} if none.
   */
  public abstract IPortRange splitAbove(int above);

  /**
   * Combines this port range with the given port range if possible.
   * <ol>
   * <li>If different protocols are non-overlapping and non-adjacent, returns {@code null}.
   * <li>If the combined range equals {@code this}, returns {@code this}.</li>
   * <li>If the combined range equals {@code other}, returns {@code other}.</li>
   * <li>Otherwise, returns a new port range covering the full range.</li>
   * </ol>
   *
   * @return  When the protocols match and port ranges overlap or are adjacent,
   *          returns a port range spanning both.
   *          {@code null} when they cannot be combined.
   */
  public final IPortRange coalesce(IPortRange other) {
    if (protocol != other.protocol) {
      // Different protocols
      return null;
    }
    int from;
    int to;
    if (this.overlaps(other)) {
      // Some overlap range
      from = Math.min(this.getFrom(), other.getFrom());
      to   = Math.max(this.getTo(),   other.getTo());
    } else if ((this.getTo() + 1) == other.getFrom()) {
      // This is adjacent before other
      from =  this.getFrom();
      to   = other.getTo();
    } else if ((other.getTo() + 1) == this.getFrom()) {
      // This is adjacent after other
      from = other.getFrom();
      to   =  this.getTo();
    } else {
      // No overlap and not adjacent
      return null;
    }
    if (from ==  this.getFrom() && to ==  this.getTo()) {
      return  this;
    }
    if (from == other.getFrom() && to == other.getTo()) {
      return other;
    }
    return valueOfNoValidate(from, to, protocol);
  }
}
