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

package org.webical.web.component.event;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import org.webical.Event;
import org.webical.ical.Recurrence;
import org.webical.ical.RecurrenceUtil;
import org.webical.manager.WebicalException;
import org.webical.util.CalendarUtils;

/**
 * Wraps the event for use in {@see EventForm}. This wrapper uses {@see Event} and 
 * {@see Recurrence} to provide the form with the correct information.
 * Storage of the Event and Recurrence information is also done through this 
 * wrapper. Storage of the Event instance is done by the {@see EventManager}.
 * 
 * @author Mattijs Hoitink
 *
 */
public class EventWrapper implements Serializable {
	private static final long serialVersionUID = 1L;

	private Event event;
	private Recurrence recurrence = null;

	public EventWrapper(Event parentEvent) {
		this.event = parentEvent;
		try {
			this.recurrence = RecurrenceUtil.getRecurrenceFromRecurrenceRuleSet(this.event.getrRule());
		} catch (WebicalException e) {

		}
		if (this.recurrence == null) {
			this.recurrence = new Recurrence();
		}
	}

	/* Custom & Overridden methods */
	public void setEventStartDate(Date eventStartDate) {
		// Calendar that holds the new start date
		GregorianCalendar newStartDateCalendar = new GregorianCalendar();
		newStartDateCalendar.setTime(eventStartDate);

		// Calendar that holds the current start date
		GregorianCalendar startDateCalendar = new GregorianCalendar();
		if (event.getDtStart() != null) {
			startDateCalendar.setTime(event.getDtStart());

			startDateCalendar.set(GregorianCalendar.DAY_OF_YEAR, newStartDateCalendar.get(GregorianCalendar.DAY_OF_YEAR));
			startDateCalendar.set(GregorianCalendar.MONTH, newStartDateCalendar.get(GregorianCalendar.MONTH));
			startDateCalendar.set(GregorianCalendar.YEAR, newStartDateCalendar.get(GregorianCalendar.YEAR));
		}
		event.setDtStart(startDateCalendar.getTime());
	}

	public Date getEventStartDate() {
		return event.getDtStart();
	}

	public void setEventEndDate(Date eventEndDate) {
		// Calendar that holds the new start date
		GregorianCalendar newEndDateCalendar = new GregorianCalendar();
		newEndDateCalendar.setTime(eventEndDate);

		// Calendar that holds the current start date
		GregorianCalendar endDateCalendar = new GregorianCalendar();
		if (event.getDtEnd() != null) {
			endDateCalendar.setTime(event.getDtEnd());

			endDateCalendar.set(GregorianCalendar.DAY_OF_YEAR, newEndDateCalendar.get(GregorianCalendar.DAY_OF_YEAR));
			endDateCalendar.set(GregorianCalendar.MONTH, newEndDateCalendar.get(GregorianCalendar.MONTH));
			endDateCalendar.set(GregorianCalendar.YEAR, newEndDateCalendar.get(GregorianCalendar.YEAR));
		}

		event.setDtEnd(endDateCalendar.getTime());
	}

	public Date getEventEndDate() {
		return event.getDtEnd();
	}

	public void setEventStartTime(Date eventStartTime) {
		// Calendar that holds the new start time
		GregorianCalendar newStartTimeCalendar = new GregorianCalendar();
		newStartTimeCalendar.setTime(eventStartTime);

		// Calendar that holds the current start time
		GregorianCalendar startTimeCalendar = new GregorianCalendar();

		if (event.getDtStart() != null) {
			startTimeCalendar.setTime(event.getDtStart());

			startTimeCalendar.set(GregorianCalendar.HOUR_OF_DAY, newStartTimeCalendar.get(GregorianCalendar.HOUR_OF_DAY));
			startTimeCalendar.set(GregorianCalendar.MINUTE, newStartTimeCalendar.get(GregorianCalendar.MINUTE));
		}

		event.setDtStart(startTimeCalendar.getTime());
	}

	public Date getEventStartTime() {
		return event.getDtStart();
	}

