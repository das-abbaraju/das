package com.picsauditing.flagcalculator.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "com.picsauditing.flagcalculator.entities.AuditOptionGroup")
@Table(name = "audit_option_group")
@SuppressWarnings("serial")
public class AuditOptionGroup extends BaseTable {
	private String uniqueCode;

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
}