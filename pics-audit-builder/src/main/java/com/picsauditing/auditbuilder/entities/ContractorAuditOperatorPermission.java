package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit_operator_permission")
public class ContractorAuditOperatorPermission implements Serializable {

	private ContractorAuditOperator cao;
	private OperatorAccount operator;
	private ContractorAuditOperator previousCao;

	@ManyToOne
	@JoinColumn(name = "caoID")
	public ContractorAuditOperator getCao() {
		return cao;
	}

	public void setCao(ContractorAuditOperator cao) {
		this.cao = cao;
	}

	@ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "previousCaoID")
	public ContractorAuditOperator getPreviousCao() {
		return previousCao;
	}

	public void setPreviousCao(ContractorAuditOperator previousCao) {
		this.previousCao = previousCao;
	}
}