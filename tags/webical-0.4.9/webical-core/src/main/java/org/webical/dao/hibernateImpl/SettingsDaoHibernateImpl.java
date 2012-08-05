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

package org.webical.dao.hibernateImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.Restrictions;
import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.User;
import org.webical.UserPluginSettings;
import org.webical.UserSettings;
import org.webical.dao.DaoException;
import org.webical.dao.SettingsDao;
import org.webical.dao.annotation.Transaction;

/**
 * Hibernate implementation for the {@link SettingsDao}
 * @author ivo
 */
public class SettingsDaoHibernateImpl extends BaseHibernateImpl implements SettingsDao {
	private static final String PLUGIN_CLASS = "pluginClass";
	private static Log log = LogFactory.getLog(SettingsDaoHibernateImpl.class);

	/* (non-Javadoc)
	 * @see org.webical.dao.SettingsDao#getPluginSettings(java.lang.String)
	 */
	@Transaction(readOnly=true)
	public PluginSettings getPluginSettings(String pluginClass) throws DaoException {
		if(StringUtils.isEmpty(pluginClass)) {
			return null;
		}

		try {
			Criteria criteria = getSession().createCriteria(PluginSettings.class);
			criteria.add(Restrictions.eq(PLUGIN_CLASS, pluginClass));
			return (PluginSettings) criteria.uniqueResult();
		} catch (Exception exception) {
			log.error("Could not retrieve settings for plugin: " + pluginClass, exception);
			throw new DaoException("Could not retrieve settings for plugin: " + pluginClass, exception);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.dao.SettingsDao#getUserPluginSettings(java.lang.String, org.webical.User)
	 */
	@Transaction(readOnly=true)
	public UserSettings getUserSettings(User user) throws DaoException {
		if (user == null) {
			return null;
		}

		try {
			Criteria criteria = getSession().createCriteria(UserSettings.class);
			criteria.add(Restrictions.eq("user", user));
			return (UserSettings) criteria.uniqueResult();
		} catch (Exception exception) {
			log.error("Could not retreive settings for  user: " + user, exception);
			throw new DaoException("Could not retrieve settings for user: " + user, exception);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.dao.SettingsDao#getUserPluginSettings(java.lang.String, org.webical.User)
	 */
	@Transaction(readOnly=true)
	public UserPluginSettings getUserPluginSettings(String pluginClass, User user) throws DaoException {
		if (StringUtils.isEmpty(pluginClass) || user == null) {
			return null;
		}

		try {
			Criteria criteria = getSession().createCriteria(UserPluginSettings.class);
			criteria.add(Restrictions.eq(PLUGIN_CLASS, pluginClass));
			criteria.add(Restrictions.eq("user", user));
			return (UserPluginSettings) criteria.uniqueResult();
		} catch (Exception exception) {
			log.error("Could not retreive settings for plugin: " + pluginClass + " and user: " + user, exception);
			throw new DaoException("Could not retrieve settings for plugin: " + pluginClass + " and user: " + user, exception);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.dao.SettingsDao#removeSettings(org.webical.Settings)
	 */
	@Transaction(readOnly=false)
	public void removeSettings(Settings settings) throws DaoException {
		if (settings == null) {
			return;
		}

		try {
			getSession().lock(settings, LockMode.NONE);
			delete(settings);
		} catch (Exception exception) {
			log.error("Could not delete settings: " + settings, exception);
			throw new DaoException("Could not delete settings: " + settings, exception);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.dao.SettingsDao#storePluginSettings(org.webical.PluginSettings)
	 */
	@Transaction(readOnly=false)
	public void storePluginSettings(PluginSettings pluginSettings) throws DaoException {
		if (pluginSettings == null) {
			return;
		}

		try {
			saveOrUpdate(pluginSettings);
		} catch (Exception exception) {
			log.error("Could not store pluginSettings: " + pluginSettings, exception);
			throw new DaoException("Could not store pluginSettings: " + pluginSettings, exception);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.dao.SettingsDao#storeUserPluginSettings(org.webical.UserPluginSettings)
	 */
	@Transaction(readOnly=false)
	public void storeUserSettings(UserSettings userSettings) throws DaoException {
		if (userSettings == null) {
			return;
		}

		try {
			saveOrUpdate(userSettings);
		} catch (Exception exception) {
			log.error("Could not store userSettings: " + userSettings, exception);
			throw new DaoException("Could not store userPluginSettings: " + userSettings, exception);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.dao.SettingsDao#storeUserPluginSettings(org.webical.UserPluginSettings)
	 */
	@Transaction(readOnly=false)
	public void storeUserPluginSettings(UserPluginSettings userPluginSettings) throws DaoException {
		if (userPluginSettings == null) {
			return;
		}

		try {
			saveOrUpdate(userPluginSettings);
		} catch (Exception exception) {
			log.error("Could not store userPluginSettings: " + userPluginSettings, exception);
			throw new DaoException("Could not store userPluginSettings: " + userPluginSettings, exception);
		}
	}
}
