package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.util.*;
import com.picsauditing.PICS.*;
import com.picsauditing.PICS.pqf.*;

/**
 * Business engine to calculate operator flag color for contractors
 * 
 * @author Jeff Jensen
 */
public class FlagCalculator extends com.picsauditing.PICS.DataBean {
	public OSHABean osBean = new OSHABean();
	public FlagOshaCriteriaDO redFlagOshaCriteriaDO = null;
	public FlagOshaCriteriaDO amberFlagOshaCriteriaDO = null;

	public String conID = "";

	public String flagStatus = "Green";
	public boolean isGreenFlagListed = false;
	public boolean isAmberFlagListed = false;
	public boolean isRedFlagListed = false;
	public String dateExpires = "";

	public Map<String,String> qIDToFlagMap = null;
	public Map<String,String> qIDToAnswerMap = null;
	private int currentYearGrace  = 0;
	private boolean duringGracePeriod = false;

	/**
	 * Depending on the Red or Amber Flag criteria, construct and return a SQL string 
	 * that can query contractor data from the PQF and OSHA tables
	 * @param flagCriteria
	 * @return
	 */
	private String getSelectQuery(FlagCriteria flagCriteria){
		StringBuffer fromQuery = new StringBuffer();
		StringBuffer joinQuery = new StringBuffer();
		for (String qID: flagCriteria.flagCriteriaMap.keySet()) {
			// Include all of the criteria questions except the "average EMR" question
			if (!FlagCriteria.EMR_AVE_QUESTION_ID.equals(qID)){
				joinQuery.append("LEFT JOIN pqfData q").append(qID).append(" ON (q").append(qID).
					append(".conID=cons.id AND q").append(qID).append(".questionID=").append(qID).append(") ");
				fromQuery.append(",q").append(qID).append(".*");
			}//if
		}//for
		if (flagCriteria.flagCriteriaMap.keySet().contains(FlagCriteria.EMR_AVE_QUESTION_ID)){
			// The operator uses the Average EMR question
			// If any of the EMR years aren't already included, then include them here
			if (!flagCriteria.flagCriteriaMap.keySet().contains(Constants.EMR_YEAR1)){
				joinQuery.append("LEFT JOIN pqfData q").append(Constants.EMR_YEAR1).append(" ON (q").
					append(Constants.EMR_YEAR1).append(".conID=cons.id AND q").append(Constants.EMR_YEAR1).
					append(".questionID=").append(Constants.EMR_YEAR1).append(") ");
				fromQuery.append(",q").append(Constants.EMR_YEAR1).append(".*");
			}//if
			if (!flagCriteria.flagCriteriaMap.keySet().contains(Constants.EMR_YEAR2)){
				joinQuery.append("LEFT JOIN pqfData q").append(Constants.EMR_YEAR2).append(" ON (q").
					append(Constants.EMR_YEAR2).append(".conID=cons.id AND q").append(Constants.EMR_YEAR2).
					append(".questionID=").append(Constants.EMR_YEAR2).append(") ");
				fromQuery.append(",q").append(Constants.EMR_YEAR2).append(".*");
			}//if
			if (!flagCriteria.flagCriteriaMap.keySet().contains(Constants.EMR_YEAR3)){
				joinQuery.append("LEFT JOIN pqfData q").append(Constants.EMR_YEAR3).append(" ON (q").
					append(Constants.EMR_YEAR3).append(".conID=cons.id AND q").append(Constants.EMR_YEAR3).
					append(".questionID=").append(Constants.EMR_YEAR3).append(") ");
				fromQuery.append(",q").append(Constants.EMR_YEAR3).append(".*");
			}//if
		}//if
		String returnString = "SELECT cons.id"+fromQuery.toString()+", OSHA.* "+
 			"FROM contractor_info cons LEFT JOIN OSHA ON OSHA.conID=cons.id AND location='Corporate' "+joinQuery.toString();
		return returnString;
	}//getSelectQuery

