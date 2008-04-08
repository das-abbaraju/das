package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "audit_type")
public class AuditType {
	public static final int PQF = 1;
	public static final int DESKTOP = 2;
	public static final int OFFICE = 3;
	public static final int NCMS = 4;
	public static final int DA = 6;

	protected int auditTypeID;

	protected String auditName;
	protected String description;
	protected boolean hasMultiple;
	protected boolean isScheduled;
	protected boolean hasAuditor;
	protected boolean hasRequirements;
	protected boolean canContractorView;
	protected boolean canContractorEdit;
	protected Integer monthsToExpire = -1;
	protected Date dateToExpire;
	protected String legacyCode;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isHasMultiple() {
		return hasMultiple;
	}

	public void setHasMultiple(boolean hasMultiple) {
		this.hasMultiple = hasMultiple;
	}

	@Column( name="isScheduled")
	public boolean isScheduled() {
		return isScheduled;
	}

	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public boolean isHasAuditor() {
		return hasAuditor;
	}

	public void setHasAuditor(boolean hasAuditor) {
		this.hasAuditor = hasAuditor;
	}

	public boolean isHasRequirements() {
		return hasRequirements;
	}

	public void setHasRequirements(boolean hasRequirements) {
		this.hasRequirements = hasRequirements;
	}

	public boolean isCanContractorView() {
		return canContractorView;
	}

	public void setCanContractorView(boolean canContractorView) {
		this.canContractorView = canContractorView;
	}

	public boolean isCanContractorEdit() {
		return canContractorEdit;
	}

	public void setCanContractorEdit(boolean canContractorEdit) {
		this.canContractorEdit = canContractorEdit;
	}

	public Integer getMonthsToExpire() {
		return monthsToExpire;
	}

	public void setMonthsToExpire(Integer monthsToExpire) {
		this.monthsToExpire = monthsToExpire;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getDateToExpire() {
		return dateToExpire;
	}

	public void setDateToExpire(Date dateToExpire) {
		this.dateToExpire = dateToExpire;
	}
	
	public String getLegacyCode() {
		return legacyCode;
	}

	public void setLegacyCode(String legacyCode) {
		this.legacyCode = legacyCode;
	}

}
