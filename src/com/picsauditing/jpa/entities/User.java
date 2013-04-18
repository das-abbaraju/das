package com.picsauditing.jpa.entities;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.mail.Subscription;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.ReportOnClause;
import com.picsauditing.search.IndexOverrideWeight;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.search.IndexableOverride;
import com.picsauditing.security.EncodedMessage;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Location;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.validator.InputValidator;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
@IndexableOverride(overrides = { @IndexOverrideWeight(methodName = "getId", weight = 4) })
public class User extends AbstractIndexableTable implements java.io.Serializable, Comparable<User>, JSONable {
	private static final Logger logger = LoggerFactory.getLogger(User.class);

	public static String DEFAULT_AUDITOR = "- Auditor -";
	public static String DELETED_PREFIX = "DELETE-";
	public static int SYSTEM = 1;
	public static int GROUP_ADMIN = 10;
	public static int GROUP_AUDITOR = 11;
	public static int GROUP_CSR = 959;
	public static int GROUP_MANAGER = 981;
	public static int GROUP_MARKETING = 10801;
	public static int GROUP_SALES_REPS = 96297;
	public static int GROUP_DEVELOPER = 33885;
	public static int GROUP_GC_FREE = 61460;
	public static int GROUP_GC_FULL = 61461;
	public static int GROUP_STAKEHOLDER = 64680;
	public static int GROUP_BETATESTER = 64681;
	public static int GROUP_SAFETY = 65744;
	public static int CONTRACTOR = 12;
	protected boolean needsIndexing = true;
	private static final int GROUP_SU = 9; // Group that automatically has ALL
	public static final int GROUP_ISR = 71638;
    public static final int SELENIUM_MASTER_USER = 94545;
	// permissions
	public static int INDEPENDENT_CONTRACTOR = 11265;

	// grant privileges

	private String username;
	private YesNo isGroup;
	private String email;
	// TODO - read GMail to see if emails are bouncing and auto update this
	// field
	private Date emailConfirmedDate;
	private String name;
	private String firstName;
	private String lastName;
	private YesNo isActive = YesNo.Yes;
	private Date lastLogin;
	private Account account;
	private String phone;
	private String phoneIndex;
	private String fax;

	private String password;
	private Date passwordChanged;
	private String resetHash;
	private boolean forcePasswordReset;
	private int failedAttempts = 0;
	private Date lockUntil = null;
	private TimeZone timezone = null;
	private Locale locale = Locale.ENGLISH;
	private String department;
	private String apiKey;
	private boolean usingDynamicReports;
	private Date usingDynamicReportsDate;
	private boolean usingVersion7Menus;
	private Date usingVersion7MenusDate;
	private int assignmentCapacity;

	private List<UserGroup> groups = new ArrayList<UserGroup>();
	private List<UserGroup> members = new ArrayList<UserGroup>();
	private List<UserAccess> ownedPermissions = new ArrayList<UserAccess>();
	private List<UserSwitch> switchTos = new ArrayList<UserSwitch>();
	private List<UserSwitch> switchFroms = new ArrayList<UserSwitch>();
	private List<EmailSubscription> subscriptions = new ArrayList<EmailSubscription>();
	private List<ContractorWatch> watchedContractors = new ArrayList<ContractorWatch>();
	private List<Report> reports = new ArrayList<Report>();
	private List<Locale> spokenLanguages = new ArrayList<Locale>();
	private List<String> countriesServiced = new ArrayList<String>();

	// Specifically for testing, DO NOT @Autowired
	private InputValidator inputValidator;

	// This is specifically used for testing, do not auto-wire
	private FeatureToggle featureToggle;

	@Transient
	public boolean isSuperUser() {
		return (id == GROUP_SU);
	}

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public User(int id) {
		this.id = id;
	}

