package com.picsauditing.auditbuilder.entities;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseHistory extends BaseTable {

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
}