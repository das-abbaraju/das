package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "generalcontractors")
public class ContractorOperator implements java.io.Serializable {

	private int id;
	private Account operatorAccount;
	private Account contractorAccount;
	private Date dateAdded;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "genID", nullable = false, updatable = false)
	public Account getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(Account operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@ManyToOne
	@JoinColumn(name = "subID", nullable = false, updatable = false)
	public Account getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(Account contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateAdded", nullable = false, length = 10)
	public Date getDateAdded() {
		return this.dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

}
