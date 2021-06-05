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

CREATE OR REPLACE FUNCTION "com.aoapps.net"."DomainLabel.validate" (label citext)
RETURNS text AS $$
BEGIN
  IF label IS NULL THEN
    RETURN 'DomainLabel.validate.isNull';
  END IF;
  IF LENGTH(label) = 0 THEN
    RETURN 'DomainLabel.validate.empty';
  END IF;
  IF LENGTH(label) > 63 THEN
    RETURN 'DomainLabel.validate.tooLong';
  END IF;
  IF label LIKE '-%' THEN
    RETURN 'DomainLabel.validate.startsDash';
  END IF;
  IF label LIKE '%-' THEN
    RETURN 'DomainLabel.validate.endsDash';
  END IF;
  IF label !~ '^[a-zA-Z0-9-]+$' THEN
    RETURN 'DomainLabel.validate.invalidCharacter';
  END IF;
  -- All is OK
  RETURN null;
END;
$$ LANGUAGE plpgsql
IMMUTABLE;

COMMENT ON FUNCTION "com.aoapps.net"."DomainLabel.validate" (citext) IS
'Matches method com.aoapps.net.DomainLabel.validate, but with less verbose
messages and without translations.  The database type is for final enforcement,
whereas higher level code should have already validated and provided more
detailed messages.';
