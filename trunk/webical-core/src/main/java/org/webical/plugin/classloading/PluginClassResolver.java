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

package org.webical.plugin.classloading;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.application.DefaultClassResolver;
import org.apache.wicket.application.IClassResolver;
import org.webical.plugin.PluginException;

/**
 * ClassResolver which looks up the classes through the PluginClassLoader if the DefaultClassResolver
 * could not find it. 
 * @author ivo
 *
 */
public class PluginClassResolver implements IClassResolver {
	
	private static Log log = LogFactory.getLog(PluginClassResolver.class);
	
	/** Default class resolver */
	private final IClassResolver defaultClassResolver = new DefaultClassResolver();
	
	private PluginClassLoader pluginClassLoader;
	
	public PluginClassResolver() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		pluginClassLoader = new PluginClassLoader(classLoader);
	}

	/**
	 * First tries to load the class using the default ClassResolver
	 * If that fails the PluginClassLoader is used
	 * @see wicket.application.IClassResolver#resolveClass(java.lang.String)
	 * @throws ClassNotFoundException
	 */
	public Class resolveClass(String classname) throws ClassNotFoundException {
		//First try the default ClassResolver
		
		try {
			if(log.isDebugEnabled()) {
				log.debug("Trying to load the class: " + classname + " through the default ClassResolver");
			}
			return defaultClassResolver.resolveClass(classname);
		} catch (ClassNotFoundException e) {
			if(log.isDebugEnabled()) {
				log.debug("Default ClassResolver could not load class: " + classname);
			}
		}
		
		return getPluginClass(classname);
	}
	
	/**
	 * Locates a Class using the PluginClassLoader
	 * @param classname the name of the Class to find
	 * @return a Class
	 * @throws ClassNotFoundException
	 */
	private Class getPluginClass(String classname) throws ClassNotFoundException {
		return pluginClassLoader.findClass(classname);
	}
	
	//Access methods for registrations

	/**
	 * Takes a fully qualified classname (eg some.package.SomeClass) and the associated file
	 * @param fullyQualifiedClassName a fully qualified classname
	 * @param classFile the file containing the class
	 * @throws DuplicateClassPathEntryException if the class was already registered
	 */
	public void addClassRegistration(String fullyQualifiedClassName, File classFile) throws DuplicateClassPathEntryException {
		pluginClassLoader.addClassRegistration(fullyQualifiedClassName, classFile);
	}
	
	/**
	 * Takes a resource identifier (eg. some/package/TheResource.extension) and the associated file to regsiter
	 * on the classpath 
	 * @param resourceIdentifier a resource identifier (eg. some/package/TheResource.extension) 
	 * @param file the associated file to regsiter
	 * @throws DuplicateClassPathEntryException if a resource with the same identifier was already registered.
	 */
	public void addClassPathResourceRegistration(String resourceIdentifier, File file) throws DuplicateClassPathEntryException {
		pluginClassLoader.addClassPathResourceRegistration(resourceIdentifier, file);
	}
	
	/**
	 * Adds a jar file to the list of registrations
	 * @param jarFile
	 * @throws PluginException 
	 */
	public void addJarFileRegistration(File jarFile) throws PluginException {
		pluginClassLoader.readJarFile(jarFile);
	}

}
