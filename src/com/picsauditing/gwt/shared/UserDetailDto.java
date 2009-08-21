package com.picsauditing.gwt.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.picsauditing.gwt.client.ModelChangeListener;
import com.picsauditing.gwt.client.ModelChangeListenerList;

public class UserDetailDto implements IsSerializable {

	private Date dateCreated;
	private String password;
	private Date lastLogin;

	private String email;
	private String username;
	private String phone;
	private String fax;

	private List<UserDto> groups = new ArrayList<UserDto>();
	private List<UserDto> members = new ArrayList<UserDto>();
	private List<UserDto> switchTos = new ArrayList<UserDto>();

	private ModelChangeListenerList<UserDetailDto> listeners = new ModelChangeListenerList<UserDetailDto>(this);

	public void addModelChangeListener(ModelChangeListener<UserDetailDto> listener) {
		listeners.add(listener);
	}

	public void removeModelChangeListener(ModelChangeListener<UserDetailDto> listener) {
		listeners.remove(listener);
	}

	private void fireModelChangeEvent() {
		listeners.fireChangeEvent();
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		fireModelChangeEvent();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		fireModelChangeEvent();
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
		fireModelChangeEvent();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
		fireModelChangeEvent();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		fireModelChangeEvent();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
		fireModelChangeEvent();
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
		fireModelChangeEvent();
	}

	public List<UserDto> getGroups() {
		return groups;
	}

	public void setGroups(List<UserDto> groups) {
		this.groups = new ArrayList<UserDto>(groups);
		fireModelChangeEvent();
	}

	public List<UserDto> getMembers() {
		return Collections.unmodifiableList(members);
	}

	public void setMembers(List<UserDto> members) {
		this.members = members;
		fireModelChangeEvent();
	}

	public List<UserDto> getSwitchTos() {
		return switchTos;
	}

	public void setSwitchTos(List<UserDto> switchTos) {
		this.switchTos = switchTos;
		fireModelChangeEvent();
	}

	public void addSwitchTo(UserDto switchTo) {
		if (!switchTos.contains(switchTo)) {
			this.switchTos.add(switchTo);
			fireModelChangeEvent();
		}
	}

	public void removeSwitchTo(UserDto switchTo) {
		if (switchTos.contains(switchTo)) {
			this.switchTos.remove(switchTo);
			fireModelChangeEvent();
		}
	}
}
