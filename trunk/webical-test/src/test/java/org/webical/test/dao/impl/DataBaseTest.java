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

package org.webical.test.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.webdav.WebdavServlet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.orm.hibernate3.HibernateTemplate;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
// import org.dbunit.ext.mysql.MySqlDataTypeFactory;

import org.webical.ApplicationSettings;
import org.webical.User;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.Option;
import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.UserPluginSettings;
import org.webical.dao.encryption.EncryptorFactory;
import org.webical.dao.hibernateImpl.SessionFactoryUtils;

import org.webical.test.TestUtils;

/**
 * SuperClass used for all Hibernate testclasses
 * Takes care of the dbunit initialisation
 * Sets up Hibernate
 * @author ivo
 *
 */
public abstract class DataBaseTest extends DatabaseTestCase {

	private static final String DBUNIT_TEST_CONFIGURATION_FILE = "dbunit-test-configuration.xml";

	private static final String DATASOURCE_PASSWORD = "datasource.password";

	private static final String DATASOURCE_USERNAME = "datasource.username";

	private static final String DATASOURCE_URL = "datasource.url";

	private static final String DATASOURCE_DRIVERCLASS = "datasource.driverclass";

	private static final String DATASOURCE_DIALECT = "datasource.dialect";

	private static final String DATASET_FILE = "dataset-file";

	private static final String WEBDAV_ROOT = "/etc/webdav_root";
	private static final String WEBDAV_CLEAN = "/etc/webdav_clean";

	private static XMLConfiguration dbunitConfiguration;

	private static String datasetFilename;

	private static Log log = LogFactory.getLog(DataBaseTest.class);

	private org.hibernate.cfg.Configuration hibernateConfiguration = new org.hibernate.cfg.Configuration();
	private SessionFactory sessionFactory;
	private Session session;
	private HibernateTemplate hibernateTemplate;

	private Server server;

