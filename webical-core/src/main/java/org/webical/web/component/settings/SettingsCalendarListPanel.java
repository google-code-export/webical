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
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.webical.Calendar;
import org.webical.comparator.CalendarNameComparator;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;
import org.webical.web.action.CalendarSelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.components.ajax.decorator.ConfirmationAjaxCallDecorator;

/**
 * Lists the registered calendars on the Settings panel
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 */
public abstract class SettingsCalendarListPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SettingsCalendarListPanel.class);

	//Markup ids
	private static final String PAGING_NAVIGATOR_MARKUP_ID = "pagingNavigator";
	private static final String CALENDAR_LIST_VIEW_MARKUP_ID = "calendarListView";

	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;

	private List<Calendar> calendars;

	private PageableCalendarListView calendarListView;

	/**
	 * Constructor
	 * @param markupId the id used in the markup
	 */
	public SettingsCalendarListPanel(String markupId) {
		super(markupId, SettingsCalendarListPanel.class);

		calendars = new ArrayList<Calendar>();
		try {
			calendars = calendarManager.getCalendars(WebicalSession.getWebicalSession().getUser());
			if(calendars != null) {
				Collections.sort(calendars, new CalendarNameComparator());
			}
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not load calendars for user: " + WebicalSession.getWebicalSession().getUser().getUserId(), e);
		}

	}

	public void setupCommonComponents() {
		calendarListView = new PageableCalendarListView(CALENDAR_LIST_VIEW_MARKUP_ID, calendars, 5);
		addOrReplace(calendarListView);
	}

	public void setupAccessibleComponents() {
		addOrReplace(new PagingNavigator(PAGING_NAVIGATOR_MARKUP_ID, calendarListView));
	}

	public void setupNonAccessibleComponents() {
		addOrReplace(new AjaxPagingNavigator(PAGING_NAVIGATOR_MARKUP_ID, calendarListView));
	}

	/**
	 * Shows a list of calendars for a User
	 * @author ivo
	 *
	 */
	private class PageableCalendarListView extends PageableListView {
		private static final String REMOVE_CONFIRMATION_MESSAGE_RESOURCE_ID = "remove_confirmation_message";
		private static final String CALENDAR_TITLE_RESOURCE_PARAMETER = "calendar_title";
		private static final String REMOVE_CALENDAR_LINK_MARKUP_ID = "removeCalendarLink";
		private static final String EDIT_CALENDAR_LINK_MARKUP_ID = "editCalendarLink";
		private static final String CALENDAR_NAME_MARKUP_ID = "calendarName";
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor.
		 * @param id The ID to use in markup
		 * @param list The list of items to show
		 * @param rowsPerPage The number of rows per page
		 */
		public PageableCalendarListView(String id, List<Calendar> list, int rowsPerPage) {
			super(id, list, rowsPerPage);
		}

		/* (non-Javadoc)
		 * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
		 */
		@Override
		protected void populateItem(ListItem listItem) {
			final Calendar calendar = (Calendar)listItem.getModelObject();
			listItem.add(new Label(CALENDAR_NAME_MARKUP_ID, calendar.getName()));
			addRemoveLink(listItem, calendar);
			addEditLink(listItem, calendar);
		}

		/**
		 * adds a remove link to the listitem
		 * @param listItem the listitem to add it to
		 * @param calendar the calendar to remove
		 */
		private void addRemoveLink(ListItem listItem, final Calendar calendar) {
			if(accessible) {
				listItem.add(new Link(REMOVE_CALENDAR_LINK_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.apache.wicket.markup.html.link.Link#onClick()
					 */
					@Override
					public void onClick() {
						// TODO mattijs: show confirmation window
						try {
							if(log.isDebugEnabled()) {
								log.debug("removing calendar: " + calendar.getName() + " for user: " + calendar.getUser().getUserId());
							}
							calendarManager.removeCalendar(calendar);
						} catch (WebicalException e) {
							throw new WebicalWebAplicationException("Could not remove calendar: " + calendar.getName(), e);
						}
					}

				});
			} else {
				listItem.add(new IndicatingAjaxLink(REMOVE_CALENDAR_LINK_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.apache.wicket.ajax.markup.html.AjaxLink#onClick(org.apache.wicket.ajax.AjaxRequestTarget)
					 */
					@Override
					public void onClick(AjaxRequestTarget target) {
						// TODO mattijs: show confirmation window
						try {
							if(log.isDebugEnabled()) {
								log.debug("removing calendar: " + calendar.getName() + " for user: " + calendar.getUser().getUserId());
							}
							calendarManager.removeCalendar(calendar);
							target.addComponent(SettingsCalendarListPanel.this);
						} catch (WebicalException e) {
							throw new WebicalWebAplicationException("Could not remove calendar: " + calendar.getName(), e);
						}
					}

					@Override
					protected IAjaxCallDecorator getAjaxCallDecorator() {
						ValueMap parameterMap = new ValueMap();
						parameterMap.put(CALENDAR_TITLE_RESOURCE_PARAMETER, calendar.getName());
						return new ConfirmationAjaxCallDecorator(new StringResourceModel(REMOVE_CONFIRMATION_MESSAGE_RESOURCE_ID, SettingsCalendarListPanel.this, new Model(parameterMap)).getString());
					}

				});
			}
		}

		/**
		 * Adds a edit calendar link (ajax/accessible)
		 * @param listItem the listime to ad it to
		 * @param calendar the calendar to edit
		 */
		private void addEditLink(ListItem listItem, final Calendar calendar) {
			if(accessible) {
				listItem.add(new Link(EDIT_CALENDAR_LINK_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.apache.wicket.markup.html.link.Link#onClick()
					 */
					@Override
					public void onClick() {
						SettingsCalendarListPanel.this.onAction(new CalendarSelectedAction(calendar, null));
					}

				});
			} else {
				listItem.add(new IndicatingAjaxLink(EDIT_CALENDAR_LINK_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.apache.wicket.ajax.markup.html.AjaxLink#onClick(org.apache.wicket.ajax.AjaxRequestTarget)
					 */
					@Override
					public void onClick(AjaxRequestTarget target) {
						SettingsCalendarListPanel.this.onAction(new CalendarSelectedAction(calendar, target));
					}

				});
			}
		}

	}

	/**
	 * Handle actions generated by this panel.
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

	/**
	 * @param calendarManager a CalendarManager
	 */
	public void setCalendarManager(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}

}
