package com.picsauditing.access;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;

/**
 * This is the main class that is stored for each user containing information 
 * if they are logged in, which groups they're in, and what permission(s) they have
 * 
 * Warning: this class is stored in the session
 * Make sure you keep the footprint very small
 */
public class Permissions {
	
	private int userID;
	private boolean loggedIn = false;
	private Set<Integer> groups = new HashSet<Integer>();
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
			Set<User> temp = user.getGroups();
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
	public Set<Integer> getGroups() {
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
	
	public boolean loginRequired(javax.servlet.http.HttpServletResponse response, String returnURL) throws IOException {
		if (this.loggedIn) return true;
		if (returnURL != null && returnURL.length() > 0) {
			Cookie fromCookie = new Cookie("from",returnURL);
			fromCookie.setMaxAge(3600);
			response.addCookie(fromCookie);
		}
		response.sendRedirect("logout.jsp?msg=Your session has timed out. Please log back in");
		return false;
	}
	public boolean loginRequired(javax.servlet.http.HttpServletResponse response) throws IOException {
		// TODO get the current URL
		return this.loginRequired(response, "");
	}
	
	public boolean hasGroup(Integer group){
		for(Integer i : groups)
			if(i == group)
				return true;
		
		return false;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}
	
}
