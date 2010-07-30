package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.access.OpPerms;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "useraccess")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class UserAccess implements java.io.Serializable, Comparable<UserAccess> {
	private int id;
	private User user;
	private OpPerms opPerm;
	private Boolean viewFlag;
	private Boolean editFlag;
	private Boolean deleteFlag;
	private Boolean grantFlag;
	private Date lastUpdate;
	private User grantedBy;

	public UserAccess() {
	}

	public UserAccess(UserAccess other) {
		setUser(other.getUser());
		setOpPerm(other.getOpPerm());
		setViewFlag(other.getViewFlag());
		setEditFlag(other.getEditFlag());
		setDeleteFlag(other.getDeleteFlag());
		setGrantFlag(other.getGrantFlag());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "accessID")
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

	@Enumerated(EnumType.STRING)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.access.OpPerms") })
	@Column(name = "accessType", nullable = false)
	public OpPerms getOpPerm() {
		return opPerm;
	}

	public void setOpPerm(OpPerms opPerm) {
		this.opPerm = opPerm;
	}

	public Boolean getViewFlag() {
		if (opPerm != null && !opPerm.usesView())
			return null;
		return viewFlag;
	}

	public void setViewFlag(Boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public Boolean getEditFlag() {
		if (opPerm != null && !opPerm.usesEdit())
			return null;
		return editFlag;
	}

	public void setEditFlag(Boolean editFlag) {
		this.editFlag = editFlag;
	}

	public Boolean getDeleteFlag() {
		if (opPerm != null && !opPerm.usesDelete())
			return null;
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Boolean getGrantFlag() {
		return grantFlag;
	}

	public void setGrantFlag(Boolean grantFlag) {
		this.grantFlag = grantFlag;
	}

	@ManyToOne
	@JoinColumn(name = "grantedByID", nullable = true)
	public User getGrantedBy() {
		return grantedBy;
	}

	public void setGrantedBy(User grantedBy) {
		this.grantedBy = grantedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public int compareTo(UserAccess o) {
		if (opPerm == null)
			return 1;
		if (o.getOpPerm() == null)
			return -1;
		return opPerm.getDescription().compareToIgnoreCase(o.getOpPerm().getDescription());
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (Boolean.TRUE.equals(getViewFlag()))
			s.append("V");
		if (Boolean.TRUE.equals(getEditFlag())) {
			if (s.length() > 0)
				s.append(" ");
			s.append("E");
		}
		if (Boolean.TRUE.equals(getDeleteFlag())) {
			if (s.length() > 0)
				s.append(" ");
			s.append("D");
		}
		if (Boolean.TRUE.equals(getGrantFlag())) {
			if (s.length() > 0)
				s.append(" ");
			s.append("G");
		}
		return s.toString();
	}
}
