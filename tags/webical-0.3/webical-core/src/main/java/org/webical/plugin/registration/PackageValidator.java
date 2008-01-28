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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.dao.EventDao;
import org.webical.dao.TaskDao;
import org.webical.dao.factory.DaoFactory;
import org.webical.plugin.PluginException;
import org.webical.plugin.classloading.ClassUtils;
import org.webical.plugin.classloading.DuplicateClassPathEntryException;
import org.webical.plugin.classloading.PluginClassLoader;
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
import org.webical.web.event.ExtensionHandler;
import org.webical.web.event.ExtensionListener;

/**
 * Validates extracted packages according to their manifest information
 * @author ivo
 *
 */
public class PackageValidator {
	private static Log log = LogFactory.getLog(PackageValidator.class);
	
	/** ClassLoader used to check the extending classes */
	private PluginClassLoader pluginClassLoader;
	
	/** Directory where the plugin package was extracted */
	private File packageExctractionDir;
	
	/** The Plugin representation of the manifest file */
	private Plugin manifestInfo;
	
	/** Map of classes registered in the manifest file, used to check the front/backend registrations */
	private Map<String, File> classRegistrations = new HashMap<String, File>();
	
	/**
	 * Creates a package validator for the given extgraxtiondirectory and manifest
	 * @param packageExctractionDir
	 * @param manifestInfo
	 */
	public PackageValidator(File packageExctractionDir, Plugin manifestInfo ) {
		this.packageExctractionDir = packageExctractionDir;
		this.manifestInfo = manifestInfo;
		
		//Set up with an empty map. Add the classes to load lateron
		pluginClassLoader = new PluginClassLoader(Thread.currentThread().getContextClassLoader());
	}
	
	/**
	 * validates the package
	 * @throws PluginException
	 */
	public void validatePackage() throws PluginException {
		if(manifestInfo == null) {
			log.error("Plugin manifest info not set");
			throw new PluginException("Plugin manifest info not set");
		}
		
		if(packageExctractionDir == null || !packageExctractionDir.exists()) {
			log.error("Plugin extraction directory not set or does not excist");
			throw new PluginException("Plugin extraction directory not set or does not excist");
		}
		
		if(log.isDebugEnabled()) {
			log.debug("Validating package from directory: " + packageExctractionDir.getAbsolutePath());
		}
		
		//Validate resource folders
		if(manifestInfo.getResourceFolders() != null 
				&& manifestInfo.getResourceFolders().getResourceFolder() != null 
				&& manifestInfo.getResourceFolders().getResourceFolder().size() > 0) {
			validateResourceFolders(manifestInfo.getResourceFolders());
		} else {
			if(log.isDebugEnabled()) {
				log.debug("no resource folders registered");
			}
		}
		
		//Validate jar folders
		if(manifestInfo.getJarFolders() != null
			&& manifestInfo.getJarFolders().getJarFolder() != null
			&& manifestInfo.getJarFolders().getJarFolder().size() > 0) {
				loadJarFiles(packageExctractionDir, manifestInfo.getJarFolders().getJarFolder());
				validateJarFolders(manifestInfo.getJarFolders());
		} else {
			if(log.isDebugEnabled()) {
				log.debug("No class registrations");
			}
		}
		
		//Validate class folders
		if(manifestInfo.getClassFolders() != null
			&& manifestInfo.getClassFolders().getClassFolder() != null
			&& manifestInfo.getClassFolders().getClassFolder().size() > 0) {
				try {
					loadClasses(packageExctractionDir, manifestInfo.getClassFolders().getClassFolder());
				} catch (RegistrationException e) {
					throw new PluginException("Could not load classes for plugin: " + manifestInfo.getPluginName(), e);
				}
				validateClassFolders(manifestInfo.getClassFolders());
		} else {
			if(log.isDebugEnabled()) {
				log.debug("No class folders");
			}
		}
		
		//Validate registrations
		if(manifestInfo.getRegistrations() != null) {
			validateRegistrations(manifestInfo.getRegistrations());
		} else {
			if(log.isDebugEnabled()) {
				log.debug("No registrations in manifest");
			}
		}
	}
	
	/**
	 * Loads all class from the given {@link ClassFolder}s
	 * @param root the base of the plugin extraction
	 * @param classFolders the list of {@link ClassFolder}
	 * @throws RegistrationException 
	 */
	private void loadClasses(File root, List<ClassFolder> classFolders) throws RegistrationException {
		log.debug("Loading all class files for validation");
		for (ClassFolder folder : classFolders) {
			File classDir = new File(root ,folder.getFileName());
			List<File> classFiles = FileUtils.traverseTree(classDir, FileUtils.CLASS_FILE_EXTENSION);
			
			for (File classFile : classFiles) {
				String fullyQaulifiedClassname = ClassUtils.fileToFullClassName(classDir, classFile);
				classRegistrations.put(fullyQaulifiedClassname, classFile);
				try {
					pluginClassLoader.addClassRegistration(fullyQaulifiedClassname, classFile);
				} catch (DuplicateClassPathEntryException e) {
					throw new RegistrationException("Could not register class: " + fullyQaulifiedClassname, e);
				}
			}
		}
		
		
	}
	
