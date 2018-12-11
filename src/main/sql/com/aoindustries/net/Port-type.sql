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
-- TODO: Should this be a composite type, combining port number and protocol like
--       the Java objects?  How well do the current PostgreSQL JDBC drivers handle
--       composite types?
CREATE DOMAIN "com.aoindustries.net"."Port" AS integer
  CHECK (value >= 1 and value <= 65535);

COMMENT ON DOMAIN "com.aoindustries.net"."Port"
IS 'Matches class com.aoindustries.net.Port';