	/**
	 * Recalculates the flag for a single contractor and operator
	 * @param opID
	 * @throws Exception
	 */
	public void setConFlags(String cID, String opID) throws Exception{
		try{
			ResultSet rs = null;
			DBReady();

			// Get this operator's Osha Critera for red flags
			redFlagOshaCriteriaDO = new FlagOshaCriteriaDO();
			redFlagOshaCriteriaDO.setFromDB(opID,"Red");
			
			// Get this operator's Osha Critera for amber flags
			amberFlagOshaCriteriaDO = new FlagOshaCriteriaDO();
			amberFlagOshaCriteriaDO.setFromDB(opID,"Amber");
			
			qIDToFlagMap = new TreeMap<String,String>();
			qIDToAnswerMap = new TreeMap<String,String>();
			
			// We need to calculate both red and amber flags
			ArrayList<String> tempAL = new ArrayList<String>();
			tempAL.add("Amber");
			tempAL.add("Red");
			for (String thisFlag: tempAL) {
				// Get the Amber or Red Osha criteria for this Operator
				//FlagOshaCriteriaDO flagOshaCriteriaDO = new FlagOshaCriteriaDO();
				//flagOshaCriteriaDO.setFromDB(opID, thisFlag);
				
				// Get the Amber or Red criteria for this Operator
				FlagCriteria flagCriteria = new FlagCriteria();
				flagCriteria.setFromDB(opID, thisFlag);
				
				// Run a query against pqfData and OSHA data that considers the flagCriteria
				String sql = getSelectQuery(flagCriteria);
				rs = SQLStatement.executeQuery(sql+" WHERE id="+cID);
				boolean flagged = false;
				if (rs.next()){
					for (String questionID: flagCriteria.getCheckedQuestionIDsAL()) {
						FlagCriteriaDO flagCriteriaDO = flagCriteria.getFlagCriteriaDO(questionID);
						qIDToAnswerMap.put(questionID,flagCriteriaDO.getAnswer(rs));
						if (flagCriteriaDO.isFlagged(rs)){
							qIDToFlagMap.put(questionID,thisFlag);
							if (!flagged)
								flagged = true;
						}
					}
				}
				osBean.setFromResultSet(rs);
				if (getIsOshaFlagged(flagged,flagCriteria.flagOshaCriteriaDO))
					flagStatus = thisFlag;
				rs.close();
			}//for
			
			String selectQuery = "SELECT flagStatus, dateExpires FROM forcedFlagList " + 
				"WHERE opID="+opID+" AND conID="+cID+" AND dateExpires > CURDATE()";
			rs = SQLStatement.executeQuery(selectQuery);
			if (rs.next()){
				// An override exists, so set flagStatus and set the boolean flags 
				// so we can show this info on the webpage too
				this.flagStatus = rs.getString("flagStatus");
				dateExpires = DateBean.toShowFormat(rs.getString("dateExpires"));
				if ("Red".equals(flagStatus))
					isRedFlagListed = true;
				else if ("Green".equals(flagStatus))
					isGreenFlagListed = true;
				else if ("Amber".equals(flagStatus))
					isAmberFlagListed = true;
				else
					throw new Exception ("Invalid Flag Status: "+flagStatus);
			}//if
			rs.close();
			
			// Determine if we need to change the existing contractor flag or not
			selectQuery = "SELECT flag FROM flags WHERE opID="+opID+" AND conID="+cID+";";
			rs = SQLStatement.executeQuery(selectQuery);
			String oldFlagStatus = "";
			if (rs.next())
				oldFlagStatus = rs.getString("flag");
			rs.close();
			if (!flagStatus.equals(oldFlagStatus)){
				SQLStatement.executeUpdate("DELETE FROM flags WHERE opID="+opID+" AND conID="+cID+";");
				String insertQuery = "INSERT INTO flags (opID, conID, flag) VALUES ("+opID+","+cID+",'"+flagStatus+"')";
				SQLStatement.executeUpdate(insertQuery);
			}
		}finally{
			DBClose();
		}
	}//setConFlags

