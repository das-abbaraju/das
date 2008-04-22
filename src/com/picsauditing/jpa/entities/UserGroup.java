package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "usergroup")
public class UserGroup {
	protected int userGroupID;
	protected User user;
	protected User group;
	protected Date creationDate;
	protected int createdBy;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(int userGroupID) {
		this.userGroupID = userGroupID;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="userID", nullable=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="groupID", nullable=false)
	public User getGroup() {
		return group;
	}

	public void setGroup(User group) {
		this.group = group;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

}
