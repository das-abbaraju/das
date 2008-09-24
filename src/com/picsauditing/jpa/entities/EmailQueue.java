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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "email_queue")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class EmailQueue implements java.io.Serializable {
	private int id;
	private EmailStatus status = EmailStatus.Pending;
	private String fromAddress = "";
	private String toAddresses = "";
	private String ccAddresses;
	private String bccAddresses;
	private String subject = "";
	private String body;
	private int priority = 50;
	private Date creationDate;
	private User createdBy;
	private Date sentDate;
	private EmailTemplate emailTemplate;
	private ContractorAccount contractorAccount;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "emailID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "status", nullable = false)
	public EmailStatus getStatus() {
		return status;
	}

	public void setStatus(EmailStatus status) {
		this.status = status;
	}

	@Column(name = "fromAddress", nullable = false, length = 150)
	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	@Column(name = "toAddresses", nullable = false, length = 1000)
	public String getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(String toAddresses) {
		this.toAddresses = toAddresses;
	}

	@Column(name = "ccAddresses", length = 1000)
	public String getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(String ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	@Column(name = "bccAddresses", length = 1000)
	public String getBccAddresses() {
		return bccAddresses;
	}

	public void setBccAddresses(String bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	@Column(name = "subject", length = 150, nullable = false)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "body")
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Column(name = "priority", nullable = false, length = 4)
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Temporal(TemporalType.DATE)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdBy")
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.DATE)
	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "templateID")
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conID")
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
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
		final EmailQueue other = (EmailQueue) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
