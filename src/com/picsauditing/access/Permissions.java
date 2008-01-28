package com.picsauditing.access;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Permissions{
	
	private int userID;
	private boolean loggedIn = false;
	private List<Integer> groups = new ArrayList();
	private Set<Permission> permissions = new HashSet<Permission>();	
	private String username;
	private int accountID;
	private String accountType;
	private int adminID;
	
	public Permissions(){
	}
	
	public void login(User user) throws Exception {
		try{
			userID = Integer.parseInt(user.userDO.id);
			if (userID == 0) throw new Exception("Missing User");
			loggedIn = true;
			username = user.userDO.username;
			accountID = Integer.parseInt(user.userDO.accountID);
			accountType = user.userDO.accountType;
			permissions = user.getPermissions();
			List<User> temp = user.getGroups();
			for(User u : temp)
				groups.add(Integer.parseInt(u.userDO.id));
		}catch(Exception ex){
			// All or nothing, if something went wrong, then clear it all
			clear();
			throw ex;
		}
	}
	
	public void clear() {
		userID = 0;
		loggedIn = false;
		username = "";
		accountID = 0;
		permissions.clear();
		groups.clear();
	}
	
	public int getUserId() {
		return userID;
	}
	public String getUserIdString() {
		return Integer.toString(userID);
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
	public int getAccountId() {
		return accountID;
	}
	public String getAccountIdString() {
		return Integer.toString(accountID);
	}
	public String getAccountType() {
		return accountType;
	}
	public int getAdminID() {
		return adminID;
	}
	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}

	/**
	 * Does this user have 'oType' access to 'opPerm'
	 * @param opPerm OSHA, ContractorDetails, UserAdmin, etc
	 * @param oType View, Edit, Delete, or Grant
	 * @return
	 */
	public boolean hasPermission(OpPerms opPerm, OpType oType) {
		for(Permission perm : permissions) {
			if (opPerm == perm.getAccessType()) {
				if(oType == OpType.Edit)
					return perm.isEditFlag();			
				else if(oType ==OpType.Delete)
					return perm.isDeleteFlag();
				else if(oType == OpType.Grant)
					return perm.isGrantFlag();
				// Default to OpType.View
				return perm.isViewFlag();
			}
		}
		return false;
	}
	public boolean hasPermission(OpPerms opPerm) {
		return this.hasPermission(opPerm, OpType.View);
	}
	
	public void tryPermission(OpPerms opPerm, OpType oType) throws NoRightsException {
		if (this.hasPermission(opPerm, oType)) return;
		throw new NoRightsException(opPerm, oType);
	}
	
	public void tryPermission(OpPerms opPerm) throws NoRightsException {
		this.tryPermission(opPerm, OpType.View);
	}
	
	public boolean hasGroup(Integer group){
		for(Integer i : groups)
			if(i == group)
				return true;
		
		return false;
	}
}
