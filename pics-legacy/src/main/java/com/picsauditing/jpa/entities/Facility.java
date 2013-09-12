package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "facilities")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Facility extends BaseTable {

	private OperatorAccount operator;
	private OperatorAccount corporate;
	private String type;

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne
	@JoinColumn(name = "corporateID", nullable = false, updatable = false)
	public OperatorAccount getCorporate() {
		return corporate;
	}

	public void setCorporate(OperatorAccount corporate) {
		this.corporate = corporate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
