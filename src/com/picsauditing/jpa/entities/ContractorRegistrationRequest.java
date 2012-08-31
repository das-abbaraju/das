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

import com.picsauditing.util.Strings;

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
	private ContractorRegistrationRequestStatus status = ContractorRegistrationRequestStatus.Active;
	private String reasonForDecline;
	private Date deadline;
	private Date holdDate;
	private User lastContactedBy;
	private Date lastContactDate;
	private Date lastContactedByAutomatedEmailDate;
	private int contactCountByEmail;
	private int contactCountByPhone;
	private int matchCount;
	private String notes;
	private ContractorAccount contractor;
	private String operatorTags;
	private String registrationHash;

	@Deprecated
	public String getName() {
		return name;
	}

	@Deprecated
	public void setName(String name) {
		this.name = name;
	}

	@Deprecated
	@ManyToOne
	@JoinColumn(name = "requestedByID")
	public OperatorAccount getRequestedBy() {
		return requestedBy;
	}

	@Deprecated
	public void setRequestedBy(OperatorAccount requestedBy) {
		this.requestedBy = requestedBy;
	}

	@Deprecated
	@ManyToOne
	@JoinColumn(name = "requestedByUserID")
	public User getRequestedByUser() {
		return requestedByUser;
	}

	@Deprecated
	public void setRequestedByUser(User requestedByUser) {
		this.requestedByUser = requestedByUser;
	}

	@Deprecated
	@Column(name = "requestedByUser")
	public String getRequestedByUserOther() {
		return requestedByUserOther;
	}

	@Deprecated
	public void setRequestedByUserOther(String requestedByUserOther) {
		this.requestedByUserOther = requestedByUserOther;
	}

	@Deprecated
	public String getContact() {
		return contact;
	}

	@Deprecated
	public void setContact(String contact) {
		this.contact = contact;
	}

	@Deprecated
	public String getPhone() {
		return phone;
	}

	@Deprecated
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Deprecated
	@Column(nullable = false)
	public String getEmail() {
		return email;
	}

	@Deprecated
	public void setEmail(String email) {
		this.email = email;
	}

	@Deprecated
	public String getTaxID() {
		return taxID;
	}

	@Deprecated
	public void setTaxID(String taxID) {
		this.taxID = taxID;
	}

	@Deprecated
	public String getAddress() {
		return address;
	}

	@Deprecated
	public void setAddress(String address) {
		this.address = address;
	}

	@Deprecated
	public String getCity() {
		return city;
	}

	@Deprecated
	public void setCity(String city) {
		this.city = city;
	}

	@Deprecated
	@ManyToOne
	@JoinColumn(name = "countrySubdivision")
	public CountrySubdivision getCountrySubdivision() {
		return countrySubdivision;
	}

	@Deprecated
	public void setCountrySubdivision(CountrySubdivision countrySubdivision) {
		this.countrySubdivision = countrySubdivision;
	}

	@Deprecated
	public String getZip() {
		return zip;
	}

	@Deprecated
	public void setZip(String zip) {
		this.zip = zip;
	}

	@Deprecated
	@ManyToOne
	@JoinColumn(name = "country", nullable = false)
	public Country getCountry() {
		return country;
	}

	@Deprecated
	public void setCountry(Country country) {
		this.country = country;
	}

	@Deprecated
	public Date getDeadline() {
		return deadline;
	}

	@Deprecated
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Deprecated
	public String getReasonForRegistration() {
		return reasonForRegistration;
	}

	@Deprecated
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

	@Deprecated
	@ManyToOne
	@JoinColumn(name = "lastContactedBy")
	public User getLastContactedBy() {
		return lastContactedBy;
	}

	@Deprecated
	public void setLastContactedBy(User lastContactedBy) {
		this.lastContactedBy = lastContactedBy;
	}

	@Deprecated
	public Date getLastContactDate() {
		return lastContactDate;
	}

	@Deprecated
	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public Date getLastContactedByAutomatedEmailDate() {
		return lastContactedByAutomatedEmailDate;
	}

	public void setLastContactedByAutomatedEmailDate(Date lastContactedByAutomatedEmailDate) {
		this.lastContactedByAutomatedEmailDate = lastContactedByAutomatedEmailDate;
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

	@Transient
	public int getContactCount() {
		return contactCountByEmail + contactCountByPhone;
	}

	public int getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}

	@Deprecated
	public String getNotes() {
		return notes;
	}

	@Deprecated
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Deprecated
	@Transient
	public void addToNotes(String note, User user) {
		setNotes(String.format("%tD - %s - %s\n\n%s", new Date(), user.getName(), note,
				Strings.isEmpty(getNotes()) ? "" : getNotes()));
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount con) {
		this.contractor = con;
	}

	@Deprecated
	public String getOperatorTags() {
		return operatorTags;
	}

	@Deprecated
	public void setOperatorTags(String operatorTags) {
		this.operatorTags = operatorTags;
	}

	public String getRegistrationHash() {
		return registrationHash;
	}

	public void setRegistrationHash(String registrationHash) {
		this.registrationHash = registrationHash;
	}

	@Transient
	public void generateHash() {
		Date now = new Date();
		setRegistrationHash(Strings.hash("request" + getId() + now.toString()));
	}

	@Deprecated
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
}
