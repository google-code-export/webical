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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.plugin.PluginException;
import org.webical.plugin.file.FileUtils;

/**
 * 
 * ClassLoader that handles loading classes registered in a HashMap
 * @author ivo
 *
 */
public class PluginClassLoader extends ClassLoader {
	
	private static final String META_INF = "META-INF/";
	protected static final String JAR_EXTRACTION_EXTENSION = "_EXTRACTED_JAR_RESOURCES";
	
	private static Log log = LogFactory.getLog(PluginClassLoader.class);
	
	/** Registered class files **/
	private Map<String, File> classNameToFileMap;
	
	/**
	 * Resources registered on the classpath
	 */
	private Map<String, File> resourceNameToFileMap;
	
	/** Loaded class bytes from jar files */
	private Hashtable<String, byte[]> classArrays;
	
	/**
	 * This map holds all the class definitions so that the classloader always returns the same Class object
	 * for a given classname (this is required, see the java language specs)
	 */
	private HashMap<String, Class> definedClassesMap;
	
	/**
	 * @param classLoader the parent ClassLoader
	 */
	public PluginClassLoader(ClassLoader classLoader) {
		super(classLoader);
		classNameToFileMap = new HashMap<String, File>();
		classArrays = new Hashtable<String, byte[]>();
		resourceNameToFileMap = new HashMap<String, File>();
		definedClassesMap = new HashMap<String, Class>();
	}

