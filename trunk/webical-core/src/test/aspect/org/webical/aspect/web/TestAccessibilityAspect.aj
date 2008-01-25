package org.webical.aspect.web;

import org.apache.commons.logging.LogFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.app.WebicalSession;
import org.webical.web.annotation.Accessibility;

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

	void around() throws Exception : accessibilityInvocation() && !cflowbelow(accessibilityInvocation()) {

		// get the current accessbility value
		WebicalApplicationTest testApplication = (WebicalApplicationTest) thisJoinPoint.getThis();
		WebicalSession session = testApplication.getTestSession();
		boolean previousAccessibilityState = session.isAccessible();

		boolean requestedAccessibilityState = false;

		String currentMethod = thisJoinPoint.getSignature().getName();
		Method declaredMethods[] = thisJoinPoint.getTarget().getClass().getDeclaredMethods();

		// Check if there are methods in the class
		if(declaredMethods != null || declaredMethods.length != 0) {
			for (int i = 0; i < declaredMethods.length; i++) {
				Method method = declaredMethods[i];
				// Loop through the methods and compare it with the current method
				if(method.getName().equals(currentMethod) && method.getParameterTypes() != null || method.getParameterTypes().length != 0) {
					Annotation annotation[] = method.getAnnotations();
					// Current method is found, get the annotations for this method
					if(annotation != null || annotation.length != 0) {
						for (int j = 0; j < annotation.length; j++) {
							// get the Accessibility method
							if(annotation[j] instanceof org.webical.web.annotation.Accessibility) {
								//save the requested state and set it in the session
								requestedAccessibilityState = ((org.webical.web.annotation.Accessibility) annotation[j]).accessible();
								log.debug("Annotation of type Accessibility found, value = " + requestedAccessibilityState);
							}
							else {
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
