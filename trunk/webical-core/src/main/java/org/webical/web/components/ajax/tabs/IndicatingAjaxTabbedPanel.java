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

package org.webical.web.components.ajax.tabs;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * Adds indicating behavior to the tabbed panel
 * @author ivo
 *
 */
public class IndicatingAjaxTabbedPanel extends AjaxTabbedPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param id the components id
	 * @param tabs the tabs to show
	 */
	public IndicatingAjaxTabbedPanel(String id, List tabs) {
		super(id, tabs);
	}

	/**
	 * Adds an IndicatingAjaxLink
	 * @see wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel#newLink(java.lang.String, int)
	 */
	@Override
	protected WebMarkupContainer newLink(String linkId, final int index) {

		return new IndicatingAjaxLink(linkId) {
			private static final long serialVersionUID = 1L;

			/**
			 * Copied from <code>wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel</code>
			 * @see wicket.ajax.markup.html.AjaxLink#onClick(wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void onClick(AjaxRequestTarget target) {
				setSelectedTab(index);
				
				if(target != null) {
					target.addComponent(IndicatingAjaxTabbedPanel.this);
				}
				
				onAjaxUpdate(target);
			}
			
		};
		
	}
}
