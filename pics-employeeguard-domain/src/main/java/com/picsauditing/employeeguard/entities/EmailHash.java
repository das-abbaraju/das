package com.picsauditing.employeeguard.entities;


import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.util.Strings;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "employee_email_hash")
public class EmailHash implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String hashCode;

	@ManyToOne
	@JoinColumn(name = "employeeID")
	private SoftDeletedEmployee employee;

	@Column(name = "email")
	private String emailAddress;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date expirationDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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
				+ (getCreatedDate() != null ? Strings.EMPTY_STRING : getCreatedDate().toString());
	}
}
