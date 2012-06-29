package com.picsauditing.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.util.DatabaseUtil;

public class Database {
	private final Logger logger = LoggerFactory.getLogger(Database.class);
	
	private int allRows = 0;

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> select(String sql, boolean countRows) throws SQLException {
		Connection Conn = null;
		Statement stmt = null;
		ResultSet tempRS = null;
		RowSetDynaClass rsdc;

		try {
			Conn = DBBean.getDBConnection();
			
			stmt = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(sql);
			rsdc = new RowSetDynaClass(rs, false, true);
			if (countRows) {
				tempRS = stmt.executeQuery("SELECT FOUND_ROWS()");
				tempRS.next();
				allRows = tempRS.getInt(1);
			}

			return rsdc.getRows();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeResultSet(tempRS);
			DatabaseUtil.closeStatement(stmt);
			DatabaseUtil.closeConnection(Conn);
		}
	}

	public long executeInsert(String sql) throws SQLException {
		Connection Conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Conn = DBBean.getDBConnection();
			stmt = Conn.createStatement();
			
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();

			long id = -1;
			while (rs.next()) {
				id = rs.getLong(1);
			}
			
			return id;
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(stmt);
			DatabaseUtil.closeConnection(Conn);
		}
	}

	public int executeUpdate(String sql) throws SQLException {
		Connection Conn = null;
		Statement stmt = null;
		
		try {
			Conn = DBBean.getDBConnection();
			stmt = Conn.createStatement();
			
			return stmt.executeUpdate(sql);
		} finally {
			DatabaseUtil.closeStatement(stmt);
			DatabaseUtil.closeConnection(Conn);
		}
	}

	public boolean execute(String sql) throws SQLException {
		Connection Conn = null;
		Statement stmt = null;
		try {
			Conn = DBBean.getDBConnection();
			stmt = Conn.createStatement();
			
			return stmt.execute(sql);
		} finally {
			DatabaseUtil.closeStatement(stmt);
			DatabaseUtil.closeConnection(Conn);
		}
	}

	public int getAllRows() {
		return allRows;
	}

	static public int toInt(BasicDynaBean row, String columnName) {
		Object value = row.get(columnName);
		return Integer.parseInt(value.toString());
	}

	public static boolean toBoolean(BasicDynaBean row, String columnName) {
		Object value = row.get(columnName);

		if (value.toString().equals("1"))
			return true;

		return Boolean.parseBoolean(value.toString());
	}

	public static float toFloat(BasicDynaBean row, String columnName) {
		Object value = row.get(columnName);
		return Float.parseFloat(value.toString());
	}

	public static String getDatabaseName() throws SQLException {
		Connection connection = null;
		String databaseName = "";

		try {
			connection = DBBean.getDBConnection();
			
			databaseName = connection.getCatalog();
		} finally {
			DatabaseUtil.closeConnection(connection);
		}

		return databaseName;
	}
}
