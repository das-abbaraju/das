package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
abstract public class FlagCriteria {
	protected int id;
	protected OperatorAccount operatorAccount;
	protected FlagColor flagColor;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "criteriaID", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="opID", nullable=false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

}
