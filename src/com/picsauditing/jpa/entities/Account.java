package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.util.Luhn;

@SuppressWarnings("serial")
@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseTable implements java.io.Serializable {
	static public int PicsID = 1100;

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
	protected char seesAllB;
	protected char sendActivationEmailB;
	protected String activationEmailsB;
	protected Date emailConfirmedDate;
	protected String type;

	// Other tables
	// protected List<ContractorOperator> contractors;
	protected List<AccountName> names = new ArrayList<AccountName>();

	@Transient
	public String getIdString() {
		return ((Integer) this.id).toString();
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

	@Column(name = "password", length = 50)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "passwordChange", length = 10)
	public Date getPasswordChange() {
		return this.passwordChange;
	}

	public void setPasswordChange(Date passwordChange) {
		this.passwordChange = passwordChange;
	}

	@Column(name = "lastLogin", length = 19)
	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Column(name = "contact", length = 50)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "address", length = 50)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "city", length = 50)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state", length = 2)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "zip", length = 50)
	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(name = "phone", length = 50)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "phone2", length = 50)
	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	@Column(name = "fax", length = 20)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "email", length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "web_URL", length = 50)
	public String getWebUrl() {
		return this.webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	@Column(name = "industry", length = 50)
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

	@Column(name = "seesAll_B", length = 1)
	public char getSeesAllB() {
		return this.seesAllB;
	}

	public void setSeesAllB(char seesAllB) {
		this.seesAllB = seesAllB;
	}

	@Column(name = "sendActivationEmail_B", length = 1)
	public char getSendActivationEmailB() {
		return this.sendActivationEmailB;
	}

	public void setSendActivationEmailB(char sendActivationEmailB) {
		this.sendActivationEmailB = sendActivationEmailB;
	}

	@Column(name = "activationEmails_B", length = 155)
	public String getActivationEmailsB() {
		return this.activationEmailsB;
	}

	public void setActivationEmailsB(String activationEmailsB) {
		this.activationEmailsB = activationEmailsB;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "emailConfirmedDate", length = 10)
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
	
	@OneToMany(mappedBy = "account")
	@OrderBy("name")
	public List<AccountName> getNames() {
		return names;
	}
	
	public void setNames(List<AccountName> names) {
		this.names = names;
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
	
	@Transient
	public boolean isActiveB() {
		return active == 'Y';
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
		
		// We use to compare class names, but with Hibernate, the names get really weird
		// Now we just ignore the names and just cast it to an Account object
		// System.out.println("this.getClass() "+getClass().getName());
		// System.out.println("obj.getClass()  "+obj.getClass().getName());
		// System.out.println("obj.getClass().getSuperclass()  "+obj.getClass().getSuperclass().getName());
		try {
			// Try to cast this to an account
			final Account other = (Account) obj;
			if (id == other.getId())
				return true;
			return false;
		} catch (Exception e) {
			// something went wrong so these must not be equal
			return false;
		}
	}
	
}
