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

package org.webical.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.util.file.File;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.xml.XmlConfiguration;

/**
 *
 * Fires up the application in Jetty
 * @author ivo
 *
 */
public class StartWebApplication {
	private static final String SRC_TEST_RESOURCES_JETTY_XML = "src/test/resources/jetty.xml";
	private static Log log = LogFactory.getLog(StartWebApplication.class);

	/**
	 * Main function, starts the jetty server.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		log.info("Starting up Jetty");
		//System.setProperty("DEBUG", "true");
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(8888);
		server.setConnectors(new Connector[] { connector });
		WebAppContext web = new WebAppContext();

		try {
			XmlConfiguration configuration = new XmlConfiguration(new File(SRC_TEST_RESOURCES_JETTY_XML).toURL());
			configuration.configure(web);
			server.addHandler(web);
			server.start();
			server.join();
		} catch (Exception e) {
			log.error(e,e);
			System.exit(100);
		}
	}
}
