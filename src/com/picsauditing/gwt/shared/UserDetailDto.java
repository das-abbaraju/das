package com.picsauditing.gwt.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserDetailDto implements IsSerializable {

	private Date dateCreated = new Date();
	private String password = "abc";
	private Date lastLogin = new Date();

	private String email = "jblow@ss.com";
	private String username = "jblow";
	private String phone = "333 333 3333";
	private String fax = "333 333 3333";

	private List<UserDto> groups = new ArrayList<UserDto>();
	private List<UserDto> members = new ArrayList<UserDto>();
	private List<UserDto> switchTos = new ArrayList<UserDto>();

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		System.out.println("setPhone: [" + phone + "]");
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public List<UserDto> getGroups() {
		return groups;
	}

	public void setGroups(List<UserDto> groups) {
		this.groups = groups;
	}

	public List<UserDto> getMembers() {
		return members;
	}

	public void setMembers(List<UserDto> members) {
		this.members = members;
	}

	public List<UserDto> getSwitchTos() {
		return switchTos;
	}

	public void setSwitchTos(List<UserDto> switchTos) {
		this.switchTos = switchTos;
	}

}
