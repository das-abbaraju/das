package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;

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
 
	public void DBReady() throws SQLException{
		try{
			if (null == Conn){
				Conn = DBBean.getDBConnection();
			}
			SQLStatement = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		}catch (SQLException ex){
			DBClose();
			throw ex;
		}
	}

	public void statement2Ready() throws SQLException{
		try {
			SQLStatement = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException ex) {
			DBClose();
			throw ex;
		}
	}

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
	
	/**
	 * Get the columnName from the given ResultSet as a string
	 * If the data is null, then it returns a empty string ("") instead
	 * @param rs
	 * @param columnName
	 * @return
	 */
	protected String getString(ResultSet rs, String columnName) {
		String value = "";
		try {
			value = rs.getString(columnName);
			if (rs.wasNull()) value = "";
		} catch (SQLException ex) {
			System.out.println("Failed to find value for "+columnName+" : "+ex.getMessage());
			return ex.getMessage();
		}
		return value;
	}

	public boolean isSet() {
		return this.isSet;
	}
	
	public List<BasicDynaBean> executeQuery(SQLBuilder sql) throws SQLException {
		return executeQuery(sql.toString());
	}
	public List<BasicDynaBean> executeQuery(String sql) throws SQLException {
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(sql);
		    RowSetDynaClass rsdc = new RowSetDynaClass(rs, false);
		    rs.close();
		    return rsdc.getRows();
		}finally{
			DBClose();
		}
	}
	
	public Vector<String> getErrors(){
		return errorMessages;
	}
}