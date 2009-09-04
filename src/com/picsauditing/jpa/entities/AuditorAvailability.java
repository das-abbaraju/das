package com.picsauditing.jpa.entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "auditor_availability")
public class AuditorAvailability extends BaseTable {
	private User user;
	private Date startDate;
	private int duration;
	private String restrictions;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Number of minutes the auditor is available during this time slot
	 * 
	 * @return
	 */
	@Column(nullable = false)
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * Serialized version of an AvailabilityRestrictions object
	 * 
	 * @return
	 */
	public String getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(String restrictions) {
		this.restrictions = restrictions;
	}

	@Transient
	public AvailabilityRestrictions getRestrictionsObject() {
		if (restrictions == null)
			return new AvailabilityRestrictions();
		
		ByteArrayInputStream byteStream = null;
		ObjectInputStream objectStream = null;
		try {
			AvailabilityRestrictions availabilityRestrictions = null;
			byteStream = new ByteArrayInputStream(restrictions.getBytes());
			objectStream = new ObjectInputStream(byteStream);
			availabilityRestrictions = (AvailabilityRestrictions) objectStream.readObject();
			objectStream.close();
			return availabilityRestrictions;
		} catch (IOException ex) {
			ex.printStackTrace();
			return new AvailabilityRestrictions();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return new AvailabilityRestrictions();
		}
	}

	public void setRestrictionsObject(AvailabilityRestrictions restrictions) {
		ByteArrayOutputStream byteStream = null;
		ObjectOutputStream objectStream = null;
		try {
			byteStream = new ByteArrayOutputStream();
			objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(restrictions);
			objectStream.close();
			this.restrictions = byteStream.toString();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
