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
CREATE DOMAIN "com.aoapps.net"."InetAddress" AS inet CHECK (
    value IS NULL
    -- Must have a mask of 32 for IPv4 or 128 for IPv6
    OR masklen(value) = CASE WHEN family(value) = 4 THEN 32 WHEN family(value) = 6 THEN 128 ELSE -1 END
);

COMMENT ON DOMAIN "com.aoapps.net"."InetAddress"
IS 'Matches class com.aoapps.net.InetAddress';
