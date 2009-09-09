package com.picsauditing.actions.chart;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.util.chart.DataRow;

public class ChartDAO {
	/**
	 * 
	 * @param sql valid column names are label, value, sortBy, series, link
	 * @return
	 * @throws SQLException
	 */
	public List<DataRow> select(String sql) throws SQLException {
		Connection Conn = DBBean.getDBConnection();
		Statement stmt = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		try {
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<DataRow> data = new ArrayList<DataRow>();
			while (rs.next()) {
				DataRow row = new DataRow();
				row.setLabel(rs.getString("label"));
				row.setValue(rs.getFloat("value"));
				try {row.setIndex(rs.getString("sortBy"));
				} catch (SQLException e) { }
				try {row.setSeries(rs.getString("series"));
				} catch (SQLException e) { }
				try {row.setSeries(rs.getString("link"));
				} catch (SQLException e) { }
				data.add(row);
			}
			rs.close();

			return data;
		} finally {
			stmt.close();
			Conn.close();
		}
	}
}
