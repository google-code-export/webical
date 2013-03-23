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

package org.webical.test.plugin.registration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;

import junit.framework.TestCase;

import org.webical.plugin.PluginException;
import org.webical.plugin.file.FileUtils;
import org.webical.plugin.file.ZipFileExtractor;
import org.webical.plugin.jaxb.BackendPlugin;
import org.webical.plugin.jaxb.ClassFolder;
import org.webical.plugin.jaxb.ClassFolders;
import org.webical.plugin.jaxb.FrontendPlugin;
import org.webical.plugin.jaxb.JarFolder;
import org.webical.plugin.jaxb.JarFolders;
import org.webical.plugin.jaxb.Plugin;
import org.webical.plugin.jaxb.Registrations;
import org.webical.plugin.jaxb.ResourceFolder;
import org.webical.plugin.jaxb.ResourceFolders;
import org.webical.plugin.registration.PackageValidator;

import org.webical.test.TestUtils;

/**
 * Tests the Package validator
 * @author ivo
 *
 */
public class PackageValidatorTest extends TestCase {

	private static Log log = LogFactory.getLog(PackageValidatorTest.class);

	private static FileSystemResource workingDirectory = new FileSystemResource(TestUtils.WORKINGDIRECTORY + "/" + PackageValidatorTest.class.getSimpleName());

	protected void setUp() throws Exception
	{
		super.setUp();
		if (! workingDirectory.exists()) workingDirectory.getFile().mkdir();
	}

	/**
	 * Tests with a proper Plugin File
	 * @throws IOException
	 * @throws PluginException
	 */
	public void testProperPluginFile() throws IOException, PluginException {
		//File to extract
		File pluginPackage = new FileSystemResource(TestUtils.RESOURCE_DIRECTORY + "org/webical/test/plugin/registration/valid/valid-test-plugin.zip").getFile();
		//Name of the extraction directory
		File extractionDir = new File(workingDirectory.getFile().getAbsolutePath() + File.separator + pluginPackage.getName());
		extractionDir.mkdir();
		ZipFileExtractor.unpackZipFile(pluginPackage.getAbsoluteFile(), extractionDir);

		PackageValidator packageValidator = new PackageValidator(extractionDir, getPluginManifest());
		packageValidator.validatePackage();
	}

	/**
	 * Tests with a plugin package with a missing entry
	 * @throws IOException
	 */
	public void testPluginFileWithMissingClass() throws IOException {
		//File to extract
		File pluginPackage = new FileSystemResource(TestUtils.RESOURCE_DIRECTORY + "org/webical/test/plugin/registration/invalid/missingfile-test-plugin.zip").getFile();
		//Name of the extraction directory
		File extractionDir = new File(workingDirectory.getFile().getAbsolutePath() + File.separator + pluginPackage.getName());
		extractionDir.mkdir();
		ZipFileExtractor.unpackZipFile(pluginPackage.getAbsoluteFile(), extractionDir);

		PackageValidator packageValidator = new PackageValidator(extractionDir, getPluginManifest());
		try {
			packageValidator.validatePackage();
			fail("Should notice the missing file");
		} catch (PluginException e) {
			log.debug("Expected this exception: " + e.getMessage());
		}
	}

	/**
	 * Tests with a plugin package where a ExtensionListener doesn't implement the right interfaces
	 * @throws IOException
	 */
	public void testPluginFileWithUnimplementedInterface() throws IOException {
		//File to extract
		File pluginPackage = new FileSystemResource(TestUtils.RESOURCE_DIRECTORY + "org/webical/test/plugin/registration/invalid/incorrectclass-test-plugin.zip").getFile();
		//Name of the extraction directory
		File extractionDir = new File(workingDirectory.getFile().getAbsolutePath() + File.separator + pluginPackage.getName());
		extractionDir.mkdir();
		ZipFileExtractor.unpackZipFile(pluginPackage.getAbsoluteFile(), extractionDir);

		//Mess with the manifest to make sure the wrong kind of class is in the backend plugin registration
		Plugin manifest = getPluginManifest();
		manifest.getRegistrations().getBackendPlugin().get(0).setClassName("SomeExtension");

		PackageValidator packageValidator = new PackageValidator(extractionDir, manifest);
		try {
			packageValidator.validatePackage();
			fail("Should have noticed unimplemented interface in the backend plugin");
		} catch (PluginException e) {
			log.debug("Expected this exception: " + e.getMessage());
		}

		//Mess with the manifest to make sure the wrong kind of class is in the frontend plugin registration
		manifest = getPluginManifest();
		manifest.getRegistrations().getFrontendPlugin().get(0).setClassName("SomeClass");

		packageValidator = new PackageValidator(extractionDir, manifest);
		try {
			packageValidator.validatePackage();
			fail("Should have noticed unimplemented interface in the frontend plugin");
		} catch (PluginException e) {
			log.debug("Expected this exception: " + e.getMessage());
		}
	}

	public void testJarResources() {
		//TODO implement me
	}

	/**
	 * @return the plugin manifest
	 */
	private Plugin getPluginManifest() {
		Plugin plugin = new Plugin();
		ResourceFolders resourceFolders = new ResourceFolders();
		Registrations registrations = new Registrations();
		ClassFolders classFolders = new ClassFolders();
		JarFolders jarFolders = new JarFolders();

		plugin.setResourceFolders(resourceFolders);
		plugin.setRegistrations(registrations);
		plugin.setClassFolders(classFolders);
		plugin.setJarFolders(jarFolders);

		//Resources
		ResourceFolder resourceFolder = new ResourceFolder();
		resourceFolder.setFileName("resources");
		plugin.getResourceFolders().getResourceFolder().add(resourceFolder);

		//Class folders
		ClassFolder classFolder = new ClassFolder();
		classFolder.setFileName("classfiles");
		plugin.getClassFolders().getClassFolder().add(classFolder);

		//Jar folders
		JarFolder jarFolder = new JarFolder();
		jarFolder.setFileName("jarfiles");
		plugin.getJarFolders().getJarFolder().add(jarFolder);

		//Backend Registration
		BackendPlugin backendPlugin =  new BackendPlugin();
		backendPlugin.setCalendarType("some-strange-calendar");
		backendPlugin.setClassName("SomeClass");
		backendPlugin.setDaoType("Event");

		plugin.getRegistrations().getBackendPlugin().add(backendPlugin);

		//Frontend Registration
		FrontendPlugin frontendPlugin = new FrontendPlugin();
		frontendPlugin.setClassName("SomeExtension");
		frontendPlugin.setExtendable(true);
		frontendPlugin.setExtendedClass(TestUtils.WEBICAL_BASE_PACKAGE + ".web.component.HeaderPanel");

		plugin.getRegistrations().getFrontendPlugin().add(frontendPlugin);

		return plugin;
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
