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

package org.webical.web.pages;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.webical.dao.ConnectionDaoException;
import org.webical.dao.DaoException;
import org.webical.dao.IcalParserDaoException;
import org.webical.dao.InvalidURIDaoException;
import org.webical.dao.SSLDaoException;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Tries to display a userfriendly message
 * @author ivo
 *
 */
public class ErrorPage extends AbstractBasePage {

	
	private static final String SETTINGS_LINK_MARKUP_ID = "settingsLink";
	private static final String HOME_LINK_MARKUP_ID = "homeLink";
	
	private static final String CHECK_SSL_SETTINGS_LABEL = "check_ssl_settings";
	private static final String CHECK_URLS_LABEL = "check_urls";
	private static final String CHECK_REMOTE_CALENDAR_LABEL = "check_remote_calendar";
	private static final String CHECK_CONNECTION_SETTINGS_LABEL = "check_connection_settings";
	private static final String SHOW_DETAILS_LINK_MARKUP_ID = "showDetailsLink";
	private static final String ERROR_PANEL_MARKUP_ID = "errorPanel";
	private static final String ERROR_CAUSE_LIST_MARKUP_ID = "errorCauseList";
	private static final String DETAILED_ERROR_PANEL_ID = "detailedErrorPanel";
	private static final String PROBLEM_OCCURRED_LABEL = "problem_occurred_label";
	private static final long serialVersionUID = 1L;
	
	private Throwable throwable;
	
	public ErrorPage() { 
		super(ErrorPage.class);
	}
	
	/**
	 * @param previousPage the previousPage to return to
	 * @param throwable the Exception thrown
	 */
	public ErrorPage(final Page previousPage, Throwable throwable) {
		super(ErrorPage.class);
		this.throwable = throwable;
	}
	
	public void setupCommonComponents() {
		//Common error message
		String message = new StringResourceModel(PROBLEM_OCCURRED_LABEL, this, null).getString();
		
		//Common cause list
		if(throwable != null) {
			if(throwable.getCause() != null && throwable.getCause().getMessage() != null) {
				message = throwable.getCause().getMessage();
			} else if (throwable.getMessage() != null) {
				message = throwable.getMessage();
			}
		}
		
		//Add components
		addOrReplace(new CauseList(ERROR_CAUSE_LIST_MARKUP_ID, getCauses(throwable)));
		addOrReplace(new Label(ERROR_PANEL_MARKUP_ID, message));
		
		addOrReplace(new PageLink(HOME_LINK_MARKUP_ID, BasePage.class));
		
		BookmarkablePageLink settingsLink = new BookmarkablePageLink(SETTINGS_LINK_MARKUP_ID, BasePage.class);
		settingsLink.setParameter(BasePage.CONTENT_PANEL_PARAMETER, BasePage.SETTINGS_PANEL_PARAMETER_VALUE);
		addOrReplace(settingsLink);
	}
	
	public void setupAccessibleComponents() {
		//Detailed error message
		final String detailedMessage = getDetailedMessage();
		final DetailPanel detailPanel = new DetailPanel(DETAILED_ERROR_PANEL_ID, new Model(detailedMessage!=null?detailedMessage:""));
		detailPanel.setVisible(false);
		
		//Link to show/hide the details
		Link showDetailsLink = new Link(SHOW_DETAILS_LINK_MARKUP_ID) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				if(detailPanel.isVisible()) {
					detailPanel.setVisible(false);
				} else {
					detailPanel.setVisible(true);
				}
			}
			
		};
		
		if(detailedMessage == null) {
			showDetailsLink.setEnabled(false);
		}
		
		//Add components
		addOrReplace(detailPanel);
		addOrReplace(showDetailsLink);
	}



	public void setupNonAccessibleComponents() {
		//ModelDialog for the detailmessage
		final ModalWindow modalWindow = new ModalWindow(DETAILED_ERROR_PANEL_ID);
		
		//The detailed message
		final String detailedMessage = getDetailedMessage();
		
		//Link to show the details
		IndicatingAjaxLink showDetailsLink = new IndicatingAjaxLink(SHOW_DETAILS_LINK_MARKUP_ID) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget ajaxRequestTarget) {
				
				modalWindow.setContent(new DetailPanel(modalWindow.getContentId(), new Model(detailedMessage)));
				modalWindow.setVisible(true);
				modalWindow.show(ajaxRequestTarget);
			}
			
		};
		
		if(detailedMessage == null) {
			showDetailsLink.setEnabled(false);
		} else {
			modalWindow.setInitialWidth(700);
			modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {

				private static final long serialVersionUID = 1L;

				public void onClose(AjaxRequestTarget ajaxRequestTarget) {
					modalWindow.setContent(new Label(modalWindow.getContentId(), new Model("")));
					modalWindow.setVisible(false);
					ajaxRequestTarget.addComponent(modalWindow);
					getSession().createAutoPageMap();
				}
				
			});
		}
		
		//Add components
		addOrReplace(modalWindow);
		addOrReplace(showDetailsLink);
	}
	
	private class DetailPanel extends Panel {
		private static final String DETAILED_LABEL_MARKUP_ID = "detailedLabel";
		private static final long serialVersionUID = 1L;

		public DetailPanel(String markupId, Model model) {
			super(markupId, model);
			add(new Label(DETAILED_LABEL_MARKUP_ID, model));
		}
		
	}
	
	/**
	 * ListView for the cause messages
	 * @author ivo
	 *
	 */
	private class CauseList extends ListView {
		private static final String CAUSE_MARKUP_ID = "cause";
		private static final long serialVersionUID = 1L;

		public CauseList(String id, List list) {
			super(id, list);
		}

		/**
		 * Adds a Label with the cause message
		 * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
		 */
		@Override
		protected void populateItem(ListItem listItem) {
			listItem.add(new Label(CAUSE_MARKUP_ID, listItem.getModelObjectAsString()));
			
		}
		
	}
	
	private String getDetailedMessage() {
		if(throwable != null) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			throwable.printStackTrace(new PrintStream(byteArrayOutputStream));
			return byteArrayOutputStream.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a list of cause messages
	 * @param throwable the top throwable
	 * @return a list of messages
	 */
	private List<String> getCauses(Throwable throwable) {
		List<String> causes = new ArrayList<String>();
		
		if(throwable != null && throwable.getCause() != null) {
			Throwable cause = throwable.getCause();
			while(cause.getCause() != null) {
				cause = cause.getCause();
				String message = cause.getMessage();
				if(cause instanceof DaoException) {
					message += " " + getAdditionalHelpMessage((DaoException) cause);
				}
				causes.add(message);
			}
		}
		
		return causes;
	}
	
	/**
	 * Adds additional help text to know exceptions
	 * @param daoException
	 * @return a help text
	 */
	private String getAdditionalHelpMessage(DaoException daoException) {
		if(daoException instanceof ConnectionDaoException) {
			return new StringResourceModel(CHECK_CONNECTION_SETTINGS_LABEL, this, null).getString();
		} else if(daoException instanceof IcalParserDaoException) {
			return new StringResourceModel(CHECK_REMOTE_CALENDAR_LABEL, this, null).getString();
		} else if(daoException instanceof InvalidURIDaoException) {
			return new StringResourceModel(CHECK_URLS_LABEL, this, null).getString();
		} else if(daoException instanceof SSLDaoException) {
			return new StringResourceModel(CHECK_SSL_SETTINGS_LABEL, this, null).getString();
		} else {
			return "";
		}
	}

}
