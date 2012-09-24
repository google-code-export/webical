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

package org.webical.dao.hibernateImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.criterion.Restrictions;
import org.hibernate.id.Configurable;
import org.hibernate.id.UUIDHexGenerator;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.dao.DaoException;
import org.webical.dao.DeleteConflictException;
import org.webical.dao.EventDao;
import org.webical.dao.UpdateConflictException;
import org.webical.dao.annotation.Transaction;
import org.webical.dao.util.WebDavCalendarSynchronisation;
import org.webical.ical.RecurrenceUtil;
import org.webical.settings.ApplicationSettingsFactory;

/**
 *
 * EventDao implemention for Buffered ical over webdav
 * @author ivo
 *
 */
public class EventDaoWebDavHibernateBufferedImpl extends BaseHibernateImpl implements EventDao {

	private static Log log = LogFactory.getLog(EventDaoWebDavHibernateBufferedImpl.class);

	private UUIDHexGenerator uuidGen = null;

	///////////////////
	/// API METHODS ///
	///////////////////

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#storeEvent(org.webical.Event)
	 */
	@Transaction(readOnly=false)
	@SuppressWarnings("unchecked")
	public void storeEvent(Event updatedEvent) throws DaoException, DeleteConflictException, UpdateConflictException
	{
		if (updatedEvent == null) return;

		try {
			boolean update = (updatedEvent.getUid() != null);

			// Create a new WebDavCalendarSynchronisation util
			WebDavCalendarSynchronisation calendarSynchronisation = new WebDavCalendarSynchronisation();
			if (update) {
				if (log.isDebugEnabled()) {
					log.debug("Merging existing Event: " + updatedEvent.getUid());
				}

				//Remove the old events (except the updated event)
				Criteria criteria = getSession().createCriteria(Event.class);
				criteria.add(Restrictions.eq(Calendar.CALENDAR_PROPERTY_NAME, updatedEvent.getCalendar()));
				criteria.add(Restrictions.not(Restrictions.idEq(updatedEvent.getEventId())));
				List<Event> eventsToRemove = criteria.list();
				if (eventsToRemove != null) {
					deleteAll(eventsToRemove);
				}

				// Synchronize the updated event
				List<Event> newEvents = calendarSynchronisation.updateEvent(updatedEvent, updatedEvent.getCalendar());

				//Store the new events
				if (newEvents != null) {
					for (Event event : newEvents) {
						saveOrUpdate(event);
					}
				}

				// Write changes to remote calendar
				calendarSynchronisation.writeToRemoteCalendarFile(updatedEvent.getCalendar(), newEvents);
			} else {
				updatedEvent.setUid(generateUid());

				if (log.isDebugEnabled()) {
					log.debug("Storing new Event: " + updatedEvent.getUid());
				}

				saveOrUpdate(updatedEvent);

				// Calendar Ical4J over webdav has its own transactions?
				calendarSynchronisation.writeToRemoteCalendarFile(updatedEvent.getCalendar(), getEventsForCalendar(updatedEvent.getCalendar()));
			}
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not store Event", e);
		}
	}

