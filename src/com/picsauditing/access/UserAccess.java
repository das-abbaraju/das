package com.picsauditing.access;

import java.sql.ResultSet;
import java.util.*;
import com.picsauditing.PICS.Utilities;

/**
 * This class saves data into either the useraccess or the opaccess tables
 * TODO figure out how to support operator/account permissions too
 * @deprecated
 * @see PermissionDB
 */
public class UserAccess extends com.picsauditing.PICS.DataBean {
	public String userID = "0";
	public String db = "userAccess";
	Collection<OpPerms> accessSet = null;
	Collection<OpPerms> oldAccessSet = null;

	public void setDB(String newDB){
		this.db = newDB;
	}//setDB
	
	/**
	 * @deprecated
	 * @see Permissions
	 * @param user_ID
	 * @throws Exception
	 */
	public void setFromDB(String user_ID) throws Exception {
		try{
			this.userID = user_ID;
			String selectQuery = "SELECT accessType FROM "+db+" WHERE userID="+
				Utilities.intToDB(userID)+";";
			accessSet = new HashSet<OpPerms>();
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			while (rs.next()){
				OpPerms perm = OpPerms.valueOf(rs.getString("accessType"));
				accessSet.add(perm);
			}//while
			oldAccessSet = new HashSet<OpPerms>(accessSet);
			rs.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	/**
	 * @deprecated
	 * 
	 * @param request
	 * @throws Exception
	 */
	public void setFromRequest(javax.servlet.http.HttpServletRequest request) throws Exception {
		accessSet = new HashSet<OpPerms>();
		for (OpPerms perm:OpPerms.values()){
			if ("checked".equals(request.getParameter("perm_"+perm)))
				accessSet.add(perm);
		}//for
	}//setFromRequest

	/**
	 * @deprecated
	 * 
	 * @param grantedByID
	 * @param userID
	 * @throws Exception
	 */
	public void writeNewToDB(Permissions permissions,String userID) throws Exception{
		try{
			String insertQuery = "INSERT INTO "+db+" (userID,accessType,grantedByID) VALUES";
			boolean isInsert = false;
			for (OpPerms perm:OpPerms.values()){
				if (accessSet.contains(perm)){
					insertQuery += "("+userID+",'"+perm+"',"+permissions.getUserId()+"),";
					isInsert = true;
				}//if
			}//for
			if (isInsert){
				DBReady();
				SQLStatement.executeUpdate(insertQuery.substring(0,insertQuery.length()-1));
			}//if
		}finally{
			DBClose();
		}//finally
	}//writeNewToDB

	/**
	 * @deprecated
	 * 
	 * @param grantedByID
	 * @throws Exception
	 */
	public void writeToDB(Permissions permissions) throws Exception{
		try{
			String deleteQuery = "DELETE FROM "+db+" WHERE userID="+userID+" AND accessType IN (";
			String insertQuery = "INSERT INTO "+db+" (userID,accessType,grantedByID) VALUES";
			boolean isDelete = false;
			boolean isInsert = false;
			for (OpPerms perm:OpPerms.values()){
				if (oldAccessSet.contains(perm) && !accessSet.contains(perm)){
					deleteQuery += "'"+perm+"',";
					isDelete = true;
				}//if
				if (accessSet.contains(perm) && !oldAccessSet.contains(perm)){
					insertQuery += "("+userID+",'"+perm+"',"+permissions.getUserId()+"),";
					isInsert = true;
				}//if
			}//for
			if (isDelete || isInsert){
				DBReady();
				SQLStatement.getConnection().setAutoCommit(false);
				if (isDelete)
					SQLStatement.executeUpdate(deleteQuery.substring(0,deleteQuery.length()-1)+")");
				if (isInsert)
					SQLStatement.executeUpdate(insertQuery.substring(0,insertQuery.length()-1));
				SQLStatement.getConnection().commit();
				SQLStatement.getConnection().setAutoCommit(true);
			}//if
		}finally{
			DBClose();
		}//finally
	}//writeToDB
	
	/**
	 * @deprecated
	 * 
	 * @param testAccess
	 * @return
	 */
	public boolean hasAccess(OpPerms testAccess) {
		//return this.permissions.hasPermission(testAccess);
		if (null == accessSet)
			return false;
		return (accessSet.contains(testAccess));
	}//hasAccess

	/**
	 * @deprecated
	 */
	public String getChecked(OpPerms perm){
		if (hasAccess(perm))
			return "checked";
		return "";
	}//getChecked
}//Access