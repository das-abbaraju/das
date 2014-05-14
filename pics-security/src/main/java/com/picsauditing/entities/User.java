package com.picsauditing.entities;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class User extends BaseTable implements java.io.Serializable/*, Comparable<User>, JSONable, Identifiable*/ {
//	private static final Logger logger = LoggerFactory.getLogger(User.class);
//
//	public static String DEFAULT_AUDITOR = "- Auditor -";
//	public static String DELETED_PREFIX = "DELETE-";
//	public static int SYSTEM = 1;
//	public static int GROUP_ADMIN = 10;
	public static int GROUP_AUDITOR = 11;
//	public static int GROUP_CSR = 959;
//	public static int GROUP_INSIDE_SALES = 71638;
//	public static int GROUP_MANAGER = 981;
//	public static int GROUP_MARKETING = 10801;
//	public static int GROUP_SALES_REPS = 96297;
//	public static int GROUP_DEVELOPER = 33885;
//	public static int GROUP_GC_FREE = 61460;
//	public static int GROUP_GC_FULL = 61461;
//	public static int GROUP_STAKEHOLDER = 64680;
//	public static int GROUP_BETATESTER = 64681;
//	public static int GROUP_SAFETY = 65744;
//	public static int CONTRACTOR = 12;
//	protected boolean needsIndexing = true;
	public static final int GROUP_SU = 9; // Group that automatically has ALL
//	public static final int GROUP_ISR = 71638;
//    public static final int SELENIUM_MASTER_USER = 94545;
//	// permissions
//	public static int INDEPENDENT_CONTRACTOR = 11265;
//
//	// grant privileges
//
	private AppUser appUser = new AppUser();
	private YesNo isGroup;
	private String email;
//	// TODO - read GMail to see if emails are bouncing and auto update this
//	// field
//	private Date emailConfirmedDate;
	private String name;
//	private String firstName;
//	private String lastName;
	private YesNo isActive = YesNo.Yes;
//	private Date lastLogin;
	private Account account;
	private String phone;
//	private String phoneIndex;
	private String fax;

//	private Date passwordChanged = new Date();
//	private String resetHash;
	private boolean forcePasswordReset;
//	private int failedAttempts = 0;
//	private Date lockUntil = null;
	private TimeZone timezone = null;
	private Locale locale = Locale.ENGLISH;
//	private String department;
//	private String apiKey;
//	private boolean usingDynamicReports = true;
//	private Date usingDynamicReportsDate = new Date();
//	private boolean usingVersion7Menus = true;
//	private Date usingVersion7MenusDate = new Date();
//	private int assignmentCapacity;
//	private Date reportsManagerTutorialDate;
//
	private List<UserGroup> groups = new ArrayList<>();
//	private List<UserGroup> members = new ArrayList<UserGroup>();
	private List<UserAccess> ownedPermissions = new ArrayList<>();
//	private List<UserSwitch> switchTos = new ArrayList<UserSwitch>();
//	private List<UserSwitch> switchFroms = new ArrayList<UserSwitch>();
//	private List<EmailSubscription> subscriptions = new ArrayList<EmailSubscription>();
//	private List<ContractorWatch> watchedContractors = new ArrayList<ContractorWatch>();
//	private List<Report> reports = new ArrayList<Report>();
//	private List<Locale> spokenLanguages = new ArrayList<Locale>();
//	private List<String> countriesServiced = new ArrayList<String>();
//
//	// Specifically for testing, DO NOT @Autowired
//	private InputValidator inputValidator;
//
//	// This is specifically used for testing, do not auto-wire
//	private FeatureToggle featureToggle;
//
//	public User() {
//	}
//
//	public User(String name) {
//		this.name = name;
//	}
//
//	public User(int id) {
//		this.id = id;
//	}
//
//	@SuppressWarnings("deprecation")
//	public User(User u, boolean copyAll) {
//		if (copyAll) {
//			this.id = u.getId();
//			this.createdBy = u.getCreatedBy();
//			this.creationDate = u.getCreationDate();
//			this.updatedBy = u.getUpdatedBy();
//			this.updateDate = u.getUpdateDate();
//		}
//
//		this.appUser = u.getAppUser();
//		this.isGroup = u.getIsGroup();
//		this.email = EmailAddressUtils.validate(u.getEmail());
//		this.emailConfirmedDate = u.getEmailConfirmedDate();
//		this.name = u.getName();
//		this.isActive = u.getIsActive();
//		this.lastLogin = u.getLastLogin();
//		this.account = u.getAccount();
//		this.phone = u.getPhone();
//		this.phoneIndex = u.getPhoneIndex();
//		this.fax = u.getFax();
//		this.passwordChanged = u.getPasswordChanged();
//		this.resetHash = u.getResetHash();
//		this.forcePasswordReset = u.isForcePasswordReset();
//		this.failedAttempts = u.getFailedAttempts();
//		this.lockUntil = u.getLockUntil();
//		this.timezone = u.getTimezone();
//		this.locale = u.getLocale();
//		this.department = u.getDepartment();
//		this.groups = u.getGroups();
//		this.members = u.getMembers();
//		this.ownedPermissions = u.getOwnedPermissions();
//		this.switchTos = u.getSwitchTos();
//		this.switchFroms = u.getSwitchFroms();
//		this.subscriptions = u.getSubscriptions();
//	}
//
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="appUserID")
	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	public YesNo getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(YesNo isGroup) {
		this.isGroup = isGroup;
	}

	@Column(name = "email", length = 100)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
        if (StringUtils.isNotEmpty(email)) {
            email = email.trim();
        }
		this.email = email;
	}

