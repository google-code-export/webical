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
import java.util.List;

import org.webical.ical.Recurrence;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Class to wrap a StringResourceModel for recurrence frequency
 * @author ivo
 *
 */
public class RecurrenceFrequentyStringResourceModel extends StringResourceModel{

	private static final long serialVersionUID = 1L;
	
	public static final int POSITION_DAILY = 1;
	public static final int POSITION_WEEKLY = 2;
	public static final int POSITION_MONTHLY = 3;
	public static final int POSITION_YEARLY = 4;
	
	private int frequenty;
	
	public RecurrenceFrequentyStringResourceModel(String resourceKey, Component component, IModel model, int frequenty) {
		super(resourceKey, component, model);
		this.frequenty = frequenty;
	}
	
	public int getFrequenty() {
		return frequenty;
	}

	public void setFrequenty(int frequenty) {
		this.frequenty = frequenty;
	}



	public static List<RecurrenceFrequentyStringResourceModel> createListOfStringResourceModel(Component referencedComponent){
		List<RecurrenceFrequentyStringResourceModel> frequencies = new ArrayList<RecurrenceFrequentyStringResourceModel>();
		
		RecurrenceFrequentyStringResourceModel model = new RecurrenceFrequentyStringResourceModel("repeat.day", referencedComponent, null, Recurrence.DAILY);
		frequencies.add(model);
		model = new RecurrenceFrequentyStringResourceModel("repeat.week", referencedComponent, null, Recurrence.WEEKLY);
		frequencies.add(model);
		model = new RecurrenceFrequentyStringResourceModel("repeat.month", referencedComponent, null, Recurrence.MONTHLY);
		frequencies.add(model);
		model = new RecurrenceFrequentyStringResourceModel("repeat.year", referencedComponent, null, Recurrence.YEARLY);
		frequencies.add(model);
		
		return frequencies;
	}
	
	/**
	 * Finds the right StringResourceModel for this frequency
	 * @param recurFrequency the frequenty
	 * @param modelList the list of RecurrenceFrequentyStringResourceModel to search in
	 * @return The StringResourceModel for this frequency
	 */
	public static RecurrenceFrequentyStringResourceModel getStringResourceModel(int recurFrequency, List<RecurrenceFrequentyStringResourceModel> modelList){
		
		if(recurFrequency > 0 && recurFrequency < modelList.size()) {
			for(RecurrenceFrequentyStringResourceModel frequencyStringModel : modelList){
				if(frequencyStringModel.getFrequenty() == recurFrequency){
					return frequencyStringModel;
				}
			}
		}
		
		return null;
	}
}
