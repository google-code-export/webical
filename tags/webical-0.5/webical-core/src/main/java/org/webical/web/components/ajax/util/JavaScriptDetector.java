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

package org.webical.web.components.ajax.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.template.PackagedTextTemplate;

/**
 * JavaScript detector. When javascript is available an Ajax rewuest is send onload
 * @author ivo
 *
 */
public abstract class JavaScriptDetector extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 1L;
	
	private static ResourceReference LOAD_EVENT = new ResourceReference(JavaScriptDetector.class, "addLoadEvent.js");
	
	/* (non-Javadoc)
	 * @see org.apache.wicket.behavior.AbstractAjaxBehavior#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.renderJavascriptReference(LOAD_EVENT);
		
		//Write the callbackscript
		PackagedTextTemplate template = new PackagedTextTemplate(JavaScriptDetector.class, "OnloadCallback.js");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CALLBACK_SCRIPT", getCallbackScript());
		response.renderJavascript(template.asString(params), JavaScriptDetector.class.getName());
	}
	
}
