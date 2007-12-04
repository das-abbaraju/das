package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.util.*;

public class ForcedFlagList extends com.picsauditing.PICS.DataBean{
	String opID = "";
	Set<String> greenFlagList = null;
	Set<String> amberFlagList = null;
	Set<String> redFlagList = null;
	public ForcedFlagList(String opID)throws Exception{
		this.opID = opID;
		setFromDB(opID);
	}//ForcedFlagListDO

	public void setFromDB(String opID)throws Exception{
		this.opID = opID;
		String selectQuery = "SELECT conID,flagStatus FROM forcedFlagList WHERE opID="+opID+" AND dateExpires>CURDATE();";
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			greenFlagList = new HashSet<String>();
			amberFlagList = new HashSet<String>();
			redFlagList = new HashSet<String>();
			while (rs.next()){
				String conID = rs.getString("conID");
				String forcedFlagStatus = rs.getString("flagStatus");
				if ("Green".equals(forcedFlagStatus))
					greenFlagList.add(conID);
				else if ("Amber".equals(forcedFlagStatus))
					amberFlagList.add(conID);
				else if ("Red".equals(forcedFlagStatus))
					redFlagList.add(conID);
			}//if
			rs.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	boolean isForcedGreenFlag(String conID){
		return greenFlagList.contains(conID);
	}//isForcedGreenFlag
	boolean isForcedAmberFlag(String conID){
		return amberFlagList.contains(conID);
	}//isForcedAmberFlag
	boolean isForcedRedFlag(String conID){
		return redFlagList.contains(conID);
	}//isForcedRedFlag
}//ForcedFlagListDO