//	@Temporal(TemporalType.DATE)
//	public Date getEmailConfirmedDate() {
//		return emailConfirmedDate;
//	}
//
//	public void setEmailConfirmedDate(Date emailConfirmedDate) {
//		this.emailConfirmedDate = emailConfirmedDate;
//	}
//
	@Column(name = "name", length = 255, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	@Override
//	@ReportField(type = FieldType.String, width = 75, importance = FieldImportance.Average)
//	public String getFirstName() {
//		return firstName;
//	}
//
//	public void setFirstName(String firstName) {
//		this.firstName = sanitizeField(firstName);
//	}
//
//	@Override
//	@ReportField(type = FieldType.String, width = 75, importance = FieldImportance.Average)
//	public String getLastName() {
//		return lastName;
//	}
//
//	public void setLastName(String lastName) {
//		this.lastName = sanitizeField(lastName);
//	}
//
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	public YesNo getIsActive() {
		return isActive;
	}

//	public void setActive(boolean value) {
//		this.isActive = YesNo.valueOf(value);
//	}
//
	public void setIsActive(YesNo isActive) {
		this.isActive = isActive;
	}

//	@Temporal(TemporalType.TIMESTAMP)
//	@ReportField(type = FieldType.Date)
//	public Date getLastLogin() {
//		return lastLogin;
//	}
//
//	public void setLastLogin(Date lastLogin) {
//		this.lastLogin = lastLogin;
//	}
//
//	@Transient
//	public String getPassword() {
//		return getAppUser().getPassword();
//	}
//
//	@Transient
//	public void setPassword(String password) {
//		getAppUser().setPassword(password);
//	}
//
//	@Temporal(TemporalType.DATE)
//	public Date getPasswordChanged() {
//		return passwordChanged;
//	}
//
//	public void setPasswordChanged(Date passwordChanged) {
//		this.passwordChanged = passwordChanged;
//	}
//
//	public String getResetHash() {
//		return resetHash;
//	}
//
//	public void setResetHash(String resetHash) {
//		this.resetHash = resetHash;
//	}
//
	public boolean isForcePasswordReset() {
		return forcePasswordReset;
	}

	public void setForcePasswordReset(boolean forcePasswordReset) {
		this.forcePasswordReset = forcePasswordReset;
	}

//	public int getFailedAttempts() {
//		return failedAttempts;
//	}
//
//	public void setFailedAttempts(int failedAttempts) {
//		this.failedAttempts = failedAttempts;
//	}
//
//	public void unlockLogin() {
//		setFailedAttempts(0);
//		setLockUntil(null);
//	}
//
//	@Temporal(TemporalType.TIMESTAMP)
//	public Date getLockUntil() {
//		return lockUntil;
//	}
//
//	public void setLockUntil(Date lockUntil) {
//		this.lockUntil = lockUntil;
//	}
//
//	@Transient
//	public boolean isLocked() {
//		return (getLockUntil() != null) ? new Date().before(getLockUntil()) : false;
//	}
//
	@Column(name = "phone", length = 50)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
        if (StringUtils.isNotEmpty(phone)) {
            phone = phone.trim();
        }
		this.phone = phone;
	}

