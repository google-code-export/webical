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

package org.webical.test.aspect.web;

import org.apache.commons.logging.LogFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.webical.test.web.WebicalApplicationTest;
import org.webical.test.web.annotation.Accessibility;
import org.webical.web.app.WebicalSession;

/**
 * Aspect to change the accessibility state of the session for testing
 * Add {@see Accessibility}(accessible=true) to set the session to accessible,
 * {@see Accessibility}(accessible=false) to change the session to non-accessible.
 *
 * @author Mattijs Hoitink
 */
public aspect TestAccessibilityAspect {

	private static org.apache.commons.logging.Log log = LogFactory.getLog(TestAccessibilityAspect.class);

	pointcut accessibilityInvocation() : execution(@Accessibility * *());

	void around() throws Exception : accessibilityInvocation() && !cflowbelow(accessibilityInvocation())
	{
		// get the current accessbility value
		WebicalApplicationTest testApplication = (WebicalApplicationTest) thisJoinPoint.getThis();
		WebicalSession session = testApplication.getTestSession();
		boolean previousAccessibilityState = session.isAccessible();

		boolean requestedAccessibilityState = false;

		String currentMethod = thisJoinPoint.getSignature().getName();
		Method declaredMethods[] = thisJoinPoint.getTarget().getClass().getDeclaredMethods();

		// Check if there are methods in the class
		if (declaredMethods != null && declaredMethods.length > 0) {
			for (int i = 0; i < declaredMethods.length; i++) {
				Method method = declaredMethods[i];
				// Loop through the methods and compare it with the current method
				if (method.getName().equals(currentMethod)  &&
					(method.getParameterTypes() != null || method.getParameterTypes().length != 0)) {
					Annotation annotation[] = method.getAnnotations();
					// Current method is found, get the annotations for this method
					if (annotation != null && annotation.length > 0) {
						for (int j = 0; j < annotation.length; ++ j) {
							// get the Accessibility method
							if (annotation[j] instanceof org.webical.test.web.annotation.Accessibility) {
								//save the requested state and set it in the session
								requestedAccessibilityState = ((org.webical.test.web.annotation.Accessibility) annotation[j]).accessible();
								log.debug("Annotation of type Accessibility found, value = " + requestedAccessibilityState);
							} else {
								// The annotation is not of the type Accessibility
							}
						}
					} else {
						// No annotations found
					}
				} else {
					// No parameters found
				}
			}
		} else {
			log.debug("No methods found in declared class of this jointpoint");
		}

		try {
			// change the accessibility in the session to the new value
			session.setAccessible(requestedAccessibilityState);
            proceed();
        } finally {
        	// change the accessibility of the session back to it's previous value
        	session.setAccessible(previousAccessibilityState);
        }
	}
}
