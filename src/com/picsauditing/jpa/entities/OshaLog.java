package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

// TODO handle duringGracePeriod

@Entity
@Table(name = "osha")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="temp")
public class OshaLog implements java.io.Serializable {
	private int id;
	private ContractorAccount contractorAccount;
	private OshaType type;
	private String location = "Corporate";
	private String description;
	private int auditorId;

	private OshaLogYear year1;
	private OshaLogYear year2;
	private OshaLogYear year3;
	private OshaLogYear avg;

	private FlagColor flagColor;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "OID", nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

	@Column(name = "SHAType", nullable = false)
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

	public int getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(int auditorId) {
		this.auditorId = auditorId;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "na", column = @Column(name = "na1")),
			@AttributeOverride(name = "manHours", column = @Column(name = "manHours1")),
			@AttributeOverride(name = "fatalities", column = @Column(name = "fatalities1")),
			@AttributeOverride(name = "lostWorkCases", column = @Column(name = "lostWorkCases1")),
			@AttributeOverride(name = "lostWorkDays", column = @Column(name = "lostWorkDays1")),
			@AttributeOverride(name = "injuryIllnessCases", column = @Column(name = "injuryIllnessCases1")),
			@AttributeOverride(name = "restrictedWorkCases", column = @Column(name = "restrictedWorkCases1")),
			@AttributeOverride(name = "recordableTotal", column = @Column(name = "recordableTotal1")),
			@AttributeOverride(name = "verifiedDate", column = @Column(name = "verifiedDate1")),
			@AttributeOverride(name = "comment", column = @Column(name = "comment1")),
			@AttributeOverride(name = "file", column = @Column(name = "file1YearAgo")) })
	public OshaLogYear getYear1() {
		return year1;
	}

	public void setYear1(OshaLogYear year1) {
		this.year1 = year1;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "na", column = @Column(name = "na2")),
			@AttributeOverride(name = "manHours", column = @Column(name = "manHours2")),
			@AttributeOverride(name = "fatalities", column = @Column(name = "fatalities2")),
			@AttributeOverride(name = "lostWorkCases", column = @Column(name = "lostWorkCases2")),
			@AttributeOverride(name = "lostWorkDays", column = @Column(name = "lostWorkDays2")),
			@AttributeOverride(name = "injuryIllnessCases", column = @Column(name = "injuryIllnessCases2")),
			@AttributeOverride(name = "restrictedWorkCases", column = @Column(name = "restrictedWorkCases2")),
			@AttributeOverride(name = "recordableTotal", column = @Column(name = "recordableTotal2")),
			@AttributeOverride(name = "verifiedDate", column = @Column(name = "verifiedDate2")),
			@AttributeOverride(name = "comment", column = @Column(name = "comment2")),
			@AttributeOverride(name = "file", column = @Column(name = "file2YearAgo")) })
	public OshaLogYear getYear2() {
		return year2;
	}

	public void setYear2(OshaLogYear year2) {
		this.year2 = year2;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "na", column = @Column(name = "na3")),
			@AttributeOverride(name = "manHours", column = @Column(name = "manHours3")),
			@AttributeOverride(name = "fatalities", column = @Column(name = "fatalities3")),
			@AttributeOverride(name = "lostWorkCases", column = @Column(name = "lostWorkCases3")),
			@AttributeOverride(name = "lostWorkDays", column = @Column(name = "lostWorkDays3")),
			@AttributeOverride(name = "injuryIllnessCases", column = @Column(name = "injuryIllnessCases3")),
			@AttributeOverride(name = "restrictedWorkCases", column = @Column(name = "restrictedWorkCases3")),
			@AttributeOverride(name = "recordableTotal", column = @Column(name = "recordableTotal3")),
			@AttributeOverride(name = "verifiedDate", column = @Column(name = "verifiedDate3")),
			@AttributeOverride(name = "comment", column = @Column(name = "comment3")),
			@AttributeOverride(name = "file", column = @Column(name = "file3YearAgo")) })
	public OshaLogYear getYear3() {
		return year3;
	}

	public void setYear3(OshaLogYear year3) {
		this.year3 = year3;
	}

	@Transient
	public float getAverageLwcr() {
		if (countApplicableYears() == 0)
			return 0;
		
		float rate = 0;
		rate = rate + getYear1().getLostWorkCasesRate();
		rate = rate + getYear2().getLostWorkCasesRate();
		rate = rate + getYear3().getLostWorkCasesRate();
		
		return rate/countApplicableYears();
	}

	@Transient
	public float getAverageTrir() {
		if (countApplicableYears() == 0)
			return 0;
		
		float rate = 0;
		rate = rate + getYear1().getRecordableTotalRate();
		rate = rate + getYear2().getRecordableTotalRate();
		rate = rate + getYear3().getRecordableTotalRate();
		
		return rate/countApplicableYears();
	}

	@Transient
	public float getAverageFatalities() {
		if (countApplicableYears() == 0)
			return 0;
		
		float rate = 0;
		rate = rate + getYear1().getFatalitiesRate();
		rate = rate + getYear2().getFatalitiesRate();
		rate = rate + getYear3().getFatalitiesRate();
		
		return rate/countApplicableYears();
	}
	
	@Transient
	private int countApplicableYears() {
		int years = 0;
		if (getYear1().isApplicable())
			years++;
		if (getYear2().isApplicable())
			years++;
		if (getYear3().isApplicable())
			years++;
		return years;
	}

	@Transient
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}
	
	@Transient
	public OshaLogYear getAvg() {
		if (avg == null) {
			avg = new OshaLogYear();
			
			List<OshaLogYear> yearList = new ArrayList<OshaLogYear>();
			if (year1.isApplicable()) yearList.add(year1);
			if (year2.isApplicable()) yearList.add(year2);
			if (year3.isApplicable()) yearList.add(year3);
			
			int years = 0;
			int manHours = 0;
			int fatalities = 0;
			int injuryIllnessCases = 0;
			int lostWorkCases = 0;
			int lostWorkDays = 0;
			int recordableTotal = 0;
			int restrictedWorkCases = 0;
			
			for(OshaLogYear year : yearList) {
				years++;
				manHours += year.getManHours();
				fatalities += year.getFatalities();
				injuryIllnessCases += year.getInjuryIllnessCases();
				lostWorkCases += year.getLostWorkCases();
				lostWorkDays += year.getLostWorkDays();
				recordableTotal += year.getRecordableTotal();
				restrictedWorkCases += year.getRestrictedWorkCases();
			}
			if (years > 0) {
				avg.setManHours(Math.round(manHours/years));
				avg.setFatalities(Math.round(fatalities/years));
				avg.setInjuryIllnessCases(Math.round(injuryIllnessCases/years));
				avg.setLostWorkCases(Math.round(lostWorkCases/years));
				avg.setLostWorkDays(Math.round(lostWorkDays/years));
				avg.setRecordableTotal(Math.round(recordableTotal/years));
				avg.setRestrictedWorkCases(Math.round(restrictedWorkCases/years));
			}
		}
		
		return avg;
	}

}
