package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

@Deprecated
/**
 * Use AuditQuestionDAO.findQuestionByType() instead
 */
public class TradesBean extends DataBean {
	public static final String DEFAULT_SELECT_TRADE = "- Trade -";
	public static final String DEFAULT_SELECT_TRADE_ID = "0";
	public ArrayList<String> trades = new ArrayList<String>();
	public Map<Integer, String> tradeMap = new TreeMap<Integer, String>();
	public static final String DEFAULT_PERFORMED_BY = "- Performed By -";
	public static final String[] PERFORMED_BY_ARRAY = {DEFAULT_PERFORMED_BY,"Self Performed","Sub Contracted"};
	
	public void setFromDB() throws Exception {
		if (isSet) 
			return;
		String selectQuery = "SELECT * FROM pqfQuestions WHERE questionType='Service' ORDER BY question ASC;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			trades = new ArrayList<String>();
			while (SQLResult.next()) {
				trades.add(SQLResult.getString("id"));
				trades.add(SQLResult.getString("question"));
				tradeMap.put(SQLResult.getInt("id"), SQLResult.getString("question"));
			}
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
		isSet = true;
	}//setFromDB

	public String getTradeFromID(String qID) throws Exception {
		setFromDB();
		int i = trades.indexOf(qID);
		if (-1 == i)
			return "";
		else
			return (String)trades.get(i+1);
	}//getTradeFromID

	public int getNumTrades() {
		return trades.size()/2;
	}//getNumTrades

	public String getTrade(int i) {
		if (i*2 <= trades.size())
			return (String)trades.get((i*2)-1);
		else
			return "";
	}//getTrade

	public String getTradesSelect(String name, String classType, String selectedTrade) throws Exception {
		setFromDB();		
		return Inputs.inputSelect2First(name, classType, selectedTrade,(String[])trades.toArray(new String[0]),
			"0", DEFAULT_SELECT_TRADE);
	}//getTradesSelect

	public String getTradesNameSelect(String name, String classType, String selectedTrade) throws Exception {
		setFromDB();		
		ArrayList<String> tempAL = new ArrayList<String>();
		for (int i=0;i < trades.size();i+=2)
			tempAL.add((String)trades.get(i+1));
		return Inputs.inputSelectFirst(name, classType, selectedTrade,(String[])tempAL.toArray(new String[0]),
			DEFAULT_SELECT_TRADE);
	}//getTradesNameSelect

	public Map<Integer, String> getTradeList() throws Exception {
		setFromDB();
		Map<Integer, String> list = new TreeMap<Integer, String>();
		list.put(0, DEFAULT_SELECT_TRADE);
		list.putAll(tradeMap);
		return list;
	}
}
