/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2010-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a host's address as either a <code>DomainName</code> or an <code>InetAddress</code>.
 * To not allow the IP address representation, use <code>DomainName</code> instead.
 * No DNS lookups are performed during validation.
 *
 * @author  AO Industries, Inc.
 */
public final class HostAddress implements
    Comparable<HostAddress>,
    Serializable,
    DtoFactory<com.aoapps.net.dto.HostAddress>,
    Internable<HostAddress> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, HostAddress.class);

  private static final long serialVersionUID = -6323326583709666966L;

  private static boolean isIp(String address) {
    if (address == null) {
      return false;
    }
    int len = address.length();
    if (len == 0) {
      return false;
    }
    if (
        len >= 2
            && address.charAt(0) == '['
            && address.charAt(len - 1) == ']'
    ) {
      return true;
    }
    // If contains all digits and periods, or contains any colon, then is an IP
    boolean allDigitsAndPeriods = true;
    for (int c = 0; c < len; c++) {
      char ch = address.charAt(c);
      if (ch == ':') {
        return true;
      }
      if (
          (ch < '0' || ch > '9')
              && ch != '.'
      ) {
        allDigitsAndPeriods = false;
        // Still need to look for any colons
      }
    }
    return allDigitsAndPeriods;
  }

  /**
   * Validates a host address, must be either a valid domain name or a valid IP address.
   * <p>
   * When enclosed in brackets <code>"[...]"</code>, will be validated as an IPv6 {@link InetAddress}
   * (see {@link #toBracketedString()}).
   * </p>
   * <p>
   * TODO: Must be non-arpa
   * </p>
   */
  public static ValidationResult validate(String address) {
    if (isIp(address)) {
      return InetAddress.validate(address);
    } else {
      return DomainName.validate(address);
    }
  }

  private static final ConcurrentMap<DomainName, HostAddress> internedByDomainName = new ConcurrentHashMap<>();

  private static final ConcurrentMap<InetAddress, HostAddress> internedByInetAddress = new ConcurrentHashMap<>();

  /**
   * When enclosed in brackets <code>"[...]"</code>, will be parsed as an IPv6 {@link InetAddress}
   * (see {@link #toBracketedString()}).
   *
   * @param address  when {@code null}, returns {@code null}
   */
  public static HostAddress valueOf(String address) throws ValidationException {
    if (address == null) {
      return null;
    }
    return
        isIp(address)
            ? valueOf(InetAddress.valueOf(address))
            : valueOf(DomainName.valueOf(address));
  }

  /**
   * If domainName is null, returns null.
   */
  public static HostAddress valueOf(DomainName domainName) {
    if (domainName == null) {
      return null;
    }
    //HostAddress existing = internedByDomainName.get(domainName);
    //return existing != null ? existing : new HostAddress(domainName);
    return new HostAddress(domainName);
  }

  /**
   * If ip is null, returns null.
   */
  public static HostAddress valueOf(InetAddress ip) {
    if (ip == null) {
      return null;
    }
    //HostAddress existing = internedByInetAddress.get(ip);
    //return existing != null ? existing : new HostAddress(ip);
    return new HostAddress(ip);
  }

  private final DomainName domainName;
  private final InetAddress inetAddress;

  private HostAddress(DomainName domainName) {
    this.domainName = domainName;
    this.inetAddress = null;
  }

  private HostAddress(InetAddress inetAddress) {
    this.domainName = null;
    this.inetAddress = inetAddress;
  }

  private void validate() throws ValidationException {
    if (domainName == null && inetAddress == null) {
      throw new ValidationException(new InvalidResult(RESOURCES, "validate.bothNull"));
    }
    if (domainName != null && inetAddress != null) {
      throw new ValidationException(new InvalidResult(RESOURCES, "validate.bothNonNull"));
    }
  }

  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
    try {
      validate();
    } catch (ValidationException err) {
      InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
      newErr.initCause(err);
      throw newErr;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof HostAddress)) {
      return false;
    }
    HostAddress other = (HostAddress) obj;
    return
        Objects.equals(domainName, other.domainName)
            && Objects.equals(inetAddress, other.inetAddress);
  }

  @Override
  public int hashCode() {
    return domainName != null ? domainName.hashCode() : inetAddress.hashCode();
  }

  /**
   * Sorts IP addresses before domain names.
   */
  @Override
  public int compareTo(HostAddress other) {
    if (this == other) {
      return 0;
    }
    if (domainName != null) {
      if (other.domainName != null) {
        return domainName.compareTo(other.domainName);
      } else {
        return 1;
      }
    } else {
      if (other.domainName != null) {
        return -1;
      } else {
        return inetAddress.compareTo(other.inetAddress);
      }
    }
  }

  @Override
  public String toString() {
    return domainName != null ? domainName.toString() : inetAddress.toString();
  }

  public String toBracketedString() {
    return domainName != null ? domainName.toString() : inetAddress.toBracketedString();
  }

  /**
   * Interns this host address much in the same fashion as <code>String.intern()</code>.
   *
   * @see  String#intern()
   */
  @Override
  public HostAddress intern() {
    if (domainName != null) {
      HostAddress existing = internedByDomainName.get(domainName);
      if (existing == null) {
        DomainName internedDomainName = domainName.intern();
        HostAddress addMe = (domainName == internedDomainName) ? this : new HostAddress(internedDomainName);
        existing = internedByDomainName.putIfAbsent(internedDomainName, addMe);
        if (existing == null) {
          existing = addMe;
        }
      }
      return existing;
    } else {
      HostAddress existing = internedByInetAddress.get(inetAddress);
      if (existing == null) {
        InetAddress internedInetAddress = inetAddress.intern();
        HostAddress addMe = (inetAddress == internedInetAddress) ? this : new HostAddress(internedInetAddress);
        existing = internedByInetAddress.putIfAbsent(internedInetAddress, addMe);
        if (existing == null) {
          existing = addMe;
        }
      }
      return existing;
    }
  }

  public DomainName getDomainName() {
    return domainName;
  }

  public InetAddress getInetAddress() {
    return inetAddress;
  }

  @Override
  public com.aoapps.net.dto.HostAddress getDto() {
    return new com.aoapps.net.dto.HostAddress(toString());
  }
}
