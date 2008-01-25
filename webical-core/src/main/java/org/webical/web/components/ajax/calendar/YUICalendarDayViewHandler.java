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
import org.apache.wicket.markup.html.IHeaderResponse;
import org.webical.web.components.ajax.YUIAjaxHandler;
import org.webical.web.components.ajax.YUIAjaxHandlerUnboundException;

public class YUICalendarDayViewHandler extends YUIAjaxHandler {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(YUICalendarDayViewHandler.class);
	
	/** container handler is attached to. */
	private YUICalendarDayView container;

	public YUICalendarDayViewHandler(String containerId) {
		super(containerId);
	}

	/**
	 * Retrieves the Container component
	 * @see wicket.AjaxHandler#onBind()
	 */
	protected void onBind() {
		if(getComponent() != null) {
			this.container = (YUICalendarDayView)getComponent();
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
		//Add helper functions to header
		String initFunction = 
			"<script type=\"text/javascript\"> \n" +
				"\tfunction createCalendarClickUrl(timestring) { \n" +
					"\t\treturn '" + getCallbackUrl(true)  + "&modus=calendarClick&starttime='+timestring; \n" +
				"\t} \n" +
				
//				"\tfunction createEventClickUrl(e) { \n" +
//					"\t\treturn '" + getCallbackUrl(true, true)  + "&modus=eventClick'; \n" +
//				"\t} \n" +
//				
//				"\tfunction onClickHandlerFunction(e) { \n" +
//					"\t\tif(this.id != \"handleDiv\"){ \n" +
//						"\t\t\tvar wicketPanelURL = createEventClickUrl(e); \n" +
//						"\t\t\twicketAjaxGet(wicketPanelURL, function() { }, function() { }); \n" +
//					"\t\t} \n" +
//				"\t} \n" + 
				
				"\tfunction onEndDragFunction(e, uid, calendarId, starttime) { \n" +
					"\t\tif(this.id != \"handleDiv\"){ \n" +
						"\t\t\tvar wicketPanelURL = createEndDragUrl(e, uid, calendarId, starttime); \n" +
						"\t\t\twicketAjaxGet(wicketPanelURL, function() { }, function() { }); \n" +
					"\t\t} \n" +
				"\t} \n" +	
				
				"\tfunction createEndDragUrl(e, uid, calendarId, starttime) { \n" +
					"\t\treturn '" + getCallbackUrl(true)  + "&modus=enddrag&starttime='+starttime+'&uid='+uid+'&calendarId='+calendarId; \n" +
				"\t} \n" +
			
				"\tfunction onEndResizeFunction(e, uid, calendarId, duration) { \n;" +
					"\t\tvar wicketPanelURL = createEndResizeUrl(e, uid, calendarId, duration); \n" +
					"\t\twicketAjaxGet(wicketPanelURL, function() { }, function() { }); \n" +
				"\t} \n" +	

				"\tfunction createEndResizeUrl(e, uid, calendarId, duration) { \n" +
					"\t\treturn '" + getCallbackUrl(true)  + "&modus=endresize&duration='+duration+'&uid='+uid+'&calendarId='+calendarId; \n" +
				"\t} \n" +				
				
			"</script> \n";

		//Add yui calendarview init function
		initFunction = initFunction + 
			"<script type=\"text/javascript\"> \n" +
				"\tvar slot;\n" +					
				"\tfunction init() { \n" +
				
					"\t\tYAHOO.util.DDM.mode = YAHOO.util.DDM.INTERSECT; \n" + 
	            	"\t\t// slots \n" +
	            	"\t\tslot = new YAHOO.example.DDContainer(\"yuiCalendarDayView\", \"topslots\"); \n" +
	            
					"\t\tvar groupCache = {}; \n"+
					"\t\tvar id = \"yuiCalendarEvent\"; \n"+
					"\t\tif(!groupCache[id]){ \n"+
						"\t\t\tgroupCache[id] = []; \n"+
					"\t\t} \n"+
					"\t\tvar nodes = groupCache[id]; \n"+
					"\t\tfor(var x=0; x<nodes .length; x++){ \n"+
						"\t\t\tif(nodes[x].id != \"\"){ \n"+
							"\t\t\t\tnodes.splice(x, 1); \n"+
							"\t\t\t\tx--; \n"+
						"\t\t\t} \n"+
					"\t\t} \n"+
					"\t\tvar tmpNode = document.getElementById(id); \n"+
					"\t\twhile(tmpNode){ \n"+
						"\t\t\tnodes.push(tmpNode); \n"+
						"\t\t\ttmpNode.id = \"\"; \n"+
						"\t\t\ttmpNode = document.getElementById(id); \n"+
					"\t\t} \n"+
					"\t\tvar scrollElement = document.getElementById('calendarViewPanelContentBlock');\n"+
					"\t\tvar containerHeight = scrollElement.clientHeight;\n"+
					"\t\tfor(var i=0; i<nodes.length; i++){ \n"+
					"\t\t\tnodes[i].setAttribute('id',i); \n" +
					"\t\t\tYAHOO.util.Event.addListener(nodes[i], \"click\", onClickHandlerFunction); \n" +
					"\t\t\tvar dtStart = nodes[i].getElementsByTagName('span')[0].innerHTML; \n"+
					"\t\t\tnodes[i].getElementsByTagName('span')[0].innerHTML = \"\";\n"+
					"\t\t\tvar dtEnd = nodes[i].getElementsByTagName('span')[1].innerHTML; \n"+
					"\t\t\tnodes[i].getElementsByTagName('span')[1].innerHTML = \"\";\n"+
					"\t\t\tvar uid = nodes[i].getElementsByTagName('span')[2].innerHTML; \n"+
					"\t\t\tnodes[i].getElementsByTagName('span')[2].innerHTML = \"\";\n"+
					"\t\t\tvar calendarId = nodes[i].getElementsByTagName('span')[3].innerHTML; \n"+
					"\t\t\tnodes[i].getElementsByTagName('span')[3].innerHTML = \"\";\n"+
					"\t\t\tvar description = nodes[i].getElementsByTagName('span')[4].innerHTML; \n"+
					"\t\t\tnodes[i].getElementsByTagName('span')[4].innerHTML = \"\";\n"+
					"\t\t\tvar handle = nodes[i].getElementsByTagName('div')[1]; \n"+
					"\t\t\thandle.id = handle.id + i; \n"+
					"\t\t\tvar resize = new YAHOO.example.DDResize(nodes[i].id, handle.id,\"topslots\", slot, uid, calendarId);\n"+
					"\t\t\tvar start = nodes[i].parentNode.offsetTop;\n"+
	            	"\t\t\tvar player = new YAHOO.example.DDPlayer(nodes[i].id, \"topslots\", slot, dtStart, dtEnd, start, containerHeight, scrollElement, uid, calendarId, description); \n" +
	            	"\t\t\tplayer.addInvalidHandleId(handle.id); \n" +
					"\t\t\tplayer.onEndDrag = onEndDragFunction; \n" +
					"\t\t\tresize.onEndResize = onEndResizeFunction; \n" +
	            	"\t\t\tslot.addPlayer(player); \n\n" +
					"\t\t} \n"+
					"\t\tslot.paintPlayers(); \n"+
	            	
				"\t} \n" +					
				"\tYAHOO.util.Event.addListener(window, \"load\", init); \n" +
			"</script> \n";
			
		response.renderJavascript(initFunction, "init function");
		response.renderCSSReference("assets/calendardayview.css");
	}
	
}
