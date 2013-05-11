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
import java.util.List;

import org.webical.plugin.classloading.PluginClassResolver;
import org.webical.plugin.jaxb.Plugin;
import org.webical.plugin.registration.PluginRegistration.PluginState;

/**
 * Interface to hide details about the implementing class containing the {@link PluginClassResolver}
 * and registrations
 * @author ivo
 *
 */
public interface PluginRegistrationStore {
	
	/**
	 * Adds a path to the list of resource paths
	 * @param path the path to add
	 */
	public void addResourcePath(String path);
	
	/**
	 * Adds a class to the list of registrations
	 * @param className the name of the class
	 * @param file the file containing the class' bytes
	 */
	public void addClassRegistration(String className, File file) throws RegistrationException;
	
	/**
	 * Takes a resource identifier (eg. some/package/TheResource.extension) and the associated file to regsiter
	 * on the classpath 
	 * @param resourceIdentifier a resource identifier (eg. some/package/TheResource.extension) 
	 * @param file the associated file to regsiter
	 * @throws RegistrationException
	 */
	public void addClassPathResourceRegistration(String resourceIdentifier, File file) throws RegistrationException;
	
	/**
	 * Register a jarFile
	 * @param jarFile the File to register
	 */
	public void addJarRegistration(File jarFile) throws RegistrationException;
	
	/**
	 * Resolve classes registered earlier with this store
	 * @param className the {@link Class} to resolve
	 * @return the {@link Class} or null
	 * @throws ClassNotFoundException
	 */
	public Class resolvePluginClass(String className) throws ClassNotFoundException;
	
	/**
	 * Adds a {@link Plugin} to the registry
	 * @param plugin the {@link Plugin} to regsiter
	 * @param pluginState the state of the registration
	 * @param message an optional message
	 * @param throwable a detailed error
	 */
	public void addPluginToRegistry(Plugin plugin, PluginState pluginState, String message, Throwable throwable);
	
	/**
	 * Returns all registered {@link PluginRegistration}s
	 * @return the regsitered {@link PluginRegistration}s
	 */
	public List<PluginRegistration> getPluginRegistrations();

}
