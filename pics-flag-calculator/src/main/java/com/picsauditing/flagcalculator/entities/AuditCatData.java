package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.AuditCatData")
@Table(name = "audit_cat_data")
<<<<<<< HEAD
public class AuditCatData extends BaseTable implements java.io.Serializable/*, Comparable<AuditCatData>*/ {
//
	private ContractorAudit audit;
=======
public class AuditCatData extends BaseTable implements java.io.Serializable {
>>>>>>> 7ae760b... US831 Deprecated old FDC
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

<<<<<<< HEAD
	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

//	/**
//	 * If true, then allow for manually setting the isApplies field
//	 */
//	@Enumerated(EnumType.ORDINAL)
//	@Column(nullable = false)
//	public boolean isOverride() {
//		return override;
//	}
//
//	public void setOverride(boolean override) {
//		this.override = override;
//	}
//
=======
>>>>>>> 7ae760b... US831 Deprecated old FDC
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
