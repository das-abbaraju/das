package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "accountemployeeguard")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "insert into accountemployeeguard (accountId,deletedDate) values (?, ?) ON DUPLICATE KEY UPDATE deletedDate = null")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AccountEmployeeGuard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "accountID")
	private int accountId;

  @Temporal(TemporalType.TIMESTAMP)
  private Date deletedDate;


  public AccountEmployeeGuard() {
  }

  public AccountEmployeeGuard(final int accountId) {
    this.accountId = accountId;
  }

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

  public Date getDeletedDate() {
    return deletedDate;
  }

  public void setDeletedDate(Date deletedDate) {
    this.deletedDate = deletedDate;
  }
}
