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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.dao.EncryptingDao;
import org.webical.dao.encryption.EncryptorFactory;
import org.webical.manager.EncryptionManager;
import org.webical.manager.WebicalException;

/**
 *
 * {@link EncryptionManager} implementation using DES
 * @author ivo
 *
 */
public class EncryptionManagerImpl implements EncryptionManager, InitializingBean {
	private static Log log = LogFactory.getLog(EncryptionManagerImpl.class);

	/** Spring injected {@link EncryptingDao}s */
	private List<EncryptingDao> encryptingDaos;

	//The file to read the passphrase from
	private File passphraseFile;

	private boolean initialized;
	private Boolean configured;


	/* (non-Javadoc)
	 * @see org.webical.manager.EncryptionManager#updateEncryptionPassphrase(java.lang.String)
	 */
	public void updateEncryptionPassphrase(String passphrase) throws WebicalException {
		if(passphrase == null || passphrase.length() == 0) {
			log.fatal("Please give a password");
			throw new IllegalArgumentException("Please give a password");
		}

		try {
			//Set new encryption passphrase
			EncryptorFactory.setEncryptionPassword(passphrase);

			//update all EncryptingDaos
			if(encryptingDaos != null) {
				for (EncryptingDao encryptingDao : encryptingDaos) {
					encryptingDao.updateAllEntities();
				}
			}

			//Set new decryption passphrase
			EncryptorFactory.setDecryptionPassword(passphrase);

			//Hash and save to the file
			writePassphraseToFile(hashString(passphrase));
		} catch (Exception e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.EncryptionManager#isConfigured()
	 */
	public boolean isConfigured() throws WebicalException {
		if(configured == null) {
			if(isPassphraseFileCorrect()) {
				String passphrase = null;
				try {
					passphrase = getPassPhraseFromFile();
				} catch (IOException e) {
					log.error("Could not read passphrase file", e);
					throw new WebicalException("Could not read passphrase file", e);
				}
				if(passphrase != null && passphrase.length() > 0) {
					return true;
				}
			}
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.EncryptionManager#isInitialized()
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if(passphraseFile == null) {
			log.fatal("Passphrasefile not configured");
			throw new ExceptionInInitializerError("Passphrasefile not configured");
		}

		initialize();
	}

	///////////////
	/// Utility ///
	///////////////

	/**
	 * Initializes the encryption system.
	 * @throws WebicalException if initialization couldn't complete (eg missing configuration)
	 */
	private void initialize() throws WebicalException {
		String passphrase = null;
		try {
			if(isPassphraseFileCorrect()) {
				passphrase = getPassPhraseFromFile();
				if(passphrase == null || passphrase.length() == 0) {
					log.error("Passphrase file is empty");
					throw new WebicalException("Passphrase file is empty");
				}
			}

			EncryptorFactory.setDecryptionPassword(passphrase);
			EncryptorFactory.setEncryptionPassword(passphrase);
		} catch (IOException e) {
			log.error("Could not read passphrase file", e);
			throw new WebicalException(e);
		}

		initialized = true;
	}

	/**
	 * Checks the passphraseFile
	 * @return true if correct
	 */
	private boolean isPassphraseFileCorrect() {
		return (passphraseFile != null
				&& passphraseFile.exists()
				&& passphraseFile.canRead()
				&& passphraseFile.isFile());
	}

	/**
	 * Retrieves the passphrase from the file
	 * @return the passphrase or null
	 * @throws IOException on IO errors
	 */
	private String getPassPhraseFromFile() throws IOException {
		if(isPassphraseFileCorrect()) {
			FileReader fr = null;
			BufferedReader br = null;
			try {
				StringBuffer buffer = new StringBuffer();
				fr = new FileReader(passphraseFile);
				br = new BufferedReader(fr);
				String line = null;
				while((line = br.readLine()) != null) {
					buffer.append(line);
				}
				return buffer.toString();
			} finally {
				if(fr != null) {
					fr.close();
				}
				if(br != null) {
					br.close();
				}
			}
		} else {
			return null;
		}
	}

	private void writePassphraseToFile(String passphrase) throws IOException {
		FileWriter outFile = null;
		PrintWriter out = null;
		try {
			outFile = new FileWriter(passphraseFile);
			out = new PrintWriter(outFile);
			out.println(passphrase);
		} finally {
			out.close();
			outFile.close();
		}
	}

	private String hashString(String passphrase) {
		//TODO @see ivo
		return passphrase;
	}

	///////////////////////
	/// Getters/Setters ///
	///////////////////////

	/**
	 * @param passphraseFile set the passphrasefile for this factory
	 */
	public void setPassphraseFile(File passphraseFile) {
		this.passphraseFile = passphraseFile;
	}

	/**
	 * Set by Spring
	 * @param encryptingDaos a List of {@link EncryptingDao} to update on encryption change
	 */
	public void setEncryptingDaos(List<EncryptingDao> encryptingDaos) {
		this.encryptingDaos = encryptingDaos;
	}

}
