package com.picsauditing.actions.users;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.model.group.GroupManagementService;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.InputValidator;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;

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
	private boolean usingVersion7Menus = false;
	// used to track whether or not this is being executed from a "Save" Action
	private boolean isSaveAction = false;
	private Locale selectedLanguage;
	private Locale removeLanguage;
	private String selectedCountry;
	private String removeCountry;

	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	protected UserAccessDAO userAccessDAO;
	@Autowired
	protected UserGroupDAO userGroupDAO;
	@Autowired
	protected UserSwitchDAO userSwitchDao;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	@Autowired
	private InputValidator inputValidator;
	@Autowired
	private FeatureToggle featureToggle;
    @Autowired
    protected UserManagementService userManagementService;
    @Autowired
    private GroupManagementService groupManagementService;

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

		if (!supportedLanguages.getStableLanguageLocales().contains(user.getLocale())) {
			user.setLocale(supportedLanguages.getNearestStableAndBetaLocale(user.getLocale()));
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
        if (userIsGroup.isTrue()) {
            user = groupManagementService.initializeNewGroup(account);
        } else {
            user = userManagementService.initializeNewUser(account);
        }
		return SUCCESS;
	}

	public String save() throws Exception {
		isSaveAction = true;
		startup();

		user.setIsGroup(userIsGroup);

        initializedNeededLazyCollections();

		validateInputAndRecordErrors();
		if (hasFieldErrors() || hasActionErrors()) {
            // TODO: the code to refresh the user on validation error has been here for a long time (years), but this seems
            // like really bad UX to me, shouldn't it put back the user entered values?
			userManagementService.resetUser(user);
			return INPUT_ERROR;
		}

        setUserAccountIfNeeded();

        if (user.getId() == 0) {
            newUser = true;
        }

        if (user.isGroup()) {
            if (groupManagementService.isGroupnameAvailable(user)) {
                groupManagementService.setUsernameToGeneratedGroupname(user);
                groupManagementService.saveWithAuditColumnsAndRefresh(user, permissions);
                addActionMessage(getText("UsersManage.GroupSavedSuccessfully"));
            } else {
                addActionError(getText("UsersManage.GroupnameNotAvailable"));
                groupManagementService.resetGroup(user);
                return SUCCESS;
            }
        } else {
            if (user.getAccount().isContractor()) {
                UserGroupManagementStatus status = userManagementService.contractorUserIsSavable(user, account, permissionsForContractorAccount());
                if (!status.isOk) {
                    addActionErrorFromStatus(status);
                    return SUCCESS;
                }
                updateUserRoles();
            }

            saveNonGroupUser();
        }

		if (newUser && (user.getAccount().isAdmin() || user.getAccount().isOperatorCorporate())) {
			return this.setUrlForRedirect("UsersManage.action?account=" + account.getId() + "&user=" + user.getId());
		}

		return SUCCESS;
	}

    private List<OpPerms> permissionsForContractorAccount() {
        List<OpPerms> permissionsBeingAdded = new ArrayList<OpPerms>();
        if (conAdmin) {
            permissionsBeingAdded.add(OpPerms.ContractorAdmin);
        }
        if (conInsurance) {
            permissionsBeingAdded.add(OpPerms.ContractorInsurance);
        }
        if (conSafety) {
            permissionsBeingAdded.add(OpPerms.ContractorSafety);
        }
        if (conBilling) {
            permissionsBeingAdded.add(OpPerms.ContractorBilling);
        }
        return permissionsBeingAdded;
    }

    private void saveNonGroupUser() throws Exception {
        user.setPhoneIndex(Strings.stripPhoneNumber(user.getPhone()));

        updateShadowCSR();

        // Send activation email if set
        if (sendActivationEmail && user.getId() == 0) {
            setUserResetHash();
            addActionMessage(sendActivationEmail(user, permissions));
        }

        if (setPrimaryAccount && user != null && !user.isGroup() && user.getAccount() != null) {
            user.getAccount().setPrimaryContact(user);
        }
        user.setUsingVersion7Menus(isUsingVersion7Menus());
        if (!featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
            user.setUsingDynamicReports(isUsingVersion7Menus());
        }

        user.updateDisplayNameBasedOnFirstAndLastName();

        try {
            user = userManagementService.saveWithAuditColumnsAndRefresh(user, permissions);
            addActionMessage(getText("UsersManage.UserSavedSuccessfully"));
        } catch (ConstraintViolationException e) {
            addActionError(getText("UsersManage.UsernameInUse"));
        } catch (DataIntegrityViolationException e) {
            addActionError(getText("UsersManage.UsernameInUse"));
        } finally {
            for (UserAccess userAccess : accessToBeRemoved) {
                userAccessDAO.remove(userAccess);
            }
        }
    }

    private void setUserAccountIfNeeded() {
        if (user.getAccount() == null) {
            user.setAccount(new Account());
            if (user.getId() == 0) {
                user.setAccount(account);
            } else if (!permissions.hasPermission(OpPerms.AllOperators)) {
                user.getAccount().setId(permissions.getAccountId());
            }
        }
    }

    private void initializedNeededLazyCollections() {
        user.getGroups().size();
        user.getOwnedPermissions().size();
    }

	private void updateUserRoles() {
		Set<OpPerms> userPerms = user.getOwnedOpPerms();

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
		if (account.getUsersByRole(role).size() > 1) {
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

    // NOTE: unlocking does not validate or save any of the form fields - it only unlocks
	public String unlock() throws Exception {
		startup();

        userManagementService.unlock(user);

		addActionMessage(getText("UsersManage.Unlocked"));

		return setUrlForRedirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId());
	}

	public String move() throws Exception {
		startup();

        UserGroupManagementStatus status = validateUserOrGroupIsMovable();
        if (!status.isOk) {
            addActionErrorFromStatus(status);
        } else {
            userManagementService.moveUserToNewAccount(user, moveToAccount);
    		addActionMessage(getTextParameterized("UsersManage.SuccessfullyMoved", user.getName(), user.getAccount()
				.getName()));
        }
		return setUrlForRedirect("UsersManage.action?account=" + user.getAccount().getId() + "&user=" + user.getId());
	}

    private UserGroupManagementStatus validateUserOrGroupIsMovable() {
        UserGroupManagementStatus status;
        if (!user.isGroup()) {
            status = userManagementService.userIsMovable(user);
        } else {
            status = groupManagementService.groupIsMovable(user);
        }
        return status;
    }


    @RequiredPermission(value = OpPerms.EditUsers, type = OpType.Edit)
	public String deactivate() throws Exception {
		startup();
        UserGroupManagementStatus status = validateUserOrGroupIsDeactivatable();
        if (!status.isOk) {
            addActionErrorFromStatus(status);
        } else {
            deactivateUserOrGroup();
            addActionMessage(getTextParameterized("UsersManage.UserInactivated", user.isGroup() ? 1 : 0,
                user.isGroup() ? user.getName() : user.getUsername()));
        }
		return SUCCESS;
	}

    private void deactivateUserOrGroup() throws Exception {
        if (!user.isGroup()) {
            userManagementService.deactivate(user);
        } else {
            groupManagementService.deactivate(user);
        }
    }

    private UserGroupManagementStatus validateUserOrGroupIsDeactivatable() {
        UserGroupManagementStatus status;
        if (!user.isGroup()) {
            status = userManagementService.userIsDeactivatable(user, account);
        } else {
            status = groupManagementService.groupIsDeactivatable(user);
        }
        return status;
    }

    @RequiredPermission(value = OpPerms.EditUsers, type = OpType.Edit)
	public String activate() throws Exception {
		startup();

        userManagementService.activateUser(user);

		removeFromExclusionList();

		addActionMessage(getTextParameterized("UsersManage.UserActivated", user.isGroup() ? 1 : 0,
				user.isGroup() ? user.getName() : user.getUsername()));

		// when an user is reactived, refresh the page to change the isactive status
		return this.setUrlForRedirect("UsersManage.action?account=" + account.getId() + "&user=" + user.getId());
	}

	@RequiredPermission(value = OpPerms.EditUsers, type = OpType.Delete)
	public String delete() throws Exception {
		startup();

        UserGroupManagementStatus status = validateUserOrGroupIsDeletable();
        if (!status.isOk) {
            addActionErrorFromStatus(status);
        } else {
            deleteUserOrGroup();
    		addActionMessage(getTextParameterized("UsersManage.SuccessfullyRemoved", user.isGroup() ? 1 : 0,
				user.isGroup() ? user.getName() : user.getUsername()));
	    	user = null;
        }

		return SUCCESS;
	}

    private void deleteUserOrGroup() throws Exception {
        if (!user.isGroup()) {
            userManagementService.delete(user);
        } else {
            groupManagementService.delete(user);
        }
    }

    private void addActionErrorFromStatus(UserGroupManagementStatus status) {
        if (Strings.isEmpty(status.errorDetail)) {
            addActionError(getText(status.notOkErrorKey));
        } else {
            addActionError(getTextParameterized(status.notOkErrorKey, status.errorDetail));
        }
    }

    private UserGroupManagementStatus validateUserOrGroupIsDeletable() {
        UserGroupManagementStatus status;
        if (!user.isGroup()) {
            status = userManagementService.userIsDeletable(user);
        } else {
            status = groupManagementService.groupIsDeletable(user);
        }
        return status;
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
			if (!permissions.getOperatorChildren().contains(account.getId())) {
				permissions.tryPermission(OpPerms.AllOperators);
			}
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

	public void validateInputAndRecordErrors() {
		if (user == null) {
			addActionError(getText("UsersManage.NoUserFound"));
			return;
		}

		String errorMessageKey;

		if (user.isGroup()) {
			errorMessageKey = inputValidator.validateName(user.getName());
			addFieldErrorIfMessage("user.name", errorMessageKey);

			return;
		}

		errorMessageKey = inputValidator.validateFirstName(user.getFirstName());
		addFieldErrorIfMessage("user.firstName", errorMessageKey);

		errorMessageKey = inputValidator.validateLastName(user.getLastName());
		addFieldErrorIfMessage("user.lastName", errorMessageKey);

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
			if ("Yes".equals(isGroup) || "No".equals(isGroup)) {
				sql.addWhere("isGroup = '" + isGroup + "'");
			}

			if ("Yes".equals(isActive) || "No".equals(isActive)) {
				sql.addWhere("isActive = '" + isActive + "'");
			}

			userList = db.select(sql.toString(), false);
		}
		return userList;
	}

	public List<OpPerms> getGrantablePermissions() {
		List<OpPerms> list = new ArrayList<OpPerms>();
		for (com.picsauditing.access.UserAccess perm : permissions.getPermissions()) {
			// I can grant these permissions
			if (perm.isGrantFlag()) {
				list.add(perm.getOpPerm());
			}
		}

		for (UserAccess perm : user.getOwnedPermissions()) {
			// but these permissions, have already been granted
			list.remove(perm.getOpPerm());
		}
		Collections.sort(list, OpPerms.PermissionComparator);

		return list;
	}

	public List<User> getAddableGroups() {
		return userManagementService.getAddableGroups(permissions, account, user);
	}

	public List<UserSwitch> getSwitchTos() {
		return userSwitchDao.findByUserId(user.getId());
	}

	public List<User> getAddableMembers() {
		List<User> list = new ArrayList<User>();

		if (!permissions.hasPermission(OpPerms.EditUsers, OpType.Edit)) {
			return list;
		}

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
				if (o1 == null) {
					return -1;
				}
				if (o2 == null) {
					return 1;
				}
				return o1.getGroup().getName().compareTo(o2.getGroup().getName());
			}
		};
	}

	public Comparator<UserGroup> getUserNameComparator() {
		return new Comparator<UserGroup>() {

			@Override
			public int compare(UserGroup o1, UserGroup o2) {
				if (o1 == null) {
					return -1;
				}
				if (o2 == null) {
					return 1;
				}
				return o1.getUser().compareTo(o2.getUser());
			}
		};
	}

	public List<BasicDynaBean> getAccountList() throws SQLException {
		if (user != null) {
			if (permissions.isAdmin()) {
				String like = ((String[]) ActionContext.getContext().getParameters().get("q"))[0];
				if (like == null) {
					like = "";
				}

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
				if (userGroup.getGroup().isGroup() && userGroup.getGroup().getId() == User.GROUP_CSR) {
					return true;
				}
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
						if (currentUserGroup.equals(userGroup) || currentUserGroup.getUser().isGroup()) {
							iterator.remove();
						}
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

	public boolean isUsingVersion7Menus() {
		return usingVersion7Menus;
	}

	public void setUsingVersion7Menus(boolean usingVersion7Menus) {
		this.usingVersion7Menus = usingVersion7Menus;
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
		if (user.getAccount().isContractor()) {
			return true;
		}

		for (UserAccess userAccess : user.getPermissions()) {
			if (userAccess.getOpPerm().equals(OpPerms.EditProfile)) {
				return true;
			}

		}
		return false;
	}

    // TODO Technical Debt: PICS-9613
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
