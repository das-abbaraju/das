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
public class OshaLogReport implements java.io.Serializable {

	private Short oid;
	private ContractorAccount contractorInfo;
	private String location;
	private String verifiedDate;
	private Date verifiedDate1;
	private Date verifiedDate2;
	private Date verifiedDate3;

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
	public ContractorAccount getContractorInfo() {
		return this.contractorInfo;
	}

	public void setContractorInfo(ContractorAccount contractorInfo) {
		this.contractorInfo = contractorInfo;
	}

	@Column(name = "location", nullable = false, length = 100)
	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(name = "verifiedDate", nullable = false, length = 100)
	public String getVerifiedDate() {
		return this.verifiedDate;
	}

	public void setVerifiedDate(String verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "verifiedDate1", nullable = false, length = 10)
	public Date getVerifiedDate1() {
		return this.verifiedDate1;
	}

	@Temporal(TemporalType.DATE)
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

}
