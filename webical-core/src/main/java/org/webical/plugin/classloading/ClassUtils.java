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

package org.webical.plugin.classloading;

import java.io.File;

import org.apache.commons.lang.StringUtils;

/**
 * Central class for Class operations
 * @author ivo
 *
 */
public class ClassUtils {

	/**
	 * Creates a identifier used for classpath entries (eg some/package/SomeResource.someextension)
	 * @param baseFolder the basefolder containing the classes and resources
	 * @param resource the resource in question
	 * @return
	 */
	public static String fileToClassPathResourceIdentifier(File baseFolder, File resource) {
		if (resource == null || StringUtils.isEmpty(resource.getAbsolutePath())) {
			return null;
		}
		return getRelativePath(baseFolder, resource).replace(File.separator, "/");		// For Windows
	}

	/**
	 * @param classFile the class file
	 * @param baseFolder the base to start from (eg the classes folder)
	 * @return
	 */
	public static String fileToFullClassName(File baseFolder, File classFile) {
		if (classFile == null || StringUtils.isEmpty(classFile.getAbsolutePath())) {
			return null;
		}
		String relPath = getRelativePath(baseFolder, classFile);
		return fileToFullClassName(relPath);
	}

	/**
	 * Transforms the relative classfile's path into a fully qualified classname: <br />
	 * some/package/SomeClass.class becomes some.package.SomeClass.
	 * For full filepaths use {@link ClassUtils#fileToFullClassName(File, File)}
	 * @param relativeClassFilePath the relative path to the class file
	 * @return the fully qualified classname
	 */
	public static String fileToFullClassName(String relativeClassFilePath) {
		if (StringUtils.isEmpty(relativeClassFilePath)) return null;
		String relClassPath = relativeClassFilePath.substring(0, relativeClassFilePath.lastIndexOf("."));
		String fullClassName = relClassPath.replace(File.separator, ".");
		return fullClassName.replace("/", ".");		// for windows
	}

	/**
	 * Helper function to get the relative path for subject starting at baseFolder
	 * @param baseFolder the base
	 * @param subject the subject
	 * @return
	 */
	protected static String getRelativePath(File baseFolder, File subject) {
		return subject.getAbsolutePath().substring(baseFolder.getAbsolutePath().length() + 1);
	}
}
