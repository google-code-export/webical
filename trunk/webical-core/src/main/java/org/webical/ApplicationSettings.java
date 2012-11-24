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
import java.util.Set;
import java.util.HashSet;

import javax.persistence.Version;

/**
 * Holder class for the application wide settings
 * @author ivo
 *
 */
public class ApplicationSettings implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The id used in the database 
	 */
	private Long applicationSettingsId;

	/**
	 * A list of paths to include when searching for additional resources (html/css/etc)
	 */
	private Set<String> resourcePaths;

    /**
     * The title to use on the pages
     */
	private String customPageTitle;

    /**
     * A list of paths to look for plugin packages
     */
	private Set<String> pluginPaths;

    /**
     * The location of a writeable directory to extract the plugin packages
     */
	private String pluginWorkPath;

    /**
     * The file extension used for the plugins
     */
	private String pluginPackageExtension;

    /**
     * The timeout between calendar refreshes 
	 */
	private int calendarRefreshTimeMs;

	/**
	 * Determines if the plugin directory is to be emptied on context shutdown
	 */
	private boolean pluginCleanupEnabled;

	/**
	 * username to log into the configuration page
	 */
	private String configurationUsername;

	/**
	 * password to log into the configuration page
	 */
	private String configurationPassword;

	/**
	 * Last update time of this record
	 */
	private Date lastUpdateTime;

	/**
	 * @return the calendarRefreshTimeMs
	 */
	public int getCalendarRefreshTimeMs() {
		return calendarRefreshTimeMs;
	}
	/**
	 * @param calendarRefreshTimeMs the calendarRefreshTimeMs to set
	 */
	public void setCalendarRefreshTimeMs(int calendarRefreshTimeMs) {
		this.calendarRefreshTimeMs = calendarRefreshTimeMs;
	}

	/**
	 * @return the customPageTitle
	 */
	public String getCustomPageTitle() {
		return customPageTitle;
	}
	/**
	 * @param customPageTitle the customPageTitle to set
	 */
	public void setCustomPageTitle(String customPageTitle) {
		this.customPageTitle = customPageTitle;
	}

	/**
	 * @return the pluginPackageExtension
	 */
	public String getPluginPackageExtension() {
		return pluginPackageExtension;
	}
	/**
	 * @param pluginPackageExtension the pluginPackageExtension to set
	 */
	public void setPluginPackageExtension(String pluginPackageExtension) {
		this.pluginPackageExtension = pluginPackageExtension;
	}

	/**
	 * @return the pluginPaths
	 */
	public Set<String> getPluginPaths() {
		if(pluginPaths == null) {
			pluginPaths = new HashSet<String>();
		}
		return pluginPaths;
	}
	/**
	 * @param pluginPaths the pluginPaths to set
	 */
	public void setPluginPaths(Set<String> pluginPaths) {
		this.pluginPaths = pluginPaths;
	}

	/**
	 * @return the pluginWorkPath
	 */
	public String getPluginWorkPath() {
		return pluginWorkPath;
	}
	/**
	 * @param pluginWorkPath the pluginWorkPath to set
	 */
	public void setPluginWorkPath(String pluginWorkPath) {
		this.pluginWorkPath = pluginWorkPath;
	}

	/**
	 * @return the resourcePaths
	 */
	public Set<String> getResourcePaths() {
		if(resourcePaths == null) {
			resourcePaths = new HashSet<String>();
		}
		return resourcePaths;
	}
	/**
	 * @param resourcePaths the resourcePaths to set
	 */
	public void setResourcePaths(Set<String> resourcePaths) {
		this.resourcePaths = resourcePaths;
	}

	/**
	 * @return the applicationSettingsId
	 */
	public Long getApplicationSettingsId() {
		return applicationSettingsId;
	}
	/**
	 * @param applicationSettingsId the applicationSettingsId to set
	 */
	public void setApplicationSettingsId(Long applicationSettingsId) {
		this.applicationSettingsId = applicationSettingsId;
	}

	/**
	 * @return the pluginCleanupEnabled
	 */
	public boolean isPluginCleanupEnabled() {
		return pluginCleanupEnabled;
	}
	/**
	 * @param pluginCleanupEnabled the pluginCleanupEnabled to set
	 */
	public void setPluginCleanupEnabled(boolean pluginCleanupEnabled) {
		this.pluginCleanupEnabled = pluginCleanupEnabled;
	}

	/**
	 * @return the configurationPassword
	 */
	public String getConfigurationPassword() {
		return configurationPassword;
	}
	/**
	 * @param configurationPassword the configurationPassword to set
	 */
	public void setConfigurationPassword(String configurationPassword) {
		this.configurationPassword = configurationPassword;
	}

	/**
	 * @return the configurationUsername
	 */
	public String getConfigurationUsername() {
		return configurationUsername;
	}
	/**
	 * @param configurationUsername the configurationUsername to set
	 */
	public void setConfigurationUsername(String configurationUsername) {
		this.configurationUsername = configurationUsername;
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
