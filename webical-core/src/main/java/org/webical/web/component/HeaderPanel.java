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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.webical.web.app.WebicalSession;
import org.webical.web.pages.BasePage;

/**
 * Panel to be placed at the to of the page including all panel navigation and a logout button
 * @author ivo
 *
 */
public abstract class HeaderPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String LOGOUT_LINK_MARKUP_ID = "logoutLink";
	private static final String LOGOUT_LABEL_MARKUP_ID = "logoutLink.label";
	private static final String SETTINGS_PANEL_LINK_MARKUP_ID = "settingsPanelLink";
	private static final String CALENDAR_VIEW_LINK_MARKUP_ID = "calendarViewsPanelLink";

	/**
	 * Constructor
	 * @param markupId the id used in the markup
	 * @param listener a ContentPanelChangeListener used to change the content panel
	 */
	public HeaderPanel(String markupId) {
		super(markupId, HeaderPanel.class);
	}

	public void setupCommonComponents() {
		//Add logout link
		Link logoutLink = new Link(LOGOUT_LINK_MARKUP_ID) {
				private static final long serialVersionUID = 1L;

				/**
				 * Logs out the user and resets the url
				 * @see wicket.markup.html.link.Link#onClick()
				 */
				@Override
				public void onClick() {
					getSession().invalidate();
					HeaderPanel.this.setResponsePage(BasePage.class);
				}

			};
		Label logoutLinkLabel = new Label(LOGOUT_LABEL_MARKUP_ID, new StringResourceModel(LOGOUT_LABEL_MARKUP_ID, this, new CompoundPropertyModel(WebicalSession.getWebicalSession().getUser())));
		logoutLink.add(logoutLinkLabel);
		addOrReplace(logoutLink);
	}

	public void setupAccessibleComponents() {
		//Link to settings panel
		addOrReplace(new Link(SETTINGS_PANEL_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Changes the the panel to a SettingsPanelPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				HeaderPanel.this.changeContent(BasePage.SETTINGS_PANELS_PANEL, null);
			}

		});

		//Link to the CalendarViewsPanel
		addOrReplace(new Link(CALENDAR_VIEW_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Change the panel to a CalendarViewsPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				HeaderPanel.this.changeContent(BasePage.CALENDAR_PANEL, null);
			}

		});
	}

	public void setupNonAccessibleComponents() {
		//Link to settings panel
		add(new IndicatingAjaxLink(SETTINGS_PANEL_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/** Changes the the panel to a SettingsPanelPanel
			 * @see wicket.ajax.markup.html.IndicatingAjaxLink#onClick(wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void onClick(AjaxRequestTarget target) {
				HeaderPanel.this.changeContent(BasePage.SETTINGS_PANELS_PANEL, target);
			}

		});

		//Link to the CalendarViewsPanel
		addOrReplace(new IndicatingAjaxLink(CALENDAR_VIEW_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Change the panel to a CalendarViewsPanel
			 * @see wicket.ajax.markup.html.IndicatingAjaxLink#onClick(wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void onClick(AjaxRequestTarget target) {
				HeaderPanel.this.changeContent(BasePage.CALENDAR_PANEL, target);
			}

		});
	}

	/**
	 * Let the parent know the content of the main panel has to be changed
	 * @param panelId The ID of the panel to set as the new content
	 * @param target The Ajax Target of the panel
	 */
	public abstract void changeContent(int panelId, AjaxRequestTarget target);
}

