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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "certificates")
public class Certificate {
	protected int id;
	protected ContractorAccount contractorAccount;
	protected OperatorAccount operatorAccount;
	protected String type;
	protected Date expiration;
	protected int sentEmails;
	protected Date lastSentDate;
	protected int liabilityLimit;
	protected String namedInsured;
	protected YesNo subrogationWaived;
	protected String status;
	protected YesNo verified;
	protected String reason;
	protected String fileExtension;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "cert_id", nullable = false, insertable = false, updatable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contractor_id", nullable = false, updatable = false)
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator_id", nullable = false, updatable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "expDate")
	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	@Column(name = "sent")
	public int getSentEmails() {
		return sentEmails;
	}

	public void setSentEmails(int sentEmails) {
		this.sentEmails = sentEmails;
	}

	@Temporal(TemporalType.DATE)
	public Date getLastSentDate() {
		return lastSentDate;
	}

	public void setLastSentDate(Date lastSentDate) {
		this.lastSentDate = lastSentDate;
	}

	public int getLiabilityLimit() {
		return liabilityLimit;
	}

	public void setLiabilityLimit(int liabilityLimit) {
		this.liabilityLimit = liabilityLimit;
	}

	public String getNamedInsured() {
		return namedInsured;
	}

	public void setNamedInsured(String namedInsured) {
		this.namedInsured = namedInsured;
	}

	public YesNo getSubrogationWaived() {
		return subrogationWaived;
	}

	public void setSubrogationWaived(YesNo subrogationWaived) {
		this.subrogationWaived = subrogationWaived;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public YesNo getVerified() {
		return verified;
	}

	public void setVerified(YesNo verified) {
		this.verified = verified;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "ext")
	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	@Transient
	public FlagColor getFlagColor() {
		if (status == null)
			return null;
		if (status.equals("Approved")) {
			return FlagColor.Green;
		}
		if (status.equals("Expired")) {
			return FlagColor.Amber;
		}
		if (status.equals("Rejected")) {
			return FlagColor.Red;
		}
		return null;
	}
}
