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

package org.webical.web.util;

import javax.servlet.http.HttpServletRequest;

public class Browser {
	public enum Agent {
		GECKO("gecko"), IE5("ie5"), IE6("ie6"), IE7("ie7"), IEX("iex"), SAFARI("safari"), CHROME("chrome"),
		FIREFOX("firefox"), OPERA("opera");

		private String value;

		Agent(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static Agent sniffBrowser(HttpServletRequest request) {
		if(request.getHeader("User-Agent") != null) {
			String userAgent = request.getHeader("User-Agent").toLowerCase();
			String[] safariAgents = { "safari", "khtml", "konqueror" };
			for (String agent : safariAgents) {
				if (userAgent.contains(agent)) {
					return Agent.SAFARI;
				}
			}

			if (userAgent.contains("msie 5")) {
				return Agent.IE5;
			} else if (userAgent.contains("msie 6")) {
				return Agent.IE6;
			} else if (userAgent.contains("msie 7")) {
				return Agent.IE7;
			} else if (userAgent.contains("msie ")) {
				return Agent.IEX;
			}

			else if (userAgent.contains(Agent.CHROME.toString())) {
				return Agent.CHROME;
			}
			else if (userAgent.contains(Agent.FIREFOX.toString())) {
				return Agent.FIREFOX;
			}
			else if (userAgent.contains(Agent.OPERA.toString())) {
				return Agent.OPERA;
			}
		}

		// XXX: it might as well be a crawler
		return Agent.GECKO;
	}
}
