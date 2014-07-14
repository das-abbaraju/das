package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorOperator extends BaseTable implements java.io.Serializable {
	private OperatorAccount operatorAccount;

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operator) {
		this.operatorAccount = operator;
	}
}