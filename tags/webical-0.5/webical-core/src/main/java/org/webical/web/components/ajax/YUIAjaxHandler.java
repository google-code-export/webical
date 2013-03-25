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

package org.webical.web.components.ajax;

import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.settings.IDebugSettings;

/**
 * Default YUI AjaxHandler
 * @author ivo
 *
 */
public abstract class YUIAjaxHandler extends AbstractAjaxBehavior {
	
	/**  Borrowed from AbstractDefaultAjaxBehavior */
	
	/** reference to the default ajax support javascript file.*/
	private static final ResourceReference WICKET_JAVASCRIPT = new ResourceReference(AbstractDefaultAjaxBehavior.class, "wicket-ajax.js");
	
	/** reference to the default ajax debug support javascript file. */
	private static final ResourceReference WICKET_JAVASCRIPT_DEBUG_DRAG = new ResourceReference(AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug-drag.js");

	/** reference to the default ajax debug support javascript file. */
	private static final ResourceReference WICKET_JAVASCRIPT_DEBUG = new ResourceReference(AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug.js");
	
	/** YUI js references */
	private static final ResourceReference YUI_YAHOO_JS = new ResourceReference(YUIAjaxHandler.class, "yahoo/yahoo.js");
	private static final ResourceReference YUI_DOM_JS = new ResourceReference(YUIAjaxHandler.class, "dom/dom.js");
	private static final ResourceReference YUI_EVENT_JS = new ResourceReference(YUIAjaxHandler.class, "event/event.js");
	private static final ResourceReference YUI_DRAGDROP_JS = new ResourceReference(YUIAjaxHandler.class, "dragdrop/dragdrop.js");
	private static final ResourceReference YUI_ANIMATION_JS = new ResourceReference(YUIAjaxHandler.class, "animation/animation.js");
	private static final ResourceReference YUI_DDCONTAINER_JS = new ResourceReference(YUIAjaxHandler.class, "calendar/DDContainer.js");
	private static final ResourceReference YUI_DDPLAYER_JS = new ResourceReference(YUIAjaxHandler.class, "calendar/DDPlayer.js");
	private static final ResourceReference YUI_DDRESIZE_JS = new ResourceReference(YUIAjaxHandler.class, "calendar/DDResize.js");

	private String containerId;
	
	/**
	 * Construct.
	 * @param containerId the id of the container this handler is attached to (in wicket 2.0 this should be fixed)
	 */
	public YUIAjaxHandler(String containerId) {
		this.containerId = containerId;
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.behavior.AbstractAjaxBehavior#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderJavascriptReference(YUI_YAHOO_JS);
		response.renderJavascriptReference(YUI_DOM_JS);
		response.renderJavascriptReference(YUI_EVENT_JS);
		response.renderJavascriptReference(YUI_DRAGDROP_JS);
		response.renderJavascriptReference(YUI_ANIMATION_JS);
		response.renderJavascriptReference(YUI_DDCONTAINER_JS);
		response.renderJavascriptReference(YUI_DDPLAYER_JS);
		response.renderJavascriptReference(YUI_DDRESIZE_JS);
		response.renderJavascriptReference(WICKET_JAVASCRIPT);
		
		//Enable debug?
		final IDebugSettings settings = Application.get().getDebugSettings();
		if (settings.isAjaxDebugModeEnabled()) {
			response.renderJavascript(
					"\n<script type=\"text/javascript\" >" +
						"\n wicketAjaxDebugEnable=true; \n " +
					"</script>\n", "debug scripts");
			response.renderJavascriptReference(WICKET_JAVASCRIPT_DEBUG_DRAG);
			response.renderJavascriptReference(WICKET_JAVASCRIPT_DEBUG);
		}
	}
		
	/**
	 * Sets up the RequestTarget
	 * @see wicket.behavior.IBehaviorListener#onRequest()
	 */
	public void onRequest() {
		boolean isPageVersioned = true;
		try {
			isPageVersioned = getComponent().getPage().isVersioned();
			getComponent().getPage().setVersioned(false);

			//AjaxRequestTarget target = new AjaxRequestTarget();
			AjaxRequestTarget target = null;
			RequestCycle.get().setRequestTarget(target);
			
			respond(target);
		}  finally  {
			getComponent().getPage().setVersioned(isPageVersioned);
		}
	}
	
	
	/**
	 * Implement to get onResponse notification
	 * @param target
	 */
	protected abstract void respond(AjaxRequestTarget target);

	//Getters/Setters
	
	/**
	 * @return the containers MarkupId
	 */
	public String getContainerId() {
		return containerId;
	}

	/**
	 * @param containerId the containers MarkupId
	 */
	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}
}
