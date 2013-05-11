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

package org.webical.dao.hibernateImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.dao.DaoException;

public class SessionFactoryUtils implements InitializingBean {
	private static Log log = LogFactory.getLog(SessionFactoryUtils.class);

	private static ThreadLocal<Session> threadLocalSession = new ThreadLocal<Session>();
	private static SessionFactory sessionFactory;

	public static Session getSession() throws DaoException {
		if(threadLocalSession.get() == null || !threadLocalSession.get().isOpen()) {
			throw new DaoException("Session does not exist or was already closed");
		}
		return threadLocalSession.get();
	}

	public static void flushSession() throws DaoException {
		if (threadLocalSession.get().isOpen()) threadLocalSession.get().flush();
	}

	public static void closeSession() throws DaoException {
		log.debug("Closing Session");
		if(threadLocalSession.get() == null || !threadLocalSession.get().isOpen()) {
			throw new DaoException("Session does not exist or was already closed");
		}
		flushSession();
		threadLocalSession.get().close();
	}

	public static void createSession() {
		log.debug("Setting up a new Session");
		Session session = sessionFactory.openSession();
		threadLocalSession.set(session);
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		SessionFactoryUtils.sessionFactory = sessionFactory;
	}

	public void afterPropertiesSet() throws Exception {
		if(sessionFactory == null) {
			log.error(SessionFactoryUtils.class + " needs a " + SessionFactory.class);
			throw new ExceptionInInitializerError(SessionFactoryUtils.class + " needs a " + SessionFactory.class);
		}
	}
}
