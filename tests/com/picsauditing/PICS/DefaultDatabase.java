package com.picsauditing.PICS;

import java.sql.DriverManager;
import com.mysql.jdbc.Connection;

/**
 * This is a simple database connection that we use during JUnit testing to get 
 * a connection to the alpha database. Do not use this for any production code!
 * 
 * @author Trevor
 */
public class DefaultDatabase {
	/**
	 * @return a connection that can be added to a PICS Database
	 * @throws Exception
	 */
	static public Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		return (Connection) DriverManager.getConnection("jdbc:mysql://alpha.picsauditing.com:3306/pics_alpha", "pics", "pics");
	}
}
