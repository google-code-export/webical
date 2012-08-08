/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    $Id$
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

package org.webical.test.dao.impl.util;

import java.text.ParseException;
import java.util.List;
import java.util.GregorianCalendar;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.dao.util.ComponentFactory;

import junit.framework.TestCase;

public class ComponentFactoryTest extends TestCase {

	/**
	 *  Method for testing to build a webicalCalendar from an ical4jCalendar (*.ics) calendar with
	 *  all the events
	 */
	public void testbuildCalendarFromIcalCalendar() {
		Calendar calendar = new Calendar();
		net.fortuna.ical4j.model.Calendar ical4jCalendar = new net.fortuna.ical4j.model.Calendar();

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 31);

		VEvent myBirthday = new VEvent();

		Summary sumProperty = new Summary("verjaardag");
		myBirthday.getProperties().add(sumProperty);

		Status statusProperty = new Status("CONFIRMED");
		myBirthday.getProperties().add(statusProperty);

		ical4jCalendar.getComponents().add(myBirthday);

		try {
			List<Event> events = ComponentFactory.buildComponentsFromIcal4JCalendar(calendar, ical4jCalendar);
			assertNotNull(events);
			assertEquals(1, events.size());

			String status = null;

			for (Event event : events) {
				if (event.getSummary().equals("verjaardag")) {
					status = event.getStatus();
				}
			}

			assertEquals(status, "CONFIRMED");

		} catch (ParseException e) {
			fail("Could not parse the ical file");
		}
	}
}
