/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019, 2021, 2022, 2024  AO Industries, Inc.
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

import java.util.BitSet;

/**
 * Java helper for <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax</a>.
 *
 * <p>TODO: Find something that does this well already.
 * <a href="https://jena.apache.org/documentation/notes/iri.html">jena-iri</a>?
 * <a href="https://github.com/xbib/net>org.xbib:net-url</a>?
 * <a href="https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/utils/URIBuilder.html">URIBuilder</a>?</p>
 *
 * <p>TODO: Base on <a href="https://url.spec.whatwg.org/">URL Standard</a>, once stabilized and ubiquitous.</p>
 *
 * @author  AO Industries, Inc.
 */
final class RFC3986 {

  /** Make no instances. */
  private RFC3986() {
    throw new AssertionError();
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a>.
   */
  static final BitSet GEN_DELIMS;

  static {
    GEN_DELIMS = new BitSet(128);
    GEN_DELIMS.set(':');
    GEN_DELIMS.set('/');
    GEN_DELIMS.set('?');
    GEN_DELIMS.set('#');
    GEN_DELIMS.set('[');
    GEN_DELIMS.set(']');
    GEN_DELIMS.set('@');
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a>.
   */
  static boolean isGenDelim(char ch) {
    return GEN_DELIMS.get(ch);
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a>.
   */
  static final BitSet SUB_DELIMS;

  static {
    SUB_DELIMS = new BitSet(128);
    SUB_DELIMS.set('!');
    SUB_DELIMS.set('$');
    SUB_DELIMS.set('&');
    SUB_DELIMS.set('\'');
    SUB_DELIMS.set('(');
    SUB_DELIMS.set(')');
    SUB_DELIMS.set('*');
    SUB_DELIMS.set('+');
    SUB_DELIMS.set(',');
    SUB_DELIMS.set(';');
    SUB_DELIMS.set('=');
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a>.
   */
  static boolean isSubDelim(char ch) {
    return SUB_DELIMS.get(ch);
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a>.
   */
  static final BitSet RESERVED;

  static {
    RESERVED = new BitSet(128);
    RESERVED.or(GEN_DELIMS);
    RESERVED.or(SUB_DELIMS);
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a>.
   */
  static boolean isReserved(char ch) {
    return RESERVED.get(ch);
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">Unreserved Characters</a>.
   */
  static final BitSet UNRESERVED;

  static {
    UNRESERVED = new BitSet(128);
    UNRESERVED.set('A', 'Z' + 1);
    UNRESERVED.set('a', 'z' + 1);
    UNRESERVED.set('0', '9' + 1);
    UNRESERVED.set('-');
    UNRESERVED.set('.');
    UNRESERVED.set('_');
    UNRESERVED.set('~');
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">Unreserved Characters</a>.
   */
  static boolean isUnreserved(char ch) {
    return UNRESERVED.get(ch);
  }

  /**
   * The set of characters in US-ASCII that are valid in either URI or IRI.  Characters outside this set should never be in a URI or IRI:
   * <ul>
   * <li>'%' (for already percent-encoded)</li>
   * <li><a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a></li>
   * <li><a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">Unreserved Characters</a></li>
   * <li>Printable characters in US-ASCII that are not allowed in URIs, namely "&lt;", "&gt;", '"', space,
   *     "{", "}", "|", "\", "^", and "`".
   * </li>
   * </ul>
   */
  static final BitSet VALID;

  static {
    VALID = new BitSet(128);
    VALID.set('%');
    VALID.or(RESERVED);
    VALID.or(UNRESERVED);
    // IRI-only US-ASCII
    VALID.set('<');
    VALID.set('>');
    VALID.set('"');
    VALID.set(' ');
    VALID.set('{');
    VALID.set('}');
    VALID.set('|');
    VALID.set('\\');
    VALID.set('^');
    VALID.set('`');
  }

  /**
   * The set of characters in US-ASCII that are valid in either URI or IRI.  Characters outside this set should never be in a URI or IRI:
   * <ul>
   * <li>'%' (for already percent-encoded)</li>
   * <li><a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">Reserved Characters</a></li>
   * <li><a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">Unreserved Characters</a></li>
   * <li>Printable characters in US-ASCII that are not allowed in URIs, namely "&lt;", "&gt;", '"', space,
   *     "{", "}", "|", "\", "^", and "`".
   * </li>
   * </ul>
   */
  static boolean isValid(char ch) {
    return VALID.get(ch);
  }

  static final BitSet RESERVED_OR_INVALID;

  static {
    RESERVED_OR_INVALID = new BitSet(128);
    RESERVED_OR_INVALID.or(RESERVED);
    for (int i = 0; i < 128; i++) {
      boolean invalid = !RFC3986.VALID.get(i);
      if (invalid) {
        RESERVED_OR_INVALID.set(i);
      }
    }
  }

  static boolean isReservedOrInvalid(char ch) {
    return RESERVED_OR_INVALID.get(ch);
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-3.1">Scheme</a>.
   * <blockquote>
   * Scheme names consist of a sequence of characters beginning with a
   * letter […]
   * </blockquote>
   */
  static boolean isSchemeBeginning(char ch) {
    return
        (ch >= 'A' && ch <= 'Z')
            || (ch >= 'a' && ch <= 'z');
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-3.1">Scheme</a>.
   * <blockquote>
   * Scheme names consist of a sequence of characters […]
   * followed by any combination of letters, digits, plus
   * ("+"), period ("."), or hyphen ("-").
   * </blockquote>
   */
  static boolean isSchemeRemaining(char ch) {
    return
        (ch >= 'A' && ch <= 'Z')
            || (ch >= 'a' && ch <= 'z')
            || (ch >= '0' && ch <= '9')
            || ch == '+'
            || ch == '-'
            || ch == '.';
  }

  /**
   * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-3.1">Scheme</a>.
   * <blockquote>
   * An implementation should accept uppercase letters as equivalent to
   * lowercase in scheme names […] but should only produce lowercase scheme
   * names for consistency.
   * </blockquote>
   */
  static char normalizeScheme(char ch) {
    if (ch >= 'A' && ch <= 'Z') {
      ch += 'a' - 'A';
    }
    return ch;
  }
}
