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

package org.webical.web;

import java.lang.reflect.Field;
import java.text.DateFormat;
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
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.app.WebicalSession;

/**
 * Abstract base class for the wicket tests
 * @author ivo
 *
 */
public abstract class WebicalApplicationTest extends TestCase {
	private static final String FIXED_USER_ID = "fixedUserId";

	protected WicketTester wicketTester;
	protected AnnotApplicationContextMock annotApplicationContextMock;
	protected DummyHomePage dummyHomePage;
	protected DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
	protected DateFormat dateFormatFull = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());

	protected WebicalSession webicalSession;

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
				if(WebicalApplicationTest.this.webicalSession == null) {
					WebicalApplicationTest.this.webicalSession = new WebicalSession(request, FIXED_USER_ID);
					WebicalApplicationTest.this.webicalSession.setUserManager(new MockUserManager());
					WebicalApplicationTest.this.webicalSession.setAccessible(true);
				}
				return WebicalApplicationTest.this.webicalSession;
			}

		});

		//Couple an Injector to inject the spring beans
		wicketTester.getApplication().addComponentInstantiationListener(new ComponentInjector());

		//Initialize the mock context
		annotApplicationContextMock  = new AnnotApplicationContextMock();

		//Set the dummypage by default
		dummyHomePage = new DummyHomePage();

	}

	/**
	 * @throws Exception
	 */
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
	public void setAnnotApplicationContextMock(
			AnnotApplicationContextMock annotApplicationContextMock) {
		this.annotApplicationContextMock = annotApplicationContextMock;
	}

	/**
	 * @return the dummy homepage
	 */
	public DummyHomePage getDummyHomePage() {
		return dummyHomePage;
	}

	/**
	 * @param dummyHomePage the dummy homepage
	 */
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
