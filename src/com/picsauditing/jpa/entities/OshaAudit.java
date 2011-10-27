package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "osha_audit")
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
	private float manHours;
	private float fatalities;
	private float lostWorkCases;
	private float lostWorkDays;
	private float injuryIllnessCases;
	private float restrictedWorkCases;
	private float recordableTotal;
	private float factor = 200000;
	private String comment;

	private FlagColor flagColor;
	private Float trir = null;
	private Float lwcr = null;
	private Float dart = null;
	private Float severityRate = null;

	private Float cad7;
	private Float neer;
	private float firstAidInjuries;
	private float modifiedWorkDay;
	private float vehicleIncidents;
	private float totalkmDriven;

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

	public float getManHours() {
		return manHours;
	}

	public void setManHours(float manHours) {
		this.manHours = manHours;
	}

	public float getFatalities() {
		return fatalities;
	}

	public void setFatalities(float fatalities) {
		this.fatalities = fatalities;
	}

	public float getLostWorkCases() {
		return lostWorkCases;
	}

	public void setLostWorkCases(float lostWorkCases) {
		this.lostWorkCases = lostWorkCases;
	}

	public float getLostWorkDays() {
		return lostWorkDays;
	}

	public void setLostWorkDays(float lostWorkDays) {
		this.lostWorkDays = lostWorkDays;
	}

	public float getInjuryIllnessCases() {
		return injuryIllnessCases;
	}

	public void setInjuryIllnessCases(float injuryIllnessCases) {
		this.injuryIllnessCases = injuryIllnessCases;
	}

	public float getRestrictedWorkCases() {
		return restrictedWorkCases;
	}

	public void setRestrictedWorkCases(float restrictedWorkCases) {
		this.restrictedWorkCases = restrictedWorkCases;
	}

	public float getRecordableTotal() {
		return recordableTotal;
	}

	public void setRecordableTotal(float recordableTotal) {
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

	public float getFirstAidInjuries() {
		return firstAidInjuries;
	}

	public void setFirstAidInjuries(float firstAidInjuries) {
		this.firstAidInjuries = firstAidInjuries;
	}

	public float getModifiedWorkDay() {
		return modifiedWorkDay;
	}

	public void setModifiedWorkDay(float modifiedWorkDay) {
		this.modifiedWorkDay = modifiedWorkDay;
	}

	public float getVehicleIncidents() {
		return vehicleIncidents;
	}

	public void setVehicleIncidents(float vehicleIncidents) {
		this.vehicleIncidents = vehicleIncidents;
	}

	public float getTotalkmDriven() {
		return totalkmDriven;
	}

	public void setTotalkmDriven(float totalkmDriven) {
		this.totalkmDriven = totalkmDriven;
	}

	/**
	 * default factor (200K) used to normalize man hours
	 * @return
	 */
	@Transient
	public float getFactor() {
		return factor;
	}

	public void setFactor(float factor) {
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
			return calculateRate(lostWorkCases);
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
	public float getModifiedWorkDayRate() {
		return calculateRate(modifiedWorkDay);
	}

	@Transient
	/**
	 * Get the TRIR, you can call setRecordableTotalRate() 
	 * if you don't want this to automatically calculate, 
	 * for example, in an average rate calculation.
	 */
	public float getRecordableTotalRate() {
		if (trir == null)
			return calculateRate(recordableTotal);
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
			return calculateRate(lostWorkCases + restrictedWorkCases);
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
				return calculateRate(lostWorkDays + modifiedWorkDay);
			} else {
				return calculateRate(lostWorkDays);
			}
		}
		return severityRate;
	}

	public void setRestrictedOrJobTransferDays(float rate) {
		this.severityRate = rate;
	}

	@Transient
	private float calculateRate(float value) {
		if (manHours > 0) {
			BigDecimal rate =  new BigDecimal(value * 200000);
			return rate.divide(new BigDecimal(manHours), 2, BigDecimal.ROUND_HALF_UP).floatValue();
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
	public Float getRate(OshaRateType rateType) {
		// expecting that the caller already knows the type of rate they expect
		// (i.e. caller knows it is a COHS and therefore expects COHS rateTypes
		// to be valid)
		switch (rateType) {
		case SeverityRate:
			return getRestrictedOrJobTransferDays();
		case LwcrAbsolute:
			return getLostWorkCasesRate();
		case LwcrNaics:
			return getLostWorkCasesRate();
		case TrirAbsolute:
			return getRecordableTotalRate();
		case TrirNaics:
			return getRecordableTotalRate();
		case Fatalities:
			return (float)getFatalities();
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
	public Float getValue(OshaRateType rateType) {
		// expecting that the caller already knows the type of rate they expect
		// (i.e. caller knows it is a COHS and therefore expects COHS rateTypes
		// to be valid)
		switch (rateType) {
		case SeverityRate:
			return (float)(type.equals(OshaType.OSHA) ? lostWorkDays + modifiedWorkDay : lostWorkDays);
		case LwcrAbsolute:
			return (float)getLostWorkCases();
		case LwcrNaics:
			return (float)getLostWorkCases();
		case TrirAbsolute:
			return (float)getRecordableTotal();
		case TrirNaics:
			return (float)getRecordableTotal();
		case Fatalities:
			return (float)getFatalities();
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
