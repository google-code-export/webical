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

package org.webical.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.util.file.File;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.xml.XmlConfiguration;

import org.webical.plugin.file.FileUtils;

/**
 *
 * Fires up the application in Jetty
 * @author ivo
 * @author Harm-Jan, Cebuned
 *
 */
public class StartWebApplication
{
	private static Log log = LogFactory.getLog(StartWebApplication.class);

	private static final String SRC_TEST_RESOURCES_JETTY_XML = TestUtils.RESOURCE_DIRECTORY + "jetty.xml";
	private static final String workingDirectory = TestUtils.WORKINGDIRECTORY + "/webical";

	/**
	 * Main function, starts the Jetty test server.
	 *
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		log.info("Starting up Jetty");

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(TestUtils.portNoTest);

		Server jettyServer = new Server();
		jettyServer.setConnectors(new Connector[] { connector });

		File workingDir = null;
		WebAppContext web = new WebAppContext();
		try {
			XmlConfiguration configuration = new XmlConfiguration(new File(SRC_TEST_RESOURCES_JETTY_XML).toURI().toURL());

			configuration.configure(web);
			workingDir = new File(workingDirectory);
			if (! workingDir.exists()) workingDir.mkdir();
			web.setAttribute("javax.servlet.context.tempdir", workingDir);

			jettyServer.addHandler(web);

			jettyServer.setStopAtShutdown(true);
//			jettyServer.setGracefulShutdown(1000);

			// monitor the Jetty server
			MonitorJettyServer monitorJetty = new MonitorJettyServer(jettyServer);
			monitorJetty.start();

			jettyServer.start();
			jettyServer.join();
		} catch (Exception e) {
			log.error(e,e);
			System.exit(100);
		}
		finally {
			FileUtils.deleteFileRecursively(workingDir);
		}
	}

	/**
	 * Monitor for stop requests for the Jetty test server.
	 *
	 * @param args not used
	 */
    private static class MonitorJettyServer extends Thread
    {
        public MonitorJettyServer(Server jettyServer)
        {
        	this.jettyServer = jettyServer;

            setDaemon(true);
            setName("StopJettyServerMonitor");
            try {
                socket = new ServerSocket(TestUtils.portNoTest - 1, 1, InetAddress.getByName("localhost"));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run()
        {
    		log.info("Running monitor Jetty 'stop' thread");
            Socket accept;
            try {
                accept = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                reader.readLine();
        		log.info("Stopping Jetty server");
        		jettyServer.stop();
                accept.close();
                socket.close();
            } catch (Exception e) {
    			log.error(e,e);
            }
        }
        private Server jettyServer;
        private ServerSocket socket;
    }
}
