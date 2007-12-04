package com.picsauditing.PICS;

import java.sql.*;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBBean {
	//static final String driverClass = "com.mysql.jdbc.Driver";
	//static final String connString = "jdbc:mysql://localhost/PICS?zeroDateTimeBehavior=convertToNull&user=root&password=jose";

	public static Connection getDBConnection() throws Exception {
		DBBean dbBean = new DBBean();
                try{
                     DataSource ds = dbBean.getJdbcPics();
                     return ds.getConnection();
                }catch(NamingException ne){
                    ne.printStackTrace();
                    return null;
                }//catch
	}//getDBConnection

    private com.picsauditing.PICS.PICSDBLocator serviceLocator;

    private com.picsauditing.PICS.PICSDBLocator getServiceLocator() {
        if (serviceLocator == null)
            serviceLocator = new com.picsauditing.PICS.PICSDBLocator();    
        return serviceLocator;
    }

    private DataSource getJdbcPics() throws NamingException {
    	return (DataSource) getServiceLocator().getDataSource("java:comp/env/jdbc/pics");
    }
}//DBBean
