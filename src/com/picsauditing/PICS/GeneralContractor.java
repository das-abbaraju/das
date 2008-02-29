package com.picsauditing.PICS;

import java.util.ArrayList;

public class GeneralContractor extends DataBean {
	private int conID;
	private int opID;
	private String dateAdded;
	private String workStatus = "P";
	
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
	public String getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(String workStatus) {
		if ("P".equals(workStatus) || "Y".equals(workStatus) || "N".equals(workStatus))
			this.workStatus = workStatus;
	}
	
	public boolean save() throws Exception {
		if (this.conID == 0) return false;
		if (this.opID == 0) return false;
		if (this.dateAdded == null) this.dateAdded = DateBean.toDBFormat(DateBean.getTodaysDateTime());
		
		String sql = "INSERT INTO generalcontractors " +
				"(subID, genID, dateAdded, workStatus) " +
				"VALUES ("+conID+", "+opID+", NOW(), '"+this.workStatus+"') " +
				"ON DUPLICATE KEY UPDATE workStatus='"+this.workStatus+"'";
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
