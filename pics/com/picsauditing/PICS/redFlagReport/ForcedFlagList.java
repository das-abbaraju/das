package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.util.*;

/**
 * Gets a list of forced flags for a given operator and organizes them by flag color 
 * @author Jeff Jensen
 *
 */
public class ForcedFlagList extends com.picsauditing.PICS.DataBean{
	private String opID = "";
	private Set<String> greenFlagList = null;
	private Set<String> amberFlagList = null;
	private Set<String> redFlagList = null;
	private HashMap<String, String> contractorFlags = null;
	
	public ForcedFlagList(String opID)throws Exception {
		this.opID = opID;
		setFromDB(opID);
	}

	public void setFromDB(String id)throws Exception {
		this.opID = id;
		String selectQuery = "SELECT conID, flagStatus FROM forcedFlagList " + 
				"WHERE opID="+opID+" AND dateExpires > CURDATE() ORDER BY conID, flagStatus";
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			greenFlagList = new HashSet<String>();
			amberFlagList = new HashSet<String>();
			redFlagList = new HashSet<String>();
			contractorFlags = new HashMap<String, String>();
			while (rs.next()) {
				String conID = rs.getString("conID");
				String forcedFlagStatus = rs.getString("flagStatus");
				
				contractorFlags.put(conID, forcedFlagStatus);
				if ("Green".equals(forcedFlagStatus))
					greenFlagList.add(conID);
				else if ("Amber".equals(forcedFlagStatus))
					amberFlagList.add(conID);
				else if ("Red".equals(forcedFlagStatus))
					redFlagList.add(conID);
			}
			rs.close();
		}finally{
			DBClose();
		}
	}//setFromDB

	String getContractorFlag(String conID){
		return this.contractorFlags.get(conID);
	}//isForcedGreenFlag
	boolean isForcedGreenFlag(String conID){
		return this.greenFlagList.contains(conID);
	}//isForcedGreenFlag
	boolean isForcedAmberFlag(String conID){
		return this.amberFlagList.contains(conID);
	}//isForcedAmberFlag
	boolean isForcedRedFlag(String conID){
		return this.redFlagList.contains(conID);
	}//isForcedRedFlag
}//ForcedFlagListDO
