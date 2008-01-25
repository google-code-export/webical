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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.DecoderException;

/**
 * Interface for data encrypters
 * @author ivo
 *
 */
public interface Encryptor {
	
    /**
     * Encrypts a String
     * @param unencrytedString the string to encrypt
     * @return the encrypted String (Base64 encoded)
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
	public String encrypt(String unencrytedString) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException;
	
    /**
     * Decrypts a previously encrypted String
     * @param encryptedBase64String the String to decrypt (Base64 encoded)
     * @return The decrypted String
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws DecoderException
     */
	public String decrypt(String encryptedBase64String) throws IOException, IllegalBlockSizeException, BadPaddingException, DecoderException;
}
