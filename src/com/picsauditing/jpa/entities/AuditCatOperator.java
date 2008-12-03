package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfopmatrix")
/**
 * A list of Audit Categories that an operator want on their Audits
 */
public class AuditCatOperator implements java.io.Serializable {

	protected int id;
	protected AuditCategory category;
	protected OperatorAccount operatorAccount;
	protected LowMedHigh riskLevel;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "catID", nullable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Enumerated(EnumType.ORDINAL)
	public LowMedHigh getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(LowMedHigh riskLevel) {
		this.riskLevel = riskLevel;
	}

	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AuditCatOperator other = (AuditCatOperator) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	
}
