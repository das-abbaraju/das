package com.picsauditing.employeeguard.forms.employee;

import com.picsauditing.employeeguard.services.calculator.DocumentStatus;

import java.util.Date;

public class ProfileDocumentInfo implements Comparable<ProfileDocumentInfo> {

	private int id;
	private String name;
	private Date added;
	private Date expires;
	private boolean doesNotExpire;
	private DocumentStatus status;

	public int getId() {
		return id;
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

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public boolean isDoesNotExpire() {
		return doesNotExpire;
	}

	public void setDoesNotExpire(boolean doesNotExpire) {
		this.doesNotExpire = doesNotExpire;
	}

	public DocumentStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentStatus status) {
		this.status = status;
	}

	@Override
	public int compareTo(ProfileDocumentInfo that) {
		return this.name.compareTo(that.name);
	}
}
