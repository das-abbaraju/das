package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;
import java.util.*;

@SuppressWarnings("serial")
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
}