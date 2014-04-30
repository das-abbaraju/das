package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;

import java.util.Date;

public class EmailHashBuilder {

	private EmailHash emailHash;

	public EmailHashBuilder() {
		this.emailHash = new EmailHash();
	}

	public EmailHashBuilder id(int id) {
		emailHash.setId(id);
		return this;
	}

	public EmailHashBuilder hashCode(String hashCode) {
		emailHash.setHashCode(hashCode);
		return this;
	}

	public EmailHashBuilder softDeletedEmployee(SoftDeletedEmployee softDeletedEmployee) {
		emailHash.setEmployee(softDeletedEmployee);
		return this;
	}

	public EmailHashBuilder createdDate(Date createdDate) {
		emailHash.setCreatedDate(createdDate);
		return this;
	}

	public EmailHashBuilder expirationDate(Date expirationDate) {
		emailHash.setExpirationDate(expirationDate);
		return this;
	}

	public EmailHash build() {
		return emailHash;
	}

}
