package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.mail.Subscription;
import com.picsauditing.search.IndexOverrideWeight;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.search.IndexableOverride;
import com.picsauditing.util.Location;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
@IndexableOverride(overrides = { @IndexOverrideWeight(methodName = "getId", weight = 4) })
public class User extends AbstractIndexableTable implements java.io.Serializable, Comparable<User>, JSONable {

	public static String DEFAULT_AUDITOR = "- Auditor -";
	public static int SYSTEM = 1;
	public static int GROUP_ADMIN = 10;
	public static int GROUP_AUDITOR = 11;
	public static int GROUP_CSR = 959;
	public static int GROUP_MANAGER = 981;
	public static int GROUP_MARKETING = 10801;
	public static int CONTRACTOR = 12;
	protected boolean needsIndexing = true;
	private static final int GROUP_SU = 9; // Group that automatically has ALL
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
	private YesNo isActive = YesNo.Yes;
	private Date lastLogin;
	private Account account;
	private String phone;
	private String phoneIndex;
	private String fax;

	private String password;
	private Date passwordChanged;
	private String resetHash;
	private String passwordHistory;
	private boolean forcePasswordReset;
	private int failedAttempts = 0;
	private Date lockUntil = null;
	private TimeZone timezone = null;
	private Locale locale = Locale.ENGLISH;
	private String department ;

	private List<UserGroup> groups = new ArrayList<UserGroup>();
	private List<UserGroup> members = new ArrayList<UserGroup>();
	private List<UserAccess> ownedPermissions = new ArrayList<UserAccess>();
	private List<UserSwitch> switchTos = new ArrayList<UserSwitch>();
	private List<UserSwitch> switchFroms = new ArrayList<UserSwitch>();
	private List<EmailSubscription> subscriptions = new ArrayList<EmailSubscription>();

	@Transient
	public boolean isSuperUser() {
		return (id == GROUP_SU);
	}

	// TimeZone.getAvailableIDs();

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public User(int id) {
		this.id = id;
	}

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
		this.email = u.getEmail();
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
		this.passwordHistory = u.getPasswordHistory();
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

	@Column(length = 100, nullable = false, unique = true)
	@IndexableField(type = IndexValueType.EMAILTYPE, weight = 6)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
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

	@Column(length = 100)
	@IndexableField(type = IndexValueType.EMAILTYPE, weight = 5)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Temporal(TemporalType.DATE)
	public Date getEmailConfirmedDate() {
		return emailConfirmedDate;
	}

	public void setEmailConfirmedDate(Date emailConfirmedDate) {
		this.emailConfirmedDate = emailConfirmedDate;
	}

