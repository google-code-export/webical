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

package org.webical.web.component.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
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
import org.webical.web.action.ShowSettingsAction;
import org.webical.web.action.SwitchCalendarVisibilityAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.app.WebicalWebApplication;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.ConfirmActionPanel;
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

	// Image resources for the visibility switch images
	private ResourceReference enabledImage = new ResourceReference(WebicalWebApplication.class, "enabled.gif");
	private ResourceReference disabledImage = new ResourceReference(WebicalWebApplication.class, "disabled.gif");

	/** List of actions handled by this panel */
	protected static Class[] PANELACTIONS = new Class[] { SwitchCalendarVisibilityAction.class };

	/** CalendarManager set by Spring **/
	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;

	/** List with calendars for the user **/
	private List<Calendar> calendars;

	/** Pagable Calendar ListView **/
	private PageableCalendarListView calendarListView;

	/**
	 * Constructor
	 * @param markupId the id used in markup
	 */
	public SettingsCalendarListPanel(String markupId) {
		super(markupId, SettingsCalendarListPanel.class);

		calendars = new ArrayList<Calendar>();
		try {
			calendars = calendarManager.getCalendars(WebicalSession.getWebicalSession().getUser());
			if (calendars != null) {
				Collections.sort(calendars, new CalendarNameComparator());
			}
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not load calendars for user: " + WebicalSession.getWebicalSession().getUser().getUserId(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		calendarListView = new PageableCalendarListView(CALENDAR_LIST_VIEW_MARKUP_ID, calendars, 5) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				SettingsCalendarListPanel.this.internalOnaction(action);
			}
		};
		addOrReplace(calendarListView);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		addOrReplace(new PagingNavigator(PAGING_NAVIGATOR_MARKUP_ID, calendarListView));
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		addOrReplace(new AjaxPagingNavigator(PAGING_NAVIGATOR_MARKUP_ID, calendarListView));
	}

	/**
	 * Filters actions for this panel before sending them to the next component
	 * @param action The action to handle
	 */
	private void internalOnaction(IAction action) {
		// Check if the action is in the list with actions for this panel
		if (Arrays.asList(PANELACTIONS).contains(action.getClass())) {

			if (action.getClass().equals(SwitchCalendarVisibilityAction.class)) {
				SwitchCalendarVisibilityAction switchCalendarVisibilityAction = (SwitchCalendarVisibilityAction) action;
				try {
					// Set the calendar visibility to the opposite of its current state
					org.webical.Calendar selectedCalendar = switchCalendarVisibilityAction.getCalendar();
					selectedCalendar.setVisible(!selectedCalendar.getVisible());
					// Store the calendar through the manager
					calendarManager.storeCalendar(selectedCalendar);
				} catch (WebicalException e) {
					throw new WebicalWebAplicationException("Could not store calendar", e);
				}
				// Reload and show the settings panel
				this.onAction(new ShowSettingsAction(SettingsPanelsPanel.CALENDAR_SETTINGS_TAB_INDEX));
			}
		}
		else {
			// Pass the action to the next component
			this.onAction(action);
		}
	}

	/**
	 * Handle actions generated by this panel.
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

	/**
	 * Shows a list of calendars for a User
	 * @author Ivo van Dongen
	 * @author Mattijs Hoitink
	 */
	private abstract class PageableCalendarListView extends PageableListView {
		private static final long serialVersionUID = 1L;

		private static final String CLASS_ATTRIBUTE_INVISIBLE = "calendarItem-invisible";
		private static final String CLASS_ATTRIBUTE_ID = "class";
		private static final String REMOVE_CONFIRMATION_MESSAGE_RESOURCE_ID = "remove_confirmation_message";

		// Markup ID's
		private static final String CALENDAR_VISIBLE_LINK_MARKUP_ID = "switchCalendarVisibilityLink";
		private static final String CALENDAR_VISIBLE_LINK_IMG_MARKUP_ID = "calendarVisibleLinkImg";
		private static final String CALENDAR_TITLE_RESOURCE_PARAMETER = "calendar_title";
		private static final String REMOVE_CALENDAR_LINK_MARKUP_ID = "removeCalendarLink";
		private static final String EDIT_CALENDAR_LINK_MARKUP_ID = "editCalendarLink";
		private static final String CALENDAR_NAME_MARKUP_ID = "calendarName";

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
			addVisibilityLink(listItem, calendar);
			listItem.add(new Label(CALENDAR_NAME_MARKUP_ID, calendar.getName()));
			addRemoveLink(listItem, calendar);
			addEditLink(listItem, calendar);
		}

		/**
		 * Adds a remove link to the listitem
		 * @param listItem The listitem to add it to
		 * @param calendar The calendar to remove
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
						// Show confirmation window
						SettingsCalendarListPanel.this.getParent().replaceWith(new ConfirmActionPanel(SettingsCalendarListPanel.this.getParent().getId(), "Are you sure you want to delete " + calendar.getName() + "?") {
							private static final long serialVersionUID = 1L;

							@Override
							public void onCancel() {
								this.replaceWith(SettingsCalendarListPanel.this.getParent());
							}

							@Override
							public void onConfirm() {
								try {
									if(log.isDebugEnabled()) {
										log.debug("removing calendar: " + calendar.getName() + " for user: " + calendar.getUser().getUserId());
									}
									// Remove the Calendar through the CalendarManager
									calendarManager.removeCalendar(calendar);
								} catch (WebicalException e) {
									throw new WebicalWebAplicationException("Could not remove calendar: " + calendar.getName(), e);
								}

								// Show and reload the settings panel
								SettingsCalendarListPanel.this.onAction(new ShowSettingsAction(SettingsPanelsPanel.CALENDAR_SETTINGS_TAB_INDEX));
							}
						});
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
						final AjaxRequestTarget ajaxTarget = target;
						// Show confirmation window
						SettingsCalendarListPanel.this.getParent().replaceWith(new ConfirmActionPanel(SettingsCalendarListPanel.this.getParent().getId(), "Are you sure you want to delete " + calendar.getName() + "?") {
							private static final long serialVersionUID = 1L;

							@Override
							public void onCancel() {
								this.replaceWith(SettingsCalendarListPanel.this.getParent());
							}

							@Override
							public void onConfirm() {
								try {
									if(log.isDebugEnabled()) {
										log.debug("removing calendar: " + calendar.getName() + " for user: " + calendar.getUser().getUserId());
									}
									// Remove the Calendar through the CalendarManager
									calendarManager.removeCalendar(calendar);
								} catch (WebicalException e) {
									throw new WebicalWebAplicationException("Could not remove calendar: " + calendar.getName(), e);
								}

								// Show and reload the settings panel
								SettingsCalendarListPanel.this.onAction(new ShowSettingsAction(SettingsPanelsPanel.CALENDAR_SETTINGS_TAB_INDEX, ajaxTarget));
							}
						});
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
		 * Adds an edit link (ajax/accessible)
		 * @param listItem the listime to ad it to
		 * @param calendar the calendar to edit
		 */
		private void addEditLink(ListItem listItem, final Calendar calendar) {
			if (accessible) {
				listItem.add(new Link(EDIT_CALENDAR_LINK_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.apache.wicket.markup.html.link.Link#onClick()
					 */
					@Override
					public void onClick() {
						PageableCalendarListView.this.onAction(new CalendarSelectedAction(calendar, null));
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
						PageableCalendarListView.this.onAction(new CalendarSelectedAction(calendar, target));
					}
				});
			}
		}

		/**
		 * Add a link to switch the visibility of a Calendar
		 * @param listItem The listitem to add the link to
		 * @param calendar The calendar to switch
		 */
		private void addVisibilityLink(ListItem listItem, final Calendar calendar) {
			// Create the visibility images
			Image visibilityImage = null;
			if(calendar.getVisible()) {
				visibilityImage = new Image(CALENDAR_VISIBLE_LINK_IMG_MARKUP_ID, enabledImage);
			} else {
				visibilityImage = new Image(CALENDAR_VISIBLE_LINK_IMG_MARKUP_ID, disabledImage);
			}

			// Create and add the links
			if (accessible) {
				Link calendarVisibleLink = new Link(CALENDAR_VISIBLE_LINK_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						PageableCalendarListView.this.onAction(new SwitchCalendarVisibilityAction(calendar));
					}
				};
				calendarVisibleLink.add(visibilityImage);
				listItem.add(calendarVisibleLink);
			} else {
				IndicatingAjaxLink calendarVisibleLink = new IndicatingAjaxLink(CALENDAR_VISIBLE_LINK_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						PageableCalendarListView.this.onAction(new SwitchCalendarVisibilityAction(calendar, target));
					}
				}; 
				calendarVisibleLink.add(visibilityImage);
				listItem.add(calendarVisibleLink);
			}

			// Set the right image
			setVisibilityAttributes(calendar.getVisible(), listItem, visibilityImage);
		}

		private void setVisibilityAttributes(boolean visible, ListItem listItem, Image visibilityImage) {
			//Add style (visible/invisible)
			if (visible) {
				listItem.add(new AttributeModifier(CLASS_ATTRIBUTE_ID, true, new Model(CLASS_ATTRIBUTE_INVISIBLE)));
			}

			//Add image alt tag
			StringResourceModel altStringResourceModel = null;
			if (visible) {
				altStringResourceModel = new StringResourceModel("image.alt.enabled", SettingsCalendarListPanel.this,  null);
			} else {
				altStringResourceModel = new StringResourceModel("image.alt.disabled", SettingsCalendarListPanel.this,  null);
			}
			visibilityImage.add(new AttributeModifier("alt", altStringResourceModel));
		}

		public abstract void onAction(IAction action);
	}

	/**
	 * @param calendarManager a CalendarManager
	 */
	public void setCalendarManager(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}
}