	/**
	 * NOT A PART OF THE PUBLIC API
	 * @see org.webical.aspect.dao.EventDao#storeEvents(java.util.List)
	 */
	@Transaction(readOnly=false)
	public void storeEvents(List<Event> events) throws DaoException
	{
		if (events == null) return;

		try {
			saveOrUpdateAll(events);
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not store Event", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#removeEvent(org.webical.Event)
	 */
	@Transaction(readOnly=false)
	public void removeEvent(Event event) throws DaoException
	{
		if (event == null) return;

		try {
			if (log.isDebugEnabled()) log.debug("removeEvent: " + event.toString());

			// Remove the event from the session
			getSession().buildLockRequest(new LockOptions(LockMode.NONE)).lock(event);
			delete(event);

			// First refresh the calendar from remote
			List<Event> events = refreshCalendarEvents(event.getCalendar());

			// Remove the event (if still exists)
			Event removedEvent = null;
			for (Event e : events) {
				if (e.getUid().equals(event.getUid())) {
					removedEvent = e;
				}
			}
			// 2x ?
			if (removedEvent != null)
			{
				getSession().buildLockRequest(new LockOptions(LockMode.NONE)).lock(removedEvent);
				delete(removedEvent);

				//Synchronize the removed event
				//Create a new WebDavCalendarSynchronisation util
				WebDavCalendarSynchronisation calendarSynchronisation = new WebDavCalendarSynchronisation();
				//Sync to the remote ics file
				calendarSynchronisation.writeToRemoteCalendarFile(removedEvent.getCalendar(), getAllEvents(removedEvent.getCalendar()));
			}
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not remove Event", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#getAllEvents(org.webical.Calendar)
	 */
	@Transaction(readOnly=false)
	public List<Event> getAllEvents(Calendar calendar) throws DaoException
	{
		if (calendar == null) return null;

		refreshCalendarEventsAfterRefreshTime(calendar);
		return getEventsForCalendar(calendar);
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#removeAllEventsForCalendar(org.webical.Calendar)
	 */
	@Transaction(readOnly=false)
	public void removeAllEventsForCalendar(Calendar calendar) throws DaoException
	{
		if (calendar == null) return;

		if (log.isDebugEnabled()) {
			log.debug("Removing all events for calendar: " + calendar.getName());
		}
		try {
			getSession().buildLockRequest(new LockOptions(LockMode.NONE)).lock(calendar);
			removeAllEvents(getEventsForCalendar(calendar));
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not remove EventList",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#getEventsForPeriod(org.webical.Calendar, java.util.Date, java.util.Date)
	 */
	@Transaction(readOnly=false)
	@SuppressWarnings("unchecked")
	public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) throws DaoException
	{
		if (calendar == null || dtStart == null || dtEnd == null) return null;

		List<Event> eventList = new ArrayList<Event>();

		refreshCalendarEventsAfterRefreshTime(calendar);
		try {
			Criteria criteria = getSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq(Calendar.CALENDAR_PROPERTY_NAME, calendar));
			criteria.add(Restrictions.lt("dtStart", new Date(dtEnd.getTime()+1000)));
			criteria.add(Restrictions.gt("dtEnd", new Date(dtStart.getTime()-1000)));
			if (log.isDebugEnabled()) log.debug(criteria.toString() + " " + calendar.getName());

			List<Event> events = criteria.list();
			eventList.addAll(this.getRecurringEvents(calendar, events, dtStart, dtEnd));

			if (log.isDebugEnabled()) {
				log.debug("Events: " + eventList.size() + " for period: " + dtStart + " to " + dtEnd );
			}
			return eventList;
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not get Events", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#getEventByUid(org.webical.Calendar, java.lang.String)
	 */
	@Transaction(readOnly=true)
	public Event getEventByUid(Calendar calendar, String uid) throws DaoException
	{
		if (calendar == null || uid == null) return null;

		Criteria criteria = getSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("uid", uid));
		criteria.add(Restrictions.eq(Calendar.CALENDAR_PROPERTY_NAME, calendar));

		Event event = null;
		if (criteria.list().size() > 0) {
			event = (Event)criteria.uniqueResult();
		}
		return event;
	}

	//////////////////////
	/// HELPER METHODS ///
	//////////////////////

	/**
	 * gets All Events for a calendar without checking the calendarRefreshTime
	 * @param calendar the calendar to get the events for
	 * @return all events
	 * @throws DaoException
	 */
	@Transaction(readOnly=true)
	@SuppressWarnings("unchecked")
	private List<Event> getEventsForCalendar(Calendar calendar) throws DaoException
	{
		if (calendar == null) return null;

		try {
			Criteria criteria = getSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq(Calendar.CALENDAR_PROPERTY_NAME, calendar));
			return criteria.list();
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not get Events", e);
		}
	}

	/**
	 * Checks if its necessary to update the calendar from the remote file
	 * @param calendar the calendar to check
	 * @throws DaoException
	 */
	@Transaction(readOnly=false)
	private void refreshCalendarEventsAfterRefreshTime(Calendar calendar) throws DaoException
	{
		if (calendar == null) return;

		try {
			long calendarRefreshTimeMs = ApplicationSettingsFactory.getInstance().getApplicationSettings().getCalendarRefreshTimeMs();
			if (log.isDebugEnabled()) {
				log.debug("refreshCalendarEventsAfterRefreshTime, checking if the calendar needs refreshing. Last refreshtime: " + calendar.getLastRefreshTimeStamp()
						+ " Refresh interval: " + calendarRefreshTimeMs
						+ " current time: " + System.currentTimeMillis());
			}
			boolean refresh = false;
			if (calendar.getLastRefreshTimeStamp() == null)
			{
				refresh = true;
			}
			else
			{
				if ((calendar.getLastRefreshTimeStamp() + calendarRefreshTimeMs) < System.currentTimeMillis())
				{
					refresh = false;
				}
				else
				{
					WebDavCalendarSynchronisation calendarSynchronisation = new WebDavCalendarSynchronisation();
					Date calLastMod = calendarSynchronisation.lastModificationCalendar(calendar);
					if (calLastMod == null  ||
						calLastMod.getTime() >= calendar.getLastRefreshTimeStamp()) refresh = true;
				}
			}
			if (refresh) {
				refreshCalendarEvents(calendar);
			}
		}
		catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not get Events", e);
		}
	}

	/**
	 * Updates the events for the calendar from the remote file
	 * @param calendar the calendar to check
	 * @return the new Events
	 * @throws DaoException
	 */
	@Transaction(readOnly=false)
	private List<Event> refreshCalendarEvents(Calendar calendar) throws DaoException
	{
		if (calendar == null) return null;

		if (log.isDebugEnabled()) {
			log.debug("refreshCalendarEvents " + calendar.getName());
		}

		try {
			//Create a new WebDavCalendarSynchronisation util
			WebDavCalendarSynchronisation calendarSynchronisation = new WebDavCalendarSynchronisation();

			//remove all events from the database
			removeAllEventsForCalendar(calendar);

			//build a new calendar from the remote ics file
			List<Event> events = calendarSynchronisation.getEventsFromRemoteCalendar(calendar);

			//store all events in the database as a cache
			storeEvents(events);

			//set the lastest refreshtime from the calendar to now
			calendar.setLastRefreshTimeStamp(System.currentTimeMillis());
			//Save the calendar
			saveCalendar(calendar);

			return events;

		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not get Events", e);
		}
	}

	@Transaction(readOnly=false)
	private void saveCalendar(Calendar calendar) throws DaoException {
		getSession().merge(calendar);
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#removeAllEvents(java.util.List)
	 */
	@Transaction(readOnly=false)
	private void removeAllEvents(List<Event> events) throws DaoException
	{
		if (events == null) return;

		try {
			deleteAll(events);
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not remove EventList",e);
		}
	}

	/**
	 * Retrieve a list of Events that have a recurrent occurrence in a given daterange
	 * @param calendar the calendar for which the Events have to be searched
	 * @param dbEvents A list of Events that are already valid for this daterange (to exclude double occurrences)
	 * @param dtStart start date for the daterange (inclusive)
	 * @param dtEnd (inclusive)
	 * @return a List of recurrent Events
	 * @throws DaoException
	 */
	private List<Event> getRecurringEvents(Calendar calendar, List<Event> dbEvents, Date dtStart, Date dtEnd) throws DaoException
	{
		if (calendar == null) return null;

		//the list of events to return
		List<Event> applicableEvents = new ArrayList<Event>();
		List<Event> recurringEvents = getAllRecurringEventsForCalendar(calendar);
		try {
			//Check if the events are applicable for the given date range
			for (Event event : recurringEvents) {
				if (RecurrenceUtil.isApplicableForDateRange(event, dtStart, dtEnd)) {
					applicableEvents.add(event);
				}
			}

			//add the events from the database that are valid for the given daterange and not
			//already is the list to show
			for (Event event : dbEvents) {
				if (RecurrenceUtil.isApplicableForDateRange(event, dtStart, dtEnd) && !applicableEvents.contains(event))
				{
					applicableEvents.add(event);
				}
			}
		} catch (ParseException e) {
			log.error(e, e);
			throw new DaoException(e);
		}
		return applicableEvents;
	}

	/**
	 * Retrieve a List of all recurring Event for a given calendar
	 * @param calendar the Calendar for which the recurring Events are returned
	 * @return a List of all recurring Events
	 * @throws DaoException
	 */
	@Transaction(readOnly=true)
	@SuppressWarnings("unchecked")
	private List<Event> getAllRecurringEventsForCalendar(Calendar calendar) throws DaoException
	{
		if (calendar == null) return null;

		try {
			Criteria criteria = getSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq(Calendar.CALENDAR_PROPERTY_NAME, calendar));
			criteria.add(
					Restrictions.or(
						Restrictions.isNotEmpty("rDate"),
						Restrictions.isNotEmpty("rRule")
					)
				);
			if (log.isDebugEnabled()) log.debug(criteria.toString() + " " + calendar.getName());
			return criteria.list();
		} catch(Exception e) {
			log.error(e);
			throw new DaoException("Could not get recurringEvents",e);
		}
	}

	/**
	 * returns a unique uid for an Event
	 */
	private String generateUid() {
		StringBuffer sbUid = new StringBuffer();
		sbUid.append("WEBICAL-");
		sbUid.append((String) getUuidGen().generate(null, null));
		sbUid.append("webical.org");
		return sbUid.toString();
	}

	/**
	 * Uses the hibernate uid generator
	 * @return a UUIDHexGenerator
	 */
	private UUIDHexGenerator getUuidGen()
	{
		if (uuidGen != null) return uuidGen;

		Properties uidprops = new Properties();
		uidprops.setProperty("separator", "-");
		uuidGen = new UUIDHexGenerator();
		((Configurable) uuidGen).configure(org.hibernate.type.StandardBasicTypes.STRING, uidprops, null);
		return uuidGen;
	}
}
