package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "accountemployeeguard")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AccountEmployeeGuard {

	public AccountEmployeeGuard() {
	}

	public AccountEmployeeGuard(final int accountId) {
		this.accountId = accountId;
	}

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
