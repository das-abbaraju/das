package com.picsauditing.jpa.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "osha")
public class OshaLog implements java.io.Serializable {
	private Short oid;
	private ContractorInfo contractorInfo;
	private String shatype;
	private String location;
	private String description;
	private int manHours1;
	private byte fatalities1;
	private short lostWorkCases1;
	private short lostWorkDays1;
	private short injuryIllnessCases1;
	private short restrictedWorkCases1;
	private short recordableTotal1;
	private int manHours2;
	private byte fatalities2;
	private short lostWorkCases2;
	private short lostWorkDays2;
	private short injuryIllnessCases2;
	private short restrictedWorkCases2;
	private short recordableTotal2;
	private int manHours3;
	private byte fatalities3;
	private short lostWorkCases3;
	private short lostWorkDays3;
	private short injuryIllnessCases3;
	private short restrictedWorkCases3;
	private short injuryDays3;
	private short illnessDays3;
	private short recordableTotal3;
	private int manHours4;
	private byte fatalities4;
	private short lostWorkCases4;
	private short lostWorkDays4;
	private short injuryIllnessCases4;
	private short restrictedWorkCases4;
	private short injuryDays4;
	private short illnessDays4;
	private short recordableTotal4;
	private int manHours5;
	private byte fatalities5;
	private short lostWorkCases5;
	private short lostWorkDays5;
	private short injuryIllnessCases5;
	private short restrictedWorkCases5;
	private short injuryDays5;
	private short illnessDays5;
	private short recordableTotal5;
	private int manHours6;
	private byte fatalities6;
	private short injuryDays6;
	private short illnessDays6;
	private short recordableTotal6;
	private String file1yearAgo;
	private String file2yearAgo;
	private String file3yearAgo;
	private String file4yearAgo;
	private String file5yearAgo;
	private String file6yearAgo;
	private String verifiedDate;
	private Short auditorId;
	private Date verifiedDate1;
	private Date verifiedDate2;
	private Date verifiedDate3;
	private String comment1;
	private String comment2;
	private String comment3;
	private String na1;
	private String na2;
	private String na3;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "OID", nullable = false)
	public Short getOid() {
		return this.oid;
	}

	public void setOid(Short oid) {
		this.oid = oid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conID", nullable = false)
	public ContractorInfo getContractorInfo() {
		return this.contractorInfo;
	}

	public void setContractorInfo(ContractorInfo contractorInfo) {
		this.contractorInfo = contractorInfo;
	}

	@Column(name = "SHAType", nullable = false, length = 5)
	public String getShatype() {
		return this.shatype;
	}

	public void setShatype(String shatype) {
		this.shatype = shatype;
	}

	@Column(name = "location", nullable = false, length = 100)
	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(name = "description", nullable = false, length = 250)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "manHours1", nullable = false)
	public int getManHours1() {
		return this.manHours1;
	}

	public void setManHours1(int manHours1) {
		this.manHours1 = manHours1;
	}

	@Column(name = "fatalities1", nullable = false)
	public byte getFatalities1() {
		return this.fatalities1;
	}

	public void setFatalities1(byte fatalities1) {
		this.fatalities1 = fatalities1;
	}

	@Column(name = "lostWorkCases1", nullable = false)
	public short getLostWorkCases1() {
		return this.lostWorkCases1;
	}

	public void setLostWorkCases1(short lostWorkCases1) {
		this.lostWorkCases1 = lostWorkCases1;
	}

	@Column(name = "lostWorkDays1", nullable = false)
	public short getLostWorkDays1() {
		return this.lostWorkDays1;
	}

	public void setLostWorkDays1(short lostWorkDays1) {
		this.lostWorkDays1 = lostWorkDays1;
	}

	@Column(name = "InjuryIllnessCases1", nullable = false)
	public short getInjuryIllnessCases1() {
		return this.injuryIllnessCases1;
	}

	public void setInjuryIllnessCases1(short injuryIllnessCases1) {
		this.injuryIllnessCases1 = injuryIllnessCases1;
	}

	@Column(name = "restrictedWorkCases1", nullable = false)
	public short getRestrictedWorkCases1() {
		return this.restrictedWorkCases1;
	}

	public void setRestrictedWorkCases1(short restrictedWorkCases1) {
		this.restrictedWorkCases1 = restrictedWorkCases1;
	}

	@Column(name = "recordableTotal1", nullable = false)
	public short getRecordableTotal1() {
		return this.recordableTotal1;
	}

	public void setRecordableTotal1(short recordableTotal1) {
		this.recordableTotal1 = recordableTotal1;
	}

	@Column(name = "manHours2", nullable = false)
	public int getManHours2() {
		return this.manHours2;
	}

	public void setManHours2(int manHours2) {
		this.manHours2 = manHours2;
	}

	@Column(name = "fatalities2", nullable = false)
	public byte getFatalities2() {
		return this.fatalities2;
	}

	public void setFatalities2(byte fatalities2) {
		this.fatalities2 = fatalities2;
	}

	@Column(name = "lostWorkCases2", nullable = false)
	public short getLostWorkCases2() {
		return this.lostWorkCases2;
	}

	public void setLostWorkCases2(short lostWorkCases2) {
		this.lostWorkCases2 = lostWorkCases2;
	}

	@Column(name = "lostWorkDays2", nullable = false)
	public short getLostWorkDays2() {
		return this.lostWorkDays2;
	}

	public void setLostWorkDays2(short lostWorkDays2) {
		this.lostWorkDays2 = lostWorkDays2;
	}

	@Column(name = "InjuryIllnessCases2", nullable = false)
	public short getInjuryIllnessCases2() {
		return this.injuryIllnessCases2;
	}

	public void setInjuryIllnessCases2(short injuryIllnessCases2) {
		this.injuryIllnessCases2 = injuryIllnessCases2;
	}

	@Column(name = "restrictedWorkCases2", nullable = false)
	public short getRestrictedWorkCases2() {
		return this.restrictedWorkCases2;
	}

	public void setRestrictedWorkCases2(short restrictedWorkCases2) {
		this.restrictedWorkCases2 = restrictedWorkCases2;
	}

	@Column(name = "recordableTotal2", nullable = false)
	public short getRecordableTotal2() {
		return this.recordableTotal2;
	}

	public void setRecordableTotal2(short recordableTotal2) {
		this.recordableTotal2 = recordableTotal2;
	}

	@Column(name = "manHours3", nullable = false)
	public int getManHours3() {
		return this.manHours3;
	}

	public void setManHours3(int manHours3) {
		this.manHours3 = manHours3;
	}

	@Column(name = "fatalities3", nullable = false)
	public byte getFatalities3() {
		return this.fatalities3;
	}

	public void setFatalities3(byte fatalities3) {
		this.fatalities3 = fatalities3;
	}

	@Column(name = "lostWorkCases3", nullable = false)
	public short getLostWorkCases3() {
		return this.lostWorkCases3;
	}

	public void setLostWorkCases3(short lostWorkCases3) {
		this.lostWorkCases3 = lostWorkCases3;
	}

	@Column(name = "lostWorkDays3", nullable = false)
	public short getLostWorkDays3() {
		return this.lostWorkDays3;
	}

	public void setLostWorkDays3(short lostWorkDays3) {
		this.lostWorkDays3 = lostWorkDays3;
	}

	@Column(name = "InjuryIllnessCases3", nullable = false)
	public short getInjuryIllnessCases3() {
		return this.injuryIllnessCases3;
	}

	public void setInjuryIllnessCases3(short injuryIllnessCases3) {
		this.injuryIllnessCases3 = injuryIllnessCases3;
	}

	@Column(name = "restrictedWorkCases3", nullable = false)
	public short getRestrictedWorkCases3() {
		return this.restrictedWorkCases3;
	}

	public void setRestrictedWorkCases3(short restrictedWorkCases3) {
		this.restrictedWorkCases3 = restrictedWorkCases3;
	}

	@Column(name = "injuryDays3", nullable = false)
	public short getInjuryDays3() {
		return this.injuryDays3;
	}

	public void setInjuryDays3(short injuryDays3) {
		this.injuryDays3 = injuryDays3;
	}

	@Column(name = "illnessDays3", nullable = false)
	public short getIllnessDays3() {
		return this.illnessDays3;
	}

	public void setIllnessDays3(short illnessDays3) {
		this.illnessDays3 = illnessDays3;
	}

	@Column(name = "recordableTotal3", nullable = false)
	public short getRecordableTotal3() {
		return this.recordableTotal3;
	}

	public void setRecordableTotal3(short recordableTotal3) {
		this.recordableTotal3 = recordableTotal3;
	}

	@Column(name = "manHours4", nullable = false)
	public int getManHours4() {
		return this.manHours4;
	}

	public void setManHours4(int manHours4) {
		this.manHours4 = manHours4;
	}

	@Column(name = "fatalities4", nullable = false)
	public byte getFatalities4() {
		return this.fatalities4;
	}

	public void setFatalities4(byte fatalities4) {
		this.fatalities4 = fatalities4;
	}

	@Column(name = "lostWorkCases4", nullable = false)
	public short getLostWorkCases4() {
		return this.lostWorkCases4;
	}

	public void setLostWorkCases4(short lostWorkCases4) {
		this.lostWorkCases4 = lostWorkCases4;
	}

	@Column(name = "lostWorkDays4", nullable = false)
	public short getLostWorkDays4() {
		return this.lostWorkDays4;
	}

	public void setLostWorkDays4(short lostWorkDays4) {
		this.lostWorkDays4 = lostWorkDays4;
	}

	@Column(name = "InjuryIllnessCases4", nullable = false)
	public short getInjuryIllnessCases4() {
		return this.injuryIllnessCases4;
	}

	public void setInjuryIllnessCases4(short injuryIllnessCases4) {
		this.injuryIllnessCases4 = injuryIllnessCases4;
	}

	@Column(name = "restrictedWorkCases4", nullable = false)
	public short getRestrictedWorkCases4() {
		return this.restrictedWorkCases4;
	}

	public void setRestrictedWorkCases4(short restrictedWorkCases4) {
		this.restrictedWorkCases4 = restrictedWorkCases4;
	}

	@Column(name = "injuryDays4", nullable = false)
	public short getInjuryDays4() {
		return this.injuryDays4;
	}

	public void setInjuryDays4(short injuryDays4) {
		this.injuryDays4 = injuryDays4;
	}

	@Column(name = "illnessDays4", nullable = false)
	public short getIllnessDays4() {
		return this.illnessDays4;
	}

	public void setIllnessDays4(short illnessDays4) {
		this.illnessDays4 = illnessDays4;
	}

	@Column(name = "recordableTotal4", nullable = false)
	public short getRecordableTotal4() {
		return this.recordableTotal4;
	}

	public void setRecordableTotal4(short recordableTotal4) {
		this.recordableTotal4 = recordableTotal4;
	}

	@Column(name = "manHours5", nullable = false)
	public int getManHours5() {
		return this.manHours5;
	}

	public void setManHours5(int manHours5) {
		this.manHours5 = manHours5;
	}

	@Column(name = "fatalities5", nullable = false)
	public byte getFatalities5() {
		return this.fatalities5;
	}

	public void setFatalities5(byte fatalities5) {
		this.fatalities5 = fatalities5;
	}

	@Column(name = "lostWorkCases5", nullable = false)
	public short getLostWorkCases5() {
		return this.lostWorkCases5;
	}

	public void setLostWorkCases5(short lostWorkCases5) {
		this.lostWorkCases5 = lostWorkCases5;
	}

	@Column(name = "lostWorkDays5", nullable = false)
	public short getLostWorkDays5() {
		return this.lostWorkDays5;
	}

	public void setLostWorkDays5(short lostWorkDays5) {
		this.lostWorkDays5 = lostWorkDays5;
	}

	@Column(name = "InjuryIllnessCases5", nullable = false)
	public short getInjuryIllnessCases5() {
		return this.injuryIllnessCases5;
	}

	public void setInjuryIllnessCases5(short injuryIllnessCases5) {
		this.injuryIllnessCases5 = injuryIllnessCases5;
	}

	@Column(name = "restrictedWorkCases5", nullable = false)
	public short getRestrictedWorkCases5() {
		return this.restrictedWorkCases5;
	}

	public void setRestrictedWorkCases5(short restrictedWorkCases5) {
		this.restrictedWorkCases5 = restrictedWorkCases5;
	}

	@Column(name = "injuryDays5", nullable = false)
	public short getInjuryDays5() {
		return this.injuryDays5;
	}

	public void setInjuryDays5(short injuryDays5) {
		this.injuryDays5 = injuryDays5;
	}

	@Column(name = "illnessDays5", nullable = false)
	public short getIllnessDays5() {
		return this.illnessDays5;
	}

	public void setIllnessDays5(short illnessDays5) {
		this.illnessDays5 = illnessDays5;
	}

	@Column(name = "recordableTotal5", nullable = false)
	public short getRecordableTotal5() {
		return this.recordableTotal5;
	}

	public void setRecordableTotal5(short recordableTotal5) {
		this.recordableTotal5 = recordableTotal5;
	}

	@Column(name = "manHours6", nullable = false)
	public int getManHours6() {
		return this.manHours6;
	}

	public void setManHours6(int manHours6) {
		this.manHours6 = manHours6;
	}

	@Column(name = "fatalities6", nullable = false)
	public byte getFatalities6() {
		return this.fatalities6;
	}

	public void setFatalities6(byte fatalities6) {
		this.fatalities6 = fatalities6;
	}

	@Column(name = "injuryDays6", nullable = false)
	public short getInjuryDays6() {
		return this.injuryDays6;
	}

	public void setInjuryDays6(short injuryDays6) {
		this.injuryDays6 = injuryDays6;
	}

	@Column(name = "illnessDays6", nullable = false)
	public short getIllnessDays6() {
		return this.illnessDays6;
	}

	public void setIllnessDays6(short illnessDays6) {
		this.illnessDays6 = illnessDays6;
	}

	@Column(name = "recordableTotal6", nullable = false)
	public short getRecordableTotal6() {
		return this.recordableTotal6;
	}

	public void setRecordableTotal6(short recordableTotal6) {
		this.recordableTotal6 = recordableTotal6;
	}

	@Column(name = "file1YearAgo", nullable = false, length = 3)
	public String getFile1yearAgo() {
		return this.file1yearAgo;
	}

	public void setFile1yearAgo(String file1yearAgo) {
		this.file1yearAgo = file1yearAgo;
	}

	@Column(name = "file2YearAgo", nullable = false, length = 3)
	public String getFile2yearAgo() {
		return this.file2yearAgo;
	}

	public void setFile2yearAgo(String file2yearAgo) {
		this.file2yearAgo = file2yearAgo;
	}

	@Column(name = "file3YearAgo", nullable = false, length = 3)
	public String getFile3yearAgo() {
		return this.file3yearAgo;
	}

	public void setFile3yearAgo(String file3yearAgo) {
		this.file3yearAgo = file3yearAgo;
	}

	@Column(name = "file4YearAgo", nullable = false, length = 3)
	public String getFile4yearAgo() {
		return this.file4yearAgo;
	}

	public void setFile4yearAgo(String file4yearAgo) {
		this.file4yearAgo = file4yearAgo;
	}

	@Column(name = "file5YearAgo", nullable = false, length = 3)
	public String getFile5yearAgo() {
		return this.file5yearAgo;
	}

	public void setFile5yearAgo(String file5yearAgo) {
		this.file5yearAgo = file5yearAgo;
	}

	@Column(name = "file6YearAgo", nullable = false, length = 3)
	public String getFile6yearAgo() {
		return this.file6yearAgo;
	}

	public void setFile6yearAgo(String file6yearAgo) {
		this.file6yearAgo = file6yearAgo;
	}

	@Column(name = "verifiedDate", nullable = false, length = 100)
	public String getVerifiedDate() {
		return this.verifiedDate;
	}

	public void setVerifiedDate(String verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	@Column(name = "auditorID")
	public Short getAuditorId() {
		return this.auditorId;
	}

	public void setAuditorId(Short auditorId) {
		this.auditorId = auditorId;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "verifiedDate1", length = 10)
	public Date getVerifiedDate1() {
		return this.verifiedDate1;
	}

	public void setVerifiedDate1(Date verifiedDate1) {
		this.verifiedDate1 = verifiedDate1;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "verifiedDate2", length = 10)
	public Date getVerifiedDate2() {
		return this.verifiedDate2;
	}

	public void setVerifiedDate2(Date verifiedDate2) {
		this.verifiedDate2 = verifiedDate2;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "verifiedDate3", length = 10)
	public Date getVerifiedDate3() {
		return this.verifiedDate3;
	}

	public void setVerifiedDate3(Date verifiedDate3) {
		this.verifiedDate3 = verifiedDate3;
	}

	@Column(name = "comment1", length = 250)
	public String getComment1() {
		return this.comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	@Column(name = "comment2", length = 250)
	public String getComment2() {
		return this.comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	@Column(name = "comment3", length = 250)
	public String getComment3() {
		return this.comment3;
	}

	public void setComment3(String comment3) {
		this.comment3 = comment3;
	}

	@Column(name = "NA1", nullable = false, length = 3)
	public String getNa1() {
		return this.na1;
	}

	public void setNa1(String na1) {
		this.na1 = na1;
	}

	@Column(name = "NA2", nullable = false, length = 3)
	public String getNa2() {
		return this.na2;
	}

	public void setNa2(String na2) {
		this.na2 = na2;
	}

	@Column(name = "NA3", nullable = false, length = 3)
	public String getNa3() {
		return this.na3;
	}

	public void setNa3(String na3) {
		this.na3 = na3;
	}

}
