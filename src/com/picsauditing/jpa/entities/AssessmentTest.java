package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "assessment_test")
public class AssessmentTest extends BaseTable {

	private Account assessmentCenter;
	private String qualificationType;
	private String qualificationMethod;
	private String description;
	private Date effectiveDate;
	private Date expirationDate;
	private boolean verifiable = true;
	private int monthsToExpire = 36;

	@ManyToOne
	@JoinColumn(name = "assessmentCenterID", nullable = false, updatable = false)
	public Account getAssessmentCenter() {
		return assessmentCenter;
	}

	public void setAssessmentCenter(Account assessmentCenter) {
		this.assessmentCenter = assessmentCenter;
	}

	public String getQualificationType() {
		return qualificationType;
	}

	public void setQualificationType(String qualificationType) {
		this.qualificationType = qualificationType;
	}

	public String getQualificationMethod() {
		return qualificationMethod;
	}

	public void setQualificationMethod(String qualificationMethod) {
		this.qualificationMethod = qualificationMethod;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isVerifiable() {
		return verifiable;
	}

	public void setVerifiable(boolean verifiable) {
		this.verifiable = verifiable;
	}

	public int getMonthsToExpire() {
		return monthsToExpire;
	}

	public void setMonthsToExpire(int monthsToExpire) {
		this.monthsToExpire = monthsToExpire;
	}

	@Transient
	public String getName(){
		return assessmentCenter.getName()+" "+qualificationType+":"+qualificationMethod;
	}
}
