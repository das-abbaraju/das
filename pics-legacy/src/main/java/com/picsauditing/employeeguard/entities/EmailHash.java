package com.picsauditing.employeeguard.entities;


import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="email_hash")
public class EmailHash {
	@Id
	private int id;
	private String hash;
	@ManyToOne
	@JoinColumn(name = "account_employeeID")
	@WhereJoinTable(clause = "deletedBy = 0 OR deletedBy > 0")
	private Employee employee;
	private String emailAddress;
	private Date creationDate;
	private Date expirationDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		return id + "-" + employee.getId() + "-" + emailAddress + "-" + creationDate.toString();
	}
}
