package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "accounts")
public class Account implements java.io.Serializable {
	
	private Integer id = 0;
	private String type;
	private String name;
	private String username;
	private String password;
	private Date passwordChange;
	private Date lastLogin;
	private String contact;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String phone;
	private String phone2;
	private String fax;
	private String email;
	private String webUrl;
	private String industry;
	private char active;
	private String createdBy;
	private Date dateCreated;
	private char seesAllB;
	private char sendActivationEmailB;
	private String activationEmailsB;
	private Date emailConfirmedDate;
	
	// Other tables
	private ContractorInfo contractor;
	private List<ContractorOperator> contractors;
	private List<ContractorOperator> operators;
	private List<OshaLog> oshas;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "type", nullable = false, length = 20)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "name", nullable = false, length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "username", unique = true, nullable = false, length = 50)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false, length = 50)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "passwordChange", nullable = false, length = 10)
	public Date getPasswordChange() {
		return this.passwordChange;
	}

	public void setPasswordChange(Date passwordChange) {
		this.passwordChange = passwordChange;
	}

	@Column(name = "lastLogin", nullable = false, length = 19)
	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Column(name = "contact", nullable = false, length = 50)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "address", nullable = false, length = 50)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "city", nullable = false, length = 50)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state", nullable = false, length = 2)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "zip", nullable = false, length = 50)
	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(name = "phone", nullable = false, length = 50)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "phone2", nullable = false, length = 50)
	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	@Column(name = "fax", nullable = false, length = 20)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "email", nullable = false, length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "web_URL", nullable = false, length = 50)
	public String getWebUrl() {
		return this.webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	@Column(name = "industry", nullable = false, length = 50)
	public String getIndustry() {
		return this.industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	@Column(name = "active", nullable = false, length = 1)
	public char getActive() {
		return this.active;
	}

	public void setActive(char active) {
		this.active = active;
	}

	@Column(name = "createdBy", nullable = false, length = 10)
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateCreated", nullable = false, length = 10)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "seesAll_B", nullable = false, length = 1)
	public char getSeesAllB() {
		return this.seesAllB;
	}

	public void setSeesAllB(char seesAllB) {
		this.seesAllB = seesAllB;
	}

	@Column(name = "sendActivationEmail_B", nullable = false, length = 1)
	public char getSendActivationEmailB() {
		return this.sendActivationEmailB;
	}

	public void setSendActivationEmailB(char sendActivationEmailB) {
		this.sendActivationEmailB = sendActivationEmailB;
	}

	@Column(name = "activationEmails_B", nullable = false, length = 155)
	public String getActivationEmailsB() {
		return this.activationEmailsB;
	}

	public void setActivationEmailsB(String activationEmailsB) {
		this.activationEmailsB = activationEmailsB;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "emailConfirmedDate", nullable = false, length = 10)
	public Date getEmailConfirmedDate() {
		return this.emailConfirmedDate;
	}

	public void setEmailConfirmedDate(Date emailConfirmedDate) {
		this.emailConfirmedDate = emailConfirmedDate;
	}

	//////////////////
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "operatorAccount")
	public List<ContractorOperator> getContractors() {
		return this.contractors;
	}

	public void setContractors(List<ContractorOperator> contractors) {
		this.contractors = contractors;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contractorAccount")
	public List<ContractorOperator> getOperators() {
		return this.operators;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", nullable = false, insertable = false, updatable = false)
	public ContractorInfo getContractor() {
		return this.contractor;
	}

	public void setContractor(ContractorInfo contractor) {
		this.contractor = contractor;
	}

	@OneToMany(mappedBy = "contractorAccount")
	public List<OshaLog> getOshas() {
		return oshas;
	}

	public void setOshas(List<OshaLog> oshas) {
		this.oshas = oshas;
	}

}