	/**
	 * Override of the default method. First tries the parent {@link ClassLoader}
	 * (Standard practice) and then our implementation (first the jar regsitrations and then the class registrations)
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if(StringUtils.isEmpty(className)) {
			throw new ClassNotFoundException("Cannot load a class without a name: " + className);
		}
		
		if(definedClassesMap.containsKey(className)) {
			return definedClassesMap.get(className);
		}
		
		//Try the default ClassLoader (standard practice)
		try {
			if(log.isDebugEnabled()) {
				log.debug("Trying to load class: " + className + " through the default ClassLoader");
			}
			return getParent().loadClass(className);
		} catch (ClassNotFoundException e) {
			if(log.isDebugEnabled()) {
				log.debug("Default ClassLoader could not load class: " + className);
			}
		}
		
		byte[] classBytes = null;
		
		//First try the loaded jar files
		log.debug("Trying to load class: " + className + " from the registered jar files");
		classBytes = (byte[]) classArrays.get(className);
		if (classBytes != null) {
			log.debug("Class found in jar file");
			Class clazz = defineClass(className, classBytes, 0, classBytes.length, null);
			if(clazz != null) {
				definedClassesMap.put(className, clazz);
			}
			return clazz;
		}
		
		//Not found, use our implementation
		
		//Locate and read in the class file
		File classFile = classNameToFileMap.get(className);
		if(classFile == null) {
			log.error("Class does not seem to be registered: " + className);
			throw new ClassNotFoundException("Class does not seem to be registered: " + className);
		}
		if(!classFile.canRead()) {
			log.error("Class does not seem to be readable: " + className + " file: " + classFile.getAbsolutePath());
			throw new ClassNotFoundException("Class does not seem to be readable: " + className + " file: " + classFile.getAbsolutePath());
		}
		FileInputStream classInputStream = null;
		
		try {
			classInputStream = new FileInputStream(classFile);
			classBytes = getBytesFromFile(classInputStream, classFile.length());
		} catch (FileNotFoundException e) {
			log.error("Class: " + className + " could not be found at registered location: " + classFile.getAbsolutePath());
			throw new ClassNotFoundException("Class: " + className + " could not be found at registered location: " + classFile.getAbsolutePath());
		} catch (IOException e) {
			log.error("Class: " + className + " could not be read from file " + classFile.getAbsolutePath(), e);
			throw new ClassNotFoundException("Class: " + className + " could not be read from file " + classFile.getAbsolutePath(), e);
		} finally {
			if(classInputStream != null) {
				try {
					classInputStream.close();
				} catch (IOException e) {
					log.error("Could not close FileInputStream for file: " + classFile.getAbsolutePath());
				}
			}
		}
		
		if(log.isDebugEnabled()) {
			log.debug("Successfully loaded in the bytes for class: " + className);
		}
		
		//Define and return the class
		try {
			Class clazz = defineClass(className.substring(className.lastIndexOf("/") + 1), classBytes, 0, classBytes.length);
			if(clazz != null) {
				definedClassesMap.put(className, clazz);
			}
			return clazz;
		} catch (ClassFormatError e) {
			log.error("Error parsing class file: " + classFile.getAbsolutePath(), e);
			throw new ClassNotFoundException("Error parsing class file: " + classFile.getAbsolutePath(), e);
		}
		
		
	}
	
    /**
     * Reads in a File
     * @param fileInputStream the fileinputstream
     * @param fileLength the length of the file
     * @return a byte array containing the file contents 
     * @throws IOException on IO error
     */
    public static byte[] getBytesFromFile(FileInputStream fileInputStream, long fileLength) throws IOException  {
    
        if (fileLength > Integer.MAX_VALUE) {
            // File is too large
        	throw new IOException("File is to large: " + fileLength + " exceeds to maximum: " + Integer.MAX_VALUE);
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)fileLength];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = fileInputStream.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file");
        }
    
        // Close the input stream and return bytes
        fileInputStream.close();
        return bytes;
    }
    
    
    
	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#findResource(java.lang.String)
	 */
	@Override
	protected URL findResource(String name) {
		log.debug("Trying to find resource: " + name);
		
		if(!StringUtils.isEmpty(name)) {
			File file = resourceNameToFileMap.get(name);
			if(file != null) {
				log.debug("Found resource: " + name + " in file: " + file.getAbsolutePath() + " readable: " + file.canRead());
				try {
					URL url = file.toURL();
					log.debug("Returning url:" + url.toString());
					return url;
				} catch (MalformedURLException e) {
					log.error("Could not create url from stored file for resource: " + name, e);
				}
			}
		}
		
		log.warn("Could not find resource: " + name + " trying default classloader");
		URL resource = getParent().getResource(name);
		if(resource == null) {
			log.warn("No luck with the default classloader aswell for resource: " + name);
		}
		return resource;
	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#findResources(java.lang.String)
	 */
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		Hashtable<String, URL> resources = new Hashtable<String, URL>();
		resources.put(name, findResource(name));
		return resources.elements();
	}

	/**
	 * Reads in all classes in a jar file for later reference
	 * @param name the ful name and path of the jar file
	 * @throws PluginException 
	 */
	public void readJarFile(File jarFile) throws PluginException {
		//Firstly check input
		if(jarFile == null) {
			log.error("Cannot load jar without a reference...");
			throw new PluginException("Cannot load jar without a reference...");
		}
		
		JarInputStream jis;
		JarEntry je;

		if(!jarFile.exists()) {
			log.error("Jar does not exist: " + jarFile.getAbsolutePath());
			throw new PluginException("Jar does not exist: " + jarFile.getAbsolutePath());
		}
		if(!jarFile.canRead()) {
			log.error("Jar is not readable: " + jarFile.getAbsolutePath());
			throw new PluginException("Jar is not readable: " + jarFile.getAbsolutePath());
		}
		log.debug("Loading jar file " + jarFile);

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(jarFile);
			jis = new JarInputStream(fis);
		} catch (IOException ioe) {
			log.error("Can't open jar file " + jarFile, ioe);
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					log.error("Could not close filehandle to: " + jarFile.getAbsolutePath(), e);
					throw new PluginException("Could not close filehandle to: " + jarFile.getAbsolutePath(), e);
				}
			}
			throw new PluginException("Can't open jar file " + jarFile, ioe);
		}

		//Loop over the jarfile entries
		try {
			while ((je = jis.getNextJarEntry()) != null) {
				String jarEntryName = je.getName();
				
				//Skip the META-INF dir
				if(jarEntryName.startsWith(META_INF)) {
					continue;
				}
				
				if (jarEntryName.endsWith(FileUtils.CLASS_FILE_EXTENSION)) {
					//Extract java class
					loadClassBytes(jis, ClassUtils.fileToFullClassName(jarEntryName));
				} else if(je.isDirectory()) {
					//Extract directory
					unpackDirectory(jarEntryName, jarFile);
				} else {
					//it could be an image or audio file so let's extract it and store it somewhere for later reference
					try {
						extractJarResource(jis, jarEntryName, jarFile);
					} catch (Exception e) {
						log.error("Cannot cache jar resource: " + jarEntryName + " from jar file: " + jarFile.getAbsolutePath(), e);
						throw new PluginException("Cannot cache jar resource: " + jarEntryName + " from jar file: " + jarFile.getAbsolutePath(), e);
					}
				}
				jis.closeEntry();
			}
		} catch (IOException ioe) {
			log.error("Badly formatted jar file: " + jarFile.getAbsolutePath(), ioe);
			throw new PluginException("Badly formatted jar file: " + jarFile.getAbsolutePath(), ioe);
		} finally {
			if(jis != null) {
				try {
					jis.close();
				} catch (IOException e) {
					log.error("Could not close connection to jar: " + jarFile.getAbsolutePath(), e);
					throw new PluginException("Could not close connection to jar: " + jarFile.getAbsolutePath(), e);
				}
			}
		}
	}

	/**
	 * loads the class' bytes into the {@link Hashtable} from a jar
	 * @param jis the {@link JarInputStream} to read from
	 * @param className the name of the jar
	 * @throws IOException 
	 */
	private void loadClassBytes(JarInputStream jis, String className) throws IOException {
		if(!classArrays.containsKey(className)) {
			try {
				classArrays.put(className, readBytesFromJarEntry(jis));
			} catch (IOException ioe) {
				log.error("Error reading entry " + className, ioe);
				throw ioe;
			}
		} else {
			//just not include this entry
			log.warn("Class: " + className + " was already registerd from a jar file");
		}
	}
	
	
	/**
	 * Extracts a jar resources and adds it to the resourceNameToFileMap
	 * @param jis the {@link JarInputStream} to read from
	 * @param name the name of the jarResource
	 * @param jarFile the jar {@link File}
	 * @throws IOException 
	 */
	private void extractJarResource(JarInputStream jis, String name, File jarFile) throws IOException {
		File extactionDir = getExtractionDirectory(jarFile);
		File extractedFile = new File(extactionDir, name);
		String resourceIdentifier = ClassUtils.fileToClassPathResourceIdentifier(extactionDir, extractedFile);
		
		log.debug("Extracting jar resource: " + resourceIdentifier + "  it to: " + extractedFile.getAbsolutePath());
		
		FileUtils.streamToFile(jis, extractedFile, false);
		resourceNameToFileMap.put(resourceIdentifier, extractedFile);
	}
	
	/**
	 * @param jarFile
	 * @return
	 */
	private File getExtractionDirectory(File jarFile) {
		File extactionDir = new File(jarFile.getAbsolutePath() + JAR_EXTRACTION_EXTENSION);
		if(!extactionDir.exists()) {
			log.debug("Creating directory to extract resource files from jar: " + jarFile.getAbsolutePath() + " -> " + extactionDir.getAbsolutePath());
			extactionDir.mkdir();
		}
		return extactionDir;
	}
	
	/**
	 * @param name
	 * @param jarFile
	 */
	private void unpackDirectory(String name, File jarFile) {
		File extactionDir = getExtractionDirectory(jarFile);
		File extractedDirectory = new File(extactionDir, name);
		if(!extractedDirectory.exists()) {
			log.debug("Creating new directory: " + extractedDirectory.getAbsolutePath());
			extractedDirectory.mkdir();
		}
	}
	
	/**
	 * @param jis the {@link JarInputStream}
	 * @return the bytes
	 * @throws IOException
	 */
	private byte[] readBytesFromJarEntry(JarInputStream jis) throws IOException{
		BufferedInputStream jarBuf = new BufferedInputStream(jis);
		ByteArrayOutputStream jarOut = new ByteArrayOutputStream();
		int buffer;
		
		while ((buffer = jarBuf.read()) != -1) {
			jarOut.write(buffer);
		}
		return jarOut.toByteArray();
	}
	
	//////////////////////////////////////////////////
	// Getters for the Class/Resource registrations //
	//////////////////////////////////////////////////
	
	/**
	 * Takes a fully qualified classname (eg some.package.SomeClass) and the associated file
	 * @param fullyQualifiedClassName a fully qualified classname
	 * @param classFile the file containing the class
	 * @throws DuplicateClassPathEntryException if the class was already registered
	 */
	public void addClassRegistration(String fullyQualifiedClassName, File classFile) throws DuplicateClassPathEntryException {
		if(!StringUtils.isEmpty(fullyQualifiedClassName) && classFile != null) {
			if(!classNameToFileMap.containsKey(fullyQualifiedClassName)) {
				log.debug("Registering class: " + fullyQualifiedClassName + " with associated file: " + (classFile!=null?classFile.getAbsolutePath():null));
				classNameToFileMap.put(fullyQualifiedClassName, classFile);
			} else {
				log.error("Class: " + fullyQualifiedClassName + " was already registerd from a plugin class file");
				throw new DuplicateClassPathEntryException("Class: " + fullyQualifiedClassName + " was already registerd from a plugin class file");
			}
		} else {
			log.error("Not registering class: " + fullyQualifiedClassName + " with file: " + (classFile!=null?classFile.getAbsolutePath():null));
		}
	}
	
	/**
	 * Takes a resource identifier (eg. some/package/TheResource.extension) and the associated file to regsiter
	 * on the classpath 
	 * @param resourceIdentifier a resource identifier (eg. some/package/TheResource.extension) 
	 * @param file the associated file to regsiter
	 * @throws DuplicateClassPathEntryException if a resource with the same identifier was already registered.
	 */
	public void addClassPathResourceRegistration(String resourceIdentifier, File file) throws DuplicateClassPathEntryException {
		if(!StringUtils.isEmpty(resourceIdentifier) && file != null) {
			if(!resourceNameToFileMap.containsKey(resourceIdentifier)) {
				log.debug("Registering resource on classpath: " + resourceIdentifier + " with associated file: " + (file!=null?file.getAbsolutePath():null));
				resourceNameToFileMap.put(resourceIdentifier, file);
			} else {
				log.error("Resource: " + resourceIdentifier + " was already registerd from a plugin class file");
				throw new DuplicateClassPathEntryException("Resource: " + resourceIdentifier + " was already registerd from a plugin class file");
			}
		} else {
			log.error("Not registering resource: " + resourceIdentifier + " with file: " + (file!=null?file.getAbsolutePath():null));
		}
	}

}
