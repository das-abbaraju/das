package com.picsauditing.access.permissions.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "usergroup")
public class UserGroup extends BaseTable implements java.io.Serializable {
//	protected User user;
	protected User group;

//	@ManyToOne(optional=false)
//	@JoinColumn(name="userID", nullable=false)
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
	@ManyToOne(optional=false)
	@JoinColumn(name="groupID", nullable=false)
	public User getGroup() {
		return group;
	}

	public void setGroup(User group) {
		this.group = group;
	}

//	@Override
//	public int hashCode() {
//		final int PRIME = 31;
//		int result = 1;
//		result = PRIME * result + id;
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		final UserGroup other = (UserGroup) obj;
//		if (id != other.id)
//			return false;
//		return true;
//	}
}
