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

package org.webical.test.plugin.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.webical.plugin.file.FileUtils;
import org.webical.plugin.file.ZipFileExtractor;

import junit.framework.TestCase;

import org.webical.test.TestUtils;

/**
 * Tests the zipfile extraction utility
 * @author ivo
 *
 */
public class ZipFileExtractorTest extends TestCase {
	private static Log log = LogFactory.getLog(ZipFileExtractorTest.class);

	private static final String TEST_NORMALFILE = TestUtils.RESOURCE_DIRECTORY + "org/webical/test/plugin/file/test.normalfile";
	private static final String TEST_ZIP = TestUtils.RESOURCE_DIRECTORY + "org/webical/test/plugin/file/test.zip";

	private static FileSystemResource workingDirectory = new FileSystemResource(TestUtils.WORKINGDIRECTORY + "/" + ZipFileExtractorTest.class.getSimpleName());

	protected void setUp() throws Exception
	{
		super.setUp();
		if (! workingDirectory.exists()) workingDirectory.getFile().mkdir();
	}

	/**
	 * Tests with a proper zipfile and working directory
	 * @throws IOException
	 */
	public void testProperZipfile() throws IOException {
		File fileToExtract = new FileSystemResource(TEST_ZIP).getFile();
		ZipFileExtractor.unpackZipFile(fileToExtract, workingDirectory.getFile());
	}

	/**
	 * Tests with a inproper zipfile and proper working directory
	 */
	public void testOtherFile() {
		File fileToExtract = new FileSystemResource(TEST_NORMALFILE).getFile();
		try {
			ZipFileExtractor.unpackZipFile(fileToExtract, workingDirectory.getFile());
			fail("Should have thrown an exception");
		} catch (Exception e) {
			log.debug("Expected this exception: " + e.getMessage());
		}
	}

	/**
	 * Tests with a proper zipfile and inproper working directory
	 */
	public void testNonexistingExtractionDir() {
		File fileToExtract = new FileSystemResource(TEST_ZIP).getFile();
		try {
			ZipFileExtractor.unpackZipFile(fileToExtract, new File(workingDirectory.getFile().getAbsolutePath(), "subdir"));
			fail("Should have thrown an exception");
		} catch (Exception e) {
			log.debug("Expected this exception: " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cleanup();
	}

	/**
	 * Cleans the working directory
	 */
	private void cleanup() {
		log.debug("Cleaning up");
		FileUtils.cleanupDirectory(workingDirectory.getFile());
		if (workingDirectory.exists()) workingDirectory.getFile().delete();
	}
}
