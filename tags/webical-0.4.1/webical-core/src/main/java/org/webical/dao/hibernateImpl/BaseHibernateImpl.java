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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.webical.dao.DaoException;

/**
 * Base class for Hibernate daos
 * Full of handy methods
 * @author ivo
 *
 */
public class BaseHibernateImpl {

	/**
	 * @return the current {@link Session} for this {@link Thread}
	 * @throws DaoException
	 */
	protected Session getSession() throws DaoException {
		return SessionFactoryUtils.getSession();
	}
	
	/**
	 * Get a persistend object by id
	 * @param clazz the {@link Class} of the Object to retrieve
	 * @param id the id
	 * @return the Object or null
	 * @throws DaoException
	 */
	protected Object get(Class clazz, Serializable id) throws DaoException {
		return getSession().get(clazz, id);
	}
	
	/**
	 * Convenience method to load all instances of a given class (use with care)
	 * @param entityClass teh {@link Class} to retrieve
	 * @return
	 * @throws DaoException
	 */
	protected List loadAll(Class entityClass) throws DaoException {
		Criteria criteria = getSession().createCriteria(entityClass);
		return criteria.list();
	}
	
	/**
	 * Convenience method to store a list of objects
	 * @param entities
	 * @throws DaoException
	 */
	protected void saveOrUpdateAll(Collection entities) throws DaoException {
		for (Iterator it = entities.iterator(); it.hasNext();) {
			getSession().saveOrUpdate(it.next());
		}
	}
	
	/**
	 * Convenience method to delete a list of objects
	 * @param entities
	 * @throws DaoException
	 */
	protected void deleteAll(Collection entities) throws DaoException {
		for (Iterator it = entities.iterator(); it.hasNext();) {
			getSession().delete(it.next());
		}
	}
	
	/**
	 * Convenience method to store an object
	 * @param object
	 * @throws DaoException
	 */
	protected void saveOrUpdate(Object object) throws DaoException {
		getSession().saveOrUpdate(object);
	}
	
	protected void saveOrUpdate(String entityName, Object object) throws DaoException {
		getSession().saveOrUpdate(entityName, object);
	}
	
	/**
	 * Convenience method to store an object
	 * @param object
	 * @throws DaoException
	 */
	protected void delete(Object object) throws DaoException {
		getSession().delete(object);
	}
}
