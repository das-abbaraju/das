package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.AuditCatData")
@Table(name = "audit_cat_data")
public class AuditCatData extends BaseTable implements java.io.Serializable {
    private ContractorAudit audit;
	private AuditCategory category;
	private boolean applies = true;

	@ManyToOne
	@JoinColumn(name = "categoryID", nullable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	/**
	 * This is dynamically set by AuditBuilder.fillAuditCategories() when one or
	 * more CAOs require this category. If Override==true, then the value must
	 * be manually set
	 */
	@Column(nullable = false)
	public boolean isApplies() {
		return applies;
	}

	public void setApplies(boolean applies) {
		this.applies = applies;
	}
}
