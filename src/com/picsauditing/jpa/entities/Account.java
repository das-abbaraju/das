package com.picsauditing.jpa.entities;

import java.util.ArrayList;
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

import com.picsauditing.PICS.Utilities;
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
	protected String address;
	protected String city;
	protected Country country;
	protected State state;
	protected String zip;
	protected String phone;
	protected String fax;
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
	private String description;
	protected User primaryContact;

	// Other tables
	// protected List<ContractorOperator> contractors;
	protected List<User> users = new ArrayList<User>();
	protected List<AccountUser> accountUsers = new ArrayList<AccountUser>();

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

	@ManyToOne
	@JoinColumn(name = "country")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@ManyToOne
	@JoinColumn(name = "state")
	public State getState() {
		return this.state;
	}

	public void setState(State state) {
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
		if (state != null)
			full.append(", ").append(state.getIsoCode());
		if (country != null && !country.getIsoCode().equals("US"))
			full.append(", ").append(country.getName());
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

	@Column(length = 20)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
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
	 * North American Industry Classification System
	 * http://www.census.gov/eos/www/naics/ NAICS replaced the SIC in 1997
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
	 * True if QuickBooks Web Connector needs to pull this record into
	 * QuickBooks
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

	@Column(name = "description", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getDescriptionHTML() {
		return Utilities.escapeNewLines(this.description);
	}

	@OneToMany(mappedBy = "account")
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@OneToMany(mappedBy = "account", cascade = { CascadeType.MERGE, CascadeType.REMOVE })
	public List<AccountUser> getAccountUsers() {
		return accountUsers;
	}

	public void setAccountUsers(List<AccountUser> accountUsers) {
		this.accountUsers = accountUsers;
	}

	
	@Transient
	public boolean isAdmin() {
		return id == PicsID;
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

	/**
	 * Is Operator or Corporate
	 * @return
	 */
	@Transient
	public boolean isOperatorCorporate() {
		return isOperator() || isCorporate();
	}

	@Override
	public int compareTo(Account o) {
		if (o.getId() == id)
			return 0;
		return name.compareToIgnoreCase(o.getName());
	}

	@Transient
	public Currency getCurrency() {
		return Currency.getFromISO(country.getIsoCode());
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transient
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("name", name);
		obj.put("active", isActiveB());
		obj.put("type", type.toString());

		if (!full)
			return obj;

		if (address != null)
			obj.put("address", address);
		if (dbaName != null)
			obj.put("dbaName", dbaName);
		if (city != null)
			obj.put("city", city);
		if (state != null)
			obj.put("state", state);
		if (country != null)
			obj.put("country", country);
		if (zip != null)
			obj.put("zip", zip);
		if (phone != null)
			obj.put("phone", phone);
		if (fax != null)
			obj.put("fax", fax);
		if (industry != null)
			obj.put("industry", industry.toString());

		return obj;
	}

	@Override
	public void fromJSON(JSONObject obj) {
		super.fromJSON(obj);
		name = (String) obj.get("name");
	}

	@ManyToOne
	@JoinColumn(name = "contactID", nullable = true)
	public User getPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(User user) {
		this.primaryContact = user;
		
		if (!users.contains(user))
			users.add(user);
	}
}
