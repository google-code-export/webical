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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;
import org.webical.web.action.AddEventAction;
import org.webical.web.action.DaySelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.action.WeekSelectedAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.calendar.model.DatePickerModel;
import org.webical.web.component.calendar.model.DateSwitcherModel;

/**
 * CalendarPanel holds the different calendar views and allows switching between them with tabs. <br />
 * CalendarPanel is also used to share information and save state between the different views, and presents
 * two methods of switching to a different date.
 *
 * @author Mattijs Hoitink
 */

public abstract class CalendarPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String CALENDAR_DAY_VIEW_TAB_LABEL = "calendar_day_view_tab_label";
	private static final String CALENDAR_WEEK_VIEW_TAB_LABEL = "calendar_week_view_tab_label";
	private static final String CALENDAR_MONTH_VIEW_TAB_LABEL = "calendar_month_view_tab_label";
	private static final String CALENDAR_AGENDA_VIEW_TAB_LABEL = "calendar_xdays_view_tab_label";
	private static final String CALENDAR_XDAYS_VIEW_TAB_LABEL_AFTER = "calendar_xdays_view_tab_label_after";
	private static final String CALENDAR_VIEWS_TABS_MARKUP_ID = "calendarViewsTabs";
	private static final String CALENDAR_VIEWS_CONTENT_PANEL_MARKUP_ID = "panel";
	private static final String ADD_EVENT_LINK_MARKUP_ID = "addEventLink";
	private static final String ADD_EVENT_LABEL_MARKUP_ID = "addEventLabel";
	private static final String DATESWITCHER_MARKUP_ID = "dateSwitcher";
	private static final String DATEPICKER_MARKUP_ID = "datePicker";

	// ID's for the Calendar Views
	public static final int DAY_VIEW = 0;
	public static final int WEEK_VIEW = 1;
	public static final int MONTH_VIEW = 2;
	public static final int AGENDA_VIEW = 3;

	/** List of actions handled by this panel */
	@SuppressWarnings("unchecked")
	protected static Class[] PANELACTIONS = new Class[] { DaySelectedAction.class, WeekSelectedAction.class };

	// TODO mattijs: get the default view from user settings
	/** The default Calendar View to show. */
	public static final int DEFAULT_VIEW = 1;

	/** Used by Spring to inject the Calendar Manager. */
	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;

	private TabbedPanel tabbedPanel;
	private List<AbstractTab> tabs = new ArrayList<AbstractTab>();

	public static boolean enableAddEvent;

	/** The View currently selected. */
	private int currentView;
	private CalendarViewPanel currentViewPanel;

	/** The selected date. */
	private GregorianCalendar currentDate;

	// Panel components
	private Link addEventLink;
	private Label addEventLabel;
	private DatePickerPanel datePicker;
	private CompoundPropertyModel datePickerModel;
	private DateSwitcherPanel dateSwitcher;
	private DateSwitcherModel dateSwitcherModel;

	/**
	 * Sets up the base panel for the calendar views.
	 * This panel holds the state between different views.
	 *
	 * @param markupId The ID to use in the markup
	 * @param date The date to display
	 */
	@SuppressWarnings("serial")
	public CalendarPanel(String markupId, final Calendar date) {
		super(markupId, CalendarPanel.class);
		if(date != null) {
			currentDate = (GregorianCalendar) date;
		} else {
			currentDate = new GregorianCalendar();
		}

		currentView = DEFAULT_VIEW;

		// Check if the user can add events
		try {
			CalendarPanel.enableAddEvent = (calendarManager.getCalendars(WebicalSession.getWebicalSession().getUser()).size() > 0);
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException(e);
		}

		// Set up the tabs
		createTabs();
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	@SuppressWarnings("serial")
	public void setupCommonComponents() {
		// Create link to add an Event to the current date
		addEventLink = new Link(ADD_EVENT_LINK_MARKUP_ID) {

			@Override
			public void onClick() {
				CalendarPanel.this.internalOnAction(new AddEventAction(currentDate));
			}

		};
		addEventLabel = new Label(ADD_EVENT_LABEL_MARKUP_ID, new StringResourceModel(ADD_EVENT_LABEL_MARKUP_ID, this, null));
		addEventLink.add(addEventLabel);
		add(addEventLink);

		/*
		 *  Add a Date Switcher
		 * The Date Switcher model is updated in the see onBeforeRender phase
		 */
		dateSwitcher = new DateSwitcherPanel(DATESWITCHER_MARKUP_ID, dateSwitcherModel) {

			@Override
			public void nextPeriod(AjaxRequestTarget target) {
				// Add the view period length of the view period for the current panel
				CalendarPanel.this.currentDate.add(CalendarPanel.this.getCurrentViewPanel().getViewPeriodId(), CalendarPanel.this.getCurrentViewPanel().getViewPeriodLength());

				// reload the current calendar view
				CalendarPanel.this.switchView(currentView);
			}

			@Override
			public void previousPeriod(AjaxRequestTarget target) {
				// Subtract the view period length of the view period for the current panel
				CalendarPanel.this.currentDate.add(CalendarPanel.this.getCurrentViewPanel().getViewPeriodId(), (CalendarPanel.this.getCurrentViewPanel().getViewPeriodLength()) * -1);

				// reload the current calendar view
				CalendarPanel.this.switchView(getCurrentView());
			}

			@Override
			public void todaySelected(AjaxRequestTarget target) {
				CalendarPanel.this.changeCalendarView(CalendarPanel.this.getCurrentView(), new GregorianCalendar(), target);
			}

		};
		addOrReplace(dateSwitcher);

		/*
		 * Add a Date Picker
		 * The Date Picker model is updated in the onBeforeRender phase
		 */
		datePicker = new DatePickerPanel(DATEPICKER_MARKUP_ID, new CompoundPropertyModel(datePickerModel)) {

			@Override
			public void onAction(IAction action) {
				CalendarPanel.this.internalOnAction(action);
			}

		};
		addOrReplace(datePicker);

	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		tabbedPanel = new TabbedPanel(CALENDAR_VIEWS_TABS_MARKUP_ID, tabs) {
			private static final long serialVersionUID = 1L;
		};
		/*
		 * Set the selected tab immidiatley before the TabbedPanel is added to the container
		 * to prevent the first tab (day view) being loaded loaded.
		 * If this is not done, the day view will access the DAO and collecting it's events even if the day view isn't rendered.
		 * Recurring Events are not displayed properly if the day view is seleced from the default view (assuming this is a
		 * user setting and the default view isn't the day view) and the day view has to be reloaded (once, only the first time)
		 * to show the recurring events.
		 */
		switchView(DEFAULT_VIEW);
		// Add the TabbedPanel
		addOrReplace(tabbedPanel);

	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}

	/**
	 * Gets the current date used by the Calendar Views.
	 * @return The current date
	 */
	public Calendar getCurrentDate() {
		return currentDate;
	}

	/**
	 * Sets the current date for the Calendar Views.
	 * @param currentDate The current date
	 */
	public void setCurrentDate(Calendar currentDate) {
		// Update the time in the object, don't change the reference else changes are not reflected in the models
		this.currentDate.setTime(currentDate.getTime());
	}

	/**
	 * Gets the ID of current view.
	 * @return The selected view id
	 */
	private int getCurrentView() {
		return currentView;
	}

	/**
	 * Sets the ID of the current view.
	 * @param newView The id of the current view
	 */
	private void setCurrentView(int newView) {
		this.currentView = newView;
	}

	/**
	 * Gets the panel for the current view.
	 * @return The panel for the current view
	 */
	public CalendarViewPanel getCurrentViewPanel() {
		//return (CalendarViewPanel) tabs.get(getCurrentView()).getPanel(CALENDAR_VIEWS_CONTENT_PANEL_MARKUP_ID);
		return currentViewPanel;
	}

	/**
	 * Sets the currently viewed panel.
	 * @param currentViewPanel The currently viewed panel
	 */
	public void setCurrentViewPanel(CalendarViewPanel currentViewPanel) {
		this.currentViewPanel = currentViewPanel;
	}

	/**
	 * Sets the view to show. It does not actually change the view, see {@code switchView} for that.
	 * @param viewId The ID of the view to show
	 * @param date The date to show
	 * @param target The Ajax target of the panel
	 */
	protected void changeCalendarView(int viewId, Calendar date, AjaxRequestTarget target) {
		// set the current date so the panel will load the correct events period
		currentDate.setTime(date.getTime());

		// set the apporpriate view
		switchView(viewId);
	}

	/**
	 * Change the calendar view.
	 * @param viewId The id of the view to show
	 */
	private void switchView(int viewId) {
		if(viewId > tabs.size() -1 || viewId < 0) {
			throw new WebicalWebAplicationException("The viewId: " + viewId + " exceeds the tabs number: " + tabs.size());
		}

		// update the to new view
		tabbedPanel.setSelectedTab(viewId);
	}

	/**
	 * Handles actions defined by this panel. It passes on actions that aren't in {@code PANELACTIONS}.
	 * @param action The action to handle
	 */
	private void internalOnAction(IAction action) {
		// Filter the actions that can be handled by this panel
		if(Arrays.asList(PANELACTIONS).contains(action.getClass())) {
			// Day Selected
			if(action.getClass().equals(DaySelectedAction.class)) {
				CalendarPanel.this.changeCalendarView(CalendarPanel.DAY_VIEW, ((DaySelectedAction) action).getDaySelected(), null);
			}
			// Week Selected
			else if(action.getClass().equals(WeekSelectedAction.class)) {
				CalendarPanel.this.changeCalendarView(CalendarPanel.WEEK_VIEW, ((WeekSelectedAction) action).getWeekSelected(), null);
			}

		}
		// Pass the onther actions to the parent component
		else {
			CalendarPanel.this.onAction(action);
		}
	}

	/**
	 * Implemented by the parent component to handle actions from this panel.
	 * @param action The action to pass on
	 */
	public abstract void onAction(IAction action);

	/**
	 * Creates the tabs to switch between calendar views
	 */
	@SuppressWarnings("serial")
	protected void createTabs() {
		// DayView Tab
		AbstractTab dayTab = new AbstractTab(new StringResourceModel(CALENDAR_DAY_VIEW_TAB_LABEL, this, null)) {
			@Override
			public Panel getPanel(String markupId) {
				DayViewPanel dayViewPanel = new DayViewPanel(CALENDAR_VIEWS_CONTENT_PANEL_MARKUP_ID, currentDate) {

						@Override
						public void onAction(IAction action) {
							// Route action to internal method first
							CalendarPanel.this.internalOnAction(action);
						}

					};
				// Set the view id to this tab
				CalendarPanel.this.setCurrentView(CalendarPanel.DAY_VIEW);
				CalendarPanel.this.setCurrentViewPanel(dayViewPanel);
				// Return the panel
				return dayViewPanel;
			}
		};

		// WeekView Tab
		AbstractTab weekTab = new AbstractTab( new StringResourceModel(CALENDAR_WEEK_VIEW_TAB_LABEL, this, null)){
			@Override
			public Panel getPanel(String markupId) {
				WeekViewPanel calendarWeekViewPanel = new WeekViewPanel(CALENDAR_VIEWS_CONTENT_PANEL_MARKUP_ID, currentDate, 7) {
						@Override
						public void onAction(IAction action) {
							// Route action to internal method first
							CalendarPanel.this.internalOnAction(action);
						}
					};
				// Set the view id to this tab
				CalendarPanel.this.setCurrentView(CalendarPanel.WEEK_VIEW);
				CalendarPanel.this.setCurrentViewPanel(calendarWeekViewPanel);
				// Return the panel
				return calendarWeekViewPanel;
			}
		};

		// MonthView Tab
		AbstractTab monthTab = new AbstractTab( new StringResourceModel(CALENDAR_MONTH_VIEW_TAB_LABEL, this, null)){
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getPanel(String markupId) {
				MonthViewPanel calendarMonthViewPanel = new MonthViewPanel(CALENDAR_VIEWS_CONTENT_PANEL_MARKUP_ID, currentDate) {
						@Override
						public void onAction(IAction action) {
							// Route action to internal method first
							CalendarPanel.this.internalOnAction(action);
						}
					};
				// Set the view id to this tab
				CalendarPanel.this.setCurrentView(CalendarPanel.MONTH_VIEW);
				CalendarPanel.this.setCurrentViewPanel(calendarMonthViewPanel);
				// Return a new panel
				return calendarMonthViewPanel;
			}
		};

		// AgendaView Tab
		// TODO mattijs: get number of days from user settings
		final int daysToShow = 4;
		AbstractTab agendaTab = new AbstractTab(new Model(new StringResourceModel(CALENDAR_AGENDA_VIEW_TAB_LABEL, this, null).getString() + " " + daysToShow + " " + new StringResourceModel(CALENDAR_XDAYS_VIEW_TAB_LABEL_AFTER, this, null).getString())) {
			@Override
			public Panel getPanel(String markupId) {
				WeekViewPanel calendarAgendaViewPanel = new WeekViewPanel(CALENDAR_VIEWS_CONTENT_PANEL_MARKUP_ID, currentDate, daysToShow) {
						@Override
						public void onAction(IAction action) {
							// Route action to internal method first
							CalendarPanel.this.internalOnAction(action);
						}
					};
				CalendarPanel.this.setCurrentView(CalendarPanel.AGENDA_VIEW);
				CalendarPanel.this.setCurrentViewPanel(calendarAgendaViewPanel);
				// Return a new panel
				return calendarAgendaViewPanel;
			}
		};

		tabs.add(DAY_VIEW, dayTab);
		tabs.add(WEEK_VIEW, weekTab);
		tabs.add(MONTH_VIEW, monthTab);
		tabs.add(AGENDA_VIEW, agendaTab);

	}

	/**
	 * Updates the date selector models.
	 * @see org.webical.web.component.AbstractBasePanel#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		// update the models used by the date selectors before they are rendered
		if(getCurrentViewPanel() == null) {
			setCurrentViewPanel((CalendarViewPanel) tabs.get(getCurrentView()).getPanel(CALENDAR_VIEWS_CONTENT_PANEL_MARKUP_ID));
		}
		dateSwitcherModel = new DateSwitcherModel(currentDate, getCurrentViewPanel());
		datePickerModel = new CompoundPropertyModel(new DatePickerModel(currentDate));
		if(dateSwitcher != null && datePicker != null) {
			dateSwitcher.setModel(dateSwitcherModel);
			datePicker.setModel(datePickerModel);
		}

		// Continue with rendering
		super.onBeforeRender();
	}

	/**
	 * Used by spring to set the Calendar Manager.
	 * @param calendarManager The Calendar Manager
	 */
	public void setCalendarManager(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}

}
