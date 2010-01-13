package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_registration_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorRegistrationRequest extends BaseTable implements java.io.Serializable {
	private String name;
	private OperatorAccount requestedBy;
	private User requestedByUser;
	private String requestedByUserOther;
	private WaitingOn handledBy = WaitingOn.PICS;
	private boolean open = true;
	private String contact;
	private String phone;
	private String email;
	private String taxID;
	private String address;
	private String city;
	private State state;
	private String zip;
	private Country country;
	private Date deadline;
	private User lastContactedBy;
	private Date lastContactDate;
	private int contactCount;
	private int matchCount;
	private String notes;
	private ContractorAccount contractor;

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

	@Column(name="requestedByUser")
	public String getRequestedByUserOther() {
		return requestedByUserOther;
	}

	public void setRequestedByUserOther(String requestedByUserOther) {
		this.requestedByUserOther = requestedByUserOther;
	}

	@Enumerated(EnumType.STRING)
	public WaitingOn getHandledBy() {
		return handledBy;
	}

	public void setHandledBy(WaitingOn handledBy) {
		this.handledBy = handledBy;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
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
	@JoinColumn(name = "state")
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
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

	public int getContactCount() {
		return contactCount;
	}

	public void setContactCount(int contactCount) {
		this.contactCount = contactCount;
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
}