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
	protected Boolean rootCategory;

	@ManyToOne
	@JoinColumn(name = "catID")
	public AuditCategory getAuditCategory() {
		return auditCategory;
	}

	public void setAuditCategory(AuditCategory category) {
		this.auditCategory = category;
	}

	public Boolean getRootCategory() {
		return rootCategory;
	}

	public void setRootCategory(Boolean rootCategory) {
		this.rootCategory = rootCategory;
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

	public void merge(AuditRule source) {
		super.merge(source);
		if (auditCategory == null)
			auditCategory = ((AuditCategoryRule) source).auditCategory;
	}

	public void update(AuditRule source) {
		super.update(source);
		auditCategory = ((AuditCategoryRule) source).auditCategory;
	}

	@Override
	public String toString() {
		String out = super.toString();
		if (auditCategory != null)
			out += " and Category = " + auditCategory;
		return out;
	}
}
