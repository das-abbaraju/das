package com.picsauditing.jpa.entities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public abstract class BaseHistory extends BaseTable {

	static final public Date END_OF_TIME = new GregorianCalendar(4000, 0, 1).getTime();

	protected Date effectiveDate;
	protected Date expirationDate;
	
	@Temporal(TemporalType.DATE)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public void defaultDates() {
		effectiveDate = new Date();
		expirationDate = (Date)END_OF_TIME.clone();
	}
	
	/**
	 * Expire this record to the start of today (midnight)
	 */
	public void expire() {
		expirationDate = getMidnightToday();
	}
	
	static public Date getMidnightToday() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	@Transient
	public boolean isCurrent() {
		Date now = new Date();
		return isCurrent(now);
	}

	@Transient
	public boolean isCurrent(Date now) {
		if (effectiveDate != null && effectiveDate.after(now))
			return false;
		if (expirationDate != null && expirationDate.before(now))
			return false;
		return true;
	}


	@Override
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("effectiveDate", effectiveDate.getTime());
		json.put("expirationDate", expirationDate.getTime());

		return json;
	}
}
