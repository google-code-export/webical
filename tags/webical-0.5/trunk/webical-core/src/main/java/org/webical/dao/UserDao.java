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

package org.webical.dao;

import org.webical.User;

/**
 * @author ivo
 *
 */
public interface UserDao {
	
	/**
	 * Stores a User
	 * @param user the User to store
	 * @throws DaoException
	 */
	public void storeUser(User user) throws DaoException;
	
	/**
	 * Removes a User
	 * @param user the User to remove
	 * @throws DaoException
	 */
	public void removeUser(User user) throws DaoException;
	
	/**
	 * Retrieve a user with its userId
	 * @param userId the Id of the user to lookup
	 * @return a User or null
	 * @throws DaoException
	 */
	public User getUser(String userId) throws DaoException;

}
