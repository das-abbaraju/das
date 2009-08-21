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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.gwt.shared.LoginLogDTO;
import com.picsauditing.gwt.shared.UserDto;

@Entity
@Table(name = "loginlog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class UserLoginLog {
	private int id = 0;
	private String username = "";
	private String password = "";
	private int userID;
	private char successful;
	private Date loginDate;
	private String remoteAddress;
	private User admin;
	private String sessionId;

	public UserLoginLog() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
	@Column(name = "id")
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	@Column(name = "date", nullable = true)
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
	
	@Column( name= "sessionID" )
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Transient
	public LoginLogDTO toDTO() {
		LoginLogDTO l = new LoginLogDTO();
		l.setLoginDate(loginDate);
		if(admin != null) {
			l.setAdminName(admin.getUsername());
			l.setAdminAccountName(admin.getAccount().getName());
		}
		l.setRemoteAddress(remoteAddress);
		l.setSuccessful(successful);
		return l;
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
