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
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.webical.web.components.ajax.YUIAjaxHandler;
import org.webical.web.components.ajax.YUIAjaxHandlerUnboundException;

/**
 * The AjaxHandler specific for the calendar
 * @author ivo
 *
 */
public class YUICalendarHandler extends YUIAjaxHandler {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(YUICalendarHandler.class);
	
	//Resources
	private static final String CALENDAR_CSS = "assets/calendar.css";
	private static final String CALENDAR_JS = "calendar.js";
	
	/** container handler is attached to. */
	private YUICalendar container;
	
	/**
	 * Constructor
	 * @param containerId id used by the containing Component (fixed in wicket 2.0)
	 */
	public YUICalendarHandler(String containerId) {
		super(containerId);
	}

	/**
	 * Retrieves the Container component
	 * @see wicket.AjaxHandler#onBind()
	 */
	protected void onBind() {
		if(getComponent() != null) {
			this.container = (YUICalendar)getComponent();
			if(log.isDebugEnabled()) {
				log.debug("Attached the handler to component: " + getComponent().getClass());
			}
		} else {
			log.error("Not attached to a component");
			throw new YUIAjaxHandlerUnboundException("Not attached to a component");
		}
	}

	/**
	 * Informs the YUICalendarView that an event has occured
	 * Gets the resource to render to the requester.
	 * @see wicket.yui.YUIAjaxHandler#respond(wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	protected void respond(AjaxRequestTarget target) {
		log.debug("respond: " + target);
		container.onAjaxCallBack(target);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.wicket.behavior.AbstractAjaxBehavior#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderJavascriptReference(new ResourceReference(YUICalendarHandler.class, CALENDAR_JS));
			
		response.renderCSSReference(CALENDAR_CSS);
	}
	
}