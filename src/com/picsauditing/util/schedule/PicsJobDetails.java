package com.picsauditing.util.schedule;

import org.quartz.Job;

public class PicsJobDetails {
	String jobName = "";
	Class<? extends Job> jobClass = null;
	
	public PicsJobDetails() {
	}
	public PicsJobDetails(String jobName, Class<? extends Job> jobClass) {
		this.jobName = jobName;
		this.jobClass = jobClass;
	}
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public Class<? extends Job> getJobClass() {
		return jobClass;
	}
	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
	}

}
