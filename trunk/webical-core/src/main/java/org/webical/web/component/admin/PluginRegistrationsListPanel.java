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

package org.webical.web.component.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.webical.plugin.registration.PluginRegistration;
import org.webical.plugin.registration.PluginRegistrationStore;
import org.webical.plugin.registration.PluginRegistration.PluginState;
import org.webical.web.component.AbstractBasePanel;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Fragment;

public class PluginRegistrationsListPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;
	private static final int NUMBER_OFF_ITEMS_PER_ROW = 5;

	public PluginRegistrationsListPanel(String id) {
		super(id, PluginRegistrationsListPanel.class);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		List<PluginRegistration> pluginRegistrations = ((PluginRegistrationStore)getApplication()).getPluginRegistrations();

		//TODO extract comparator for reuse
		Collections.sort(pluginRegistrations, new Comparator<PluginRegistration>() {

			public int compare(PluginRegistration registration1, PluginRegistration registration2) {
				return registration1.getPlugin().getPluginName().compareToIgnoreCase(registration2.getPlugin().getPluginName());
			}
		});

		Fragment listFragment = null;

		if (pluginRegistrations == null || pluginRegistrations.size() == 0) {
			listFragment = new Fragment("listFragment", "noRegistrationsFragment");
		} else {
			listFragment = new Fragment("listFragment", "registrationsFragment");

			PluginRegistrationsList pluginRegistrationsList = new PluginRegistrationsList("pluginRegistrationList", pluginRegistrations, NUMBER_OFF_ITEMS_PER_ROW);

			listFragment.add(pluginRegistrationsList);
			listFragment.add(new AjaxPagingNavigator("pager", pluginRegistrationsList));
		}

		add(listFragment);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		//NOT IMPLEMENTED
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		//NOT IMPLEMENTED
	}

	private class PluginRegistrationsList extends PageableListView {
		private static final long serialVersionUID = 1L;

		/**
		 * @param id
		 * @param pluginRegistrations
		 * @param rowsPerPage
		 */
		public PluginRegistrationsList(String id, List<PluginRegistration> pluginRegistrations, int rowsPerPage) {
			super(id, pluginRegistrations, rowsPerPage);
		}

		/* (non-Javadoc)
		 * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
		 */
		@Override
		protected void populateItem(ListItem item) {
			PluginRegistration pluginRegistration = (PluginRegistration) item.getModelObject();
			Fragment fragment = null;

			if (pluginRegistration.getPluginState().equals(PluginState.REGISTERED)) {
				//Add items for success
				fragment = new Fragment("pluginRegistrationFragment", "pluginRegistrationFragment_succesfull");
				fragment.add(new Label("pluginDescription", "" + pluginRegistration.getPlugin().getPluginDescription()));
				fragment.add(new Label("pluginVersion", "" + pluginRegistration.getPlugin().getPluginVersion()));
			} else {
				//Add items for failure
				fragment = new Fragment("pluginRegistrationFragment", "pluginRegistrationFragment_unsuccesfull");
				fragment.add(new Label("message", "" + pluginRegistration.getMessage()));
			}

			//General items
			fragment.add(new Label("pluginName", "" + pluginRegistration.getPlugin().getPluginName()));
			fragment.add(new Label("pluginState", "" + pluginRegistration.getPluginState().toString()));

			item.add(fragment);
		}
	}
}
