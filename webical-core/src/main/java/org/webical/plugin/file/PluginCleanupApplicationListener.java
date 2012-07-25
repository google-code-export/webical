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

package org.webical.plugin.file;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.webical.settings.ApplicationSettingsException;
import org.webical.settings.ApplicationSettingsFactory;
import org.webical.web.app.WebicalWebAplicationException;

/**
 * Cleans up the plugin work directory when the application stops
 * @author ivo
 *
 */
public class PluginCleanupApplicationListener implements ApplicationListener {
	private static Log log = LogFactory.getLog(PluginCleanupApplicationListener.class);

	/**
	 * Cleans up the plugin work directory on context stop
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	public void onApplicationEvent(ApplicationEvent event) {
		try {
			if (event instanceof ContextClosedEvent && ApplicationSettingsFactory.getInstance().getApplicationSettings().isPluginCleanupEnabled()) {
				File pluginWorkDir = new File(ApplicationSettingsFactory.getInstance().getApplicationSettings().getPluginWorkPath());

				if (pluginWorkDir.exists() && pluginWorkDir.isDirectory()) {
					log.info("Cleaning up the plugin work directory: " + pluginWorkDir.getAbsolutePath());
					FileUtils.cleanupDirectory(pluginWorkDir);
				} else {
					log.info("Could not cleanup plugin work directory: " + pluginWorkDir.getAbsolutePath() + " because it doesn't exist or is not a directory");
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Received ApplicationEvent of class: " + event.getClass());
				}
			}
		} catch (ApplicationSettingsException e) {
			throw new WebicalWebAplicationException(e);
		}
	}
}
