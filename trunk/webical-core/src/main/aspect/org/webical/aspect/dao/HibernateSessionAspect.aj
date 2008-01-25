package org.webical.aspect.dao;

import org.webical.dao.DaoException;
import org.webical.dao.annotation.*;
import org.webical.dao.hibernateImpl.SessionFactoryUtils;
import org.hibernate.Session;

/**
 * This aspect takes care of the {@link Session} management around 
 * methods that are marked with {@link Transaction}
 * 
 * TODO: Differentiate between different kinds of transactions @see Transaction 
 * 
 * @author ivo
 *
 */
public aspect HibernateSessionAspect {

	pointcut transactionalInvocation() : execution(@Transaction * *(..));
	
    /**
     * Matches all methods marked with {@link Transaction} except for 
     * methods in the flow of antother method maked with {@link Transaction}
     * @throws DaoException on {@link Session} closure errors
     */
    Object around() throws DaoException: transactionalInvocation() && !cflowbelow(transactionalInvocation()) {
    	SessionFactoryUtils.createSession();
        try {
            return proceed();
        } finally {
           SessionFactoryUtils.closeSession();
        }
    }
}
