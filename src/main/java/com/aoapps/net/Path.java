/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021, 2022, 2024, 2025  AO Industries, Inc.
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

import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.ComparatorUtils;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a full path.  Paths must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Start with a <code>/</code></li>
 *   <li>Not contain any null characters</li>
 *   <li>Not contain any /../ or /./ path elements</li>
 *   <li>Not end with /.. or /.</li>
 *   <li>Not contain any // in the path</li>
 * </ul>
 *
 * <p>Note, this concept of path is minimally restrictive and only represents a well-formed path.
 * The path may not be valid for some contexts, such as the path part of a URL.
 * This does not implement <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>.</p>
 *
 * <p>TODO: Should we disallow any number path element that is all dots, such as "...." and not just "." and ".."?</p>
 *
 * <p>TODO: This matches <code>UnixPath</code> in aoserv-client with the exception of allowing trailing slash.
 *       Remove this redundancy?</p>
 *
 * @author  AO Industries, Inc.
 */
// Matches src/main/sql/com/aoapps/net/Path-type.sql
public final class Path implements
    Comparable<Path>,
    Serializable,
    DtoFactory<com.aoapps.net.dto.Path>,
    Internable<Path> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Path.class);

  private static final long serialVersionUID = 1L;

  /**
   * The path separator as a char {@code '/'}.
   */
  public static final char SEPARATOR_CHAR = '/';

  /**
   * The path separator as a single-character String {@code "/"}.
   */
  public static final String SEPARATOR_STRING = Character.toString(SEPARATOR_CHAR);

  private static final String SLASH_DOT_DOT_SLASH = SEPARATOR_STRING + ".." + SEPARATOR_STRING;
  private static final String SLASH_DOT_SLASH = SEPARATOR_STRING + "." + SEPARATOR_STRING;
  private static final String SLASH_DOT = SEPARATOR_STRING + ".";
  private static final String SLASH_DOT_DOT = SEPARATOR_STRING + "..";
  private static final String SLASH_SLASH = SEPARATOR_STRING + SEPARATOR_STRING;

  // Matches src/main/sql/com/aoapps/net/Path.validate-function.sql
  public static ValidationResult validate(String path) {
    // Be non-null
    if (path == null) {
      return new InvalidResult(RESOURCES, "validate.isNull");
    }
    // Be non-empty
    if (path.length() == 0) {
      return new InvalidResult(RESOURCES, "validate.empty");
    }
    // Start with a /
    if (path.charAt(0) != SEPARATOR_CHAR) {
      return new InvalidResult(RESOURCES, "validate.startWithNonSlash", path.charAt(0));
    }
    // Not contain any null characters
    {
      int pos = path.indexOf('\0');
      if (pos != -1) {
        return new InvalidResult(RESOURCES, "validate.containsNullCharacter", pos);
      }
    }
    // Not contain any /../ or /./ path elements
    {
      int pos = path.indexOf(SLASH_DOT_DOT_SLASH);
      if (pos != -1) {
        return new InvalidResult(RESOURCES, "validate.containsDotDot", pos);
      }
    }
    {
      int pos = path.indexOf(SLASH_DOT_SLASH);
      if (pos != -1) {
        return new InvalidResult(RESOURCES, "validate.containsDot", pos);
      }
    }
    // Not end with /.. or /.
    if (path.endsWith(SLASH_DOT)) {
      return new InvalidResult(RESOURCES, "validate.endsSlashDot");
    }
    if (path.endsWith(SLASH_DOT_DOT)) {
      return new InvalidResult(RESOURCES, "validate.endsSlashDotDot");
    }
    // Not contain any // in the path
    {
      int pos = path.indexOf(SLASH_SLASH);
      if (pos != -1) {
        return new InvalidResult(RESOURCES, "validate.containsDoubleSlash", pos);
      }
    }
    return ValidResult.getInstance();
  }

  private static final ConcurrentMap<String, Path> interned = new ConcurrentHashMap<>();

  /**
   * @param path  when {@code null}, returns {@code null}
   */
  public static Path valueOf(String path) throws ValidationException {
    if (path == null) {
      return null;
    }
    if (path.length() == 1 && path.charAt(0) == SEPARATOR_CHAR) {
      return ROOT;
    }
    //UnixPath existing = interned.get(path);
    //return existing != null ? existing : new UnixPath(path);
    return new Path(path, true);
  }

  /**
   * The root path {@code "/"}.  This is implemented as a singleton
   * as is safe for direct object equality check "{@code == }".
   */
  // Note: These constants must go below the static checks and interned due to class initialization order
  public static final Path ROOT = new Path(SEPARATOR_STRING).intern();

  private final String path;

  private Path(String path, boolean validate) throws ValidationException {
    this.path = path;
    if (validate) {
      validate();
    }
  }

  /**
   * @param  path  Does not validate, should only be used with a known valid value.
   */
  private Path(String path) {
    ValidationResult result;
    assert (result = validate(path)).isValid() : result.toString();
    this.path = path;
  }

  private void validate() throws ValidationException {
    ValidationResult result = validate(path);
    if (!result.isValid()) {
      throw new ValidationException(result);
    }
  }

  /**
   * Perform same validation as constructor on readObject.
   */
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

  private Object readResolve() {
    if (path.length() == 1 && path.charAt(0) == SEPARATOR_CHAR) {
      return ROOT;
    }
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    return
        (obj instanceof Path)
            && path.equals(((Path) obj).path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public int compareTo(Path other) {
    // TODO: Sub directories before files in directory?  /path/bravo/ before /path/alpha ?
    // TODO: This would result in a sorted traversal of paths being a depth-first traversal.
    return (this == other) ? 0 : ComparatorUtils.compareIgnoreCaseConsistentWithEquals(path, other.path);
  }

  @Override
  public String toString() {
    return path;
  }

  /**
   * Interns this path much in the same fashion as <code>String.intern()</code>.
   *
   * @see  String#intern()
   */
  @Override
  public Path intern() {
    Path existing = interned.get(path);
    if (existing == null) {
      String internedPath = path.intern();
      @SuppressWarnings("StringEquality")
      Path addMe = (path == internedPath) ? this : new Path(internedPath);
      existing = interned.putIfAbsent(internedPath, addMe);
      if (existing == null) {
        existing = addMe;
      }
    }
    return existing;
  }

  @Override
  public com.aoapps.net.dto.Path getDto() {
    return new com.aoapps.net.dto.Path(path);
  }

  /**
   * Gets a sub path of the given beginning and end.
   *
   * @param  beginIndex  Must align with a {@link #SEPARATOR_CHAR}.
   * @param  endIndex    One-past the last character to include.
   *                     Must be greater than {@code beginIndex}.
   *
   * @see  String#substring(int, int)
   */
  public Path subPath(int beginIndex, int endIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
    if (path.charAt(beginIndex) != SEPARATOR_CHAR) {
      throw new LocalizedIllegalArgumentException(
          RESOURCES,
          "subPath.beginIndexNotSlash",
          beginIndex
      );
    }
    if (beginIndex >= endIndex) {
      throw new LocalizedIllegalArgumentException(
          RESOURCES,
          "subPath.beginIndexNotBeforeEndIndex",
          beginIndex,
          endIndex
      );
    }
    if (endIndex == (beginIndex + 1)) {
      assert path.substring(beginIndex, endIndex).equals(SEPARATOR_STRING);
      return ROOT;
    }
    String subPath = path.substring(beginIndex, endIndex);
    @SuppressWarnings("StringEquality")
    Path result = (path == subPath) ? this : new Path(subPath);
    return result;
  }

  /**
   * Gets a prefix path of the given length.
   *
   * <p><b>Implementation Note:</b><br>
   * Calls {@link #subPath(int, int) subPath(0, len)}</p>
   *
   * @param  len  Must be greater than {@code 0}.
   *
   * @see  #subPath(int, int)
   */
  public Path prefix(int len) throws IllegalArgumentException, IndexOutOfBoundsException {
    return subPath(0, len);
  }

  /**
   * Gets a suffix path starting at the given index.
   *
   * <p><b>Implementation Note:</b><br>
   * Calls {@link #subPath(int, int) subPath(beginIndex, path.length())}</p>
   *
   * @param  beginIndex  Must align with a {@link #SEPARATOR_CHAR}.
   *
   * @see  #subPath(int, int)
   */
  public Path suffix(int beginIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
    return subPath(beginIndex, path.length());
  }
}
