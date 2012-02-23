package com.picsauditing.PICS;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class DBBean implements InitializingBean  {
	private static DataSource staticDataSource;
	private DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static Connection getDBConnection() throws SQLException {
		if (staticDataSource != null) {
			return staticDataSource.getConnection();
		}
		try {
			DataSource dataSource = getJdbcPics();
			return dataSource.getConnection();
		} catch (NamingException ne) {
			ne.printStackTrace();
			return null;
		}
	}


	private static com.picsauditing.PICS.PICSDBLocator serviceLocator;

	private static com.picsauditing.PICS.PICSDBLocator getServiceLocator() {
		if (serviceLocator == null)
			serviceLocator = new com.picsauditing.PICS.PICSDBLocator();
		return serviceLocator;
	}

	private static DataSource getJdbcPics() throws NamingException {
		return (DataSource) getServiceLocator().getDataSource("java:comp/env/jdbc/pics");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert dataSource != null;
		staticDataSource = dataSource;
	}

}
