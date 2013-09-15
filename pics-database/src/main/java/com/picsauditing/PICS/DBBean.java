package com.picsauditing.PICS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class DBBean implements InitializingBean {

	private static final String PICS_JDBC_RESOURCE_NAME = "java:comp/env/jdbc/pics";
	private static final String PICS_READ_ONLY_JDBC_RESOURCE_NAME = "java:comp/env/jdbc/picsro";
	private static final String TRANSLATIONS_JDBC_RESOURCE_NAME = "java:comp/env/jdbc/translations";

	private static com.picsauditing.PICS.PICSDBLocator serviceLocator;
	private static DataSource dataSource;

	// volatile to use as part of the double-locking pattern
	static volatile DataSource staticDataSource;
	static volatile DataSource readOnlyDataSource;
	static volatile DataSource translationsDataSource;
	static AtomicInteger instantiationCount = new AtomicInteger(0);
	static AtomicInteger readOnlyDataSourceInstantiationCount = new AtomicInteger(0);
	static AtomicInteger translationsDataSourceInstantiationCount = new AtomicInteger(0);

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

	/**
	 * Use double-locking to improve the performance and ensure that only one
	 * instance of the readOnlyDataSource is created.
	 *
	 * @return
	 * @throws SQLException
	 */
	public static Connection getReadOnlyConnection() throws SQLException {
		DataSource dataSource = readOnlyDataSource;
		if (dataSource == null) {
			synchronized(DBBean.class) {
				dataSource = readOnlyDataSource;
				if (dataSource == null) {
					try {
						readOnlyDataSource = dataSource = new PICSDBLocator().getDataSource(PICS_READ_ONLY_JDBC_RESOURCE_NAME);
						DBBean.readOnlyDataSourceInstantiationCount.getAndIncrement();
					} catch (NamingException ne) {
						ne.printStackTrace();
						return null;
					}
				}
			}
		}

		return dataSource.getConnection();
	}

    /**
     * Use double-locking to improve the performance and ensure that only one
     * instance of the readOnlyDataSource is created.
     *
     * @return
     * @throws SQLException
     */
    public static Connection getTranslationsConnection() throws SQLException {
        DataSource dataSource = translationsDataSource;
        if (dataSource == null) {
            synchronized(DBBean.class) {
                dataSource = translationsDataSource;
                if (dataSource == null) {
                    try {
                        translationsDataSource = dataSource = new PICSDBLocator().getDataSource(TRANSLATIONS_JDBC_RESOURCE_NAME);
                        DBBean.translationsDataSourceInstantiationCount.getAndIncrement();
                    } catch (NamingException ne) {
                        ne.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return dataSource.getConnection();
    }

    private static DataSource getJdbcPics() throws NamingException {
		if (dataSource == null) {
			return getServiceLocator().getDataSource(PICS_JDBC_RESOURCE_NAME);
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
		DBBean.dataSource = dataSource;
	}

}
