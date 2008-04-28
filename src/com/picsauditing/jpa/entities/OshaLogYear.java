package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Embeddable
public class OshaLogYear {
	private YesNo na = YesNo.No;
	private int manHours;
	private int fatalities;
	private int lostWorkCases;
	private int lostWorkDays;
	private int injuryIllnessCases;
	private int restrictedWorkCases;
	private int recordableTotal;
	private YesNo file = YesNo.No;
	private Date verifiedDate;
	private String comment;

	@Type(type="com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", 
			parameters= {@Parameter(name="enumClass", value = "com.picsauditing.jpa.entities.YesNo")})	
	@Enumerated(EnumType.STRING)
	public YesNo getNa() {
		return na;
	}

	public void setNa(YesNo na) {
		this.na = na;
	}
	
	@Transient
	public boolean isApplicable() {
		return !YesNo.Yes.equals(na);
	}
	
	public void setApplicable(boolean value) {
		this.na = (value) ? YesNo.No : YesNo.Yes;
	}

	public int getManHours() {
		return manHours;
	}

	public void setManHours(int manHours) {
		this.manHours = manHours;
	}

	public int getFatalities() {
		return fatalities;
	}

	@Transient
	public float getFatalitiesRate() {
		return calculateRate(fatalities);
	}

	public void setFatalities(int fatalities) {
		this.fatalities = fatalities;
	}

	public int getLostWorkCases() {
		return lostWorkCases;
	}

	@Transient
	public float getLostWorkCasesRate() {
		return calculateRate(lostWorkCases);
	}

	public void setLostWorkCases(int lostWorkCases) {
		this.lostWorkCases = lostWorkCases;
	}

	public int getLostWorkDays() {
		return lostWorkDays;
	}

	@Transient
	public float getLostWorkDaysRate() {
		return calculateRate(lostWorkDays);
	}

	public void setLostWorkDays(int lostWorkDays) {
		this.lostWorkDays = lostWorkDays;
	}

	public int getInjuryIllnessCases() {
		return injuryIllnessCases;
	}

	public void setInjuryIllnessCases(int injuryIllnessCases) {
		this.injuryIllnessCases = injuryIllnessCases;
	}

	public int getRestrictedWorkCases() {
		return restrictedWorkCases;
	}

	public void setRestrictedWorkCases(int restrictedWorkCases) {
		this.restrictedWorkCases = restrictedWorkCases;
	}

	public int getRecordableTotal() {
		return recordableTotal;
	}

	@Transient
	public float getRecordableTotalRate() {
		return calculateRate(recordableTotal);
	}

	public void setRecordableTotal(int recordableTotal) {
		this.recordableTotal = recordableTotal;
	}

	@Type(type="com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", 
			parameters= {@Parameter(name="enumClass", value = "com.picsauditing.jpa.entities.YesNo")})
	public YesNo getFile() {
		return file;
	}

	@Enumerated(EnumType.STRING)
	public void setFile(YesNo file) {
		this.file = file;
	}

	public Date getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Transient
	public boolean getVerified() {
		return verifiedDate != null;
	}

	public void setVerified(boolean verified) {
		if (verified) {
			if (this.verifiedDate == null) {
				this.verifiedDate = new Date();
			}
		} else {
			this.verifiedDate = null;
		}
	}

	@Transient
	private float calculateRate(int value) {
		if (isApplicable() && manHours > 0) {
			float rate = value*200000;
			return rate/manHours;
		}
		
		return 0;
	}

}
