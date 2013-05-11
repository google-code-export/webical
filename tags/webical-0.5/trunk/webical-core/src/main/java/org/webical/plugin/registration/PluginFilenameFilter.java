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

package org.webical.plugin.registration;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * Filename filter for plugin packages
 * @author ivo
 *
 */
public class PluginFilenameFilter implements FilenameFilter {
	private static Log log = LogFactory.getLog(PluginFilenameFilter.class);
	
	//The extension that is accepted
	private String extension;
	
	/**
	 * Constructor
	 * @param extension the accepted file extension
	 */
	public PluginFilenameFilter(String extension) {
		this.extension = extension;
		if(log.isDebugEnabled()) {
			log.debug("Accepting files with extension: " + extension);
		}
	}

	/**
	 * Accepts files ending with the extension given in the constructor
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File directory, String filename) {
		if(filename.endsWith(extension)) {
			if(log.isDebugEnabled()) {
				log.debug("Accepting plugin package: " + filename + " in directory " + directory.getAbsolutePath());
			}
			return true;
		} else {
			if(log.isDebugEnabled()) {
				log.debug("Rejecting file: " + filename + " as a plugin in directory " + directory.getAbsolutePath());
			}
			return false;
		}
	}

}
