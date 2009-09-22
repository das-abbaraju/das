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

	/**
	 * One time conversion from object to bytestream (this.restrictions)
	 * If you change the AvailabilityRestrictions, you MUST call setRestrictionsObject(restrictions) again!
	 * @param restrictions
	 */
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
		obj.put("id", "Availability_" + id);
		obj.put("title", user.getName());

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		obj.put("start", cal.getTimeInMillis());

		cal.add(Calendar.MINUTE, duration);
		obj.put("end", cal.getTimeInMillis());
		
		obj.put("allDay", false);
		obj.put("editable", false);

		obj.put("className", "cal-availability");

		return obj;
	}

	@Transient
	public boolean isOkFor(ContractorAudit conAudit, boolean strict) {
		PicsLogger.log("is auditID " + conAudit.getId() + " OK" + (strict ? " using Strict" : ""));
		AvailabilityRestrictions aRestrictions = getRestrictionsObject();
		String[] states = aRestrictions.getOnlyInStates();
		if (strict && states != null && states.length > 0) {
			boolean matchedState = false;
			for (String state : aRestrictions.getOnlyInStates()) {
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

		boolean onSite = isConductedOnsite(conAudit);

		if (aRestrictions.isWebOnly()) {
			if (onSite) {
				PicsLogger.log("onsite audits can't be conducted on webOnly slots");
				return false;
			}
		}

		if (aRestrictions.isOnsiteOnly()) {
			if (!onSite) {
				PicsLogger.log("web audits can't be conducted on onSite only slots");
				return false;
			}
		}

		PicsLogger.log("Audit is OK for this timeslot");
		return true;
	}

	/**
	 * If the audit is close enough, then assume it will be onsite
	 * 
	 * @param conAudit
	 * @return
	 */
	@Transient
	public boolean isConductedOnsite(ContractorAudit conAudit) {
		AvailabilityRestrictions aRestrictions = getRestrictionsObject();

		if (aRestrictions.getLocation() == null || conAudit.getLocation() == null)
			// If you don't know where you are, you can't measure distance!
			return false;

		double distanceApart = Geo.distance(aRestrictions.getLocation(), conAudit.getLocation());

		// If the audit is close enough, then assume it will be onsite
		return (distanceApart < aRestrictions.getMaxDistance());
	}

	@Transient
	public int rank(ContractorAudit conAudit) {
		int rank = 1;

		if (isConductedOnsite(conAudit)) {
			rank += 100;
		}

		AvailabilityRestrictions aRestrictions = getRestrictionsObject();
		String[] states = aRestrictions.getOnlyInStates();
		if (states != null && states.length > 0) {
			boolean matchedState = false;
			for (String state : aRestrictions.getOnlyInStates()) {
				if (state.equals(conAudit.getState())) {
					rank += 25;
				}
			}
			if (!matchedState) {
				rank -= 10;
			}
		}

		return rank;
	}

}
