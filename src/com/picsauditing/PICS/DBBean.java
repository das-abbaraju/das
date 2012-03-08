package com.picsauditing.PICS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.util.Testable;

public class DBBean implements InitializingBean {
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
		if (staticDataSource == null) {
			try {
				staticDataSource = getJdbcPics();
			} catch (NamingException ne) {
				ne.printStackTrace();
				return null;
			}
		}
		return staticDataSource.getConnection();
	}

	private static com.picsauditing.PICS.PICSDBLocator serviceLocator;
	// The following field is just to support the unit test that proves that this code is threadsafe
	public static AtomicInteger serviceLocatorCount = new AtomicInteger();

	private static synchronized com.picsauditing.PICS.PICSDBLocator getServiceLocator() {
		if (serviceLocator == null) {
			serviceLocator = new com.picsauditing.PICS.PICSDBLocator();
			serviceLocatorCount.addAndGet(1);
		}
		return serviceLocator;
	}

	@Testable
	static DataSource getJdbcPics() throws NamingException {
		return (DataSource) getServiceLocator().getDataSource("java:comp/env/jdbc/pics");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert dataSource != null;
		staticDataSource = dataSource;
	}

}
