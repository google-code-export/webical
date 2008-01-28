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

package org.webical.web.component;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.manager.impl.mock.MockCalendarManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.component.settings.SettingsPanelsPanel;

/**
 * Tests for the SettingsPanelsPanel
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 *
 */
public class SettingsPanelsPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
	}

	/**
	 * Test whether the SettingsPanelsPanel Renders correctly with a User
	 */
	public void testRenderDefaultMode() {
		//Prepare the user
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gosling");
		user.setUserId("jag");
		webicalSession.setUser(user);

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());

		// Create testpage with a SettingsPanelsPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new SettingsPanelsPanel(PanelTestPage.PANEL_MARKUP_ID) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, SettingsPanelsPanel.class);
	}

}
