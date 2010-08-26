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
			level++;
			priority += 120;
		}
	}

	public void merge(AuditCategoryRule source) {
		super.merge(source);
		if (auditCategory == null)
			auditCategory = source.auditCategory;
	}
	
	public void update(AuditCategoryRule source) {
		super.update(source);
		auditCategory = source.auditCategory;
	}

}
