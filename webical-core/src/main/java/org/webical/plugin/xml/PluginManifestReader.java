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

package org.webical.plugin.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.webical.plugin.PluginException;
import org.webical.plugin.file.FileUtils;
import org.webical.plugin.jaxb.Plugin;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class that validates and unmarshalls the plugin manifest
 * @author ivo
 *
 */
public class PluginManifestReader implements InitializingBean {
	private static Log log = LogFactory.getLog(PluginManifestReader.class);
	
	/** The plugin.xsd to validate against */
	private final static String  webicalPluginSchemaLocation =  "plugin.xsd";
	
	/** The file reference */
	private static File webicalPluginSchema;
	
	//Used for JAXP validation
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	/**
	 * @param pluginManifest the manifest to unMarshall
	 * @return a Plugin
	 * @throws PluginException with wrapped exception
	 */
	public Plugin parsePluginManifest(File pluginManifest) throws PluginException {
		if(pluginManifest == null) {
			log.error("Null reference passed for pluginManifest file");
			return null;
		}
		
		if(!pluginManifest.exists()) {
			log.error("File: " + pluginManifest.getAbsolutePath() + " does not excist");
			throw new PluginException("File: " + pluginManifest.getAbsolutePath() + " does not excist");
		}
		
		Document document = null;
		try {
			document = validatePluginManifest(pluginManifest);
		} catch (ParserConfigurationException exception) {
			log.error("Exception while configuring parser", exception);
			throw new PluginException("File: " + pluginManifest.getAbsolutePath() + " does not excist", exception);
		} catch (SAXException exception) {
			log.error("File: " + pluginManifest.getAbsolutePath() + " is not valid: " + exception.getMessage(), exception);
			throw new PluginException("File: " + pluginManifest.getAbsolutePath() + " is not valid: " + exception.getMessage(), exception);
		} catch (IOException exception) {
			log.error("File: " + pluginManifest.getAbsolutePath() + " could not be read", exception);
			throw new PluginException("File: " + pluginManifest.getAbsolutePath() + " could not be read", exception);
		}
		
		try {
			return unMarshall(document);
		} catch (JAXBException e) {
			log.error(e,e);
			throw new PluginException("Could not unmarshall the plugin manifest for file: " + pluginManifest.getName(), e);
		}		
	}
	
	/**
	 * Validates the pluginManifest to the webicalPluginSchemaSource
	 * @param pluginManifest the manifest to validate
	 * @return the Domdocument build to validate
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws IOException on error
	 */
	private Document validatePluginManifest(File pluginManifest) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		
		factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		if(log.isDebugEnabled()) {
			log.debug("Xsd resource property: " + webicalPluginSchema);
			log.debug("Setting xsd to: " + webicalPluginSchema);
		}
		
		factory.setAttribute(JAXP_SCHEMA_SOURCE, webicalPluginSchema);
		
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		documentBuilder.setErrorHandler(new PluginValidationErrorHandler(pluginManifest));
		
		return documentBuilder.parse(pluginManifest);
	}
	
	/**
	 * Unmarshalls the pluginManifest Document
	 * @param pluginManifestDocument the Document representation of a PluginManifest
	 * @return a Plugin containing the manifest information
	 * @throws JAXBException on error
	 */
	private Plugin unMarshall( Document pluginManifestDocument ) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext .newInstance("org.webical.plugin.jaxb");
		
		
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (Plugin)unmarshaller.unmarshal(pluginManifestDocument);
	}
	
	/**
	 * Prepares the plugin schema
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		//load the plugin xsd
		try {
			loadPluginSchema();
		} catch (IOException e) {
			log.fatal("Could not load schema file", e);
			throw e;
		}
		
		if(webicalPluginSchema == null || !webicalPluginSchema.exists()) {
			log.error("Xsd does not exist: " + (webicalPluginSchema != null ? webicalPluginSchema.getAbsolutePath() : null) + " (relative: " + webicalPluginSchemaLocation + ")");
			throw new ExceptionInInitializerError("Xsd does not exist: " + (webicalPluginSchema != null ? webicalPluginSchema.getAbsolutePath() : null) + " (relative: " + webicalPluginSchemaLocation + ")");
		}
		
	}
	
	/**
	 * Loads the plugin schema and saves it to a temporary file for later reference
	 * @throws IOException 
	 */
	private void loadPluginSchema() throws IOException {
		//load the plugin xsd
		InputStream pluginSchemaInputStream = getClass().getClassLoader().getResourceAsStream(webicalPluginSchemaLocation);
		
		webicalPluginSchema = File.createTempFile("webical-", "-temporary-plugin-schema-file.xsd");
		FileUtils.streamToFile(pluginSchemaInputStream, webicalPluginSchema, true);
	}
	
	/**
	 * ErrorHandler implementation that logs exceptions and rethrows them
	 * @author ivo
	 *
	 */
	private class PluginValidationErrorHandler implements ErrorHandler {
		private File pluginManifest;
		
		public PluginValidationErrorHandler(File pluginManifest) {
			this.pluginManifest = pluginManifest;
		}

		public void warning(SAXParseException exception) throws SAXException {
			log.warn("Warning pluginmanifest: " + pluginManifest.getAbsolutePath() + " not correct: " + exception);
			throw exception;
		}

		public void error(SAXParseException exception) throws SAXException {
			log.error("Error pluginmanifest: " + pluginManifest.getAbsolutePath() + " not correct: " + exception);
			throw exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			log.error("Fatal error pluginmanifest: " + pluginManifest.getAbsolutePath() + " not correct: " + exception);
			throw exception;
		}
		
	}
}
