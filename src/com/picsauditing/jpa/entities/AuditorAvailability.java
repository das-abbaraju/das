package com.picsauditing.jpa.entities;

import com.picsauditing.access.Permissions;
import com.picsauditing.util.Geo;
import com.picsauditing.util.Location;
import com.picsauditing.util.Strings;
import com.picsauditing.util.TimeZoneUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.json.simple.JSONObject;

import javax.persistence.Column;
import javax.persistence.*;
import java.text.ParseException;
import java.util.*;

@Entity
@SuppressWarnings("serial")
@Table(name = "auditor_availability")
public class AuditorAvailability extends BaseTable {
	public static final String START_TIME_FORMAT = "hh:mm a";
	public static final String STOP_TIME_FORMAT = "hh:mm a z";
    public static final String CANCELLATION_TIME_FORMAT = "yyyy-MM-dd hh:mm a z";

	private User user;
	private Date startDate;
	private int duration;

	private float latitude = 0;
	private float longitude = 0;
	private int maxDistance = 30; // km

	private boolean onsiteOnly = false;
	private boolean webOnly = false;
	private String onlyInCountrySubdivisions = null;

	@Transient
	public Date getEndDate() {
		DateTime endDate = new DateTime(startDate);
		return endDate.plusMinutes(duration).toDate();
	}

    @Transient
    public Date getLastCancellationDate() {
        DateTime jodaStartDate = new DateTime(startDate);
        jodaStartDate = jodaStartDate.minusDays(2);
        while (jodaStartDate.dayOfWeek().equals(DateTimeConstants.SATURDAY) || jodaStartDate.dayOfWeek().equals(DateTimeConstants.SUNDAY)) {
            jodaStartDate = jodaStartDate.minusDays(1);
        }

        return jodaStartDate.toDate();
    }

	@Transient
	public String getTimeZoneStartDate(String timezoneId) throws ParseException {
		TimeZone timezone = TimeZone.getTimeZone(timezoneId);
		return TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, START_TIME_FORMAT, startDate);
	}

	@Transient
	public String getTimeZoneEndDate(String timezoneId) throws ParseException {
		TimeZone timezone = TimeZone.getTimeZone(timezoneId);
		return TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, STOP_TIME_FORMAT, getEndDate());
	}

    @Transient
    public String getTimeZoneLastCancellationDate(String timezoneId) throws ParseException {
		TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        return TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, CANCELLATION_TIME_FORMAT, getLastCancellationDate());
    }

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

	@Transient
	public String getOnlyInCountrySubdivisions() {
		return onlyInCountrySubdivisions;
	}

	@Transient
	public void setOnlyInCountrySubdivisions(String onlyInCountrySubdivisions) {
		this.onlyInCountrySubdivisions = onlyInCountrySubdivisions;
	}

	@Transient
	public void setOnlyInCountrySubdivisions(String[] onlyInCountrySubdivisions) {
		this.onlyInCountrySubdivisions = Strings.implode(Arrays.asList(onlyInCountrySubdivisions), ",");
	}

	@Transient
	public String[] getOnlyInCountrySubdivisionsArray() {
		if (onlyInCountrySubdivisions == null)
			return null;

		return onlyInCountrySubdivisions.split(",");
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
		return rank(conAudit, permissions, null);
	}

	@Transient
	public int rank(ContractorAudit conAudit, Permissions permissions, List<User> auditors) {
		int rank = 1;

		if (isConductedOnsite(conAudit)) {
			rank += 100;
		}

		String[] countrySubdivisions = getOnlyInCountrySubdivisionsArray();
		if (countrySubdivisions != null && countrySubdivisions.length > 0) {
			boolean matchedCountrySubdivision = false;
			for (String countrySubdivision : countrySubdivisions) {
				if (countrySubdivision.equals(conAudit.getCountrySubdivision())) {
					matchedCountrySubdivision = true;
					rank += 25;
				}
			}
			if (!matchedCountrySubdivision) {
				rank -= 10;
			}
		}

		if (permissions.isAuditor() && user.getId() == permissions.getUserId()
				|| (conAudit.getAuditor() != null && user.getId() == conAudit.getAuditor().getId())
				|| (auditors != null && auditors.contains(user)))
			rank += 1000;

		return rank;
	}

}
