package com.picsauditing.actions.users;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserGroupDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.UserSwitch;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.security.CookieSupport;
import com.picsauditing.security.EncodedKey;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.InputValidator;

@SuppressWarnings("serial")
public class UsersManage extends PicsActionSupport {
	protected User user;
	protected Account account;

	private boolean sendActivationEmail = false;
	private boolean setPrimaryAccount = false;

	private List<BasicDynaBean> userList = null;

	private int shadowID;
	private int moveToAccount = 0;

	private String isGroup = "";
	private String isActive = "Yes";
	private YesNo userIsGroup = YesNo.No;

	private boolean conAdmin = false;
	private boolean conBilling = false;
	private boolean conSafety = false;
	private boolean conInsurance = false;
	private boolean newUser = false;
	private boolean usingDynamicReports = false;
	// used to track whether or not this is being executed from a "Save" Action
	private boolean isSaveAction = false;
	private Locale selectedLanguage;
	private Locale removeLanguage;
	private String selectedCountry;
	private String removeCountry;

	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private CountryDAO countryDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected UserAccessDAO userAccessDAO;
	@Autowired
	protected UserGroupDAO userGroupDAO;
	@Autowired
	protected UserSwitchDAO userSwitchDao;
	@Autowired
	protected AppPropertyDAO appPropertyDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	@Autowired
	private InputValidator inputValidator;

	private Set<UserAccess> accessToBeRemoved = new HashSet<UserAccess>();

	private final Logger logger = LoggerFactory.getLogger(UsersManage.class);