	@SuppressWarnings("deprecation")
	public User(User u, boolean copyAll) {
		if (copyAll) {
			this.id = u.getId();
			this.createdBy = u.getCreatedBy();
			this.creationDate = u.getCreationDate();
			this.updatedBy = u.getUpdatedBy();
			this.updateDate = u.getUpdateDate();
		}

		this.username = u.getUsername();
		this.isGroup = u.getIsGroup();
		this.email = EmailAddressUtils.validate(u.getEmail());
		this.emailConfirmedDate = u.getEmailConfirmedDate();
		this.name = u.getName();
		this.isActive = u.getIsActive();
		this.lastLogin = u.getLastLogin();
		this.account = u.getAccount();
		this.phone = u.getPhone();
		this.phoneIndex = u.getPhoneIndex();
		this.fax = u.getFax();
		this.password = u.getPassword();
		this.passwordChanged = u.getPasswordChanged();
		this.resetHash = u.getResetHash();
		this.forcePasswordReset = u.isForcePasswordReset();
		this.failedAttempts = u.getFailedAttempts();
		this.lockUntil = u.getLockUntil();
		this.timezone = u.getTimezone();
		this.locale = u.getLocale();
		this.department = u.getDepartment();
		this.groups = u.getGroups();
		this.members = u.getMembers();
		this.ownedPermissions = u.getOwnedPermissions();
		this.switchTos = u.getSwitchTos();
		this.switchFroms = u.getSwitchFroms();
		this.subscriptions = u.getSubscriptions();
	}

