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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.builders.EmailQueueBuilder;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.util.StringUtils;

import com.ibm.icu.util.StringTokenizer;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@JsonAutoDetect(JsonMethod.FIELD)
@Entity
@Table(name = "email_queue")
public class EmailQueue implements java.io.Serializable {
	public static final int HIGH_PRIORITY = 70;
	private int id;
	private EmailStatus status = EmailStatus.Pending;
	private String fromAddress = "";
	private String fromPassword = null;
	private String toAddresses = "";
	private String ccAddresses;
	private String bccAddresses;
	private String subject = "";
	private String body = "";
	private int priority = 20;
	private Date creationDate = new Date();
	private User createdBy;
	private Date sentDate;
	private EmailTemplate emailTemplate;
	private ContractorAccount contractorAccount;
	private boolean html = false;
	private Account bodyViewableBy;
	private Account subjectViewableBy;

	@JsonProperty
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

	@JsonProperty
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

	@JsonProperty
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

	@JsonProperty
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

	@JsonProperty
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

	@JsonProperty
	@Column(length = 150, nullable = false)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@JsonProperty
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Higher priority numbers are sent first by the MailCron
	 * 
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

	@ManyToOne
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

	@ManyToOne
	@JoinColumn(name = "templateID", nullable = true)
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

	@ManyToOne
	@JoinColumn(name = "bodyViewableBy")
	public Account getBodyViewableBy() {
		return bodyViewableBy;
	}

	public void setBodyViewableBy(Account bodyViewableBy) {
		this.bodyViewableBy = bodyViewableBy;
	}

	public void setBodyViewableById(int bodyViewableBy) {
		if (bodyViewableBy == 0)
			this.bodyViewableBy = null;
		this.bodyViewableBy = new Account();
		this.bodyViewableBy.setId(bodyViewableBy);
	}

	@ManyToOne
	@JoinColumn(name = "subjectViewableBy")
	public Account getSubjectViewableBy() {
		return subjectViewableBy;
	}

	public void setSubjectViewableBy(Account subjectViewableBy) {
		this.subjectViewableBy = subjectViewableBy;
	}

	public void setSubjectViewableById(int subjectViewableByID) {
		if (subjectViewableByID == 0) {
			this.subjectViewableBy = null;
		}
		this.subjectViewableBy = new Account();
		this.subjectViewableBy.setId(subjectViewableByID);
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

	public void cleanupEmailAddresses() {
		toAddresses = cleanupEmailAddress(toAddresses);
		fromAddress = cleanupEmailAddress(fromAddress);
		ccAddresses = cleanupEmailAddress(ccAddresses);
		bccAddresses = cleanupEmailAddress(bccAddresses);
	}

	public String cleanupEmailAddress(String addresses) {
		if (addresses == null)
			return null;
		
		StringTokenizer st = new StringTokenizer(addresses, ",");
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens()) {
			String address = st.nextToken();

			address = StringUtils.trimAllWhitespace(address);
			address = StringUtils.trimLeadingCharacter(address, '[');
			address = StringUtils.trimTrailingCharacter(address, ']');
			address = StringUtils.delete(address, "mailto:");
			sb.append(address).append(",");
		}
		String returnAddress = StringUtils.trimTrailingCharacter(sb.toString(), ',');
		return returnAddress;
	}

	@JsonProperty
	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public void setCriticalPriority()
	{
		priority = 100;
	}
	
	public void setVeryHighPriority()
	{
		priority = 90;
	}
	
	public void setHighPriority()
	{
		priority = HIGH_PRIORITY;
	}
	
	public void setMediumPriority()
	{
		priority = 50;
	}
	
	public void setLowPriority()
	{
		priority = 30;
	}
	
	public void setVeryLowPriority()
	{
		priority = 10;
	}

    public static EmailQueueBuilder builder() {
        return new EmailQueueBuilder();
    }
}
