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

package org.webical.plugin.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common fileoperation methods
 * @author ivo
 *
 */
public class FileUtils {

	public static final String CLASS_FILE_EXTENSION = ".class";
	public static final String JAR_FILE_EXTENSION = ".jar";

	private static final String SVN_DIRECTORY = ".svn";
	private static Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * Removes all files and directories from a given directory
	 * @param directory the directory to clean
	 */
	public static void cleanupDirectory(File directory)
	{
		if (directory.exists() && directory.isDirectory())
		{
			File[] filesToDelete = directory.listFiles();
			for (File file : filesToDelete)
			{
				if (!file.getName().equalsIgnoreCase(SVN_DIRECTORY)) {
					deleteFileRecursively(file);
				}
			}
		} else {
			log.error("Could not clean directory");
		}
	}

    /**
     * Deletes a file or directories recursively
     * @param file
     * @return
     */
    public static boolean deleteFileRecursively(File file)
    {
        if (file.isDirectory())
        {
            String[] children = file.list();
            for (int i=0; i<children.length; ++ i)
            {
            	File delFile = new File(file, children[i]);
                boolean success = deleteFileRecursively(delFile);
                if (! success) {
                	log.debug("Failed to delete " + delFile.toString());
                }
            }
        }

        // The directory should now be empty so delete it
        log.debug("Deleting file: " + file.getAbsolutePath());
        return file.delete();
    }

    /**
     * Traverses a tree and returns all files
     * @param file the file to start
     * @return the list of files
     */
	public static List<File> traverseTree(File file, String extension) {
    	List<File> files = new ArrayList<File>();

    	if (file != null) {
    		log.debug(file.getPath());

	        if (file.isDirectory()) {
	            String[] children = file.list();
	            for (int i = 0; i < children.length; ++i) {
	                files.addAll(traverseTree(new File(file, children[i]), extension));
	            }
	        } else {
	        	if (!StringUtils.isEmpty(extension)) {
	        		if (file.getName().endsWith(extension)) {
	        			files.add(file);
	        		}
	        	} else {
	        		files.add(file);
				}
	        }
    	}
    	return files;
    }

	/**
	 * Reads the {@link String} from a {@link InputStream}
	 * @param inputStream the {@link InputStream} to read from
	 * @return the {@link String}
	 * @throws IOException on error
	 */
	public static String getStringFromStream(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("No inputstream provided");
		}

		StringBuffer contents = new StringBuffer();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
		return contents.toString();
	}

	/**
	 * Copies a stream to a file
	 * @param stream the stream to copy
	 * @param file the destination
	 * @throws IOException
	 */
	public static final void streamToFile(InputStream stream, File file, boolean closeStream) throws IOException {
		if (file == null || stream == null) {
			throw new IllegalArgumentException("File or stream is null");
		}

		if (!file.exists()) {
			log.debug("File does not exist yet, creating: " + file.getAbsolutePath());
			file.createNewFile();
		}

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		copyInputStream(stream, fileOutputStream, closeStream);
	}

	/**
	 * Simple method to copy an InputStream into an OutputStream
	 * @param inputStream the InputStream
	 * @param outputStream the OutputStream
	 * @throws IOException
	 */
	public static final void copyInputStream(InputStream inputStream, OutputStream outputStream, boolean closeStreams) throws IOException
	{
		byte[] buffer = new byte[1024];
		int length;

		while ((length = inputStream.read(buffer)) >= 0) {
			outputStream.write(buffer, 0, length);
		}

		if (closeStreams) {
			inputStream.close();
			outputStream.close();
		}
	}
}
