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

package org.webical;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Class representing one Event
 *
 * @author jochem
 * @author Mattijs Hoitink
 */

public class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	//TODO alarmc

	//Foreign Key
	private Calendar calendar = null;

	//Database id
	private Long eventId = null;

	//Optional
	private String clazz = null;
	private Date created = null;
	private String description = null;
	private Date dtStart = null;
	private String geo = null;
	private Date lastMod = null;
	private String location = null;
	private String organizer = null;
	private Integer priority = null;
	private Date dtStamp = null;
	private Integer seq = null;
	private String status = null;
	private String summary = null;
	private String transp = null;
	private String uid = null;
	private String url = null;
	private Boolean allDay = Boolean.FALSE;

	//Must not occur in the same eventprop
	private Date dtEnd = null;
	private String duration = null;

	//May occur more than once
	private Set<String> attach = null;
	private Set<String> attendee = null;
	private Set<String> categories = null;
	private Set<String> comment = null;
	private Set<String> contact = null;
	private Set<String> rStatus = null;
	private Set<String> related = null;
	private Set<String> resources = null;
	private Map<String, String> xProps = null;

	//Recurrence
	private Set<Date> rDate = null;
	private Set<String> rRule = null;
	private Set<Date> exDate = null;
	private Set<String> exRule = null;

	/** Getters & Setters */
	public Boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(Boolean allday) {
		this.allDay = allday;
	}

	public Set<String> getAttach() {
		if (attach == null) {
			attach = new HashSet<String>();
		}
		return attach;
	}
	public void setAttach(Set<String> attach) {
		this.attach = attach;
	}

	public Set<String> getAttendee() {
		if (attendee == null) {
			attendee = new HashSet<String>();
		}
		return attendee;
	}
	public void setAttendee(Set<String> attendee) {
		this.attendee = attendee;
	}

	public Set<String> getCategories() {
		if (categories == null) {
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
		if (comment == null) {
			comment = new HashSet<String>();
		}
		return comment;
	}
	public void setComment(Set<String> comments) {
		this.comment = comments;
	}

	public Set<String> getContact() {
		if (contact == null) {
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
		if (exDate == null) {
			exDate = new HashSet<Date>();
		}
		return exDate;
	}
	public void setExDate(Set<Date> exDate) {
		this.exDate = exDate;
	}

	public Set<String> getExRule() {
		if (exRule == null) {
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
		if (rDate == null) {
			rDate = new HashSet<Date>();
		}
		return rDate;
	}

	public void setrDate(Set<Date> date) {
		rDate = date;
	}
	public Set<String> getRelated() {
		if (related == null) {
			related = new HashSet<String>();
		}
		return related;
	}

	public void setRelated(Set<String> related) {
		this.related = related;
	}
	public Set<String> getResources() {
		if (resources == null) {
			resources = new HashSet<String>();
		}
		return resources;
	}

	public void setResources(Set<String> resources) {
		this.resources = resources;
	}

	public Set<String> getrRule() {
		if (rRule == null) {
			rRule = new HashSet<String>();
		}
		return rRule;
	}
	public void setrRule(Set<String> rule) {
		rRule = rule;
	}
	public Set<String> getrStatus() {
		if (rStatus == null) {
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
	public void setUid(String wuid) {
		this.uid = wuid;
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
		if (xProps == null || key ==null) {
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
		result = PRIME * result + ((getUid() == null) ? 0 : getUid().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if (obj == null) return false;

		if (getClass() != obj.getClass()) return false;

		final Event other = (Event) obj;
		if (getUid() == null) {
			if (other.getUid() != null) return false;
		} else if (!getUid().equals(other.getUid())) return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder sbts = new StringBuilder();
		if (getUid() != null)
		{
			sbts.append("UID: ");
			sbts.append(getUid());
		}
		if (getSummary() != null)
		{
			sbts.append(", SUMMARY: ");
			sbts.append(getSummary());
		}
		if (getCalendar() != null)
		{
			sbts.append(", Calendar: ");
			sbts.append(getCalendar().getName());
		}
		return sbts.toString();
	}

	public Event copyEvent() {
		Event event = new Event();
		event.setSummary(this.getSummary());
		event.setDtStart(this.getDtStart());
		event.setDtEnd(this.getDtEnd());
		event.setAllDay(this.isAllDay());
		event.setLocation(this.getLocation());
		event.setDescription(this.getDescription());

		event.setrRule(this.getrRule());
		event.setExDate(this.getExDate());
		event.setCalendar(this.getCalendar());

		return event;
	}
}
