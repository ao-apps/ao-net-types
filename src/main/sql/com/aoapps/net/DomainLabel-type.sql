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

CREATE DOMAIN "com.aoapps.net"."DomainLabel" AS citext
  CHECK (value IS NULL OR "com.aoapps.net"."DomainLabel.validate"(value) IS NULL);

COMMENT ON DOMAIN "com.aoapps.net"."DomainLabel"
IS 'Matches class com.aoapps.net.DomainLabel';
