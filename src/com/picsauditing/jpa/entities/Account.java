package com.picsauditing.jpa.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

import com.picsauditing.util.Luhn;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseTable implements java.io.Serializable, Comparable<Account>, JSONable {
	static public int EVERYONE = 1;
	static public int PRIVATE = 2;
	static public int PicsID = 1100;

	protected String name;
	protected String nameIndex;
	protected String dbaName;
	protected Date lastLogin;
	protected String contact;
	protected String address;
	protected String city;
	protected String country = "USA";
	protected String state;
	protected String zip;
	protected String phone;
	protected String phone2;
	protected String fax;
	protected String email;
	protected String webUrl;
	protected Industry industry;
	private Naics naics;
	private boolean naicsValid;
	protected char active;
	protected char seesAllB;
	protected char sendActivationEmailB;
	protected String activationEmailsB;
	protected String type;
	protected boolean qbSync;
	protected String qbListID;
	protected String reason;
	protected boolean acceptsBids; 

	// Other tables
	// protected List<ContractorOperator> contractors;
	protected List<User> users;
	protected List<AccountUser> accountUsers;

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

	@Column(length = 50)
	public String getNameIndex() {
		return this.nameIndex;
	}

	public void setNameIndex(String name) {
		this.nameIndex = name;
	}

	public void setNameIndex() {
		this.nameIndex = Strings.indexName(this.name);
	}

	@Column(length = 100)
	public String getDbaName() {
		return dbaName;
	}

	public void setDbaName(String dbaName) {
		this.dbaName = dbaName;
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

	@Column(length = 35)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(length = 25)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(length = 2)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(length = 15)
	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@Transient
	public String getFullAddress() {
		// We may want to extract this out and create a String address formatter
		StringBuffer full = new StringBuffer();
		full.append(address);
		if (!Strings.isEmpty(city))
			full.append(", ").append(city);
		if (!Strings.isEmpty(state))
			full.append(", ").append(state);
		if (!Strings.isEmpty(country) && !country.equals("US") && !country.startsWith("United"))
			full.append(", ").append(country);
		if (!Strings.isEmpty(zip))
			full.append(" ").append(zip);

		return full.toString();
	}

	@Column(length = 25)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(length = 25)
	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	@Column(length = 20)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(length = 50)
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

	/**
	 * North American Industry Classification System http://www.census.gov/eos/www/naics/ NAICS replaced the SIC in 1997
	 * 
	 * @return
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "naics")
	public Naics getNaics() {
		return naics;
	}

	public void setNaics(Naics naics) {
		this.naics = naics;
	}

	public boolean isNaicsValid() {
		return naicsValid;
	}

	public void setNaicsValid(boolean naicsValid) {
		this.naicsValid = naicsValid;
	}

	@Column(nullable = false, length = 1)
	public char getActive() {
		return this.active;
	}

	public void setActive(char active) {
		this.active = active;
	}

	@Transient
	public boolean isActiveB() {
		return active == 'Y';
	}

	/**
	 * True if QuickBooks Web Connector needs to pull this record into QuickBooks
	 * 
	 * @return
	 */
	public boolean isQbSync() {
		return qbSync;
	}

	public void setQbSync(boolean qbSync) {
		this.qbSync = qbSync;
	}

	/**
	 * Unique Customer ID in QuickBooks, sample: 31A0000-1151296183
	 * 
	 * @return
	 */
	public String getQbListID() {
		return qbListID;
	}

	public void setQbListID(String qbListID) {
		this.qbListID = qbListID;
	}

	/**
	 * Contractor, Operator, Admin, Corporate
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isAcceptsBids() {
		return acceptsBids;
	}

	public void setAcceptsBids(boolean acceptsBids) {
		this.acceptsBids = acceptsBids;
	}

	@OneToMany(mappedBy = "account")
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@OneToMany(mappedBy = "account", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
	public List<AccountUser> getAccountUsers() {
		return accountUsers;
	}

	public void setAccountUsers(List<AccountUser> accountUsers) {
		this.accountUsers = accountUsers;
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
	public int compareTo(Account o) {
		if (o.getId() == id)
			return 0;
		return name.compareToIgnoreCase(o.getName());
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transient
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("name", name);
		obj.put("contact", contact);
		obj.put("active", isActiveB());
		obj.put("type", type.toString());
		
		if (!full)
			return obj;
		
		// TODO full out the optional fields for account
		
		return obj;
	}
	
	@Override
	public void fromJSON(JSONObject obj) {
		super.fromJSON(obj);
		name = (String)obj.get("name");
	}
}