	public void setEventEndTime(Date eventEndTime) {
		// Calendar that holds the new end time
		GregorianCalendar newEndTimeCalendar = new GregorianCalendar();
		newEndTimeCalendar.setTime(eventEndTime);

		// Calendar that holds the current end time
		GregorianCalendar endTimeCalendar = new GregorianCalendar();
		if (event.getDtEnd() != null) {
			endTimeCalendar.setTime(event.getDtEnd());

			endTimeCalendar.set(GregorianCalendar.HOUR_OF_DAY, newEndTimeCalendar.get(GregorianCalendar.HOUR_OF_DAY));
			endTimeCalendar.set(GregorianCalendar.MINUTE, newEndTimeCalendar.get(GregorianCalendar.MINUTE));
		}

		event.setDtEnd(endTimeCalendar.getTime());
	}

	public Date getEventEndTime() {
		return event.getDtEnd();
	}

	public void resetAllDay() {
		java.util.Calendar currentDateCalendar = GregorianCalendar.getInstance();
		java.util.Calendar formDateCalendar = GregorianCalendar.getInstance();

		formDateCalendar.setTime(getDtStart());
		formDateCalendar.set(GregorianCalendar.HOUR_OF_DAY, currentDateCalendar.get(GregorianCalendar.HOUR_OF_DAY));
		formDateCalendar.set(GregorianCalendar.MINUTE, currentDateCalendar.get(GregorianCalendar.MINUTE));

		//Fill the start and end times with proper values
		setDtStart(formDateCalendar.getTime());
		formDateCalendar.set(GregorianCalendar.HOUR_OF_DAY, formDateCalendar.get(GregorianCalendar.HOUR_OF_DAY) + 1);
		setDtEnd(formDateCalendar.getTime());
		event.setAllDay(false);
	}

	public void setAllDay() {
		setDtStart(CalendarUtils.getStartOfDay(getDtStart()));
		setDtEnd(CalendarUtils.getStartOfDay(getDtEnd()));
		event.setAllDay(true);
	}

	public Event getEvent() {
		return event;
	}

	public void storeRecurrence() {
		RecurrenceUtil.setRecurrenceRule(this.event, this.recurrence);
	}

	public void clearRecurrence() {
		this.recurrence = null;
		RecurrenceUtil.clearRecurrence(this.event);
	}

	public void setRecurrence(Recurrence recurrence) {
		this.recurrence = recurrence;
	}

	/* Delegate Methods to Recurrence */
	public Integer getFrequency(){
		return recurrence.getFrequency();
	}

	public void setFrequency(Integer frequency) {
		recurrence.setFrequency(frequency);
	}

	public Integer getInterval() {
		return recurrence.getInterval();
	}

	public void setInterval(Integer interval) {
		recurrence.setInterval(interval);
	}

	public Integer getCount() {
		return recurrence.getCount();
	}

	public void setCount(Integer count) {
		recurrence.setCount(count);
	}

	public Date getUntil() {
		return recurrence.getEndDay();
	}

	public void setUntil(Date until) {
		recurrence.setEndDay(until);
	}

	/* Delegate Methods to Event*/
	public void addXProp(String key, String value) {
		event.addXProp(key, value);
	}

	public Event copyEvent() {
		return event.copyEvent();
	}

	public boolean equals(Object obj) {
		return event.equals(obj);
	}

	public Set<String> getAttach() {
		return event.getAttach();
	}

	public Set<String> getAttendee() {
		return event.getAttendee();
	}

	public org.webical.Calendar getCalendar() {
		return event.getCalendar();
	}

	public Set<String> getCategories() {
		return event.getCategories();
	}

	public String getClazz() {
		return event.getClazz();
	}

	public Set<String> getComment() {
		return event.getComment();
	}

	public Set<String> getContact() {
		return event.getContact();
	}

	public Date getCreated() {
		return event.getCreated();
	}

	public String getDescription() {
		return event.getDescription();
	}

	public Date getDtEnd() {
		return event.getDtEnd();
	}

	public Date getDtStamp() {
		return event.getDtStamp();
	}

	public Date getDtStart() {
		return event.getDtStart();
	}

	public String getDuration() {
		return event.getDuration();
	}

	public Long getEventId() {
		return event.getEventId();
	}

