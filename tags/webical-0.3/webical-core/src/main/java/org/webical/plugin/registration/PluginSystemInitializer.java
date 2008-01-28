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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.dao.DaoException;
import org.webical.dao.EventDao;
import org.webical.dao.factory.DaoFactory;
import org.webical.plugin.PluginException;
import org.webical.plugin.classloading.ClassUtils;
import org.webical.plugin.file.FileUtils;
import org.webical.plugin.file.ZipFileExtractor;
import org.webical.plugin.jaxb.BackendPlugin;
import org.webical.plugin.jaxb.ClassFolder;
import org.webical.plugin.jaxb.ClassFolders;
import org.webical.plugin.jaxb.FrontendPlugin;
import org.webical.plugin.jaxb.JarFolder;
import org.webical.plugin.jaxb.JarFolders;
import org.webical.plugin.jaxb.Plugin;
import org.webical.plugin.jaxb.ResourceFolder;
import org.webical.plugin.jaxb.ResourceFolders;
import org.webical.plugin.registration.PluginRegistration.PluginState;
import org.webical.plugin.xml.PluginManifestReader;
import org.webical.settings.ApplicationSettingsException;
import org.webical.settings.ApplicationSettingsFactory;
import org.webical.web.event.ExtensionListener;
import org.webical.web.event.ExtensionListenerRegistrations;

/**
 *
 * Initializes the plugin framwork and registers the plugin contents
 * @author ivo
 *
 */
public class PluginSystemInitializer implements InitializingBean {
	private static Log log = LogFactory.getLog(PluginSystemInitializer.class);

	/** Set by Spring */

	/** The version of webical we're running */
	private String webicalVersion;

	/** the plugin registration store */
	PluginRegistrationStore pluginRegistrationStore;

	/** A list of paths to search for plugin packages */
	private List<String> pluginPaths;

	/** The path to extract the plugin packages to */
	private String pluginWorkPath;

	/** The file extension used for packages */
	private String pluginPackageExtension;

	/** Reference to the Plugin manifest reader (validator) */
	private PluginManifestReader pluginManifestReader;

	/** the DaoFactory */
	private DaoFactory daoFactory;

	//The directory to unpack the plugins
	private File workDir;

	/**
	 * Sets up all the plugins
	 * @throws ApplicationSettingsException
	 */
	public void setupPlugins() throws ApplicationSettingsException {
		log.info("Setting up plugins");

		//Get Application settings
		pluginPackageExtension = ApplicationSettingsFactory.getInstance().getApplicationSettings().getPluginPackageExtension();
		pluginPaths = new ArrayList<String>(ApplicationSettingsFactory.getInstance().getApplicationSettings().getPluginPaths());
		pluginWorkPath = ApplicationSettingsFactory.getInstance().getApplicationSettings().getPluginWorkPath();

		if(!pluginPathsSet()) {
			return;
		}

		workDir = new File(pluginWorkPath);

		//Loop over the plugin paths
		for (String pluginPath : pluginPaths) {
			log.debug("Handling plugin path: " + pluginPath);
			File[] pluginPackages = new File(pluginPath).listFiles(new PluginFilenameFilter(pluginPackageExtension));

			//Loop over the plugin packages
			for (File pluginPackage : pluginPackages) {
				String pluginExtractionPathName = workDir.getAbsolutePath() + File.separator + pluginPackage.getName() + "." + Math.abs(new Random().nextLong());
				File pluginExtractionPath = new File(pluginExtractionPathName);

				if(log.isDebugEnabled()) {
					log.debug("Extracting package: " + pluginPath + " to: " + pluginExtractionPathName);
				}

				Plugin plugin = null;

				try {
					//Extract the plugin
					pluginExtractionPath.mkdir();
					unpackPlugin(pluginPackage, pluginExtractionPath);

					File manifestFile = new File(pluginExtractionPath + File.separator + "META-INF" + File.separator + "manifest.xml");

					//Read the manifest
					plugin = getPluginManifestInfo(manifestFile);

					//Check the required webical version
					checkRequiredWebicalVersion(plugin);

					//Validate the package
					validatePackage(pluginExtractionPath, plugin);

					//Register the new Resource paths
					registerResourcePaths(pluginExtractionPath, plugin.getResourceFolders());

					//Register the new classes
					registerClasses(pluginExtractionPath, plugin.getClassFolders());

					//Register the jar files
					registerJarFiles(pluginExtractionPath, plugin.getJarFolders());

					//Register the backendPlugins
					registerBackendPlugins(plugin.getRegistrations().getBackendPlugin());

					//Register the frontendPlugins
					registerFrontendPlugins(plugin.getRegistrations().getFrontendPlugin());

				} catch (Exception e) {
					//Log and regsiter registration failure
					log.error("Could not register plugin: " + pluginPackage.getAbsolutePath(), e);
					if(plugin == null) {
						plugin = new Plugin();
						plugin.setPluginName("" + pluginPackage.getName() + " (No plugin info available)");
					}
					pluginRegistrationStore.addPluginToRegistry(plugin, PluginState.NOT_REGISTERED, e.getMessage(), e);
					continue;
				}
				//Register plugin
				pluginRegistrationStore.addPluginToRegistry(plugin, PluginState.REGISTERED, null, null);
			}
		}

	}

