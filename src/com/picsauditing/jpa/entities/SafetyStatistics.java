package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.List;


/**
 * A normalized holding area with the info needed to analyze the safety information for a particular HSE audit (for a particular OSHA type, for a particular contractor, for a particular year). 
 *
 */
public abstract class SafetyStatistics {
	protected HashMap<OshaRateType, AuditData> answerMap;
	protected OshaType oshaType;
	protected int year;
	protected AuditData fileUpload = null;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public SafetyStatistics(int year, OshaType oshaType, List<AuditData> data) {
		this.oshaType = oshaType;
		this.year = year;
	}

	public OshaType getOshaType(){
		return oshaType;
	}
	
	public String getStats(OshaRateType rateType) {
		return answerMap.get(rateType).getAnswer();
	}

	public HashMap<OshaRateType, AuditData> getAnswerMap() {
		return answerMap;
	}

	public AuditData getFileUpload() {
		return fileUpload;
	}

	abstract public List<AuditData> getQuestionsToVerify();
}
