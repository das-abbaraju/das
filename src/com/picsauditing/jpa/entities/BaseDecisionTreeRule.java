package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public abstract class BaseDecisionTreeRule extends BaseHistory {

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
	
	abstract public void calculatePriority();

}
