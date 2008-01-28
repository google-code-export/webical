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

package org.webical;

import org.webical.manager.ApplicationSettingsManager;
import org.webical.manager.WebicalException;
import org.webical.settings.ApplicationSettingsFactory;

/**
 * Coolection of helpfull methods
 * @author ivo
 *
 */
public class TestUtils {

	/**
	 * Initializes the ApplicationSettingsFactory with a dummy ApplicationSettingsManager
	 */
	public static void initializeApplicationSettingsFactory(final ApplicationSettings applicationSettings) {
		ApplicationSettingsFactory.getInstance().setApplicationSettingsManager(new ApplicationSettingsManager() {

			public ApplicationSettings getApplicationSettings() throws WebicalException {
				return applicationSettings;
			}

			public void storeApplicationSettings(ApplicationSettings applicationSettings) throws WebicalException {
			}
			
		});
	}
}
