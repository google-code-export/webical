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

package org.webical.manager.impl.mock;

import java.util.HashMap;

import org.webical.User;
import org.webical.manager.UserManager;
import org.webical.manager.WebicalException;

/**
 * Mock implementation for unit tests
 * Simple implementation with maps
 * @author ivo
 *
 */
public class MockUserManager implements UserManager {
	
	private HashMap<String, User> userMap = new HashMap<String, User>();

	/* (non-Javadoc)
	 * @see org.webical.manager.UserManager#getUser(java.lang.String)
	 */
	public User getUser(String userId) throws WebicalException {
		return userMap.get(userId);
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.UserManager#removeUser(org.webical.User)
	 */
	public void removeUser(User user) throws WebicalException {
		userMap.remove(user.getUserId());
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.UserManager#storeUser(org.webical.User)
	 */
	public void storeUser(User user) throws WebicalException {
		userMap.put(user.getUserId(), user);
	}

}
