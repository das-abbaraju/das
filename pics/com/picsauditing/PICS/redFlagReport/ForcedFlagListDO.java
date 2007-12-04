package com.picsauditing.PICS.redFlagReport;

import java.util.Vector;
import com.picsauditing.PICS.*;

public class ForcedFlagListDO extends com.picsauditing.PICS.DataBean{
	String opID = "";
	String conID = "";
	String flagStatus = "";
	String dateAdded = "";
	String dateExpires = "";

	public ForcedFlagListDO(){
	}//ForcedFlagListDO

	public ForcedFlagListDO(String opID, String conID, String flagStatus, String dateExpires){
		this.opID = opID;
		this.conID = conID;
		this.flagStatus = flagStatus;
		this.dateExpires = dateExpires;
	}//ForcedFlagListDO Constructor

	public void writeToDB()throws Exception{
		try{
			DBReady();
			String insertQuery = "REPLACE INTO forcedFlagList (opID,conID,flagStatus,dateExpires) VALUES("+
				opID+","+conID+",'"+flagStatus+"','"+DateBean.toDBFormat(dateExpires)+"');";
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB
	
	public void deleteFromDB(String opID, String conID, String flagStatus)throws Exception{
		try{
			DBReady();
			String deleteQuery = "DELETE FROM forcedFlagList WHERE opID="+opID+" AND conID="+conID+
					" AND flagStatus='"+flagStatus+"' LIMIT 1";
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
	}//deleteListEntry
	public boolean isOK()throws Exception{
		errorMessages = new Vector<String>();
		try{
			if (!DateBean.isAfterToday(dateExpires))
				errorMessages.add("Invalid Expiration Date - Must be a date after today");
		}catch(Exception ex){
			errorMessages.add("Invalid Expiration Date - Must be of the form 1/20/08");
		}//catch
		return (errorMessages.size() == 0);
	}//isOK
}//ForcedFlagListDO