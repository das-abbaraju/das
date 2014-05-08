package com.picsauditing.employeeguard.services.email;

import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;

/**
 * Wrapper class to provide same properties to the email template that existed with previous
 * emailHash.
 */
public class EmailHashWrapper {

	private String hash;
	private SoftDeletedEmployee employee;

	public EmailHashWrapper(final EmailHash emailHash) {
		this.hash = emailHash.getHashCode();
		this.employee = emailHash.getEmployee();
	}

	public String getHash() {
		return hash;
	}

	public SoftDeletedEmployee getEmployee() {
		return employee;
	}
}
