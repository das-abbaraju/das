package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "email_queue")
public class EmailQueue implements java.io.Serializable {
	private int id;
	private EmailStatus status = EmailStatus.Pending;
	private String fromAddress = "";
	private String fromPassword = null;
	private String toAddresses = "";
	private String ccAddresses;
	private String bccAddresses;
	private String subject = "";
	private String body = "";
	private int priority = 50;
	private Date creationDate = new Date();
	private User createdBy;
	private Date sentDate;
	private EmailTemplate emailTemplate;
	private ContractorAccount contractorAccount;
	private boolean html = false;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "emailID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public EmailStatus getStatus() {
		return status;
	}

	public void setStatus(EmailStatus status) {
		this.status = status;
	}

	@Column(length = 150)
	public String getFromAddress() {
		return fromAddress;
	}
	
	@Transient
	public InternetAddress getFromAddress2() throws AddressException {
		if (Strings.isEmpty(fromAddress))
			return null;
		return new InternetAddress(fromAddress);
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	
	@Column(length = 50)
	public String getFromPassword() {
		return fromPassword;
	}

	public void setFromPassword(String fromPassword) {
		this.fromPassword = fromPassword;
	}

	@Column(nullable = false, length = 1000)
	public String getToAddresses() {
		return toAddresses;
	}

	@Transient
	public Address[] getToAddresses2() throws AddressException {
		if (Strings.isEmpty(toAddresses))
			return null;
		return InternetAddress.parse(toAddresses);
	}

	public void setToAddresses(String toAddresses) {
		this.toAddresses = toAddresses;
	}

	@Column(length = 1000)
	public String getCcAddresses() {
		return ccAddresses;
	}

	@Transient
	public Address[] getCcAddresses2() throws AddressException {
		if (Strings.isEmpty(ccAddresses))
			return null;
		return InternetAddress.parse(ccAddresses);
	}

	public void setCcAddresses(String ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	@Column(length = 1000)
	public String getBccAddresses() {
		return bccAddresses;
	}

	@Transient
	public Address[] getBccAddresses2() throws AddressException {
		if (Strings.isEmpty(bccAddresses))
			return null;
		return InternetAddress.parse(bccAddresses);
	}

	public void setBccAddresses(String bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	@Column(length = 150, nullable = false)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Higher priority numbers are sent first by the MailCron
	 * @return
	 */
	@Column(nullable = false, length = 4)
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "templateID", nullable=true)
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

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

}
