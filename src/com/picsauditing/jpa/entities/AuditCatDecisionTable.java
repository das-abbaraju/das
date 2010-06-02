package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_cat_dt")
public class AuditCatDecisionTable extends BaseDecisionTable {

	private AuditCategory auditCategory;
	private LowMedHigh risk;
	private OperatorAccount operatorAccount;
	private ContractorType contractorType;
	private ContractorTag tag;

	public AuditCategory getAuditCategory() {
		return auditCategory;
	}

	public void setAuditCategory(AuditCategory auditCategory) {
		this.auditCategory = auditCategory;
	}

	public LowMedHigh getRisk() {
		return risk;
	}

	public void setRisk(LowMedHigh risk) {
		this.risk = risk;
	}

	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	public ContractorType getContractorType() {
		return contractorType;
	}

	public void setContractorType(ContractorType contractorType) {
		this.contractorType = contractorType;
	}

	public ContractorTag getTag() {
		return tag;
	}

	public void setTag(ContractorTag tag) {
		this.tag = tag;
	}

}