	protected boolean pluginPathsSet() {
		//Check the plugin directories
		if(pluginPaths != null && pluginPaths.size() > 0) {
			for (String pluginPath : pluginPaths) {
				File pluginPathFile = new File(pluginPath);
				if(!pluginPathFile.exists()) {
					throw new ExceptionInInitializerError("Plugin path does not exist: " + pluginPath);
				}

				if(!pluginPathFile.isDirectory()) {
					throw new ExceptionInInitializerError("Plugin path is not a directory: " + pluginPath);
				}
			}

			//Check the output directory
			if(pluginWorkPath == null || pluginWorkPath.length() == 0) {
				throw new ExceptionInInitializerError("No plugin output path is defined");
			}

			File pluginWorkPathFile = new File(pluginWorkPath);

			if(!pluginWorkPathFile.exists()) {
				throw new ExceptionInInitializerError("Plugin output path does not exist: " + pluginWorkPath);
			}

			if(!pluginWorkPathFile.isDirectory()) {
				throw new ExceptionInInitializerError("Plugin output path is not a directory: " + pluginWorkPath);
			}

			if(!pluginWorkPathFile.canWrite()) {
				throw new ExceptionInInitializerError("Plugin output path is not writeable: " + pluginWorkPath);
			}

			if(log.isDebugEnabled()) {
				log.debug("Plugin work directory: " + pluginWorkPath);
			}

			return true;
		} else {
			log.info("No plugin paths defided. Not setting up the plugin system");
			return false;
		}
	}

	/**
	 * Unpacks a plugin zip
	 * @param inputFile the file to unpack
	 * @param destinationDir the directory to unpack to
	 * @throws IOException
	 */
	protected void unpackPlugin(File inputFile, File destinationDir) throws IOException {
		ZipFileExtractor.unpackZipFile(inputFile, destinationDir);
	}

	/**
	 * Parses and validates a manifest file
	 * @param pluginManifest the manifest file
	 * @return a Plugin
	 * @throws PluginException
	 */
	protected Plugin getPluginManifestInfo(File pluginManifest) throws PluginException {
		if(!pluginManifest.exists() || !pluginManifest.canRead()) {
			log.error("Could not read plugin manifest: " + pluginManifest.getAbsolutePath());
			throw new PluginException("Could not read plugin manifest: " + pluginManifest.getAbsolutePath());
		}
		return pluginManifestReader.parsePluginManifest(pluginManifest);
	}

	/**
	 * Tests the webical version set in the plugins manifest with the running version of webical
	 * @param manifestInfo
	 * @throws PluginException if the required version is incorrect
	 */
	protected void checkRequiredWebicalVersion(Plugin manifestInfo) throws PluginException {
		if(StringUtils.isEmpty(manifestInfo.getWebicalVersion())) {
			throw new PluginException("Required webical version cannot be empty, please check the plugin's manifest");
		}
		if(!manifestInfo.getWebicalVersion().equals(webicalVersion)) {
			throw new PluginException("Required webical version ("
					+ manifestInfo.getWebicalVersion()
					+ ") does not match the current version ("
					+ webicalVersion
					+ ")");
		}
	}

	/**
	 * Validates the plugin package files
	 * @param extractionDir the extraction directory
	 * @param manifestInfo the plugin manifest
	 * @throws PluginException
	 */
	protected void validatePackage(File extractionDir, Plugin manifestInfo) throws PluginException {
		PackageValidator packageValidator = new PackageValidator(extractionDir, manifestInfo);
		packageValidator.validatePackage();
	}

	/**
	 * Register new resource paths with the webapplication
	 * @param pluginExtractionPath the extraction directory
	 * @param resourceFolders the resource folders
	 */
	protected void registerResourcePaths(File pluginExtractionPath, ResourceFolders resourceFolders) {
		if(resourceFolders == null || resourceFolders.getResourceFolder() == null) {
			return;
		}

		for (ResourceFolder resourceFolder : resourceFolders.getResourceFolder()) {
			String path = pluginExtractionPath.getAbsolutePath() + File.separator + resourceFolder.getFileName();
			log.debug("Registering resource folder: " + path);
			pluginRegistrationStore.addResourcePath(path);
		}
	}

	/**
	 * Register the plugin classes with the PluginClassResolver
	 * @param pluginExtractionPath the extraction directory
	 * @param classFolders the Class folders
	 * @throws RegistrationException
	 */
	protected void registerClasses(File pluginExtractionPath, ClassFolders classFolders) throws RegistrationException {
		if(classFolders == null || classFolders.getClassFolder() == null) {
			return;
		}

		for (ClassFolder classFolder : classFolders.getClassFolder()) {
			List<File> files = FileUtils.traverseTree(new File(pluginExtractionPath, classFolder.getFileName()), null);
			for (File file : files) {
				if(file.getName().endsWith(FileUtils.CLASS_FILE_EXTENSION)) {
					pluginRegistrationStore.addClassRegistration(ClassUtils.fileToFullClassName(new File(pluginExtractionPath, classFolder.getFileName()), file), file);
				} else {
					pluginRegistrationStore.addClassPathResourceRegistration(ClassUtils.fileToClassPathResourceIdentifier(new File(pluginExtractionPath, classFolder.getFileName()), file), file);
				}
			}
		}
	}

