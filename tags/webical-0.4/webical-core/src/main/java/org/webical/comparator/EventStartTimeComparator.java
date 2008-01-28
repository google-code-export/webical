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

import java.util.Calendar;
import java.util.Comparator;

import org.webical.Event;

/**
 * Compares two events based on their startime
 * @author ivo
 *
 */
public class EventStartTimeComparator implements Comparator<Event> {
	
	public enum CompareMode {FULL_DATE, TIME};
	
	private CompareMode compareMode;
	
	/**
	 * Mode constructor - compares either on the Full date or just the time
	 */
	public EventStartTimeComparator(CompareMode compareMode) {
		this.compareMode = compareMode;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Event event1, Event event2) {
		if(event1.getDtStart() == null) {
			return -1;
		}
		
		if(event2.getDtStart() == null) {
			return 1;
		}
		
		if(compareMode.equals(CompareMode.FULL_DATE)) {
			return event1.getDtStart().compareTo(event2.getDtStart());
		} else {
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(event1.getDtStart());
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(event2.getDtStart());
			calendar2.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
			return calendar1.compareTo(calendar2);
		}
	}

}
