package com.picsauditing.PICS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.util.Testable;

public class DBBean implements InitializingBean {
	
	private static com.picsauditing.PICS.PICSDBLocator serviceLocator;
	private static DataSource dataSource;
	final static Logger logger = LoggerFactory.getLogger(PICSDBLocator.class); 
	
	// volatile to use as part of the double-locking pattern
	@Testable
	static volatile DataSource staticDataSource;
	
	@Testable
	static AtomicInteger instantiationCount = new AtomicInteger(0); 
	
	/**
	 * Enforce the singleton nature of this class by making the 
	 * constructor private 
	 */
	private DBBean() {}
	
	/**
	 * Use double-locking to improve the performance and ensure that only one
	 * instance of the staticDataSource is created.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getDBConnection() throws SQLException {
		DataSource result = staticDataSource;
		if (result == null) {
			synchronized(DBBean.class) {
				result = staticDataSource;
				if (result == null) {
					try {
						staticDataSource = result = getJdbcPics();
						DBBean.instantiationCount.getAndIncrement();
					} catch (NamingException ne) {
						ne.printStackTrace();
						return null;
					}
				}
			}
		}
		
		return result.getConnection();
	}

	private static DataSource getJdbcPics() throws NamingException {
		if (dataSource == null) {
			return (DataSource) getServiceLocator().getDataSource("java:comp/env/jdbc/pics");
		}
		
		return dataSource;
	}
	
	private static com.picsauditing.PICS.PICSDBLocator getServiceLocator() {
		if (serviceLocator == null) {
			serviceLocator = new com.picsauditing.PICS.PICSDBLocator();
		}
		
		return serviceLocator;
	}

	/**
	 * Used by Spring InitializingBean
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		assert dataSource != null;
		staticDataSource = dataSource;
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		logger.info("Setting Datasource via injection");
		DBBean.dataSource = dataSource;
	}

}
