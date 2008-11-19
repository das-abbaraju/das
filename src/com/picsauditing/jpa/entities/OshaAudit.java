package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "osha_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class OshaAudit implements java.io.Serializable {
	private static final long serialVersionUID = -4451146415122493617L;
	private int id;
	private ContractorAudit conAudit;
	private OshaType type = OshaType.OSHA;
	private String location = "Corporate";
	private String description;

	private boolean applicable = true;
	private Date verifiedDate;

	private boolean fileUploaded = true;
	private int manHours;
	private int fatalities;
	private int lostWorkCases;
	private int lostWorkDays;
	private int injuryIllnessCases;
	private int restrictedWorkCases;
	private int recordableTotal;
	private int factor = 200000; // default factor used to normalize man hours
	private String comment;

	private FlagColor flagColor;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditID", nullable = false)
	public ContractorAudit getConAudit() {
		return conAudit;
	}

	public void setConAudit(ContractorAudit conAudit) {
		this.conAudit = conAudit;
	}

	@Column(name = "SHAType", nullable = false)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.OshaType") })
	@Enumerated(EnumType.STRING)
	public OshaType getType() {
		return type;
	}

	public void setType(OshaType type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Transient
	public boolean isCorporate() {
		return "Corporate".equals(location);
	}

	public void setCorporate(boolean corporate) {
		this.location = "Corporate";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
	}

	@Temporal(TemporalType.DATE)
	public Date getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	public boolean isFileUploaded() {
		return fileUploaded;
	}

	public void setFileUploaded(boolean fileUploaded) {
		this.fileUploaded = fileUploaded;
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

	public void setFatalities(int fatalities) {
		this.fatalities = fatalities;
	}

	public int getLostWorkCases() {
		return lostWorkCases;
	}

	public void setLostWorkCases(int lostWorkCases) {
		this.lostWorkCases = lostWorkCases;
	}

	public int getLostWorkDays() {
		return lostWorkDays;
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

	public void setRecordableTotal(int recordableTotal) {
		this.recordableTotal = recordableTotal;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Transient
	private float calculateRate(int value) {
		if (isApplicable() && manHours > 0) {
			float rate = value*200000;
			return rate/manHours;
		}
		
		return 0;
	}

	@Transient
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OshaAudit other = (OshaAudit) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
