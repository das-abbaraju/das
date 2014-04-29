package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;
import java.util.*;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseHistory extends BaseTable {

	static final public Date END_OF_TIME = new GregorianCalendar(4000, 0, 1).getTime();
    // static final public Date BEGINING_OF_TIME = new GregorianCalendar(2001, 0, 1).getTime();

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

//	public void defaultDates() {
//		effectiveDate = new Date();
//		expirationDate = (Date) END_OF_TIME.clone();
//	}

//	/**
//	 * Expire this record to the start of today (midnight)
//	 */
//	public void expire() {
//		expirationDate = getMidnightToday();
//	}

//	static public Date getMidnightToday() {
//		Calendar cal = new GregorianCalendar();
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		return cal.getTime();
//	}
//	@Override
//	@SuppressWarnings("unchecked")
//	public JSONObject toJSON(boolean full) {
//		JSONObject json = super.toJSON(full);
//
//		json.put("effectiveDate", effectiveDate.getTime());
//		json.put("expirationDate", expirationDate.getTime());
//
//		return json;
//	}
}
