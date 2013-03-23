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

import java.text.DateFormat;

import org.apache.wicket.markup.html.basic.Label;
import org.webical.User;
import org.webical.web.app.WebicalSession;

/**
 * This Panel displays the User information
 * @author ivo
 *
 */
public class UserInfoPanel extends AbstractBasePanel {
	private static final String BIRTHDATE_MARKUP_ID = "birthdate";
	private static final String LAST_NAME_MARKUP_ID = "lastName";
	private static final String LAST_NAME_PREFIX_MARKUP_ID = "lastNamePrefix";
	private static final String FIRST_NAME_MARKUP_ID = "firstName";
	private static final String USER_ID_MARKUP_ID = "userId";
	private static final long serialVersionUID = 1L;

	private User user;

	/**
	 * Constructs the panel
	 * @param markupId The ID used in the markup
	 */
	public UserInfoPanel(String markupId) {
		super(markupId, UserInfoPanel.class);
		this.user = WebicalSession.getWebicalSession().getUser();
	}

	public void setupCommonComponents() {
		addOrReplace(new Label(USER_ID_MARKUP_ID, user.getUserId()));
		addOrReplace(new Label(FIRST_NAME_MARKUP_ID, user.getFirstName()));
		addOrReplace(new Label(LAST_NAME_PREFIX_MARKUP_ID, user.getLastNamePrefix()));
		addOrReplace(new Label(LAST_NAME_MARKUP_ID, user.getLastName()));

		String birthDay = "";
		if(user.getBirthDate() != null) {
			DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, getLocale());
			birthDay = dateFormat.format(user.getBirthDate());
		}
		addOrReplace(new Label(BIRTHDATE_MARKUP_ID, birthDay));
	}

	public void setupNonAccessibleComponents() {
		//Not Implemented in this panel
	}

	public void setupAccessibleComponents() {
		//Not Implemented in this panel
	}

}
