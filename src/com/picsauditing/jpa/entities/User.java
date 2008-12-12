package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

import com.picsauditing.access.OpPerms;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class User implements java.io.Serializable, Comparable<User> {
	public static String DEFAULT_AUDITOR = "- Auditor -";
	public static int GROUP_ADMIN = 10;
	public static int GROUP_AUDITOR = 11;
	public static int CONTRACTOR = 12;
	private static final int GROUP_SU = 9; // Group that automatically has ALL

	// grant privileges

	@Transient
	public boolean isSuperUser() {
		return (id == GROUP_SU);
	}

	private int id = 0;
	private String username;
	private String password;
	private YesNo isGroup;
	private String email;
	private String name;
	private YesNo isActive;
	private Date dateCreated;
	private Date lastLogin;
	private Account account;

	private List<UserGroup> groups = new ArrayList<UserGroup>();
	private List<UserGroup> members = new ArrayList<UserGroup>();
	private List<UserAccess> ownedPermissions = new ArrayList<UserAccess>();

	private String passwordHistory;
	private int failedAttempts = 0;
	private Date lockUntil = null;

	private boolean debug = false;

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public User(int id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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

	public void setIsActive(YesNo isActive) {
		this.isActive = isActive;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
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
	public Set<UserAccess> getPermissions() {
		// Our permissions are empty, so go get some
		Set<UserAccess> permissions = new TreeSet<UserAccess>();

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
			return permissions;
		}

		// get all the groups this user (or group) is a part of
		for (UserGroup userGroup : getGroups()) {
			debug(this.getName() + " - Getting inherited perms for " + userGroup.getGroup().getName());
			Set<UserAccess> tempPerms = userGroup.getGroup().getPermissions();
			for (UserAccess perm : tempPerms) {
				this.add(permissions, perm, false);
			}
		}

		// READ the permissions assigned directly to this THIS user/group
		for (UserAccess perm : ownedPermissions) {
			if (perm != null)
				this.add(permissions, perm, true);
		}

		return permissions;
	}

	private void add(Set<UserAccess> permissions, UserAccess perm, boolean overrideBoth) {
		if (perm == null || perm.getOpPerm() == null)
			return;

		debug(this.getName() + " - - Adding perm " + perm.getOpPerm().getDescription() + " V:" + perm.getViewFlag()
				+ " E:" + perm.getEditFlag() + " D:" + perm.getDeleteFlag() + " G:" + perm.getGrantFlag());
		for (UserAccess origPerm : permissions) {
			if (origPerm.getOpPerm().equals(perm.getOpPerm())) {
				if (overrideBoth) {
					debug(" overriding previous values (these are ownedPermissions)");
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
					debug(" optimistic granting (merging with sibling perms)");
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
		if (!this.isActive.equals(o.isActive)) {
			// Sort Active before Inactive
			if (this.isActive.equals("Yes"))
				return -1;
			else
				return 1;
		}
		if (!this.isGroup.equals(o.isGroup)) {
			// Sort Groups before Users
			if (this.isGroup.equals("Yes"))
				return -1;
			else
				return 1;
		}
		// Then sort by name
		return this.name.compareToIgnoreCase(o.name);
	}

	private void debug(String message) {
		if (this.debug)
			System.out.println(message);
	}
}
