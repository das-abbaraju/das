package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.util.Luhn;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account implements java.io.Serializable {

	protected int id;
	protected String name;
	protected String username;
	protected String password;
	protected Date passwordChange;
	protected Date lastLogin;
	protected String contact;
	protected String address;
	protected String city;
	protected String state;
	protected String zip;
	protected String phone;
	protected String phone2;
	protected String fax;
	protected String email;
	protected String webUrl;
	protected Industry industry;
	protected char active;
	protected String createdBy;
	protected Date dateCreated;
	protected char seesAllB;
	protected char sendActivationEmailB;
	protected String activationEmailsB;
	protected Date emailConfirmedDate;
	protected String type;

	// Other tables
	// protected List<ContractorOperator> contractors;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	@Transient
	public String getIdString() {
		return ((Integer) this.id).toString();
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public String getLuhnId() {
		Integer value = this.id;
		return Luhn.addCheckDigit(value.toString());
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

	@Column(name = "password", nullable = true, length = 50)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "passwordChange", nullable = true, length = 10)
	public Date getPasswordChange() {
		return this.passwordChange;
	}

	public void setPasswordChange(Date passwordChange) {
		this.passwordChange = passwordChange;
	}

	@Column(name = "lastLogin", nullable = true, length = 19)
	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Column(name = "contact", nullable = true, length = 50)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "address", nullable = true, length = 50)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "city", nullable = true, length = 50)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state", nullable = true, length = 2)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "zip", nullable = true, length = 50)
	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(name = "phone", nullable = true, length = 50)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "phone2", nullable = true, length = 50)
	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	@Column(name = "fax", nullable = true, length = 20)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "email", nullable = true, length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "web_URL", nullable = true, length = 50)
	public String getWebUrl() {
		return this.webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	@Column(name = "industry", nullable = true, length = 50)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.Industry") })
	@Enumerated(EnumType.STRING)
	public Industry getIndustry() {
		return this.industry;
	}

	public void setIndustry(Industry industry) {
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

	@Column(name = "seesAll_B", nullable = true, length = 1)
	public char getSeesAllB() {
		return this.seesAllB;
	}

	public void setSeesAllB(char seesAllB) {
		this.seesAllB = seesAllB;
	}

	@Column(name = "sendActivationEmail_B", nullable = true, length = 1)
	public char getSendActivationEmailB() {
		return this.sendActivationEmailB;
	}

	public void setSendActivationEmailB(char sendActivationEmailB) {
		this.sendActivationEmailB = sendActivationEmailB;
	}

	@Column(name = "activationEmails_B", nullable = true, length = 155)
	public String getActivationEmailsB() {
		return this.activationEmailsB;
	}

	public void setActivationEmailsB(String activationEmailsB) {
		this.activationEmailsB = activationEmailsB;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "emailConfirmedDate", nullable = true, length = 10)
	public Date getEmailConfirmedDate() {
		return this.emailConfirmedDate;
	}

	public void setEmailConfirmedDate(Date emailConfirmedDate) {
		this.emailConfirmedDate = emailConfirmedDate;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Transient
	public boolean isContractor() {
		return "Contractor".equals(type);
	}
	
	@Transient
	public boolean isOperator() {
		return "Operator".equals(type);
	}
	
	@Transient
	public boolean isCorporate() {
		return "Corporate".equals(type);
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
		if (!(getClass().equals(obj.getClass().getSuperclass())))
			return false;
		final Account other = (Account) obj;
		if (id != other.getId().intValue())
			return false;
		return true;
	}
	
}
