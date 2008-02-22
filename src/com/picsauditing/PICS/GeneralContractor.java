package com.picsauditing.PICS;

import java.util.ArrayList;

public class GeneralContractor extends DataBean {
	private int conID;
	private int opID;
	private String dateAdded;
	private int approvedByID = 0;
	private String approvedStatus = "";
	private String approvedDate = "";
	
	public int getConID() {
		return conID;
	}
	public void setConID(int conID) {
		this.conID = conID;
	}
	public int getOpID() {
		return opID;
	}
	public void setOpID(int opID) {
		this.opID = opID;
	}
	public String getDateAdded() {
		return dateAdded;
	}
	public int getApprovedByID() {
		return approvedByID;
	}
	public void setApprovedByID(int approvedByID) {
		this.approvedByID = approvedByID;
		try {
			this.approvedDate = DateBean.toDBFormat(DateBean.getTodaysDateTime());
		} catch (Exception e) {
			this.approvedDate = "";
		}
	}
	public String getApprovedStatus() {
		return approvedStatus;
	}
	public void setApprovedStatus(String approvedStatus) {
		if ("".equals(approvedStatus) || "Yes".equals(approvedStatus) || "No".equals(approvedStatus))
			this.approvedStatus = approvedStatus;
	}
	public String getApprovedDate() {
		return approvedDate;
	}
	
	public boolean save() throws Exception {
		if (this.conID == 0) return false;
		if (this.opID == 0) return false;
		if (this.dateAdded == null) this.dateAdded = DateBean.toDBFormat(DateBean.getTodaysDateTime());
		
		String approvedDateSQL = "".equals(this.approvedDate) ? "NULL" : "'"+this.approvedDate+"'";
		String sql = "INSERT INTO generalcontractors " +
				"(subID, genID, dateAdded, approvedByID, approvedStatus, approvedDate) " +
				"VALUES ("+conID+", "+opID+", NOW(), "+this.approvedByID+", '"+this.approvedStatus+"', "+approvedDateSQL+") " +
				"ON DUPLICATE KEY UPDATE approvedByID="+this.approvedByID+", approvedStatus='"+this.approvedStatus+"', approvedDate="+approvedDateSQL;
		try {
			DBReady();
			SQLStatement.executeUpdate(sql);
		} finally {
			DBClose();
		}
		return true;
	}
	
	public boolean delete(String conID, String opID) throws Exception {
		String sql = "DELETE FROM generalcontractors " +
				"WHERE subID = " + Utilities.intToDB(conID) + " AND genID = " + Utilities.intToDB(opID);
		try {
			DBReady();
			SQLStatement.executeUpdate(sql);
		} finally {
			DBClose();
		}
		return true;
	}
	
	public ArrayList<GeneralContractor> getListByContractor(String conID) {
		return new ArrayList<GeneralContractor>();
	}
}
