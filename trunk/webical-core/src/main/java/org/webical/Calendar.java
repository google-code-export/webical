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

/**
 * Calendar
 * @author jochem
 *
 */

public class Calendar implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long calendarId;
	private String name;
	private String type;
	private String url;
	private String username;
	private String password;
	private Boolean visible;

	//timezone
	private Long offSetFrom;
	private Long offSetTo;

	private User user;

	private Long lastRefreshTimeStamp = null;

	/*Getters & Setters*/
	public Long getOffSetFrom() {
		return offSetFrom;
	}
	public void setOffSetFrom(Long offSetFrom) {
		this.offSetFrom = offSetFrom;
	}
	public Long getOffSetTo() {
		return offSetTo;
	}
	public void setOffSetTo(Long offSetTo) {
		this.offSetTo = offSetTo;
	}
	public Long getLastRefreshTimeStamp() {
		return lastRefreshTimeStamp;
	}
	public void setLastRefreshTimeStamp(Long lastRefreshTimeStamp) {
		this.lastRefreshTimeStamp = lastRefreshTimeStamp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Boolean getVisible() {
		if(visible == null) {
			visible = Boolean.FALSE;
		}
		return visible;
	}
	public void setVisible(Boolean selected) {
		this.visible = selected;
	}
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((calendarId == null) ? 0 : calendarId.hashCode());
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
		final Calendar other = (Calendar) obj;
		if (calendarId == null) {
			if (other.calendarId != null)
				return false;
		} else if (!calendarId.equals(other.calendarId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return this.getName();
	}
}