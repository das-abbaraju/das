package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "osha")
public class OshaLog implements java.io.Serializable {
	private int id;
	private Account contractorAccount;
	private OshaType type;
	private String location;
	private String description;
	private String verifiedDate;
	private int auditorId;

	private int manHours1;
	private int fatalities1;
	private int lostWorkCases1;
	private int lostWorkDays1;
	private int injuryIllnessCases1;
	private int restrictedWorkCases1;
	private int recordableTotal1;
	private YesNo file1yearAgo;
	private Date verifiedDate1;
	private String comment1;
	private YesNo na1;

	private int manHours2;
	private int fatalities2;
	private int lostWorkCases2;
	private int lostWorkDays2;
	private int injuryIllnessCases2;
	private int restrictedWorkCases2;
	private int recordableTotal2;
	private YesNo file2yearAgo;
	private Date verifiedDate2;
	private String comment2;
	private YesNo na2;

	private int manHours3;
	private int fatalities3;
	private int lostWorkCases3;
	private int lostWorkDays3;
	private int injuryIllnessCases3;
	private int restrictedWorkCases3;
	private int injuryDays3;
	private int illnessDays3;
	private int recordableTotal3;
	private YesNo file3yearAgo;
	private Date verifiedDate3;
	private String comment3;
	private YesNo na3;

	private int manHours4;
	private int fatalities4;
	private int lostWorkCases4;
	private int lostWorkDays4;
	private int injuryIllnessCases4;
	private int restrictedWorkCases4;
	private int injuryDays4;
	private int illnessDays4;
	private int recordableTotal4;
	private YesNo file4yearAgo;

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
	public Account getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(Account contractorAccount) {
		this.contractorAccount = contractorAccount;
	}


