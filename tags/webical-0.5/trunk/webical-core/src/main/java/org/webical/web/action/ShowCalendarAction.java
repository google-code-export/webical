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

package org.webical.web.action;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class ShowCalendarAction implements IAction {
	private static final long serialVersionUID = 1L;

	private AjaxRequestTarget target;
	
	/**
	 * Should the calendar be reloaded to update the events shown. Default is true.
	 */
	private boolean reloadCalendarView;

	public ShowCalendarAction() {
		this(true, null);
	}

	public ShowCalendarAction(boolean reloadCalendar) {
		this(reloadCalendar, null);
	}

	public ShowCalendarAction(AjaxRequestTarget target) {
		this(true, target);
	}

	public ShowCalendarAction(boolean reloadCalenarView, AjaxRequestTarget target) {
		this.reloadCalendarView = reloadCalenarView;
		this.target = target;
	}

	public boolean isReloadCalendarView() {
		return reloadCalendarView;
	}

	public void setReloadCalendarView(boolean reloadCalendarView) {
		this.reloadCalendarView = reloadCalendarView;
	}

	public AjaxRequestTarget getAjaxRequestTarget() {
		return target;
	}

}
