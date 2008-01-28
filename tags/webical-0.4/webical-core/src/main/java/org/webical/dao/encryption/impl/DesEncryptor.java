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

package org.webical.dao.encryption.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.webical.dao.encryption.Encryptor;

/**
 * Class to encrypt strings using PBE with MD5 hash and DES
 * @author ivo
 *
 */
public class DesEncryptor implements Encryptor {
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    // 8-byte Salt
    private byte[] salt = {
        (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };

    // Iteration count
    private int iterationCount = 128;

    /**
     * Creates the DesEncryptor
     * @param passPhrase the passphrase to use in encryption and decryption
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    public DesEncryptor(String passPhrase) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        // Create the key
        KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        encryptCipher = Cipher.getInstance(key.getAlgorithm());
        decryptCipher = Cipher.getInstance(key.getAlgorithm());

        // Prepare the parameter to the ciphers
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

        // Create the ciphers
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
    }

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.encryption.Encryptor#decrypt(java.lang.String)
	 */
	public String decrypt(String encryptedBase64String) throws IOException, IllegalBlockSizeException, BadPaddingException, DecoderException {
	    // Decode base64 to get bytes
		byte[] encrypted = Base64.decodeBase64(encryptedBase64String.getBytes("UTF8"));

	    // Decrypt
	    byte[] utf8UnencryptedString = decryptCipher.doFinal(encrypted);

	    // Decode using utf-8
	    return new String(utf8UnencryptedString, "UTF8");
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.encryption.Encryptor#encrypt(java.lang.String)
	 */
	public String encrypt(String unencrytedString) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
	    // Encode the string into bytes using utf-8
	    byte[] utf8UnencryptedString = unencrytedString.getBytes("UTF8");

	    // Encrypt
	    byte[] encrypted = encryptCipher.doFinal(utf8UnencryptedString);

	    // Encode bytes to base64 to get a string
	    return new String(Base64.encodeBase64(encrypted));
	}


}