	/**
	 * @param root
	 * @param jarFolders
	 * @throws PluginException 
	 */
	private void loadJarFiles(File root, List<JarFolder> jarFolders) throws PluginException {
		log.debug("Loading all jar files for validation");
		for (JarFolder folder : jarFolders) {
			File jarDir = new File(root ,folder.getFileName());
			List<File> jarFiles = FileUtils.traverseTree(jarDir, FileUtils.JAR_FILE_EXTENSION);
			
			for (File file : jarFiles) {
				pluginClassLoader.readJarFile(file);
			}
			
		}
	}
	
	/**
	 * Validates registrations in the Plugin
	 * @param registrations the registrations to check
	 * @throws PluginException
	 */
	private void validateRegistrations(Registrations registrations) throws PluginException {
		if(registrations.getBackendPlugin() != null) {
			for(BackendPlugin backendPlugin : registrations.getBackendPlugin()) {
				validateBackendPlugin(backendPlugin);
			}
		} else {
			if(log.isDebugEnabled()) {
				log.debug("No backend plugins registrations");
			}
		}
		
		if(registrations.getFrontendPlugin() != null) {
			for(FrontendPlugin frontendPlugin : registrations.getFrontendPlugin()) {
				validateFrontendPlugin(frontendPlugin);
			}
		} else {
			if(log.isDebugEnabled()) {
				log.debug("No frontend plugins registrationsd");
			}
		}
	}
	
	/**
	 * Validates all {@link ResourceFolders}
	 * @param resourceFolders the {@link ResourceFolders}
	 * @throws PluginException
	 */
	private void validateResourceFolders(ResourceFolders resourceFolders) throws PluginException {
		for (ResourceFolder resourceFolder : resourceFolders.getResourceFolder()) {
			validateResourceFolder(resourceFolder);
		}
	}
	
	/**
	 * Validates all {@link ClassFolders}
	 * @param classFolders the {@link ClassFolders}
	 * @throws PluginException
	 */
	private void validateClassFolders(ClassFolders classFolders) throws PluginException {
		for (ClassFolder classFolder : classFolders.getClassFolder()) {
			validateClassFolder(classFolder);
		}
	}
	
	/**
	 * Validates all {@link JarFolders}
	 * @param jarFolders the {@link JarFolders}
	 * @throws PluginException
	 */
	private void validateJarFolders(JarFolders jarFolders) throws PluginException {
		for (JarFolder jarFolder : jarFolders.getJarFolder()) {
			validateJarFolder(jarFolder);
		}
	}
	
	/**
	 * Validates a {@link JarFolder} entry
	 * @param jarFolder the {@link JarFolder} to check
	 * @throws PluginException
	 */
	private void validateJarFolder(JarFolder jarFolder) throws PluginException {
		if(log.isDebugEnabled()) {
			log.debug("Checking jar folder: " + jarFolder.getFileName());
		}
		
		validateFilePresence(jarFolder.getFileName());
	}
	
	/**
	 * Validates a {@link ResourceFolder} entry
	 * @param resourceFolder the {@link ResourceFolder} to check
	 * @throws PluginException
	 */
	private void validateResourceFolder(ResourceFolder resourceFolder) throws PluginException {
		if(log.isDebugEnabled()) {
			log.debug("Checking resource folder: " + resourceFolder.getFileName());
		}
		
		validateFilePresence(resourceFolder.getFileName());
	}
	
	/**
	 * Validates a {@link ClassFolder} reference
	 * @param classFolder the {@link ClassFolder} to check
	 * @throws PluginException
	 */
	private void validateClassFolder(ClassFolder classFolder) throws PluginException {
		if(log.isDebugEnabled()) {
			log.debug("Checking class folder: " + classFolder.getFileName());
		}
		
		validateFilePresence(classFolder.getFileName());
	}
	
