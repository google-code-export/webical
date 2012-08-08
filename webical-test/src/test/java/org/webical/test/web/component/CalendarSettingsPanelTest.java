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

package org.webical.test.web.component;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.component.settings.CalendarSettingsPanel;
import org.webical.web.component.settings.SettingsCalendarListPanel;

/**
 * @author ivo
 *
 */
public class CalendarSettingsPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());
	}

	/**
	 * Tests whether the panel gets rendered properly in default mode
	 */
	public void testRendersDefaultMode() {

		// Create testpage with a CalendarSettingsPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarSettingsPanel(PanelTestPage.PANEL_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarSettingsPanel.class);

		// Assert the loaded panel
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID+":calendarSettingsPanelContent", SettingsCalendarListPanel.class);
	}
}
