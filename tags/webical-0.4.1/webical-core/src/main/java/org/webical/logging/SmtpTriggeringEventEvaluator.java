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

package org.webical.logging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/**
 * @author martin
 * Contributed by Func.
 * 
 */
public class SmtpTriggeringEventEvaluator implements TriggeringEventEvaluator {

	/** keep track of the last event */
	private static Map<String, Date> lastEvent = new HashMap<String, Date>();

	/** the time between two the same exceptions */
	private final static int timeout = 60 * 1000;

	/** all log levels greater of equal will pass */
	private final static Level loglevel = Level.ERROR;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.TriggeringEventEvaluator#isTriggeringEvent(org.apache.
	 *      log4j.spi.LoggingEvent)
	 */
	public boolean isTriggeringEvent(LoggingEvent event) {
		// should we consider this event as triggering?
		if (!event.getLevel().isGreaterOrEqual(loglevel)) {
			return false;
		}
		// if so, check if it already triggered us before
		LocationInfo li = event.getLocationInformation();
		String locationKey = li.getFileName() + ":" + li.getLineNumber();

		Date now = new Date();
		if (lastEvent.containsKey(locationKey)) {
			// check time
			Date date = (Date) lastEvent.get(locationKey);
			if (now.after(date)) {
				lastEvent.put(locationKey, new Date((now.getTime() + timeout)));
				return true;
			}
		} else {
			lastEvent.put(locationKey, new Date((now.getTime() + timeout)));
			return true;
		}
		return false;
	}

}