	/**
	 * Validates a Backend plugin
	 * @param backendPlugin the plugin to check
	 * @throws PluginException
	 */
	private void validateBackendPlugin(BackendPlugin backendPlugin) throws PluginException {
		//Check for the right interface implementation
		List<Class> implementedInterfaces = new ArrayList<Class>();
		
		if(backendPlugin.getDaoType().equals(DaoFactory.EVENT_DAO_TYPE)) {
			implementedInterfaces.add(EventDao.class);
		} else if(backendPlugin.getDaoType().equals(DaoFactory.TASK_DAO_TYPE)) {
			implementedInterfaces.add(TaskDao.class);
		} else {
			throw new PluginException(backendPlugin.getClassName() + " does not implement either the event or task dao interface");
		}
		
		File classFile = classRegistrations.get(backendPlugin.getClassName());
		if(classFile == null) {
			throw new PluginException("Classfile noted in the backend plugin: " + backendPlugin.getClassName() + " is not in de class-references list");
		}
		
		checkForImplementedInterfaces(backendPlugin.getClassName(), classFile, implementedInterfaces);
	}
	
	/**
	 * Validates a frontend plugin registration
	 * @param frontendPlugin the Frontend plugin to check
	 * @throws PluginException
	 */
	@SuppressWarnings("unchecked")
	private void validateFrontendPlugin(FrontendPlugin frontendPlugin) throws PluginException {
		//Check the extending class for the right interface implementation
		List<Class> implementedInterfaces = new ArrayList<Class>();
		implementedInterfaces.add(ExtensionListener.class);
		
		if(frontendPlugin.isExtendable()) {
			implementedInterfaces.add(ExtensionHandler.class);
		}
		
		File classFile = classRegistrations.get(frontendPlugin.getClassName());
		if(classFile == null) {
			throw new PluginException("Classfile noted in the frontendplugin: " + frontendPlugin.getClassName() + " is not in de class-references list");
		}
		
		checkForImplementedInterfaces(frontendPlugin.getClassName(), classFile, implementedInterfaces);
		
		//Check the extended class for the rigth interface (ExtensionHandler)
		if(log.isDebugEnabled()) {
			log.debug("Checking for implementations of ExtensionHandler interface in extended class: " + frontendPlugin.getExtendedClass());
		}
		
		implementedInterfaces.clear();
		implementedInterfaces.add(ExtensionHandler.class);
		
		try {
			checkForImplementedInterfaces(Thread.currentThread().getContextClassLoader().loadClass(frontendPlugin.getExtendedClass()), implementedInterfaces);
		} catch (ClassNotFoundException e) {
			log.error("Could not find extended class: " + frontendPlugin.getExtendedClass(), e);
			throw new PluginException(e);
		}
		
		
	}
	
	/**
	 * Checks if a resource/class file exists
	 * @param fileName the file
	 * @throws PluginException if the file does not exist
	 */
	private File validateFilePresence(String fileName) throws PluginException {
		File resourceFile = new File(this.packageExctractionDir.getAbsolutePath() + File.separator +  fileName);
		if(!resourceFile.exists()) {
			log.error("File in manifest does not excist, expected file: " + resourceFile.getAbsolutePath());
			throw new PluginException("File in manifest does noet excist, expected file: " + resourceFile.getAbsolutePath());
		}
		
		return resourceFile;
	}
	
	/**
	 * Chacks a raw Class file for the implementation of one or more interfaces
	 * @param className the name of the Class
	 * @param classFile the file containing the Class
	 * @param interfaces a List of interfaces the class should implement
	 * @throws PluginException
	 */
	private void checkForImplementedInterfaces(String className, File classFile, List<Class> interfaces) throws PluginException {
		//Use a PluginClassloader to load the class and check the implemented interfaces
		if(log.isDebugEnabled()) {
			log.debug("Loading extending class: " + className + " from file: " + classFile.getAbsolutePath());
		}
		
		Class extendingClass = null; 
		try {
			extendingClass = pluginClassLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			log.error("Could not load class: " + className + " from file: " + classFile.getAbsolutePath());
			throw new PluginException("Could not load class: " + className + " from file: " + classFile.getAbsolutePath());
		}
		
		checkForImplementedInterfaces(extendingClass, interfaces);

	}
	
	/**
	 * Checks a class for the implementation of one or more interfaces
	 * @param clazz the Class to check
	 * @param interfaces a List of interfaces the class should implement
	 * @throws PluginException
	 */
	@SuppressWarnings("unchecked")
	private void checkForImplementedInterfaces(Class clazz, List<Class> interfaces) throws PluginException {
		
		for (Class expectedInterface : interfaces) {
			if(log.isDebugEnabled()) {
				log.debug("Checking class: " + clazz.getName() + " for interface: " + expectedInterface.getName());
			}
			if(!expectedInterface.isAssignableFrom(clazz)) {
				log.error("Class: " + clazz.getName() + " does not implement required interface: " + expectedInterface.getName());
				throw new PluginException("Class: " + clazz.getName() + " does not implement required interface: " + expectedInterface.getName());
			}
		}
	}

}
