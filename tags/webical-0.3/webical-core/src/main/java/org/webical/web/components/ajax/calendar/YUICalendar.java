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

package org.webical.web.components.ajax.calendar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.ajax.markup.html.WicketAjaxIndicatorAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.template.PackagedTextTemplate;

/**
 * Calendar Component
 * @author ivo
 *
 */
public abstract class YUICalendar extends Panel {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(YUICalendar.class);

	private final WicketAjaxIndicatorAppender indicatorAppender = new WicketAjaxIndicatorAppender();

	//Date formatters (To JavaScript)
	private static final SimpleDateFormat monthYearDateFormat = new SimpleDateFormat("MM/yyyy");
	private static final SimpleDateFormat monthDayYearDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	//Markup ids
	private static final String CALENDAR_CONTAINER_MARKUP_ID = "calendar";
	private static final String JAVASCRIPT_INIT_SCRIPT_MARKUP_ID = "javascriptInitScript";

	//Resources
	private static final String CALENDAR_INIT_JS = "calendarInit.js";
	private static final String CALENDAR_INIT_JS_WITH_CONFIGURATION_MARKUP_ID = "calendarInitWithConfiguration.js";
	private static final String CALENDAR_CSS = "assets/calendar.css";

	//JavaScript template variables
	private static final String BUSY_INDICATOR_ID_VARIABLE = "BUSY_INDICATOR_ID";
	private static final String CALENDAR_ID_VARIABLE = "CALENDAR_ID";
	private static final String CALENDAR_NAMESPACE_VARIABLE = "CALENDAR_NAMESPACE";
	private static final String CALLBACK_URL_VARIABLE = "CALLBACK_URL";
	private static final String MARKUP_ID_VARIABLE = "MARKUP_ID";
	private static final String PAGE_DATE_VARIABLE = "PAGE_DATE";
	private static final String SELECTED_VARIABLE = "SELECTED";
	private static final String START_WEEKDAY_VARIABLE = "START_WEEKDAY";

	//JavaScript template variable values
	private static final String WEBICAL_CALENDAR_VALUE = "webical.calendar";

	//Ajax callback parameters
	private static final String SELECTED_DATE_CALL_PARAM = "selectedDate";

	//Components
	private WebMarkupContainer calendarContainer;

	//The calendars configuration
	private YUICalendarConfiguration calendarConfiguration;

	//The YUICalendarHandler
	private YUICalendarHandler calendarHandler;

	/**
	 * Constructor
	 * @param id the id used for the component
	 * @param calendarConfiguration optional calendar configuration object
	 */
	public YUICalendar(String id, YUICalendarConfiguration calendarConfiguration) {
		super(id);

		this.calendarConfiguration = calendarConfiguration;

		calendarContainer = new WebMarkupContainer(CALENDAR_CONTAINER_MARKUP_ID);
		calendarContainer.setOutputMarkupId(true);

		Label initScript = new Label(JAVASCRIPT_INIT_SCRIPT_MARKUP_ID, new LoadableDetachableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Object load() {
				return getJavaScriptInitFunction(YUICalendar.this.calendarConfiguration);
			}

		});

		initScript.setEscapeModelStrings(false);

		this.add(calendarContainer);
		this.add(initScript);

		//Add Handler
		calendarHandler = new YUICalendarHandler(this.getId());
		this.add(calendarHandler);

		//Add css
		add(HeaderContributor.forCss(YUICalendar.class, CALENDAR_CSS));

