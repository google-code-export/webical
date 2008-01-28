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

package org.webical.web.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.Calendar;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;
import org.webical.web.action.AddEventAction;
import org.webical.web.action.CalendarSelectedAction;
import org.webical.web.action.EditEventAction;
import org.webical.web.action.EventSelectedAction;
import org.webical.web.action.FormFinishedAction;
import org.webical.web.action.IAction;
import org.webical.web.action.ShowCalendarAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.HeaderPanel;
import org.webical.web.component.UserInfoPanel;
import org.webical.web.component.calendar.CalendarAddEditPanel;
import org.webical.web.component.calendar.CalendarListPanel;
import org.webical.web.component.calendar.CalendarPanel;
import org.webical.web.component.event.EventAddEditPanel;
import org.webical.web.component.event.EventDetailsViewPanel;
import org.webical.web.component.settings.SettingsPanelsPanel;

/**
 * The one and only page. All content is displayed in the content panel
 *
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 */
public class BasePage extends AbstractBasePage {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(BasePage.class);

	// Markup ID's
	private static final String HEADER_PANEL_MARKUP_ID = "headerPanel";
	private static final String CONTENT_PANEL_MARKUP_ID = "contentPanel";
	private static final String CALENDARLIST_PANEL_MARKUP_ID = "calendarPanel";

	//Page Parameters
	public static final String CONTENT_PANEL_PARAMETER = "contentpanel";

	//Page Parameters values
	public static final String SETTINGS_PANEL_PARAMETER_VALUE = "settingspanel";
	public static final String CALENDAR_PANEL_PARAMETER_VALUE = "calendarpanel";

	// Page identifiers
	/**
	 * Default value; use to indicate the panel is unset
	 */
	public static final int NOT_SET = -1;

	/**
	 * Identifier for the CalendarEditPanel
	 */
	public final static int CALENDAR_ADD_PANEL = 1;

	/**
	 * Identifier for the SettingsPanelsPanel
	 */
	public final static int SETTINGS_PANELS_PANEL = 2;

	/**
	 * Identifier for the UserInfoPanel
	 */
	public final static int USER_INFO_PANEL = 3;

	/**
	 * Identifier for the CalendarViewsPanel
	 */
	public final static int CALENDAR_PANEL = 4;

	/**
	 * Identifier for the EventAddEditPanel
	 */
	public final static int EVENT_ADD_PANEL = 5;

	/**
	 *  Identifier for the CalendarDayViewPanel
	 */
	public final static int CALENDAR_DAY_VIEW_PANEL = 6;

	/**
	 * Identifier for the EventDetailsPanel
	 */
	public final static int EVENT_DETAILS_PANEL = 7;

	/**
	 * Identifier for the first panel to show when logged in
	 */
	private int startPanel = NOT_SET;

	/**
	 * List of actions handled by this panel
	 */
	@SuppressWarnings("unchecked")
	protected static Class[] PANELACTIONS = new Class[] { ShowCalendarAction.class, EventSelectedAction.class, AddEventAction.class, EditEventAction.class, FormFinishedAction.class, CalendarSelectedAction.class };

	// Page panels
	/** Panel to use for the different calendar views */
	private CalendarPanel calendarPanel;

	/** The HeaderPanel containing the calendar, settings and logout tabs */
	private HeaderPanel headerPanel;

	/** CalendarListPanel with a list of calendars accessible to the user */
	private CalendarListPanel calendarListPanel;

	/** Current Content Panel */
	private Panel contentPanel;

	/**
	 * Previous Panel
	 * This is stored so forms can find their way back to the previous panel
	 */
	private Panel previousContent;

	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;

	private GregorianCalendar currentDate = new GregorianCalendar();

	/**
	 * Default constructor
	 */
	public BasePage() {
		super(BasePage.class);
	}

	/**
	 * Constructor to point to a specific content panel
	 */
	public BasePage(PageParameters parameters) {
		super(BasePage.class);
		String panel = parameters.getString(CONTENT_PANEL_PARAMETER);

		if(panel != null) {
			if(panel.equals(CALENDAR_PANEL_PARAMETER_VALUE)) {
				startPanel = BasePage.CALENDAR_PANEL;
			} else if(panel.equals(SETTINGS_PANEL_PARAMETER_VALUE)) {
				startPanel = BasePage.SETTINGS_PANELS_PANEL;
			}
		}
	}

