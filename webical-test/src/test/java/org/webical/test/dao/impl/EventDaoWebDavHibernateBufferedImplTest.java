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

package org.webical.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.ApplicationSettings;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.TestUtils;
import org.webical.User;
import org.webical.dao.DaoException;
import org.webical.dao.DeleteConflictException;
import org.webical.dao.UpdateConflictException;
import org.webical.dao.hibernateImpl.CalendarDaoHibernateImpl;
import org.webical.dao.hibernateImpl.EventDaoWebDavHibernateBufferedImpl;

/**
 * Tests the EventDaoWebDavHibernateBufferedImpl
 * @author ivo
 *
 */
public class EventDaoWebDavHibernateBufferedImplTest extends DataBaseTest {
	private static Log log = LogFactory.getLog(EventDaoWebDavHibernateBufferedImplTest.class);

	private EventDaoWebDavHibernateBufferedImpl eventDao;
	private CalendarDaoHibernateImpl calendarDao;
	
	/**
	 * Sets up the dao for each test
	 * @throws Exception 
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		eventDao = new EventDaoWebDavHibernateBufferedImpl();
		
		ApplicationSettings applicationSettings = new ApplicationSettings();
		applicationSettings.setPluginPackageExtension(".zip");
		applicationSettings.setCalendarRefreshTimeMs(3000);
		TestUtils.initializeApplicationSettingsFactory(applicationSettings);
		
		calendarDao = new CalendarDaoHibernateImpl();
		
	}
	
	public Event getEvent(){
		Event event = new Event();
		event.setCalendar(getCalendarForWebicalUser());
		event.setAttach(new HashSet<String>(Arrays.asList(new String[]{"attach"})));
		event.setAttendee(new HashSet<String>(Arrays.asList(new String[]{"attendee"})));
		event.setCategories(new HashSet<String>(Arrays.asList(new String[]{"category"})));
		event.setClazz("clazz");
		event.setComment(new HashSet<String>(Arrays.asList(new String[]{"comment"})));
		event.setContact(new HashSet<String>(Arrays.asList(new String[]{"contact"})));
		event.setCreated(new Date());
		event.setDescription("description");
		event.setDtEnd(new Date());
		event.setDtStamp(new Date());
		event.setDtStart(new Date());
		event.setDuration("duration?");
		event.setExDate(new HashSet<Date>(Arrays.asList(new Date[]{new Date()})));
		event.setExRule(new HashSet<String>(Arrays.asList(new String[]{"exrule"})));
		//event.setGeo("adam");
		//event.setLocation("adam");
		//event.setOrganizer("me");
		event.setPriority(1);
		event.setrDate(new HashSet<Date>(Arrays.asList(new Date[]{new Date()})));
		event.setRelated(new HashSet<String>(Arrays.asList(new String[]{"related"})));
		event.setResources(new HashSet<String>(Arrays.asList(new String[]{"resources"})));
		//event.setrRule(new HashSet<String>(Arrays.asList(new String[]{"rrule"})));
		event.setrStatus(new HashSet<String>(Arrays.asList(new String[]{"rstatus"})));
		event.setSeq(2);
		event.setStatus("status");
		event.setSummary("summary");
		event.setTransp("transp");
		event.setUid("gfd");
		event.setUrl("http://www.func.nl");
		Map<String, String> xprops = new HashMap<String, String>();
		//xprops.put("x-prop-1", "somethingorother");
		event.setxProps(xprops);
		
		return event;
	}
	
	/**
	 * Test the store method
	 * @throws DaoException 
	 */
	public void testStoreEventUnrelated() {
		Event event = new Event();
		try {
			eventDao.storeEvent(event);
			fail("Should not be able to store an unrelated event");
		} catch (DaoException e) {
			//Expected
		} catch (Exception e) {
			//fail("Unexpected exception... " + e);
			//FIXME org.hibernate.PropertyValueException...
			//(org.hibernate.AssertionFailure)
			log.warn("THIS SHOUL REALLY BE FIXED :");
		}
		
	}
	
	/**
	 * Test the store method
	 * @throws DaoException 
	 */
	public void testStoreAndRemoveEventMinimal() throws DaoException {
		Event event = new Event();
		String description = "test description!!!";
		event.setDescription(description);
		event.setCalendar(getCalendarForWebicalUser());
		
		List<Event> currentEvents = eventDao.getAllEvents(getCalendarForWebicalUser());
	
		int beforeCount = currentEvents.size();
		
		eventDao.storeEvent(event);
		
		List<Event> eventsAfter = eventDao.getAllEvents(getCalendarForWebicalUser());
		
		int afterStore = eventsAfter.size();
		
		for(Event tempEvent : eventsAfter){
			if((tempEvent.getDescription() != null) && (tempEvent.getDescription().equals(description))){
				event = tempEvent;
				break;
			}
		}
		
		eventDao.removeEvent(event);		
		
		int afterCount = eventDao.getAllEvents(getCalendarForWebicalUser()).size();

		assertEquals(beforeCount, afterStore - 1);
		assertEquals(beforeCount, afterCount);
	}
	