	public String execute() throws Exception {
		startup();

		if ("department".equalsIgnoreCase(button)) {
			return "department";
		}

		if (user == null) {
			return SUCCESS;
		}

		if ("Suggest".equalsIgnoreCase(button)) {
			return "suggest";
		}

		if (user.getAccount() != null) {
			account = user.getAccount();
		}

		if (user.getId() > 0) {
			userIsGroup = user.getIsGroup();
		}

		if (!YesNo.toBoolean(userIsGroup) && CollectionUtils.isEmpty(user.getPermissions())) {
			addAlertMessage(getText("UsersManage.AssignUserToGroup"));
		}

		for (UserAccess userAccess : user.getOwnedPermissions()) {
			switch (userAccess.getOpPerm()) {
			case ContractorAdmin:
				conAdmin = true;
				break;
			case ContractorBilling:
				conBilling = true;
				break;
			case ContractorSafety:
				conSafety = true;
				break;
			case ContractorInsurance:
				conInsurance = true;
				break;
			}
		}

		if (user.equals(user.getAccount().getPrimaryContact())) {
			addActionMessage(getTextParameterized("UsersManage.DeactivatePrimary", user.getAccount().getName()));
		}

		if (!user.isActiveB()) {
			addAlertMessage(getTextParameterized("UsersManage.InactiveUser", user.getAccount().getName()));
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
		// isOk is now validateInput
		user.getGroups().size();
		user.getOwnedPermissions().size();

		validateInput();
		if (hasFieldErrors() || hasActionErrors()) {
			userDAO.refresh(user); // Clear out ALL changes for the user
			return INPUT_ERROR;
		}

		// a contractor user.
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
					addActionError(getText("UsersManage.CannotInactivate"));
					user.setIsActive(YesNo.Yes); // Save everything but isActive
					return SUCCESS;
				}
			}
		}

		// TODO remove this nonsense
		if (user.getId() < 0) {
			// We want to save a new user
			final String randomPassword = EncodedKey.randomPassword();
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
			// LW: verify is the group name is duplicate in the current system.
			if (userDAO.duplicateUsername(user.getUsername(), user.getId())) {
				addActionError(getText("UsersManage.GroupnameNotAvailable"));
				userDAO.refresh(user); // Clear out ALL changes for the user
				return SUCCESS;
			}
		} else {
			user.setPhoneIndex(Strings.stripPhoneNumber(user.getPhone()));
		}

		if (user.getAccount().isContractor()) {
			Set<OpPerms> userPerms = getUserPerms();

			if (!userPerms.contains(OpPerms.ContractorAdmin) && conAdmin) {
				if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorAdmin).size() >= 3) {
					addActionError(getTextParameterized("UsersManage.1-3AdminUsers",
							OpPerms.ContractorAdmin.getDescription()));
					return SUCCESS;
				}
			}
			udpateUserRoles();

			if (user.getOwnedPermissions().size() == 0 && user.isActiveB()) {
				addActionError(getText("UsersManage.AddPermissionToUser"));
				return SUCCESS;
			}
		}

		updateShadowCSR();

		// Send activation email if set
		if (sendActivationEmail && user.getId() == 0) {
            setUserResetHash();
			addActionMessage(sendActivationEmail(user, permissions));
		}

		if (user.getId() == 0) {
			newUser = true;
		}

		try {
			if (setPrimaryAccount && user != null && !user.isGroup() && user.getAccount() != null)
				user.getAccount().setPrimaryContact(user);
			// auto indexing, no longer need to call it.
			// user.setNeedsIndexing(true);
			user.setUsingDynamicReports(isUsingDynamicReports());
			user = userDAO.save(user);

			if (!user.isGroup()) {
				addActionMessage(getText("UsersManage.UserSavedSuccessfully"));
			}
		} catch (ConstraintViolationException e) {
			addActionError(getText("UsersManage.UsernameInUse"));
		} catch (DataIntegrityViolationException e) {
			addActionError(getText("UsersManage.UsernameInUse"));
		} finally {
			for (UserAccess userAccess : accessToBeRemoved) {
				userAccessDAO.remove(userAccess);
			}
		}

		if (newUser && (user.getAccount().isAdmin() || user.getAccount().isOperatorCorporate())) {
			return this.setUrlForRedirect("UsersManage.action?account=" + account.getId() + "&user=" + user.getId());
		}

		return SUCCESS;
	}

	private Set<OpPerms> getUserPerms() {
		Set<OpPerms> userPerms = new HashSet<OpPerms>();
		userPerms = new HashSet<OpPerms>();
		for (UserAccess ua : user.getOwnedPermissions()) {
			userPerms.add(ua.getOpPerm());
		}
		return userPerms;
	}

	private void udpateUserRoles() {
		Set<OpPerms> userPerms = getUserPerms();

		if (!userPerms.contains(OpPerms.ContractorAdmin) && conAdmin) {
			user.addOwnedPermissions(OpPerms.ContractorAdmin, permissions.getUserId());
		} else if (userPerms.contains(OpPerms.ContractorAdmin) && !conAdmin) {
			removeUserRole(OpPerms.ContractorAdmin);
		}

		if (!userPerms.contains(OpPerms.ContractorBilling) && conBilling) {
			user.addOwnedPermissions(OpPerms.ContractorBilling, permissions.getUserId());
		} else if (userPerms.contains(OpPerms.ContractorBilling) && !conBilling) {
			removeUserRole(OpPerms.ContractorBilling);
		}

		if (!userPerms.contains(OpPerms.ContractorSafety) && conSafety) {
			user.addOwnedPermissions(OpPerms.ContractorSafety, permissions.getUserId());
		} else if (userPerms.contains(OpPerms.ContractorSafety) && !conSafety) {
			removeUserRole(OpPerms.ContractorSafety);
		}

		if (!userPerms.contains(OpPerms.ContractorInsurance) && conInsurance) {
			user.addOwnedPermissions(OpPerms.ContractorInsurance, permissions.getUserId());
		} else if (userPerms.contains(OpPerms.ContractorInsurance) && !conInsurance) {
			removeUserRole(OpPerms.ContractorInsurance);
		}
	}

	private void removeUserRole(OpPerms role) {
		if (((ContractorAccount) account).getUsersByRole(role).size() > 1) {
			removeUserAccess(role);
		} else {
			addActionError(getTextParameterized("UsersManage.MustHaveOneUserWithPermission", role.getDescription()));
		}
	}

	private void updateShadowCSR() {
		if (shadowID == 0) {
			removeNonGroupUserGroups();
		} else {
			if (user.getShadowedUser() == null) {
				addShadowUserGroup();
			} else {
				if (shadowID != user.getShadowedUser().getId()) {
					removeNonGroupUserGroups();
					addShadowUserGroup();
				}
			}
		}
	}

	private void addShadowUserGroup() {
		User shadow = userDAO.find(shadowID);

		UserGroup ug = new UserGroup();
		ug.setUser(user);
		ug.setGroup(shadow);
		ug.setAuditColumns(permissions);
		userGroupDAO.save(ug);
	}

	private void removeNonGroupUserGroups() {
		List<UserGroup> removeUserGroups = new ArrayList<UserGroup>();
		Iterator<UserGroup> iterator = user.getGroups().iterator();
		while (iterator.hasNext()) {
			UserGroup ug = iterator.next();
			if (!ug.getGroup().isGroup()) {
				removeUserGroups.add(ug);
				iterator.remove();
			}
		}

		for (UserGroup ug : removeUserGroups) {
			userGroupDAO.remove(ug);
		}
	}

	public String unlock() throws Exception {
		startup();

		validateInput();
		if (hasFieldErrors() || hasActionErrors()) {
			userDAO.clear();
			return SUCCESS;
		}

		user.setLockUntil(null);
		userDAO.save(user);

		addActionMessage(getText("UsersManage.Unlocked"));

		return setUrlForRedirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId());
	}

	public String move() throws Exception {
		startup();

		if (user.getAccount().getUsers().size() == 1) {
			addActionMessage(getText("UsersManage.CannotMoveUser"));

			return setUrlForRedirect("UsersManage.action?account=" + user.getAccount().getId() + "&user="
					+ user.getId());
		}

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

		addActionMessage(getTextParameterized("UsersManage.SuccessfullyMoved", user.getName(), user.getAccount()
				.getName()));

		return setUrlForRedirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId());
	}

	@RequiredPermission(value = OpPerms.EditUsers, type = OpType.Edit)
	public String inActivate() throws Exception {
		startup();
		// permissions.tryPermission(OpPerms.EditUsers, OpType.Edit);
		if (!user.isGroup()) {
			// This user is a user (not a group)
			if (user.equals(user.getAccount().getPrimaryContact())) {
				addActionError(getTextParameterized("UsersManage.CannotInactivate", user.getAccount().getName()));
				return SUCCESS;
			}
		}
		// is a contractor
		if (user.getAccount().isContractor()) {
			Set<OpPerms> userPerms = getUserPerms();

			if (userPerms.contains(OpPerms.ContractorAdmin)) {
				if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorAdmin).size() < 2) {
					addActionError(getTextParameterized("UsersManage.MustHaveOneUserWithPermission",
							OpPerms.ContractorAdmin.getDescription()));
					return SUCCESS;
				}
			}

			if (userPerms.contains(OpPerms.ContractorBilling)) {
				if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorBilling).size() < 2) {
					addActionError(getTextParameterized("UsersManage.MustHaveOneUserWithPermission",
							OpPerms.ContractorBilling.getDescription()));
					return SUCCESS;
				}
			}

			if (userPerms.contains(OpPerms.ContractorSafety)) {
				if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorSafety).size() < 2) {
					addActionError(getTextParameterized("UsersManage.MustHaveOneUserWithPermission",
							OpPerms.ContractorSafety.getDescription()));
					return SUCCESS;
				}
			}

			if (userPerms.contains(OpPerms.ContractorInsurance)) {
				if (((ContractorAccount) account).getUsersByRole(OpPerms.ContractorInsurance).size() < 2) {
					addActionError(getTextParameterized("UsersManage.MustHaveOneUserWithPermission",
							OpPerms.ContractorInsurance.getDescription()));
					return SUCCESS;
				}
			}

			if (user.getOwnedPermissions().size() == 0 && user.isActiveB()) {
				addActionError(getText("UsersManage.AddPermissionToUser"));
				return SUCCESS;
			}
		}

		user.setActive(false);
		userDAO.save(user);
		addActionMessage(getTextParameterized("UsersManage.UserInactivated", user.isGroup() ? 1 : 0,
				user.isGroup() ? user.getName() : user.getUsername()));

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.EditUsers, type = OpType.Edit)
	public String activate() throws Exception {
		startup();
		// permissions.tryPermission(OpPerms.EditUsers, OpType.Edit);
		if (!user.isGroup()) {
			// This user is a user (not a group)
			if (user.equals(user.getAccount().getPrimaryContact())) {
				addActionError(getTextParameterized("UsersManage.CannotActivate", user.getAccount().getName()));
				return SUCCESS;
			}
		}

		user.setActive(true);
		userDAO.save(user);

		removeFromExclusionList();

		addActionMessage(getTextParameterized("UsersManage.UserActivated", user.isGroup() ? 1 : 0,
				user.isGroup() ? user.getName() : user.getUsername()));

		// when an user is reactived, refresh the page to change the isactive
		// status.
		return this.setUrlForRedirect("UsersManage.action?account=" + account.getId() + "&user=" + user.getId());

	}

	@RequiredPermission(value = OpPerms.EditUsers, type = OpType.Delete)
	public String delete() throws Exception {
		startup();
		// permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
		if (!user.isGroup()) {
			// This user is a user (not a group)
			if (user.equals(user.getAccount().getPrimaryContact())) {
				addActionError(getTextParameterized("UsersManage.CannotRemovePrimary", user.getAccount().getName()));
				return SUCCESS;
			}
		}

		user.setUsername("DELETE-" + user.getId() + "-" + Strings.hashUrlSafe(user.getUsername()));
		userDAO.save(user);
		addActionMessage(getTextParameterized("UsersManage.SuccessfullyRemoved", user.isGroup() ? 1 : 0,
				user.isGroup() ? user.getName() : user.getUsername()));
		user = null;

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.SwitchUser)
	public String switchUserToDifferentServer() throws Exception {
		// remove the cookie the switch to beta
		removeBetaMaxCookie();

		// get the sessionid form the cookie
		String sessionID = getJSessionID();

		// do not create new sessionid
		HttpSession sessionid = ServletActionContext.getRequest().getSession(false);
		sessionid.setAttribute("JSESSIONID", sessionID);
		sessionid.setAttribute("redirect", "true");
		// query the app_session to look the sessionID, if exist, do the
		// redirect, else do nothing.
		ServletActionContext.getResponse().sendRedirect("Login.action?button=login&switchToUser=" + user.getId());
		return SUCCESS;
	}

	private String getJSessionID() {
		Cookie[] cookiesA = ServletActionContext.getRequest().getCookies();
		String jSessionID = "";
		if (cookiesA != null) {
			for (int i = 0; i < cookiesA.length; i++) {
				if (cookiesA[i].getName().equals("JSESSIONID")) {
					jSessionID = cookiesA[i].getValue();
				}
			}
		}
		return jSessionID;
	}

	public void removeBetaMaxCookie() {
		Cookie cookie = new Cookie(CookieSupport.USE_BETA_COOKIE_NAME, "");
		cookie.setMaxAge(0);
		ServletActionContext.getResponse().addCookie(cookie);
	}

	private void startup() throws Exception {
		if (permissions.isContractor()) {
			permissions.tryPermission(OpPerms.ContractorAdmin);
		} else {
			permissions.tryPermission(OpPerms.EditUsers);
		}

		if (account == null) {
			// This would happen if I'm looking at my own account, but not a
			// user yet
			account = accountDAO.find(permissions.getAccountId());
		}

		// Make sure we can edit users in this account
		if (permissions.getAccountId() != account.getId()) {
			if (!permissions.getOperatorChildren().contains(account.getId()))
				permissions.tryPermission(OpPerms.AllOperators);
		}

		// checking to see if primary account user is set
		if (!isSaveAction && (!isPrimaryUserEstablished() || isUserPrimaryContact())) {
			setPrimaryAccount = true;
		}

		// Default isActive to show all for contractors
		if (account != null && account.isContractor()) {
			isActive = "All";
		}
	}

	private boolean isPrimaryUserEstablished() {
		return (account != null && account.getPrimaryContact() != null);
	}

	private boolean isUserPrimaryContact() {
		return (account != null && account.getPrimaryContact() != null && account.getPrimaryContact().equals(user));
	}

	public void validateInput() {
		if (user == null) {
			addActionError(getText("UsersManage.NoUserFound"));
			return;
		}

		String errorMessageKey = inputValidator.validateName(user.getName());
		addFieldErrorIfMessage("user.name", errorMessageKey);

		if (user.isGroup()) {
			return;
		}

		errorMessageKey = inputValidator.validateName(user.getDepartment(), false);
		addFieldErrorIfMessage("user.department", errorMessageKey);

		errorMessageKey = inputValidator.validateEmail(user.getEmail());
		addFieldErrorIfMessage("user.email", errorMessageKey);

		errorMessageKey = inputValidator.validateUsername(user.getUsername());
		addFieldErrorIfMessage("user.username", errorMessageKey);

		errorMessageKey = inputValidator.validateUsernameAvailable(user.getUsername(), user.getId());
		addFieldErrorIfMessage("user.username", errorMessageKey);

		errorMessageKey = inputValidator.validatePhoneNumber(user.getPhone(), false);
		addFieldErrorIfMessage("user.phone", errorMessageKey);

		errorMessageKey = inputValidator.validatePhoneNumber(user.getFax(), false);
		addFieldErrorIfMessage("user.fax", errorMessageKey);

		errorMessageKey = inputValidator.validateLocale(user.getLocale());
		addFieldErrorIfMessage("user.locale", errorMessageKey);

		if (user.getTimezone() == null) {
			addFieldErrorIfMessage("user.timezone", InputValidator.REQUIRED_KEY);
		}

		if (user.getId() > 0 && account.isContractor()) {
			// Could not find an OpPerms type for the Primary User, so just
			// using ContractorAccounts
			if (!validUserForRoleExists(user, OpPerms.ContractorAccounts)) {
				addActionError(getText("UsersManage.Error.PrimaryUser"));
			}
			if (!userRoleExists(OpPerms.ContractorAdmin) && isActive.equals("No")) {
				addActionError(getText("UsersManage.Error.AdminUser"));
			}
		}
	}

	private boolean userRoleExists(OpPerms op) {
		List<User> usersWithRole = user.getAccount().getUsersByRole(op);
		if (usersWithRole.size() != 0) {
			if (user.hasPermission(op) && usersWithRole.size() > 1) {
				return true;
			} else if (!user.hasPermission(op) && usersWithRole.size() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean validUserForRoleExists(User user, OpPerms userRole) {
		if (OpPerms.ContractorAdmin != userRole && OpPerms.ContractorAccounts != userRole) {
			throw new IllegalArgumentException(
					"userRole can only be OpPerms.ContractorAccounts or OpPerms.ContractorAdmin!");
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
				// this is a special case, because when iterating over the users
				// from the Account object,
				// those users are from the database and may contain the user
				// being Edited, but in
				// a different state than it is in this Action class instance
				if (validateCurrentUserForRoleIsActive(user, userRole)) {
					return true;
				} else if (verifyOtherUserIsActive(account, user, userRole)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * The current user being edited should not have the User Role we are
	 * checking for in order for this to return true, because we are checking
	 * that another user will have the same role after this user has been
	 * updated.
	 * 
	 * @param account
	 * @param user
	 * @return
	 */
	private boolean verifyOtherUserIsActive(Account account, User user, OpPerms userRole) {
		if (OpPerms.ContractorAdmin == userRole) {
			return (!this.user.equals(user) && isUserForRoleActive(user, account, userRole) && user.isActiveB() && !conAdmin);
		}

		return (!this.user.equals(user) && isUserForRoleActive(user, account, userRole) && user.isActiveB() && !setPrimaryAccount);
	}

	private boolean validateCurrentUserForRoleIsActive(User user, OpPerms userRole) {
		if (OpPerms.ContractorAdmin == userRole) {
			return (this.user.equals(user) && conAdmin && this.user.isActiveB());
		}

		return (this.user.equals(user) && setPrimaryAccount && this.user.isActiveB());
	}

	private boolean isUserForRoleActive(User user, Account account, OpPerms userRole) {
		if (OpPerms.ContractorAdmin == userRole) {
			return (user != null && user.isActiveB() && userHasRole(user, userRole));
		}

		return (user != null && account.getPrimaryContact().equals(user) && user.isActiveB());
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
			sql.addWhere("username not like 'DELETE-%'");
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
		Collections.sort(list, OpPerms.PermissionComparator);

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

		try {
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
		} catch (Exception e) {
			logger.error(e.getMessage());

		}

		try {
			for (UserGroup userGroup : user.getGroups()) {
				// but these groups, have already been added
				list.remove(userGroup.getGroup());
			}
		} catch (Exception e) {
			logger.error("test 2 {}", e.getMessage());
		}
		list.remove(user);
		return list;
	}

	public List<UserSwitch> getSwitchTos() {
		return userSwitchDao.findByUserId(user.getId());
	}

	public List<User> getAddableMembers() {
		List<User> list = new ArrayList<User>();

		if (!permissions.hasPermission(OpPerms.EditUsers, OpType.Edit))
			return list;

		if (permissions.hasPermission(OpPerms.AllOperators) || permissions.hasGroup(user.getId())) {
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

	public List<BasicDynaBean> getAccountList() throws SQLException {
		if (user != null) {
			if (permissions.isAdmin()) {
				String like = (String) ((String[]) ActionContext.getContext().getParameters().get("q"))[0];
				if (like == null)
					like = "";

				// don't use hibernate to pull up accounts
				SelectAccount sql = new SelectAccount();
				sql.addWhere("status IN ('Active', 'Deactivated', 'Pending', 'Demo') AND name LIKE '" + like + "%'");
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
		} else if (!user.isGroup()) {
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

	public boolean isUsingDynamicReports() {
		return usingDynamicReports;
	}

	public void setUsingDynamicReports(boolean usingDynamicReports) {
		this.usingDynamicReports = usingDynamicReports;
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

	public String emailPassword() throws Exception {
		setUserResetHash();
		addActionMessage(sendRecoveryEmail(user));
		return SUCCESS;
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

	// TODO: Move this email logic to Event Subscription Builder
	public String sendRecoveryEmail(User user) {
		try {
			String serverName = ServletActionContext.getRequest().getServerName();

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(85);
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			emailBuilder.addToken("user", user);

			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			String confirmLink = "https://" + serverName + "/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setCriticalPriority();

			emailSender.send(emailQueue);
			return getTextParameterized("AccountRecovery.EmailSent", user.getEmail());
		} catch (Exception e) {
			return getText("AccountRecovery.error.ResetEmailError");
		}
	}

	public String reSendActivationEmail() throws Exception {
		setUserResetHash();
		addActionMessage(sendActivationEmail(user, permissions));
		return SUCCESS;
	}

	private void setUserResetHash() {
		// Seeding the time in the reset hash so that each one will be
		// guaranteed unique
		user.setResetHash(Strings.hashUrlSafe("user" + user.getId() + String.valueOf(new Date().getTime())));
		userDAO.save(user);
	}

	// TODO Technical Debt: PICS-9613
	protected String sendActivationEmail(User user, Permissions permission) {
		try {
			String serverName = ServletActionContext.getRequest().getServerName();

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(5);
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			emailBuilder.setBccAddresses(EmailAddressUtils.PICS_MARKETING_EMAIL_ADDRESS_WITH_NAME);
			emailBuilder.addToken("user", user);
			String confirmLink = "https://" + serverName + "/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());
			emailBuilder.setPermissions(permission);

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setCriticalPriority();
			emailQueue.setHtml(true);

			emailSender.send(emailQueue);

			return getTextParameterized("AccountRecovery.EmailSent", user.getEmail());
		} catch (Exception e) {
			return getText("AccountRecovery.error.ResetEmailError");
		}
	}

	public String addToExclusionList() throws Exception {
		emailQueueDAO.addEmailAddressExclusions(user.getEmail(), permissions.getUserId());
		return SUCCESS;
	}

	private void removeFromExclusionList() throws SQLException {
		emailQueueDAO.removeEmailAddressExclusions(user.getEmail());
	}

	public Locale getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(Locale selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public Locale[] getSortedLocales() {
		Locale[] locales = Locale.getAvailableLocales();
		Comparator<Locale> localeComparator = new Comparator<Locale>() {
			public int compare(Locale locale1, Locale locale2) {
				return locale1.getDisplayName().compareTo(locale2.getDisplayName());
			}
		};
		Arrays.sort(locales, localeComparator);

		return locales;
	}

	public List<Locale> getSortedSpokenLanguages() {
		List<Locale> languages = user.getSpokenLanguages();
		Comparator<Locale> localeComparator = new Comparator<Locale>() {
			public int compare(Locale locale1, Locale locale2) {
				return locale1.getDisplayName().compareTo(locale2.getDisplayName());
			}
		};
		Collections.sort(languages, localeComparator);
		return languages;
	}

	public List<Country> getSortedCountriesServiced() {
		List<String> countries = user.getCountriesServiced();
		List<Country> listOfCountriesServiced = new ArrayList<Country>();
		
		for (String countryString: countries) {
			listOfCountriesServiced.add(countryDAO.findbyISO(countryString));
		}

		Collections.sort(listOfCountriesServiced);
		return listOfCountriesServiced;
	}
	
	public List<Country> getSortedCountryList() {
		List<Country> countryList = countryDAO.findAll();
		Collections.sort(countryList, new Comparator<Country>() {
			public int compare(Country o1, Country o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		countryList.add(0, countryDAO.find("GB"));
		countryList.add(0, countryDAO.find("CA"));
		countryList.add(0, countryDAO.find("US"));

		return countryList;
	}

	
	public Locale getRemoveLanguage() {
		return removeLanguage;
	}

	public void setRemoveLanguage(Locale removeLanguage) {
		this.removeLanguage = removeLanguage;
	}

	public String getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
	}

	public String getRemoveCountry() {
		return removeCountry;
	}

	public void setRemoveCountry(String removeCountry) {
		this.removeCountry = removeCountry;
	}

	public String addLanguage() {
		user.getSpokenLanguages().add(selectedLanguage);
		userDAO.save(user);
		
		
		return SUCCESS;
	}

	public String removeLanguage() throws Exception {
		startup();
		Iterator<Locale> iterator = user.getSpokenLanguages().iterator();
		while (iterator.hasNext()) {
			Locale locale = iterator.next();
			if (locale.toString().equals(removeLanguage.toString())) {
				iterator.remove();
			}
		}

		userDAO.save(user);
		return setUrlForRedirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId());
	}

	public String addCountry() {
		List<String> countriesServiced = user.getCountriesServiced();
		
		if (countriesServiced.contains(selectedCountry)) {
			this.addActionError("Country Already Selected");
			return SUCCESS;
		}
		
		countriesServiced.add(selectedCountry);
		userDAO.save(user);

		addActionMessage("Successfully added country");
		return SUCCESS;
	}

	public String removeCountry() throws Exception {
		startup();
		user.getCountriesServiced().remove(removeCountry);
		userDAO.save(user);

		addActionMessage("Successfully removed country");
		return setUrlForRedirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId());
	}
}
