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

package org.webical.dao.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VTimeZone;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.dao.ConnectionDaoException;
import org.webical.dao.DaoException;
import org.webical.dao.DeleteConflictException;
import org.webical.dao.IcalParserDaoException;
import org.webical.dao.InvalidURIDaoException;
import org.webical.dao.SSLDaoException;
import org.webical.dao.UpdateConflictException;
import org.webical.dao.encryption.Encryptor;

/**
 * @author paul
 * The WebDavCalendarSynchronisation takes care about the synchronisation from the
 * remote calendarfile and the local calendar.
 *
 */
public class WebDavCalendarSynchronisation {

	private static Log log = LogFactory.getLog(WebDavCalendarSynchronisation.class);

	/**
	 * @param encrypter the {@link Encryptor} to use when connecting
	 */
	public WebDavCalendarSynchronisation() {
	}

	/**
	 * @param calendar the calendar with the information about the remote calendar
	 * @return the list of events parsed from the remote iCal4J calendar
	 * @throws DaoException with wrapped exception
	 */
	public List<Event> getEventsFromRemoteCalendar(final Calendar calendar) throws DaoException {

		if (calendar == null || calendar.getUrl() == null) {
			throw new DaoException("calendar or url should not be null");
		}
		if (log.isDebugEnabled()) log.debug("getEventsFromRemoteCalendar " + calendar.getName());

		try {
			return ComponentFactory.buildComponentsFromIcal4JCalendar(calendar, getIcal4JCalendarFromRemoteCalendar(calendar));
		} catch (URIException e) {
			log.error(e,e);
			throw new InvalidURIDaoException("Invalid url",e);
		} catch (IOException e) {
			log.error(e,e);
			throw new ConnectionDaoException("Could not create connection", e);
		} catch (ParserException e) {
			log.error(e,e);
			throw new IcalParserDaoException("Could not parse the ical file", e);
		} catch (ParseException e) {
			log.error(e,e);
			throw new IcalParserDaoException("Could not parse iCal date" ,e);
		} catch (KeyManagementException e) {
			log.error(e,e);
			throw new SSLDaoException("Could not connect to ssl socket" ,e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e,e);
			throw new SSLDaoException("Could not connect to ssl socket" ,e);
		} catch (IllegalArgumentException e) {
			log.error(e,e);
			throw new InvalidURIDaoException("Invalid url",e);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	/**
	 * Uploads the eventlist to the remote calendar file
	 * @param calendar the calendar witch holds the remote location
	 * @param events the events to place in the calendar file
	 * @throws DaoException with wrapped exception
	 */
	public void writeToRemoteCalendarFile(final Calendar calendar, List<Event> events) throws DaoException {

		if (calendar == null || calendar.getUrl() == null) {
			throw new DaoException("calendar or url should not be null");
		}
		if (log.isDebugEnabled()) log.debug("writeToRemoteCalendarFile " + calendar.getName() + ":" + events.size());

		HttpURLConnection connection = null;
		try {
			try {
				//Set up authentication if needed
				if (calendar.getUsername() != null && calendar.getPassword() != null) {
					ConnectionUtil.setAuthentication(calendar.getUsername(), calendar.getPassword());
				}
			} catch (Exception e) {
				log.error("Error in decrypting password", e);
				throw new DaoException("Error in decrypting password", e);
			}

			//Get the correct output source and build the calendar
			URL url = new URL(calendar.getUrl());
			if (url.getProtocol().equalsIgnoreCase(ConnectionUtil.HTTPS_PROTOCOL)) {
				//TODO get from User Settings
				connection = ConnectionUtil.getHttpsUrlConnection(url, true);
			} else {
				connection = ConnectionUtil.getHttpUrlConnection(url);
			}

			//Set up the calendar builder and parse the calendar
			//XXX turned off validation - could not find out why this version of ical4j (1.0-beta4) is chocking on valid calendars.
			CalendarOutputter calendarOutputter = new CalendarOutputter(false);
			net.fortuna.ical4j.model.Calendar ical4jCalendar = ComponentFactory.buildIcal4JCalendarFromComponents(events, calendar);

			//Write the calendar to the remote file
			connection.setDoOutput(true);
			connection.setRequestMethod(ConnectionUtil.HTTP_PUT_METHOD);
			calendarOutputter.output(ical4jCalendar, connection.getOutputStream() );

			if (log.isDebugEnabled()) {
				log.debug("Flushing connection: " + connection.getURL());
			}

			connection.getOutputStream().flush();
			connection.getOutputStream().close();

			//XXX Somehow the output is not writen if the input is not read, so read 1 byte.
			if (connection.getInputStream().available() > 0) {
				connection.getInputStream().read();
			}

		} catch (URIException e) {
			log.error(e,e);
			throw new DaoException("Invalid url",e);
		} catch (IllegalArgumentException e) {
			log.error(e,e);
			throw new DaoException("Invalid url",e);
		} catch (IOException e) {
			log.error(e,e);
			throw new DaoException("Could not create connection", e);
		} catch (KeyManagementException e) {
			log.error(e,e);
			throw new DaoException("Could not connect to ssl socket" ,e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e,e);
			throw new DaoException("Could not connect to ssl socket" ,e);
		} catch (ValidationException e) {
			log.error(e,e);
			throw new DaoException("Could not validate the Calendar" ,e);
		} catch (URISyntaxException e) {
			log.error(e,e);
			throw new DaoException("Invalid url",e);
		} catch (ParseException e) {
			log.error(e,e);
			throw new DaoException("Could not parse calendar",e);
		}

	}

	/**
	 * updates an event and returns the refreshed calendar list
	 * @param updatedEvent the event to update
	 * @param calendar the calendar to wich the event belongs
	 * @return a list of events from the calendar
	 * @throws UpdateConflictException on update conflict
	 * @throws DeleteConflictException on delete conflict
	 * @throws DaoException with wrapped exception
	 */
	@SuppressWarnings("unchecked")
	public List<Event> updateEvent(Event updatedEvent, Calendar calendar) throws UpdateConflictException, DeleteConflictException, DaoException
	{
		if (log.isDebugEnabled()) log.debug("updateEvent " + calendar.getName() + ":" + updatedEvent.getSummary());

		//The conflicstatus keeps track of possible collisions
		ConflictStatus status = new ConflictStatus();

		//The calendar as in the remote file
		net.fortuna.ical4j.model.Calendar ical4jCalendar = null;
		try {
			ical4jCalendar = getIcal4JCalendarFromRemoteCalendar(calendar);
		} catch (KeyManagementException e) {
			log.error(e,e);
			throw new SSLDaoException("Could not connect to ssl socket" ,e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e,e);
			throw new SSLDaoException("Could not connect to ssl socket" ,e);
		} catch (UnknownHostException e) {
			log.error(e,e);
			throw new ConnectionDaoException("Could not create connection", e);
		} catch (IOException e) {
			log.error(e,e);
			throw new ConnectionDaoException("Could not create connection", e);
		} catch (ParserException e) {
			log.error(e, e);
			throw new IcalParserDaoException("Could not parse the ical file", e);
		} catch (Exception e) {
			throw new DaoException(e);
		}

		//The merged list of events
		List<Event> events = new ArrayList<Event>();

		//Convert the list of events
		try {
			for (Component component : (List<Component>)ical4jCalendar.getComponents()) {
				if(component.getName().equals(Component.VEVENT)) {
					if(component.getProperty(Property.UID) != null && component.getProperty(Property.UID).getValue() != null
							&& component.getProperty(Property.UID).getValue().equals(updatedEvent.getUid())) {
						// it is the event that is being modified, so it hasn't been deleted from database
						status.deletedConflict = false;

						if (component.getProperty(Property.LAST_MODIFIED) != null
								&& new Date(new net.fortuna.ical4j.model.DateTime(component.getProperty(Property.LAST_MODIFIED).getValue()).getTime()).after(updatedEvent.getLastMod())) {
							// bad news: conflict: the event has been modified in the ics also
							status.updateConflict = true;
						} else {
							updatedEvent.setLastMod(new Date());
							events.add(updatedEvent);
							continue;
						}
					}
					events.add(ComponentFactory.buildEventFromComponent(calendar,component));
				} else if(component.getName().equals(Component.VTIMEZONE)) {
					Long offSet = ComponentFactory.buildOffSetFromIcal4JTimeZone((VTimeZone)component);
					calendar.setOffSetFrom(offSet);
					calendar.setOffSetTo(offSet + 1);
				}
			}
		} catch (ParseException exception) {
			log.error(exception, exception);
			throw new IcalParserDaoException("Could not parse the ical file", exception);
		}

		if (status.deletedConflict) {
			throw new DeleteConflictException("The event has been deleted by someone else in the ICS.");
		}
		if (status.updateConflict) {
			throw new UpdateConflictException("The event has been modified by someone else in the ICS.");
		}

		return events;
	}

	/**
	 * Retrieves last modification datetime of the specified calendar
	 *
	 * @param calendar - calendar to retrieve last modification datetime
	 * @return date of last modification or null
	 *
	 * @throws DaoException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public Date lastModificationCalendar(Calendar calendar) throws DaoException, MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException
	{
		if (log.isDebugEnabled()) log.debug("lastModificationCalendar " + calendar.getName());

		Date calLastMod = null;
		if (calendar == null || calendar.getUrl() == null)
		{
			throw new DaoException("calendar or url should not be null");
		}

		// Set up authentication if needed
		if (calendar.getUsername() != null && calendar.getPassword() != null)
		{
			ConnectionUtil.setAuthentication(calendar.getUsername(), calendar.getPassword());
		}

		// Get the correct input source
		URLConnection connection = null;
		URL url = new URL(calendar.getUrl());
		if (url.getProtocol().equalsIgnoreCase(ConnectionUtil.HTTPS_PROTOCOL))
		{
			//TODO get from User Settings
			connection = ConnectionUtil.getURLConnection(url, true);
		}
		else
		{
			connection = url.openConnection();
		}
		log.debug(connection.getHeaderFields());

		Date datum = new Date(connection.getLastModified());		// GMT date time
		if (datum.getTime() > 1000000000000L) calLastMod = datum;	// > 09/09/2001
		return calLastMod;
	}

	/**
	 * Retrieves the Ical Calendar from the remote location
	 * @param calendar the calendar to retrieve
	 * @return the Ical Calendar
	 * @throws DaoException with wrapped exception
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ParserException
	 * @throws DecoderException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 */
	public net.fortuna.ical4j.model.Calendar getIcal4JCalendarFromRemoteCalendar(Calendar calendar) throws DaoException, KeyManagementException, NoSuchAlgorithmException, UnknownHostException, IOException, ParserException, IllegalBlockSizeException, BadPaddingException, DecoderException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException
	{
		if (calendar == null || calendar.getUrl() == null) {
			throw new DaoException("calendar or url should not be null");
		}
		if (log.isDebugEnabled()) log.debug("getIcal4JCalendarFromRemoteCalendar " + calendar.getName());

		//Set up authentication if needed
		if (calendar.getUsername() != null && calendar.getPassword() != null) {
			ConnectionUtil.setAuthentication(calendar.getUsername(), calendar.getPassword());
		}

		URLConnection connection = null;

		//Get the correct input source and build the calendar
		URL url = new URL(calendar.getUrl());
		if(url.getProtocol().equalsIgnoreCase(ConnectionUtil.HTTPS_PROTOCOL)) {
			//TODO get from User Settings
			connection = ConnectionUtil.getURLConnection(url, true);
		} else {
			connection = url.openConnection();
		}
		//Set up the calendar builder and parse the calendar
		CalendarBuilder calendarBuilder = new CalendarBuilder();
		return calendarBuilder.build( connection.getInputStream() );
	}

	/**
	 * @author jochem
	 * Class for conflicts while updating
	 */
	class ConflictStatus {
		public boolean updateConflict = false;
		public boolean deletedConflict = true;
		public boolean hasConflicts() {
			return updateConflict || deletedConflict;
		}
	}
}
