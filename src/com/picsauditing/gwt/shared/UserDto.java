package com.picsauditing.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Trevor
 * 
 */
public class UserDto implements IsSerializable {

	private int id;
	private String name;
	private boolean group;
	private boolean active;
	private int accountID;
	private String accountName;
	private UserDetailDto userDetail;

	public int getId() {
		return id;
	}

	public UserDetailDto getUserDetail() {
		if (userDetail == null)
			userDetail = new UserDetailDto();
		return userDetail;
	}

	public void setUserDetail(UserDetailDto userDetail) {
		this.userDetail = userDetail;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	@Override
	public String toString() {
		return name;
	}
}
