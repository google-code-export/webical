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

package org.webical.web.components.ajax.calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringBufferResourceStream;

public abstract class YUICalendarDayView extends WebMarkupContainer {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(YUICalendarDayView.class);
	
	
	private static final String MODUS_CALL_PARAM = "modus";
	private static final String MODUS_CLICK_CALENDAR = "calendarClick";
	private static final String MODUS_CLICK_EVENT = "eventClick";
	private static final String MODUS_ENDDRAG_EVENT = "enddrag";
	private static final String MODUS_ENDRESIZE_EVENT = "endresize";
	
	private static final String STARTTIME_PARA = "starttime";
	private static final String ENDRESIZE_DURATION_PARA = "duration";
	private static final String UID_PARA = "uid";
	private static final String CALENDARID_PARA = "calendarId";

	public YUICalendarDayView(String id) {
		super(id);
		this.setOutputMarkupId(false);
		
		//Add Handler
		this.add(new YUICalendarDayViewHandler(this.getId()));
	}

	/**
	 * returns the resource stream whose value will become the value of the
	 * data argument in the defined client-side javascript
	 * callback handler.
	 * @return resource stream
	 */
	protected IResourceStream getResponseResourceStream() {
		return new StringBufferResourceStream();
	}
	/**
	 * Called on an AjaxCallBack
	 * @param target the AjaxRequestTarget
	 */
	public void onAjaxCallBack(AjaxRequestTarget target) {
		String modus = getRequest().getParameter(MODUS_CALL_PARAM);
		if(modus != null && modus.length() > 0) {
			
			if(modus.equals(MODUS_CLICK_CALENDAR)) {
				String startTime = getRequest().getParameter(STARTTIME_PARA);
				onClick(target,startTime);
			} else if(modus.equals(MODUS_ENDDRAG_EVENT)) {
				String startTime = getRequest().getParameter(STARTTIME_PARA);
				String uid = getRequest().getParameter(UID_PARA);
				String calendarId = getRequest().getParameter(CALENDARID_PARA);
				onEventEndDrag(uid,calendarId,startTime);
//			} else if (modus.equals(MODUS_CLICK_EVENT)){
//				String uid = getRequest().getParameter(UID_PARA);
//				onEventClicked(uid);
			} else if (modus.equals(MODUS_ENDRESIZE_EVENT)){
				String duration = getRequest().getParameter(ENDRESIZE_DURATION_PARA);
				String uid = getRequest().getParameter(UID_PARA);
				String calendarId = getRequest().getParameter(CALENDARID_PARA);
				onEventEndResize(uid,calendarId,duration);
			} else {
				if(log.isDebugEnabled()) {
					log.debug("Incorrect modus returned from ajax callback: " + modus);
				}
				//TODO Throw exception
			}
		} else {
			if(log.isDebugEnabled()) {
				log.debug("no date returned from ajax callback");
			}
			//TODO Throw exception
		}	
	}
	
	/**
	 * Implemented by a sublcass to recieve the selected Date
	 * @param selectedDate the selected Date
	 */
	public abstract void onClick(AjaxRequestTarget target,String time);
//	public abstract void onEventClicked(String uid);
	public abstract void onEventEndDrag(String uid, String calendarId, String startTime);
	public abstract void onEventEndResize(String uid, String calendarId, String duration);
}

