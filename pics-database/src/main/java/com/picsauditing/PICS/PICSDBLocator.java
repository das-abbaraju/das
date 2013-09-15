package com.picsauditing.PICS;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
	 * This method obtains the datasource itself for a caller
	 * 
	 * @return the DataSource corresponding to the name parameter
	 */
	public DataSource getDataSource(String dataSourceName) throws NamingException {
		return (DataSource) lookup(dataSourceName);
	}
}
