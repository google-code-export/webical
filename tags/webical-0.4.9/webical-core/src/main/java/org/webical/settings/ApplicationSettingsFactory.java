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

package org.webical.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.ApplicationSettings;
import org.webical.manager.ApplicationSettingsManager;
import org.webical.manager.WebicalException;

/**
 * Factry for easy access to the ApplicationSettings
 * @author ivo
 *
 */
public class ApplicationSettingsFactory implements InitializingBean {
	private static Log log = LogFactory.getLog(ApplicationSettingsFactory.class);

	/** The only instance */
	private static ApplicationSettingsFactory instance;

	/** The applicationSettingsManager is set by Spring */
	private ApplicationSettingsManager applicationSettingsManager;

	/**
	 * Protected constructor
	 */
	protected ApplicationSettingsFactory() {

	}

	/**
	 * @return the singleton instance
	 */
	public static ApplicationSettingsFactory getInstance() {
		if(instance == null) {
			log.debug("Creating ApplicationSettingsFactory singleton instance");
			instance = new ApplicationSettingsFactory();
		}
		return instance;
	}

	/**
	 * @return the current application settigns
	 * @throws WebicalException
	 */
	public ApplicationSettings getApplicationSettings() throws ApplicationSettingsException {
		try {
			return applicationSettingsManager.getApplicationSettings();
		} catch (WebicalException e) {
			throw new ApplicationSettingsException(e);
		}
	}

	/**
	 * @param applicationSettingsManager the applicationSettingsManager to set
	 */
	public void setApplicationSettingsManager(
			ApplicationSettingsManager applicationSettingsManager) {
		this.applicationSettingsManager = applicationSettingsManager;
	}

	public void afterPropertiesSet() throws Exception {
		if(applicationSettingsManager == null) {
			log.fatal(ApplicationSettingsFactory.class + " needs a " + ApplicationSettingsManager.class);
			throw new ExceptionInInitializerError(ApplicationSettingsFactory.class + " needs a " + ApplicationSettingsManager.class);
		}

		instance = this;
	}

}
