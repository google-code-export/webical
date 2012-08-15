/*
 *    Webical - http://code.google.com/p/webical/
 *    Copyright (C) 2012 by Cebuned
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

package org.webical.aspect.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.webical.dao.annotation.*;
import org.webical.dao.DaoException;
import org.webical.dao.hibernateImpl.SessionFactoryUtils;

/**
 * This aspect takes care of the {@link Session} management around 
 * methods that are marked with {@link Transaction}
 * 
 * TODO: Differentiate between different kinds of transactions @see Transaction 
 * 
 * @author ivo
 */
public aspect HibernateSessionAspect {

	private static Log log = LogFactory.getLog(HibernateSessionAspect.class);

	public pointcut transactionalInvocation(Transaction transaction)
				: execution(@Transaction * * (..)) && @annotation(transaction);

    /**
     * Matches all methods marked with {@link Transaction} except for 
     * methods in the flow of another method marked with {@link Transaction}
     * @throws DaoException on {@link Session} closure errors
     */
    Object around(Transaction transaction) throws DaoException
    			:transactionalInvocation(transaction) && !cflowbelow(transactionalInvocation(Transaction))
    {
    	SessionFactoryUtils.createSession();
    	org.hibernate.Transaction hbtransact = null;
        try {
        	Session sessie = SessionFactoryUtils.getSession();
        	if (sessie.getTransaction().isActive()) {
        		log.error("around: Active Transaction?");
        	} else {
        		hbtransact = sessie.beginTransaction();
        	}
            return proceed(transaction);
		} catch (DaoException dax) {
			log.error(dax, dax);
            if (hbtransact != null) hbtransact.rollback();
            hbtransact = null;
            throw dax;
        } finally {
            if (hbtransact != null) {
            	log.info("Commit Transaction: readOnly " + transaction.readOnly());
            	SessionFactoryUtils.flushSession();
            	hbtransact.commit();
            }
            hbtransact = null;
            SessionFactoryUtils.closeSession();
        }
    }
}
