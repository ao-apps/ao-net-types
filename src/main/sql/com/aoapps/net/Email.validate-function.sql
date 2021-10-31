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
-- Must be done as "postgres" user in PostgreSQL < 13:
CREATE EXTENSION IF NOT EXISTS citext;

CREATE OR REPLACE FUNCTION "com.aoapps.net"."Email.validate" (
  "localPart" text,
  "domain" "com.aoapps.net"."DomainName"
)
RETURNS text AS $$
DECLARE
  "totalLen" integer;
BEGIN
  IF "localPart" IS NULL THEN
    RETURN 'Email.validate.localePart.isNull';
  END IF;
  IF "domain" IS NULL THEN
    RETURN 'Email.validate.domain.isNull';
  END IF;
  IF "domain" NOT LIKE '%.%' THEN
    RETURN 'Email.validate.domain.noDot';
  END IF;
  IF "domain" LIKE '%.in-addr.arpa' THEN
    RETURN 'Email.validate.domain.isArpa';
  END IF;
  "totalLen" := length("localPart") + 1 + length("domain");
  IF "totalLen" > 254 THEN
    RETURN 'Email.validate.tooLong';
  END IF;
  IF length("localPart") = 0 THEN
    RETURN 'Email.validate.localePart.empty';
  END IF;
  IF length("localPart") > 64 THEN
    RETURN 'Email.validate.localePart.tooLong';
  END IF;
  IF "localPart" LIKE '.%' THEN
    RETURN 'Email.validate.localePart.startsDot';
  END IF;
  IF "localPart" LIKE '%.' THEN
    RETURN 'Email.validate.localePart.endsDot';
  END IF;
  IF "localPart" LIKE '%..%' THEN
    RETURN 'Email.validate.localePart.doubleDot';
  END IF;
  -- https://www.regular-expressions.info/charclass.html
  IF "localPart" !~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+$' THEN
    RETURN 'Email.validate.localePart.invalidCharacter';
  END IF;
  -- All is OK
  RETURN null;
END;
$$ LANGUAGE plpgsql
STABLE; -- IMMUTABLE possible? since queries TopLevelDomains via DomainName?

COMMENT ON FUNCTION "com.aoapps.net"."Email.validate" (text, "com.aoapps.net"."DomainName") IS
'Matches method com.aoapps.net.Email.validate, but with less verbose
messages and without translations.  The database type is for final enforcement,
whereas higher level code should have already validated and provided more
detailed messages.';

CREATE OR REPLACE FUNCTION "com.aoapps.net"."Email.validate" (
  "localPart" text,
  "domain" citext
)
RETURNS text AS $$
DECLARE
  "result" text;
BEGIN
  IF "domain" IS NOT NULL THEN
    "result" := "com.aoapps.net"."DomainName.validate"("domain");
    IF "result" IS NOT NULL THEN
      RETURN "result";
    END IF;
  END IF;
  RETURN "com.aoapps.net"."Email.validate"(
    "localPart",
    "domain" :: "com.aoapps.net"."DomainName"
  );
END;
$$ LANGUAGE plpgsql
STABLE; -- IMMUTABLE possible? since queries TopLevelDomains via DomainName?

COMMENT ON FUNCTION "com.aoapps.net"."Email.validate" (text, citext) IS
'Matches method com.aoapps.net.Email.validate, but with less verbose
messages and without translations.  The database type is for final enforcement,
whereas higher level code should have already validated and provided more
detailed messages.';

CREATE OR REPLACE FUNCTION "com.aoapps.net"."Email.validate" (address text)
RETURNS text AS $$
DECLARE
  "atPos" integer;
BEGIN
  -- Be non-null
  IF address IS NULL THEN
    RETURN 'Email.validate.isNull';
  END IF;
  -- Be non-empty
  IF LENGTH(address) = 0 THEN
    RETURN 'Email.validate.empty';
  END IF;
  "atPos" := position('@' IN address);
  IF "atPos" = 0 THEN
    RETURN 'Email.validate.noAt';
  END IF;
  RETURN "com.aoapps.net"."Email.validate"(
    substring(address from 1 for "atPos" - 1),
    substring(address from "atPos" + 1)::citext
  );
END;
$$ LANGUAGE plpgsql
STABLE; -- IMMUTABLE possible? since queries TopLevelDomains via DomainName?

COMMENT ON FUNCTION "com.aoapps.net"."Email.validate" (text) IS
'Matches method com.aoapps.net.Email.validate, but with less verbose
messages and without translations.  The database type is for final enforcement,
whereas higher level code should have already validated and provided more
detailed messages.';
