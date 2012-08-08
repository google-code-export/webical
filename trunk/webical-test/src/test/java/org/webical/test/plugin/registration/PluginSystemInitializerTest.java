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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import junit.framework.TestCase;
import org.webical.ApplicationSettings;
import org.webical.Calendar;
import org.webical.dao.EventDao;
import org.webical.dao.factory.DaoFactory;
import org.webical.manager.ApplicationSettingsManager;
import org.webical.manager.WebicalException;
import org.webical.plugin.PluginException;
import org.webical.plugin.file.FileUtils;
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
import org.webical.plugin.registration.PluginRegistration;
import org.webical.plugin.registration.PluginSystemInitializer;
import org.webical.plugin.xml.PluginManifestReader;
import org.webical.settings.ApplicationSettingsException;
import org.webical.settings.ApplicationSettingsFactory;
import org.webical.web.app.WebicalWebApplication;
import org.webical.web.event.ExtensionListenerRegistrations;
import org.webical.test.TestUtils;

/**
 * Tests the PluginIntializer
 * @author ivo
 *
 */
public class PluginSystemInitializerTest extends TestCase {

	private static final String SOME_STRANGE_CALENDAR = "some-strange-calendar";

	private static Log log = LogFactory.getLog(PluginSystemInitializerTest.class);

	private static FileSystemResource workingDirectory = new FileSystemResource(TestUtils.WORKINGDIRECTORY + "/" + PluginSystemInitializerTest.class.getSimpleName());

	/**
	 * Clear the extensionListener map before each run
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (! workingDirectory.exists()) workingDirectory.getFile().mkdir();
		ExtensionListenerRegistrations.getExtensionHandlerToExtensionListenersMap().clear();
	}

	/**
	 * Tests if all Classes/Resources/Plugins are registered correctly
	 * @throws Exception
	 */
	public void testValidPackage() throws Exception {
		DaoFactory daoFactory = DaoFactory.getInstance();
		WebicalWebApplication webicalWebApplication = new WebicalWebApplication();

		PluginSystemInitializer pluginSystemInitializer = new PluginSystemInitializer() {
			@Override
			protected void checkRequiredWebicalVersion(Plugin manifestInfo) throws PluginException {}
		};

		pluginSystemInitializer.setDaoFactory(daoFactory);
		pluginSystemInitializer.setPluginManifestReader(getPluginManifestReader());
		pluginSystemInitializer.setPluginRegistrationStore(webicalWebApplication);

		ApplicationSettingsFactory.getInstance().setApplicationSettingsManager(new ApplicationSettingsManager() {
			public ApplicationSettings getApplicationSettings() throws WebicalException {
				ApplicationSettings applicationSettings = new ApplicationSettings();
				Set<String> pluginPaths = new HashSet<String>();
				pluginPaths.add(TestUtils.RESOURCE_DIRECTORY + "org/webical/test/plugin/registration/valid/");
				applicationSettings.setPluginPaths(pluginPaths);
				applicationSettings.setPluginWorkPath(workingDirectory.getFile().getAbsolutePath());
				applicationSettings.setPluginPackageExtension(".zip");
				return applicationSettings;
			}

			public void storeApplicationSettings(ApplicationSettings applicationSettings) throws WebicalException { }
		});
		pluginSystemInitializer.setupPlugins();

		//Check the backend registration
		Calendar calendar = new Calendar();
		calendar.setType(SOME_STRANGE_CALENDAR);
		EventDao eventDao = daoFactory.getEventDaoForCalendar(calendar);
		assertNotNull(eventDao);

		//Check the frontend registration
		assertEquals(1, ExtensionListenerRegistrations.getExtensionHandlerToExtensionListenersMap().size());

		//Check the resource path registrations
		assertEquals(1, webicalWebApplication.getResourcePaths().size());

		//Check the Class registrations
		assertNotNull(webicalWebApplication.getPluginClassResolver().resolveClass("DummyClass"));
		assertNotNull(webicalWebApplication.getPluginClassResolver().resolveClass("SomeClass"));
		assertNotNull(webicalWebApplication.getPluginClassResolver().resolveClass("SomeExtension"));

		//check the jar file registrations
		assertNotNull(webicalWebApplication.getPluginClassResolver().resolveClass("TestClassInJar1"));
		assertNotNull(webicalWebApplication.getPluginClassResolver().resolveClass("TestClassInJar2"));
		assertNotNull(webicalWebApplication.getPluginClassResolver().resolveClass("ResourceLoadingClassInJAr"));
	}

	/**
	 * Tests if invalid packages are treated correctly
	 * @throws ApplicationSettingsException
	 */
	public void testInvalidPackage() throws ApplicationSettingsException {
		DaoFactory daoFactory = DaoFactory.getInstance();
		PluginSystemInitializer pluginSystemInitializer = new PluginSystemInitializer();
		pluginSystemInitializer.setDaoFactory(daoFactory);
		pluginSystemInitializer.setPluginManifestReader(getPluginManifestReader());

		Set<String> pluginPaths = new HashSet<String>();
		pluginPaths.add(TestUtils.RESOURCE_DIRECTORY + "org/webical/test/plugin/registration/invalid/");

		WebicalWebApplication application = new WebicalWebApplication();
		pluginSystemInitializer.setPluginRegistrationStore(application);

		ApplicationSettings applicationSettings = new ApplicationSettings();
		applicationSettings.setPluginPackageExtension(".zip");
		applicationSettings.setPluginPaths(pluginPaths);
		applicationSettings.setPluginWorkPath(workingDirectory.getFile().getAbsolutePath());
		TestUtils.initializeApplicationSettingsFactory(applicationSettings);

		try {
			pluginSystemInitializer.setupPlugins();
		} catch (Exception e) {
			fail("This should no longer happen...");
		}

		PluginRegistration pluginRegistration1 = application.getPluginRegistrations().get(0);

		assertEquals(2, application.getPluginRegistrations().size());
		assertEquals(PluginRegistration.PluginState.NOT_REGISTERED, pluginRegistration1.getPluginState());
	}

	/**
	 * @return a Dummy PluginManifestReader
	 */
	private PluginManifestReader getPluginManifestReader() {
		return new PluginManifestReader() {
			@Override
			public Plugin parsePluginManifest(File pluginManifest) throws PluginException {
				return getPluginManifest();
			}
		};
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
