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

package org.webical.web.listeners;

import java.io.Serializable;
import java.util.GregorianCalendar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.webical.Calendar;
import org.webical.Event;

/**
 * Listener to be passed by the views
 * @author paul
 *
 */
public interface EventSelectionListener extends Serializable{

	public static final int DEFAULT_ALL_EVENTS = 0;
	public static final int EDIT_ALL_EVENTS = 1;
	public static final int EDIT_ONE_EVENT = 2;
	public static final int CANCEL_EDIT_EVENT = 3;
	/**
	 * Called to indicate a event is selected
	 * @param event the selected event
	 * @param edit true if needed to edit the event. Else just show the details
	 * @param editAmout: just edit this single event, of the entire event serie
	 * @param calendar is just edit this event, for which date
	 */
	public void eventSelected(Event event, boolean edit, int editAmount, GregorianCalendar calendar);


	/**
	 * Called to indicate a event is selected used in the ajax view
	 * @param event the selected event
	 * @param edit true if needed to edit the event. Else just show the details
	 * @param editAmout: just edit this single event, of the entire event serie
	 * @param calendar is just edit this event, for which date
	 */
	public void eventSelected(Event event, boolean edit, int editAmount, GregorianCalendar calendar, AjaxRequestTarget target, ModalWindow modalWindow);

	/**
	 * Use when update or insert an event. Show an UpdateAndDeleteExceptionPanel
	 * @param event the selected event
	 * @param calendar calendar used for event
	 * @param isUpdateException indicates if it is a update or delete exception
	 */
	public void eventUpdateandDeleteException(Event event, Calendar calendar, boolean isUpdateException);
}
