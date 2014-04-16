package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "accountemployeeguard")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO accountemployeeguard (accountID, deletedDate) values (?, ?) ON DUPLICATE KEY UPDATE deletedDate = null")
@SQLDelete(sql = "UPDATE accountemployeeguard SET deletedDate = NOW() WHERE id = ?")
public class AccountEmployeeGuard {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Column(name = "accountID", nullable = false, unique = true)
	private int accountId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deletedDate", nullable = true)
	private Date deletedDate;

	public AccountEmployeeGuard() {
	}

	public AccountEmployeeGuard(int accountId) {
		this.accountId = accountId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
