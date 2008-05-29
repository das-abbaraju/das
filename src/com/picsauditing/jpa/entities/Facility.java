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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "facilities")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="daily")
public class Facility {

	private int id;
	private OperatorAccount operator;
	private OperatorAccount corporate;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "facilityID", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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