//	@Column(length = 10)
//	@IndexableField(type = IndexValueType.PHONETYPE, weight = 7)
//	public String getPhoneIndex() {
//		return phoneIndex;
//	}
//
//	public void setPhoneIndex(String phoneIndex) {
//		this.phoneIndex = phoneIndex;
//	}
//
	@Column(length = 15)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
        if (StringUtils.isNotEmpty(fax)) {
            fax = fax.trim();
        }
		this.fax = fax;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "accountID", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

//	@Override
//	public int hashCode() {
//		final int PRIME = 31;
//		int result = 1;
//		result = PRIME * result + id;
//		return result;
//	}
//
//    @ReportField(type = FieldType.Integer)
//	public int getAssignmentCapacity() {
//		return assignmentCapacity;
//	}
//
//	public void setAssignmentCapacity(int assignmentCapacity) {
//		this.assignmentCapacity = assignmentCapacity;
//	}
//
	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
	public List<UserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}

//    @Transient
//    public Set<UserGroup> getAllInheritedGroups() {
//        Set<UserGroup> userGroups = new HashSet<UserGroup>();
//        for (UserGroup group : groups) {
//            userGroups.add(group);
//            userGroups.addAll(group.getGroup().getAllInheritedGroups());
//        }
//
//        return userGroups;
//    }
//
//	@OneToMany(mappedBy = "group", cascade = { CascadeType.ALL })
//	public List<UserGroup> getMembers() {
//		return members;
//	}
//
//	public void setMembers(List<UserGroup> members) {
//		this.members = members;
//	}
//
//	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
//	public List<UserSwitch> getSwitchTos() {
//		return switchTos;
//	}
//
//	public void setSwitchTos(List<UserSwitch> switchTos) {
//		this.switchTos = switchTos;
//	}
//
//	@OneToMany(mappedBy = "switchTo", cascade = { CascadeType.ALL })
//	public List<UserSwitch> getSwitchFroms() {
//		return switchFroms;
//	}
//
//	public void setSwitchFroms(List<UserSwitch> switchFroms) {
//		this.switchFroms = switchFroms;
//	}
//
//	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
//	public List<EmailSubscription> getSubscriptions() {
//		return subscriptions;
//	}
//
//	public void setSubscriptions(List<EmailSubscription> subscriptions) {
//		this.subscriptions = subscriptions;
//	}
//
//	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
//	public List<ContractorWatch> getWatchedContractors() {
//		return watchedContractors;
//	}
//
//	public void setWatchedContractors(List<ContractorWatch> watchedContractors) {
//		this.watchedContractors = watchedContractors;
//	}
//
//	@OneToMany(mappedBy = "createdBy", cascade = CascadeType.REMOVE)
//	public List<Report> getReports() {
//		return reports;
//	}
//
//	public void setReports(List<Report> reports) {
//		this.reports = reports;
//	}
//
//	@ElementCollection
//	@CollectionTable(name = "user_language", joinColumns = @JoinColumn(name = "userID"))
//	@Column(name = "locale")
//	public List<Locale> getSpokenLanguages() {
//		return spokenLanguages;
//	}
//
//	public void setSpokenLanguages(List<Locale> spokenLanguages) {
//		this.spokenLanguages = spokenLanguages;
//	}
//
//	@ElementCollection
//	@CollectionTable(name="user_country", joinColumns = @JoinColumn(name = "userID"))
//	@Column(name = "isoCode")
//	public List<String> getCountriesServiced() {
//		return countriesServiced;
//	}
//
//	public void setCountriesServiced(List<String> countriesServiced) {
//		this.countriesServiced = countriesServiced;
//	}
//
//	@Transient
//	public EmailSubscription getEmailSubscription(Subscription subscription) {
//		for (EmailSubscription emailSub : subscriptions) {
//			if (emailSub.getSubscription() == subscription) {
//				return emailSub;
//			}
//		}
//		return null;
//	}
//
	// TODO: change this to a Set from a List
	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
	@OrderBy("opPerm")
	public List<UserAccess> getOwnedPermissions() {
		return ownedPermissions;
	}

	public void setOwnedPermissions(List<UserAccess> ownedPermissions) {
		this.ownedPermissions = ownedPermissions;
	}

