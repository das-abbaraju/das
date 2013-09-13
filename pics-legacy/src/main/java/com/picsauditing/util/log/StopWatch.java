package com.picsauditing.util.log;

import java.util.Date;

public class StopWatch {
	protected String name = null;
	protected String fqn = null;
	protected StopWatch parent = null;
	protected Date startTime = new Date();

	public StopWatch(String name) {
		this.name = name;
		this.fqn = "|" + name;
	}
	
	public StopWatch(StopWatch parent, String name) {
		this.name = name;
		this.parent = parent;
		this.fqn = parent.getFqn() + "|" + name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFqn() {
		return fqn;
	}
	public StopWatch getParent() {
		return parent;
	}
	public void setParent(StopWatch parent) {
		this.parent = parent;
	}
	
	public Date getDate() {
		return startTime;
	}

	public String getDateString() {
		return startTime.toString();
	}
}
