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

CREATE OR REPLACE FUNCTION "com.aoapps.net"."DomainName.validate" ("domain" citext)
RETURNS text AS $$
DECLARE
  is_arpa CONSTANT boolean := "domain" LIKE '%.in-addr.arpa';
  _label citext;
  i integer := 0;
  _result text;
BEGIN
  IF "domain" IS NULL THEN
    RETURN 'DomainName.validate.isNull';
  END IF;
  IF LENGTH("domain") = 0 THEN
    RETURN 'DomainName.validate.empty';
  END IF;
  IF "domain" NOT IN ('localhost', 'localhost.localdomain') THEN
    IF "domain" = 'default' THEN
      RETURN 'DomainName.validate.isDefault';
    END IF;
    IF LENGTH("domain") > 253 THEN
      RETURN 'DomainName.validate.tooLong';
    END IF;
    -- Split into labels
    FOREACH _label IN ARRAY string_to_array("domain", '.') LOOP
      -- For reverse IP address delegation, if the domain ends with ".in-addr.arpa", the first label may also be in the format "##/##".
      IF (NOT is_arpa) OR i != 0 OR _label !~ '^[0-9]+/[0-9]+$' THEN
        _result := "com.aoapps.net"."DomainLabel.validate"(_label);
        IF _result IS NOT NULL THEN
          RETURN _result;
        END IF;
      END IF;
      i := i + 1;
    END LOOP;
    -- Last domain label must be alphabetic (not be all numeric)
    IF _label ~ '^[0-9]+$' THEN
      RETURN 'DomainName.validate.lastLabelAllDigits';
    END IF;
    -- Last label must be a valid top level domain
    IF
      user != 'postgres' -- Do not check while performing database dump/restore, since other table might not be populated
      AND NOT EXISTS (SELECT * FROM "com.aoapps.tlds"."TopLevelDomain" WHERE label = _label)
    THEN
      RETURN 'DomainName.validate.notEndTopLevelDomain';
    END IF;
  END IF;
  -- All is OK
  RETURN null;
END;
$$ LANGUAGE plpgsql
STABLE; -- IMMUTABLE possible? since queries TopLevelDomains?

COMMENT ON FUNCTION "com.aoapps.net"."DomainName.validate" (citext) IS
'Matches method com.aoapps.net.DomainName.validate, but with less verbose
messages and without translations.  The database type is for final enforcement,
whereas higher level code should have already validated and provided more
detailed messages.';
