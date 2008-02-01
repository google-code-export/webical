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

package org.webical.dao.hibernateImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
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

	private static final String CALENDAR_PROPERTY_NAME = "calendar";
	private static Log log = LogFactory.getLog(EventDaoWebDavHibernateBufferedImpl.class);

	private UUIDHexGenerator uuidGen;

	///////////////////
	/// API METHODS ///
	///////////////////

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#storeEvent(org.webical.Event)
	 */
	@Transaction
	@SuppressWarnings("unchecked")
	public void storeEvent(Event updatedEvent) throws DaoException, DeleteConflictException, UpdateConflictException {
		if(updatedEvent == null) {
			return;
		}

		try {
			boolean update = (updatedEvent.getUid() != null);

			//Create a new WebDavCalendarSynchronisation util
			WebDavCalendarSynchronisation calendarSynchronisation = new WebDavCalendarSynchronisation();

			if(update) {
				if(log.isDebugEnabled()) {
					log.debug("Merging existing Event: " + updatedEvent.getUid());
				}

				//Remove the old events (except the updated event)
				getSession().beginTransaction();
				Criteria criteria = getSession().createCriteria(Event.class);
				criteria.add(Restrictions.not(Restrictions.idEq(updatedEvent.getEventId())));
				List<Event> eventsToRemove = criteria.list();
				if(eventsToRemove != null && eventsToRemove.size() > 0) {
					deleteAll(eventsToRemove);
				}
				getSession().getTransaction().commit();

				//Synchronize the updated event
				List<Event> newEvents = calendarSynchronisation.updateEvent(updatedEvent, updatedEvent.getCalendar());


				//Store the new events
				if(newEvents != null && newEvents.size() > 0) {
					getSession().beginTransaction();
					for (Event event : newEvents) {
						saveOrUpdate(event);
					}
					getSession().getTransaction().commit();
				}

				//Write changes to remote calendar
				calendarSynchronisation.writeToRemoteCalendarFile(updatedEvent.getCalendar(), newEvents);

			} else {
				updatedEvent.setUid(generateUid());

				if(log.isDebugEnabled()) {
					log.debug("Storing new Event: " + updatedEvent.getUid());
				}

				saveOrUpdate(updatedEvent);

				calendarSynchronisation.writeToRemoteCalendarFile(updatedEvent.getCalendar(), getEventsForCalendar(updatedEvent.getCalendar()));

			}
		} catch (DeleteConflictException e){
			log.error(e,e);
			throw new DeleteConflictException("Could not store Event", e);
		} catch (UpdateConflictException e){
			log.error(e,e);
			throw new UpdateConflictException("Could not store Event", e);
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not store Event", e);
		}
	}

	/**
	 * NOT A PART OF THE PUBLIC API
	 * @see org.webical.aspect.dao.EventDao#storeEvents(java.util.List)
	 */
	@Transaction
	public void storeEvents(List<Event> events) throws DaoException {
		if(events == null || events.size() == 0) {
			return;
		}

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
	@Transaction
	public void removeEvent(Event event) throws DaoException {
		if(event == null) {
			return;
		}

		try {

			//Remove the event from the session
			delete(event);

			//First refresh the calendar from remote
			List<Event> events = refreshCalendarEvents(event.getCalendar());

			//remove the event (if still exists)
			Event removedEvent = null;

			for(Event e : events){
				if(e.getUid().equals(event.getUid())){
					removedEvent = e;
				}
			}

			if (removedEvent != null){
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
	 * @see org.webical.aspect.dao.EventDao#removeAllEvents(java.util.List)
	 */
	@Transaction
	public void removeAllEvents(List<Event> events) throws DaoException {
		if(events == null || events.size() == 0) {
			return;
		}

		try {
			deleteAll(events);
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not remove EventList",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#removeAllEventsForCalendar(org.webical.Calendar)
	 */
	public void removeAllEventsForCalendar(Calendar calendar) throws DaoException {
		if(calendar == null) {
			return;
		}

		log.debug("Removing all events for calendar: " + calendar.getName());

		try {
			removeAllEvents(getEventsForCalendar(calendar));
		}catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not remove EventList",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#getAllEvents(org.webical.Calendar)
	 */
	public List<Event> getAllEvents(Calendar calendar) throws DaoException {
		if(calendar == null) {
			return null;
		}

		refreshCalendarEventsAfterRefreshTime(calendar);
		return getEventsForCalendar(calendar);
	}

	/**
	 * gets All Events for a calendar without checking the calendarRefreshTime
	 * @param calendar the calendar to get the events for
	 * @return all events
	 * @throws DaoException
	 */
	@Transaction
	@SuppressWarnings("unchecked")
	private List<Event> getEventsForCalendar(Calendar calendar) throws DaoException {
		if(calendar == null) {
			return null;
		}

		try {

			Criteria criteria = getSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq(CALENDAR_PROPERTY_NAME, calendar));
			List<Event> events =  criteria.list();

			return events;

		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not get Events", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#getEventsForPeriod(org.webical.Calendar, java.util.Date, java.util.Date)
	 */
	@Transaction
	@SuppressWarnings("unchecked")
	public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) throws DaoException {
		if(calendar == null || dtStart == null || dtEnd == null) {
			return null;
		}

		List<Event> eventList = new ArrayList<Event>();

		refreshCalendarEventsAfterRefreshTime(calendar);
		try {

			Criteria criteria = getSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq(CALENDAR_PROPERTY_NAME, calendar));
			criteria.add(Restrictions.between("dtStart", new Date(dtStart.getTime()-1000), new Date(dtEnd.getTime()-1000)));

			List<Event> events = criteria.list();

			eventList.addAll(this.getRecurringEvents(calendar, events,dtStart, dtEnd));

			log.debug("Events: " + eventList.size() + " for period: " + dtStart + " to " + dtEnd );
			return eventList;
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not get Events", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EventDao#getEventByUid(org.webical.Calendar, java.lang.String)
	 */
	@Transaction
	public Event getEventByUid(Calendar calendar, String uid) throws DaoException {
		if(calendar == null || uid == null) {
			return null;
		}

		Criteria criteria = getSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("uid", uid));
		criteria.add(Restrictions.eq("calendar", calendar));

		Event event = null;
		if(criteria.list().size() > 0){
			event =  (Event)criteria.uniqueResult();
		}
		return event;
	}

	//////////////////////
	/// HELPER METHODS ///
	//////////////////////

	/**
	 * Checks if its neccecary to update the calendar from the remote file
	 * @param calendar the calendar to check
	 * @throws DaoException
	 */
	private void refreshCalendarEventsAfterRefreshTime(Calendar calendar) throws DaoException {
		if(calendar == null) {
			return;
		}

		try{
			long calendarRefreshTimeMs = ApplicationSettingsFactory.getInstance().getApplicationSettings().getCalendarRefreshTimeMs();
			if(log.isDebugEnabled()) {
				log.debug("refreshCalendarEventsAfterRefreshTime, checking if the calendar needs refreshing. Last refreshtime: " + calendar.getLastRefreshTimeStamp()
						+ " Refresh interval: " + calendarRefreshTimeMs
						+ " current time: " + System.currentTimeMillis());
			}
			if(calendar.getLastRefreshTimeStamp() == null || (calendar.getLastRefreshTimeStamp() + calendarRefreshTimeMs) < System.currentTimeMillis() ){
				refreshCalendarEvents(calendar);
			}
		} catch (Exception e) {
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
	private List<Event> refreshCalendarEvents(Calendar calendar) throws DaoException {
		if(calendar == null) {
			return null;
		}

		if(log.isDebugEnabled()) {
			log.debug("refreshCalendarEvents");
		}
		try{
			//Create a new WebDavCalendarSynchronisation util
			WebDavCalendarSynchronisation calendarSynchronisation = new WebDavCalendarSynchronisation();

			//remove all events from the database
			removeAllEventsForCalendar(calendar);

			//build a new calendar from the remote ics file
			List<Event> events = calendarSynchronisation.getEventsFromRemoteCalendar(calendar);

			//store all events in the database as a cache
			if(events != null && events.size() > 0) {
				storeEvents(events);
			}

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

	@Transaction
	private void saveCalendar(Calendar calendar) throws DaoException {
		getSession().merge(calendar);
	}

	/**
	 * Retrieve a list of Events that have a recurrent occurence in a given daterange
	 * @param calendar the calendar for which the Events have to be searched
	 * @param dbEvents A list of Events that are already valid for this daterange (to exclude double occurences)
	 * @param dtStart start date for the daterange (inclusive)
	 * @param dtEnd (inclusive)
	 * @return a List of recurrent Events
	 * @throws DaoException
	 */
	private List<Event> getRecurringEvents(Calendar calendar, List<Event> dbEvents, Date dtStart, Date dtEnd) throws DaoException {
		if(calendar == null) {
			return null;
		}

		//the list of events to return
		List<Event> applicableEvents = new ArrayList<Event>();


		List<Event> recurringEvents = getAllRecurringEventsForCalendar(calendar);

		try {
			//Check if the events are applicable for the given date range
			for (Event event : recurringEvents) {
				if(RecurrenceUtil.isApplicableForDateRange(event, dtStart, dtEnd)){
					applicableEvents.add(event);
				}
			}

			//add the events from the datebase that are valid for the given daterange and not
			//already is the list to show
			for( Event event : dbEvents ){

				if(RecurrenceUtil.isApplicableForDateRange(event, dtStart, dtEnd) && !applicableEvents.contains(event)){
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
	 * Retrieve a List of all recuring Event for a given calendar
	 * @param calendar the Calendar for which the recurring Events are returned
	 * @return a List of all recurring Events
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	private List<Event> getAllRecurringEventsForCalendar(Calendar calendar) throws DaoException {
		if(calendar == null) {
			return null;
		}

		try {
			Criteria criteria = getSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq("calendar", calendar));
			criteria.add(
					Restrictions.or(
						Restrictions.isNotEmpty("rDate"),
						Restrictions.isNotEmpty("rRule")
					)
				);

			return criteria.list();
		}  catch(Exception e){
			log.error(e);
			throw new DaoException("Could not get recurringEvents",e);
		}
	}

	/**
	 * returns a unique uid for an Event
	 */
	private String generateUid(){
		return "WEBICAL-" + (String)getUuidGen().generate(null, null) +
		"webical.org";
	}

	/**
	 * Uses the hibernate uid generator
	 * @return a UUIDHexGenerator
	 */
	private UUIDHexGenerator getUuidGen() {
		if (uuidGen != null) {
			return uuidGen;
		}

		Properties uidprops = new Properties();
		uidprops.setProperty("separator", "-");
		uuidGen = new UUIDHexGenerator();
		((Configurable)uuidGen).configure(Hibernate.STRING, uidprops, null);
		return uuidGen;
	}
}
