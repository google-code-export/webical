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

package org.webical.web.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public abstract class ConfirmActionPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	private final String MESSAGE_LABEL_MARKUP_ID = "messageLabel";
	private final String CONFIRM_LINK_MARKUP_ID = "confirmLink";
	private final String CANCEL_LINK_MARKUP_ID = "cancelLink";
	
	private IModel message;
	private Label messageLabel;
	private Link confirmLink, cancelLink;
	
	public ConfirmActionPanel(String markupId, String message) {
		super(markupId, ConfirmActionPanel.class);
		this.message = new Model(message);
	}
	
	public ConfirmActionPanel(String markupId, StringResourceModel model) {
		this(markupId, model.getString());
	}

	public void setupCommonComponents() {
		messageLabel = new Label(MESSAGE_LABEL_MARKUP_ID, message);
		addOrReplace(messageLabel);
	}
	
	public void setupAccessibleComponents() {
		confirmLink = new Link(CONFIRM_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ConfirmActionPanel.this.onConfirm();
			}
		};
		addOrReplace(confirmLink);
		
		cancelLink = new Link(CANCEL_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ConfirmActionPanel.this.onCancel();
			}
			
		};
		addOrReplace(cancelLink);
	}

	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}

	public abstract void onConfirm();
	
	public abstract void onCancel();
	
}
