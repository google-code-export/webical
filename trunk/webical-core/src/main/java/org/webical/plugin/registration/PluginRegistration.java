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

package org.webical.plugin.registration;

import java.io.Serializable;

import org.webical.plugin.jaxb.Plugin;

/**
 * This class serves as a registration for plugins
 * @author ivo
 *
 */
public class PluginRegistration implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum PluginState {
		REGISTERED,
		NOT_REGISTERED
	};
	
	/**
	 * The plugin
	 */
	private Plugin plugin;
	
	
	/**
	 * The state of the plugin registration
	 */
	private PluginState pluginState;
	
	
	/**
	 * An optional error message
	 */
	private String message;
	
	/**
	 * The detailed error
	 */
	private Throwable exception;
	
	
	/**
	 * @param plugin
	 * @param pluginState
	 * @param message
	 */
	public PluginRegistration(Plugin plugin, PluginState pluginState, String message, Throwable throwable) {
		super();
		this.plugin = plugin;
		this.pluginState = pluginState;
		this.message = message;
		this.exception = throwable;
	}
	
	/**
	 * @param plugin
	 * @param pluginState
	 */
	public PluginRegistration(Plugin plugin, PluginState pluginState) {
		super();
		this.plugin = plugin;
		this.pluginState = pluginState;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the plugin
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * @param plugin the plugin to set
	 */
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return the pluginState
	 */
	public PluginState getPluginState() {
		return pluginState;
	}

	/**
	 * @param pluginState the pluginState to set
	 */
	public void setPluginState(PluginState pluginState) {
		this.pluginState = pluginState;
	}

	/**
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

}
