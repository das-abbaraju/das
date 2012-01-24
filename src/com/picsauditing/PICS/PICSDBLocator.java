package com.picsauditing.PICS;

import java.net.URL;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.mail.Session;
import javax.print.attribute.standard.Destination;
import org.apache.commons.dbcp.ConnectionFactory;

public class PICSDBLocator {

	private InitialContext ic;

	public PICSDBLocator() {
		try {
			ic = new InitialContext();
		} catch (NamingException ne) {
			throw new RuntimeException(ne);
		}
	}

	private Object lookup(String jndiName) throws NamingException {
		return ic.lookup(jndiName);
	}

	/**
	 * This method helps in obtaining the jms connection factory
	 * 
	 * @return the factory for obtaining jms connection
	 */
	public ConnectionFactory getConnectionFactory(String connFactoryName) throws NamingException {
		return (ConnectionFactory) lookup(connFactoryName);
	}

	/**
	 * This method obtains the topc itself for a caller
	 * 
	 * @return the Topic Destination to send messages to
	 */
	public Destination getDestination(String destName) throws NamingException {
		return (Destination) lookup(destName);
	}

	/**
	 * This method obtains the datasource itself for a caller
	 * 
	 * @return the DataSource corresponding to the name parameter
	 */
	public DataSource getDataSource(String dataSourceName) throws NamingException {
		return (DataSource) lookup(dataSourceName);
	}

	/**
	 * This method obtains the E-mail session itself for a caller
	 * 
	 * @return the Session corresponding to the name parameter
	 */
	public Session getSession(String sessionName) throws NamingException {
		return (Session) lookup(sessionName);
	}

	/**
	 * @return the URL value corresponding to the env entry name.
	 */
	public URL getUrl(String envName) throws NamingException {
		return (URL) lookup(envName);
	}

	/**
	 * @return the boolean value corresponding to the env entry
	 */
	public boolean getBoolean(String envName) throws NamingException {
		Boolean bool = (Boolean) lookup(envName);
		return bool.booleanValue();
	}

	/**
	 * @return the String value corresponding to the env entry name.
	 */
	public String getString(String envName) throws NamingException {
		return (String) lookup(envName);
	}
}
