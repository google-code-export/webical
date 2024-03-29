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

package org.webical.web.component.calendar;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.webical.Calendar;
import org.webical.web.action.CalendarSelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.action.SwitchCalendarVisibilityAction;
import org.webical.web.app.WebicalWebApplication;
import org.webical.web.component.AbstractBasePanel;

/**
 * Panel that displays a List of calendars from the user
 *
 * @author paul
 * @author Mattijs Hoitink
 *
 */
public abstract class CalendarListPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	// Markup IDs
	private static final String CALENDAR_LISTVIEW_MARKUP_ID = "calendarListview";
	private static final String ADD_CALENDAR_LINK_MARKUP_ID = "addCalendarLink";

	/** List with Calendars for thsi User **/
	private List<Calendar> calendars;

	private Link addCalendarLink;

	// Image resources
	private ResourceReference enabledImage = new ResourceReference(WebicalWebApplication.class, "enabled.gif");
	private ResourceReference disabledImage = new ResourceReference(WebicalWebApplication.class, "disabled.gif");

	/**
	 * Constructor
	 * @param markupId
	 */
	public CalendarListPanel(String markupId, List<Calendar> calendars) {
		super(markupId, CalendarListPanel.class);
		this.calendars = calendars;
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		addOrReplace(new CalendarListview(CALENDAR_LISTVIEW_MARKUP_ID, calendars));
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		addCalendarLink = new Link(ADD_CALENDAR_LINK_MARKUP_ID){
			private static final long serialVersionUID = 1L;

			/** Gives the ContentPanelChangeListner a sign to change the panel to the CalendarEditPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				// pass an empty calendar to fool the calendar settings panel in showing a form instead of a list (use null for list)
				CalendarListPanel.this.onAction(new CalendarSelectedAction(new Calendar(), null));
			}
		};
		addOrReplace(addCalendarLink);
	}

	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}

	// TODO mattijs: implement with onAction(IAction action)
	/**
	 * Notify the parent to enable all calendars for the current user
	 * @param target The Ajax Target of the panel
	 */
	public abstract void enableAllCalendars(AjaxRequestTarget target);

	/**
	 * Notify the parent to enble only this calendar for the current user
	 * @param calendar The calendar to edit
	 * @param target The Ajax Target of the panel
	 */
	public abstract void enableOnlyThisCalendar(Calendar calendar, AjaxRequestTarget target);

	/** 
	 * Handle actions generated by this panel
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

	/**
	 * Shows a list of calendars for the user
	 * @author paul
	 */
	private class CalendarListview extends ListView {

		private static final String CLASS_ATTRIBUTE_INVISIBLE = "calendarItem-invisible";
		private static final String CLASS_ATTRIBUTE_ID = "class";
		private static final String CALENDAR_VISIBLE_LINK_IMG_MARKUP_ID = "calendarVisibleLinkImg";
		private static final String CALENDAR_VISIBLE_LINK_MARKUP_ID = "calendarVisibleLink";
		private static final String CALENDAR_NAME_MARKUP_ID = "calendarName";
		private static final String CALENDAR_LINK_MARKUP_ID = "calendarLink";

		/**
		 * Constructor.
		 * @param id The ID to use in markup
		 * @param list List of items to display
		 */
		public CalendarListview(String id, List<Calendar> list) {
			super(id, list);
		}

		private static final long serialVersionUID = 1L;

		// Build the list of calendars
		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.list.ListView#populateItem(org.apache.wicket.markup.html.list.ListItem)
		 */
		@Override
		protected void populateItem(ListItem listItem) {
			final Calendar calendar = (Calendar)listItem.getModelObject();

			Link calendarVisibleLink = new Link(CALENDAR_VISIBLE_LINK_MARKUP_ID) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					CalendarListPanel.this.onAction(new SwitchCalendarVisibilityAction(calendar));
				}
			};

			Link calendarLink = new Link(CALENDAR_LINK_MARKUP_ID){
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					CalendarListPanel.this.onAction(new CalendarSelectedAction(calendar, null));
				}
			};
			calendarLink.add(new Label(CALENDAR_NAME_MARKUP_ID, calendar.getName()));
			
			//Add an image to the visibility link
			Image visibilityImage = null;
			if(calendar.getVisible()) {
				visibilityImage = new Image(CALENDAR_VISIBLE_LINK_IMG_MARKUP_ID, enabledImage);
			} else {
				visibilityImage = new Image(CALENDAR_VISIBLE_LINK_IMG_MARKUP_ID, disabledImage);
			}
			calendarVisibleLink.add(visibilityImage);

			//Add the components to the listitem
			listItem.add(calendarVisibleLink);
			listItem.add(calendarLink);

			//Set visibility attributes
			setVisibilityAttributes(calendar.getVisible(), listItem, visibilityImage);
		}

		private void setVisibilityAttributes(boolean visible, ListItem listItem, Image visibilityImage) {
			//Add style (visible/invisible)
			if(visible) {
				listItem.add(new AttributeModifier(CLASS_ATTRIBUTE_ID, true, new Model(CLASS_ATTRIBUTE_INVISIBLE)));
			}

			//Add image alt tag
			StringResourceModel altStringResourceModel = null;
			if(visible) {
				altStringResourceModel = new StringResourceModel("image.alt.enabled", CalendarListPanel.this,  null);
			} else {
				altStringResourceModel = new StringResourceModel("image.alt.disabled", CalendarListPanel.this,  null);
			}
			visibilityImage.add(new AttributeModifier("alt", altStringResourceModel));
		}

		
	}

}