	@Column(name = "username", length = 100, nullable = false, unique = true)
	@IndexableField(type = IndexValueType.EMAILTYPE, weight = 6)
	@ReportField(type = FieldType.String, importance = FieldImportance.Low)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = sanitizeField(username);
	}

	@Transient
	private String sanitizeField(String field) {
		if (Strings.isNotEmpty(field)) {
			return field.trim();
		}
		return null;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.Boolean, sql = "CASE " + ReportOnClause.ToAlias
			+ ".isGroup WHEN 'Yes' THEN 1 ELSE 0 END")
	public YesNo getIsGroup() {
		return isGroup;
	}

	@Transient
	public boolean isGroup() {
		return YesNo.Yes == isGroup;
	}

	public void setIsGroup(YesNo isGroup) {
		this.isGroup = isGroup;
	}

	@Column(name = "email", length = 100)
	@IndexableField(type = IndexValueType.EMAILTYPE, weight = 5)
	@ReportField(type = FieldType.String, width = 150, importance = FieldImportance.Average)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = sanitizeField(email);
	}

	@Temporal(TemporalType.DATE)
	public Date getEmailConfirmedDate() {
		return emailConfirmedDate;
	}

	public void setEmailConfirmedDate(Date emailConfirmedDate) {
		this.emailConfirmedDate = emailConfirmedDate;
	}

	@Column(name = "name", length = 255, nullable = false)
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 7)
	@ReportField(type = FieldType.String, width = 150, importance = FieldImportance.Required)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = sanitizeField(firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = sanitizeField(lastName);
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.Boolean, sql = "CASE " + ReportOnClause.ToAlias
			+ ".isActive WHEN 'Yes' THEN 1 ELSE 0 END")
	public YesNo getIsActive() {
		return isActive;
	}

	@Transient
	public boolean isActiveB() {
		return YesNo.Yes.equals(isActive);
	}

	public void setActive(boolean value) {
		this.isActive = YesNo.valueOf(value);
	}

	public void setIsActive(YesNo isActive) {
		this.isActive = isActive;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@ReportField(type = FieldType.Date)
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.DATE)
	public Date getPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(Date passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public String getResetHash() {
		return resetHash;
	}

	public void setResetHash(String resetHash) {
		this.resetHash = resetHash;
	}

	public boolean isForcePasswordReset() {
		return forcePasswordReset;
	}

	public void setForcePasswordReset(boolean forcePasswordReset) {
		this.forcePasswordReset = forcePasswordReset;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public void unlockLogin() {
		setFailedAttempts(0);
		setLockUntil(null);
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLockUntil() {
		return lockUntil;
	}

	public void setLockUntil(Date lockUntil) {
		this.lockUntil = lockUntil;
	}

	@Transient
	public boolean isLocked() {
		return (getLockUntil() != null) ? new Date().before(getLockUntil()) : false;
	}

	@Column(name = "phone", length = 50)
	@ReportField(type = FieldType.String, width = 125, importance = FieldImportance.Average)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = sanitizeField(phone);
	}

	@Column(length = 10)
	@IndexableField(type = IndexValueType.PHONETYPE, weight = 7)
	public String getPhoneIndex() {
		return phoneIndex;
	}

	public void setPhoneIndex(String phoneIndex) {
		this.phoneIndex = phoneIndex;
	}

	@Column(length = 15)
	@ReportField(type = FieldType.String, width = 125, importance = FieldImportance.Average)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = sanitizeField(fax);
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "accountID", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	public int getAssignmentCapacity() {
		return assignmentCapacity;
	}

	public void setAssignmentCapacity(int assignmentCapacity) {
		this.assignmentCapacity = assignmentCapacity;
	}

	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
	public List<UserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}

	@OneToMany(mappedBy = "group", cascade = { CascadeType.ALL })
	public List<UserGroup> getMembers() {
		return members;
	}

	public void setMembers(List<UserGroup> members) {
		this.members = members;
	}

	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
	public List<UserSwitch> getSwitchTos() {
		return switchTos;
	}

	public void setSwitchTos(List<UserSwitch> switchTos) {
		this.switchTos = switchTos;
	}

	@OneToMany(mappedBy = "switchTo", cascade = { CascadeType.ALL })
	public List<UserSwitch> getSwitchFroms() {
		return switchFroms;
	}

	public void setSwitchFroms(List<UserSwitch> switchFroms) {
		this.switchFroms = switchFroms;
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	public List<EmailSubscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<EmailSubscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	public List<ContractorWatch> getWatchedContractors() {
		return watchedContractors;
	}

	public void setWatchedContractors(List<ContractorWatch> watchedContractors) {
		this.watchedContractors = watchedContractors;
	}

	@OneToMany(mappedBy = "createdBy", cascade = CascadeType.REMOVE)
	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	@ElementCollection
	@CollectionTable(name = "user_language", joinColumns = @JoinColumn(name = "userID"))
	@Column(name = "locale")
	public List<Locale> getSpokenLanguages() {
		return spokenLanguages;
	}

	public void setSpokenLanguages(List<Locale> spokenLanguages) {
		this.spokenLanguages = spokenLanguages;
	}

	@ElementCollection
	@CollectionTable(name="user_country", joinColumns = @JoinColumn(name = "userID"))
	@Column(name = "isoCode")
	public List<String> getCountriesServiced() {
		return countriesServiced;
	}

	public void setCountriesServiced(List<String> countriesServiced) {
		this.countriesServiced = countriesServiced;
	}

	@Transient
	public EmailSubscription getEmailSubscription(Subscription subscription) {
		for (EmailSubscription emailSub : subscriptions) {
			if (emailSub.getSubscription() == subscription) {
				return emailSub;
			}
		}
		return null;
	}

	// TODO: change this to a Set from a List
	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
	@OrderBy("opPerm")
	public List<UserAccess> getOwnedPermissions() {
		return ownedPermissions;
	}

	public void setOwnedPermissions(List<UserAccess> ownedPermissions) {
		this.ownedPermissions = ownedPermissions;
	}

    @Transient
    public Set<OpPerms> getOwnedOpPerms() {
        Set<OpPerms> userPerms = new HashSet<OpPerms>();
        userPerms = new HashSet<OpPerms>();
        for (UserAccess ua : getOwnedPermissions()) {
            userPerms.add(ua.getOpPerm());
        }
        return userPerms;
    }

    @ReportField(importance = FieldImportance.Average)
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

	@Column(length = 100)
	@ReportField(type = FieldType.String, width = 125, importance = FieldImportance.Average)
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = sanitizeField(department);
	}

	/**
	 * This is a total HACK!! But we can add it to the DB later or something
	 *
	 * @return
	 */
	@Transient
	public Location getLocation() {
		if (account.getId() != Account.PicsID) {
			return null;
		}

		switch (id) {
		case 938: // Jake Fazeli
		case 34065: // Phillip Laraway
		case 1725: // Royce Burnett
		case 9463: // Tiffany Homayounshad
		case 11503: // Gary Rogers
		case 38048: // Chad Frost
		case 38050: // George Megress
		case 42203: // Preston Case
		case 50555: // John Van Haaren
			// Houston Employees
			return new Location(29.769f, -95.3527f);
		default:
			// Irvine Employees
			return new Location(33.695f, -117.858f);
		}
	}

	@Transient
	public Set<UserAccess> getPermissions() {
		// Our permissions are empty, so go get some
		Set<UserAccess> permissions = new TreeSet<UserAccess>();

		Logger logger = LoggerFactory.getLogger("org.perf4j.TimingLogger");
		StopWatch stopwatch = new Slf4JStopWatch(logger);

		stopwatch.start("User.getPermissions()", "userID = " + id);

		if (isSuperUser()) {
			// This is the Super User Group, which should have grant ability on
			// ALL permissions
			// Also grant view/edit/delete on EditUsers
			// SuperUser group does not inherit from parent groups
			for (OpPerms accessType : OpPerms.values()) {
				UserAccess perm = new UserAccess();
				perm.setOpPerm(accessType);
				if (accessType.equals(OpPerms.EditUsers)) {
					perm.setViewFlag(true);
					perm.setEditFlag(true);
					perm.setDeleteFlag(true);
				} else {
					perm.setViewFlag(false);
					perm.setEditFlag(false);
					perm.setDeleteFlag(false);
				}
				perm.setGrantFlag(true);
				permissions.add(perm);
			}
			// PicsLogger.log(message)

			stopwatch.stop();
			return permissions;
		}

		// get all the groups this user (or group) is a part of
		for (UserGroup userGroup : getGroups()) {
			if (userGroup.getGroup().isGroup()) {
				Set<UserAccess> tempPerms = userGroup.getGroup().getPermissions();
				for (UserAccess perm : tempPerms) {
					add(permissions, perm, false);
				}
			}
		}

		// READ the permissions assigned directly to this THIS user/group
		for (UserAccess perm : ownedPermissions) {
			if (perm != null) {
				add(permissions, perm, true);
			}
		}

		stopwatch.stop();
		return permissions;
	}

	/**
	 * @param permissions
	 *            The new set of permission for this user (transient version of
	 *            user.permissions)
	 * @param connectPerm
	 *            The actual UserAccess object owned by either the current user
	 *            or one of its parent groups.
	 * @param overrideBoth
	 *            True if perm is from "this", false if perm is from a parent
	 */
	static private void add(Set<UserAccess> permissions, UserAccess connectPerm, boolean overrideBoth) {
		if (connectPerm == null || connectPerm.getOpPerm() == null) {
			return;
		}

		// Create a disconnected copy of UserAccess (perm)
		UserAccess perm = new UserAccess(connectPerm);
		logger.info(
				" - - Adding perm {} V:{} E:{} D:{} G:{}",
				new Object[] { perm.getOpPerm().getDescription(), perm.getViewFlag(), perm.getEditFlag(),
						perm.getDeleteFlag(), perm.getGrantFlag() });

		for (UserAccess origPerm : permissions) {
			if (origPerm.getOpPerm().equals(perm.getOpPerm())) {
				if (overrideBoth) {
					logger.info(" overriding previous values (these are ownedPermissions)");
					// Override the previous settings, regardless if Granting or
					// Revoking
					if (perm.getViewFlag() != null) {
						origPerm.setViewFlag(perm.getViewFlag());
					}
					if (perm.getEditFlag() != null) {
						origPerm.setEditFlag(perm.getEditFlag());
					}
					if (perm.getDeleteFlag() != null) {
						origPerm.setDeleteFlag(perm.getDeleteFlag());
					}
					if (perm.getGrantFlag() != null) {
						origPerm.setGrantFlag(perm.getGrantFlag());
					}
				} else {
					logger.info(" optimistic granting (merging with sibling perms)");
					// Optimistic Granting
					// if the user has two groups with the same perm type,
					// and one grants but the other revokes, then the users WILL
					// be granted the right
					if (perm.getViewFlag() != null && perm.getViewFlag()) {
						origPerm.setViewFlag(true);
					}
					if (perm.getEditFlag() != null && perm.getEditFlag()) {
						origPerm.setEditFlag(true);
					}
					if (perm.getDeleteFlag() != null && perm.getDeleteFlag()) {
						origPerm.setDeleteFlag(true);
					}
					if (perm.getGrantFlag() != null && perm.getGrantFlag()) {
						origPerm.setGrantFlag(true);
					}
				}
				return;
			}
		}

		// add the parent group's permissions to the user's permissions
		permissions.add(perm);
		return;
	}

	@Override
	public int compareTo(User o) {
		if (!this.isActive.equals(o.getIsActive())) {
			// Sort Active before Inactive
			if (this.isActiveB()) {
				return -1;
			} else {
				return 1;
			}
		}
		int accounts = this.account.compareTo(o.getAccount());
		if (accounts != 0) {
			return accounts;
		}
		if (!this.isGroup.equals(o.getIsGroup())) {
			// Sort Groups before Users
			if (this.isGroup()) {
				return -1;
			} else {
				return 1;
			}
		}

		return this.name.compareToIgnoreCase(o.getName());
	}

	@Transient
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("name", name);

		if (full) {
			obj.put("account", account == null ? null : account.toJSON());
			obj.put("group", isGroup());
			obj.put("active", isActiveB());
			obj.put("email", email);
			obj.put("fax", fax);
			obj.put("phone", phone);
			obj.put("lastLogin", lastLogin);
			obj.put("username", username);

			JSONArray dtoGroups = new JSONArray();
			for (UserGroup userGroup : groups) {
				dtoGroups.add(userGroup.getGroup().toJSON());
			}
			obj.put("groups", dtoGroups);
		}

		return obj;
	}

	public void fromJSON(JSONObject o) {
		name = (String) o.get("name");
	}

	@Transient
	public boolean isEncryptedPasswordEqual(String query) {
		return password != null && password.endsWith(EncodedMessage.hash(query + this.getId()));
	}

	@Transient
	public void setEncryptedPassword(String unencryptedPassword) {
		this.setPassword(EncodedMessage.hash(unencryptedPassword + this.getId()));
	}

	/**
	 * Grants all allowable permission types for this OpPerm
	 *
	 * @param opPerm
	 * @param grantorID
	 *            who is granting the permission
	 * @return
	 */
	public UserAccess addOwnedPermissions(OpPerms opPerm, int grantorID) {
		UserAccess ua = new UserAccess();
		ua.setUser(this);
		ua.setOpPerm(opPerm);
		ua.setViewFlag(opPerm.usesView());
		ua.setEditFlag(opPerm.usesEdit());
		ua.setDeleteFlag(opPerm.usesDelete());
		ua.setGrantFlag(true);
		ua.setLastUpdate(new Date());
		ua.setGrantedBy(new User(grantorID));
		ownedPermissions.add(ua);
		return ua;
	}

	@Override
	public String toString() {
		return (account == null ? "NULL" : account.toString()) + ": " + name + "(" + (isGroup() ? "G" : "U") + id + ")";
	}

	@Transient
	public String getIndexType() {
		if (this.isGroup()) {
			return "G";
		} else {
			return "U";
		}
	}

	@Override
	public boolean isNeedsIndexing() {
		return needsIndexing;
	}

	public void setNeedsIndexing(boolean needsIndexing) {
		this.needsIndexing = needsIndexing;
	}

	@Transient
	public String getReturnType() {
		return "user";
	}

	@Transient
	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 2)
	public String getType() {
		if (this.isGroup()) {
			return "GROUP";
		} else {
			return "USER";
		}
	}

	@Transient
	public String getSearchText() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getReturnType()).append('|').append("User");
		sb.append('|').append(this.id).append('|').append(this.name).append('|').append(this.account.name);
		sb.append('|');

		if (!isActiveB()) {
			sb.append("inactive");
		} else {
			sb.append("active");
		}

		sb.append("\n");
		return sb.toString();
	}

	@Transient
	public String getViewLink() {
		return "UsersManage.action?account=" + this.getAccount().getId() + "&user=" + this.id;
	}

	/**
	 * In UsersManage, another user (non-group) is inserted into this user's
	 * groups for shadowing
	 *
	 * @return shadowed user or null
	 */
	@Transient
	public User getShadowedUser() {
		// Pull up any non-group this user is shadowing
		for (UserGroup ug : getGroups()) {
			if (!ug.getGroup().isGroup()) {
				return ug.getGroup();
			}
		}

		return null;
	}

	@Transient
	public EmailSubscription getSubscription(Subscription subscription) {
		for (EmailSubscription emailSubscription : getSubscriptions()) {
			if (emailSubscription.getSubscription().equals(subscription)) {
				return emailSubscription;
			}
		}

		return null;
	}

	/**
	 * Enables subscription if disabled or creates subscription if it does not
	 * exist.
	 *
	 * @param subscription
	 * @return
	 */
	@Transient
	public EmailSubscription getFallbackEmailSubscription(Subscription subscription) {
		EmailSubscription emailSubscription = getSubscription(subscription);

		if (emailSubscription != null) {
			if (emailSubscription.isDisabled()) {
				emailSubscription.enable();
			}

			return emailSubscription;
		}

		return subscription.createEmailSubscription(this);
	}

	@Transient
	public boolean hasPermission(OpPerms opPerm, OpType oType) {
		for (UserAccess perm : getPermissions()) {
			if (opPerm.isForContractor() && getAccount().isContractor() && perm.getOpPerm() == OpPerms.ContractorAdmin) {
				return true;
			}
			if (opPerm == perm.getOpPerm()) {
				if (oType == OpType.Edit) {
					return perm.getEditFlag();
				} else if (oType == OpType.Delete) {
					return perm.getDeleteFlag();
				} else if (oType == OpType.Grant) {
					return perm.getGrantFlag();
				}
				// Default to OpType.View
				return perm.getViewFlag();
			}
		}
		return false;
	}

	@Transient
	public boolean hasPermission(OpPerms opPerm) {
		return this.hasPermission(opPerm, OpType.View);
	}

	/**
	 * Deprecated in favor of isUsingVersion7Menus()
	 */
	@Deprecated
	public boolean isUsingDynamicReports() {
		return usingDynamicReports;
	}

	/**
	 * Deprecated in favor of setUsingVersion7Menus()
	 */
	@Deprecated
	public void setUsingDynamicReports(boolean usingDynamicReports) {
		this.usingDynamicReports = usingDynamicReports;
	}

	/**
	 * Deprecated in favor of getUsingVersion7MenusDate()
	 */
	@Deprecated
	public Date getUsingDynamicReportsDate() {
		return usingDynamicReportsDate;
	}

	/**
	 * Deprecated in favor of setUsingVersion7MenusDate()
	 */
	@Deprecated
	@Temporal(TemporalType.DATE)
	public void setusingDynamicReportsDate(Date usingDynamicReportsDate) {
		this.usingDynamicReportsDate = usingDynamicReportsDate;
	}

	public boolean isUsingVersion7Menus() {
		if (!getFeatureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
			return isUsingDynamicReports();
		}

		return usingVersion7Menus;
	}

	public void setUsingVersion7Menus(boolean usingVersion7Menus) {
		this.usingVersion7Menus = usingVersion7Menus;
	}

	@Temporal(TemporalType.DATE)
	public Date getUsingVersion7MenusDate() {
		if (!getFeatureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
			return getUsingDynamicReportsDate();
		}

		return usingVersion7MenusDate;
	}

	public void setUsingVersion7MenusDate(Date usingVersion7MenusDate) {
		this.usingVersion7MenusDate = usingVersion7MenusDate;
	}

	@Transient
	public boolean isRemoved() {
		return (isActive == YesNo.No || username.startsWith("DELETE-"));
	}

	@Transient
	public boolean hasGroup(int id) {
		for (UserGroup group : groups) {
			if (group.getGroup().getId() == id) {
				return true;
			}
		}
		return false;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Transient
	public boolean isApi() {
		return (this.apiKey != null) && hasPermission(OpPerms.RestApi, OpType.View);
	}

	@Transient
	public InputValidator getInputValidator() {
		if (inputValidator == null) {
			return SpringUtils.getBean("InputValidator");
		}

		return inputValidator;
	}

	@SuppressWarnings("deprecation")
	@Transient
	public boolean isUsernameValid(String username) {
		return getInputValidator().isUsernameValid(username);
	}

	@Transient
	public boolean containsOnlySafeCharacters(String str) {
		return getInputValidator().containsOnlySafeCharacters(str);
	}

	@Transient
	public boolean isUsernameNotTaken(String username) {
		// TODO see if we want to pass something other than 0 in
		return !getInputValidator().isUsernameTaken(username, 0);
	}

	@Transient
	private FeatureToggle getFeatureToggle() {
		if (featureToggle == null) {
			return SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
		}

		return featureToggle;
	}

	// FIXME: This needs to move into the UserManagementService
	@Transient
	public void updateDisplayNameBasedOnFirstAndLastName() {
		this.name = (firstName + " " + lastName).trim();
	}

	@Transient
	public boolean isDeleted() {
		return Strings.isNotEmpty(username) && username.startsWith(DELETED_PREFIX);
	}
}