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

package org.webical.plugin.file;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility to extract a zip file
 * @author ivo
 *
 */
public class ZipFileExtractor {
	
	private static Log log = LogFactory.getLog(ZipFileExtractor.class);
	
	/**
	 * Unpacks a zip file to the given destination directory
	 * @param inputFile the zipfile to extract
	 * @param destinationDir the destination directory
	 * @throws IOException
	 */
	public static void unpackZipFile(File inputFile, File destinationDir) throws IOException {
		Enumeration entries;
		ZipFile zipFile;
		
		try {
			zipFile = new ZipFile(inputFile);
		
			entries = zipFile.entries();
		
			while(entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();
		
				if(entry.isDirectory()) {
					if(log.isDebugEnabled()) {
						log.debug("Extracting directory: " + destinationDir + File.separator + entry.getName());
					}
					(new File(destinationDir.getAbsolutePath() + File.separator + entry.getName())).mkdir();
					continue;
				}
				if(log.isDebugEnabled()) {
					log.debug("Extracting file: " + destinationDir + File.separator + entry.getName());
				}
				FileUtils.copyInputStream(zipFile.getInputStream(entry), 
		    		new BufferedOutputStream(new FileOutputStream(destinationDir + File.separator + entry.getName())), 
		    		true);
			}
		
			zipFile.close();
		} catch (IOException ioe) {
			log.error("Error while extracting file: " + inputFile.getAbsolutePath(), ioe);
			throw ioe;
		}
	}

}
