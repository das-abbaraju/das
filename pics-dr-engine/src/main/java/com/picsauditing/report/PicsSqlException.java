package com.picsauditing.report;

import java.sql.SQLException;

public class PicsSqlException extends SQLException {

	private static final long serialVersionUID = 1L;

	private String sql = new String();

	public PicsSqlException(SQLException se, String sql) {
		super(se);
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}