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

package org.webical.manager.impl;

import org.webical.ApplicationSettings;
import org.webical.dao.ApplicationSettingsDao;
import org.webical.dao.DaoException;
import org.webical.manager.WebicalException;

/**
 * @author ivo
 *
 */
public class ApplicationSettingsManagerImpl implements
		org.webical.manager.ApplicationSettingsManager {

	/**
	 * The ApplicationSettingsDao implementation (Set by Spring)
	 */
	private ApplicationSettingsDao applicationSettingsDao;

	/* (non-Javadoc)
	 * @see org.webical.manager.ApplicationSettingsManager#getApplicationSettings()
	 */
	public ApplicationSettings getApplicationSettings() throws WebicalException {
		try {
			return applicationSettingsDao.getApplicationSettings();
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.ApplicationSettingsManager#storeApplicationSettings(org.webical.ApplicationSettings)
	 */
	public void storeApplicationSettings(ApplicationSettings applicationSettings)
			throws WebicalException {

		if(applicationSettings == null) {
			return;
		}

		try {
			applicationSettingsDao.storeApplicationSettings(applicationSettings);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}
	//////////////////////////
	/// Setters for Spring ///
	//////////////////////////

	/**
	 * @param applicationSettingsDao the applicationSettingsDao to set
	 */
	public void setApplicationSettingsDao(
			ApplicationSettingsDao applicationSettingsDao) {
		this.applicationSettingsDao = applicationSettingsDao;
	}

}
