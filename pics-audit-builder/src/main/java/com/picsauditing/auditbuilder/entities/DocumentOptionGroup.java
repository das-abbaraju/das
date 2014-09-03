package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "com.picsauditing.auditbuilder.entities.AuditOptionGroup")
@Table(name = "audit_option_group")
@SuppressWarnings("serial")
public class DocumentOptionGroup extends BaseTable {

	private List<DocumentOptionValue> values = new ArrayList<>();

	@OneToMany(mappedBy = "group", cascade = { CascadeType.REMOVE })
	public List<DocumentOptionValue> getValues() {
		return values;
	}

	public void setValues(List<DocumentOptionValue> values) {
		this.values = values;
	}
}