package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Basic data object containing a single row from the flagcriteria table
 * Contains no CRUD ability
 * 
 * @see FlagCriteria
 * @see "Table flagcriteria"
 * @author Jeff Jensen
 */
public class FlagCriteriaDO{
	String opID = "";
	String questionID = "";
	String flagStatus = "";
	String isChecked = "";
	String questionType = "";
	String comparison = "";
	String value = "";
	
	/**
	 * empty constructor
	 */
	public FlagCriteriaDO() {
	}
	
	public FlagCriteriaDO(String opID, String questionID, String flagStatus, String isFlagged, 
				String questionType, String comparison, String value) {
		this.opID = opID;
		this.questionID = questionID;
		this.flagStatus = flagStatus;
		this.isChecked = isFlagged;
		this.questionType = questionType;
		this.comparison = comparison;
		this.value = value;
	}
	
	public void setFromResultSet(ResultSet rs) throws SQLException {
		opID = rs.getString("opID");
		questionID = rs.getString("questionID");
		flagStatus = rs.getString("flagStatus");
		isChecked = rs.getString("isChecked");
		questionType = rs.getString("questionType");
		comparison = rs.getString("comparison");
		value = rs.getString("value");
	}//setFromResultSet
	
	public boolean isChecked() {
		return "Yes".equals(isChecked);
	}//isChecked
	
	public String getAnswer(ResultSet rs)throws Exception{
		if (FlagCriteria.EMR_AVE_QUESTION_ID.equals(questionID)){
			try{
				float temp = 0;
				questionID = com.picsauditing.PICS.pqf.Constants.EMR_YEAR1;
				temp += Float.parseFloat(getAnswer(rs));
				questionID = com.picsauditing.PICS.pqf.Constants.EMR_YEAR2;
				temp += Float.parseFloat(getAnswer(rs));
				questionID = com.picsauditing.PICS.pqf.Constants.EMR_YEAR3;
				temp += Float.parseFloat(getAnswer(rs));
				questionID = FlagCriteria.EMR_AVE_QUESTION_ID;
				temp = temp/3;
				DecimalFormat decFormatter = new DecimalFormat("###,##0.00");
				return decFormatter.format(temp);
			}catch(Exception ex){
				questionID = FlagCriteria.EMR_AVE_QUESTION_ID;
				return "-";				
			}//catch
		}//if
		String answer = rs.getString("q"+questionID+".verifiedAnswer");
		if (null == answer || "null".equals(answer))
			return "-";
		if ("".equals(answer))
			answer = rs.getString("q"+questionID+".answer");
		if ("".equals(answer))
			return "-";
		return answer;
	}//getAnswer

	public boolean isFlagged(ResultSet rs) throws Exception{
		String answer = getAnswer(rs);
		if ("Check Box".equals(questionType))
			if ("=".equals(comparison))
				return value.equals(answer);
			else
				return !value.equals(answer);
		if ("-".equals(answer))
			return true;
		if ("Yes/No/NA".equals(questionType) || "Yes/No".equals(questionType) || "Manual".equals(questionType))
			if ("=".equals(comparison))
				return value.equals(answer);
			else
				return !value.equals(answer);
		if ("Decimal Number".equals(questionType)){
			return isFlaggedRate(answer,value);
		}//if
		return false;
	}//isFlagged
	
	public boolean isFlaggedRate(String rate,String cutoff){
		float tempRate = 0;
		float tempCutoff = 0;
		try{tempRate = Float.parseFloat(rate);}
		catch (Exception e){return true;}
		try{tempCutoff = Float.parseFloat(cutoff);}
		catch (Exception e){return true;}
		if (">".equals(comparison))
				return (tempRate>tempCutoff);
		if ("<".equals(comparison))
				return (tempRate<tempCutoff);
		return (tempRate==tempCutoff);
	}//isFlaggedRate
	
	public boolean isFlaggedRateNoZeros(String rate,String cutoff){
		float tempRate = 0;
		float tempCutoff = 0;
		try{tempRate = Float.parseFloat(rate);}
		catch (Exception e){;}
		try{tempCutoff = Float.parseFloat(cutoff);}
		catch (Exception e){;}
		return (tempRate>tempCutoff || 0==tempRate);
	}//getRedFlagNoZeros
	
	public boolean isFlaggedAnswerNo(String answer){
		return (!"Yes".equals(answer));
	}//getRedFlagAnswerNo

	public String toString(){
		return "opID="+opID+","+
		"questionID="+questionID+","+
		"flagStatus="+flagStatus+","+
		"isChecked="+isChecked+","+
		"questionType="+questionType+","+
		"comparison="+comparison+","+
		"value="+value;
	}//toString
}//FlagCriteriaDO
