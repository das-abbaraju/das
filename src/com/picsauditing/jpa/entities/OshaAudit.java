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
	
	/**
	 * Key used when creating a map of osha data including the past 3 year average
	 */
	public static final String AVG = "Average";
	private int id;

	private Date creationDate;
	private Date updateDate;

	private ContractorAudit conAudit;
	private OshaType type = OshaType.OSHA;
	private String location = "Corporate";
	private String description;

	private Date verifiedDate;

	private boolean fileUploaded = false;
	private int manHours;
	private int fatalities;
	private int lostWorkCases;
	private int lostWorkDays;
	private int injuryIllnessCases;
	private int restrictedWorkCases;
	private int recordableTotal;
	private int factor = 200000; // default factor used to normalize man
	// hours
	private String comment;

	private FlagColor flagColor;
	private Float trir = null;
	private Float lwcr = null;
	
	private Float cad7 = 0.0f;
	private Float neer = 0.0f;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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

	public Float getCad7() {
		return cad7;
	}

	public void setCad7(Float cad7) {
		this.cad7 = cad7;
	}

	public Float getNeer() {
		return neer;
	}

	public void setNeer(Float neer) {
		this.neer = neer;
	}

	@Transient
	public int getFactor() {
		return factor;
	}

	public void setFactor(int factor) {
		this.factor = factor;
	}

	@Transient
	public float getFatalitiesRate() {
		return calculateRate(fatalities);
	}

	@Transient
	/**
	 * Get the LWCR, you can call setLostWorkCasesRate()
	 * if you don't want this to automatically calculate, 
	 * for example, in an average rate calculation.
	 */
	public float getLostWorkCasesRate() {
		if (lwcr == null)
			lwcr = calculateRate(lostWorkCases);
		return lwcr;
	}
	
	public void setLostWorkCasesRate(float rate) {
		this.lwcr = rate;
	}

	@Transient
	public float getLostWorkDaysRate() {
		return calculateRate(lostWorkDays);
	}

	@Transient
	public float getInjuryIllnessCasesRate() {
		return calculateRate(injuryIllnessCases);
	}

	@Transient
	public float getRestrictedWorkCasesRate() {
		return calculateRate(restrictedWorkCases);
	}

	@Transient
	/**
	 * Get the TRIR, you can call setRecordableTotalRate() 
	 * if you don't want this to automatically calculate, 
	 * for example, in an average rate calculation.
	 */
	public float getRecordableTotalRate() {
		if (trir == null)
			trir = calculateRate(recordableTotal);
		return trir;
	}

	public void setRecordableTotalRate(float rate) {
		this.trir = rate;
	}

	
	@Transient
	private float calculateRate(int value) {
		if (manHours > 0) {
			float rate = value * 200000;
			return rate / manHours;
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
	
	@Transient
	public boolean isVerified() {
		return verifiedDate != null;
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
	
	@Transient
	public String getDescriptionReportable() {
		if (getType() != null && getType().equals(OshaType.MSHA))
			return "MSHA Reportable";
		else
			return "OSHA Recordable";
	}
	
}