	/**
	 * Register jar files
	 * @param pluginExtractionPath
	 * @param jarFolders
	 * @throws RegistrationException
	 */
	protected void registerJarFiles(File pluginExtractionPath, JarFolders jarFolders) throws RegistrationException {
		if(jarFolders == null || jarFolders.getJarFolder() == null) {
			return;
		}

		for(JarFolder jarFolder : jarFolders.getJarFolder()) {
			List<File> files = FileUtils.traverseTree(new File(pluginExtractionPath, jarFolder.getFileName()), FileUtils.JAR_FILE_EXTENSION);
			for (File file : files) {
				pluginRegistrationStore.addJarRegistration(file);
			}
		}
	}

	/**
	 * Register the backend plugins
	 * @param backendPlugins the backend plugins
	 * @throws PluginException, ClassNotFoundException
	 */
	protected void registerBackendPlugins(List<BackendPlugin> backendPlugins) throws PluginException, ClassNotFoundException {
		for(BackendPlugin backendPlugin : backendPlugins) {
			Object backendPluginDao = null;
			try {
				backendPluginDao = pluginRegistrationStore.resolvePluginClass(backendPlugin.getClassName()).newInstance();
			} catch (InstantiationException exception) {
				log.error("Could not instantiate: " + backendPlugin.getClassName() + ". Is the class public?", exception);
				throw new PluginException("Could not instantiate: " + backendPlugin.getClassName() + ". Is the class public?", exception);
			} catch (IllegalAccessException exception) {
				log.error("Could not instantiate: " + backendPlugin.getClassName() + ". Is the class public?", exception);
				throw new PluginException("Could not instantiate: " + backendPlugin.getClassName() + ". Is the class public?", exception);
			}

			if(DaoFactory.EVENT_DAO_TYPE.equals(backendPlugin.getDaoType())) {
				try {
					daoFactory.addEventDaoRegistration(backendPlugin.getCalendarType(), (EventDao)backendPluginDao);
				} catch (DaoException e) {
					throw new PluginException(e);
				}
			} else {
				//TODO @see ivo
				log.info("TODO -> IMPLEMENT TASK DAO PLUGIN LIST IN DAOFACTORY");
			}
		}
	}

	/**
	 * Registers the frontend plugins
	 * @param frontendPlugins the frontend plugins
	 * @throws PluginException, ClassNotFoundException
	 */
	protected void registerFrontendPlugins(List<FrontendPlugin> frontendPlugins) throws PluginException, ClassNotFoundException {
		for (FrontendPlugin plugin : frontendPlugins) {
			ExtensionListener extensionListener = null;
			try {
				extensionListener = (ExtensionListener) pluginRegistrationStore.resolvePluginClass(plugin.getClassName()).newInstance();
			} catch (InstantiationException exception) {
				log.error("Could not instantiate: " + plugin.getClassName() + ". Is the class public?", exception);
				throw new PluginException("Could not instantiate: " + plugin.getClassName() + ". Is the class public?", exception);
			} catch (IllegalAccessException exception) {
				log.error("Could not instantiate: " + plugin.getClassName() + ". Is the class public?", exception);
				throw new PluginException("Could not instantiate: " + plugin.getClassName() + ". Is the class public?", exception);
			}
			Class extendedClass = pluginRegistrationStore.resolvePluginClass(plugin.getClassName());
			ExtensionListenerRegistrations.addExtensionListenerRegistration(extendedClass, extensionListener);
		}
	}

	/**
	 * Checks the variables (hopefully) set by spring
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {

		if(pluginRegistrationStore == null) {
			throw new ExceptionInInitializerError("No pluginRegistrationStore available");
		}

		if(pluginManifestReader == null) {
			throw new ExceptionInInitializerError("No plugin mainfest reader set by spring");
		}

		if(StringUtils.isEmpty(webicalVersion)) {
			throw new ExceptionInInitializerError("Webical version is required here");
		} else {
			log.info("Webical version: " + webicalVersion);
		}

	}

	///////////////////////////////////
	/// Setters (mainly for Spring) ///
	///////////////////////////////////

	/**
	 * @param pluginManifestReader
	 */
	public void setPluginManifestReader(PluginManifestReader pluginManifestReader) {
		this.pluginManifestReader = pluginManifestReader;
	}

	/**
	 * @param daoFactory
	 */
	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * @param pluginRegistrationStore
	 */
	public void setPluginRegistrationStore(
			PluginRegistrationStore pluginRegistrationStore) {
		this.pluginRegistrationStore = pluginRegistrationStore;
	}

	/**
	 * @param webicalVersion the webicalVersion to set
	 */
	public void setWebicalVersion(String webicalVersion) {
		this.webicalVersion = webicalVersion;
	}

}
