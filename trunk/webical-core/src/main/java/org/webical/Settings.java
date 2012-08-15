/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    $Id: $
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
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all kinds of settings 
 * @author ivo
 */
public abstract class Settings implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long settingsId = null;
	private Set<Option> options = null;

	public Settings() {
	}

	/**
	 * @return the id
	 */
	public Long getSettingsId() {
		return settingsId;
	}
	/**
	 * @param id the id to set
	 */
	public void setSettingsId(Long settingsId) {
		this.settingsId = settingsId;
	}

	/**
	 * @return the options
	 */
	public Set<Option> getOptions() {
		if (options == null) {
			options = new HashSet<Option>();
		}
		return options;
	}
	/**
	 * @param options the options to set
	 */
	public void setOptions(Set<Option> options) {
		this.options = options;
	}
}
