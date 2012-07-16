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

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.webical.web.action.DaySelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.calendar.model.DatePickerModel;

public abstract class DatePickerColumnPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	private static String OTHER_MONTH_CSS_CLASS = "otherMonth";

	protected static Class[] PANELACTIONS = new Class[] { };

	private DatePickerModel datePickerModel;
	private GregorianCalendar currentDate;
	private GregorianCalendar dayCalendar;

	private Label dateLabel;

	public DatePickerColumnPanel(String markupId, GregorianCalendar dayCalendar, DatePickerModel datePickerModel) {
		super(markupId, DatePickerColumnPanel.class);

		this.datePickerModel = datePickerModel;
		this.currentDate = new GregorianCalendar();
		this.currentDate.setTime(datePickerModel.getCurrentDate().getTime());
		this.dayCalendar = new GregorianCalendar();
		this.dayCalendar.setTime(dayCalendar.getTime());
	}

	public void setupCommonComponents() {
	}

	public void setupAccessibleComponents() {

		SimpleDateFormat dateFormatter = new SimpleDateFormat("d", getLocale());

		// Create the date label
		dateLabel = new Label("dateLabel", "" + dateFormatter.format(this.dayCalendar.getTime()));

		// Create the date link
		Link dateLink = new Link("dateLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				DatePickerColumnPanel.this.onAction(new DaySelectedAction(dayCalendar));
			}
		};

		if (dayCalendar.get(GregorianCalendar.MONTH) != currentDate.get(GregorianCalendar.MONTH)) {
			dateLink.add(new AttributeAppender("class", true, new Model(OTHER_MONTH_CSS_CLASS), " "));
		}

		dateLink.add(dateLabel);

		addOrReplace(dateLink);
	}

	public void setupNonAccessibleComponents() {
		/* NOTHING TO DO */
	}

	/**
	 * Handle actions generated by this panel.
	 *
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);
}
