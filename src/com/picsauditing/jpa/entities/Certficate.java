package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "certificates")
public class Certficate {
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
	protected String Status;
	protected YesNo verified;
	protected String reason;
	protected String fileExtension;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

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

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public int getSentEmails() {
		return sentEmails;
	}

	public void setSentEmails(int sentEmails) {
		this.sentEmails = sentEmails;
	}

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
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
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

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

}
