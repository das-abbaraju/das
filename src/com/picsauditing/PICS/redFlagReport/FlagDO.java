package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.picsauditing.PICS.*;

/**
 * CRUD for a single row in the `flag` table
 * 
 * @author Jeff Jensen
 */
public class FlagDO extends DataBean{
	private String opID = "";
	private String conID = "";
	private String flag = "";
	private String lastUpdate = "";

	public FlagDO(){
	}

	public FlagDO(String opID, String conID, String flag, String lastUpdate){
		this.opID = opID;
		this.conID = conID;
		this.flag = flag;
		this.lastUpdate = lastUpdate;
	}

	/**
	 * 
	 * @param conID
	 * @param opID
	 * @return the current flag color for this conID and opID
	 */
	public String getFlagStatus(String conID, String opID) throws Exception, IllegalArgumentException {
		if ((null == opID) || ("".equals(opID)))
			throw new IllegalArgumentException("Can't set Flag from DB because opID is not set");
		if ((null == conID) || ("".equals(conID)))
			throw new IllegalArgumentException("Can't set Flag from DB because conID is not set");
		
		String selectQuery = "SELECT * FROM flags WHERE conID="+Utilities.intToDB(conID)+" AND opID="+Utilities.intToDB(opID);
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			if (rs.next())
				setFromResultSet(rs);
			rs.close();
			return flag;
		}finally{
			DBClose();
		}
	}//getFlagStatus
	
	/**
	 * 
	 * @param conID
	 * @param opID
	 * @return the current flag color for this conID and opID
	 */
	public HashMap<String, FlagDO> getFlagByContractor(String conID) throws Exception {
		if ((null == conID) || ("".equals(conID)))
			throw new IllegalArgumentException("Can't set Flag from DB because conID is not set");
		
		String selectQuery = "SELECT * FROM flags WHERE conID="+Utilities.intToDB(conID);
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			HashMap<String, FlagDO> flags = new HashMap<String, FlagDO>();
			while (rs.next()) {
				FlagDO flag = new FlagDO();
				flag.setFromResultSet(rs);
				flags.put(flag.opID, flag);
			}
			rs.close();
			return flags;
		}finally{
			DBClose();
		}
	}

	public void setFromResultSet(ResultSet SQLResult) throws SQLException {
		opID = SQLResult.getString("opID");
		conID= SQLResult.getString("conID");
		flag= SQLResult.getString("flag");
		lastUpdate = SQLResult.getString("lastUpdate");
	}
	
	public void writeToDB()throws Exception{
		try{
			DBReady();
			String insertQuery = "INSERT INTO flags (opID,conID,flag,lastUpdate) VALUES("+
				opID+","+conID+",'"+flag+"',NOW());";
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public String getOpID() {
		return opID;
	}

	public void setOpID(String opID) {
		this.opID = opID;
	}

	public String getConID() {
		return conID;
	}

	public void setConID(String conID) {
		this.conID = conID;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
}//Note
