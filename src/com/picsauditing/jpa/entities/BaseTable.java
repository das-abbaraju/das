package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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
import com.picsauditing.dao.PicsDAO;


@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public abstract class BaseTable implements JSONable, Serializable {
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
		if (permissions == null) {
			setAuditColumns();
			return;
		}
		int userID = permissions.getUserId();
		if (permissions.getAdminID() > 0)
			userID = permissions.getAdminID();
		setAuditColumns(new User(userID));
	}
	
	@Transient
	public String getWhoString() {
		if (createdBy == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
		StringBuilder sb = new StringBuilder();
		sb.append("Created By ").append(createdBy.getName()).append("(").append(createdBy.getId()).append(")");
		if (creationDate != null)
			sb.append(" on ").append(sdf.format(creationDate));
		if (updatedBy != null) {
			if (updatedBy.getId() == createdBy.getId()) {
				if (updateDate != null && !updateDate.equals(creationDate)) {
					sb.setLength(0);
					sb.append("Last Updated by ").append(updatedBy.getName()).append("(").append(updatedBy.getId())
							.append(") on ").append(sdf.format(updateDate));
				}
			} else {
				sb.append("; Updated By ").append(updatedBy.getName()).append("(").append(updatedBy.getId())
						.append(")");
				if (updateDate != null)
					sb.append(" on ").append(sdf.format(updateDate));
			}
		}
		return sb.toString();
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
		if (full) {
			obj.put("createdBy", createdBy == null ? null : createdBy.toJSON());
			obj.put("updatedBy", updatedBy == null ? null : updatedBy.toJSON());
			obj.put("creationDate", creationDate == null ? null : creationDate.getTime());
			obj.put("updateDate", updateDate == null ? null : updateDate.getTime());
		}
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
	
	@Override
	public int hashCode() {
		if( id == 0 )
			return super.hashCode();
		else
			return ( ( getClass().getName().hashCode() % 1000 ) * 10000000 ) + id;
	}

	public void update(BaseTable b) {
		setId(b.getId());
		setUpdateDate(b.getUpdateDate());
		setUpdatedBy(b.getUpdatedBy());
	}

	// UPDATE must be Overridden in the inheriting class
	public static <T extends BaseTable> Collection<T> insertUpdateDeleteManaged(Collection<T> dbLinkedList, Collection<T> changes) {
		// update/delete
		Iterator<T> dbIterator = dbLinkedList.iterator();
		Collection<T> removalList = new ArrayList<T>();
		
		while (dbIterator.hasNext()) {
			T fromDB = dbIterator.next();
			T found = null;

			for (T change : changes) {
				if (fromDB.equals(change)) {
					fromDB.update(change);
					found = change;
				}
			}

			if (found != null)
				changes.remove(found); // update was performed
			else {
				removalList.add(fromDB);
			}
		}
		
		// merging remaining changes (updates/inserts)
		dbLinkedList.addAll(changes);
		
		return removalList;
	}

	// UPDATE must be Overridden in the inheriting class
	// IMPORTANT NOTE: Only use this as a necessity. Performance using this
	// operation is severely degraded
	// compared to the hibernate managed insert/update/delete above
	public static <T extends BaseTable> void insertUpdateDeleteExplicit(Collection<T> unLinkedList,
			Collection<T> changes, PicsDAO dao) {
		// update/delete
		Collection<T> deletes = new ArrayList<T>();
		Iterator<T> dbIterator = unLinkedList.iterator();
		while (dbIterator.hasNext()) {
			T fromDB = dbIterator.next();
			T found = null;

			for (T change : changes) {
				if (fromDB.equals(change)) {
					fromDB.update(change);
					found = change;
				}
			}

			if (found != null)
				changes.remove(found); // update was performed
			else {
				deletes.add(fromDB);
				dbIterator.remove();
			}
		}

		// merging remaining changes (updates/inserts)
		unLinkedList.addAll(changes);
		for (T insertOrUpdate : unLinkedList)
			dao.save(insertOrUpdate);

		// performing deletes
		for (T delete : deletes)
			dao.remove(delete);
	}
}
