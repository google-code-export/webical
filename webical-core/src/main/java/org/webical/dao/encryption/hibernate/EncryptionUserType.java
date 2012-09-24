/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    $Id$
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

package org.webical.dao.encryption.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.util.EqualsHelper;
import org.webical.dao.encryption.Encryptor;
import org.webical.dao.encryption.EncryptorFactory;

/**
 * A hibernate {@link UserType} that enables encryption on a field.<br />
 * Two steps are needed to enable this on a field:
 * <ul>
 * <li>First enable the {@link UserType} in the hibernate-mapping file like so: 
 * <code>&lt;typedef name="encrypted" class="org.webical.aspect.dao.encryption.hibernate.EncryptionUserType" /&gt;</code></li>
 * <li>Then use <code>type="encrypted"</code> in the mapping to enable this on a field</li>
 * <br />
 * This class makes use of the {@link EncryptorFactory} to get the encrypters/decrypters ( {@link Encryptor} ) and check 
 * if an encryption password upgrade is in progress.
 * <br />
 * @author ivo
 *
 */
public class EncryptionUserType implements UserType {
	private static Log log = LogFactory.getLog(EncryptionUserType.class);

	private static int sqlType = org.hibernate.type.StandardBasicTypes.STRING.sqlType();
	private static int[] sqlTypes = new int[]{ sqlType };

	/**
	 * Decrypts the message before returning
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		String message = rs.getString(names[0]);
		try {
			return rs.wasNull() ? null : EncryptorFactory.getCurrentDecryptionEncryptor().decrypt(message);
		} catch (Exception e) {
			log.error("Could not decrypt message", e);
			throw new HibernateException(e);
		}
	}

	/**
	 * Encrypts the message before storing
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		if(value == null) {
			st.setNull(index, sqlType);
		} else {
			try {
				st.setString(index, EncryptorFactory.getCurrentEncryptionEncryptor().encrypt((String)value));
			} catch (Exception e) {
				log.error("Could not encrypt message", e);
				throw new HibernateException(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
	 */
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		if(cached == null) {
			return null;
		} else {
			return deepCopy(cached);
		}
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		if(value == null) {
			return null;
		} else {
			return (Serializable)deepCopy(value);
		}
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	/**
	 * If there is a encryption password update in progress always returns false 
	 * to make sure the encryption is updated in the database
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object x, Object y) throws HibernateException {
		if(EncryptorFactory.isUpdateInProgress()) {
			log.debug("In ecryption update mode; returning false");
			return false;
		} else {
			return EqualsHelper.equals(x, y);
		}
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	public Class returnedClass() {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return sqlTypes;
	}
}
