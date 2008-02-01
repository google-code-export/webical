package org.webical.web.action;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.webical.Calendar;

/**
 * Action to switch the visibility of a calendar in the calendar view.
 * 
 * @author mattijs
 *
 */
public class SwitchCalendarVisibilityAction implements IAction {
	private static final long serialVersionUID = 1L;

	private Calendar calendar;
	private AjaxRequestTarget target;
	
	public SwitchCalendarVisibilityAction(Calendar calendar) {
		this.calendar = calendar;
		this.target = null;
	}
	
	public SwitchCalendarVisibilityAction(Calendar calendar, AjaxRequestTarget target) {
		this.calendar = calendar;
		this.target = target;
	}
	
	public Calendar getCalendar() {
		return this.calendar;
	}
	
	public AjaxRequestTarget getAjaxRequestTarget() {
		return this.target;
	}

}
