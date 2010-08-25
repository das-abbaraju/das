package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public abstract class BaseDecisionTreeRule extends BaseHistoryTime implements Comparable<BaseDecisionTreeRule> {

	protected int priority;
	protected int level;
	protected boolean include = true;

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isInclude() {
		return include;
	}

	public void setInclude(boolean include) {
		this.include = include;
	}

	abstract public void calculatePriority();

	@Transient
	public int getDepth() {
		return (int) Math.floor(priority / 100.0);
	}

	@Override
	public int compareTo(BaseDecisionTreeRule o) {
		return priority - o.priority;
	}
}
