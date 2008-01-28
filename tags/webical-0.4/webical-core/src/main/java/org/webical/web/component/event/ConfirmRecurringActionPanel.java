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

package org.webical.web.component.event;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.webical.web.component.AbstractBasePanel;

public abstract class ConfirmRecurringActionPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	private final String MESSAGE_LABEL_MARKUP_ID = "messageLabel";
	private final String CONFIRM_THIS_LINK_MARKUP_ID = "confirmThisLink";
	private final String CONFIRM_ALL_LINK_MARKUP_ID = "confirmAllLink";
	private final String CONFIRM_FOLLOWING_LINK_MARKUP_ID = "confirmFollowingLink";
	private final String CANCEL_LINK_MARKUP_ID = "cancelLink";
	
	private Model message;
	private Label messageLabel;
	private Link confirmThisLink, confirmAllLink, confirmFollowingLink, cancelLink;
	
	public ConfirmRecurringActionPanel(String markupId, String message) {
		super(markupId, ConfirmRecurringActionPanel.class);
		this.message = new Model(message);
	}
	
	public void setupCommonComponents() {
		messageLabel = new Label(MESSAGE_LABEL_MARKUP_ID, message);
		addOrReplace(messageLabel);
	}
	
	public void setupAccessibleComponents() {
		confirmThisLink = new Link(CONFIRM_THIS_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ConfirmRecurringActionPanel.this.onConfirmThis();
			}
		};
		addOrReplace(confirmThisLink);
		
		confirmAllLink = new Link(CONFIRM_ALL_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ConfirmRecurringActionPanel.this.onConfirmAll();
			}
		};
		addOrReplace(confirmAllLink);
		
		confirmFollowingLink = new Link(CONFIRM_FOLLOWING_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ConfirmRecurringActionPanel.this.onConfirmFollowing();
			}
		};
		addOrReplace(confirmFollowingLink);
		
		cancelLink = new Link(CANCEL_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ConfirmRecurringActionPanel.this.onCancel();
			}
		};
		addOrReplace(cancelLink);
		
	}

	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}
	
	public abstract void onConfirmAll();
	public abstract void onConfirmThis();
	public abstract void onConfirmFollowing();
	public abstract void onCancel();

}
