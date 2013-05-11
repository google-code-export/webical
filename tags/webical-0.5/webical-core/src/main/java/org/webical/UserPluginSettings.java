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

/**
 * Pluginsettings per user. We're not extending
 * {@link PluginSettings} here because hibernate would'nt have it.
 * @author ivo
 */
public class UserPluginSettings extends Settings {
	private static final long serialVersionUID = 1L;

	private User user = null;

	/**
	 * The fully qualified classname
	 */
	private String pluginClass = null;

	public UserPluginSettings() {
	}

	/**
	 * @return the pluginClass
	 */
	public String getPluginClass() {
		return pluginClass;
	}
	/**
	 * @param pluginClass the pluginClass to set
	 */
	public void setPluginClass(String pluginName) {
		this.pluginClass = pluginName;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
