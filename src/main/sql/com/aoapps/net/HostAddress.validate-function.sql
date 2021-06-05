/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2018, 2021  AO Industries, Inc.
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
-- Must be done as "postgres" user in PostgreSQL < 13:
CREATE EXTENSION IF NOT EXISTS citext;

-- TODO: PostgreSQL 11: Implement as domain over composite type of
-- either DomainName or InetAddress (but not both).

CREATE OR REPLACE FUNCTION "com.aoapps.net"."HostAddress.validate" (address citext)
RETURNS text AS $$
DECLARE
  "parsed_inetAddress" "com.aoapps.net"."InetAddress";
BEGIN
  IF "com.aoapps.net"."DomainName.validate"(address) IS NULL
  THEN
    -- Is a valid DomainName
    RETURN NULL;
  ELSE
    -- Must be a valid InetAddress, test by parsing
    BEGIN
      "parsed_inetAddress" = address :: "com.aoapps.net"."InetAddress";
      -- Parsed OK, but could still have a '/'
      IF address LIKE '%/%' THEN
         RETURN 'Neither a valid DomainName nor a valid InetAddress';
      END IF;
      RETURN NULL;
    EXCEPTION WHEN OTHERS THEN
      RETURN 'Neither a valid DomainName nor a valid InetAddress';
    END;
  END IF;
END;
$$ LANGUAGE plpgsql
STABLE; -- IMMUTABLE possible? since queries TopLevelDomains via DomainName?

COMMENT ON FUNCTION "com.aoapps.net"."HostAddress.validate" (citext) IS
'Matches method com.aoapps.net.HostAddress.valueOf, but with less verbose
messages and without translations.  The database type is for final enforcement,
whereas higher level code should have already validated and provided more
detailed messages.';
