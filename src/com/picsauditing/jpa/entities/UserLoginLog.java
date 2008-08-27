package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "loginlog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class UserLoginLog {
	private int id = 0;
	private String username = "";
	private String password = "";
	private char successful;
	private Date loginDate;
	private String remoteAddress;
	private User admin;

	public UserLoginLog() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "logID", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "username", nullable = false, length = 50)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", length = 50)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "date", nullable = true)
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date date) {
		this.loginDate = date;
	}
	
	@ManyToOne( targetEntity=User.class)
	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

	@Column(name = "remoteAddress", length = 100)
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public void setSuccessful(char successful) {
		this.successful = successful;
	}

	@Column(name = "successful", nullable = true)
	public char getSuccessful() {
		return successful;
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
		final UserLoginLog other = (UserLoginLog) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
