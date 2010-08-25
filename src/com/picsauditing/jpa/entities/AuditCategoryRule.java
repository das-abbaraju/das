package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_category_rule")
public class AuditCategoryRule extends AuditRule {

	private AuditCategory auditCategory;

	@ManyToOne
	@JoinColumn(name = "catID")
	public AuditCategory getAuditCategory() {
		return auditCategory;
	}

	public void setAuditCategory(AuditCategory category) {
		this.auditCategory = category;
	}

	@Transient
	public String getAuditCategoryLabel() {
		if (auditCategory == null)
			return "*";
		return auditCategory.getName();
	}

	@Override
	public void calculatePriority() {
		super.calculatePriority();
		if (auditCategory != null) {
			if (auditType == null)
				priority += 120;
			else
				// AuditType priority is already 105
				priority += 15;
		}
	}

	public void merge(AuditCategoryRule source) {
		super.merge(source);
		if (auditCategory == null)
			auditCategory = source.auditCategory;
	}

}
