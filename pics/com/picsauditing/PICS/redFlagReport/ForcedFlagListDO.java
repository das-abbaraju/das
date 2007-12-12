package com.picsauditing.PICS.redFlagReport;

import java.util.Vector;
import com.picsauditing.PICS.*;

/**
 * Basic data object containing a single row from the `forcedflaglist` table.
 * Contains REPLACE and DELETE sql functionality
 * 
 * @author Jeff Jensen
 * @see "Table forcedflaglist"
 */
public class ForcedFlagListDO extends com.picsauditing.PICS.DataBean{
	String opID = "";
	String conID = "";
	String flagStatus = "";
	String dateAdded = "";
	String dateExpires = "";

	public ForcedFlagListDO(){
	}

	public ForcedFlagListDO(String opID, String conID, String flagStatus, String dateExpires){
		this.opID = opID;
		this.conID = conID;
		this.flagStatus = flagStatus;
		this.dateExpires = dateExpires;
	}

	public void writeToDB()throws Exception{
		try{
			DBReady();
			String insertQuery = "REPLACE INTO forcedFlagList (opID,conID,flagStatus,dateExpires) VALUES("+
				opID+","+conID+",'"+flagStatus+"','"+DateBean.toDBFormat(dateExpires)+"');";
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}
	}
	
	public void deleteFromDB(String opID, String conID, String flagStatus)throws Exception{
		try{
			DBReady();
			String deleteQuery = "DELETE FROM forcedFlagList WHERE opID="+opID+" AND conID="+conID+
					" AND flagStatus='"+flagStatus+"' LIMIT 1";
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}
	}
	
	public boolean isOK() {
		errorMessages = new Vector<String>();
		try{
			if (!DateBean.isAfterToday(dateExpires))
				errorMessages.add("Invalid Expiration Date - Must be a date after today");
		} catch(Exception ex) {
			errorMessages.add("Invalid Expiration Date - Must be of the form 1/20/08");
		}
		return (errorMessages.size() == 0);
	}
}//ForcedFlagListDO