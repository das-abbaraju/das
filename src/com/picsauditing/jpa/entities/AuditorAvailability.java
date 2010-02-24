package com.picsauditing.jpa.entities;

import java.util.Arrays;
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

import com.picsauditing.access.Permissions;
import com.picsauditing.util.Geo;
import com.picsauditing.util.Location;
import com.picsauditing.util.Strings;

@Entity
@Table(name = "auditor_availability")
public class AuditorAvailability extends BaseTable {

	private User user;
	private Date startDate;
	private int duration;

	private float latitude = 0;
	private float longitude = 0;
	private int maxDistance = 30; // km

	private boolean onsiteOnly = false;
	private boolean webOnly = false;
	private String onlyInStates = null;

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

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	@Transient
	public Location getLocation() {
		return new Location(latitude, longitude);
	}

	@Transient
	public void setLocation(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	public boolean isOnsiteOnly() {
		return onsiteOnly;
	}

	public void setOnsiteOnly(boolean onsiteOnly) {
		this.onsiteOnly = onsiteOnly;
	}

	public boolean isWebOnly() {
		return webOnly;
	}

	public void setWebOnly(boolean webOnly) {
		this.webOnly = webOnly;
	}

	public String getOnlyInStates() {
		return onlyInStates;
	}

	public void setOnlyInStates(String onlyInStates) {
		this.onlyInStates = onlyInStates;
	}

	public void setOnlyInStates(String[] onlyInStates) {
		this.onlyInStates = Strings.implode(Arrays.asList(onlyInStates), ",");
	}

	@Transient
	public String[] getOnlyInStatesArray() {
		if (onlyInStates == null)
			return null;

		return onlyInStates.split(",");
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		obj.put("id", "Availability_" + id);
		obj.put("title", "Open Timeslot");
		obj.put("owner", user.getName());

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

	/**
	 * If the audit is close enough, then assume it will be onsite
	 * 
	 * @param conAudit
	 * @return
	 */
	@Transient
	public boolean isConductedOnsite(ContractorAudit conAudit) {
		if (getLocation() == null || conAudit.getLocation() == null)
			// If you don't know where you are, you can't measure distance!
			return false;

		double distanceApart = Geo.distance(getLocation(), conAudit.getLocation());

		// If the audit is close enough, then assume it will be onsite
		return (distanceApart < getMaxDistance());
	}

	@Transient
	public int rank(ContractorAudit conAudit, Permissions permissions) {
		int rank = 1;

		if (isConductedOnsite(conAudit)) {
			rank += 100;
		}

		String[] states = getOnlyInStatesArray();
		if (states != null && states.length > 0) {
			boolean matchedState = false;
			for (String state : states) {
				if (state.equals(conAudit.getState())) {
					matchedState = true;
					rank += 25;
				}
			}
			if (!matchedState) {
				rank -= 10;
			}
		}

		if (permissions.isAuditor() && user.getId() == permissions.getUserId())
			rank += 1000;

		return rank;
	}

}
