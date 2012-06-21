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

package org.webical.web.component.calendar.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.Event;
import org.webical.comparator.EventStartTimeComparator;
import org.webical.util.CalendarUtils;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;

/**
 * Base Model which gets the Events from the DAO for the given date range.
 *
 * @author Mattijs Hoitink
 */
public class EventsModel extends LoadableDetachableModel {
	private static final long serialVersionUID = 1L;

	/**
	 * Start Date of the date range.
	 */
	private Date startDate;

	/**
	 * End Date of the date range.
	 */
	private Date endDate;

	/**
	 * Previous start Date of the date range, used for caching.
	 */
	private long previousStartDate = 0;

	/**
	 * Previous end Date of the date range, used for caching.
	 */
	private long previousEndDate = 0;

	/**
	 * EventManager used to collect the list of Events from the DAO.
	 */
	@SpringBean(name="eventManager")
	private EventManager eventManager;

	/**
	 * Constructor
	 * @param startDate Start Date for the date range
	 * @param endDate End Date for the date range
	 */
	public EventsModel(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Gets the events for the date range
	 * @see org.apache.wicket.model.LoadableDetachableModel#load()
	 */
	@Override
	public Object load() {
		//Inject this Session
		InjectorHolder.getInjector().inject(this);

		// Get a list of Events from the DAO for the specified range
		try {
			List<Event> eventsList = eventManager.getEventsForPeriod(WebicalSession.getWebicalSession().getUser(), startDate, endDate);
			if (eventsList != null && eventsList.size() > 0) {
				Collections.sort(eventsList, new EventStartTimeComparator(EventStartTimeComparator.CompareMode.FULL_DATE));
			}

			return eventsList;
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not load events for user " + WebicalSession.getWebicalSession().getUser().getUserId() + " in period " + startDate + " - " + endDate, e);
		}
	}

	/**
	 * Sets parameters to cache the events from the load() method
	 * @see org.apache.wicket.model.LoadableDetachableModel#getObject()
	 */
	@Override
	public Object getObject() {
		// If the previous date range is different from the current date range, clear the cache by calling detach()
		if (previousStartDate != startDate.getTime() || previousEndDate != endDate.getTime()) {
			detach();
		}
		// Set previous range to the current
		previousStartDate = startDate.getTime();
		previousEndDate = endDate.getTime();

		// Call super.getObject() so load() is called again to get the Events from the DAO
		return super.getObject();
	}

	/**
	 * Get the start of the date range
	 * @return Date The start of the date range for this model
	 */
	public Date getStartDate() {
		//Make sure the cache is up to date
		getObject();
		return startDate;
	}

	/**
	 * Get the end of the date range
	 * @return Date The end of the date range for this model
	 */
	public Date getEndDate() {
		//Make sure the cache is up to date
		getObject();
		return endDate;
	}

	/**
	 * Get the number of days in the model date range
	 * @return the number of days in the date range
	 */
	public int getNumberOfDays() {
		return CalendarUtils.getDifferenceInDays(getStartDate(), getEndDate());
	}

	/**
	 * Returns the EventsModel used by this EventsModel
	 * @return this EventsModel
	 */
	public EventsModel getEventsModel() {
		return this;
	}
}
