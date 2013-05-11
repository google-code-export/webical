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

package org.webical.test.web.component;

import java.util.ArrayList;
import java.util.List;

import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.event.Extension;
import org.webical.web.event.ExtensionEvent;
import org.webical.web.event.ExtensionListener;
import org.webical.web.event.ExtensionListenerRegistrations;
import org.webical.web.event.ExtensionPoint;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.ITestPageSource;

/**
 *
 * Tests the AbstractPanel for the accessibility switching and the ExtensionSystem
 * @author ivo
 *
 */
public class AbstractBasePanelTest extends WebicalApplicationTest {

	private boolean setupCommonComponentsCalled = false;
	private boolean setupAccessibleComponentsCalled = false;
	private boolean setupNonAccessibleComponentsCalled = false;

	private boolean addExtensionsAfterComponentSetupCalled = false;
	private boolean addExtensionsBeforeComponentSetupCalled = false;


	/* (non-Javadoc)
	 * @see org.webical.test.web.WebicalApplicationTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setupCommonComponentsCalled = false;
		setupAccessibleComponentsCalled = false;
		setupNonAccessibleComponentsCalled = false;

		addExtensionsAfterComponentSetupCalled = false;
		addExtensionsBeforeComponentSetupCalled = false;
	}

	/**
	 * Tests the accessibility switch for accessibility
	 */
	public void testCapabilitySwitchAccessible() {

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				webicalSession.setAccessible(true);
				return new PanelTestPage(new TestPanel(PanelTestPage.PANEL_MARKUP_ID));
			}
		});

		assertTrue(setupCommonComponentsCalled);
		assertTrue(setupAccessibleComponentsCalled);
		assertFalse(setupNonAccessibleComponentsCalled);
	}

	/**
	 * Tests the accessibility switch for non-accessibility
	 */
	public void testCapabilitySwitchNonAccessible() {
		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				webicalSession.setAccessible(false);
				return new PanelTestPage(new TestPanel(PanelTestPage.PANEL_MARKUP_ID));
			}
		});

		assertTrue(setupCommonComponentsCalled);
		assertTrue(setupNonAccessibleComponentsCalled);
		assertFalse(setupAccessibleComponentsCalled);
	}

	/**
	 * Tests if all the ExtensionListener methods are called
	 */
	public void testExtensionSystemMethodsCalled() {
		TestExtensionListener testExtensionListener = new TestExtensionListener();

		//Add extensionListener to the ExtensionListenerRegistrations
		ExtensionListenerRegistrations.addExtensionListenerRegistration(TestPanel.class, testExtensionListener);

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new TestPanel(PanelTestPage.PANEL_MARKUP_ID));
			}
		});

		assertTrue(addExtensionsAfterComponentSetupCalled);
		assertTrue(addExtensionsBeforeComponentSetupCalled);
	}

	/**
	 * Tests adding a tab to an ExtensionHandler
	 */
	public void testExtensionSystemAddTab() {
		//Add tab
		ArrayList<AbstractTab> addedTabs = new ArrayList<AbstractTab>();
		addedTabs.add(new AbstractTab(new Model("TESTTAB")) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getPanel(String arg0) {
				return null;
			}
		});

		TestExtensionListener testExtensionListener = new TestExtensionListener(addedTabs, null, null);

		//Add extensionListener to the ExtensionListenerRegistrations
		ExtensionListenerRegistrations.addExtensionListenerRegistration(TestPanel.class, testExtensionListener);

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new TestPanel(PanelTestPage.PANEL_MARKUP_ID));
			}
		});

		wicketTester.assertContains("TESTTAB");		
	}

	/**
	 * Tests adding a Component to an ExtensionHandler
	 */
	public void testExtensionSystemAddComponent() {
		//Added component
		ArrayList<String> addedLabels = new ArrayList<String>();
		addedLabels.add("TESTADDEDCOMPONENT");

		TestExtensionListener testExtensionListener = new TestExtensionListener(null, addedLabels, null);

		//Add extensionListener to the ExtensionListenerRegistrations
		ExtensionListenerRegistrations.addExtensionListenerRegistration(TestPanel.class, testExtensionListener);

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new TestPanel(PanelTestPage.PANEL_MARKUP_ID));
			}
		});

		wicketTester.assertContains("TESTADDEDCOMPONENT");
	}

	/**
	 * Tests adding a tab to an ExtensionHandler
	 */
	public void testExtensionSystemReplaceComponent() {
		//Replace components
		List<Component> replacedComponents = new ArrayList<Component>();
		replacedComponents.add(new Label("dummyLabel", new Model("REPLACED_TEXT")));

		TestExtensionListener testExtensionListener = new TestExtensionListener(null, null, replacedComponents);

		//Add extensionListener to the ExtensionListenerRegistrations
		ExtensionListenerRegistrations.addExtensionListenerRegistration(TestPanel.class, testExtensionListener);

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new TestPanel(PanelTestPage.PANEL_MARKUP_ID));
			}
		});

		wicketTester.assertContains("REPLACED_TEXT");
	}

	/**
	 * Tests replacing a component from an ExtensionHandler
	 */
	private class TestPanel extends AbstractBasePanel {
		protected static final String TEST_TABS_MARKUP_ID = "testTabs";

		private static final long serialVersionUID = 1L;

		private List<AbstractTab> tabs = new ArrayList<AbstractTab>();

		public TestPanel(String markupId) {
			super(markupId, TestPanel.class);

			tabs.add(new AbstractTab(new Model("")) {
				private static final long serialVersionUID = 1L;

				@Override
				public Panel getPanel(String markupId) {
					return new DummyPanel(markupId);
				}
			});
		}

		public void setupAccessibleComponents() {
			setupAccessibleComponentsCalled = true;
		}

		public void setupCommonComponents() {
			setupCommonComponentsCalled = true;
			addOrReplace(new TabbedPanel(TEST_TABS_MARKUP_ID, tabs));
			addOrReplace(new Label("dummyLabel", new Model("DUMMY_TEXT")));
		}

		public void setupNonAccessibleComponents() {
			setupNonAccessibleComponentsCalled = true;
		}

		@Override
		public void addTabToTabbedPanel(AbstractTab tab) {
			tabs.add(tab);
		}
	}

	/**
	 * Just a simple dummy panel
	 * @author ivo
	 *
	 */
	private class DummyPanel extends Panel {
		private static final long serialVersionUID = 1L;

		public DummyPanel(String markupId) {
			super(markupId);
		}
	}

	/**
	 * A Test implementation of ExtensionListener
	 * @author ivo
	 *
	 */
	private class TestExtensionListener implements ExtensionListener {
		private List<AbstractTab> addedTabs;
		private List<String> addedLabels;
		private List<Component> replacedComponents;

		public TestExtensionListener() {
		}

		public TestExtensionListener(List<AbstractTab> addedTabs, List<String> addedLabels, List<Component> replacedComponents) {
			this.addedTabs = addedTabs;
			this.addedLabels = addedLabels;
			this.replacedComponents = replacedComponents;
		}

		/**
		 * Adds and replaces components if available
		 * @see org.webical.test.web.event.ExtensionListener#addExtensionsAfterComponentSetup(org.webical.test.web.event.ExtensionEvent)
		 */
		public void addExtensionsAfterComponentSetup(ExtensionEvent extensionEvent) {
			addExtensionsAfterComponentSetupCalled = true;

			if (addedLabels != null && addedLabels.size() > 0) {
				ExtensionPoint extensionPoint = extensionEvent.getExtensionPoints().get(TestPanel.EXTENSIONS_MARKUP_ID);

				for (String label : addedLabels) {
					extensionEvent.getSource().addExtension(
							new Extension (new Label(extensionPoint.getNewChildId(), label), extensionPoint));
				}
			}

			if (replacedComponents != null && replacedComponents.size() > 0) {
				for (Component component : replacedComponents) {
					extensionEvent.getSource().replaceExistingComponentWithExtension(component);
				}
			}
		}

		/**
		 * Adds tabs if available
		 * @see org.webical.test.web.event.ExtensionListener#addExtensionsBeforeComponentSetup(org.webical.test.web.event.ExtensionEvent)
		 */
		public void addExtensionsBeforeComponentSetup(ExtensionEvent extensionEvent) {
			addExtensionsBeforeComponentSetupCalled = true;
			if (addedTabs != null && addedTabs.size() > 0) {
				for (AbstractTab abstractTab : addedTabs) {
					extensionEvent.getSource().addTabToTabbedPanel(abstractTab);
				}
			}
		}
	}
}
