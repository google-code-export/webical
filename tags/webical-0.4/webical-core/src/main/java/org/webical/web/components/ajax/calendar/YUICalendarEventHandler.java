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

public class YUICalendarEventHandler extends YUIAjaxHandler {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(YUICalendarEventHandler.class);
	
	private static final ResourceReference YUI_DDPLAYER_JS = new ResourceReference(YUICalendarEventHandler.class, "DDPlayer.js");
	private static final ResourceReference YUI_DDRESIZE_JS = new ResourceReference(YUICalendarEventHandler.class, "DDResize.js");
	
	/** container handler is attached to. */
	private YUICalendarEvent container;

	public YUICalendarEventHandler(String containerId) {
		super(containerId);
	}

	/**
	 * Retrieves the Container component
	 * @see wicket.AjaxHandler#onBind()
	 */
	protected void onBind() {
		if(getComponent() != null) {
			this.container = (YUICalendarEvent)getComponent();
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
		response.renderJavascriptReference(YUI_DDPLAYER_JS);
		response.renderJavascriptReference(YUI_DDRESIZE_JS);
		
		String initFunction = 
			"<script type=\"text/javascript\"> \n" +
				"\tfunction createEventClickUrl(e) { \n" +
					"\t\treturn '" + getCallbackUrl(true)  + "&modus=eventClick'; \n" +
				"\t} \n" +
	
				"\tfunction onClickHandlerFunction(e) { \n" +
					"\t\tif(this.id != \"handleDiv\"){ \n" +
						"\t\t\tvar wicketPanelURL = createEventClickUrl(e); \n" +
						"\t\t\twicketAjaxGet(wicketPanelURL, function() { }, function() { }); \n" +
					"\t\t} \n" +
				"\t} \n" + 
			"</script> \n";
		
		response.renderJavascript(initFunction, "init function");
		
		response.renderCSSReference("assets/calendarevent.css");
	}
	
	/**
	 * One Time per Component header contributions
	 * @see wicket.behavior.AbstractAjaxBehavior#onRenderHeadContribution(wicket.Response)
	 */
/*	
	@Override
	protected void onRenderHeadContribution(Response response) {
		super.onRenderHeadContribution(response);
		
		*//**
		 * Add additional resources
		 * <script type="text/javascript" src="calendar.js"></script> 
		 * <link type="text/css" rel="stylesheet" href="assets/calendarviews.css">
		 *//*
		writeJsReference(response,  YUI_DDPLAYER_JS);
		writeJsReference(response,  YUI_DDRESIZE_JS);
		HeaderContributor.forCss(YUICalendarEventHandler.class, "assets/calendarevent.css").renderHead(response);

		//Add helper functions to header

//		String jsString = 
//			"<script type=\"text/javascript\"> \n" +
//				"function createEndDragUrl(e, starttime) { \n" +
//					"return '" + getCallbackUrl(true, true)  + "&modus=enddrag&starttime='+starttime; \n" +
//				"} \n" +
//			"</script> \n";
//		response.write(jsString);
//		jsString = 
//			"<script type=\"text/javascript\"> \n" +
//				"function createEndResizeUrl(e, duration) { \n" +
//					"return '" + getCallbackUrl(true, true)  + "&modus=endresize&duration='+duration; \n" +
//				"} \n" +
//			"</script> \n";
//		response.write(jsString);

//		//Add yui calendarview init function
//		String initFunction =
//			"<script type=\"text/javascript\"> \n" +
//				"\tvar players; \n" +
//				"\tYAHOO.example.DDApp = function() { \n" +
//				    "\t\treturn { \n" +
					
						"\t\t\tonClickHandlerFunction: function(e) { \n" +
							"\t\t\t\tif(this.id != \"handleDiv\"){ \n" +
	    						"\t\t\t\t\tvar wicketPanelURL = createEventClickUrl(e); \n" +
	    						"\t\t\t\t\twicketAjaxGet(wicketPanelURL, function() { }, function() { }); \n" +
	    					"\t\t\t\t} \n" +
	    				"\t\t\t}, \n" +	
	    				
//						"\t\t\tinit: function() { \n" +
						"var groupCache = {};"+
						"var id = \"yuiCalendarEvent\";"+
						  "if(!groupCache[id]){"+
							"groupCache[id] = [];"+
							"}"+
							"var nodes = groupCache[id];"+
							"for(var x=0; x<nodes .length; x++){"+
						"if(nodes[x].id != \"\"){"+
							"nodes.splice(x, 1);"+
							"x--;"+
						"}"+
							"}"+
							"var tmpNode = document.getElementById(id);"+
						"while(tmpNode){"+
							"nodes.push(tmpNode);"+
						"tmpNode.id = \"\";"+
							"tmpNode = document.getElementById(id);"+
							"}"+

							//"alert( \"Total no. of nodes returned:\"+nodes.length );"+
							"\t\t\t\tYAHOO.util.DDM.mode = YAHOO.util.DDM.INTERSECT; \n" + 
			            	"\t\t\t\t// resize \n" +
			            	"\t\t\t\t//resize = new YAHOO.example.DDResize(\"yuiCalendarEvent\", \"handleDiv\", \"panelresize\", slot); \n" +
			            	"\t\t\t\t// players \n" +
			            	"\t\t\t\tplayers = new YAHOO.example.DDPlayer(\"yuiCalendarEvent\", \"topslots\", slot); \n" +
			            	"\t\t\t\t//players.setXConstraint(0, 0, 202); \n" +
			            	"\t\t\t\t//players.setYConstraint(0, 1104, 4); \n" +
			            	"\t\t\t\t//players.addInvalidHandleId(\"handleDiv\"); \n\n" +
			            	"\t\t\t\tslot.addPlayer(players); \n\n" +
			            	
							"\t\t\t\tplayers.onEndDrag = YAHOO.example.DDApp.onEndDragFunction; \n" +
							"\t\t\t\t//resize.onEndResize = YAHOO.example.DDApp.onEndResizeFunction; \n" +
							"\tYAHOO.util.Event.addListener(\"yuiCalendarEvent\", \"click\", YAHOO.example.DDApp.onClickHandlerFunction); \n" +
//						"\t\t\t} \n" +	
//					"\t\t} \n" +	
//				"\t} (); \n" +					
//
//				"\tYAHOO.util.Event.addListener(window, \"load\", YAHOO.example.DDApp.init); \n" +
//			"</script> \n";
		String initFunction = 
			"<script type=\"text/javascript\"> \n" +
				"\tfunction createEventClickUrl(e) { \n" +
					"\t\treturn '" + getCallbackUrl(true)  + "&modus=eventClick'; \n" +
				"\t} \n" +
	
				"\tfunction onClickHandlerFunction(e) { \n" +
					"\t\tif(this.id != \"handleDiv\"){ \n" +
						"\t\t\tvar wicketPanelURL = createEventClickUrl(e); \n" +
						"\t\t\twicketAjaxGet(wicketPanelURL, function() { }, function() { }); \n" +
					"\t\t} \n" +
				"\t} \n" + 
			"</script> \n";
		
		response.write(initFunction);
	}
*/	
}
