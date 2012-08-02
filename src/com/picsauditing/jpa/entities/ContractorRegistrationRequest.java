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
@Table(name = "contractor_registration_request")
public class ContractorRegistrationRequest extends BaseTable implements java.io.Serializable {
	private String name;
	private OperatorAccount requestedBy;
	private User requestedByUser;
	private String requestedByUserOther;
	private String contact;
	private String phone;
	private String email;
	private String taxID;
	private String address;
	private String city;
	private CountrySubdivision countrySubdivision;
	private String zip;
	private Country country;
	private String reasonForRegistration;
	private ContractorRegistrationRequestStatus status;
	private String reasonForDecline;
	private Date deadline;
	private Date holdDate;
	private User lastContactedBy;
	private Date lastContactDate;
	private int contactCountByEmail;
	private int contactCountByPhone;
	private int matchCount;
	private String notes;

	private ContractorAccount contractor;

	private String operatorTags;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name = "requestedByID")
	public OperatorAccount getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(OperatorAccount requestedBy) {
		this.requestedBy = requestedBy;
	}

	@ManyToOne
	@JoinColumn(name = "requestedByUserID")
	public User getRequestedByUser() {
		return requestedByUser;
	}

	public void setRequestedByUser(User requestedByUser) {
		this.requestedByUser = requestedByUser;
	}

	@Column(name = "requestedByUser")
	public String getRequestedByUserOther() {
		return requestedByUserOther;
	}

	public void setRequestedByUserOther(String requestedByUserOther) {
		this.requestedByUserOther = requestedByUserOther;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(nullable = false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTaxID() {
		return taxID;
	}

	public void setTaxID(String taxID) {
		this.taxID = taxID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@ManyToOne
	@JoinColumn(name = "countrySubdivision")
	public CountrySubdivision getCountrySubdivision() {
		return countrySubdivision;
	}

	public void setCountrySubdivision(CountrySubdivision countrySubdivision) {
		this.countrySubdivision = countrySubdivision;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@ManyToOne
	@JoinColumn(name = "country", nullable = false)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getReasonForRegistration() {
		return reasonForRegistration;
	}

	public void setReasonForRegistration(String reasonForRegistration) {
		this.reasonForRegistration = reasonForRegistration;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ContractorRegistrationRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ContractorRegistrationRequestStatus status) {
		this.status = status;
	}

	public String getReasonForDecline() {
		return reasonForDecline;
	}

	public void setReasonForDecline(String reasonForDecline) {
		this.reasonForDecline = reasonForDecline;
	}

	public Date getHoldDate() {
		return holdDate;
	}

	public void setHoldDate(Date holdDate) {
		this.holdDate = holdDate;
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

	public int getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount con) {
		this.contractor = con;
	}

	public String getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(String operatorTags) {
		this.operatorTags = operatorTags;
	}

	@Transient
	public String getRequestedByUserString() {
		return requestedByUser == null ? getRequestedByUserOther() : requestedByUser.getName();
	}

	@Transient
	public void contactByEmail() {
		contactCountByEmail++;
	}

	@Transient
	public void contactByPhone() {
		contactCountByPhone++;
	}

	@Transient
	public String getRegistrationLink() {
		return "http://www.picsorganizer.com/Registration.action?button=" + "request&requestID=" + getId();
	}

}