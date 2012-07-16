/*
 *    Webical - http://code.google.com/p/webical/
 *    Copyright (C) 2012 by Cebuned
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

package org.webical.web.component.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import org.webical.Calendar;
import org.webical.dao.util.WebDavCalendarSynchronisation;

/**
 * Class CalendarFormValidator validates the Calendar url on the CalendarForm
 *
 * @author Harm-Jan Zwinderman, Cebuned
 *
 */
public class CalendarFormValidator implements IFormValidator
{
	/**
	 * Constructor for a new CalendarFormValidator
	 *
	 * @param urlUsrPwdComponents - the 3 FormComponents url, user, password for the Calendar
	 */
	public CalendarFormValidator(FormComponent calendarURL, FormComponent calendarUser, FormComponent password) {
		m_urlUsrPwd = new FormComponent[3];
		m_urlUsrPwd[0] = calendarURL;
		m_urlUsrPwd[1] = calendarUser;
		m_urlUsrPwd[2] = password;
	}
	public FormComponent[] getDependentFormComponents() {
		return m_urlUsrPwd;
	}
	/**
	 * Validate the Calendar
	 */
	public void validate(Form form) {
		String url = m_urlUsrPwd[0].getValue();
		log.debug(url);
		String usr = m_urlUsrPwd[1].getValue();
		log.debug(usr);
		String pwd = m_urlUsrPwd[2].getValue();
		log.debug(pwd);

		WebDavCalendarSynchronisation wdcs = new WebDavCalendarSynchronisation();
		try {
			Calendar calendar = new Calendar();
			calendar.setUrl(url);
			calendar.setUsername(usr);
			calendar.setPassword(pwd);
			wdcs.getIcal4JCalendarFromRemoteCalendar(calendar);
		}
		catch (Exception exc) {
			log.error(exc);
			m_urlUsrPwd[0].error(exc.getLocalizedMessage());
		}
	}
	protected FormComponent[] m_urlUsrPwd = null;

	private static Log log = LogFactory.getLog(CalendarFormValidator.class);
	public static final long serialVersionUID = 20120525122458L;
}
