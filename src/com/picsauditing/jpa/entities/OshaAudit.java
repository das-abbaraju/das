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
public class OshaAudit implements java.io.Serializable, Comparable<OshaAudit> {
	private static final long serialVersionUID = -4451146415122493617L;

	/**
	 * Key used when creating a map of osha data including the past 3 year
	 * average
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
	private User auditor;

	private boolean fileUploaded = false;
	private int manHours;
	private int fatalities;
	private int lostWorkCases;
	private int lostWorkDays;
	private int injuryIllnessCases;
	private int restrictedWorkCases;
	private int recordableTotal;
	private int factor = 200000;
	private String comment;

	private FlagColor flagColor;
	private Float trir = null;
	private Float lwcr = null;
	private Float dart = null;
	private Float severityRate = null;

	private Float cad7 = 0.0f;
	private Float neer = 0.0f;
	private int firstAidInjuries;
	private int modifiedWorkDay;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditorID")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
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

	public int getFirstAidInjuries() {
		return firstAidInjuries;
	}

	public void setFirstAidInjuries(int firstAidInjuries) {
		this.firstAidInjuries = firstAidInjuries;
	}

	public int getModifiedWorkDay() {
		return modifiedWorkDay;
	}

	public void setModifiedWorkDay(int modifiedWorkDay) {
		this.modifiedWorkDay = modifiedWorkDay;
	}

	/**
	 * default factor (200K) used to normalize man hours
	 * @return
	 */
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
	/**
	 * Get the DART, you can call getRestrictedDaysAwayRate()
	 * DART is a way for operators to measure severity rates on contractors
	 * if you don't want this to automatically calculate, 
	 * for example, in an average rate calculation.
	 */
	public Float getRestrictedDaysAwayRate() {
		if (dart == null)
			dart = calculateRate(lostWorkCases + restrictedWorkCases);
		return dart;
	}

	public void setRestrictedDaysAwayRate(float rate) {
		this.dart = rate;
	}

	@Transient
	/**
	 * Get the severity rate, you can call getRestrictedOrJobTransferDays()
	 * Severity rate is a way for operators to measure severity rates on contractors
	 * if you don't want this to automatically calculate, 
	 * for example, in an average rate calculation.
	 */
	public Float getRestrictedOrJobTransferDays() {
		if (severityRate == null) {
			if (type.equals(OshaType.OSHA)) {
				severityRate = calculateRate(lostWorkDays + modifiedWorkDay);
			} else {
				severityRate = calculateRate(lostWorkDays);
			}
		}
		return severityRate;
	}

	public void setRestrictedOrJobTransferDays(float rate) {
		this.severityRate = rate;
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

	@Transient
	public float getRate(OshaRateType rateType) {
		// expecting that the caller already knows the type of rate they expect
		// (i.e. caller knows it is a COHS and therefore expects COHS rateTypes
		// to be valid)
		switch (rateType) {
		case SeverityRate:
			return getRestrictedOrJobTransferDays();
		case LwcrAbsolute:
			return getLostWorkCasesRate();
		case LwcrNaics:
			return (getLostWorkCasesRate() / conAudit.getContractorAccount().getNaics().getLwcr()) * 100;
		case TrirAbsolute:
			return getRecordableTotalRate();
		case TrirNaics:
			return (getRecordableTotalRate() / conAudit.getContractorAccount().getNaics().getTrir()) * 100;
		case Fatalities:
			return getFatalities();
		case Cad7:
			return getCad7();
		case Neer:
			return getNeer();
		default:
			throw new RuntimeException("Invalid OSHA Rate Type of " + rateType.toString()
					+ " specified for osha audit id " + getId() + ", contractor id "
					+ conAudit.getContractorAccount().getId());
		}
	}

	@Transient
	public float getValue(OshaRateType rateType) {
		// expecting that the caller already knows the type of rate they expect
		// (i.e. caller knows it is a COHS and therefore expects COHS rateTypes
		// to be valid)
		switch (rateType) {
		case SeverityRate:
			return (type.equals(OshaType.OSHA) ? lostWorkDays + modifiedWorkDay : lostWorkDays);
		case LwcrAbsolute:
			return getLostWorkCases();
		case LwcrNaics:
			return (getLostWorkCases()*100)/conAudit.getContractorAccount().getNaics().getLwcr();
		case TrirAbsolute:
			return getRecordableTotal();
		case TrirNaics:
			return (getRecordableTotal()*100)/conAudit.getContractorAccount().getNaics().getTrir();
		case Fatalities:
			return getFatalities();
		case Cad7:
			return getCad7();
		case Neer:
			return getNeer();
		default:
			throw new RuntimeException("Invalid OSHA Rate Type of " + rateType.toString()
					+ " specified for osha audit id " + getId() + ", contractor id "
					+ conAudit.getContractorAccount().getId());
		}
	}

	@Override
	public int compareTo(OshaAudit o) {
		return (this.getConAudit().getAuditFor().compareTo(o.getConAudit().getAuditFor()));
	}
}
