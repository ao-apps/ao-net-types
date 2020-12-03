/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2020  AO Industries, Inc.
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
package com.aoindustries.net;

import com.aoindustries.util.i18n.EditableResourceBundle;
import java.util.Locale;

/**
 * @author  AO Industries, Inc.
 */
public final class ApplicationResources_ja extends EditableResourceBundle {

	public ApplicationResources_ja() {
		super(
			Locale.JAPANESE,
			ApplicationResources.bundleSet,
			ApplicationResources.getSourceFile("ApplicationResources_ja.properties")
		);
	}
}