	/**
	 * Tests the getAllEvents method and the storeEvents
	 * @throws DaoException 
	 */
	public void testGetAllEvents() throws DaoException {
		
		List<Event> events = eventDao.getAllEvents(getCalendarForWebicalUser());
		assertNotNull(events);
		
		assertEquals(47, events.size());
		
		Long calendarId = getCalendarForWebicalUser().getCalendarId();
		for(Event event: events) {
			assertEquals(calendarId, event.getCalendar().getCalendarId());
		}
	}
	
	/**
	 * Tests the getEventsForPeriod for a calendar method
	 * @throws DaoException 
	 */
	public void testGetEventsForPeriodForCalendar() throws DaoException {
		
		Date startDate = org.webical.util.CalendarUtils.getStartOfDay(new GregorianCalendar(2006,java.util.Calendar.NOVEMBER, 23).getTime());
		
		Date endDate = org.webical.util.CalendarUtils.getEndOfDay(startDate);
	
		Date startBefore = org.webical.util.CalendarUtils.addDays(startDate, -1);
		Date endBefore =  org.webical.util.CalendarUtils.getEndOfDay(startBefore);
		
		List<Event> events = eventDao.getEventsForPeriod(getCalendarForWebicalUser(), startDate, endDate);
		List<Event> events_before = eventDao.getEventsForPeriod(getCalendarForWebicalUser(),startBefore , endBefore);
		
		assertNotNull(events);
		assertEquals(1, events.size());
		assertEquals(0, events_before.size());
		
		assertTrue(events.get(0).getCalendar().getCalendarId().equals(getCalendarForWebicalUser().getCalendarId()));
		
		assertTrue(events.get(0).getDtStart().getTime() >= startDate.getTime());
		
		assertTrue(events.get(0).getDtStart().getTime() <= endDate.getTime());
	}
	
	public void testUpdateConflict(){
		
		Calendar calendar = getCalendar("webical");	
		
		Event event = getEvent();
		event.setCalendar(calendar);
		//Is there is a last mod date the synchronization expect the
		//event to occur in the ics. If the lastmod is before the found lastmod date
		// there is an updateconflict
		Date d = new Date();
		d.setTime(10000);
		event.setLastMod(d);
		event.setUid("uuid:1159123236869");
		
		try {
			eventDao.storeEvent(event);
			fail("No UpdateConflict found");
		} catch (DeleteConflictException e) {
			fail("DeleteConflict");
		} catch (UpdateConflictException e) {
			
		} catch (DaoException e) {
			fail("DaoException");
		}
	}
	
	public void testDeleteConflict(){
		
		Calendar calendar = getCalendar("webical");	
		
		Event event = getEvent();
		event.setCalendar(calendar);
		//Is there is a last mod date the synchronization expect the
		//event to occur in the ics. If not occured there is a deleteconflict
		event.setLastMod(new Date());
		event.setUid("xxxxx");
		
		try {
			eventDao.storeEvent(event);
			fail("No DeleteConflict found");
		} catch (DeleteConflictException e) {
		} catch (UpdateConflictException e) {
			fail("updateConflict");
		} catch (DaoException e) {
			fail("DaoException");
		}
	}
	
	public void testRecurrence(){
		
		Calendar calendar = getCalendarForWebicalUser();
			
		try {
			
			Date startDate = org.webical.util.CalendarUtils.getStartOfDay(new GregorianCalendar(2006,java.util.Calendar.FEBRUARY, 1).getTime());
			Date endDate = org.webical.util.CalendarUtils.getEndOfDay(startDate);
			
			List<Event> events = eventDao.getEventsForPeriod(calendar, startDate, endDate);
			
			//first occurrence, this is the real event, assumed that there are no other events
			//given date
			assertEquals(1, events.size());
			
			startDate = org.webical.util.CalendarUtils.addDays(startDate, 3);
			endDate = org.webical.util.CalendarUtils.getEndOfDay(startDate);
			
			//the thirth event in the recurrence
			events = eventDao.getEventsForPeriod(calendar, startDate, endDate);
			
			assertEquals(1, events.size());
			
			startDate = org.webical.util.CalendarUtils.addDays(startDate, 4);
			endDate = org.webical.util.CalendarUtils.getEndOfDay(startDate);
			
			//this event is valid for the recurrence rule but this date is set in the exclusion property  
			events = eventDao.getEventsForPeriod(calendar, startDate, endDate);
			assertEquals(0, events.size());
			
		} catch (DeleteConflictException e) {
		} catch (UpdateConflictException e) {
			fail("updateConflict");
		} catch (DaoException e) {
			fail("DaoException");
		}
	}
	
	/**
	 * @return a Calendar for user webical
	 */
	private Calendar getCalendarForWebicalUser() {
		return getCalendar("webical");
	}
		
	/**
	 * Retruns a Calendar
	 * @param userId the user to get a Calendar for
	 * @return a Calendar or null
	 */
	private Calendar getCalendar(String userId) {
		User user = new User();
		user.setUserId(userId);
		try {
			return calendarDao.getCalendars(user).get(0);
		} catch (DaoException e) {
			log.error(e,e);
			fail("Could not retrieve calendar");
		}
		
		return null;
	}

}

