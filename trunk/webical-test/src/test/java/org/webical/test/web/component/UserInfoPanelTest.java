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

import java.text.DateFormat;

import org.webical.User;
import org.webical.web.component.UserInfoPanel;
import org.webical.test.TestUtils;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;


/**
 * Tests the UserInfoPanel
 * @author ivo
 *
 */
//TODO test needs to be rewritten with easymock
public class UserInfoPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Tests whether the panel gets rendered properly with userinfo
	 */
	public void testRenderWithUserInfo() {
		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();

		// Create the testpage with our panel
		UserInfoPanel userInfoPanel = new UserInfoPanel(PanelTestPage.PANEL_MARKUP_ID);
		wicketTester.startPage(new PanelTestPage(userInfoPanel));

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, UserInfoPanel.class);

		//Test the fields
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":birthdate", DateFormat.getDateInstance(DateFormat.LONG, userInfoPanel.getLocale()).format(user.getBirthDate()));
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":firstName", user.getFirstName());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":lastNamePrefix", user.getLastNamePrefix());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":lastName", user.getLastName());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":userId", user.getUserId());
	}

	/**
	 * Tests whether the panel gets rendered without userinfo
	 */
	public void testRenderWithoutUserInfo() {
		//Create the testpage with our panel
		wicketTester.startPage(new PanelTestPage(new UserInfoPanel(PanelTestPage.PANEL_MARKUP_ID)));

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, UserInfoPanel.class);

		//Test the fields
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":birthdate", "");
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":firstName", "");
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":lastNamePrefix", "");
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":lastName", "");
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":userId", getTestSession().getUser().getUserId());
	}
}
