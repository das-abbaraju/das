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
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.access.Permissions;

@Entity
@MappedSuperclass
public abstract class BaseTable implements JSONable {
	protected int id;
	protected User createdBy;
	protected User updatedBy;
	protected Date creationDate;
	protected Date updateDate;

	public BaseTable() {
	}

	public BaseTable(User user) {
		setAuditColumns(user);
	}

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

	public void setAuditColumns(Permissions permissions) {
		int userID = permissions.getUserId();
		if (permissions.getAdminID() > 0)
			userID = permissions.getAdminID();
		setAuditColumns(new User(userID));
	}

	@Transient
	public JSONObject toJSON() {
		return toJSON(false);
	}

	@Transient
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		if (!full)
			return obj;

		obj.put("id", id);
		if (createdBy != null)
			obj.put("createdBy", createdBy.toJSON(false));
		if (updatedBy != null)
			obj.put("updatedBy", updatedBy.toJSON(false));
		if (creationDate != null)
			obj.put("creationDate", creationDate);
		if (updateDate != null)
			obj.put("updateDate", updateDate);

		return obj;
	}

	public void fromJSON(JSONObject obj) {
		// TODO write this!!

		// if (id == 0)
		// id = (Integer)obj.get("id");
		// createdBy = new User(obj.get("createdBy"));
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
