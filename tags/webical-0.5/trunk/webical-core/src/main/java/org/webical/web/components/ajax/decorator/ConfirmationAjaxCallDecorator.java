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

package org.webical.web.components.ajax.decorator;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.util.template.PackagedTextTemplate;

/**
 * Decorates the script with a confirmation
 * @author ivo
 *
 */
public class ConfirmationAjaxCallDecorator extends AjaxCallDecorator {
	private static final String SCRIPT_VARIABLE = "SCRIPT";

	private static final String CONFIRM_MESSAGE_VARIABLE = "CONFIRM_MESSAGE";

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	/**
	 * @param message the message to put in the confirmation
	 */
	public ConfirmationAjaxCallDecorator(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see wicket.ajax.calldecorator.AjaxCallDecorator#decorateScript(java.lang.CharSequence)
	 */
	@Override
	public CharSequence decorateScript(CharSequence script) {
		Map<String, String> variables = new HashMap<String, String>();
		variables.put(CONFIRM_MESSAGE_VARIABLE, message);
		variables.put(SCRIPT_VARIABLE, script.toString());
		return new PackagedTextTemplate(ConfirmationAjaxCallDecorator.class, "confirm.js").asString(variables);
	}
}
