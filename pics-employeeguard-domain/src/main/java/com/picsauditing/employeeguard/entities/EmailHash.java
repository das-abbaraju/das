package com.picsauditing.employeeguard.entities;


import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.util.Strings;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "email_hash")
public class EmailHash implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String hash;

	@ManyToOne
	@JoinColumn(name = "account_employeeID")
	private SoftDeletedEmployee employee;

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

	public SoftDeletedEmployee getEmployee() {
		return employee;
	}

	public void setEmployee(SoftDeletedEmployee employee) {
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
		return getId() + "-"
				+ (getEmailAddress() != null ? getEmailAddress() : Strings.EMPTY_STRING) + "-"
				+ (getCreationDate() != null ? Strings.EMPTY_STRING : getCreationDate().toString());
	}
}
