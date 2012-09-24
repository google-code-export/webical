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

package org.webical.test.web;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.io.File;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.injection.ComponentInjector;
import org.apache.wicket.injection.ConfigurableInjector;
import org.apache.wicket.injection.IFieldValueFactory;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.test.AnnotApplicationContextMock;
import org.apache.wicket.util.tester.DummyHomePage;
import org.apache.wicket.util.tester.WicketTester;

import org.webical.ApplicationSettings;
import org.webical.web.app.WebicalSession;

import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockSettingsManager;
import org.webical.test.manager.impl.mock.MockUserManager;
import org.webical.test.web.annotation.Accessibility;

/**
 * Abstract base class for the wicket tests
 * @author ivo
 *
 */
public abstract class WebicalApplicationTest extends TestCase {

	protected WicketTester wicketTester = null;
	protected AnnotApplicationContextMock annotApplicationContextMock = null;
	protected DummyHomePage dummyHomePage = null;
	protected DateFormat dateFormat = null;
	protected DateFormat dateFormatFull = null;

	protected WebicalSession webicalSession = null;
	/** The application context working directory */
	protected File contextWorkDir = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		//Setup a mock injector
		InjectorHolder.setInjector(new ConfigurableInjector() {
			@Override
			protected IFieldValueFactory getFieldValueFactory() {
				return new IFieldValueFactory() {

					public Object getFieldValue(Field field, Object fieldOwner) {
						return null;
					}

					public boolean supportsField(Field field) {
						return false;
					}
				};
			}
		});

		//Set up the WicketTester
		wicketTester = new WicketTester(new WicketTester.DummyWebApplication() {
			//** override so that a WebicalSession is returned */
			/* (non-Javadoc)
			 * @see wicket.protocol.http.WebApplication#newSession()
			 */
			@Override
			public Session newSession(Request request, Response response) {
				if (WebicalApplicationTest.this.webicalSession == null) {
					WebicalApplicationTest.this.webicalSession = new WebicalSession(request, TestUtils.USERID_JAG);
					WebicalApplicationTest.this.webicalSession.setUserManager(new MockUserManager());
					WebicalApplicationTest.this.webicalSession.setSettingsManager(new MockSettingsManager());
					WebicalApplicationTest.this.webicalSession.setAccessible(true);
				}
				return WebicalApplicationTest.this.webicalSession;
			}
		});

		// Locale for test is en_uk
		getTestSession().setLocale(Locale.UK);
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG, getTestSession().getLocale());
		dateFormatFull = DateFormat.getDateInstance(DateFormat.FULL, getTestSession().getLocale());

		//Couple an Injector to inject the spring beans
		wicketTester.getApplication().addComponentInstantiationListener(new ComponentInjector());

		//Initialize the mock context
		annotApplicationContextMock = new AnnotApplicationContextMock();

		//Initialize the ApplicationSettings
		TestUtils.initializeApplicationSettingsFactory(new ApplicationSettings());

		//Register some dummy beans
		annotApplicationContextMock.putBean("settingsManager", getTestSession().getSettingsManager());
		annotApplicationContextMock.putBean("userManager", getTestSession().getUserManager());

		//Set the dummy page by default
		dummyHomePage = new DummyHomePage();

		//The context working directory
		contextWorkDir = new File(TestUtils.WORKINGDIRECTORY); 
		wicketTester.getApplication().getServletContext().setAttribute("javax.servlet.context.tempdir", contextWorkDir);
		contextWorkDir = (File) wicketTester.getApplication().getServletContext().getAttribute("javax.servlet.context.tempdir");
	}

	/**
	 * @throws Exception
	 */
	@Accessibility(accessible=false)
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		webicalSession = null;
	}

	/**
	 * @return the mock context used to inject beans
	 */
	public AnnotApplicationContextMock getAnnotApplicationContextMock() {
		return annotApplicationContextMock;
	}
	/**
	 * @param annotApplicationContextMock set the mock context
	 */
	@Accessibility(accessible=true)
	public void setAnnotApplicationContextMock(AnnotApplicationContextMock annotApplicationContextMock) {
		this.annotApplicationContextMock = annotApplicationContextMock;
	}

	/**
	 * @return the dummy home page
	 */
	public DummyHomePage getDummyHomePage() {
		return dummyHomePage;
	}
	/**
	 * @param dummyHomePage the dummy home page
	 */
	@Accessibility(accessible=true)
	public void setDummyHomePage(DummyHomePage dummyHomePage) {
		this.dummyHomePage = dummyHomePage;
	}

	/**
	 * @return the wicket tester
	 */
	public WicketTester getWicketTester() {
		return wicketTester;
	}
	/**
	 * @param wicketTester the wicket tester
	 */
	@Accessibility(accessible=true)
	public void setWicketTester(WicketTester wicketTester) {
		this.wicketTester = wicketTester;
	}

	/**
	 * @return The webical test session
	 */
	public WebicalSession getTestSession() {
		return webicalSession;
	}
}
