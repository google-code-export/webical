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

package org.webical.web.component.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.Event;
import org.webical.manager.CalendarManager;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;
import org.webical.web.action.AddEventAction;
import org.webical.web.action.DaySelectedAction;
import org.webical.web.action.EditEventAction;
import org.webical.web.action.EventSelectedAction;
import org.webical.web.action.FormFinishedAction;
import org.webical.web.action.IAction;
import org.webical.web.action.RemoveEventAction;
import org.webical.web.action.ShowCalendarAction;
import org.webical.web.action.StoreEventAction;
import org.webical.web.action.SwitchCalendarVisibilityAction;
import org.webical.web.action.WeekSelectedAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.calendar.model.DatePickerModel;
import org.webical.web.component.calendar.model.DateSwitcherModel;
import org.webical.web.component.event.EventDetailsPanel;
import org.webical.web.component.event.EventFormPanel;

/**
 * CalendarPanel holds the different calendar views and allows switching between them with tabs. <br />
 * CalendarPanel is also used to share information and save state between the different views, and presents
 * two methods of switching to a different date.
 *
 * @author Mattijs Hoitink
 */
public abstract class CalendarPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(CalendarPanel.class);

	// Markup ID's
	private static final String CALENDAR_DAY_VIEW_TAB_LABEL = "calendar_day_view_tab_label";
	private static final String CALENDAR_WEEK_VIEW_TAB_LABEL = "calendar_week_view_tab_label";
	private static final String CALENDAR_MONTH_VIEW_TAB_LABEL = "calendar_month_view_tab_label";
	private static final String CALENDAR_AGENDA_VIEW_TAB_LABEL = "calendar_xdays_view_tab_label";
	private static final String CALENDAR_XDAYS_VIEW_TAB_LABEL_AFTER = "calendar_xdays_view_tab_label_after";
	private static final String CALENDAR_VIEW_PANEL_MARKUP_ID = "calendarViewPanel";
	private static final String CALENDAR_VIEW_PANEL_CONTENT_MARKUP_ID = "panel";
	private static final String ADD_EVENT_LINK_MARKUP_ID = "addEventLink";
	private static final String DATESWITCHER_MARKUP_ID = "dateSwitcher";
	private static final String DATEPICKER_MARKUP_ID = "datePicker";
	private static final String CALENDARLIST_PANEL_MARKUP_ID = "calendarPanel";

	// ID's for the Calendar Views
	public static final int DAY_VIEW = 0;
	public static final int WEEK_VIEW = 1;
	public static final int MONTH_VIEW = 2;
	public static final int AGENDA_VIEW = 3;

	/** List of actions handled by this panel */
	protected static Class[] PANELACTIONS = new Class[] { DaySelectedAction.class, WeekSelectedAction.class, EventSelectedAction.class, AddEventAction.class, EditEventAction.class, ShowCalendarAction.class, SwitchCalendarVisibilityAction.class, FormFinishedAction.class, StoreEventAction.class, RemoveEventAction.class };

	/** Used by Spring to inject the Calendar Manager. */
	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;

	/** Used by Spring to inject the Event Manager. */
	@SpringBean(name="eventManager")
	private EventManager eventManager;

	private TabbedPanel calendarView;
	private List<AbstractTab> tabs = new ArrayList<AbstractTab>();

	public static boolean enableAddEvent;

	/** The View currently selected. */
	private int currentCalendarView;
	private CalendarViewPanel currentViewPanel;

	/** The selected date. */
	private GregorianCalendar currentDate;

	// Panel components
	private Link addEventLink;
	private DatePickerPanel datePicker;
	private CompoundPropertyModel datePickerModel;
	private DateSwitcherPanel dateSwitcher;
	private DateSwitcherModel dateSwitcherModel;
	private CalendarListPanel calendarListPanel;

	/**
	 * Sets up the base panel for the calendar views.
	 * This panel holds the state between different views.
	 *
	 * @param markupId The ID to use in the markup
	 * @param date The date to display
	 */
	public CalendarPanel(String markupId, final GregorianCalendar date) {
		super(markupId, CalendarPanel.class);
		if (date != null) {
			currentDate = date;
		} else {
			currentDate = new GregorianCalendar();
		}
		currentCalendarView = WebicalSession.getWebicalSession().getUserSettings().getDefaultCalendarView();

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
				CalendarPanel.this.internalOnAction(new AddEventAction(getCurrentDate()));
			}
		};
		add(addEventLink);

		/*
		 * Add a Date Switcher
		 * The Date Switcher model is updated in the onBeforeRender phase
		 */
		dateSwitcher = new DateSwitcherPanel(DATESWITCHER_MARKUP_ID, dateSwitcherModel) {

			@Override
			public void nextPeriod(AjaxRequestTarget target) {
				// Add the view period length of the view period for the current panel
				CalendarPanel.this.getCurrentDate().add(CalendarPanel.this.getCurrentViewPanel().getViewPeriodId(), CalendarPanel.this.getCurrentViewPanel().getViewPeriodLength());

				// reload the current calendar view
				CalendarPanel.this.switchView(getCurrentCalendarView());
			}

			@Override
			public void previousPeriod(AjaxRequestTarget target) {
				// Subtract the view period length of the view period for the current panel
				CalendarPanel.this.getCurrentDate().add(CalendarPanel.this.getCurrentViewPanel().getViewPeriodId(), (CalendarPanel.this.getCurrentViewPanel().getViewPeriodLength()) * -1);

				// reload the current calendar view
				CalendarPanel.this.switchView(getCurrentCalendarView());
			}

			@Override
			public void todaySelected(AjaxRequestTarget target) {
				CalendarPanel.this.changeCalendarView(getCurrentCalendarView(), new GregorianCalendar(), target);
			}
		};
		addOrReplace(dateSwitcher);

		createDatePicker();

		// Get the calendars for this user from the CalendarManager
		List<org.webical.Calendar> userCalendars = new ArrayList<org.webical.Calendar>();
		try {
			userCalendars = calendarManager.getCalendars(((WebicalSession)getSession()).getUser());
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException(e);
		}

		calendarListPanel = new CalendarListPanel(CALENDARLIST_PANEL_MARKUP_ID, userCalendars) {
			private static final long serialVersionUID = 1L;

			@Override
			public void enableAllCalendars(AjaxRequestTarget target) {

			}

			@Override
			public void enableOnlyThisCalendar(org.webical.Calendar calendar, AjaxRequestTarget target) {

			}

			@Override
			public void onAction(IAction action) {
				CalendarPanel.this.internalOnAction(action);
			}
		};
		addOrReplace(calendarListPanel);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		createCalendarView(true);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}

	/**
	 * Sets the view to show. It does not actually change the view, see {@code switchView} for that.
	 * @param viewId The ID of the view to show
	 * @param date The date to show
	 * @param target The Ajax target of the panel
	 */
	private void changeCalendarView(int viewId, GregorianCalendar date, AjaxRequestTarget target) {
		// set the current date so the panel will load the correct events period
		setCurrentDate(date);

		// set the appropriate view
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
		calendarView.setSelectedTab(viewId);
	}

	/**
	 * Handles actions defined by this panel. It passes on actions that aren't in {@code PANELACTIONS}.
	 * @param action The action to handle
	 */
	private void internalOnAction(IAction action) {
		// Filter the actions that can be handled by this panel
		if (Arrays.asList(PANELACTIONS).contains(action.getClass())) {
			// Day Selected
			if (isAction(DaySelectedAction.class, action)) {
				setCurrentDate(((DaySelectedAction) action).getDaySelected());
				setCurrentCalendarView(CalendarPanel.DAY_VIEW);
				this.internalOnAction(new ShowCalendarAction());
			}
			// Week Selected
			else if (action.getClass().equals(WeekSelectedAction.class)) {
				CalendarPanel.this.changeCalendarView(CalendarPanel.WEEK_VIEW, ((WeekSelectedAction) action).getWeekSelected(), null);
			}
			// Show event details
			else if (action.getClass().equals(EventSelectedAction.class)) {
				EventSelectedAction eventSelectedAction = (EventSelectedAction) action;
				EventDetailsPanel contentPanel = new EventDetailsPanel(CALENDAR_VIEW_PANEL_MARKUP_ID, eventSelectedAction.getSelectedEvent(), eventSelectedAction.getSelectedEventDate()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						CalendarPanel.this.internalOnAction(action);
					}
				};
				addOrReplace(contentPanel);
				dateSwitcher.setVisible(false);
			}
			// Add Event
			else if (action.getClass().equals(AddEventAction.class)) {
				EventFormPanel contentPanel = new EventFormPanel(CALENDAR_VIEW_PANEL_MARKUP_ID, null, ((AddEventAction) action).getEventDate()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						CalendarPanel.this.internalOnAction(action);
					}
				};
				addOrReplace(contentPanel);
				dateSwitcher.setVisible(false);
			}
			// Edit event
			else if (action.getClass().equals(EditEventAction.class)) {
				EditEventAction editEventAction = (EditEventAction) action;
				EventFormPanel contentPanel = new EventFormPanel(CALENDAR_VIEW_PANEL_MARKUP_ID, editEventAction.getSelectedEvent(), editEventAction.getSelectedEventDate()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						CalendarPanel.this.internalOnAction(action);
					}
				};
				addOrReplace(contentPanel);
				dateSwitcher.setVisible(false);
			}
			// Show Calendar
			else if (action.getClass().equals(ShowCalendarAction.class)) {
				ShowCalendarAction showCalendarAction = (ShowCalendarAction) action;
				createCalendarView(showCalendarAction.isReloadCalendarView());
				dateSwitcher.setVisible(true);
			}
			// Form Finished
			else if (action.getClass().equals(FormFinishedAction.class)) {
				createCalendarView(false);
				dateSwitcher.setVisible(true);
			}
			// Switch calendar visibility
			else if (action.getClass().equals(SwitchCalendarVisibilityAction.class)) {
				SwitchCalendarVisibilityAction switchCalendarVisibilityAction = (SwitchCalendarVisibilityAction) action;
				try {
					org.webical.Calendar selectedCalendar = switchCalendarVisibilityAction.getCalendar();
					selectedCalendar.setVisible(!selectedCalendar.getVisible());
					calendarManager.storeCalendar(selectedCalendar);
				} catch (WebicalException e) {
					throw new WebicalWebAplicationException("Could not store calendar", e);
				}
				// Create a new ShowCalendarAction to reload the Calendar View
				this.internalOnAction(new ShowCalendarAction(true));
			}
			// Store Event
			else if (isAction(StoreEventAction.class, action)) {
				this.storeEvent((StoreEventAction) action);
			}
			// Remove Event
			else if (isAction(RemoveEventAction.class, action)) {
				this.removeEvent((RemoveEventAction) action);
			}
		}
		// Pass the other actions to the parent component
		else {
			CalendarPanel.this.onAction(action);
		}
	}

	/**
	 * Gets the current date used by the Calendar Views.
	 * @return The current date
	 */
	public GregorianCalendar getCurrentDate() {
		return currentDate;
	}
	/**
	 * Sets the current date for the Calendar Views.
	 * @param currentDate The current date
	 */
	public void setCurrentDate(GregorianCalendar currentDate) {
		// Update the time in the object, don't change the reference else changes are not reflected in the models
		this.currentDate.setTime(currentDate.getTime());
	}

	/**
	 * Gets the ID of current view.
	 * @return The selected view id
	 */
	public int getCurrentCalendarView() {
		return currentCalendarView;
	}
	/**
	 * Sets the ID of the current view.
	 * @param newView The id of the current view
	 */
	public void setCurrentCalendarView(int newView) {
		this.currentCalendarView = newView;
	}

	/**
	 * Gets the panel for the current view.
	 * @return The panel for the current view
	 */
	public CalendarViewPanel getCurrentViewPanel() {
		return currentViewPanel;
	}
	/**
	 * Sets the currently viewed panel.
	 * @param currentViewPanel The currently viewed panel
	 */
	public void setCurrentViewPanel(CalendarViewPanel currentViewPanel) {
		this.currentViewPanel = currentViewPanel;
	}

	private boolean isAction(Class<?> comparisonClass, IAction action) {
		return action.getClass().equals(comparisonClass);
	}

	/**
	 * Creates a tabbed panel for the calendar tabs
	 */
	private void createCalendarView(boolean createNew) {
		if (createNew) {
			calendarView = new TabbedPanel(CALENDAR_VIEW_PANEL_MARKUP_ID, tabs) {
				private static final long serialVersionUID = 1L;
			};
			/*
			 * Set the selected tab immediately before the TabbedPanel is added to the container
			 * to prevent the first tab (day view) being loaded loaded.
			 * If this is not done, the day view will access the DAO and collecting it's events even though the day view isn't rendered.
			 * Recurring Events are not displayed properly if the day view is selected from the default view (assuming this is a
			 * user setting and the default view isn't the day view) and the day view has to be reloaded (once, only the first time)
			 * to show the recurring events.
			 */
			switchView(getCurrentCalendarView());
		}
		// Add the TabbedPanel
		addOrReplace(calendarView);
	}
	
	/**
	 * Creates and adds a DatePicker
	 * The Date Picker model is updated in the onBeforeRender phase
	 */
	private void createDatePicker() {
		datePicker = new DatePickerPanel(DATEPICKER_MARKUP_ID, new CompoundPropertyModel(datePickerModel)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				CalendarPanel.this.internalOnAction(action);
			}
		};
		addOrReplace(datePicker);
	}

	/**
	 * Creates the tabs to switch between calendar views
	 */
	@SuppressWarnings("serial")
	private void createTabs() {
		// DayView Tab
		AbstractTab dayTab = new AbstractTab(new StringResourceModel(CALENDAR_DAY_VIEW_TAB_LABEL, this, null)) {
			@Override
			public Panel getPanel(String markupId) {
				DayViewPanel dayViewPanel = new DayViewPanel(CALENDAR_VIEW_PANEL_CONTENT_MARKUP_ID, 1, getCurrentDate()) {

					@Override
					public void onAction(IAction action) {
						// Route action to internal method first
						CalendarPanel.this.internalOnAction(action);
					}
				};
				// Set the view id to this tab
				CalendarPanel.this.setCurrentCalendarView(CalendarPanel.DAY_VIEW);
				CalendarPanel.this.setCurrentViewPanel(dayViewPanel);
				// Return the panel
				return dayViewPanel;
			}
		};

		// WeekView Tab
		AbstractTab weekTab = new AbstractTab(new StringResourceModel(CALENDAR_WEEK_VIEW_TAB_LABEL, this, null)) {
			@Override
			public Panel getPanel(String markupId) {
				WeekViewPanel calendarWeekViewPanel = new WeekViewPanel(CALENDAR_VIEW_PANEL_CONTENT_MARKUP_ID, 7, getCurrentDate()) {

					@Override
					public void onAction(IAction action) {
						// Route action to internal method first
						CalendarPanel.this.internalOnAction(action);
					}
				};
				// Set the view id to this tab
				CalendarPanel.this.setCurrentCalendarView(CalendarPanel.WEEK_VIEW);
				CalendarPanel.this.setCurrentViewPanel(calendarWeekViewPanel);
				// Return the panel
				return calendarWeekViewPanel;
			}
		};

		// MonthView Tab
		AbstractTab monthTab = new AbstractTab(new StringResourceModel(CALENDAR_MONTH_VIEW_TAB_LABEL, this, null)) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getPanel(String markupId) {
				MonthViewPanel calendarMonthViewPanel = new MonthViewPanel(CALENDAR_VIEW_PANEL_CONTENT_MARKUP_ID, 1, getCurrentDate()) {

					@Override
					public void onAction(IAction action) {
						// Route action to internal method first
						CalendarPanel.this.internalOnAction(action);
					}
				};
				// Set the view id to this tab
				CalendarPanel.this.setCurrentCalendarView(CalendarPanel.MONTH_VIEW);
				CalendarPanel.this.setCurrentViewPanel(calendarMonthViewPanel);
				// Return a new panel
				return calendarMonthViewPanel;
			}
		};

		// AgendaView Tab
		final int daysToShow = WebicalSession.getWebicalSession().getUserSettings().getNumberOfAgendaDays();
		AbstractTab agendaTab = new AbstractTab(new Model(new StringResourceModel(CALENDAR_AGENDA_VIEW_TAB_LABEL, this, null).getString() + " " + daysToShow + " " + new StringResourceModel(CALENDAR_XDAYS_VIEW_TAB_LABEL_AFTER, this, null).getString())) {
			@Override
			public Panel getPanel(String markupId) {
				WeekViewPanel calendarAgendaViewPanel = new WeekViewPanel(CALENDAR_VIEW_PANEL_CONTENT_MARKUP_ID, daysToShow, getCurrentDate()) {
					@Override
					public void onAction(IAction action) {
						// Route action to internal method first
						CalendarPanel.this.internalOnAction(action);
					}
				};
				CalendarPanel.this.setCurrentCalendarView(CalendarPanel.AGENDA_VIEW);
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

	private void storeEvent(StoreEventAction storeEventAction) {
		//Save the event
		try {
			for (Event currentEvent : storeEventAction.getEvents()) {
				eventManager.storeEvent(currentEvent);
			}

			if (!storeEventAction.isJustStore()) {
				// Change back to the calendar
				CalendarPanel.this.internalOnAction(new ShowCalendarAction(true));
			}

		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Event could not be saved", e);
		}
	}

	private void removeEvent(RemoveEventAction action) {
		try {
			eventManager.removeEvent(action.getEventToRemove());
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException(e);
		}
		CalendarPanel.this.internalOnAction(new ShowCalendarAction());
	}

	/**
	 * Enable only the selected calendar in the view for the current user
	 * @param calendar The calendar to show
	 * @param target The Ajax Target of the panel
	 */
	@SuppressWarnings("unused")
	private void enableOnlyThisCalendar(org.webical.Calendar calendar, AjaxRequestTarget target) {
		try {
			calendar.setVisible(true);
			calendarManager.storeCalendar(calendar);

			List<org.webical.Calendar> calendars = calendarManager.getCalendars(WebicalSession.getWebicalSession().getUser());
			if (calendars != null && calendars.size() > 0) {
				for (org.webical.Calendar calendarToDisable : calendars) {
					if (!calendarToDisable.equals(calendar)) {
						calendarToDisable.setVisible(false);
						calendarManager.storeCalendar(calendarToDisable);
					}
				}
			}
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not store calendar", e);
		}

		// Reload the calendar to update the events shown
		this.internalOnAction(new ShowCalendarAction());
	}

	/**
	 * Enable all calendars in the view for the current user
	 * @param target The Ajax Target of the panel
	 */
	@SuppressWarnings("unused")
	private void enableAllCalendars(AjaxRequestTarget target) {
		try {
			List<org.webical.Calendar> calendars = calendarManager.getCalendars(WebicalSession.getWebicalSession().getUser());
			if(calendars != null && calendars.size() > 0) {
				for (org.webical.Calendar calendar : calendars) {
					calendar.setVisible(true);
					calendarManager.storeCalendar(calendar);
				}
			}
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not store calendar", e);
		}
		// Reload the calendar to update the events shown
		this.internalOnAction(new ShowCalendarAction());
	}

	/**
	 * Updates the date selector models.
	 * @see org.webical.web.component.AbstractBasePanel#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		// update the models used by the date selectors before they are rendered
		if (getCurrentViewPanel() == null) {
			setCurrentViewPanel((CalendarViewPanel) tabs.get(getCurrentCalendarView()).getPanel(CALENDAR_VIEW_PANEL_CONTENT_MARKUP_ID));
		}
		dateSwitcherModel = new DateSwitcherModel(getCurrentDate(), getCurrentViewPanel());
		datePickerModel = new CompoundPropertyModel(new DatePickerModel(getCurrentDate(), getCurrentViewPanel()));
		//createDatePicker();
		if (dateSwitcher != null && datePicker != null) {
			dateSwitcher.setModel(dateSwitcherModel);
			datePicker.setModel(datePickerModel);
		}

		// Continue with rendering
		super.onBeforeRender();
	}

	/**
	 * Implemented by the parent component to handle actions from this panel.
	 * @param action The action to pass on
	 */
	public abstract void onAction(IAction action);

	/**
	 * Used by spring to set the Calendar Manager.
	 * @param calendarManager The Calendar Manager
	 */
	public void setCalendarManager(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}

	/**
	 * Used by spring to set the Event Manager.
	 * @param eventManager The Event Manager
	 */
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
}
