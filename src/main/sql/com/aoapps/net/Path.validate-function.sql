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
 * along with ao-net-types.  If not, see <https://www.gnu.org/licenses/>.
 */
CREATE OR REPLACE FUNCTION "com.aoapps.net"."Path.validate" ("path" text)
RETURNS text AS $$
BEGIN
  -- Be non-null
  IF "path" IS NULL THEN
    RETURN 'Path.validate.isNull';
  END IF;
  -- Be non-empty
  IF LENGTH("path") = 0 THEN
    RETURN 'Path.validate.empty';
  END IF;
  -- Start with a /
  IF "path" NOT LIKE '/%' THEN
    RETURN 'Path.validate.startWithNonSlash';
  END IF;
  -- Not contain any null characters
  -- Nothing to do, null characters already not allowed in text type
  -- Not contain any /../ or /./ path elements
  IF "path" LIKE '%/../%' THEN
    RETURN 'Path.validate.containsDotDot';
  END IF;
  IF "path" LIKE '%/./%' THEN
    RETURN 'Path.validate.containsDot';
  END IF;
  -- Not end with /.. or /.
  IF "path" LIKE '%/.' THEN
    RETURN 'Path.validate.endsSlashDot';
  END IF;
  IF "path" LIKE '%/..' THEN
    RETURN 'Path.validate.endsSlashDotDot';
  END IF;
  -- Not contain any // in the path
  IF "path" LIKE '%//%' THEN
    RETURN 'Path.validate.containsDoubleSlash';
  END IF;
  -- All is OK
  RETURN null;
END;
$$ LANGUAGE plpgsql
IMMUTABLE;

COMMENT ON FUNCTION "com.aoapps.net"."Path.validate" (text) IS
'Matches method com.aoapps.net.Path.validate, but with less verbose
messages and without translations.  The database type is for final enforcement,
whereas higher level code should have already validated and provided more
detailed messages.';
