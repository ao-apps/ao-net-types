/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2018  AO Industries, Inc.
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
-- Must be done as "postgres" user:
CREATE EXTENSION IF NOT EXISTS citext;

-- TODO: PostgreSQL 11: Implement as domain over composite type of
-- either DomainName or InetAddress (but not both).

CREATE DOMAIN "com.aoindustries.net"."HostAddress" AS citext
  CHECK (value IS NULL OR "com.aoindustries.net"."HostAddress.validate"(value) IS NULL);

COMMENT ON DOMAIN "com.aoindustries.net"."HostAddress" IS
'Matches class com.aoindustries.net.HostAddress';