	public Set<Date> getExDate() {
		return event.getExDate();
	}

	public Set<String> getExRule() {
		return event.getExRule();
	}

	public String getGeo() {
		return event.getGeo();
	}

	public Date getLastMod() {
		return event.getLastMod();
	}

	public String getLocation() {
		return event.getLocation();
	}

	public String getOrganizer() {
		return event.getOrganizer();
	}

	public Integer getPriority() {
		return event.getPriority();
	}

	public Set<Date> getrDate() {
		return event.getrDate();
	}

	public Set<String> getRelated() {
		return event.getRelated();
	}

	public Set<String> getResources() {
		return event.getResources();
	}

	public Set<String> getrRule() {
		return event.getrRule();
	}

	public Set<String> getrStatus() {
		return event.getrStatus();
	}

	public Integer getSeq() {
		return event.getSeq();
	}

	public String getStatus() {
		return event.getStatus();
	}

	public String getSummary() {
		return event.getSummary();
	}

	public String getTransp() {
		return event.getTransp();
	}

	public String getUid() {
		return event.getUid();
	}

	public String getUrl() {
		return event.getUrl();
	}

	public String getXProp(String key) {
		return event.getXProp(key);
	}

	public Map<String, String> getxProps() {
		return event.getxProps();
	}

	public int hashCode() {
		return event.hashCode();
	}

	public boolean isAllDay() {
		return event.isAllDay();
	}

	public void setAttach(Set<String> attach) {
		event.setAttach(attach);
	}

	public void setAttendee(Set<String> attendee) {
		event.setAttendee(attendee);
	}

	public void setCalendar(org.webical.Calendar calendar) {
		event.setCalendar(calendar);
	}

	public void setCategories(Set<String> categories) {
		event.setCategories(categories);
	}

	public void setClazz(String clazz) {
		event.setClazz(clazz);
	}

	public void setComment(Set<String> comment) {
		event.setComment(comment);
	}

	public void setContact(Set<String> contact) {
		event.setContact(contact);
	}

	public void setCreated(Date created) {
		event.setCreated(created);
	}

	public void setDescription(String description) {
		event.setDescription(description);
	}

	public void setDtEnd(Date dtEnd) {
		event.setDtEnd(dtEnd);
	}

	public void setDtStamp(Date dtStamp) {
		event.setDtStamp(dtStamp);
	}

	public void setDtStart(Date dtStart) {
		event.setDtStart(dtStart);
	}

	public void setDuration(String duration) {
		event.setDuration(duration);
	}

	public void setEventId(Long eventId) {
		event.setEventId(eventId);
	}

	public void setExDate(Set<Date> exDate) {
		event.setExDate(exDate);
	}

	public void setExRule(Set<String> exRule) {
		event.setExRule(exRule);
	}

	public void setGeo(String geo) {
		event.setGeo(geo);
	}

	public void setLastMod(Date lastMod) {
		event.setLastMod(lastMod);
	}

	public void setLocation(String location) {
		event.setLocation(location);
	}

	public void setOrganizer(String organizer) {
		event.setOrganizer(organizer);
	}

	public void setPriority(Integer priority) {
		event.setPriority(priority);
	}

	public void setrDate(Set<Date> date) {
		event.setrDate(date);
	}

	public void setRelated(Set<String> related) {
		event.setRelated(related);
	}

	public void setResources(Set<String> resources) {
		event.setResources(resources);
	}

	public void setrRule(Set<String> rule) {
		event.setrRule(rule);
	}

	public void setrStatus(Set<String> status) {
		event.setrStatus(status);
	}

	public void setSeq(Integer seq) {
		event.setSeq(seq);
	}

	public void setStatus(String status) {
		event.setStatus(status);
	}

	public void setSummary(String summary) {
		event.setSummary(summary);
	}

	public void setTransp(String transp) {
		event.setTransp(transp);
	}

	public void setUid(String uid) {
		event.setUid(uid);
	}

	public void setUrl(String url) {
		event.setUrl(url);
	}

	public void setxProps(Map<String, String> props) {
		event.setxProps(props);
	}

	public String toString() {
		return event.toString();
	}
}
