package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@MappedSuperclass
public abstract class BaseTable {
	protected int id;
	protected User createdBy;
	protected User updatedBy;
	protected Date creationDate;
	protected Date updateDate;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdBy", nullable = true)
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updatedBy", nullable = true)
	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setAuditColumns() {
		updateDate = new Date();
		
		if (createdBy == null)
			createdBy = updatedBy;
		if (creationDate == null)
			creationDate = updateDate;
	}
	public void setAuditColumns(User user) {
		if (user != null)
			updatedBy = user;
		
		setAuditColumns();
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (id == 0)
			return false;
		
		try {
			BaseTable other = (BaseTable) obj;
			if (other.getId() == 0)
				return false;
			
			return id == other.getId();
		} catch (Exception e) {
			System.out.println("Error comparing BaseTable objects: " + e.getMessage());
			return false;
		}
	}
}
