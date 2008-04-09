package com.picsauditing.PICS.pqf;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class ConAuditBean extends com.picsauditing.PICS.DataBean {
	private int auditID;
	private int auditTypeID;
	private String auditType;
	private int conID;
	
	public ConAuditBean(int auditID) throws SQLException {
		String sql = "SELECT ca.auditID, ca.conID, ca.auditTypeID, t.legacyCode FROM contractor_audit ca " +
				"JOIN audit_type t USING (auditTypeID) WHERE auditID = "+auditID;
		query(sql);
	}
	
	public ConAuditBean(String conID, String auditType) throws SQLException {
		String sql = "SELECT ca.auditID, ca.conID, ca.auditTypeID, t.legacyCode FROM contractor_audit ca " +
		"JOIN audit_type t USING (auditTypeID) WHERE conID = "+conID+" AND t.legacyCode = '"+auditType+"'";
		query(sql);
	}
	
	private void query(String sql) throws SQLException {
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			SQLResult.next();
			conID = SQLResult.getInt("conID");
			auditID = SQLResult.getInt("auditID");
			auditTypeID = SQLResult.getInt("auditTypeID");
			auditType = this.getString(SQLResult, "legacyCode");
		} finally {
			DBClose();
		}
	}

	public int getAuditID() {
		return auditID;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public String getAuditType() {
		return auditType;
	}

	public int getConID() {
		return conID;
	}
	
	
}
