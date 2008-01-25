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

package org.webical.dao.hibernateImpl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.dao.DaoException;
import org.webical.dao.annotation.Transaction;

/**
 * 
 * Dao to store a preset configuration via Spring
 * @author ivo
 *
 */
public class DatabaseBootstrapDao extends BaseHibernateImpl implements InitializingBean {
	private static Log log = LogFactory.getLog(DatabaseBootstrapDao.class);
	
	/** Set by Spring */
	private List objectsToPersist;
	
	/**
	 * Stores the list of objects to persist
	 * @throws DaoException on Hibernate Exception
	 */
	@Transaction
	private void storeObjectsToPersist() throws DaoException {
		try {
			saveOrUpdateAll(objectsToPersist);
		} catch (DaoException e) {
			log.error("Error persisting preset Object", e);
			throw new DaoException("Error persisting preset Object", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if(objectsToPersist != null && objectsToPersist.size() > 0) {
			log.info("Persisting preset configuration");
			storeObjectsToPersist();
		} else {
			log.warn(DatabaseBootstrapDao.class + " referenced but no configuration to persist...");
		}
	}
	
	/**
	 * Set by Spring
	 * @param objectsToPersist a List of persistable objects
	 */
	public void setObjectsToPersist(List objectsToPersist) {
		this.objectsToPersist = objectsToPersist;
	}
}
