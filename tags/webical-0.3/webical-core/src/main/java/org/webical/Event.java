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

package org.webical;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.webical.util.CalendarUtils;


/**
 * Class representing one Event
 *
 * @author jochem
 */

public class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	//TODO alarmc

	//Foreign Key
	private Calendar calendar;

	//Database id
	private Long eventId;

	//Optional
	private String clazz;
	private Date created;
	private String description;
	private Date dtStart;
	private String geo;
	private Date lastMod;
	private String location;
	private String organizer;
	private Integer priority;
	private Date dtStamp;
	private Integer seq;
	private String status;
	private String summary;
	private String transp;
	private String uid;
	private String url;

	//Must not occur in the same eventprop
	private Date dtEnd;
	private String duration;

	//May occur more than once
	private Set<String> attach;
	private Set<String> attendee;
	private Set<String> categories;
	private Set<String> comment;
	private Set<String> contact;
	private Set<String> rStatus;
	private Set<String> related;
	private Set<String> resources;
	private Map<String, String> xProps;

	//Recurrence
	private Set<Date> rDate;
	private Set<String> rRule;
	private Set<Date> exDate;
	private Set<String> exRule;

	/**
	 * check if this event is an all day event
	 * @return boolean
	 * @author Mattijs Hoitink
	 */
	public boolean isAllDay(){
		if(this.dtStart == null || this.dtEnd == null){
			return false;
		} else if(!CalendarUtils.getDateWithoutMs(dtStart).equals(CalendarUtils.getStartOfDay(this.dtStart)) || !CalendarUtils.getDateWithoutMs(dtEnd).equals(CalendarUtils.getStartOfDay(this.dtEnd))){
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Set the event to an all day event
	 * @author Mattijs Hoitink
	 */
	public void setAllDay() {
		this.setDtStart(CalendarUtils.getStartOfDay(this.getDtStart()));
		this.setDtEnd(CalendarUtils.getStartOfDay(this.getDtEnd()));
	}

	/**
	 * Reset the event to a non all day event
	 * @author Mattijs Hoitink
	 */
	public void resetAllDay() {
		java.util.Calendar currentDateCalendar = GregorianCalendar.getInstance();
		java.util.Calendar formDateCalendar = GregorianCalendar.getInstance();

		formDateCalendar.setTime(this.getDtStart());
		formDateCalendar.set(GregorianCalendar.HOUR_OF_DAY, currentDateCalendar.get(GregorianCalendar.HOUR_OF_DAY));
		formDateCalendar.set(GregorianCalendar.MINUTE, currentDateCalendar.get(GregorianCalendar.MINUTE));

		//Fill the start and end times with proper values
		this.setDtStart(formDateCalendar.getTime());
		formDateCalendar.set(GregorianCalendar.HOUR_OF_DAY, formDateCalendar.get(GregorianCalendar.HOUR_OF_DAY) + 1);
		this.setDtEnd(formDateCalendar.getTime());
	}

	/**
	 * Returns true if this is a recurring Event
	 * @return boolean true if event is recurring
	 */
	public boolean isRecurring() {
		// TODO mattijs: check with ivo if this is correct
		return this.getrRule().size() >= 1;
	}

	/** Getters & Setters */
	public Set<String> getAttach() {
		if(attach == null){
			attach = new HashSet<String>();
		}

		return attach;
	}

	public void setAttach(Set<String> attach) {
		this.attach = attach;
	}

	public Set<String> getAttendee() {
		if(attendee == null){
			attendee = new HashSet<String>();
		}

		return attendee;
	}

	public void setAttendee(Set<String> attendee) {
		this.attendee = attendee;
	}

	public Set<String> getCategories() {
		if(categories == null){
			categories = new HashSet<String>();
		}

		return categories;
	}

	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Set<String> getComment() {
		if(comment == null){
			comment = new HashSet<String>();
		}

		return comment;
	}

	public void setComment(Set<String> comment) {
		this.comment = comment;
	}

	public Set<String> getContact() {
		if(contact == null){
			contact = new HashSet<String>();
		}

		return contact;
	}

	public void setContact(Set<String> contact) {
		this.contact = contact;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDtEnd() {
		return dtEnd;
	}

	public void setDtEnd(Date dtEnd) {
		this.dtEnd = dtEnd;
	}

	public Date getDtStamp() {
		return dtStamp;
	}

	public void setDtStamp(Date dtStamp) {
		this.dtStamp = dtStamp;
	}

	public Date getDtStart() {
		return dtStart;
	}

	public void setDtStart(Date dtStart) {
		this.dtStart = dtStart;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Set<Date> getExDate() {
		if(exDate == null){
			exDate = new HashSet<Date>();
		}

		return exDate;
	}

	public void setExDate(Set<Date> exDate) {
		this.exDate = exDate;
	}

	public Set<String> getExRule() {
		if(exRule == null){
			exRule = new HashSet<String>();
		}

		return exRule;
	}

	public void setExRule(Set<String> exRule) {
		this.exRule = exRule;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public Date getLastMod() {
		return lastMod;
	}

	public void setLastMod(Date lastMod) {
		this.lastMod = lastMod;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Set<Date> getrDate() {
		if(rDate == null){
			rDate = new HashSet<Date>();
		}

		return rDate;
	}

	public void setrDate(Set<Date> date) {
		rDate = date;
	}

	public Set<String> getRelated() {
		if(related == null){
			related = new HashSet<String>();
		}
		return related;
	}

	public void setRelated(Set<String> related) {
		this.related = related;
	}

	public Set<String> getResources() {
		if(resources == null){
			resources = new HashSet<String>();
		}

		return resources;
	}

	public void setResources(Set<String> resources) {
		this.resources = resources;
	}

	public Set<String> getrRule() {
		if(rRule == null){
			rRule = new HashSet<String>();
		}

		return rRule;
	}

	public void setrRule(Set<String> rule) {
		rRule = rule;
	}

	public Set<String> getrStatus() {
		if(rStatus == null){
			rStatus = new HashSet<String>();
		}
		return rStatus;
	}

	public void setrStatus(Set<String> status) {
		rStatus = status;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTransp() {
		return transp;
	}

	public void setTransp(String transp) {
		this.transp = transp;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getxProps() {
		return xProps;
	}

	public void setxProps(Map<String, String> props) {
		xProps = props;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public String getXProp(String key) {
		if(xProps == null || key ==null) {
			return null;
		}
		return xProps.get(key);
	}

	public void addXProp(String key, String value) {
		if(xProps == null) {
			xProps = new HashMap<String, String>();
		}
		xProps.put(key, value);
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Event other = (Event) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UID: " + uid + ", SUMMARY: " + summary;
	}

	public Event copyEvent() {
		Event event = new Event();
		event.setSummary(this.getSummary());
		event.setDtStart(this.getDtStart());
		event.setDtEnd(this.getDtEnd());
		event.setDescription(this.getDescription());
		event.setLocation(this.getLocation());

		event.setrRule(this.getrRule());
		event.setExDate(this.getExDate());
		event.setCalendar(this.getCalendar());

		return event;
	}

}