package com.picsauditing.actions.qa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.search.SelectSQL;

public class TabularResultQueryRunner implements QueryRunner {
	private String query;
	private TabularModel data = new TabularData();
	private Connection connection;

	public TabularModel run() throws SQLException {
		Connection dbConnection = getDbConnection();
		Statement stmt = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
			setColumnNamesOnData(rs);
			setDataValues(rs);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (dbConnection != null)
				dbConnection.close();
		}
		return data;
	}

	public void setSelectSQL(SelectSQL query) {
		this.query = query.toString();
	}

	public void setSelectSQL(String query) {
		this.query = query;
	}

	public void setTabularModelForData(TabularModel data) {
		this.data = data;
	}

	private void setColumnNamesOnData(ResultSet rs) throws SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		int columnCount = rsMeta.getColumnCount();
		List<String> columnNames = new ArrayList<String>();
		for (int i = 1; i <= columnCount; i++) {
			columnNames.add(rsMeta.getColumnName(i));
		}
		data.setColumnNames(columnNames);
	}

	private void setDataValues(ResultSet rs) throws SQLException {
		int row = 1;
		while (rs.next()) {
			for (int column = 1; column <= data.getColumnCount(); column++) {
				data.setValueAt(rs.getObject(column), row, column);
			}
			row++;
		}
	}

	private Connection getDbConnection() throws SQLException {
		if (this.connection == null) {
			this.connection = DBBean.getDBConnection();
		}
		return this.connection;
	}

	protected void setDbConnection(Connection connection) {
		this.connection = connection;
	}

}
