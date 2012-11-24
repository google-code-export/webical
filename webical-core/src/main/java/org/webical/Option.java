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
 * An option to be used in {@link Settings}
 * @author ivo
 *
 */
public class Option implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long optionId = null;
	private Settings settings = null;
	private String name = null;
	private Serializable value = null;

	/**
	 * Last update time of this record
	 */
	private Date lastUpdateTime = null;

	public Option() {
	}

	/**
	 * @param settings
	 * @param name
	 * @param value
	 */
	public Option(Settings settings, String name, Serializable value) {
		super();
		this.settings = settings;
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the optionId
	 */
	public Long getOptionId() {
		return optionId;
	}
	/**
	 * @param optionId - the optionId to set
	 */
	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}

	/**
	 * @return the settings
	 */
	public Settings getSettings() {
		return settings;
	}
	/**
	 * @param settings - the settings to set
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name - the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public Serializable getValue() {
		return value;
	}
	/**
	 * @param value - the value to set
	 */
	public void setValue(Serializable value) {
		this.value = value;
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
}