	@Column(length = 255, nullable = false)
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 7)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
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

	public String getPasswordHistory() {
		return passwordHistory;
	}

	public void setPasswordHistory(String passwordHistory) {
		this.passwordHistory = passwordHistory;
	}

	@Transient
	public List<String> getPasswordHistoryList() {

		List<String> list = new ArrayList<String>();

		if (passwordHistory != null) {
			String[] list1 = passwordHistory.split("\n");
			for (String item : list1) {
				if (!Strings.isEmpty(item))
					list.add(item);
			}
		}
		return list;
	}

	public boolean isForcePasswordReset() {
		return forcePasswordReset;
	}

	public void setForcePasswordReset(boolean forcePasswordReset) {
		this.forcePasswordReset = forcePasswordReset;
	}

	public void addPasswordToHistory(String newPassword, int maxHistory) {
		List<String> list = getPasswordHistoryList();

		this.passwordHistory = "";

		if (maxHistory > 0) {
			if (!list.contains(password))
				list.add(0, password);

			// double check to see if newPassword is not equal to password.
			if (!list.contains(newPassword))
				list.add(0, newPassword);

			// "Serialize" the password history
			int i = 0;
			for (String password : list) {
				i++;
				this.passwordHistory += password + "\n";
				if (i >= maxHistory) // don't store more than maxHistory
					// passwords
					break;
			}
		}
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
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

	@Column(length = 50)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
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

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
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

	@Transient
	public EmailSubscription getEmailSubscription(Subscription subscription) {
		for (EmailSubscription emailSub : subscriptions) {
			if (emailSub.getSubscription() == subscription)
				return emailSub;
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
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * This is a total HACK!! But we can add it to the DB later or something
	 * 
	 * @return
	 */
	@Transient
	public Location getLocation() {
		if (account.getId() != Account.PicsID)
			return null;

		switch (id) {
		case 9556: // Austin Hatch
		case 938: // Jake Fazeli
		case 34065: // Phillip Laraway
		case 1725: // Royce Burnett
		case 9463: // Tiffany Homayounshad
		case 11503: // Gary Rogers
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
		PicsLogger.start("User.Permissions", "userID=" + id);

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
			PicsLogger.stop();
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
			if (perm != null)
				add(permissions, perm, true);
		}

		PicsLogger.stop();
		return permissions;
	}

	/**
	 * 
	 * @param permissions
	 *            The new set of permission for this user (transient version of user.permissions)
	 * @param perm
	 *            The actual UserAccess object owned by either the current user or one of its parent groups.
	 * @param overrideBoth
	 *            True if perm is from "this", false if perm is from a parent
	 */
	static private void add(Set<UserAccess> permissions, UserAccess connectPerm, boolean overrideBoth) {
		if (connectPerm == null || connectPerm.getOpPerm() == null)
			return;

		// Create a disconnected copy of UserAccess (perm)
		UserAccess perm = new UserAccess(connectPerm);
		PicsLogger.log(" - - Adding perm " + perm.getOpPerm().getDescription() + " V:" + perm.getViewFlag() + " E:"
				+ perm.getEditFlag() + " D:" + perm.getDeleteFlag() + " G:" + perm.getGrantFlag());

		for (UserAccess origPerm : permissions) {
			if (origPerm.getOpPerm().equals(perm.getOpPerm())) {
				if (overrideBoth) {
					PicsLogger.log(" overriding previous values (these are ownedPermissions)");
					// Override the previous settings, regardless if Granting or
					// Revoking
					if (perm.getViewFlag() != null)
						origPerm.setViewFlag(perm.getViewFlag());
					if (perm.getEditFlag() != null)
						origPerm.setEditFlag(perm.getEditFlag());
					if (perm.getDeleteFlag() != null)
						origPerm.setDeleteFlag(perm.getDeleteFlag());
					if (perm.getGrantFlag() != null)
						origPerm.setGrantFlag(perm.getGrantFlag());
				} else {
					PicsLogger.log(" optimistic granting (merging with sibling perms)");
					// Optimistic Granting
					// if the user has two groups with the same perm type,
					// and one grants but the other revokes, then the users WILL
					// be granted the right
					if (perm.getViewFlag() != null && perm.getViewFlag())
						origPerm.setViewFlag(true);
					if (perm.getEditFlag() != null && perm.getEditFlag())
						origPerm.setEditFlag(true);
					if (perm.getDeleteFlag() != null && perm.getDeleteFlag())
						origPerm.setDeleteFlag(true);
					if (perm.getGrantFlag() != null && perm.getGrantFlag())
						origPerm.setGrantFlag(true);
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
		// System.out.println("Compare " + this.toString() + " to " +
		// o.toString());
		if (!this.isActive.equals(o.getIsActive())) {
			// Sort Active before Inactive
			if (this.isActiveB())
				return -1;
			else
				return 1;
		}
		int accounts = this.account.compareTo(o.getAccount());
		if (accounts != 0)
			return accounts;
		if (!this.isGroup.equals(o.getIsGroup())) {
			// Sort Groups before Users
			if (this.isGroup())
				return -1;
			else
				return 1;
		}

		return this.name.compareToIgnoreCase(o.getName());
	}

	@Transient
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("name", name);
		obj.put("account", account == null ? null : account.toJSON());
		obj.put("group", isGroup());
		obj.put("active", isActiveB());
		obj.put("email", email);
		obj.put("fax", fax);
		obj.put("phone", phone);

		if (full) {
			obj.put("creationDate", creationDate == null ? null : creationDate.getTime());
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
		return password != null && password.endsWith(Strings.hash(query + this.getId()));
	}

	@Transient
	public void setEncryptedPassword(String unencryptedPassword) {
		this.setPassword(Strings.hash(unencryptedPassword + this.getId()));
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
		return account.toString() + ": " + name + "(" + (isGroup() ? "G" : "U") + id + ")";
	}

	@Transient
	public String getIndexType() {
		if (this.isGroup())
			return "G";
		else
			return "U";
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
		if (this.isGroup())
			return "GROUP";
		else
			return "USER";
	}

	@Transient
	public String getSearchText() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getReturnType()).append('|').append("User");
		sb.append('|').append(this.id).append('|').append(this.name).append('|').append(this.account.name).append("\n");
		return sb.toString();
	}

	@Transient
	public String getViewLink() {
		return "UsersManage.action?account=" + this.getAccount().getId() + "&user=" + this.id;
	}

	/**
	 * In UsersManage, another user (non-group) is inserted into this user's groups for shadowing
	 * 
	 * @return shadowed user or null
	 */
	@Transient
	public User getShadowedUser() {
		// Pull up any non-group this user is shadowing
		for (UserGroup ug : getGroups()) {
			if (!ug.getGroup().isGroup())
				return ug.getGroup();
		}

		return null;
	}
}