	/**
	 * Recalculates all flags for a given operator's contractors
	 * @param opID
	 * @throws Exception
	 */
	public void recalculateFlags(String opID) throws Exception{
		try{
			System.out.println("calculating flags for operator: "+opID);
			
			ArrayList<String> tempAL = new ArrayList<String>();
			tempAL.add("Amber");
			tempAL.add("Red");
			TreeMap<String,String> tempInsertFlagMap = new TreeMap<String,String>();
			DBReady();
			for (String thisFlag: tempAL) {
				// Get the Amber or Red Osha criteria for this Operator
				//FlagOshaCriteriaDO flagOshaCriteriaDO = new FlagOshaCriteriaDO();
				//flagOshaCriteriaDO.setFromDB(opID, thisFlag);
				
				// Get the Amber or Red criteria for this Operator
				FlagCriteria flagCriteria = new FlagCriteria();
				flagCriteria.setFromDB(opID, thisFlag);
				
				// Run a query against pqfData and OSHA data that considers the flagCriteria
				String sql = getSelectQuery(flagCriteria);
				// We actually calculate flags for ALL contractors regardless if they are on the operator list or not
				// For testing purposes
				//sql = sql + " AND cons.id IN (SELECT id FROM accounts WHERE name like 'A%')";
				ResultSet rs = SQLStatement.executeQuery(sql);
				while (rs.next()){
					String conID = rs.getString("cons.id");
					boolean flagged = false;
					
					// Calculate this contractor's flag color
					for (String questionID: flagCriteria.getCheckedQuestionIDsAL()) {
						FlagCriteriaDO flagCriteriaDO = flagCriteria.getFlagCriteriaDO(questionID);
						if (!flagged)
							flagged = flagCriteriaDO.isFlagged(rs);
					}//for
					osBean.setFromResultSet(rs);
					flagged = getIsOshaFlagged(flagged,flagCriteria.flagOshaCriteriaDO);
					
					if (flagged)
						tempInsertFlagMap.put(conID, thisFlag);
					
					//else if(!"Red".equals(thisFlag))
					if (!tempInsertFlagMap.containsKey(conID))
						tempInsertFlagMap.put(conID,"Green");
				}//while
				rs.close();
			}//for

			// Get the list of contractors that this operator has forced flags (Red, Amber, or Green)
			ForcedFlagList forcedFlagList = new ForcedFlagList(opID);
			
			// Delete ALL flags for this operator and Insert new ones for each contractor
			StringBuffer insertQuery = new StringBuffer("INSERT INTO flags (opID,conID,flag) VALUES ");
			for (Iterator i = tempInsertFlagMap.keySet().iterator();i.hasNext();){
				String conID = (String)i.next();
				String flag = tempInsertFlagMap.get(conID);
				
				// Does this contractor have a flag override?
				String forcedFlag = forcedFlagList.getContractorFlag(conID);
				if (!(forcedFlag == null)) {
					flag = forcedFlag;
				}
				
				insertQuery.append("("+opID+","+conID+",'"+flag+"'), ");
			}
			SQLStatement.executeUpdate("DELETE FROM flags WHERE opID="+opID);
			if (tempInsertFlagMap.size() > 0) {
				String sqlString = insertQuery.toString();
				sqlString = sqlString.substring(0,sqlString.length()-2);
				SQLStatement.executeUpdate(sqlString);
			}
		}finally{
			DBClose();
		}//finally
	}//recalculateFlags

