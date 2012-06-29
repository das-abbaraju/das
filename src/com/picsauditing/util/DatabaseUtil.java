package com.picsauditing.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
	
	/**
	 * Enforce singleton behavior
	 */
	private DatabaseUtil() { }
	
	public static void closeResultSet(ResultSet resultSet) {
		if (resultSet == null)
			return;
		
		try {
			resultSet.close();
		} catch (SQLException e) {
			logger.error("Error while closing resultSet in DatabaseUtil", e);
		}
	}
	
	public static void closeConnection(Connection connection) {
		if (connection == null)
			return;
		
		try {
			connection.close();
		} catch (SQLException e) {
			logger.error("Error while closing connection in DatabaseUtil", e);
		}
	}

	public static void closeStatement(Statement statement) {
		if (statement == null)
			return;
		
		try {
			statement.close();
		} catch (SQLException e) {
			logger.error("Error while closing statement in DatabaseUtil", e);
		}
	}

}
