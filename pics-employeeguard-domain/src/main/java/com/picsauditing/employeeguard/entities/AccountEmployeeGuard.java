package com.picsauditing.employeeguard.entities;

import javax.persistence.*;

@Entity
@Table(name = "accountemployeeguard")
public class AccountEmployeeGuard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "accountID")
	private int accountId;

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
}
