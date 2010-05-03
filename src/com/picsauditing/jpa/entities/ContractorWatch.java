package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_watch")
public class ContractorWatch extends BaseTable {

	private ContractorAccount contractor;
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "conID", nullable = false, updatable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
