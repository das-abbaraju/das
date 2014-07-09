package com.picsauditing.auditbuilder.entities;

import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseDecisionTreeRule extends BaseTable implements Comparable<BaseDecisionTreeRule> {

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

	@Override
	public int compareTo(BaseDecisionTreeRule o) {
		return priority - o.priority;
	}
}