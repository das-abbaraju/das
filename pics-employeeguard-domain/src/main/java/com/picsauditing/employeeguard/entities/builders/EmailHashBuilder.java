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

	public EmailHashBuilder hash(String hash) {
		emailHash.setHash(hash);
		return this;
	}

	public EmailHashBuilder employee(SoftDeletedEmployee softDeletedEmployee) {
		emailHash.setEmployee(softDeletedEmployee);
		return this;
	}

	public EmailHashBuilder emailAddress(String emailAddress) {
		emailHash.setEmailAddress(emailAddress);
		return this;
	}

	public EmailHashBuilder creationDate(Date creationDate) {
		emailHash.setCreationDate(creationDate);
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
