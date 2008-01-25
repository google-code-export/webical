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

package org.webical.dao.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import org.webical.dao.encryption.impl.DesEncryptor;
import org.webical.dao.encryption.impl.DummyEncryptor;

/**
 * This factory creates encryptors used for encryption/decryption with the current passwords
 * @author ivo
 *
 */
public class EncryptorFactory {
	private static String encryptionPassword;
	private static String decryptionPassword;

	/**
	 * @return an {@link Encryptor} that can be used with the current Encryption password
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 */
	public static Encryptor getCurrentEncryptionEncryptor() throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		if(encryptionPassword == null || encryptionPassword.length() == 0) {
			return new DummyEncryptor();
		} else {
			return new DesEncryptor(encryptionPassword);
		}
	}
	
	/**
	 * @return an {@link Encryptor} that can be used with the current Decryption password
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 */
	public static Encryptor getCurrentDecryptionEncryptor() throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		if(decryptionPassword == null || decryptionPassword.length() == 0) {
			return new DummyEncryptor();
		} else {
			return new DesEncryptor(decryptionPassword);
		}
	}
	
	/**
	 * Check to see if the encryption and decryption passwords are equal
	 * @return tru
	 */
	public static boolean isUpdateInProgress() {
		if(encryptionPassword != null && decryptionPassword != null) {
			return !encryptionPassword.equals(decryptionPassword);
		} else if (encryptionPassword == null && decryptionPassword == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * @param password the password to use when creating {@link Encryptor} for encrypting
	 */
	public static void setEncryptionPassword(String password) {
		encryptionPassword = password;
	}
	
	/**
	 * @param password the password to use when creating {@link Encryptor} for decrypting
	 */
	public static void setDecryptionPassword(String password) {
		decryptionPassword = password;
	}

	public static String getDecryptionPassword() {
		return decryptionPassword;
	}

	public static String getEncryptionPassword() {
		return encryptionPassword;
	}
}
