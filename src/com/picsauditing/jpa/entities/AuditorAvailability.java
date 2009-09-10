package com.picsauditing.jpa.entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.util.Geo;
import com.picsauditing.util.log.PicsLogger;

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
	
	@Transient
	public Date getEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MINUTE, duration);
		return cal.getTime();
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

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("title", "Empty Slot");

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		obj.put("start", cal.getTimeInMillis());

		cal.add(Calendar.MINUTE, duration);
		obj.put("end", cal.getTimeInMillis());

		return obj;
	}

	@Transient
	public boolean isOkFor(ContractorAudit conAudit) {
		PicsLogger.log("is auditID " + conAudit.getId() + " OK");
		AvailabilityRestrictions aRestrictions = getRestrictionsObject();
		String[] states = aRestrictions.getOnlyInStates();
		if (states != null && states.length > 0) {
			boolean matchedState = false;
			for(String state : aRestrictions.getOnlyInStates()) {
				if (state.equals(conAudit.getState())) {
					PicsLogger.log("found matching state");
					matchedState = true;
				}
			}
			if (!matchedState) {
				PicsLogger.log(conAudit.getState() + "not in " + states);
				return false;
			}
		}
		if (aRestrictions.isWebOnly()) {
			if (conAudit.isConductedOnsite()) {
				PicsLogger.log("onsite audits can't be conducted on webOnly slots");
				return false;
			}
		} else {
			if (aRestrictions.getNearLatitude() != 0 && aRestrictions.getNearLongitude() != 0) {
				if (conAudit.getLatitude() == 0 || conAudit.getLongitude() == 0)
					return false;
				
				double distance = Geo.distance(aRestrictions.getNearLatitude(), aRestrictions.getNearLongitude(), conAudit.getLatitude(), conAudit.getLongitude());
				PicsLogger.log("Audit is about " + Math.round(distance) + " km away");
				if (distance > 30)
					return false;
			}
		}
		PicsLogger.log("Audit is OK for this timeslot");
		return true;
	}
	
	@Transient
	public boolean isConductedOnsite(ContractorAudit conAudit) {
		return true;
	}

}
