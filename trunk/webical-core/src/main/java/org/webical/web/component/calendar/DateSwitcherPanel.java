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

package org.webical.web.component.calendar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.calendar.model.DateSwitcherModel;

/**
 * Panel to switch the date range
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 *
 */
public abstract class DateSwitcherPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	public static final int DAY_VIEW = 0;
	public static final int WEEK_VIEW = 1;
	public static final int MONTH_VIEW = 2;
	public static final int ANOTHER_VIEW = 3;

	private static final String RANGE_LABEL_MARKUP_ID = "rangeLabel";
	private static final String TODAY_LINK_MARKUP_ID = "todayLink";
	private static final String NEXT_LINK_MARKUP_ID = "nextLink";
	private static final String PREVIOUS_LINK_MARKUP_ID = "previousLink";

	// DateSwitcher components
	private Link nextLink, previousLink, todayLink;
	private Label rangeLabel;

	/**
	 * Constructor
	 * @param markupId The ID used in the Markup
	 * @param model The {@see DateSwitcherModel} to use
	 */
	public DateSwitcherPanel(String markupId, DateSwitcherModel model) {
		super(markupId, DateSwitcherPanel.class);
		setModel(model);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		rangeLabel = new Label(RANGE_LABEL_MARKUP_ID, getModel());
		addOrReplace(rangeLabel);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {

		todayLink = new Link(TODAY_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				DateSwitcherPanel.this.todaySelected(null);
			}

		};

		nextLink = new Link(NEXT_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				DateSwitcherPanel.this.nextPeriod(null);
			}

		};

		previousLink = new Link(PREVIOUS_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				DateSwitcherPanel.this.previousPeriod(null);
			}

		};

		addOrReplace(todayLink);
		addOrReplace(nextLink);
		addOrReplace(previousLink);

	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}


	/**
	 * Update the model of the label to reflect any date changes
	 * @see org.webical.web.component.AbstractBasePanel#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		// Reset the model of the label to reflect any changes
		if(rangeLabel != null) {
			rangeLabel.setModel(getModel());
			addOrReplace(rangeLabel);
		}
		super.onBeforeRender();
	}

	/**
	 * Select the next period in the range of the calendar panel
	 * @param target The Ajax request target
	 */
	public abstract void nextPeriod(AjaxRequestTarget target);

	/**
	 * Select the previous period in the range of the calendar panel
	 * @param target The Ajax request target
	 */
	public abstract void previousPeriod(AjaxRequestTarget target);

	/**
	 * Select the current day
	 * @param target The Ajax request target
	 */
	public abstract void todaySelected(AjaxRequestTarget target);

}

