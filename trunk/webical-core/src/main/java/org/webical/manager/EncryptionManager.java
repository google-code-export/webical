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

package org.webical.manager;

/**
 * {@link EncryptionManager} is responsible for setting a {@link Encryptor} on all {@link EncryptingDao}'s and making user the 
 * encryption is updated when needed
 * @author ivo
 *
 */
public interface EncryptionManager {

	/**
	 * Method to check if the initialization is already done
	 * @return
	 */
	public boolean isInitialized();
	
	/**
	 * Method to check if a passphrase is already configured
	 * @return true if so
	 */
	public boolean isConfigured() throws WebicalException;
	
	/**
	 * Call to update the encryption passphrase
	 * @param passphrase the new passphrase to use in encyption
	 * @throws WebicalException on error
	 */
	public void updateEncryptionPassphrase(String passphrase) throws WebicalException;
	
}
