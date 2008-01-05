package com.picsauditing.access;

import java.util.ArrayList;
import java.util.List;


public class Permissions {
	public enum PERMISSION {ADMINISTRATOR, AUDITOR, CONTRACTOR};
	
	private int userid;
	private boolean loggedIn = false;
	private List<Integer> groups = new ArrayList();
	private List<Integer> active_permissions = new ArrayList();
	private String username;
	private int accountID;
	
	public Permissions(UserDO userDO){
		userid = Integer.parseInt(userDO.id);
		loggedIn = true;
		username = userDO.username;
		accountID = Integer.parseInt(userDO.accountID);		
		
	}
	
	public int getUserid() {
		return userid;
	}
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public List<Integer> getGroups() {
		return groups;
	}
	public String getUsername() {
		return username;
	}
	public int getAccountID() {
		return accountID;
	}
	
	public boolean hasPermission(Integer permission){
		for(Integer i : active_permissions)
			if(i == permission)
				return true;
		
		return false;
	}
	
}