	@Column(name = "SHAType", nullable = false)
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(String verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	public int getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(int auditorId) {
		this.auditorId = auditorId;
	}

	public int getManHours1() {
		return manHours1;
	}

	public void setManHours1(int manHours1) {
		this.manHours1 = manHours1;
	}

	public int getFatalities1() {
		return fatalities1;
	}

	public void setFatalities1(int fatalities1) {
		this.fatalities1 = fatalities1;
	}

	public int getLostWorkCases1() {
		return lostWorkCases1;
	}

	public void setLostWorkCases1(int lostWorkCases1) {
		this.lostWorkCases1 = lostWorkCases1;
	}

	public int getLostWorkDays1() {
		return lostWorkDays1;
	}

	public void setLostWorkDays1(int lostWorkDays1) {
		this.lostWorkDays1 = lostWorkDays1;
	}

	public int getInjuryIllnessCases1() {
		return injuryIllnessCases1;
	}

	public void setInjuryIllnessCases1(int injuryIllnessCases1) {
		this.injuryIllnessCases1 = injuryIllnessCases1;
	}

	public int getRestrictedWorkCases1() {
		return restrictedWorkCases1;
	}

	public void setRestrictedWorkCases1(int restrictedWorkCases1) {
		this.restrictedWorkCases1 = restrictedWorkCases1;
	}

	public int getRecordableTotal1() {
		return recordableTotal1;
	}

	public void setRecordableTotal1(int recordableTotal1) {
		this.recordableTotal1 = recordableTotal1;
	}

	public YesNo getFile1yearAgo() {
		return file1yearAgo;
	}

	public void setFile1yearAgo(YesNo file1yearAgo) {
		this.file1yearAgo = file1yearAgo;
	}

	public Date getVerifiedDate1() {
		return verifiedDate1;
	}

	public void setVerifiedDate1(Date verifiedDate1) {
		this.verifiedDate1 = verifiedDate1;
	}

	public String getComment1() {
		return comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public YesNo getNa1() {
		return na1;
	}

	public void setNa1(YesNo na1) {
		this.na1 = na1;
	}

	public int getManHours2() {
		return manHours2;
	}

	public void setManHours2(int manHours2) {
		this.manHours2 = manHours2;
	}

	public int getFatalities2() {
		return fatalities2;
	}

	public void setFatalities2(int fatalities2) {
		this.fatalities2 = fatalities2;
	}

	public int getLostWorkCases2() {
		return lostWorkCases2;
	}

	public void setLostWorkCases2(int lostWorkCases2) {
		this.lostWorkCases2 = lostWorkCases2;
	}

	public int getLostWorkDays2() {
		return lostWorkDays2;
	}

	public void setLostWorkDays2(int lostWorkDays2) {
		this.lostWorkDays2 = lostWorkDays2;
	}

	public int getInjuryIllnessCases2() {
		return injuryIllnessCases2;
	}

	public void setInjuryIllnessCases2(int injuryIllnessCases2) {
		this.injuryIllnessCases2 = injuryIllnessCases2;
	}

	public int getRestrictedWorkCases2() {
		return restrictedWorkCases2;
	}

	public void setRestrictedWorkCases2(int restrictedWorkCases2) {
		this.restrictedWorkCases2 = restrictedWorkCases2;
	}

	public int getRecordableTotal2() {
		return recordableTotal2;
	}

	public void setRecordableTotal2(int recordableTotal2) {
		this.recordableTotal2 = recordableTotal2;
	}

	public YesNo getFile2yearAgo() {
		return file2yearAgo;
	}

	public void setFile2yearAgo(YesNo file2yearAgo) {
		this.file2yearAgo = file2yearAgo;
	}

	public Date getVerifiedDate2() {
		return verifiedDate2;
	}

	public void setVerifiedDate2(Date verifiedDate2) {
		this.verifiedDate2 = verifiedDate2;
	}

	public String getComment2() {
		return comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public YesNo getNa2() {
		return na2;
	}

	public void setNa2(YesNo na2) {
		this.na2 = na2;
	}

	public int getManHours3() {
		return manHours3;
	}

	public void setManHours3(int manHours3) {
		this.manHours3 = manHours3;
	}

	public int getFatalities3() {
		return fatalities3;
	}

	public void setFatalities3(int fatalities3) {
		this.fatalities3 = fatalities3;
	}

	public int getLostWorkCases3() {
		return lostWorkCases3;
	}

	public void setLostWorkCases3(int lostWorkCases3) {
		this.lostWorkCases3 = lostWorkCases3;
	}

	public int getLostWorkDays3() {
		return lostWorkDays3;
	}

	public void setLostWorkDays3(int lostWorkDays3) {
		this.lostWorkDays3 = lostWorkDays3;
	}

	public int getInjuryIllnessCases3() {
		return injuryIllnessCases3;
	}

	public void setInjuryIllnessCases3(int injuryIllnessCases3) {
		this.injuryIllnessCases3 = injuryIllnessCases3;
	}

	public int getRestrictedWorkCases3() {
		return restrictedWorkCases3;
	}

	public void setRestrictedWorkCases3(int restrictedWorkCases3) {
		this.restrictedWorkCases3 = restrictedWorkCases3;
	}

	public int getInjuryDays3() {
		return injuryDays3;
	}

	public void setInjuryDays3(int injuryDays3) {
		this.injuryDays3 = injuryDays3;
	}

	public int getIllnessDays3() {
		return illnessDays3;
	}

	public void setIllnessDays3(int illnessDays3) {
		this.illnessDays3 = illnessDays3;
	}

	public int getRecordableTotal3() {
		return recordableTotal3;
	}

	public void setRecordableTotal3(int recordableTotal3) {
		this.recordableTotal3 = recordableTotal3;
	}

	public YesNo getFile3yearAgo() {
		return file3yearAgo;
	}

	public void setFile3yearAgo(YesNo file3yearAgo) {
		this.file3yearAgo = file3yearAgo;
	}

	public Date getVerifiedDate3() {
		return verifiedDate3;
	}

	public void setVerifiedDate3(Date verifiedDate3) {
		this.verifiedDate3 = verifiedDate3;
	}

	public String getComment3() {
		return comment3;
	}

	public void setComment3(String comment3) {
		this.comment3 = comment3;
	}

	public YesNo getNa3() {
		return na3;
	}

	public void setNa3(YesNo na3) {
		this.na3 = na3;
	}

	public int getManHours4() {
		return manHours4;
	}

	public void setManHours4(int manHours4) {
		this.manHours4 = manHours4;
	}

	public int getFatalities4() {
		return fatalities4;
	}

	public void setFatalities4(int fatalities4) {
		this.fatalities4 = fatalities4;
	}

	public int getLostWorkCases4() {
		return lostWorkCases4;
	}

	public void setLostWorkCases4(int lostWorkCases4) {
		this.lostWorkCases4 = lostWorkCases4;
	}

	public int getLostWorkDays4() {
		return lostWorkDays4;
	}

	public void setLostWorkDays4(int lostWorkDays4) {
		this.lostWorkDays4 = lostWorkDays4;
	}

	public int getInjuryIllnessCases4() {
		return injuryIllnessCases4;
	}

	public void setInjuryIllnessCases4(int injuryIllnessCases4) {
		this.injuryIllnessCases4 = injuryIllnessCases4;
	}

	public int getRestrictedWorkCases4() {
		return restrictedWorkCases4;
	}

	public void setRestrictedWorkCases4(int restrictedWorkCases4) {
		this.restrictedWorkCases4 = restrictedWorkCases4;
	}

	public int getInjuryDays4() {
		return injuryDays4;
	}

	public void setInjuryDays4(int injuryDays4) {
		this.injuryDays4 = injuryDays4;
	}

	public int getIllnessDays4() {
		return illnessDays4;
	}

	public void setIllnessDays4(int illnessDays4) {
		this.illnessDays4 = illnessDays4;
	}

	public int getRecordableTotal4() {
		return recordableTotal4;
	}

	public void setRecordableTotal4(int recordableTotal4) {
		this.recordableTotal4 = recordableTotal4;
	}

	public YesNo getFile4yearAgo() {
		return file4yearAgo;
	}

	public void setFile4yearAgo(YesNo file4yearAgo) {
		this.file4yearAgo = file4yearAgo;
	}
}
