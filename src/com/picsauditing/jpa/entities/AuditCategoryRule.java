package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_category_rule")
public class AuditCategoryRule extends BaseDecisionTreeRule {

	private AuditCategory auditCategory;
	private LowMedHigh risk;
	private OperatorAccount operatorAccount;
	private ContractorType contractorType;
	private ContractorTag tag;

	@ManyToOne
	@JoinColumn(name = "catID", nullable = false)
	public AuditCategory getAuditCategory() {
		return auditCategory;
	}

	public void setAuditCategory(AuditCategory auditCategory) {
		this.auditCategory = auditCategory;
	}

	@Column(name = "industry", length = 50)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.LowMedHigh") })
	@Enumerated(EnumType.STRING)
	public LowMedHigh getRisk() {
		return risk;
	}

	public void setRisk(LowMedHigh risk) {
		this.risk = risk;
	}

	@ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "accountType")
	public ContractorType getContractorType() {
		return contractorType;
	}

	public void setContractorType(ContractorType contractorType) {
		this.contractorType = contractorType;
	}

	public ContractorTag getTag() {
		return tag;
	}

	@ManyToOne
	@JoinColumn(name = "tagID")
	public void setTag(ContractorTag tag) {
		this.tag = tag;
	}

	@Override
	public void calculatePriority() {
		this.priority = 100;
	}

}
