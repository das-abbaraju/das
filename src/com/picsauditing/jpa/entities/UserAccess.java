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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.access.OpPerms;

@Entity
@Table(name = "useraccess")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class UserAccess {
	private int id;
	private User user;
	private OpPerms opPerm;
	private boolean viewFlag;
	private boolean editFlag;
	private boolean deleteFlag;
	private boolean grantFlag;
	private Date lastUpdate;
	private User grantedBy;

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
	@JoinColumn(name="userID", nullable=false)
	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="accessType")
	public OpPerms getOpPerm() {
		return opPerm;
	}


	public void setOpPerm(OpPerms opPerm) {
		this.opPerm = opPerm;
	}


	public boolean isViewFlag() {
		return viewFlag;
	}


	public void setViewFlag(boolean viewFlag) {
		this.viewFlag = viewFlag;
	}


	public boolean isEditFlag() {
		return editFlag;
	}


	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}


	public boolean isDeleteFlag() {
		return deleteFlag;
	}


	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}


	public boolean isGrantFlag() {
		return grantFlag;
	}


	public void setGrantFlag(boolean grantFlag) {
		this.grantFlag = grantFlag;
	}


	@ManyToOne
	@JoinColumn(name="grantedByID", nullable=false)
	public User getGrantedBy() {
		return grantedBy;
	}


	public void setGrantedBy(User grantedBy) {
		this.grantedBy = grantedBy;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
