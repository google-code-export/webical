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

package org.webical.dao.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Takes care of all connection related issues
 * @author ivo
 *
 */
public class ConnectionUtil {
	
	public static final String HTTP_PUT_METHOD = "PUT";
	public static final String HTTPS_PROTOCOL = "https";

	/**
	 * Returns a HttpUrlConnection for a URL pointing to an HTTP resource. Make sure it does point to a HTTP resource or a IlliagalArgumentException is thrown
	 * @param url the url of a HTTP resource
	 * @return a HttpUrlConnection
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpUrlConnection(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		if(connection instanceof HttpURLConnection) {
			return (HttpURLConnection) connection;
		} else {
			throw new IllegalArgumentException("Url is not targeted at an HTTP resource" + url);
		}
	}

	/**
	 * Returns a HttpsUrlConnection for a URL pointing to an HTTPS resource. Make sure it does point to a HTTPS resource or a IlliagalArgumentException is thrown
	 * @param url the url of a HTTPS resource
	 * @param ignoreBadCertificates a boolean specifying if the certificates have to be signed
	 * @return a HttpsURLConnection
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static HttpsURLConnection getHttpsUrlConnection(URL url, boolean ignoreBadCertificates) throws KeyManagementException, NoSuchAlgorithmException, UnknownHostException, IOException {
		URLConnection connection = getURLConnection(url, ignoreBadCertificates);
		if(connection instanceof HttpsURLConnection) {
			return (HttpsURLConnection) connection;
		} else {
			throw new IllegalArgumentException("Url is not targeted at an HTTPS resource" + url);
		}
	}
	
	/**
	 * Creates a SSl connection
	 * @param url the url to connect to
	 * @param ignoreBadCertificates a boolean specifying if the certificates have to be signed
	 * @return a URLConnection
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static URLConnection getURLConnection(URL url, boolean ignoreBadCertificates) throws KeyManagementException, NoSuchAlgorithmException, UnknownHostException, IOException {
		
		SSLSocketFactory sslSocketFactory = null;
		
		if(ignoreBadCertificates) {
			//	Set a Trustmanager that accepst all certificates
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, getAllTrustingTrustManager(), new java.security.SecureRandom());
			sslSocketFactory = sslContext.getSocketFactory();
		} else {
			//Use a default trustmanager
			sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		}
		
		HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
		return url.openConnection();
	}
	
	/**
	 * Sets the authenticator for this thread
	 * @param userName the username to use for authentication
	 * @param password the password to use for authentication
	 */
	public static void setAuthentication(final String userName, final String password) {
		Authenticator.setDefault(new Authenticator() {

			/** 
			 * Authenticator with the right passwordAuthentication
			 * @see java.net.Authenticator#getPasswordAuthentication()
			 */
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password.toCharArray());
			}
			
		});
	}
	
	/**
	 * @return a TrustManager that allows all certificates
	 */
	public static TrustManager[] getAllTrustingTrustManager() {
		return new TrustManager[]{
			new X509TrustManager() {
			
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					//return new X509Certificate[]{};
					return null;
				}
				
				 public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				     //No need to implement.
				}
				 
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				     //No need to implement.
				}
			}
		};
	}
}
