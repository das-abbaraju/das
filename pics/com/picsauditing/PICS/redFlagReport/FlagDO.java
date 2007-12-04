package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;

import com.picsauditing.PICS.*;

public class FlagDO extends DataBean{
	String opID = "";
	String conID = "";
	String flag = "";
	String lastUpdate = "";

	public FlagDO(){
	}//Note

	public FlagDO(String opID, String conID, String flag, String lastUpdate){
		this.opID = opID;
		this.conID = conID;
		this.flag = flag;
		this.lastUpdate = lastUpdate;
	}//Note

	public String getFlagStatus(String conID, String opID) throws Exception{
		if ((null == opID) || ("".equals(opID)))
			throw new Exception("Can't set Flag from DB because opID is not set");
		if ((null == conID) || ("".equals(conID)))
			throw new Exception("Can't set Flag from DB because conID is not set");
		String selectQuery = "SELECT * FROM flags WHERE conID="+Utilities.intToDB(conID)+" AND opID="+Utilities.intToDB(opID)+" ;";
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			if (rs.next())
				setFromResultSet(rs);
			rs.close();
			return flag;
		}finally{
			DBClose();
		}//finally
	}//getFlagStatus
	
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

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		opID = SQLResult.getString("opID");
		conID= SQLResult.getString("conID");
		flag= SQLResult.getString("flag");
		lastUpdate = SQLResult.getString("lastUpdate");
	}//setFromResultSet
}//Note
