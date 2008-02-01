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

package org.webical.web.component.settings;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.webical.Calendar;
import org.webical.web.action.IAction;
import org.webical.web.component.AbstractBasePanel;

/**
 * SettingsPanel holds a tabbed panel containing all tabs
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 */
public abstract class SettingsPanelsPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	public static final int USER_SETTINGS_TAB_INDEX = 0;
	public static final int CALENDAR_SETTINGS_TAB_INDEX = 1;
	private static final String CALENDAR_SETTINGS_TAB_LABEL = "Calendar_settings_tab_label";
	private static final String USER_SETTINGS_TAB_LABEL = "User_settings_tab_label";
	private static final String SETTINGS_TABS_MARKUP_ID = "settingsTabs";

	private TabbedPanel tabbedPanel;
	private List<AbstractTab> tabs = new ArrayList<AbstractTab>();
	private int selectedTab = 0;

	/** Used to pass arguments to the constructed tabs **/
	private Object[] panelArguments;

	/**
	 * Sets up the Tabbed panel
	 * @param markupId The id used in the markup
	 */
	public SettingsPanelsPanel(String markupId) {
		super(markupId, SettingsPanelsPanel.class);

		// Setup the user tab
		tabs.add(new AbstractTab(new StringResourceModel(USER_SETTINGS_TAB_LABEL, this, null)) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.extensions.markup.html.tabs.AbstractTab#getPanel(java.lang.String)
			 */
			@Override
			public Panel getPanel(String markupId) {
				return new UserSettingsPanel(markupId) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.webical.web.component.settings.UserSettingsPanel#onAction(org.webical.web.action.IAction)
					 */
					@Override
					public void onAction(IAction action) {
						SettingsPanelsPanel.this.onAction(action);
					}

				};
			}

		});
		// Setup the calendars tab
		tabs.add(new AbstractTab(new StringResourceModel(CALENDAR_SETTINGS_TAB_LABEL, this, null)) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.extensions.markup.html.tabs.AbstractTab#getPanel(java.lang.String)
			 */
			@Override
			public Panel getPanel(String markupId) {
				return new CalendarSettingsPanel(markupId, getCalendar()){
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.webical.web.components.CalendarSettingsPanel#loadPreviousContent(org.apache.wicket.ajax.AjaxRequestTarget)
					 */
					@Override
					public void onAction(IAction action) {
						SettingsPanelsPanel.this.onAction(action);
					}
				};
			}
		});
	}

	public void setupCommonComponents() {
		// NOT IMPLEMENTED
	}

	public void setupAccessibleComponents() {
		tabbedPanel = new TabbedPanel(SETTINGS_TABS_MARKUP_ID, tabs ) {
			private static final long serialVersionUID = 1L;
		};
		if(selectedTab != 0) {
			tabbedPanel.setSelectedTab(selectedTab);
		}
		addOrReplace(tabbedPanel);
	}

	public void setupNonAccessibleComponents() {

	}

	/**
	 * Notify the parent to load to the previous content
	 * @param target The Ajax target of the panel
	 */
	public abstract void onAction(IAction action);

	/**
	 * Method for future use, to set the right tab when redirecting to the settings screen.
	 * @param panelTabIndex the tab to select, starting with 0
	 * @param panelArguments arguments to pass on to the tab
	 */
	public void setSelectedTab(int panelTabIndex, Object ... panelArguments ) {
		if(panelTabIndex < 0 || panelTabIndex >= tabs.size()) {
			throw new IllegalArgumentException("Wrong tab index: " + panelTabIndex + " is larger than the total number of tabs (" + tabs.size() + ")");
		}
		this.panelArguments = panelArguments;

		if(tabbedPanel != null) {
			tabbedPanel.setSelectedTab(panelTabIndex);
		} else {
			this.selectedTab = panelTabIndex;
		}
	}

	/**
	 * Returns the calendar instance when the tab is pressed
	 * @return Calendar
	 */
	private Calendar getCalendar() {
		if(panelArguments != null && panelArguments.length > 0 && panelArguments[0] instanceof Calendar) {
			Calendar retCal = (Calendar) panelArguments[0];
			panelArguments[0] = null;
			return retCal;
		} else {
			return null;
		}
	}
}
