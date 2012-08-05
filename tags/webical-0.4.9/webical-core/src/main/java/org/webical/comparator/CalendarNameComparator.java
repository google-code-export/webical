/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.webical.comparator;

import java.util.Comparator;

import org.webical.Calendar;

/**
 * Compares calendars based on their name
 * @author ivo
 *
 */
public class CalendarNameComparator implements Comparator<Calendar> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Calendar calendar, Calendar calendar2) {
		if(calendar.getName() == null) {
			return -1;
		} else if(calendar2.getName() == null) {
			return 1;
		} else {
			return calendar.getName().compareToIgnoreCase(calendar2.getName());
		}
	}

}
