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

package org.webical.test.plugin.classloading;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.plugin.PluginException;
import org.webical.plugin.classloading.ClassUtils;
import org.webical.plugin.classloading.DuplicateClassPathEntryException;
import org.webical.plugin.classloading.PluginClassLoader;
import org.webical.plugin.file.FileUtils;

import junit.framework.TestCase;

/**
 * Tests the {@link PluginClassLoader}
 * 
 * @author ivo
 *
 */
public class PluginClassLoaderTest extends TestCase {

	private static Log log = LogFactory.getLog(PluginClassLoaderTest.class);

	PluginClassLoader classLoader = null;
	private List<File> filesToCleanup = null;

	/**
	 * Setup the {@link PluginClassLoader}
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		classLoader = new PluginClassLoader(getClass().getClassLoader());
		filesToCleanup = new ArrayList<File>();
	}

	/**
	 * Cleanup directories created by this test
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		classLoader = null;
		for (File file : filesToCleanup) {
			log.debug("Cleaning up: " + file.getAbsolutePath());
			Thread.sleep(1);
			FileUtils.deleteFileRecursively(file);
		}
	}

	/**
	 * Tests the classloader for class files with correct input
	 */
	public void testClassFileLoading() {
		log.debug("testClassFileLoading");
		String classFileString1 = getClass().getResource("TestClass1.class.test").getFile();
		File classFile1= new File(classFileString1);
		String classFileString2 = getClass().getResource("TestClass2.class.test").getFile();
		File classFile2= new File(classFileString2);

		String fullClassName = null;
		try {
			fullClassName = ClassUtils.fileToFullClassName(
										new File(classFile1.getParent()),
										new File(classFileString1.substring(0, classFileString1.lastIndexOf("."))));
			classLoader.addClassRegistration(fullClassName, classFile1);

			fullClassName = ClassUtils.fileToFullClassName(
										new File(classFile2.getParent()),
										new File(classFileString2.substring(0, classFileString2.lastIndexOf("."))));
			classLoader.addClassRegistration(fullClassName,	classFile2);
		} catch (DuplicateClassPathEntryException e1) {
			log.debug(e1);
			fail("Duplicate. Where?");
		}

		try {
			classLoader.findClass("TestClass1");
			classLoader.findClass("TestClass2");
		} catch (ClassNotFoundException e2) {
			log.debug(e2);
			fail("It should be registered already");
		}
	}

	/**
	 * Tests for invalid input with regular classloading
	 */
	public void testClassFileLoadingInvalid() {
		log.info("testClassFileLoadingInvalid");
		try {
			classLoader.findClass("");
			classLoader.findClass(null);
		} catch (ClassNotFoundException e) {
			log.debug("testClassFileLoadingInvalid exception expected " + e.getMessage());
		}

		try {
			classLoader.addClassRegistration("name", null);
			classLoader.addClassRegistration("name1", new File(""));
			classLoader.addClassRegistration("", new File("something"));
			classLoader.addClassRegistration(null, new File("something"));
		} catch (DuplicateClassPathEntryException e1) {
			fail("No duplicates he??");
		}

		try {
			classLoader.findClass("name");
		} catch (ClassNotFoundException e) {
			log.debug("testClassFileLoadingInvalid exception expected " + e.getMessage());
		}

		try {
			classLoader.findClass("name1");
		} catch (ClassNotFoundException e) {
			log.debug("testClassFileLoadingInvalid exception expected " + e.getMessage());
		}

		try {
			classLoader.findClass("");
		} catch (ClassNotFoundException e) {
			log.debug("testClassFileLoadingInvalid exception expected " + e.getMessage());
		}

		try {
			classLoader.findClass(null);
		} catch (ClassNotFoundException e) {
			log.debug("testClassFileLoadingInvalid exception expected " + e.getMessage());
		}
	}

	/**
	 * Tests resource loading from the classpath
	 */
	public void testResourceLoading() {
		File resourceFile = new File(getClass().getResource("test-resource.txt").getFile());
		log.info("testResourceLoading:File " + resourceFile.toString());
		try {
			classLoader.addClassPathResourceRegistration(
					ClassUtils.fileToClassPathResourceIdentifier(resourceFile.getParentFile(), resourceFile),
					resourceFile);
		} catch (DuplicateClassPathEntryException e) {
			fail("What is it with those duplicates...");
		}

		URL resourceUrl = classLoader.findResource("test-resource.txt");
		log.info("testResourceLoading:URL " + resourceUrl.toString());
		try {
			if (resourceUrl == null || !resourceUrl.sameFile(resourceFile.toURI().toURL())) {
				fail("Could not successfully load the resource");
			}
		} catch (MalformedURLException e) {
			fail("Malformed url registered used");
		}
	}

	/**
	 * Tests incorrect input handling
	 */
	public void testResourceLoadingInvalid() {
		log.info("testResourceLoadingInvalid");
		try {
			classLoader.addClassPathResourceRegistration("1", new File(""));
			classLoader.addClassPathResourceRegistration(null, new File(""));
			classLoader.addClassPathResourceRegistration(null, null);
			classLoader.addClassPathResourceRegistration("2", null);
		} catch (DuplicateClassPathEntryException e) {
			//? they don't even exist
			fail("Duplicate resources used in test");
		}

		try {
			classLoader.findClass("1");
		} catch (ClassNotFoundException e) {
			log.debug("testResourceLoadingInvalid exception expected " + e.getMessage());
		}

		try {
			classLoader.findClass("2");
		} catch (ClassNotFoundException e) {
			log.debug("testResourceLoadingInvalid exception expected " + e.getMessage());
		}

		try {
			classLoader.findClass(null);
		} catch (ClassNotFoundException e) {
			log.debug("testResourceLoadingInvalid exception expected " + e.getMessage());
		}
	}

