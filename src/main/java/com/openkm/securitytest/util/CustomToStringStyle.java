/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.securitytest.util;

import org.apache.commons.lang3.builder.ToStringStyle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomToStringStyle extends ToStringStyle {
	public static final ToStringStyle SHORT_PREFIX_STYLE = new CustomToStringStyle();
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
		if (value instanceof Date) {
			value = new SimpleDateFormat(DATE_FORMAT).format(value);
		} else if (value instanceof Calendar) {
			value = new SimpleDateFormat(DATE_FORMAT).format(((Calendar) value).getTime());
		}

		buffer.append(value);
	}
}
