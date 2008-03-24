package com.picsauditing.entities;

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
@Table( name="audit_type" )
public class AuditType 
{
	@Deprecated
	public static final int PQF = 1;
	@Deprecated
	public static final int DESKTOP = 2;
	@Deprecated
	public static final int OFFICE = 3;
	@Deprecated
	public static final int NCMS = 4;
	@Deprecated
	public static final int DA = 6;
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	protected int auditTypeID = 0;
	protected String auditName = null;
	protected String description = null;
	protected boolean scheduled = false;
	protected boolean hasAuditor = false;
	protected boolean hasRequirements = false;
	protected boolean contractorCanView = false;
	protected boolean contractorCanEdit = false;
	protected int monthsToExpire = -1;
	
	@Temporal( value=TemporalType.DATE )
	protected Date dateToExpire = null;
	
	protected boolean hasMultiple = false;

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

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
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

	public boolean isContractorCanView() {
		return contractorCanView;
	}

	public void setContractorCanView(boolean contractorCanView) {
		this.contractorCanView = contractorCanView;
	}

	public boolean isContractorCanEdit() {
		return contractorCanEdit;
	}

	public void setContractorCanEdit(boolean contractorCanEdit) {
		this.contractorCanEdit = contractorCanEdit;
	}

	public int getMonthsToExpire() {
		return monthsToExpire;
	}

	public void setMonthsToExpire(int monthsToExpire) {
		this.monthsToExpire = monthsToExpire;
	}

	public Date getDateToExpire() {
		return dateToExpire;
	}

	public void setDateToExpire(Date dateToExpire) {
		this.dateToExpire = dateToExpire;
	}

	public boolean isHasMultiple() {
		return hasMultiple;
	}

	public void setHasMultiple(boolean hasMultiple) {
		this.hasMultiple = hasMultiple;
	}


}
