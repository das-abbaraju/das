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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_registration_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorRegistrationRequest extends BaseTable implements java.io.Serializable {
	private String name = "";
	private OperatorAccount requestedBy;
	private User requestedByUser;
	private String requestedByUserOther;
	private String handledBy = "";
	private boolean open;
	private String contact = "";
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

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "requestID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.EAGER)
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

	@Column(name = "requestedByUser", length = 20)
	public String getRequestedByUserOther() {
		return requestedByUserOther;
	}

	public void setRequestedByUserOther(String requestedByUserOther) {
		this.requestedByUserOther = requestedByUserOther;
	}

	@Column(name = "handledBy", nullable = false, length = 10)
	public String getHandledBy() {
		return handledBy;
	}

	public void setHandledBy(String handledBy) {
		this.handledBy = handledBy;
	}

	@Column(name = "open", length = 4)
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Column(name = "contact", nullable = false, length = 30)
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "phone", length = 20)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "email", length = 50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "taxID", length = 9)
	public String getTaxID() {
		return taxID;
	}

	public void setTaxID(String taxID) {
		this.taxID = taxID;
	}

	@Column(name = "address", length = 100)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "city", length = 50)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@ManyToOne
	@JoinColumn(name = "state", nullable = false)
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Column(name = "zip", length = 10)
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

	@Column(name = "deadline", length = 20)
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Column(name = "lastContactedBy", length = 9)
	public User getLastContactedBy() {
		return lastContactedBy;
	}

	public void setLastContactedBy(User lastContactedBy) {
		this.lastContactedBy = lastContactedBy;
	}

	@Column(name = "lastContactedDate")
	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	@Column(name = "contactCount", length = 4)
	public int getContactCount() {
		return contactCount;
	}

	public void setContactCount(int contactCount) {
		this.contactCount = contactCount;
	}

	@Column(name = "matchCount", length = 4)
	public int getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}

	@Column(name = "notes", length = 1000)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(name = "conID", length = 9)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount con) {
		this.contractor = con;
	}
}