		//Add Indicator appender
		add(indicatorAppender);
	}

	/**
	 * Called on an AjaxCallBack
	 * @param target the AjaxRequestTarget
	 */
	public void onAjaxCallBack(AjaxRequestTarget target) {
		String dateString = getRequest().getParameter(SELECTED_DATE_CALL_PARAM);
		if(dateString != null && dateString.length() > 0) {
			String[] dateAsInts = dateString.split(",");
			if(dateAsInts.length == 3) {
				onDateSelected(new GregorianCalendar(Integer.parseInt(dateAsInts[0]), Integer.parseInt(dateAsInts[1]) - 1, Integer.parseInt(dateAsInts[2])), target);
			} else {
				if(log.isDebugEnabled()) {
					log.debug("Incorrect date returned from ajax callback: " + dateString);
				}
				//TODO Throw exception?
			}
		} else {
			if(log.isDebugEnabled()) {
				log.debug("no date returned from ajax callback");
			}
			//TODO Throw exception?
		}


	}

	/**
	 * Implemented by a sublcass to recieve the selected Date
	 * @param selectedDate the selected Date
	 * @param target the AjaxRequestTarget
	 */
	public abstract void onDateSelected(GregorianCalendar selectedDate, AjaxRequestTarget target);

	/**
	 * Creates the initialization script form a packaged template
	 * @return the js as a string
	 */
	private String getJavaScriptInitFunction() {
		Map<String, String> variables = new HashMap<String, String>();
		variables.put(CALENDAR_NAMESPACE_VARIABLE, WEBICAL_CALENDAR_VALUE);
		variables.put(CALENDAR_ID_VARIABLE, getId());
		variables.put(MARKUP_ID_VARIABLE, calendarContainer.getMarkupId());
		variables.put(CALLBACK_URL_VARIABLE, calendarHandler.getCallbackUrl(true).toString());
		variables.put(BUSY_INDICATOR_ID_VARIABLE, indicatorAppender.getMarkupId());

		PackagedTextTemplate template = new PackagedTextTemplate(YUICalendar.class, CALENDAR_INIT_JS);
		return template.asString(variables);
	}

	/**
	 * Creates the initialization script form a packaged template
	 * @param calendarConfiguration the calendar configuration
	 * @return the js as a string
	 */
	private String getJavaScriptInitFunction(YUICalendarConfiguration calendarConfiguration) {
		if(calendarConfiguration == null) {
			return getJavaScriptInitFunction();
		}

		Map<String, String> variables = new HashMap<String, String>();
		variables.put(CALENDAR_NAMESPACE_VARIABLE, WEBICAL_CALENDAR_VALUE);
		variables.put(CALENDAR_ID_VARIABLE, getId());
		variables.put(MARKUP_ID_VARIABLE, calendarContainer.getMarkupId());
		variables.put(PAGE_DATE_VARIABLE, getMonthString(calendarConfiguration.getMonthShown()));
		variables.put(SELECTED_VARIABLE, getSelectedString(calendarConfiguration.getSelectedFrom(), calendarConfiguration.getSelectedTo()));
		variables.put(CALLBACK_URL_VARIABLE, calendarHandler.getCallbackUrl(true).toString());
		variables.put(BUSY_INDICATOR_ID_VARIABLE, indicatorAppender.getMarkupId());
		variables.put(START_WEEKDAY_VARIABLE, getFirstDayOfWeekJSValue());

		PackagedTextTemplate template = new PackagedTextTemplate(YUICalendar.class, CALENDAR_INIT_JS_WITH_CONFIGURATION_MARKUP_ID);
		return template.asString(variables);
	}

	/**
	 * If not set or invalid this is determined by the locale
	 * @return The first day of the week as an int for yui
	 */
	private String getFirstDayOfWeekJSValue() {
		int firstDayOfWeek = calendarConfiguration.getFirstDayOfWeek();
		if(firstDayOfWeek < 0 || firstDayOfWeek > 6) {
			firstDayOfWeek = Calendar.getInstance(getSession().getLocale()).getFirstDayOfWeek();
		}

		return "" + (--firstDayOfWeek);
	}

	/**
	 * Generates the string to pass into js
	 * @param monthDate the date to transfor to the rigth monthYear format
	 * @return the string to pass to js
	 */
	private String getMonthString(Date monthDate) {
		return applyYuiDateStringBugFix(monthYearDateFormat.format(monthDate));
	}

	/**
	 * Generates the string to pass into js (dd/MM/yyyy or dd/MM/yyyy-dd/MM/yyyy)
	 * @param from the from date
	 * @param to the to date or null
	 * @return the string to pass to js
	 */
	private String getSelectedString(Date from, Date to) {
		String selectedString = applyYuiDateStringBugFix(monthDayYearDateFormat.format(from));
		if(to != null) {
			selectedString += "-" + applyYuiDateStringBugFix(monthDayYearDateFormat.format(to));
		}
		return selectedString;
	}

	/**
	 * XXX YUI has a bug; remove the leading zero
	 * @param dateString the String to fix
	 * @return the fixed string (migth be the original)
	 */
	private String applyYuiDateStringBugFix(String dateString) {
		if(dateString == null) {
			return null;
		}
		//XXX YUI has a bug; remove the leading zero
		if(dateString.charAt(0) == '0') {
			return dateString.substring(1);
		} else {
			return dateString;
		}

	}


	/**
	 * Configuration object for the YUICalendar
	 * @author ivo
	 *
	 */
	public static class YUICalendarConfiguration implements Serializable {
		private static final long serialVersionUID = 1L;
		private Date monthShown;
		private Date selectedFrom;
		private Date selectedTo;
		private int firstDayOfWeek = -1;

		/**
		 * @param monthShown the month to show
		 */
		public YUICalendarConfiguration(Date monthShown) {
			super();
			this.monthShown = monthShown;
		}

		/**
		 * @param monthShown the month to show
		 * @param selected the date selected (must be within the shown month)
		 */
		public YUICalendarConfiguration(Date monthShown, Date selected) {
			super();
			this.monthShown = monthShown;
			this.selectedFrom = selected;
		}

		/**
		 * @param monthShown the month to show
		 * @param selectedFrom the first date selected (must be within the shown month and before the selectedTo)
		 * @param selectedTo the last date selected (must be within the shown month and after the selectedFrom)
		 */
		public YUICalendarConfiguration(Date monthShown, Date selectedFrom, Date selectedTo) {
			super();
			this.monthShown = monthShown;
			this.selectedFrom = selectedFrom;
			this.selectedTo = selectedTo;
		}

		public Date getMonthShown() {
			return monthShown;
		}

		public Date getSelectedFrom() {
			return selectedFrom;
		}

		public Date getSelectedTo() {
			return selectedTo;
		}

		@Override
		public String toString() {
			return "monthShown: " + monthShown
					+ ", selectedFrom: " + selectedFrom
					+ ", selectedTo: " + selectedTo;
		}

		/**
		 * @return an int representing the first day of the week.
		 */
		public int getFirstDayOfWeek() {
			return firstDayOfWeek;
		}

		/**
		 * @param firstDayOfWeek the first day of the week 1 for sunday and so on {@link java.util.calendar}
		 */
		public void setFirstDayOfWeek(int firstDayOfMonth) {
			this.firstDayOfWeek = firstDayOfMonth;
		}

	}

}

