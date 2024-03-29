/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    $Id$
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

package org.webical.test.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * TestPage to insert ready to be tested panels on  
 * @author ivo
 *
 */
public class PanelTestPage extends WebPage {

	private static final long serialVersionUID = 1L;

	/** The id used in the markup for the panel */
	public static final String PANEL_MARKUP_ID = "testpanel"; 

	/**
	 * Constructor, takes a panel to add to the page
	 * @param panel the panel to add
	 */
	public PanelTestPage(Panel panel) {
		add(panel);
	}
}
