package com.picsauditing.actions.users;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.beanutils.BasicDynaBean;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.PasswordValidator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserGroupDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class UsersManage extends PicsActionSupport {
	protected User user;
	protected Account account;

	private String password1;
	private String password2;
	private boolean sendActivationEmail = false;
	private boolean setPrimaryAccount = false;

	private List<BasicDynaBean> userList = null;

	private int shadowID;
	private int moveToAccount = 0;

	private String isGroup = "";
	private String isActive = "Yes";
	private YesNo userIsGroup;

	private boolean conAdmin = false;
	private boolean conBilling = false;
	private boolean conSafety = false;
	private boolean conInsurance = false;
	private boolean newUser = false;

	// used to track whether or not this is being executed from a "Save" Action
	private boolean isSaveAction = false;
	
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected UserAccessDAO userAccessDAO;
	@Autowired
	protected UserGroupDAO userGroupDAO;

	private Set<UserAccess> accessToBeRemoved = new HashSet<UserAccess>();

	public String execute() throws Exception {
		startup();

		if ("department".equalsIgnoreCase(button))
			return "department";

		if (user == null)
			return SUCCESS;

		if ("resetPassword".equals(button)) {
			// Seeding the time in the reset hash so that each one will be
			// guaranteed unique
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			userDAO.save(user);

			addActionMessage(sendRecoveryEmail(user));
		}

		if ("Suggest".equalsIgnoreCase(button))
			return "suggest";

		if (user.getAccount() != null)
			account = user.getAccount();
		if (user.getId() > 0)
			userIsGroup = user.getIsGroup();

		if (!userIsGroup.isTrue() && user.getPermissions().size() == 0) {
			addAlertMessage(getText("UsersManage.AssignUserToGroup"));
		}

		for (UserAccess ua : user.getOwnedPermissions()) {
			if (ua.getOpPerm().equals(OpPerms.ContractorAdmin)) {
				conAdmin = true;
			}
			if (ua.getOpPerm().equals(OpPerms.ContractorBilling)) {
				conBilling = true;
			}
			if (ua.getOpPerm().equals(OpPerms.ContractorSafety)) {
				conSafety = true;
			}
			if (ua.getOpPerm().equals(OpPerms.ContractorInsurance)) {
				conInsurance = true;
			}
		}

		return SUCCESS;
	}

	public String add() {
		user = new User();
		user.setAccount(account);
		user.setIsGroup(userIsGroup);
		user.setActive(true);

		return SUCCESS;
	}

	public String save() throws Exception {
		isSaveAction = true;
		startup();

		user.setIsGroup(userIsGroup);

		// Lazy init fix for isOk method
		user.getGroups().size();
		user.getOwnedPermissions().size();
		if (!isOK()) {
			userDAO.refresh(user); // Clear out ALL changes for the user
			return SUCCESS;
		}

		if (user.getId() > 0 && account.isContractor()) {
			if (!user.isActiveB()) {
				Set<OpPerms> userPerms = new HashSet<OpPerms>();
				for (User users : user.getAccount().getUsers()) {
					for (UserAccess ua : users.getOwnedPermissions()) {
						if (ua.getUser() != user) {
							userPerms.add(ua.getOpPerm());
						}
					}
				}

				if (userPerms.size() < 4) {
					addActionError("Cannot inactivate this user");
					user.setIsActive(YesNo.Yes); // Save everything but isActive
					return SUCCESS;
				}
			}
		}

		if (user.getId() > 0) {
			// We want to save data for an existing user
			if (!Strings.isEmpty(password2) && password2.equals(password1)) {
				user.setEncryptedPassword(password2);
				user.setForcePasswordReset(false);
			}

		} else {
			// We want to save a new user
			final String randomPassword = Long.toString(new Random().nextLong());
			user.setEncryptedPassword(randomPassword);
			user.setForcePasswordReset(true);
		}

		user.setAuditColumns(permissions);

		if (user.getAccount() == null) {
			user.setAccount(new Account());
			if (user.getId() == 0) {
				user.setAccount(account);
			} else if (!permissions.hasPermission(OpPerms.AllOperators)) {
				user.getAccount().setId(permissions.getAccountId());
			}
		}

		if (user.isGroup()) {
			// Create a unique username for this group
			String username = "GROUP";
			username += user.getAccount().getId();
			username += user.getName();

			user.setUsername(username);
		} else {
			int maxHistory = 0;
			// TODO u.getAccount().getPasswordPreferences().getMaxHistory()
			user.addPasswordToHistory(user.getPassword(), maxHistory);
			user.setPhoneIndex(Strings.stripPhoneNumber(user.getPhone()));
		}

		if (user.getAccount().isContractor()) {
			Set<OpPerms> userPerms = new HashSet<OpPerms>();
			userPerms = new HashSet<OpPerms>();
			for (UserAccess ua : user.getOwnedPermissions()) {
				userPerms.add(ua.getOpPerm());
			}

			if (!userPerms.contains(OpPerms.ContractorAdmin)) {
				if (conAdmin) {
					if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorAdmin).size() >= 3) {
						addActionError("You can only have 1-3 users with the "
								+ OpPerms.ContractorAdmin.getDescription() + " permission");
						return SUCCESS;
					}
					user.addOwnedPermissions(OpPerms.ContractorAdmin, permissions.getUserId());
				}
			} else {
				if (!conAdmin) {
					// We need both now to remove data from the useraccess
					// database
					if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorAdmin).size() > 1) {
						removeUserAccess(OpPerms.ContractorAdmin);
					} else {
						addActionError("You must have at least one user with the "
								+ OpPerms.ContractorAdmin.getDescription() + " permission");
					}
				}
			}

			if (!userPerms.contains(OpPerms.ContractorBilling)) {
				if (conBilling)
					user.addOwnedPermissions(OpPerms.ContractorBilling, permissions.getUserId());
			} else {
				if (!conBilling) {
					if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorBilling).size() > 1) {
						removeUserAccess(OpPerms.ContractorBilling);
					} else {
						addActionError("You must have at least one user with the "
								+ OpPerms.ContractorBilling.getDescription() + " permission");
					}

				}
			}

			if (!userPerms.contains(OpPerms.ContractorSafety)) {
				if (conSafety)
					user.addOwnedPermissions(OpPerms.ContractorSafety, permissions.getUserId());
			} else {
				if (!conSafety) {
					if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorSafety).size() > 1) {
						removeUserAccess(OpPerms.ContractorSafety);
					} else {
						addActionError("You must have at least one user with the "
								+ OpPerms.ContractorSafety.getDescription() + " permission");
					}
				}
			}

			if (!userPerms.contains(OpPerms.ContractorInsurance)) {
				if (conInsurance)
					user.addOwnedPermissions(OpPerms.ContractorInsurance, permissions.getUserId());
			} else {
				if (!conInsurance) {
					if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorInsurance).size() > 1) {
						removeUserAccess(OpPerms.ContractorInsurance);
					} else {
						addActionError("You must have at least one user with the "
								+ OpPerms.ContractorInsurance.getDescription() + " permission");
					}
				}
			}

			if (user.getOwnedPermissions().size() == 0 && user.isActiveB()) {
				addActionError("Please add a permission to this user");
				return SUCCESS;
			}
		}

		// CSR shadowing
		List<UserGroup> removeUserGroups = new ArrayList<UserGroup>();
		if (shadowID == 0 || (user.getShadowedUser() != null && user.getShadowedUser().getId() != shadowID)) {
			// Remove all non-groups from this user's groups
			Iterator<UserGroup> iterator = user.getGroups().iterator();
			while (iterator.hasNext()) {
				UserGroup ug = iterator.next();
				if (!ug.getGroup().isGroup()) {
					removeUserGroups.add(ug);
					iterator.remove();
				}
			}
		}

		if (shadowID > 0 && shadowID != user.getId()) {
			User shadow = userDAO.find(shadowID);

			UserGroup ug = new UserGroup();
			ug.setUser(user);
			ug.setGroup(shadow);
			ug.setAuditColumns(permissions);
			userGroupDAO.save(ug);
		}

		// Send activation email if set
		if (sendActivationEmail && user.getId() == 0)
			addActionMessage(sendActivationEmail(user, permissions));

		if (user.getId() == 0) {
			newUser = true;
		}

		try {
			if (setPrimaryAccount && user != null && !user.isGroup() && user.getAccount() != null)
				user.getAccount().setPrimaryContact(user);

			user.setNeedsIndexing(true);
			user = userDAO.save(user);
			if (!user.isGroup())
				addActionMessage("User saved successfully.");

		} catch (ConstraintViolationException e) {
			addActionError("That Username is already in use.  Please select another.");
		} catch (DataIntegrityViolationException e) {
			addActionError("That Username is already in use.  Please select another.");
		} finally {
			for (UserAccess userAccess : accessToBeRemoved) {
				userAccessDAO.remove(userAccess);
			}

			for (UserGroup ug : removeUserGroups) {
				userGroupDAO.remove(ug);
			}
		}

		if (newUser && (user.getAccount().isAdmin() || user.getAccount().isOperatorCorporate())) {
			this.redirect("UsersManage.action?account=" + account.getId() + "&user=" + user.getId());
		}

		return SUCCESS;
	}

	public String unlock() throws Exception {
		startup();

		if (!isOK()) {
			userDAO.clear();
			return SUCCESS;
		}

		user.setLockUntil(null);
		userDAO.save(user);
		return SUCCESS;
	}

	public String move() throws Exception {
		startup();

		if (user.getAccount().getUsers().size() == 1)
			return redirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId()
					+ "&msg=You Cannot Move This User As They Are The Only User On This Account");

		// accounts are different so we are moving to a new account
		// user.setOwnedPermissions(null);
		List<UserAccess> userAccessList = userAccessDAO.findByUser(user.getId());
		Iterator<UserAccess> uaIter = userAccessList.iterator();
		while (uaIter.hasNext()) {
			UserAccess next = uaIter.next();
			user.getOwnedPermissions().remove(next);
			uaIter.remove();
			userAccessList.remove(next);
			userAccessDAO.remove(next);
		}
		// user.setGroups(null);
		List<UserGroup> userGroupList = userGroupDAO.findByUser(user.getId());
		Iterator<UserGroup> ugIter = userGroupList.iterator();
		while (ugIter.hasNext()) {
			UserGroup next = ugIter.next();
			user.getGroups().remove(next);
			ugIter.remove();
			userAccessList.remove(next);
			userAccessDAO.remove(next);
		}
		// get new account
		account = accountDAO.find(moveToAccount);
		user.setAccount(account);
		// user.setNeedsIndexing(true);
		userDAO.save(user);

		return redirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId()
				+ "&msg=You have sucessfully moved " + user.getName() + " to " + user.getAccount().getName());
	}

	public String delete() throws Exception {
		startup();
		permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
		if (!user.isGroup()) {
			// This user is a user (not a group)
			if (user.equals(user.getAccount().getPrimaryContact())) {
				addActionError("Cannot remove the primary user for " + user.getAccount().getName()
						+ ". Please switch the primary user of this account and then attempt to delete them.");
				return SUCCESS;
			}
		}

		user.setActive(false);
		userDAO.save(user);
		addActionMessage("Successfully removed "
				+ (user.isGroup() ? "group: " + user.getName() : "user: " + user.getUsername()));
		user = null;

		return SUCCESS;
	}

	private void startup() throws Exception {
		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.EditUsers);

		if (account == null) {
			// This would happen if I'm looking at my own account, but not a
			// user yet
			account = accountDAO.find(permissions.getAccountId());
		}
		// Make sure we can edit users in this account
		if (permissions.getAccountId() != account.getId())
			permissions.tryPermission(OpPerms.AllOperators);

		// checking to see if primary account user is set
		if (!isSaveAction && (!isPrimaryUserEstablished() || isUserPrimaryContact()))
			setPrimaryAccount = true;
				
		// Default isActive to show all for contractors
		if (account != null && account.isContractor())
			isActive = "All";
	}
	
	private boolean isPrimaryUserEstablished() {
		return (account != null 
				&& account.getPrimaryContact() != null);
	}
	
	private boolean isUserPrimaryContact() {
		return (account != null 
				&& account.getPrimaryContact() != null 
				&& account.getPrimaryContact().equals(user)); 
	}

	private boolean isOK() throws Exception {
		if (user == null) {
			addActionError("No user found");
			return false;
		}
		if (user.getName() == null || user.getName().length() == 0)
			addActionError("Please enter a Display Name.");
		else if (user.getName().length() < 3)
			addActionError("Please enter a Display Name with more than 2 characters.");

		if (user.isGroup())
			return (getActionErrors().size() == 0);

		// Users only after this point
		User temp = new User(user, true);
		userDAO.refresh(user);

		boolean hasduplicate = userDAO.duplicateUsername(temp.getUsername().trim(), temp.getId());
		if (hasduplicate)
			addActionError("This username is NOT available. Please choose a different one.");

		user = new User(temp, true);

		// TODO: Move this into User-validation.xml and use struts 2 for this validation
		String username = user.getUsername().trim();
		if (Strings.isEmpty(username))
			addActionError(getText("User.username.error.Empty"));
		else if (username.length() < 3)
			addActionError(getText("User.username.error.Short"));
		else if (username.length() > 100)
			addActionError(getText("User.username.error.Long"));
		else if (username.contains(" "))
			addActionError(getText("User.username.error.Space"));
		else if (!username.matches("^[a-zA-Z0-9+._@-]{3,50}$"))
			addActionError(getText("User.username.error.Special"));

		if (user.getEmail() == null || user.getEmail().length() == 0 || !Strings.isValidEmail(user.getEmail()))
			addActionError("Please enter a valid Email address.");

		if (user.getId() > 0) {
			if (!Strings.isEmpty(password2)) {
				if (!password1.equals(password2) && !password1.equals(user.getPassword()))
					addActionError("Passwords don't match");

				Vector<String> errors = PasswordValidator.validateContractor(user, password1);
				for (String error : errors)
					addActionError(error);
			}
			
			// Could not find an OpPerms type for the Primary User, so just using ContractorAccounts
			if (!validUserForRoleExists(user, OpPerms.ContractorAccounts)) {
				addActionError(getText("UsersManage.Error.PrimaryUser"));
			} 
			
			if (!userRoleExists(OpPerms.ContractorAdmin) && isActive.equals("No")) {
				addActionError(getText("UsersManage.Error.AdminUser"));
			}
		}
		

		return getActionErrors().size() == 0;
	}
	
	private boolean userRoleExists(OpPerms op) {
		List<User> usersWithRole = user.getAccount().getUsersByRole(op);
		if (user.hasPermission(op) && usersWithRole.size() > 1) { 
			return true;
		} else if (!user.hasPermission(op) && usersWithRole.size() > 0) {
			return true;
		} else
		return false;
	}
	
	
	
	private boolean validUserForRoleExists(User user, OpPerms userRole) {
		if (OpPerms.ContractorAdmin != userRole && OpPerms.ContractorAccounts != userRole) {
			throw new IllegalArgumentException("userRole can only be OpPerms.ContractorAccounts or OpPerms.ContractorAdmin!");
		}
		
		if (hasAtLeastOneActiveUserWithRole(user.getAccount(), userRole)) {
			return true;
		}
		
		return false;
	}
	
	private boolean hasAtLeastOneActiveUserWithRole(Account account, OpPerms userRole) {
		if (account != null && account.getPrimaryContact() != null) {
			List<User> users = account.getUsers();			
			return hasActiveUserForRole(users, account, userRole);
		}
		
		return false;
	}
	
	private boolean hasActiveUserForRole(List<User> users, Account account, OpPerms userRole) {
		if (users != null && !users.isEmpty()) {
			for (User user : users) {
				// this is a special case, because when iterating over the users from the Account object,
				// those users are from the database and may contain the user being Edited, but in
				// a different state than it is in this Action class instance
				if (validateCurrentUserForRoleIsActive(user, userRole)) {
					return true;
				}
				else if (verifyOtherUserIsActive(account, user, userRole)) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * The current user being edited should not have the User Role we are checking for in order 
	 * for this to return true, because we are checking that another user will have the same 
	 * role after this user has been updated.
	 * 
	 * @param account
	 * @param user
	 * @return
	 */
	private boolean verifyOtherUserIsActive(Account account, User user, OpPerms userRole) {
		if (OpPerms.ContractorAdmin == userRole) {
			return (!this.user.equals(user) 
					&& isUserForRoleActive(user, account, userRole) 
					&& user.isActiveB() 
					&& !conAdmin);
		}
		
		return (!this.user.equals(user) 
				&& isUserForRoleActive(user, account, userRole) 
				&& user.isActiveB() 
				&& !setPrimaryAccount);
	}

	private boolean validateCurrentUserForRoleIsActive(User user, OpPerms userRole) {
		if (OpPerms.ContractorAdmin == userRole) {
			return (this.user.equals(user) 
					&& conAdmin 
					&& this.user.isActiveB());
		}
		
		return (this.user.equals(user) 
				&& setPrimaryAccount 
				&& this.user.isActiveB());
	}
	
	private boolean isUserForRoleActive(User user, Account account, OpPerms userRole) {
		if (OpPerms.ContractorAdmin == userRole) {
			 return (user != null && user.isActiveB() && userHasRole(user, userRole));
		}
		
		return (user != null 
				&& account.getPrimaryContact().equals(user) 
				&& user.isActiveB());
	}
	
	private boolean userHasRole(User user, OpPerms userRole) {
		List<UserAccess> roles = user.getOwnedPermissions();
		for (UserAccess userAccess : roles) {
			if (userRole == userAccess.getOpPerm()) {
				return true;
			}
		}
		
		return false;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public YesNo getUserIsGroup() {
		return userIsGroup;
	}

	public void setUserIsGroup(YesNo userIsGroup) {
		this.userIsGroup = userIsGroup;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public boolean isSendActivationEmail() {
		return sendActivationEmail;
	}

	public void setSendActivationEmail(boolean sendActivationEmail) {
		this.sendActivationEmail = sendActivationEmail;
	}

	public boolean isConAdmin() {
		return conAdmin;
	}

	public void setConAdmin(boolean conAdmin) {
		this.conAdmin = conAdmin;
	}

	public boolean isConBilling() {
		return conBilling;
	}

	public void setConBilling(boolean conBilling) {
		this.conBilling = conBilling;
	}

	public boolean isConSafety() {
		return conSafety;
	}

	public void setConSafety(boolean conSafety) {
		this.conSafety = conSafety;
	}

	public boolean isConInsurance() {
		return conInsurance;
	}

	public void setConInsurance(boolean conInsurance) {
		this.conInsurance = conInsurance;
	}

	public boolean isSetPrimaryAccount() {
		return setPrimaryAccount;
	}

	public void setSetPrimaryAccount(boolean setPrimaryAccount) {
		this.setPrimaryAccount = setPrimaryAccount;
	}

	public int getShadowID() {
		return shadowID;
	}

	public void setShadowID(int shadowID) {
		this.shadowID = shadowID;
	}

	public List<BasicDynaBean> getUserList() throws SQLException {
		if (userList == null) {
			Database db = new Database();
			SelectSQL sql = new SelectSQL("users u");
			sql.addOrderBy("isGroup");
			sql.addOrderBy("name");
			sql.addWhere("accountID = " + account.getId());
			if ("Yes".equals(isGroup) || "No".equals(isGroup))
				sql.addWhere("isGroup = '" + isGroup + "'");

			if ("Yes".equals(isActive) || "No".equals(isActive))
				sql.addWhere("isActive = '" + isActive + "'");

			userList = db.select(sql.toString(), false);
		}
		return userList;
	}

	public List<OpPerms> getGrantablePermissions() {
		List<OpPerms> list = new ArrayList<OpPerms>();
		for (com.picsauditing.access.UserAccess perm : permissions.getPermissions()) {
			// I can grant these permissions
			if (perm.isGrantFlag())
				list.add(perm.getOpPerm());
		}
		for (UserAccess perm : user.getOwnedPermissions()) {
			// but these permissions, have already been granted
			list.remove(perm.getOpPerm());
		}
		return list;
	}

	public List<User> getAddableGroups() {
		List<User> list = new ArrayList<User>();

		if (!permissions.hasPermission(OpPerms.EditUsers, OpType.Edit))
			return list;

		// for now, just add all groups in your account to the
		list = userDAO.findByAccountID(account.getId(), "Yes", "Yes");
		// This used to only add groups you were a member of,
		// but this doesn't work for admins trying to add groups they aren't
		// members of
		// for (User group : activeGroups) {
		// if (permissions.hasPermission(OpPerms.AllOperators) ||
		// permissions.getGroups().contains(group.getId()))
		// list.add(group);
		// }

		if (user.isGroup() && permissions.hasPermission(OpPerms.AllOperators)
				&& permissions.getAccountId() != account.getId()) {
			// This is an admin looking at another account (not PICS)
			// Add the non-PICS groups too
			List<User> nonPicsGroups = userDAO.findByAccountID(Account.PicsID, "Yes", "Yes");
			for (User group : nonPicsGroups) {
				// Add the groups owned by PICS but that are for
				// Operator/Corporate/Contractors/etc
				if (!group.getName().startsWith("PICS") && !list.contains(group))
					list.add(group);
			}
		}

		for (UserGroup userGroup : user.getGroups()) {
			// but these groups, have already been added
			list.remove(userGroup.getGroup());
		}
		list.remove(user);
		return list;
	}

	public List<User> getAddableMembers() {
		List<User> list = new ArrayList<User>();

		if (!permissions.hasPermission(OpPerms.EditUsers, OpType.Edit))
			return list;

		if (permissions.hasPermission(OpPerms.AllOperators) || permissions.getGroups().contains(user.getId())) {
			// I'm an admin or I'm a member of this group

			list = userDAO.findByAccountID(account.getId(), "Yes", "");

			for (UserGroup userGroup : user.getMembers()) {
				// but users, already in the group
				list.remove(userGroup.getUser());
			}
		}
		list.remove(user);
		return list;
	}

	public List<UserLoginLog> getRecentLogins() {
		UserLoginLogDAO loginLogDao = SpringUtils.getBean("UserLoginLogDAO");
		return loginLogDao.findRecentLogins(user.getId(), 10);
	}

	public Comparator<UserGroup> getGroupNameComparator() {
		return new Comparator<UserGroup>() {

			@Override
			public int compare(UserGroup o1, UserGroup o2) {
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				return o1.getGroup().getName().compareTo(o2.getGroup().getName());
			}
		};
	}

	public Comparator<UserGroup> getUserNameComparator() {
		return new Comparator<UserGroup>() {

			@Override
			public int compare(UserGroup o1, UserGroup o2) {
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				return o1.getUser().compareTo(o2.getUser());
			}
		};
	}

	public boolean isHasProfileEdit() {
		if (user.getAccount().isContractor())
			return true;

		for (UserAccess userAccess : user.getPermissions()) {
			if (userAccess.getOpPerm().equals(OpPerms.EditProfile)) {
				return true;
			}

		}
		return false;
	}

	public List<BasicDynaBean> getAccountList() throws SQLException {
		if (user != null) {
			if (permissions.isAdmin()) {
				String like = (String) ((String[]) ActionContext.getContext().getParameters().get("q"))[0];
				if (like == null)
					like = "";

				// don't use hibernate to pull up accounts
				SelectAccount sql = new SelectAccount();
				sql.addWhere("status IN ('Active', 'Deactivated', 'Pending') AND name LIKE '" + like + "%'");
				sql.addOrderBy("a.name");
				Database db = new Database();
				return db.select(sql.toString(), true);
			}
		}
		
		return null;
	}

	public int getMoveToAccount() {
		return moveToAccount;
	}

	public void setMoveToAccount(int moveToAccount) {
		this.moveToAccount = moveToAccount;
	}

	public boolean isCsr() {
		if (user != null && !user.isGroup()) {
			for (UserGroup userGroup : user.getGroups()) {
				if (userGroup.getGroup().isGroup() && userGroup.getGroup().getId() == User.GROUP_CSR)
					return true;
			}
		}

		return false;
	}

	public List<UserGroup> getCsrs() {
		if (user.getId() == User.GROUP_CSR) {
			return user.getMembers();
		}
		else if (!user.isGroup()) {
			for (UserGroup userGroup : user.getGroups()) {
				if (userGroup.getGroup().getId() == User.GROUP_CSR) {
					List<UserGroup> csrs = new ArrayList<UserGroup>(userGroup.getGroup().getMembers());
					Iterator<UserGroup> iterator = csrs.iterator();
					while (iterator.hasNext()) {
						UserGroup currentUserGroup = iterator.next();
						if (currentUserGroup.equals(userGroup) || currentUserGroup.getUser().isGroup())
							iterator.remove();
					}

					sortCSRsByName(csrs);

					return csrs;
				}
			}
		} 
			

		return null;
	}

	private void sortCSRsByName(List<UserGroup> csrs) {
		Collections.sort(csrs, new Comparator<UserGroup>() {

			@Override
			public int compare(UserGroup o1, UserGroup o2) {
				return o1.getUser().getName().compareTo(o2.getUser().getName());
			}
		});
	}

	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	public void removeUserAccess(OpPerms perm) {
		Iterator<UserAccess> permissions = user.getOwnedPermissions().iterator();
		while (permissions.hasNext()) {
			UserAccess ua = permissions.next();
			if (ua.getOpPerm() == perm) {
				permissions.remove();
				accessToBeRemoved.add(ua);
			}
		}
	}

	// TODO: Move this to Event Subscription Builder
	public String sendRecoveryEmail(User user) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(85);
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			emailBuilder.addToken("user", user);

			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			String confirmLink = "http://www.picsorganizer.com/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setPriority(100);

			EmailSenderSpring emailSenderStatic = SpringUtils.getBean("EmailSenderSpring");
			emailSenderStatic.send(emailQueue);
			return getTextParameterized("AccountRecovery.EmailSent", user.getEmail());
		} catch (Exception e) {
			return getText("AccountRecovery.error.ResetEmailError");
		}
	}

	public String sendActivationEmail(User user, Permissions permission) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(5);
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			emailBuilder.setBccAddresses("\"PICS Marketing\"<marketing@picsauditing.com>");
			emailBuilder.addToken("user", user);
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			String confirmLink = "http://www.picsorganizer.com/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());
			emailBuilder.setPermissions(permission);

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setPriority(100);

			EmailSenderSpring emailSenderStatic = SpringUtils.getBean("EmailSenderSpring");
			emailSenderStatic.send(emailQueue);

			return getTextParameterized("AccountRecovery.EmailSent", user.getEmail());
		} catch (Exception e) {
			return getText("AccountRecovery.error.ResetEmailError");
		}
	}
}
