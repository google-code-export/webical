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

package org.webical.web.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 * 
 * DateString converter that switches between a date and datetime pattern
 * @author Ivo van Dongen
 *
 */
public class DateStringConverter implements IConverter {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The {@link Locale} used by this converter
	 */
	private Locale locale;
	
	/**
	 * To decide whether the mask is date (true) or datetime (false) 
	 */
	private boolean useDate;
	
	/**
	 * Defaul constructor - sets the converter to Datetime mode instead of Date 
	 */
	public DateStringConverter(){
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.util.convert.IConverter#convertToObject(java.lang.String, java.util.Locale)
	 */
	public Object convertToObject(String value, Locale locale) {
		if(!(value instanceof String) || StringUtils.isEmpty(value)) {
			return null;
		}

		String dateString = (String) value;
		try {
			if(useDate){
				return DateFormat.getDateInstance(DateFormat.SHORT, getLocale()).parse(dateString);
			} else {
				return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, getLocale()).parse(dateString);
			}
		} catch (ParseException e) {
			 throw new ConversionException("'" + value + "' is not a Date"); 
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.util.convert.IConverter#convertToString(java.lang.Object, java.util.Locale)
	 */
	public String convertToString(Object value, Locale locale) {
		if(value == null){
			return null;
		}
		
		Date date = (Date) value;
		
		String result;
		if(useDate){
			result = DateFormat.getDateInstance(DateFormat.SHORT, getLocale()).format(date);
		} else {
			result = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, getLocale()).format(date);
		}			

		return result;	
	}
	
	
	///////////////////////
	// Getters / Setters //
	///////////////////////

	/**
	 * @return the {@link Locale} used by this converter
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the {@link Locale} to be used by this converter
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * @param useDate a boolean specifying to use a date format instead of a datetime format
	 */
	public void setUseDate(boolean useDate){
		this.useDate = useDate;
	}
}
