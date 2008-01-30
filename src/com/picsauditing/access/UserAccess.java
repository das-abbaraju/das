package com.picsauditing.access;

import java.sql.ResultSet;
import java.util.*;
import com.picsauditing.PICS.Utilities;

/**
 * @deprecated
 * @see PermissionDB
 */
public class UserAccess extends com.picsauditing.PICS.DataBean {
	public String userID = "0";
	Collection<OpPerms> accessSet = null;
	Collection<OpPerms> oldAccessSet = null;
	
	/**
	 * @deprecated
	 * @see Permissions
	 * @param user_ID
	 * @throws Exception
	 */
	public void setFromDB(String user_ID) throws Exception {
		try{
			this.userID = user_ID;
			String selectQuery = "SELECT accessType FROM useraccess WHERE userID="+
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
	public void writeNewToDB(String grantedByID,String userID) throws Exception{
		try{
			String insertQuery = "INSERT INTO useraccess (userID,accessType,grantedByID) VALUES";
			boolean isInsert = false;
			for (OpPerms perm:OpPerms.values()){
				if (accessSet.contains(perm)){
					insertQuery += "("+userID+",'"+perm+"',"+grantedByID+"),";
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
	public void writeToDB(String grantedByID) throws Exception{
		try{
			String deleteQuery = "DELETE FROM useraccess WHERE userID="+userID+" AND accessType IN (";
			String insertQuery = "INSERT INTO useraccess (userID,accessType,grantedByID) VALUES";
			boolean isDelete = false;
			boolean isInsert = false;
			for (OpPerms perm:OpPerms.values()){
				if (oldAccessSet.contains(perm) && !accessSet.contains(perm)){
					deleteQuery += "'"+perm+"',";
					isDelete = true;
				}//if
				if (accessSet.contains(perm) && !oldAccessSet.contains(perm)){
					insertQuery += "("+userID+",'"+perm+"',"+grantedByID+"),";
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