package com.picsauditing.employeeguard.forms.employee;

public class SkillDocumentForm {

	//    public enum Proof { Certified, Document, None }
	public enum ExpirationType {
		Date, NoExpiration, None
	}

	private SkillInfo skillInfo;
	private String proof;
	private int documentId;

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
}
