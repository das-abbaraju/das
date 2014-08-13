package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "com.picsauditing.auditbuilder.entities.AuditOptionGroup")
@Table(name = "audit_option_group")
@SuppressWarnings("serial")
public class AuditOptionGroup extends BaseTable {

	private List<AuditOptionValue> values = new ArrayList<>();

	@OneToMany(mappedBy = "group", cascade = { CascadeType.REMOVE })
	public List<AuditOptionValue> getValues() {
		return values;
	}

	public void setValues(List<AuditOptionValue> values) {
		this.values = values;
	}
}