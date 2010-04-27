package com.picsauditing.jpa.entities;

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
