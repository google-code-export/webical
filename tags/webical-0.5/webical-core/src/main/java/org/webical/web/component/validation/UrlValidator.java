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

package org.webical.web.component.validation;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.AbstractValidator;


/**
 * Validates an string input for url correctness.
 * @author ivo
 *
 */
public class UrlValidator extends AbstractValidator implements IValidator {
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor
	 */
	public UrlValidator() {
	}



	/* (non-Javadoc)
	 * @see org.apache.wicket.validation.validator.AbstractValidator#onValidate(org.apache.wicket.validation.IValidatable)
	 */
	protected void onValidate(final IValidatable validatable) {
		if(!(validatable.getValue() instanceof String)) {
			throw new IllegalArgumentException("Can only validate Strings...");
		}
		
		final String urlString = (String)validatable.getValue();
		
		if(urlString == null || urlString.length() == 0) {
			error(validatable);
		}
		
		try {
			new URL(urlString);
		} catch (MalformedURLException e) {
			error(validatable);
		}
	}

}
