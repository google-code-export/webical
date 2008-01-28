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

package org.webical.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;

/**
 * 
 * Creates a writeable Session per request.
 * @author ivo
 *
 */
public class WriteableOpenSessionInViewFilter extends OpenSessionInViewFilter {
	private static Log log = LogFactory.getLog(WriteableOpenSessionInViewFilter.class);
	

	/**
	 * Default construtor sets singlesession to false
	 */
	public WriteableOpenSessionInViewFilter() {
		log.info("Using multiple sessions per view");
		setSingleSession(false);
	}

	/* (non-Javadoc)
	 * @see org.springframework.orm.hibernate3.support.OpenSessionInViewFilter#closeSession(org.hibernate.Session, org.hibernate.SessionFactory)
	 */
	@Override
	protected void closeSession(Session session, SessionFactory sessionFactory) {
		log.debug("Closing the session");
		/* Flush the session before closing */
		session.flush();
		/* Close the given Session, created via the given factory, if it is not managed externally (i.e. not bound to the thread). */
		SessionFactoryUtils.releaseSession(session, sessionFactory);
	}

	/* (non-Javadoc)
	 * @see org.springframework.orm.hibernate3.support.OpenSessionInViewFilter#getSession(org.hibernate.SessionFactory)
	 */
	@Override
	protected Session getSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
		log.debug("returning session");
		/* Checks for a Session currently bound to the thread and creates one if not found */
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		/* Makes the Session writable */ 
		session.setFlushMode(FlushMode.AUTO);
		
		return session;
	}

	

}
