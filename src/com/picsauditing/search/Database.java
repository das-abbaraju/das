package com.picsauditing.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;

import com.picsauditing.PICS.DBBean;

public class Database {

	private int allRows = 0;

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> select(String sql, boolean countRows) throws SQLException {
		Connection Conn = DBBean.getDBConnection();
		Statement stmt = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		RowSetDynaClass rsdc;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			rsdc = new RowSetDynaClass(rs, false);
			rs.close();

			if (countRows) {
				ResultSet tempRS = stmt.executeQuery("SELECT FOUND_ROWS()");
				tempRS.next();
				allRows = tempRS.getInt(1);
				tempRS.close();
			}
			return rsdc.getRows();
		} finally {
			stmt.close();
			Conn.close();
		}
	}

	public long executeInsert(String sql) throws SQLException {
		Connection Conn = DBBean.getDBConnection();
		Statement stmt = Conn.createStatement();
		try {
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();

			long id = -1;
			while (rs.next()) {
				id = rs.getLong(1);
			}
			return id;
		} finally {
			stmt.close();
			Conn.close();
		}
	}

	public int executeUpdate(String sql) throws SQLException {
		Connection Conn = DBBean.getDBConnection();
		Statement stmt = Conn.createStatement();
		try {
			return stmt.executeUpdate(sql);
		} finally {
			stmt.close();
			Conn.close();
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
		return Boolean.parseBoolean(value.toString());
	}

	public static float toFloat(BasicDynaBean row, String columnName) {
		Object value = row.get(columnName);
		return Float.parseFloat(value.toString());
	}

}
