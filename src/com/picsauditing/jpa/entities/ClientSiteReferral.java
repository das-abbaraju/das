package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "operator_referral")
public class ClientSiteReferral extends BaseTable implements java.io.Serializable {

	private ContractorAccount source;
	private String sourceContact;
	private String sourceEmail;
	private String sourcePhone;
	private String name;
	private String contact;
	private String email;
	private String phone;
	private OperatorAccount clientSite;
	private ClientSiteReferralStatus status;
	private User lastContactedBy;
	private Date lastContactDate;
	private int contactCountByEmail;
	private int contactCountByPhone;
	private String notes;
	private String reasonForDecline;
	private Date closedOnDate;

	@ManyToOne
	@JoinColumn(name = "sourceID")
	public ContractorAccount getSource() {
		return source;
	}

	public void setSource(ContractorAccount source) {
		this.source = source;
	}

	public String getSourceContact() {
		return sourceContact;
	}

	public void setSourceContact(String sourceContact) {
		this.sourceContact = sourceContact;
	}

	public String getSourceEmail() {
		return sourceEmail;
	}

	public void setSourceEmail(String sourceEmail) {
		this.sourceEmail = sourceEmail;
	}

	public String getSourcePhone() {
		return sourcePhone;
	}

	public void setSourcePhone(String sourcePhone) {
		this.sourcePhone = sourcePhone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ClientSiteReferralStatus getStatus() {
		return status;
	}

	public void setStatus(ClientSiteReferralStatus status) {
		this.status = status;
	}

	@ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getClientSite() {
		return clientSite;
	}

	public void setClientSite(OperatorAccount clientSite) {
		this.clientSite = clientSite;
	}

	@ManyToOne
	@JoinColumn(name = "lastContactedBy")
	public User getLastContactedBy() {
		return lastContactedBy;
	}

	public void setLastContactedBy(User lastContactedBy) {
		this.lastContactedBy = lastContactedBy;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	@Transient
	public int getContactCount() {
		return contactCountByEmail + contactCountByPhone;
	}

	public int getContactCountByEmail() {
		return contactCountByEmail;
	}

	public void setContactCountByEmail(int contactCountByEmail) {
		this.contactCountByEmail = contactCountByEmail;
	}

	public int getContactCountByPhone() {
		return contactCountByPhone;
	}

	public void setContactCountByPhone(int contactCountByPhone) {
		this.contactCountByPhone = contactCountByPhone;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getReasonForDecline() {
		return reasonForDecline;
	}

	public void setReasonForDecline(String reasonForDecline) {
		this.reasonForDecline = reasonForDecline;
	}

	public Date getClosedOnDate() {
		return closedOnDate;
	}

	public void setClosedOnDate(Date closedOnDate) {
		this.closedOnDate = closedOnDate;
	}

	@Transient
	public void contactByEmail() {
		contactCountByEmail++;
	}

	@Transient
	public void contactByPhone() {
		contactCountByPhone++;
	}
}