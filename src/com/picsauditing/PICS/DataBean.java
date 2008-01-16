package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

public abstract class DataBean{
	protected Vector<String> errorMessages = new Vector<String>();
	protected boolean isSet = false;
	protected Connection Conn = null;
	protected Statement SQLStatement = null;
	protected Statement SQLStatement2 = null;

	public String getErrorMessages(){
		StringBuffer temp = new StringBuffer();
		for (int i=0; i < errorMessages.size(); i++)
			temp.append(errorMessages.elementAt(i)).append("<br>");
		return temp.toString();
	}//getErrorMessages
 
	public void DBReady() throws Exception{
		try{
			if (null == Conn){
				Conn = DBBean.getDBConnection();
				SQLStatement = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			}//if
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch		
	}//DBReady

	public void statement2Ready() throws Exception{
		try {
			SQLStatement = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		} catch (Exception ex) {
			DBClose();
			throw ex;
		}//catch		
	}//statement2Ready

	public void DBClose() throws SQLException{
		if (null != SQLStatement){
			SQLStatement.close();
			SQLStatement = null;
		}//if
		if (null != SQLStatement2){
			SQLStatement2.close();
			SQLStatement2 = null;
		}//if
		if (null != Conn){
			Conn.close();
			Conn = null;
		}//if
	}//DBClose

	public String eqDB(String temp) {
		return Utilities.escapeQuotes(temp);
	}//eqDB

	public Vector<String> getErrors(){
		return errorMessages;
	}
//	abstract void setFromDB(String s) throws Exception;
//	abstract void writeToDB(String s) throws Exception;
//	abstract void setFromDB() throws Exception;
//	abstract void writeToDB() throws Exception;
//	abstract void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception;
//	abstract boolean isOK();
}//DataBean