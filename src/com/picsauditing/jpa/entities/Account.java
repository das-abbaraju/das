package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.mail.NoUsersDefinedException;
import com.picsauditing.mail.Subscription;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.ReportOnClause;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.util.Luhn;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends AbstractIndexableTable implements Comparable<Account>, JSONable {

	static public int NONE = 0;
	static public int EVERYONE = 1;
	static public int PRIVATE = 2;
	static public int PicsID = 1100;
	static public int PICS_CORPORATE_ID = 14;
	static public List<Integer> PICS_CORPORATE = Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11);
	// Assessment Centers
	static public int ASSESSMENT_NCCER = 11069;

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
	protected CountrySubdivision countrySubdivision;
	protected String zip;
	protected String phone;
	protected String fax;
	protected String webUrl;
	protected Trade mainTrade;
	private Naics naics;
	private boolean naicsValid;
	protected String type;
	protected boolean qbSync;
	protected String qbListID;
	protected String qbListCAID;
	protected String qbListUKID;
	protected String qbListEUID;
	protected String reason;
	protected boolean acceptsBids;
	private String description;
	protected User primaryContact;
	protected boolean requiresOQ = false;
	protected boolean requiresCompetencyReview = false;
	protected boolean needsIndexing = true;
	// TODO: Do we want do default this?
	protected boolean onsiteServices = false;
	protected boolean offsiteServices = false;
	protected boolean materialSupplier = false;
	protected boolean transportationServices = false;
	protected Date accreditation;
	private Locale locale = Locale.ENGLISH;
	protected TimeZone timezone;
	protected boolean autoApproveRelationships = true;
	protected boolean generalContractor = false;
	private int sessionTimeout = 60;
	private int rememberMeTime = 7;
	private boolean rememberMeTimeEnabled = true;

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
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 7)
	@ReportField(category = FieldCategory.AccountInformation, importance = FieldImportance.Required, width = 250)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "nameIndex", length = 50)
	public String getNameIndex() {
		return this.nameIndex;
	}

	public void setNameIndex(String name) {
		this.nameIndex = name;
	}

	public void setNameIndex() {
		this.nameIndex = Strings.indexName(this.name);
	}

	@Column(name = "dbaName", length = 400)
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 7)
	public String getDbaName() {
		return dbaName;
	}

	public void setDbaName(String dbaName) {
		this.dbaName = dbaName;
	}

	@Column(name = "address", length = 50)
	@ReportField(category = FieldCategory.ContactInformation)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "address2", length = 50)
	@ReportField(category = FieldCategory.ContactInformation)
	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@Column(name = "address3", length = 50)
	@ReportField(category = FieldCategory.ContactInformation)
	public String getAddress3() {
		return this.address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	@Column(name = "city", length = 35)
	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 3)
	@ReportField(category = FieldCategory.ContactInformation, importance = FieldImportance.Average)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@ManyToOne
	@JoinColumn(name = "country")
	@IndexableField(type = IndexValueType.ISOTYPE, weight = 3)
	@ReportField(i18nKeyPrefix = "Country", category = FieldCategory.ContactInformation, type = FieldType.Country, width = 150)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@ManyToOne
	@JoinColumn(name = "countrySubdivision")
	@IndexableField(type = IndexValueType.ISOTYPE, weight = 4)
	@ReportField(i18nKeyPrefix = "CountrySubdivision", category = FieldCategory.ContactInformation, type = FieldType.CountrySubdivision)
	public CountrySubdivision getCountrySubdivision() {
		return countrySubdivision;
	}

	public void setCountrySubdivision(CountrySubdivision countrySubdivision) {
		this.countrySubdivision = countrySubdivision;
	}

	@Column(name = "zip", length = 15)
	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 3)
	@ReportField(category = FieldCategory.ContactInformation)
	public String getZip() {
		return this.zip;
	}

	@Transient
	public String getShortZip(String zip) {
		if (zip.length() >= 5)
			return zip.substring(0, 5);
		else
			return zip.substring(0, zip.length() - 1);
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Transient
	@ReportField(category = FieldCategory.ContactInformation, sql = "CONCAT(" + ReportOnClause.ToAlias + ".city, "
			+ ReportOnClause.ToAlias + ".countrySubdivision)", filterable = false)
	public String getFullAddress() {
		// We may want to extract this out and create a String address formatter
		StringBuffer full = new StringBuffer();
		full.append(address);
		if (!Strings.isEmpty(city))
			full.append(", ").append(city);
		if (countrySubdivision != null)
			full.append(", ").append(countrySubdivision.getIsoCode());
		if (country != null && !country.getIsoCode().equals("US"))
			full.append(", ").append(country.getName());
		if (!Strings.isEmpty(zip))
			full.append(" ").append(zip);

		return full.toString();
	}

	@Transient
	public String getShortAddress(String currentCountryCode) {
		StringBuffer full = new StringBuffer();
		if (city != null) {
			full.append(city.trim());
		}
		if (countrySubdivision != null) {
			if (full.length() > 0)
				full.append(", ");
			full.append(countrySubdivision.getIsoCode());
		}
		if (country != null && !country.getIsoCode().equals(currentCountryCode)) {
			if (full.length() > 0)
				full.append(", ");
			if (country.getName() == null)
				full.append(country.getIsoCode());
			else
				full.append(country.getName());
		}

		return full.toString();
	}

	@Column(name = "phone", length = 25)
	@IndexableField(type = IndexValueType.PHONETYPE, weight = 2)
	@ReportField(category = FieldCategory.ContactInformation)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "fax", length = 20)
	@ReportField(category = FieldCategory.ContactInformation)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "web_URL", length = 50)
	@IndexableField(type = IndexValueType.URLTYPE, weight = 4)
	@ReportField(category = FieldCategory.ContactInformation)
	public String getWebUrl() {
		return this.webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}

	@Column(nullable = false)
	public boolean isAutoApproveRelationships() {
		return autoApproveRelationships;
	}

	public void setAutoApproveRelationships(boolean autoApproveRelationships) {
		this.autoApproveRelationships = autoApproveRelationships;
	}

	@Column(nullable = false)
	public boolean isGeneralContractor() {
		return generalContractor;
	}

	public void setGeneralContractor(boolean generalContractor) {
		this.generalContractor = generalContractor;
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
	@Column(name = "status", nullable = false)
	@ReportField(type = FieldType.AccountStatus, category = FieldCategory.AccountInformation, i18nKeyPrefix = "AccountStatus", importance = FieldImportance.Average)
	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
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

	public String getQbListCAID() {
		return qbListCAID;
	}

	public void setQbListCAID(String qbListCAID) {
		this.qbListCAID = qbListCAID;
	}

	public String getQbListUKID() {
		return qbListUKID;
	}

	public void setQbListUKID(String qbListUKID) {
		this.qbListUKID = qbListUKID;
	}

	public String getQbListEUID() {
		return qbListEUID;
	}

	public void setQbListEUID(String qbListEUID) {
		this.qbListEUID = qbListEUID;
	}

	@Transient
	public String getQbListID(String currencyCode) {
		if ("CAD".equals(currencyCode))
			return getQbListCAID();
		if ("GBP".equals(currencyCode))
			return getQbListUKID();
		if ("EUR".equals(currencyCode))
			return getQbListEUID();

		// return default for other
		return getQbListID();
	}

	/**
	 * Contractor, Operator, Admin, Corporate
	 * 
	 * @return
	 */
	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 2)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@ReportField(category = FieldCategory.AccountInformation, width = 200)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	// We should move this to operator. The contractor already has an
	// accountLevel field that replaces this
	@Deprecated
	public boolean isAcceptsBids() {
		return acceptsBids;
	}

	@Deprecated
	public void setAcceptsBids(boolean acceptsBids) {
		this.acceptsBids = acceptsBids;
	}

	/**
	 * Are they subject to Operator Qualification regulation, and if Contractor,
	 * do they work for an operator who does too?
	 * 
	 * @return
	 */
	@ReportField(category = FieldCategory.Classification, type = FieldType.Boolean)
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

	@ReportField(category = FieldCategory.Classification, type = FieldType.Boolean)
	public boolean isOnsiteServices() {
		return onsiteServices;
	}

	public void setOnsiteServices(boolean onsiteServices) {
		this.onsiteServices = onsiteServices;
	}

	@ReportField(category = FieldCategory.Classification, type = FieldType.Boolean)
	public boolean isOffsiteServices() {
		return offsiteServices;
	}

	public void setOffsiteServices(boolean offsiteServices) {
		this.offsiteServices = offsiteServices;
	}

	@ReportField(category = FieldCategory.Classification, type = FieldType.Boolean)
	public boolean isMaterialSupplier() {
		return materialSupplier;
	}

	public void setMaterialSupplier(boolean materialSupplier) {
		this.materialSupplier = materialSupplier;
	}

	@ReportField(category = FieldCategory.Classification, type = FieldType.Boolean)
	public boolean isTransportationServices() {
		return transportationServices;
	}

	public void setTransportationServices(boolean transportationServices) {
		this.transportationServices = transportationServices;
	}

	/**
	 * Are they subject to Competency Reviews, and if Contractor, do they work
	 * for an operator who does too?
	 * 
	 * @return
	 */
	@ReportField(category = FieldCategory.Classification, type = FieldType.Boolean)
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

	@Transient
	public String getDescriptionHTML() {
		return Utilities.escapeHTML(this.description);
	}

	/**
	 * The date HSAN accredited the Training Provider to provide training
	 * services. If HSAN training providers use a lot more custom fields then
	 * we'll create a new table for this and other fields.
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	public Date getAccreditation() {
		return accreditation;
	}

	public void setAccreditation(Date accreditation) {
		this.accreditation = accreditation;
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

	@Transient
	public List<Employee> getActiveEmployees() {
		List<Employee> activeEmployees = new ArrayList<Employee>();

		for (Employee employee : employees) {
			if (!employee.isActive())
				continue;

			// We need to check the status also because I found a user with a
			// status of "Deleted" with active=true
			if (!employee.getStatus().equals(UserStatus.Active))
				continue;

			activeEmployees.add(employee);
		}

		return activeEmployees;
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
		return getCountry().getCurrency();
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
			obj.put("countrySubdivision", countrySubdivision == null ? null : countrySubdivision.getIsoCode());
			obj.put("country", country == null ? null : country.getIsoCode());
			obj.put("zip", zip);
			obj.put("phone", phone);
			obj.put("fax", fax);
			obj.put("mainTrade", mainTrade.toString());

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
		return (name == null ? "NULL" : name) + "(" + id + ")";
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
		sb.append(this.getReturnType()).append('|').append(this.type).append('|').append(this.id).append('|')
				.append(this.name).append('|');

		if (this.city != null) {
			sb.append(this.city);
		}

		if (this.countrySubdivision != null) {
			if (this.city != null) {
				sb.append(", ");
			}

			sb.append(this.countrySubdivision);
		}

		sb.append("|").append(this.status.toString()).append('|').append("\n");

		return sb.toString();
	}

	@Transient
	public String getViewLink() {
		if (this.type.equals("Contractor")) {
			return ("ContractorView.action?id=" + this.id);
		} else if (this.type.equals("Operator") || this.type.equals("Corporate")) {
			return ("FacilitiesEdit.action?operator=" + this.id);
		} else if (this.type.equals("Assessment")) {
			return ("AssessmentCenterEdit.action?center=" + this.id);
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
		if (isTransportationServices())
			types.add(ContractorType.Transportation);
		return types;
	}

	public void addAccountTypes(List<ContractorType> conTypes) {
		if (conTypes != null) {
			for (ContractorType conType : conTypes) {
				if (conType.equals(ContractorType.Onsite) && !isOnsiteServices())
					setOnsiteServices(true);
				if (conType.equals(ContractorType.Offsite) && !isOffsiteServices())
					setOffsiteServices(true);
				if (conType.equals(ContractorType.Supplier) && !isMaterialSupplier())
					setMaterialSupplier(true);
				if (conType.equals(ContractorType.Transportation) && !isTransportationServices())
					setTransportationServices(true);
			}
		}
	}

	@Transient
	public void setAccountTypes(List<ContractorType> serviceTypes) {
		if (serviceTypes == null) {
			return;
		}

		boolean value = false;
		for (ContractorType serviceType : ContractorType.values()) {
			if (serviceTypes.contains(serviceType))
				value = true;
			else
				value = false;
			switch (serviceType) {
			case Onsite:
				setOnsiteServices(value);
				break;
			case Offsite:
				setOffsiteServices(value);
				break;
			case Supplier:
				setMaterialSupplier(value);
				break;
			case Transportation:
				setTransportationServices(value);
				break;
			}
		}
	}

	@Transient
	public boolean isMaterialSupplierOnly() {
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

	@Transient
	@Override
	public String getAutocompleteValue() {
		return name;
	}

	@ManyToOne
	@JoinColumn(name = "mainTradeID")
	public Trade getMainTrade() {
		return mainTrade;
	}

	public void setMainTrade(Trade mainTrade) {
		this.mainTrade = mainTrade;
	}

	@Transient
	public EmailSubscription getFallbackSubscriptionForDefaultContact(Subscription subscription)
			throws NoUsersDefinedException {
		OpPerms requiredPermissionForSubscription = subscription.getRequiredPerms();
		User activeUserWithPermission = getActiveUserWithPermission(requiredPermissionForSubscription);
		if (activeUserWithPermission != null) {
			return activeUserWithPermission.getFallbackEmailSubscription(subscription);
		}

		User activeUserWithContractorAdminPermission = getActiveUserWithPermission(OpPerms.ContractorAdmin);
		if (activeUserWithContractorAdminPermission != null) {
			return activeUserWithContractorAdminPermission.getFallbackEmailSubscription(subscription);
		}

		if (getPrimaryContact() != null && getPrimaryContact().isActiveB()) {
			return getPrimaryContact().getFallbackEmailSubscription(subscription);
		}

		if (!getUsers().isEmpty()) {
			User activeUser = getActiveUser();
			return activeUser.getFallbackEmailSubscription(subscription);
		}

		throw new NoUsersDefinedException();
	}

	@Transient
	public User getActiveUser() throws NoUsersDefinedException {
		for (User user : getUsers()) {
			if (user.isActiveB()) {
				return user;
			}
		}

		throw new NoUsersDefinedException("No Active Users");
	}

	@Transient
	public User getActiveUserWithPermission(OpPerms opPerms) throws NoUsersDefinedException {
		for (User user : getUsersByRole(opPerms)) {
			if (user.isActiveB()) {
				return user;
			}
		}

		return null;
	}

	@Transient
	public List<User> getUsersByRole(OpPerms opPerms) {
		List<User> users = new ArrayList<User>();
		for (User user : getUsers()) {
			// TJA - not sure how null users are getting into the list but on
			// registration it happens
			if (user != null && user.isActiveB()) {
				for (UserAccess userAccess : user.getOwnedPermissions()) {
					if (userAccess.getOpPerm().equals(opPerms)) {
						users.add(user);
					}
				}
			}
		}
		return users;
	}

	@Transient
	public boolean isRemoved() {
		return (status == AccountStatus.Deactivated || status == AccountStatus.Deleted);
	}

	/**
	 * In minutes
	 */
	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	@Column(name = "rememberMeTime")
	public int getRememberMeTimeInDays() {
		return rememberMeTime;
	}

	public void setRememberMeTimeInDays(int rememberMeTime) {
		this.rememberMeTime = rememberMeTime;
	}

	@Column(name = "rememberMeTimeEnabled")
	public boolean isRememberMeTimeEnabled() {
		return rememberMeTimeEnabled;
	}

	public void setRememberMeTimeEnabled(boolean rememberMeTimeEnabled) {
		this.rememberMeTimeEnabled = rememberMeTimeEnabled;
	}

}
