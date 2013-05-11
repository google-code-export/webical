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

import javax.persistence.Version;

/**
 * User
 * @author ivo
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userId = null;
	private String firstName = null;
	private String lastNamePrefix = null;
	private String lastName = null;
	private Date birthDate = null;

	/**
	 * Last update time of this record
	 */
	private Date lastUpdateTime = null;

	/** Getters and setters */
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastNamePrefix() {
		return lastNamePrefix;
	}
	public void setLastNamePrefix(String lastNamePrefix) {
		this.lastNamePrefix = lastNamePrefix;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return last update time of this record
	 */
	@Version
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	/**
	 * @param lastUpdateTime - last update time of this record
	 */
	@Version
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String toString() {
		if (getUserId() == null) return this.getClass().getCanonicalName();
		return getUserId();
	}
}
