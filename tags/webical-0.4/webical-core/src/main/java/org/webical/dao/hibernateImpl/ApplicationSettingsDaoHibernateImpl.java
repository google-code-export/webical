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

package org.webical.dao.hibernateImpl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.ApplicationSettings;
import org.webical.dao.ApplicationSettingsDao;
import org.webical.dao.DaoException;
import org.webical.dao.annotation.Transaction;

/**
 * Default hibernate implementation
 * @author ivo
 *
 */
public class ApplicationSettingsDaoHibernateImpl extends BaseHibernateImpl implements ApplicationSettingsDao {
	private static Log log = LogFactory.getLog(ApplicationSettingsDaoHibernateImpl.class);

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.ApplicationSettingsDao#getApplicationSettings()
	 */
	@Transaction
	public ApplicationSettings getApplicationSettings() throws DaoException {
		List results = null;
		try {
			results = loadAll(ApplicationSettings.class);
		} catch (Exception e) {
			log.error("Could not load application settings", e);
			throw new DaoException("Could not load application settings", e);
		}
		
		if(results != null && results.size() > 0) {
			if(results.size() > 1) {
				log.warn("Multiple ApplicationSettings found returning the first entry");
			}
			return (ApplicationSettings)results.get(0);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.ApplicationSettingsDao#storeApplicationSettings(org.webical.ApplicationSettings)
	 */
	@Transaction
	public void storeApplicationSettings(ApplicationSettings applicationSettings) throws DaoException {
		if(applicationSettings == null) {
			log.error("Cannot store empty configuration");
			throw new DaoException("Cannot store empty configuration"); 
		}
		
		try {
			saveOrUpdate(applicationSettings);
		} catch (Exception e) {
			log.error("Could not store application settings");
			throw new DaoException("Could not store application settings", e);
		}
	}

}