//    @Transient
//    public Set<OpPerms> getOwnedOpPerms() {
//        Set<OpPerms> userPerms = new HashSet<OpPerms>();
//        userPerms = new HashSet<OpPerms>();
//        for (UserAccess ua : getOwnedPermissions()) {
//            userPerms.add(ua.getOpPerm());
//        }
//        return userPerms;
//    }
//
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

//	@Column(length = 100)
//	@ReportField(type = FieldType.String, width = 125, importance = FieldImportance.Average)
//	public String getDepartment() {
//		return department;
//	}
//
//	public void setDepartment(String department) {
//		this.department = sanitizeField(department);
//	}
//
//	/**
//	 * This is a total HACK!! But we can add it to the DB later or something
//	 *
//	 * @return
//	 */
//	@Transient
//	public Location getLocation() {
//		if (account.getId() != Account.PicsID) {
//			return null;
//		}
//
//		switch (id) {
//		case 938: // Jake Fazeli
//		case 34065: // Phillip Laraway
//		case 1725: // Royce Burnett
//		case 9463: // Tiffany Homayounshad
//		case 11503: // Gary Rogers
//		case 38048: // Chad Frost
//		case 38050: // George Megress
//		case 42203: // Preston Case
//		case 50555: // John Van Haaren
//			// Houston Employees
//			return new Location(29.769f, -95.3527f);
//		default:
//			// Irvine Employees
//			return new Location(33.695f, -117.858f);
//		}
//	}
//
//	@Override
//	public int compareTo(User o) {
//		if (!this.isActive.equals(o.getIsActive())) {
//			// Sort Active before Inactive
//			if (this.isActiveB()) {
//				return -1;
//			} else {
//				return 1;
//			}
//		}
//		int accounts = this.account.compareTo(o.getAccount());
//		if (accounts != 0) {
//			return accounts;
//		}
//		if (!this.isGroup.equals(o.getIsGroup())) {
//			// Sort Groups before Users
//			if (this.isGroup()) {
//				return -1;
//			} else {
//				return 1;
//			}
//		}
//
//		return Strings.compareToIgnoreCase(name, o.getName());
//	}
//
//	@Transient
//	@SuppressWarnings("unchecked")
//	public JSONObject toJSON(boolean full) {
//		JSONObject obj = super.toJSON(full);
//		obj.put("name", name);
//
//		if (full) {
//			obj.put("account", account == null ? null : account.toJSON());
//			obj.put("group", isGroup());
//			obj.put("active", isActiveB());
//			obj.put("email", email);
//			obj.put("fax", fax);
//			obj.put("phone", phone);
//			obj.put("lastLogin", lastLogin);
//			obj.put("username", getUsername());
//
//			JSONArray dtoGroups = new JSONArray();
//			for (UserGroup userGroup : groups) {
//				dtoGroups.add(userGroup.getGroup().toJSON());
//			}
//			obj.put("groups", dtoGroups);
//		}
//
//		return obj;
//	}
//
//	public void fromJSON(JSONObject o) {
//		name = (String) o.get("name");
//	}
//
//	@Transient
//	public boolean isEncryptedPasswordEqual(String query) {
//		return getPassword() != null && getPassword().endsWith(EncodedMessage.hash(query + this.getAppUser().getHashSalt()));
//	}
//
//	@Transient
//	public void setEncryptedPassword(String unencryptedPassword) {
//		this.setPassword(EncodedMessage.hash(unencryptedPassword + this.getAppUser().getHashSalt()));
//	}
//
//	/**
//	 * Grants all allowable permission types for this OpPerm
//	 *
//	 * @param opPerm
//	 * @param grantorID
//	 *            who is granting the permission
//	 * @return
//	 */
//	public UserAccess addOwnedPermissions(OpPerms opPerm, int grantorID) {
//		UserAccess ua = new UserAccess();
//		ua.setUser(this);
//		ua.setOpPerm(opPerm);
//		ua.setViewFlag(opPerm.usesView());
//		ua.setEditFlag(opPerm.usesEdit());
//		ua.setDeleteFlag(opPerm.usesDelete());
//		ua.setGrantFlag(true);
//		ua.setLastUpdate(new Date());
//		ua.setGrantedBy(new User(grantorID));
//		ownedPermissions.add(ua);
//		return ua;
//	}
//
//	@Override
//	public String toString() {
//		return (account == null ? "NULL" : account.toString()) + ": " + name + "(" + (isGroup() ? "G" : "U") + id + ")";
//	}
//
//	@Transient
//	public String getIndexType() {
//		if (this.isGroup()) {
//			return "G";
//		} else {
//			return "U";
//		}
//	}
//
//	@Override
//	public boolean isNeedsIndexing() {
//		return needsIndexing;
//	}
//
//	public void setNeedsIndexing(boolean needsIndexing) {
//		this.needsIndexing = needsIndexing;
//	}
//
//	@Transient
//	public String getReturnType() {
//		return "user";
//	}
//
//	@Transient
//	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 2)
//	public String getType() {
//		if (this.isGroup()) {
//			return "GROUP";
//		} else {
//			return "USER";
//		}
//	}
//
//	@Transient
//	public String getSearchText() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(this.getReturnType()).append('|').append("User");
//		sb.append('|').append(this.id).append('|').append(this.name).append('|').append(this.account.name);
//		sb.append('|');
//
//		if (!isActiveB()) {
//			sb.append("inactive");
//		} else {
//			sb.append("active");
//		}
//
//		sb.append("\n");
//		return sb.toString();
//	}
//
//	@Transient
//	public String getViewLink() {
//		return "UsersManage.action?account=" + this.getAccount().getId() + "&user=" + this.id;
//	}
//
//	@Transient
//	public EmailSubscription getSubscription(Subscription subscription) {
//		for (EmailSubscription emailSubscription : getSubscriptions()) {
//			if (emailSubscription.getSubscription().equals(subscription)) {
//				return emailSubscription;
//			}
//		}
//
//		return null;
//	}
//
//	/**
//	 * Enables subscription if disabled or creates subscription if it does not
//	 * exist.
//	 *
//	 * @param subscription
//	 * @return
//	 */
//	@Transient
//	public EmailSubscription getFallbackEmailSubscription(Subscription subscription) {
//		EmailSubscription emailSubscription = getSubscription(subscription);
//
//		if (emailSubscription != null) {
//			if (emailSubscription.isDisabled()) {
//				emailSubscription.enable();
//			}
//
//			return emailSubscription;
//		}
//
//		return subscription.createEmailSubscription(this);
//	}
//
//	/**
//	 * Deprecated in favor of isUsingVersion7Menus()
//	 */
//	@Deprecated
//	public boolean isUsingDynamicReports() {
//		return usingDynamicReports;
//	}
//
//	/**
//	 * Deprecated in favor of setUsingVersion7Menus()
//	 */
//	@Deprecated
//	public void setUsingDynamicReports(boolean usingDynamicReports) {
//		this.usingDynamicReports = usingDynamicReports;
//	}
//
//	/**
//	 * Deprecated in favor of getUsingVersion7MenusDate()
//	 */
//	@Deprecated
//	public Date getUsingDynamicReportsDate() {
//		return usingDynamicReportsDate;
//	}
//
//	/**
//	 * Deprecated in favor of setUsingVersion7MenusDate()
//	 */
//	@Deprecated
//	@Temporal(TemporalType.DATE)
//	public void setusingDynamicReportsDate(Date usingDynamicReportsDate) {
//		this.usingDynamicReportsDate = usingDynamicReportsDate;
//	}
//
//    @ReportField(type = FieldType.Boolean)
//	public boolean isUsingVersion7Menus() {
//		if (!getFeatureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
//			return isUsingDynamicReports();
//		}
//
//		return usingVersion7Menus;
//	}
//
//	public void setUsingVersion7Menus(boolean usingVersion7Menus) {
//		this.usingVersion7Menus = usingVersion7Menus;
//	}
//
//	@Temporal(TemporalType.DATE)
//	public Date getUsingVersion7MenusDate() {
//		if (!getFeatureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
//			return getUsingDynamicReportsDate();
//		}
//
//		return usingVersion7MenusDate;
//	}
//
//	public void setUsingVersion7MenusDate(Date usingVersion7MenusDate) {
//		this.usingVersion7MenusDate = usingVersion7MenusDate;
//	}
//
//	@Transient
//	public boolean isRemoved() {
//		return (isActive == YesNo.No || getUsername().startsWith("DELETE-"));
//	}
//
//	@Transient
//	public boolean hasGroup(int id) {
//		for (UserGroup group : groups) {
//			if (group.getGroup().getId() == id) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public String getApiKey() {
//		return apiKey;
//	}
//
//	public void setApiKey(String apiKey) {
//		this.apiKey = apiKey;
//	}
//
//	@Transient
//	public boolean isApi() {
//		return (this.apiKey != null) && hasPermission(OpPerms.RestApi, OpType.View);
//	}
//
//	@Transient
//	public InputValidator getInputValidator() {
//		if (inputValidator == null) {
//			return SpringUtils.getBean("InputValidator");
//		}
//
//		return inputValidator;
//	}
//
//	@SuppressWarnings("deprecation")
//	@Transient
//	public boolean isUsernameValid(String username) {
//		return getInputValidator().isUsernameValid(username);
//	}
//
//	@Transient
//	public boolean containsOnlySafeCharacters(String str) {
//		return getInputValidator().containsOnlySafeCharacters(str);
//	}
//
//	@Transient
//	public boolean isUsernameNotTaken(String username) {
//		// TODO see if we want to pass something other than 0 in
//		return !getInputValidator().isUsernameTaken(username, 0);
//	}
//
//	@Transient
//	private FeatureToggle getFeatureToggle() {
//		if (featureToggle == null) {
//			return SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
//		}
//
//		return featureToggle;
//	}
//
//	// FIXME: This needs to move into the UserManagementService
//    // -- BLatner: This should be automatically done by the object.
//    // Keeping the internal integrity of self-referencing fields  is
//    // the responsibility of the object, not an external service.
//	@Transient
//	public void updateDisplayNameBasedOnFirstAndLastName() {
//		this.name = (firstName + " " + lastName).trim();
//	}
//
//	@Transient
//	public boolean isDeleted() {
//		return Strings.isNotEmpty(getUsername()) && getUsername().startsWith(DELETED_PREFIX);
//	}
//
//    public static UserBuilder builder() {
//        return new UserBuilder();
//    }
//
//	@Temporal(TemporalType.DATE)
//	public Date getReportsManagerTutorialDate() {
//		return reportsManagerTutorialDate;
//	}
//
//	public void setReportsManagerTutorialDate(Date reportsManagerTutorialDate) {
//		this.reportsManagerTutorialDate = reportsManagerTutorialDate;
//    }
}
