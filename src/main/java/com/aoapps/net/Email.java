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
import com.aoapps.lang.io.FastExternalizable;
import com.aoapps.lang.io.FastObjectInput;
import com.aoapps.lang.io.FastObjectOutput;
import com.aoapps.lang.sql.LocalizedSQLException;
import com.aoapps.lang.util.ComparatorUtils;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents an email address.  Email addresses must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Contain a single @, but not at the beginning or end</li>
 *   <li>Local part must adhere to <a href="https://wikipedia.org/wiki/E-mail_address#RFC_specification">RFC 5322</a>.</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
// Matches src/main/sql/com/aoapps/net/Email-type.sql
public final class Email implements
    Comparable<Email>,
    FastExternalizable,
    DtoFactory<com.aoapps.net.dto.Email>,
    Internable<Email>,
    SQLData
{

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Email.class);

  public static final int MAX_LENGTH = 254;

  public static final int MAX_LOCAL_PART_LENGTH = 64;

  /**
   * Validates a complete email address.  Splits on @ and calls <code>validate</code> on local part and domain.
   *
   * @see #validate(java.lang.String, com.aoapps.net.DomainName)
   */
  // Matches src/main/sql/com/aoapps/net/Email.validate-function.sql
  public static ValidationResult validate(String email) {
    // Be non-null
    if (email == null) {
      return new InvalidResult(RESOURCES, "validate.isNull");
    }
    // Be non-empty
    if (email.length() == 0) {
      return new InvalidResult(RESOURCES, "validate.empty");
    }
    int atPos = email.indexOf('@');
    if (atPos == -1) {
      return new InvalidResult(RESOURCES, "validate.noAt");
    }
    return validate(email.substring(0, atPos), email.substring(atPos + 1));
  }

  /**
   * Validates the local part of the email address (before the @ symbol), as well as additional domain rules.
   */
  // Matches src/main/sql/com/aoapps/net/Email.validate-function.sql
  public static ValidationResult validate(String localPart, String domain) {
    if (domain != null) {
      ValidationResult result = DomainName.validate(domain);
      if (!result.isValid()) {
        return result;
      }
    }
    return validateImpl(localPart, domain);
  }

  /**
   * Validates the local part of the email address (before the @ symbol), as well as additional domain rules.
   */
  // Matches src/main/sql/com/aoapps/net/Email.validate-function.sql
  public static ValidationResult validate(String localPart, DomainName domain) {
    return validateImpl(localPart, Objects.toString(domain, null));
  }

  private static final boolean[] validChars = new boolean[128];

  static {
    for (int ch = 0; ch < 128; ch++) {
      validChars[ch] =
          (ch >= 'A' && ch <= 'Z')
              || (ch >= 'a' && ch <= 'z')
              || (ch >= '0' && ch <= '9')
              || ch == '!'
              || ch == '#'
              || ch == '$'
              || ch == '%'
              || ch == '&'
              || ch == '\''
              || ch == '*'
              || ch == '+'
              || ch == '-'
              || ch == '/'
              || ch == '='
              || ch == '?'
              || ch == '^'
              || ch == '_'
              || ch == '`'
              || ch == '{'
              || ch == '|'
              || ch == '}'
              || ch == '~'
              || ch == '.' // Dot here for completeness, but algorithm below will not use it
      ;
    }
  }

  /**
   * Validates the local part of the email address (before the @ symbol), as well as additional domain rules.
   */
  // Matches src/main/sql/com/aoapps/net/Email.validate-function.sql
  private static ValidationResult validateImpl(String localPart, String domain) {
    if (localPart == null) {
      return new InvalidResult(RESOURCES, "validate.localePart.isNull");
    }
    if (domain == null) {
      return new InvalidResult(RESOURCES, "validate.domain.isNull");
    }
    if (domain.lastIndexOf('.') == -1) {
      return new InvalidResult(RESOURCES, "validate.domain.noDot");
    }
    if (DomainName.isArpa(domain)) {
      return new InvalidResult(RESOURCES, "validate.domain.isArpa");
    }
    int len = localPart.length();
    int totalLen = len + 1 + domain.length();
    if (totalLen > MAX_LENGTH) {
      return new InvalidResult(RESOURCES, "validate.tooLong", MAX_LENGTH, totalLen);
    }

    if (len == 0) {
      return new InvalidResult(RESOURCES, "validate.localePart.empty");
    }
    if (len > MAX_LOCAL_PART_LENGTH) {
      return new InvalidResult(RESOURCES, "validate.localePart.tooLong", MAX_LOCAL_PART_LENGTH, len);
    }
    for (int pos = 0; pos < len; pos++) {
      char ch = localPart.charAt(pos);
      if (ch == '.') {
        if (pos == 0) {
          return new InvalidResult(RESOURCES, "validate.localePart.startsDot");
        }
        if (pos == (len - 1)) {
          return new InvalidResult(RESOURCES, "validate.localePart.endsDot");
        }
        if (localPart.charAt(pos - 1) == '.') {
          return new InvalidResult(RESOURCES, "validate.localePart.doubleDot", pos - 1);
        }
      } else if (ch >= 128 || !validChars[ch]) {
        return new InvalidResult(RESOURCES, "validate.localePart.invalidCharacter", ch, pos);
      }
    }
    return ValidResult.getInstance();
  }

  private static final ConcurrentMap<DomainName, ConcurrentMap<String, Email>> interned = new ConcurrentHashMap<>();

  /**
   * @param email  when {@code null}, returns {@code null}
   *
   * @see #valueOf(java.lang.String, com.aoapps.net.DomainName)
   */
  public static Email valueOf(String email) throws ValidationException {
    if (email == null) {
      return null;
    }
    // Be non-empty
    if (email.length() == 0) {
      throw new ValidationException(new InvalidResult(RESOURCES, "validate.empty"));
    }
    int atPos = email.indexOf('@');
    if (atPos == -1) {
      throw new ValidationException(new InvalidResult(RESOURCES, "validate.noAt"));
    }
    return valueOf(email.substring(0, atPos), DomainName.valueOf(email.substring(atPos + 1)));
  }

  public static Email valueOf(String localPart, DomainName domain) throws ValidationException {
    //ConcurrentMap<String, Email> domainMap = interned.get(domain);
    //if (domainMap != null) {
    //  Email existing = domainMap.get(localPart);
    //  if (existing != null) {
    //    return existing;
    //  }
    //}
    return new Email(localPart, domain, true);
  }

  private String localPart;
  private DomainName domain;

  private Email(String localPart, DomainName domain, boolean validate) throws ValidationException {
    this.localPart = localPart;
    this.domain = domain;
    if (validate) {
      validate();
    }
  }

  /**
   * @param  localPart  Does not validate, should only be used with a known valid value.
   * @param  domain  Does not validate, should only be used with a known valid value.
   */
  private Email(String localPart, DomainName domain) {
    ValidationResult result;
    assert (result = validate(localPart, domain)).isValid() : result.toString();
    this.localPart = localPart;
    this.domain = domain;
  }

  private void validate() throws ValidationException {
    ValidationResult result = validateImpl(localPart, domain.toString());
    if (!result.isValid()) {
      throw new ValidationException(result);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Email)) {
      return false;
    }
    Email other = (Email) obj;
    return
        localPart.equals(other.localPart)
            && domain.equals(other.domain)
    ;
  }

  @Override
  public int hashCode() {
    return localPart.hashCode() * 31 + domain.hashCode();
  }

  /**
   * Sorts by domain and then by local part.
   */
  @Override
  public int compareTo(Email other) {
    if (this == other) {
      return 0;
    }
    int diff = domain.compareTo(other.domain);
    if (diff != 0) {
      return diff;
    }
    return ComparatorUtils.compareIgnoreCaseConsistentWithEquals(localPart, other.localPart);
  }

  @Override
  public String toString() {
    return localPart + '@' + domain;
  }

  /**
   * Interns this email much in the same fashion as <code>String.intern()</code>.
   *
   * @see  String#intern()
   */
  @Override
  public Email intern() {
    // Intern the domain
    DomainName internedDomain = domain.intern();

    // Atomically get/create the per-domain map
    ConcurrentMap<String, Email> domainMap = interned.get(internedDomain);
    if (domainMap == null) {
      ConcurrentMap<String, Email> newDomainInterned = new ConcurrentHashMap<>();
      domainMap = interned.putIfAbsent(internedDomain, newDomainInterned);
      if (domainMap == null) {
        domainMap = newDomainInterned;
      }
    }

    // Atomically get/create the Email object within the domainMap
    Email existing = domainMap.get(localPart);
    if (existing == null) {
      String internedLocalPart = localPart.intern();
      @SuppressWarnings("StringEquality")
      Email addMe = (localPart == internedLocalPart) && (domain == internedDomain) ? this : new Email(internedLocalPart, internedDomain);
      existing = domainMap.putIfAbsent(internedLocalPart, addMe);
      if (existing == null) {
        existing = addMe;
      }
    }
    return existing;
  }

  public String getLocalPart() {
    return localPart;
  }

  public DomainName getDomain() {
    return domain;
  }

  @Override
  public com.aoapps.net.dto.Email getDto() {
    return new com.aoapps.net.dto.Email(localPart, domain.getDto());
  }

  // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
  private static final long serialVersionUID = 1812494521843295031L;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  FastExternalizable
   */
  @Deprecated // Java 9: (forRemoval = false)
  public Email() {
    // Do nothing
  }

  @Override
  public long getSerialVersionUID() {
    return serialVersionUID;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    FastObjectOutput fastOut = FastObjectOutput.wrap(out);
    try {
      fastOut.writeFastUTF(localPart);
      fastOut.writeObject(domain);
    } finally {
      fastOut.unwrap();
    }
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    if (localPart != null) {
      throw new IllegalStateException();
    }
    FastObjectInput fastIn = FastObjectInput.wrap(in);
    try {
      localPart = fastIn.readFastUTF();
      domain = (DomainName) fastIn.readObject();
    } finally {
      fastIn.unwrap();
    }
    try {
      validate();
    } catch (ValidationException err) {
      InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
      newErr.initCause(err);
      throw newErr;
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="SQLData">
  public static final String SQL_TYPE = "\"com.aoapps.net\".\"Email\"";

  @Override
  public String getSQLTypeName() {
    return SQL_TYPE;
  }

  @Override
  public void writeSQL(SQLOutput stream) throws SQLException {
    stream.writeString(localPart + '@' + domain.toString());
  }

  // TODO: Change SQL type "Email" to be a compound type (localPart, domain)?
  @Override
  public void readSQL(SQLInput stream, String typeName) throws SQLException {
    if (localPart != null) {
      throw new IllegalStateException();
    }
    //System.err.println("DEBUG: typeName = " + typeName);
    try {
      String email = stream.readString();
      int atPos = email.indexOf('@');
      if (atPos == -1) {
        throw new LocalizedSQLException("23000", RESOURCES, "validate.noAt");
      }
      localPart = email.substring(0, atPos);
      domain = DomainName.valueOf(email.substring(atPos + 1));
      validate();
    } catch (ValidationException err) {
      throw new SQLException(err.getMessage(), err);
    }
  }
  // </editor-fold>
}
