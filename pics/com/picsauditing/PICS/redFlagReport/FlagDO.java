package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.picsauditing.PICS.*;

/**
 * CRUD for a single row in the `flag` table
 * 
 * @author Jeff Jensen
 */
public class FlagDO extends DataBean{
	String opID = "";
	String conID = "";
	String flag = "";
	String lastUpdate = "";

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

	public void setFromResultSet(ResultSet SQLResult) throws SQLException {
		opID = SQLResult.getString("opID");
		conID= SQLResult.getString("conID");
		flag= SQLResult.getString("flag");
		lastUpdate = SQLResult.getString("lastUpdate");
	}//setFromResultSet
	
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
}//Note
