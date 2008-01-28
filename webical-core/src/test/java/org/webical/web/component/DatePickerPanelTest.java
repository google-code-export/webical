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

import java.util.GregorianCalendar;

import org.apache.wicket.Page;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.DatePickerPanel;
import org.webical.web.component.calendar.model.DatePickerModel;

/**
 * Small test for the DatePickerPanel
 * @author ivo
 *
 */
public class DatePickerPanelTest extends WebicalApplicationTest {

	/* (non-Javadoc)
	 * @see org.webical.web.WebicalApplicationTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
	}

	/**
	 * Tests the DatePickerPanel on input
	 */
	public void testDatePickerPanel() {

		//Create testpage with a DatePickerPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DatePickerPanel(PanelTestPage.PANEL_MARKUP_ID, new CompoundPropertyModel(new DatePickerModel(new GregorianCalendar()))){
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, DatePickerPanel.class);

		// Test validation
		FormTester pickerFormTester = wicketTester.newFormTester(PanelTestPage.PANEL_MARKUP_ID + ":datePickerForm");
		pickerFormTester.setValue("changeDateField", "notADate");
		pickerFormTester.submit();
		wicketTester.assertErrorMessages(new String[]{"'notADate' is not a valid Date."});

	}
}
