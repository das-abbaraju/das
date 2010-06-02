package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_dt")
public class BaseDecisionTable extends BaseTable {

	protected int priority;
	protected boolean include = true;

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isInclude() {
		return include;
	}

	public void setInclude(boolean include) {
		this.include = include;
	}

}
