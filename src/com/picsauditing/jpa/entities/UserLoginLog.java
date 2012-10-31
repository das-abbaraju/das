package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "loginlog")
public class UserLoginLog {
	private int id;
	private User user;
	private boolean successful;
	private Date loginDate;
	private String remoteAddress;
	private String serverAddress;
	private User admin;
	private String browser;
	private String fullUserAgent;
	private String targetIP;

	public UserLoginLog() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable = false)
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date date) {
		this.loginDate = date;
	}

	@ManyToOne
	@JoinColumn(name = "adminID")
	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

	@Column(name = "remoteAddress", length = 15)
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	@Column(name = "remoteAddress", length = 100)
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
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

	@Column(name = "browser", length = 50)
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	@Column(name = "fullUserAgent")
	public String getUserAgent() {
		return fullUserAgent;
	}

	public void setUserAgent(String userAgent) {
		this.fullUserAgent = userAgent;
	}

	public String getTargetIP() {
		return targetIP;
	}

	public void setTargetIP(String targetIP) {
		this.targetIP = targetIP;
	}
}
