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

package org.webical.web.components.ajax.menu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.webical.web.components.ajax.YUIAjaxHandler;
import org.webical.web.components.ajax.YUIAjaxHandlerUnboundException;

/**
 * Handler for YUI menu callbacks
 * @author ivo
 *
 */
public class YUIMenuHandler extends YUIAjaxHandler {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(YUIMenuHandler.class);
	
	//Resources
	private static final String MENU_CSS = "assets/menu.css";
	private static final String CONTAINER_CORE_JS = "container/container_core.js";
	private static final String MENU_JS = "menu.js";
	
	//Associated container
	private YUIContextMenu container;

	/**
	 * @param containerId id used by the containing Component (fixed in wicket 2.0)
	 */
	public YUIMenuHandler(String containerId) {
		super(containerId);
	}
	
	

	@Override
	protected void onBind() {
		if(getComponent() != null) {
			this.container = (YUIContextMenu)getComponent();
			if(log.isDebugEnabled()) {
				log.debug("Attached the handler to component: " + getComponent().getClass());
			}
		} else {
			log.error("Not attached to a component");
			throw new YUIAjaxHandlerUnboundException("Not attached to a component");
		}
	}
	
	/**
	 * Add css and JavaScript header contributions
	 * @see org.apache.wicket.behavior.AbstractAjaxBehavior#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.renderJavascriptReference(new ResourceReference(YUIAjaxHandler.class, CONTAINER_CORE_JS));
		response.renderJavascriptReference(new ResourceReference(YUIAjaxHandler.class, MENU_JS));
		response.renderCSSReference(MENU_CSS);
	}

	/**
	 * Let the container do his thing
	 * @see org.webical.web.components.ajax.YUIAjaxHandler#respond(wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	protected void respond(AjaxRequestTarget target) {
		container.onItemClicked(target);
	}

}
