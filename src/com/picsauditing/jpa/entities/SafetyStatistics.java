package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.List;

/**
 * A normalized holding area with the info needed to analyze the safety information for a particular 
 * HSE audit (for a particular OSHA type, for a particular contractor, for a particular year). 
 */
public abstract class SafetyStatistics {
	
	protected HashMap<OshaRateType, AuditData> answerMap;
	protected OshaType oshaType;
	protected int year;
	protected AuditData fileUpload = null;
	protected boolean verified;
	
	public SafetyStatistics(int year, OshaType oshaType, List<AuditData> data) {
		this.oshaType = oshaType;
		this.year = year;
	}

	public OshaType getOshaType() {
		return oshaType;
	}
	
	/**
	 * Returns the answer for the rateType.
	 * 
	 * Will print an error to the server output if the answerMap does not have an
	 * answer for the rate type provided and return null because there are cases
	 * when no answer will be available for the given rate type. 
	 * 
	 * @param rateType
	 * @return
	 */
	public String getStats(OshaRateType rateType) {
		AuditData auditData = answerMap.get(rateType);
		if (auditData == null) {
			logErrorInformation(rateType);
			return null;
		}
		
		return answerMap.get(rateType).getAnswer();
	}
	
	private void logErrorInformation(OshaRateType rateType) {
		StringBuilder error = new StringBuilder();
		error.append("SafetyStatistics -> Configuration Error: rateType ").append(rateType)
				.append(" is not in the answerMap.");
		
		if (fileUpload != null) {
			error.append(" AuditData id = " + fileUpload.getId());
		}
		
		error.append(" Year = " + year);
		
		System.out.println(error.toString());				
	}

	public HashMap<OshaRateType, AuditData> getAnswerMap() {
		return answerMap;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public AuditData getFileUpload() {
		return fileUpload;
	}
	
	public boolean isVerified() {
		return verified;
	}
	
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	abstract public List<AuditData> getQuestionsToVerify();
		
}
