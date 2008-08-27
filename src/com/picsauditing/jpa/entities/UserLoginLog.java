package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "loginlog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class UserLoginLog {
	private int id = 0;
	private String username = "";
	private String password = "";
	private YesNo successful;
	private Date date;
	private User adminId;

	public UserLoginLog() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	public YesNo getSuccessful() {
		return successful;
	}
	
	@Transient
	public boolean isSuccessful() {
		return YesNo.Yes == successful;
	}

	public void setIsSuccessful(YesNo successful) {
		this.successful = successful;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public User getAdminId() {  // Is this the way to grab this?
		return adminId;
	}

	public void setAdminId(User adminId) {
		this.adminId = adminId;
	}

}
