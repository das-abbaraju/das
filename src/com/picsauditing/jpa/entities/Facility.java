package com.picsauditing.jpa.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "facilities")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="daily")
public class Facility extends BaseTable implements Serializable {
	private static final long serialVersionUID = -4675060756773282099L;
	
	private OperatorAccount operator;
	private OperatorAccount corporate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "corporateID", nullable = false, updatable = false)
	public OperatorAccount getCorporate() {
		return corporate;
	}

	public void setCorporate(OperatorAccount corporate) {
		this.corporate = corporate;
	}
	
}
