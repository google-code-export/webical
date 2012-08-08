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

import junit.framework.TestCase;

import org.springframework.core.io.FileSystemResource;
import org.webical.plugin.PluginException;
import org.webical.plugin.jaxb.Plugin;

/**
 * Tests the pluginmanifest reader incl validation
 * @author ivo
 *
 */
public class PluginManifestReaderTest extends TestCase {
	
	
	/**
	 * Tests with a valid mainfest
	 * @throws Exception 
	 */
	public void testValidManifest() throws Exception {
		FileSystemResource validManifest = new FileSystemResource("src/test/resources/org/webical/plugin/xml/valid-manifest.xml");
		PluginManifestReader manifestReader = new PluginManifestReader();
		manifestReader.afterPropertiesSet();
		
		Plugin plugin = manifestReader.parsePluginManifest(validManifest.getFile());
		
		assertEquals(1, plugin.getClassFolders().getClassFolder().size());
		assertEquals(1, plugin.getResourceFolders().getResourceFolder().size());
		assertEquals(1, plugin.getRegistrations().getBackendPlugin().size());
		assertEquals(1, plugin.getRegistrations().getFrontendPlugin().size());
		
	}
	
	
	/**
	 * Tests with an invalid manifest
	 * @throws Exception 
	 */
	public void testInvalidManifest() throws Exception {
		FileSystemResource validManifest = new FileSystemResource("src/test/resources/org/webical/plugin/xml/invalid-manifest.xml");
		PluginManifestReader manifestReader = new PluginManifestReader();
		manifestReader.afterPropertiesSet();
		
		try {
			manifestReader.parsePluginManifest(validManifest.getFile());
			fail("Validation should have complained...");
		} catch (PluginException e) { }
		
	}

}
