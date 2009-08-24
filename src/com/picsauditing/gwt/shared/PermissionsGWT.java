package com.picsauditing.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the main class that is stored for each user containing information if
 * they are logged in, which groups they're in, and what permission(s) they have
 * 
 * Warning: this class is stored in the session Make sure you keep the footprint
 * very small
 */
public class PermissionsGWT implements IsSerializable {

	private int userID;
	private boolean loggedIn = false;
	private String username;
	private String name;
	private int accountID;
	private String accountName;
	private String accountType;
	private String email;
	private int adminID;
	private boolean active = false;
	private boolean accountActive = false;
	
	public PermissionsGWT(int userID, boolean loggedIn, String username,
			String name, int accountID, String accountName, String accountType,
			String email, int adminID, boolean active, boolean accountActive) {
		super();
		this.userID = userID;
		this.loggedIn = loggedIn;
		this.username = username;
		this.name = name;
		this.accountID = accountID;
		this.accountName = accountName;
		this.accountType = accountType;
		this.email = email;
		this.adminID = adminID;
		this.active = active;
		this.accountActive = accountActive;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAdminID() {
		return adminID;
	}

	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isAccountActive() {
		return accountActive;
	}

	public void setAccountActive(boolean accountActive) {
		this.accountActive = accountActive;
	}

	
}
