package com.picsauditing.actions.chart;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.util.DatabaseUtil;
import com.picsauditing.util.chart.DataRow;

public class ChartDAO {
	
	/**
	 * 
	 * @param sql valid column names are label, value, sortBy, series, link
	 * @return
	 * @throws SQLException
	 */
	public List<DataRow> select(String sql) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBBean.getDBConnection();
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			rs = stmt.executeQuery(sql);
			ArrayList<DataRow> data = new ArrayList<DataRow>();
			while (rs.next()) {
				DataRow row = new DataRow();
				row.setLabel(rs.getString("label"));
				row.setValue(rs.getFloat("value"));
				
				try {
					row.setIndex(rs.getString("sortBy"));
				} catch (SQLException e) {
					// do nothing
				}
				
				try {
					row.setSeries(rs.getString("series"));
				} catch (SQLException e) { 
					// do nothing
				}
				
				try {
					row.setLink(rs.getString("link"));
				} catch (SQLException e) { 
					// do nothing
				}
				
				data.add(row);
			}

			return data;
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(stmt);
			DatabaseUtil.closeConnection(conn);
		}
	}
}