	/**
	 * Tests the {@link PluginClassLoader} for duplicate entry handling
	 */
	public void testDuplicateHandling() {
		log.info("testDuplicateHandling");
		//First for the classes
		try {
			classLoader.addClassRegistration("TestClass", new File(""));
			classLoader.addClassRegistration("TestClass", new File(""));
			fail("No duplicate handling");
		} catch (DuplicateClassPathEntryException e) {
			log.debug("testDuplicateHandling exception expected " + e.getMessage());
		}

		//Next the resources
		try {
			classLoader.addClassPathResourceRegistration("resource", new File(""));
			classLoader.addClassPathResourceRegistration("resource", new File(""));
			fail("No duplicate handling");
		} catch (DuplicateClassPathEntryException e) {
			log.debug("testDuplicateHandling exception expected " + e.getMessage());
		}

		//And now the jar files (should run just fine)
		try {
			classLoader.readJarFile(new File(getClass().getResource("test.jar").getFile()));
			classLoader.readJarFile(new File(getClass().getResource("test.jar").getFile()));
		} catch (PluginException e) {
			fail("Should have been valid");
		}
	}

	/**
	 * Tests the classloader with valid input for jar file loading
	 * @throws PluginException 
	 */
	public void testJarLoading() throws PluginException {
		log.info("testJarLoading");
		//The jar to load
		File jarFile = new File(getClass().getResource("test.jar").getFile());

		//Add this to the list of files to cleanup
		addToLoadedJars(jarFile);

		//Load it
		classLoader.readJarFile(jarFile);
		try {
			classLoader.findClass("TestClassInJar1");
			classLoader.findClass("TestClassInJar2");
		} catch (ClassNotFoundException e) {
			fail("These classes should be loaded already...");
		}

		try {
			classLoader.findClass("TestClassInJar3");
			fail("This class doesn't even exist");
		} catch (ClassNotFoundException e) {
			log.debug("testJarLoading exception expected " + e.getMessage());
		}
	}

	/**
	 * Tests the classloader with invalid input for jar file loading
	 */
	public void testJarLoadingInvalidInput() {
		log.info("testJarLoadingInvalidInput");
		try {
			classLoader.readJarFile(new File("I do not exist.."));
			fail("I don't exist");
		} catch (PluginException e) {}

		try {
			classLoader.readJarFile(null);
			fail("I don't exist");
		} catch (PluginException e) {}

		try {
			classLoader.readJarFile(new File(getClass().getResource("test-invalid.jar").getFile()));
		} catch (PluginException e) {
			fail("Just skipping");
		}
	}

	/**
	 * Tests loading resources from embedded jar files and see if classes in jar files
	 * referencing these resources can get to them.
	 */
	public void testResourceLoadingFromJar() {
		log.info("testResourceLoadingFromJar");
		//First the simple version (without packages)
		try {
			//The jar to load
			File jarFile = new File(getClass().getResource("test-resources.jar").getFile());

			//Add this to the list of files to cleanup
			addToLoadedJars(jarFile);

			//Load it
			classLoader.readJarFile(jarFile);
		} catch (PluginException e) {
			fail("This should have worked...");
		}

		Class resourceLoadingClassInJArClass = null;
		try {
			resourceLoadingClassInJArClass = classLoader.findClass("ResourceLoadingClassInJAr");
		} catch (ClassNotFoundException e) {
			fail("Could not find class from jar file");
		}

		Object resourceLoadingClassInJArInstance = null;
		try {
			resourceLoadingClassInJArInstance = resourceLoadingClassInJArClass.newInstance();
			assertNotNull("Could not instantiate class from jar", resourceLoadingClassInJArInstance);
		} catch (Exception e) {
			fail("Could not instantiate class from jar: " + e.getMessage());
		}
		resourceLoadingClassInJArInstance = null;
		resourceLoadingClassInJArClass = null;

		//Ok, so far. Let's try to break it when packages come into play
		try {
			//The jar to load
			File jarFile = new File(getClass().getResource("test-resources-extended.jar").getFile());

			//Add this to the list of files to cleanup
			addToLoadedJars(jarFile);

			//Load it
			classLoader.readJarFile(jarFile);
		} catch (PluginException e) {
			fail("This should have worked...");
		}

		Class resourceLoadingInPackageClass = null;
		try {
			resourceLoadingInPackageClass = classLoader.findClass("somepackage.ResourceLoadingInPackage");
			assertNotNull(resourceLoadingInPackageClass);
		} catch (ClassNotFoundException e1) {
			fail("Could not find class from jar file");
		}

		Object resourceLoadingInPackageInstance = null;
		try {
			resourceLoadingInPackageInstance = resourceLoadingInPackageClass.newInstance();
			assertNotNull("Could not instantiate class from jar", resourceLoadingInPackageClass);
			log.debug(resourceLoadingInPackageClass);
		} catch (Exception e) {
			fail("Could not instantiate class from jar: " + e.getMessage());
		}

		try {
			Method method = resourceLoadingInPackageClass.getDeclaredMethods()[0];
			method.invoke(resourceLoadingInPackageInstance, (Object[])null);
		} catch (Exception exception) {
			fail("Could not invoke method on created instance: " + exception.getMessage());
		}
		resourceLoadingInPackageInstance = null;
		resourceLoadingInPackageClass = null;
	}

	private void addToLoadedJars(File jarFile) {
		String path = jarFile.getAbsolutePath() + PluginClassLoader.JAR_EXTRACTION_EXTENSION;
		log.debug(path);
		filesToCleanup.add(new File(path));
	}
}
