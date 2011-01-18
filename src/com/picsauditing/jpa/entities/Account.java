package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.util.IndexObject;
import com.picsauditing.util.Luhn;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseTable implements Comparable<Account>, JSONable, Indexable {

	static public int EVERYONE = 1;
	static public int PRIVATE = 2;
	static public int PicsID = 1100;
	static public int PICS_CORPORATE_ID = 14;
	static public List<Integer> PICS_CORPORATE = Arrays.asList(4, 5, 6, 7);

	protected String name;
	protected String nameIndex;
	// private char active;
	protected AccountStatus status = AccountStatus.Pending;
	protected String dbaName;
	protected String address;
	protected String address2;
	protected String address3;
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
	protected String type;
	protected boolean qbSync;
	protected String qbListID;
	protected String qbListCAID;
	protected String reason;
	protected boolean acceptsBids;
	private String description;
	protected User primaryContact;
	protected boolean requiresOQ = false;
	protected boolean requiresCompetencyReview = false;
	protected boolean needsIndexing = true;
	// TODO: Do we want do default this?
	protected boolean onsiteServices = true;
	protected boolean offsiteServices = false;
	protected boolean materialSupplier = false;
	protected Currency currencyCode = Currency.USD;

	// Other tables
	// protected List<ContractorOperator> contractors;
	protected List<User> users = new ArrayList<User>();
	protected List<AccountUser> accountUsers = new ArrayList<AccountUser>();
	protected List<Employee> employees = new ArrayList<Employee>();
	protected List<JobRole> jobRoles = new ArrayList<JobRole>();

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

	@Column(name = "address", length = 50)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "address2", length = 50)
	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@Column(name = "address3", length = 50)
	public String getAddress3() {
		return this.address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
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
		this.setCurrencyCode(null);
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

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.AccountStatus") })
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	// @Deprecated
	// @Column(nullable = false, length = 1)
	// public char getActive() {
	// return this.active;
	// }
	//
	// @Deprecated
	// public void setActive(char active) {
	// this.active = active;
	// }
	//
	// @Deprecated
	// @Transient
	// public boolean isActiveB() {
	// return active == 'Y';
	// }

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

	public String getQbListCAID() {
		return qbListCAID;
	}

	public void setQbListCAID(String qbListCAID) {
		this.qbListCAID = qbListCAID;
	}

	@Transient
	public String getQbListID(String countryCode) {
		if("CA".equals(countryCode))
			return getQbListCAID();
		
		// return default for other
		return getQbListID();
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

	/**
	 * Are they subject to Operator Qualification regulation, and if Contractor,
	 * do they work for an operator who does too?
	 * 
	 * @return
	 */
	public boolean isRequiresOQ() {
		return requiresOQ;
	}

	public void setRequiresOQ(boolean requiresOQ) {
		this.requiresOQ = requiresOQ;
	}

	public boolean isNeedsIndexing() {
		return needsIndexing;
	}

	public void setNeedsIndexing(boolean needsIndex) {
		this.needsIndexing = needsIndex;
	}

	public boolean isOnsiteServices() {
		return onsiteServices;
	}

	public void setOnsiteServices(boolean onsiteServices) {
		this.onsiteServices = onsiteServices;
	}

	public boolean isOffsiteServices() {
		return offsiteServices;
	}

	public void setOffsiteServices(boolean offsiteServices) {
		this.offsiteServices = offsiteServices;
	}

	public boolean isMaterialSupplier() {
		return materialSupplier;
	}

	public void setMaterialSupplier(boolean materialSupplier) {
		this.materialSupplier = materialSupplier;
	}

	/**
	 * Are they subject to Competency Reviews, and if Contractor, do they work
	 * for an operator who does too?
	 * 
	 * @return
	 */
	public boolean isRequiresCompetencyReview() {
		return requiresCompetencyReview;
	}

	public void setRequiresCompetencyReview(boolean requiresCompetencyReview) {
		this.requiresCompetencyReview = requiresCompetencyReview;
	}

	@Column(name = "description", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 * Account currency code is the country we are assuming for billing a customer.
	 * These two may differ in cases where the customer resides in a Country which
	 * we bill as a US customer.
	 * 
	 */
	@Enumerated(EnumType.STRING)
	public Currency getCurrencyCode() {
		if(getCountry() != null && "CA".equals(this.getCountry().getIsoCode()))
			return Currency.CAD;

		return Currency.USD;
	}
	
	public void setCurrencyCode(Currency currencyCode) {
		if(getCountry() != null && "CA".equals(this.getCountry().getIsoCode()))
			this.currencyCode = Currency.CAD;
		
		this.currencyCode = Currency.USD;
	}

	@Transient
	public String getDescriptionHTML() {
		return Utilities.escapeHTML(this.description);
	}

	@OneToMany(mappedBy = "account")
	@OrderBy(clause = "id ASC")
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

	@OneToMany(mappedBy = "account")
	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	@OneToMany(mappedBy = "account")
	public List<JobRole> getJobRoles() {
		return jobRoles;
	}

	public void setJobRoles(List<JobRole> jobRoles) {
		this.jobRoles = jobRoles;
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
	 * 
	 * @return
	 */
	@Transient
	public boolean isOperatorCorporate() {
		return isOperator() || isCorporate();
	}

	// Updated to reflect status instead of active
	@Transient
	public boolean isDemo() {
		return status.equals(AccountStatus.Demo);
	}

	// Operator Qualification Assessment Center
	@Transient
	public boolean isAssessment() {
		return "Assessment".equals(type);
	}

	@Override
	public int compareTo(Account o) {
		if (o.getId() == id)
			return 0;
		if (!o.getType().equals(type)) {
			if (this.isAdmin())
				return -1;
			if (this.isContractor())
				return 1;
			if (this.isCorporate()) {
				if (o.isAdmin())
					return 1;
				else
					return -1;
			}
			if (this.isOperator()) {
				if (o.isContractor())
					return -1;
				else
					return 1;
			}
		}
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
		obj.put("status", status == null ? null : status.toString());
		obj.put("type", type);

		if (full) {
			obj.put("address", address);
			obj.put("dbaName", dbaName);
			obj.put("city", city);
			obj.put("state", state == null ? null : state.getIsoCode());
			obj.put("country", country == null ? null : country.getIsoCode());
			obj.put("zip", zip);
			obj.put("phone", phone);
			obj.put("fax", fax);
			obj.put("industry", industry == null ? null : industry.toString());

			obj.put("primaryContact", primaryContact == null ? null : primaryContact.toJSON());
		}
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
	}

	@Transient
	public Date getLastLogin() {
		Date d = null;
		for (User u : users) {
			if (d == null || (u.getLastLogin() != null && u.getLastLogin().after(d)))
				d = u.getLastLogin();
		}
		return d;
	}

	@Override
	public String toString() {
		return name + "(" + id + ")";
	}

	@Transient
	public List<IndexObject> getIndexValues() {
		List<IndexObject> l = new ArrayList<IndexObject>();
		// type
		l.add(new IndexObject(this.type.toUpperCase(), 2));
		// id
		l.add(new IndexObject(String.valueOf(this.id), 10));
		// name
		String[] sA = this.name.toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
		for (String s : sA) {
			if (s != null && !s.isEmpty())
				l.add(new IndexObject(s, 7));
		}
		// dba
		if (this.dbaName != null && !this.dbaName.equals(this.name)) {
			sA = this.dbaName.toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
			for (String s : sA) {
				if (s != null && !s.isEmpty())
					l.add(new IndexObject(s, 7));
			}
		}
		// city
		if (this.city != null && !this.city.isEmpty()) {
			l.add(new IndexObject(this.city.toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", ""), 3));
		}
		// state
		// State s = s
		if (this.state != null && !this.state.isoCode.isEmpty()) {
			l.add(new IndexObject(this.state.isoCode, 4));
			if (this.state.getEnglish() != null)
				l.add(new IndexObject(this.state.getEnglish().toUpperCase(), 4));
		}
		// zip
		if (this.zip != null && !this.zip.isEmpty())
			l.add(new IndexObject(this.zip, 3));
		// country
		if (this.country != null && !this.country.isoCode.isEmpty()) {
			l.add(new IndexObject(this.country.isoCode, 3));
			if (this.country.getEnglish() != null)
				l.add(new IndexObject(this.country.getEnglish().toUpperCase(), 3));
		}
		// phone
		if (this.phone != null && !this.phone.isEmpty()) {
			String p = Strings.stripPhoneNumber(this.phone);
			if (p.length() >= 10 && !p.matches("\\W"))
				l.add(new IndexObject(p, 2));
		}
		// email l.add(this.);
		// web_URL
		if (this.webUrl != null && !this.webUrl.isEmpty()) {
			String s = this.webUrl.toUpperCase();
			l.add(new IndexObject(s.replaceAll("^(HTTP://)(W{3})|^(W{3}.)|\\W", ""), 4));
		}
		return l;
	}

	@Transient
	public String getIndexType() {
		if (type.equals("Corporate"))
			return "CO";
		if (type.equals("Assessment"))
			return "AS";
		return type.substring(0, 1);
	}

	@Transient
	public String getReturnType() {
		return "account";
	}

	@Transient
	public String getSearchText() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getReturnType()).append('|').append(this.type).append('|').append(this.id).append('|').append(
				this.name).append('|');
		if (this.city != null)
			sb.append(this.city);
		if (this.state != null)
			sb.append(", ").append(this.state).append("\n");
		return sb.toString();
	}

	@Transient
	public String getViewLink() {
		if (this.type.equals("Contractor")) {
			return ("ContractorView.action?id=" + this.id);
		} else if (this.type.equals("Operator") || this.type.equals("Corporate")) {
			return ("FacilitiesEdit.action?id=" + this.id);
		} else if (this.type.equals("Assessment")) {
			return ("AssessmentCenterEdit.action?id=" + this.id);
		}
		return "";
	}

	@Transient
	public Set<ContractorType> getAccountTypes() {
		Set<ContractorType> types = new HashSet<ContractorType>();
		if (isMaterialSupplier())
			types.add(ContractorType.Supplier);
		if (isOnsiteServices())
			types.add(ContractorType.Onsite);
		if (isOffsiteServices())
			types.add(ContractorType.Offsite);
		return types;
	}

	@Transient
	public boolean isMaterialSupplierOnly(){
		return (getAccountTypes().size() == 1 && getAccountTypes().iterator().next().equals(ContractorType.Supplier));
	}
	
	@Transient
	public boolean isUsesAccountType(ContractorType type) {
		for (ContractorType ct : getAccountTypes()) {
			if (ct.equals(type))
				return true;
		}
		
		return false;
	}
}
