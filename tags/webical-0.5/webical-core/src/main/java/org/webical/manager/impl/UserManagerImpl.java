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

package org.webical.manager.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.User;
import org.webical.dao.DaoException;
import org.webical.dao.UserDao;
import org.webical.manager.UserManager;
import org.webical.manager.WebicalException;

public class UserManagerImpl implements UserManager,InitializingBean {

	
	private static final String USER_MANAGER_IMPL_NEEDS_USER_DAO = "UserManagerImpl needs UserDao";

	private static Log log = LogFactory.getLog(UserManagerImpl.class);
		
	private UserDao userDao;
	
	
	/* (non-Javadoc)
	 * @see org.webical.manager.UserManager#getUser(java.lang.String)
	 */
	public User getUser(String userId) throws WebicalException {
		try {
			return userDao.getUser(userId);
		} catch (DaoException e) {
			throw new WebicalException("Could not retrieve User",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.UserManager#removeUser(org.webical.User)
	 */
	public void removeUser(User user) throws WebicalException {
		try {
			userDao.removeUser(user);
		} catch (DaoException e) {
			throw new WebicalException("Could not remove User",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.UserManager#storeUser(org.webical.User)
	 */
	public void storeUser(User user) throws WebicalException {
		try {
			userDao.storeUser(user);
		} catch (DaoException e) {
			throw new WebicalException("Could not store an User",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {			
		if(userDao == null) {
			throw new ExceptionInInitializerError(USER_MANAGER_IMPL_NEEDS_USER_DAO);
		}
		
		if(log.isDebugEnabled()) {
			log.debug("Class of UserDao set by Spring: " + userDao.getClass());
		}
	}

	/**
	 * @return userDao
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
