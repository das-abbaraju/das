package com.picsauditing.PICS;

import java.sql.*;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBBean {
	public static Connection getDBConnection() throws SQLException {
		DBBean dbBean = new DBBean();
        try{
             DataSource ds = dbBean.getJdbcPics();
             return ds.getConnection();
        }catch(NamingException ne){
            ne.printStackTrace();
            return null;
        }
	}

    private com.picsauditing.PICS.PICSDBLocator serviceLocator;
    private com.picsauditing.PICS.PICSDBLocator getServiceLocator() {
        if (serviceLocator == null)
            serviceLocator = new com.picsauditing.PICS.PICSDBLocator();    
        return serviceLocator;
    }

    private DataSource getJdbcPics() throws NamingException {
    	return (DataSource) getServiceLocator().getDataSource("java:comp/env/jdbc/pics");
    }
}
