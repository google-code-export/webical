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

package org.webical.web.component.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.value.IValueMap;

/**
 * Behavior that alters the class of valid and invalid form components. <br>
 * This behavior can be bound to either a Form or a single FormComponent. When bound to a Form, 
 * the behavior will automatically visit all FormComponent children to add its self as a Behavior
 * 
 * @author Mattijs Hoitink
 *
 */
public class FormComponentValidationStyleBehavior extends AbstractBehavior {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_SEPERATOR = " ";
	private static final String COMPONENT_INVALID_CLASS_NAME = "invalid";
	private static final String COMPONENT_VALID_CLASS_NAME = "valid";

	private String invalidClassName = COMPONENT_INVALID_CLASS_NAME;
	private String validClassName = COMPONENT_VALID_CLASS_NAME;
	
	/**
	 * Default Constructor
	 */
	public FormComponentValidationStyleBehavior() {
		super();
	}

	/**
	 * Constructor to overwrite the default invalid and valid class names
	 * @param invalidClassName name of the css class to use when component is invalid
	 * @param validClassName name of the css class to use when component is valid
	 */
	public FormComponentValidationStyleBehavior(String invalidClassName, String validClassName) {
		this();
		this.invalidClassName = invalidClassName;
		this.validClassName = validClassName;
	}
	
	@Override
	public void bind(Component component) {
		
		// Only visit children if we bind this behavior to a form
		if(component instanceof Form) {
			Form form = (Form) component;
			form.visitFormComponents(new FormComponent.AbstractVisitor() {
				@Override
				protected void onFormComponent(FormComponent formComponent) {
					if(!(formComponent instanceof Button)) {
						formComponent.add(new FormComponentValidationStyleBehavior());
					}
				}
			});
		}
	}
	
	/**
	 * Alter the class attribute of the component
	 */
	@Override
	public void onComponentTag(final Component component, final ComponentTag tag) {
		// Only alter the style for form components
		if(component instanceof FormComponent) {
			FormComponent comp = (FormComponent) component;			
			IValueMap attribs = tag.getAttributes();
			String tagClass = "";
			
			if(attribs.containsKey("class")) {
				tagClass = (String) attribs.get("class");
			}
			
			if (comp.isValid() && comp.getConvertedInput() != null) {
				// The component is valid				
				// Remove the invalid class
				tagClass.replace(CLASS_SEPERATOR + invalidClassName, "");
				// add the valid class
				tagClass += CLASS_SEPERATOR + validClassName;
				tag.getAttributes().put("class", tagClass);
			} else if (!comp.isValid()) {
				// The component is invalid				
				// Remove the valid class
				tagClass.replace(CLASS_SEPERATOR + validClassName, "");
				// Add the invalid class
				tagClass += CLASS_SEPERATOR + invalidClassName;
				tag.getAttributes().put("class", tagClass);
			}
		}
	}
	
	
}