	public void setupCommonComponents() {
		// Create the Header Panel
		headerPanel = new HeaderPanel(HEADER_PANEL_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.webical.web.components.HeaderPanel#changeContentPanel(int, org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void changeContent(int panelId, AjaxRequestTarget target) {
				BasePage.this.setContent(panelId, target);
			}

		};
		addOrReplace(headerPanel);

		// Get the calendars for thsi user from the CalendarManager
		List<Calendar> userCalendars = new ArrayList<Calendar>();
		try {
			userCalendars = calendarManager.getCalendars(((WebicalSession)getSession()).getUser());
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException(e);
		}

		// Create the Calendar List Panel
		calendarListPanel = new CalendarListPanel(CALENDARLIST_PANEL_MARKUP_ID, userCalendars) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.webical.web.components.CalendarListPanel#selectCalendarForEdit(Calendar, org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void selectCalendarForEdit(Calendar calendar, AjaxRequestTarget target) {
				BasePage.this.selectCalendarForEdit(calendar, target);
			}

			/* (non-Javadoc)
			 * @see org.webical.web.components.CalendarListPanel#switchCalendarVisibility(Calendar, org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void switchCalendarVisibility(Calendar calendar, AjaxRequestTarget target) {
				BasePage.this.switchCalendarVisibility(calendar, target);
			}

			/* (non-Javadoc)
			 * @see org.webical.web.components.CalendarListPanel#enableAllCalendars(org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void enableAllCalendars(AjaxRequestTarget target) {
				BasePage.this.enableAllCalendars(target);
			}

			/* (non-Javadoc)
			 * @see org.webical.web.components.CalendarListPanel#enableOnlyThisCalendar(Calendar, org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void enableOnlyThisCalendar(Calendar calendar, AjaxRequestTarget target) {
				BasePage.this.enableOnlyThisCalendar(calendar, target);
			}


		};
		addOrReplace(calendarListPanel);

		//Decide on the startPanel
		if(startPanel != BasePage.NOT_SET) {
			//Set to the requested panel
			this.setContent(startPanel, null);
		} else {
			if(userCalendars.size() > 0) {
				//Set the calendar views panel as the first panel
				setContent(BasePage.CALENDAR_PANEL, null);
			} else {
				//Set the SettingsPanel as the first panel
				setContent(BasePage.SETTINGS_PANELS_PANEL, null);
			}
		}

	}

	public void setupAccessibleComponents() {
		//NOT IMPLEMENTED IN THIS PAGE
	}

	public void setupNonAccessibleComponents() {
		//NOT IMPLEMENTED IN THIS PAGE
	}

	/**
	 * Method for handling webical actions
	 * @param action The action to handle
	 */
	public void finalOnAction(IAction action) {
		if(Arrays.asList(PANELACTIONS).contains(action.getClass())) {

			// Show Calendar
			if(action.getClass().equals(ShowCalendarAction.class)) {
				this.setContent(CALENDAR_PANEL, ((ShowCalendarAction) action).getTarget());
			}
			// Event selected
			else if(action.getClass().equals(EventSelectedAction.class)) {
				contentPanel = new EventDetailsViewPanel(CONTENT_PANEL_MARKUP_ID, ((EventSelectedAction) action).getSelectedEvent()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						BasePage.this.finalOnAction(action);
					}

				};
				setContent(contentPanel, null);
			}
			// Add Event
			else if(action.getClass().equals(AddEventAction.class)) {
				contentPanel = new EventAddEditPanel(CONTENT_PANEL_MARKUP_ID, null, false, ((AddEventAction) action).getEventDate(), null, "") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						BasePage.this.finalOnAction(action);
					}

				};
				setContent(contentPanel, null);
			}
			// Edit event
			else if(action.getClass().equals(EditEventAction.class)) {
				EditEventAction editEventAction = (EditEventAction) action;
				contentPanel = new EventAddEditPanel(CONTENT_PANEL_MARKUP_ID, editEventAction.getSelectedEvent(), false, new GregorianCalendar(), null, "") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						BasePage.this.finalOnAction(action);
					}

				};
				setContent(contentPanel, null);
			}
			// Form Finished
			else if(action.getClass().equals(FormFinishedAction.class)) {
				BasePage.this.loadPreviousContent(action.getAjaxRequestTarget());
			}
			// Calendar Selected
			else if(action.getClass().equals(CalendarSelectedAction.class)) {
				CalendarSelectedAction calendarSelectedAction = (CalendarSelectedAction) action;
				SettingsPanelsPanel settingsPanel = new SettingsPanelsPanel(CONTENT_PANEL_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						BasePage.this.finalOnAction(action);
					}
				};

				Object args[] = { calendarSelectedAction.getSelectedCalendar() };
				settingsPanel.setSelectedTab(SettingsPanelsPanel.CALENDAR_SETTINGS_TAB_INDEX, args);
				setContent(settingsPanel, calendarSelectedAction.getAjaxRequestTarget());
			}

		} else {
			log.error("Action " + action + " does not exist!");
			throw new WebicalWebAplicationException("Action " + action + " does not exist!");
		}

	}

	/**
	 * Sets the content for the contentPanel.
	 *
	 * This will must slowly be replaced by finalOnAction because all content changes will
	 * be directed through actions.
	 *
	 * @param panelId The ID of the panel to use as the new content
	 * @param target The Ajax Target of the panel
	 */
	private void setContent(int panelId, AjaxRequestTarget target) {
		switch (panelId) {
			case CALENDAR_ADD_PANEL:
				contentPanel = new CalendarAddEditPanel(CONTENT_PANEL_MARKUP_ID, null) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						BasePage.this.finalOnAction(action);
					}
				};
			break;

			case SETTINGS_PANELS_PANEL:
				contentPanel = new SettingsPanelsPanel(CONTENT_PANEL_MARKUP_ID) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						BasePage.this.finalOnAction(action);
					}
				};
			break;

			case USER_INFO_PANEL:
				contentPanel = new UserInfoPanel(CONTENT_PANEL_MARKUP_ID);
			break;

			case CALENDAR_PANEL:
				//Save the panel in the Session to preserve the rigth state
				if(this.calendarPanel == null) {
					this.calendarPanel = new CalendarPanel(CONTENT_PANEL_MARKUP_ID, new GregorianCalendar()) {
						private static final long serialVersionUID = 1L;
						@Override
						public void onAction(IAction action) {
							BasePage.this.finalOnAction(action);
						}
					};
				}
				contentPanel = calendarPanel;
			break;

			case EVENT_ADD_PANEL:
				contentPanel = new EventAddEditPanel(CONTENT_PANEL_MARKUP_ID, null, false, currentDate, null, null) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) {
						BasePage.this.finalOnAction(action);
					}
				};
			break;

			case EVENT_DETAILS_PANEL:
				//contentPanel = new EventDetailsViewPanel(CONTENT_PANEL_MARKUP_ID, )
			break;

			default:
				throw new WebicalWebAplicationException("Unknown panelId passed to ContentPanelChangeListener");

		}
		setContent(contentPanel, target);
	}

	/**
	 * Sets the content for the contentPanel
	 * @param newContentPanel The Panel to use as the new content
	 * @param target The Ajax target of the panel
	 */
	private void setContent(Panel newcontentPanel, AjaxRequestTarget target) {
		storePreviousContentPanel((Panel) BasePage.this.get("contentPanel"));
		changePanel(newcontentPanel, target);
	}

	/**
	 * Replaces the contentPanel with new content
	 * @param target the ajax target
	 */
	private void changePanel(Panel contentPanel, AjaxRequestTarget target) {
		BasePage.this.addOrReplace(contentPanel);
		if(target != null) {
			contentPanel.setOutputMarkupId(true);
			target.addComponent(contentPanel);
		}
	}

	/**
	 * Sets the previous panel for use when returning from a form
	 */
	private void storePreviousContentPanel(Panel currentContentPanel) {
		previousContent = currentContentPanel;
	}

	/**
	 * Return to the previous content panel
	 * @param target The Ajax target of the panel
	 */
	private void loadPreviousContent(AjaxRequestTarget target) {
		setContent(previousContent, target);
	}

	/**
	 * Select a calendar for editing
	 * @param calendar The calendar to edit.
	 * @param target The Ajax Target of the panel
	 */
	private void selectCalendarForEdit(Calendar calendar, AjaxRequestTarget target) {
		SettingsPanelsPanel spp = new SettingsPanelsPanel(CONTENT_PANEL_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				BasePage.this.finalOnAction(action);
			}
		};

		Object args[] = { calendar };
		spp.setSelectedTab(SettingsPanelsPanel.CALENDAR_SETTINGS_TAB_INDEX, args);
		BasePage.this.setContent(spp, null);

		if(target != null) {
			target.addComponent(BasePage.this.get(BasePage.CONTENT_PANEL_MARKUP_ID));
		}
	}

	/**
	 * Switch the visibility of a calendar
	 * @param calendar The calendar to swich
	 * @param target The Ajax Target of the panel
	 */
	private void switchCalendarVisibility(Calendar calendar, AjaxRequestTarget target) {
		try {
			calendar.setVisible(!calendar.getVisible());
			calendarManager.storeCalendar(calendar);
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not store calendar", e);
		}

		if(target != null) {
			target.addComponent(BasePage.this.get(BasePage.CONTENT_PANEL_MARKUP_ID));
			target.addComponent(BasePage.this.get(BasePage.CALENDARLIST_PANEL_MARKUP_ID));
		}
		// TODO mattijs: reload calendar panel here
	}

	/**
	 * Enable only the selected calendar in the view for the current user
	 * @param calendar The calendar to show
	 * @param target The Ajax Target of the panel
	 */
	private void enableOnlyThisCalendar(Calendar calendar, AjaxRequestTarget target) {
		try {
			calendar.setVisible(true);
			calendarManager.storeCalendar(calendar);

			List<Calendar> calendars = calendarManager.getCalendars(WebicalSession.getWebicalSession().getUser());
			if(calendars != null && calendars.size() > 0) {
				for (Calendar calendarToDisable : calendars) {
					if(!calendarToDisable.equals(calendar)) {
						calendarToDisable.setVisible(false);
						calendarManager.storeCalendar(calendarToDisable);
					}
				}
			}
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not store calendar", e);
		}

		if(target != null) {
			target.addComponent(BasePage.this.get(BasePage.CONTENT_PANEL_MARKUP_ID));
			target.addComponent(BasePage.this.get(BasePage.CALENDARLIST_PANEL_MARKUP_ID));
		}
		// TODO mattijs: reload calendar panel here
	}

	/**
	 * Enable all calendars in the view for the current user
	 * @param target The Ajax Target of the panel
	 */
	private void enableAllCalendars(AjaxRequestTarget target) {
		try {
			List<Calendar> calendars = calendarManager.getCalendars(WebicalSession.getWebicalSession().getUser());
			if(calendars != null && calendars.size() > 0) {
				for (Calendar calendar : calendars) {
					calendar.setVisible(true);
					calendarManager.storeCalendar(calendar);
				}
			}
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not store calendar", e);
		}

		if(target != null) {
			target.addComponent(BasePage.this.get(BasePage.CONTENT_PANEL_MARKUP_ID));
			target.addComponent(BasePage.this.get(BasePage.CALENDARLIST_PANEL_MARKUP_ID));
		}
		// TODO mattijs: reload calendar panel here
	}

	/**
	 * Passed to the panels that need to inform that a event is selected (The eventDetailsViewPanel)
	 * @return a EventSelectionListener implementation
	 */
	// TODO mattijs: Integrate this listener with the above methods
	/*private EventSelectionListener getEventSelectionListener() {
		return new EventSelectionListener() {
			private static final long serialVersionUID = 1L;

			*//**
			 * (non-Javadoc)
			 * @see org.webical.web.listeners.EventSelectionListener#eventSelected(org.webical.Event)
			 *//*
			public void eventSelected(Event event, boolean edit, int editAmount, GregorianCalendar calendar) {
				if(!edit) {
					BasePage.this.replace(new EventDetailsViewPanel(BasePage.CONTENT_PANEL_MARKUP_ID, event) {
						private static final long serialVersionUID = 1L;

						@Override
						public void changeContent(int panelId, AjaxRequestTarget target) {
							BasePage.this.setContent(panelId, target);
						}

					});
				} else {
					if(event.getrRule().size() == 0) {
						BasePage.this.replace(new EventAddEditPanel(CONTENT_PANEL_MARKUP_ID, event, false, new GregorianCalendar(), getEventSelectionListener(), null){
							private static final long serialVersionUID = 1L;

							@Override
							public void formFinished(AjaxRequestTarget target) {
								BasePage.this.loadPreviousContent(target);
							}

						});
					} else if (editAmount == EventSelectionListener.DEFAULT_ALL_EVENTS){
						BasePage.this.replace(new EditAllOrOneEventDecisionPanel(BasePage.CONTENT_PANEL_MARKUP_ID, event, getEventSelectionListener(), calendar, null) {
							private static final long serialVersionUID = 1L;

							@Override
							public void changeContent(int panelId, AjaxRequestTarget target) {
								BasePage.this.setContent(panelId, target);
							}

						});
					} else if (editAmount == EventSelectionListener.EDIT_ALL_EVENTS){
						BasePage.this.replace(new EventAddEditPanel(CONTENT_PANEL_MARKUP_ID, event, false, new GregorianCalendar(), getEventSelectionListener(), null){
							private static final long serialVersionUID = 1L;

							@Override
							public void formFinished(AjaxRequestTarget target) {
								BasePage.this.loadPreviousContent(target);
							}

						});
					} else if (editAmount == EventSelectionListener.EDIT_ONE_EVENT){
						BasePage.this.replace(new EventAddEditPanel(CONTENT_PANEL_MARKUP_ID, event, true, calendar, getEventSelectionListener(), null){
							private static final long serialVersionUID = 1L;

							@Override
							public void formFinished(AjaxRequestTarget target) {
								BasePage.this.loadPreviousContent(target);
							}

						});
					}
				}
			}

			public void eventSelected(Event event, boolean edit, int editAmount, GregorianCalendar calendar, AjaxRequestTarget target, ModalWindow modalWindow) {


				if(!edit){
					modalWindow.setContent(new EventDetailsViewPanel(modalWindow.getContentId(), event) {
						private static final long serialVersionUID = 1L;

						@Override
						public void changeContent(int panelId, AjaxRequestTarget target) {
							BasePage.this.setContent(panelId, target);
						}

					});
					modalWindow.show(target);
				}else {
					if(event.getrRule().size() == 0){
						modalWindow.setContent(new EventAddEditPanel(modalWindow.getContentId(),event, false, new GregorianCalendar(), getEventSelectionListener(), null){
							private static final long serialVersionUID = 1L;

							@Override
							public void formFinished(AjaxRequestTarget target) {
								BasePage.this.loadPreviousContent(target);
							}

						});
						modalWindow.show(target);
					} else if (editAmount == EventSelectionListener.DEFAULT_ALL_EVENTS){
						modalWindow.setContent(new EditAllOrOneEventDecisionPanel(modalWindow.getContentId(), event, getEventSelectionListener(), calendar, modalWindow) {
							private static final long serialVersionUID = 1L;

							@Override
							public void changeContent(int panelId, AjaxRequestTarget target) {
								BasePage.this.setContent(panelId, target);
							}

						});
						modalWindow.show(target);
					} else if (editAmount == EventSelectionListener.EDIT_ALL_EVENTS){
						modalWindow.setContent(new EventAddEditPanel(modalWindow.getContentId(), event, false, new GregorianCalendar(), getEventSelectionListener(), null){
							private static final long serialVersionUID = 1L;

							@Override
							public void formFinished(AjaxRequestTarget target) {
								BasePage.this.loadPreviousContent(target);
							}

						});
						modalWindow.show(target);
					} else if (editAmount == EventSelectionListener.EDIT_ONE_EVENT){
						modalWindow.setContent(new EventAddEditPanel(modalWindow.getContentId(), event, true, calendar, getEventSelectionListener(), null){
							private static final long serialVersionUID = 1L;

							@Override
							public void formFinished(AjaxRequestTarget target) {
								BasePage.this.loadPreviousContent(target);
							}

						});
						modalWindow.show(target);
					}
				}
			}

			public void eventUpdateandDeleteException(Event event, Calendar calendar, boolean isUpdateException) {
				BasePage.this.replace(new UpdateAndDeleteExceptionPanel(CONTENT_PANEL_MARKUP_ID, event, calendar, isUpdateException) {
					private static final long serialVersionUID = 1L;

					@Override
					public void changeContent(int panelId, AjaxRequestTarget target) {
						BasePage.this.setContent(panelId, target);
					}

				});
			}
		};
	}*/

	public void setCalendarManager(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}

}
