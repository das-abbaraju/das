package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "contractor_audit_operator_permission")
@SuppressWarnings("serial")
public class ContractorAuditOperatorPermission {
	
	private int id;
	private ContractorAuditOperator contractorAuditOperator;
	private OperatorAccount operator;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "caoID")
	public ContractorAuditOperator getContractorAuditOperator() {
		return contractorAuditOperator;
	}
	public void setContractorAuditOperator(
			ContractorAuditOperator contractorAuditOperator) {
		this.contractorAuditOperator = contractorAuditOperator;
	}

	@ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getOperator() {
		return operator;
	}
	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}
}
