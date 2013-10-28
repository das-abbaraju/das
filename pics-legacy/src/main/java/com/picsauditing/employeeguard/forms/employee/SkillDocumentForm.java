package com.picsauditing.employeeguard.forms.employee;

import java.util.Calendar;

public class SkillDocumentForm {

	//    public enum Proof { Certified, Document, None }
	public enum ExpirationType {
		Date, NoExpiration, None
	}

	private SkillInfo skillInfo;
	private String proof;
	private int documentId;
	private boolean verified;

	public SkillInfo getSkillInfo() {
		return skillInfo;
	}

	public void setSkillInfo(SkillInfo skillInfo) {
		this.skillInfo = skillInfo;
	}

	public String getProof() {
		return proof;
	}

	public void setProof(String proof) {
		this.proof = proof;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public boolean isDoesNotExpire() {
		if (skillInfo != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, 3000);
			return skillInfo.getEndDate().after(calendar.getTime());
		}

		return false;
	}
}