	/**
	 * 
	 * @param flagged
	 * @param flagOshaCriteriaDO
	 * @return
	 */
	boolean getIsOshaFlagged(boolean flagged,FlagOshaCriteriaDO flagOshaCriteriaDO){
		if (!flagged && flagOshaCriteriaDO.flagLwcr()){
			if (flagOshaCriteriaDO.isLwcrTimeAverage()){
				if (isFlaggedRate(osBean.calcAverageRate(OSHABean.LOST_WORK_CASES),flagOshaCriteriaDO.lwcrHurdle))
					flagged = true;
			}else{
				if ((!osBean.isNa1() &&  isFlaggedRate(osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR1), flagOshaCriteriaDO.lwcrHurdle)) ||
					(!osBean.isNa2() && isFlaggedRate(osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR2), flagOshaCriteriaDO.lwcrHurdle)) ||
					(!osBean.isNa3() &&	isFlaggedRate(osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR3), flagOshaCriteriaDO.lwcrHurdle)))
					flagged = true;
			}//else
		}//if
		if (!flagged && flagOshaCriteriaDO.flagTrir()){
			if (flagOshaCriteriaDO.isTrirTimeAverage()){
				if (isFlaggedRate(osBean.calcAverageRate(OSHABean.RECORDABLE_TOTAL),flagOshaCriteriaDO.trirHurdle))
					flagged = true;
			}else{
				if ((!osBean.isNa1() && isFlaggedRate(osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR1), flagOshaCriteriaDO.trirHurdle)) ||
					(!osBean.isNa2() && isFlaggedRate(osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR2), flagOshaCriteriaDO.trirHurdle)) ||
					(!osBean.isNa3() && isFlaggedRate(osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR3), flagOshaCriteriaDO.trirHurdle)))
					flagged = true;
			}//else
		}//if
		if (!flagged && flagOshaCriteriaDO.flagFatalities()){
			if (flagOshaCriteriaDO.isFatalitiesTimeAverage()){
				if (isFlaggedRate(osBean.calcAverageStat(OSHABean.FATALITIES),flagOshaCriteriaDO.fatalitiesHurdle))
					flagged = true;
			}else{
				if ((!osBean.isNa1() && isFlaggedRate(osBean.getStat(OSHABean.FATALITIES,OSHABean.YEAR1), flagOshaCriteriaDO.fatalitiesHurdle)) ||
					(!osBean.isNa2() && isFlaggedRate(osBean.getStat(OSHABean.FATALITIES,OSHABean.YEAR2), flagOshaCriteriaDO.fatalitiesHurdle)) ||
					(!osBean.isNa2() && isFlaggedRate(osBean.getStat(OSHABean.FATALITIES,OSHABean.YEAR3), flagOshaCriteriaDO.fatalitiesHurdle)))
					flagged = true;
			}//else
		}//if
		return flagged;
	}//getIsOshaFlagged
	
	private void setFromResultSet(ResultSet rs) throws Exception {
		conID = rs.getString("id");
		osBean.setFromResultSet(rs);
	}//setFromResultSet

	public boolean isFlaggedRate(String rate,String cutoff){
		float tempRate = 0;
		float tempCutoff = 0;
		try{tempRate = Float.parseFloat(rate);}
		catch (Exception e){return true;}
		try{tempCutoff = Float.parseFloat(cutoff);}
		catch (Exception e){return true;}
		return (tempRate>tempCutoff);
	}//getRedFlag

	public boolean isFlaggedRateNoZeros(String rate,String cutoff){
		float tempRate = 0;
		float tempCutoff = 0;
		try{tempRate = Float.parseFloat(rate);}
		catch (Exception e){return true;}
		try{tempCutoff = Float.parseFloat(cutoff);}
		catch (Exception e){return true;}
		return (tempRate>tempCutoff || 0==tempRate);
	}//getRedFlagNoZeros

	public boolean isFlaggedAnswerNo(String answer){
		return (!"Yes".equals(answer));
	}//getRedFlagAnswerNo

	public String getFlagIcon(){
		return "<img src=images/icon_"+flagStatus.toLowerCase()+"FlagBig.gif width=32 height=32>";
	}//getFlagIcon
	public String getAnswer(String qID){
		return qIDToAnswerMap.get(qID);
	}//getAnswer
	public String getFlagIcon(String qID){
		if (!qIDToFlagMap.containsKey(qID))
			return "";
		return "<img src=images/icon_"+qIDToFlagMap.get(qID).toLowerCase()+"Flag.gif>";
	}//getFlagIcon

	public String getOshaFlag(int oshaStat,int rate,String timeFrame){
		String tempRate = Integer.toString(rate);
		return getOshaFlag(oshaStat,tempRate,timeFrame);
	}//getOshaFlag

	public String getOshaFlag(int oshaStat,String rate,String timeFrame){
		switch(oshaStat){
			case OSHABean.LOST_WORK_CASES:
				if (redFlagOshaCriteriaDO.lwcrTime.equals(timeFrame) && 
						redFlagOshaCriteriaDO.flagLwcr() && 
						isFlaggedRate(rate,redFlagOshaCriteriaDO.lwcrHurdle))
					return rate+"</td><td><img src=images/icon_redFlag.gif>";
				if (amberFlagOshaCriteriaDO.lwcrTime.equals(timeFrame) && 
						amberFlagOshaCriteriaDO.flagLwcr() && 
						isFlaggedRate(rate,amberFlagOshaCriteriaDO.lwcrHurdle))
					return rate+"</td><td><img src=images/icon_amberFlag.gif>";
				break;
			case OSHABean.RECORDABLE_TOTAL:
				if (redFlagOshaCriteriaDO.trirTime.equals(timeFrame) && 
						redFlagOshaCriteriaDO.flagTrir() && 
						isFlaggedRate(rate,redFlagOshaCriteriaDO.trirHurdle))
					return rate+"</td><td><img src=images/icon_redFlag.gif>";
				if (amberFlagOshaCriteriaDO.trirTime.equals(timeFrame) && 
						amberFlagOshaCriteriaDO.flagTrir() && 
						isFlaggedRate(rate,amberFlagOshaCriteriaDO.trirHurdle))
					return rate+"</td><td><img src=images/icon_amberFlag.gif>";
				break;
			case OSHABean.FATALITIES:
				if (redFlagOshaCriteriaDO.flagFatalities() && isFlaggedRate(rate,redFlagOshaCriteriaDO.fatalitiesHurdle))
					return rate+"</td><td><img src=images/icon_redFlag.gif>";
				if (amberFlagOshaCriteriaDO.flagFatalities() && isFlaggedRate(rate,amberFlagOshaCriteriaDO.fatalitiesHurdle))
					return rate+"</td><td><img src=images/icon_amberFlag.gif>";
				break;
		}//switch
		return rate+"</td><td>";
	}//getOshaFlag

	public int getCurrentYearGrace() {
		return currentYearGrace;
	}

	public void setCurrentYear(int currentYear, int currentYearGrace) {
		this.currentYearGrace = currentYearGrace;
		if (this.osBean != null) {
			// if the current year doesn't equal the grace year, then 
			this.osBean.setDuringGracePeriod(currentYear != currentYearGrace);
		}
	}	

}//FlagCalculator