	/** Initialize the dbunit configuration */
	static {
		try {
			log.debug(System.getProperty(TestUtils.SYSTEM_USER_DIR));
			dbunitConfiguration = new XMLConfiguration(System.getProperty(TestUtils.SYSTEM_USER_DIR) + "/" + TestUtils.RESOURCE_DIRECTORY + DBUNIT_TEST_CONFIGURATION_FILE);
			datasetFilename = System.getProperty(TestUtils.SYSTEM_USER_DIR) + "/" + dbunitConfiguration.getString(DATASET_FILE);
			log.debug(datasetFilename);
		} catch (ConfigurationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		log.debug("Setting up the base database testclass");

		ApplicationSettings applicationSettings = new ApplicationSettings();
		applicationSettings.setPluginPackageExtension(".zip");
		applicationSettings.setCalendarRefreshTimeMs(3000);
		TestUtils.initializeApplicationSettingsFactory(applicationSettings);

		//Set up hibernate
		createHibernateConfiguration();
		registerPersistedClasses();

		//Set up the sessionFactory and HibernateTemplate
		sessionFactory = hibernateConfiguration.buildSessionFactory();
		new SessionFactoryUtils().setSessionFactory(sessionFactory);
		SessionFactoryUtils.createSession();

		//Insert test data
		insertData();

		log.debug("Done setting up the base database testclass");

		//Start the webdav server
		prepareWebdavRoot();
		startWebDavServer();
	}

	/**
	 * Starts a webdav server to test against
	 * @throws Exception
	 */
	private void startWebDavServer() throws Exception {
		log.debug("Starting up the WebDav server");
		server = new Server(TestUtils.portNoTest);
		ServletHolder servletHolder = new ServletHolder(new WebdavServlet());
		servletHolder.setInitParameter("ResourceHandlerImplementation", "net.sf.webdav.LocalFileSystemStorage");
		servletHolder.setInitParameter("rootpath", System.getProperty(TestUtils.SYSTEM_USER_DIR) + WEBDAV_ROOT);
		servletHolder.setInitParameter("storeDebug", "0");

		Context root = new Context(server, "/", Context.SESSIONS);
		root.addServlet(servletHolder, "/*");
		server.start();
	}

	/**
	 * Stops the server cleanly
	 * @throws Exception
	 */
	protected void stopWebDavServer() throws Exception {
		log.debug("Stopping the WebDav server");
		if (server != null) {
			server.stop();
		}
	}

	/**
	 * Copies all the resources to the right dir
	 * @throws IOException
	 */
	private void prepareWebdavRoot() throws IOException {
		log.debug("Preparing the WebDav server's webroot");
		File[] filesToCopy = new File(System.getProperty(TestUtils.SYSTEM_USER_DIR) + WEBDAV_CLEAN).listFiles();
		if (filesToCopy != null) {
			for (File file : filesToCopy) {
				if (file.isFile()) {
					copyFile(file, new File(System.getProperty(TestUtils.SYSTEM_USER_DIR) + WEBDAV_ROOT + "/" + file.getName()));
				}
			}
		}
	}

	/*
	 * Export DB to file
	 */
	public void exportDB() {
	    // full database export
		IDatabaseConnection connection;
		try {
			connection = new DatabaseConnection(getJDBCConnection());
			connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
			IDataSet fullDataSet = connection.createDataSet();
			StringBuilder exportFileName = new StringBuilder();
			exportFileName.append(datasetFilename.substring(0, datasetFilename.length() - 4));
			exportFileName.append("-extraction.xml");
		    FlatXmlDataSet.write(fullDataSet, new FileOutputStream(exportFileName.toString()));
		    log.info("Wrote dataset to file: " + exportFileName);
		} catch (Exception e) {
			log.error(e);
		}
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	public final void tearDown() throws Exception {
		//Shut down the server
		stopWebDavServer();
		//Reset the encryption passwords
		EncryptorFactory.setEncryptionPassword(null);
		EncryptorFactory.setDecryptionPassword(null);
	}

	/* (non-Javadoc)
	 * @see org.dbunit.DatabaseTestCase#getConnection()
	 */
	@Override
	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(getJDBCConnection());
	}

/*	private void setupSession() {
		//Get the session and bind it to the transactionmanager
		session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));

	}*/

	/**
	 * Configures a jdbc Connection for the dbunit data insertion
	 * @return a configured Connection
	 * @throws ClassNotFoundException if the driverclass can not be loaded
	 * @throws SQLException if the Connection can not be created
	 */
	private static Connection getJDBCConnection() throws ClassNotFoundException, SQLException {
		@SuppressWarnings("unused")
		Class driverClass = Class.forName(dbunitConfiguration.getString(DATASOURCE_DRIVERCLASS));
		return DriverManager.getConnection(
				dbunitConfiguration.getString(DATASOURCE_URL),
				dbunitConfiguration.getString(DATASOURCE_USERNAME), 
				dbunitConfiguration.getString(DATASOURCE_PASSWORD));
	}

	/* (non-Javadoc)
	 * @see org.dbunit.DatabaseTestCase#getDataSet()
	 */
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream(datasetFilename));
	}

	/**
	 * Sets up hibernates confiuration
	 */
	private void createHibernateConfiguration() {
		log.debug("Setting up hibernate");
		hibernateConfiguration.setProperty(Environment.DRIVER, dbunitConfiguration.getString(DATASOURCE_DRIVERCLASS));
		hibernateConfiguration.setProperty(Environment.URL,dbunitConfiguration.getString(DATASOURCE_URL));
		hibernateConfiguration.setProperty(Environment.USER, dbunitConfiguration.getString(DATASOURCE_USERNAME));
		hibernateConfiguration.setProperty(Environment.PASS, dbunitConfiguration.getString(DATASOURCE_PASSWORD));
		hibernateConfiguration.setProperty(Environment.DIALECT, dbunitConfiguration.getString(DATASOURCE_DIALECT));
		hibernateConfiguration.setProperty(Environment.QUERY_SUBSTITUTIONS, "true=1 false=0");
		hibernateConfiguration.setProperty(Environment.SHOW_SQL, "true");
		hibernateConfiguration.setProperty(Environment.MAX_FETCH_DEPTH, "1");
		hibernateConfiguration.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
		hibernateConfiguration.setProperty(Environment.USE_REFLECTION_OPTIMIZER, "false");
		System.setProperty("hibernate.cglib.use_reflection_optimizer", "false");
	}

	/**
	 * Registers all hibernate persisted classes
	 */
	private void registerPersistedClasses() {
		log.debug("Registering hibernate classes and general configuration");
		hibernateConfiguration.addFile("../webical-core/target/classes/org/webical/TypeRegistration.hbm.xml");
		hibernateConfiguration.addClass(User.class);
		hibernateConfiguration.addClass(Calendar.class);
		hibernateConfiguration.addClass(Event.class);
		hibernateConfiguration.addClass(ApplicationSettings.class);
		hibernateConfiguration.addClass(Settings.class);
		hibernateConfiguration.addClass(PluginSettings.class);
		hibernateConfiguration.addClass(UserPluginSettings.class);
		hibernateConfiguration.addClass(Option.class);
	}

	/**
	 * Inserts data into the test database
	 * @throws SQLException 
	 */
	private void insertData() throws SQLException {
		log.debug("Inserting test data from file: " + datasetFilename);
		IDatabaseConnection connection = null;
		try {
			connection = getConnection();
			connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());

			DatabaseOperation databaseOperation = DatabaseOperation.CLEAN_INSERT;
			databaseOperation.execute(connection, getDataSet());
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Copies source file to derstination file. If the derstination file does not exist, it is created
	 * @param sourceFile
	 * @param destinationFile
	 * @throws IOException
	 */
	private void copyFile(File sourceFile, File destinationFile) throws IOException {
		if(sourceFile == null || destinationFile == null) {
			return;
		}
		log.debug("Copying file : " + sourceFile.getAbsolutePath() + " to: " + destinationFile.getAbsolutePath());

        InputStream in = new FileInputStream(sourceFile);
        OutputStream out = new FileOutputStream(destinationFile);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

	/**
	 * @return the configured hibernate template
	 */
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @return the open session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Just for testing purposes, see if the dataset can be inserted correctly
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) {

//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).addHandler(new ConsoleAppender());
		DataBaseTest dataBaseTest = new DataBaseTest() {};
		if (args == null || args.length != 1 || !args[0].equals("extract")) {
			log.info("Inserting");

			try {
				dataBaseTest.setUp();
				Thread.sleep(15000);		// sleep 15 seconds
				dataBaseTest.tearDown();
			} catch (SQLException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
				System.err.println(e);
			}
		}
		// Extract db
		log.info("Extracting");
	    // full database export
		dataBaseTest.exportDB();
	}
}
