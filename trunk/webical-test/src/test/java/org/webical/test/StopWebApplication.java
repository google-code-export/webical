/*
 *    Webical - http://code.google.com/p/webical/
 *    Copyright (C) 2012 by Cebuned
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

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
* Shuts down a running Jetty test server
*
* @author Harm-Jan, Cebuned
*/
public class StopWebApplication
{
	/**
	 * Main function, stops the Jetty test server.
	 *
	 * @param args not used
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Socket socket = new Socket(InetAddress.getByName("localhost"), TestUtils.portNoTest - 1);
		OutputStream out = socket.getOutputStream();
		System.out.println("*** Sending Jetty stop request");
		out.write(("\r\n").getBytes());
		out.flush();
		socket.close();
	}
}
