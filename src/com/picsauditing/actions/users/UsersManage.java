package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.PasswordValidator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountRecovery;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Report;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class UsersManage extends PicsActionSupport implements Preparable {

	protected int accountId = 0;
	protected User user;
	protected Account account;

	protected String password1;
	protected String password2;
	protected boolean sendActivationEmail = false;
	protected boolean setPrimaryAccount = false;

	protected String filter = null;
	protected List<OperatorAccount> facilities = null;
	protected Report search = null;
	protected List<User> userList = null;

	protected String isGroup = "";
	protected String isActive = "Yes";

	protected boolean hasAllOperators = false;

	protected boolean conAdmin = false;
	protected boolean conBilling = false;
	protected boolean conSafety = false;
	protected boolean conInsurance = false;

	protected AccountDAO accountDAO;
	protected OperatorAccountDAO operatorDao;
	protected UserDAO userDAO;
	protected UserAccessDAO userAccessDAO;

	public UsersManage(AccountDAO accountDAO, OperatorAccountDAO operatorDao, UserDAO userDAO,
			UserAccessDAO userAccessDAO) {
		this.accountDAO = accountDAO;
		this.operatorDao = operatorDao;
		this.userDAO = userDAO;
		this.userAccessDAO = userAccessDAO;
	}

	@Override
	public void prepare() throws Exception {
		int id = getParameter("user.id");
		if (id > 0) {
			user = userDAO.find(id);
			if (user != null) {
				account = user.getAccount();
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
			}
		}

		int aID = getParameter("accountId");
		if (account == null && aID > 0)
			account = accountDAO.find(aID);

		// checking to see if primary account user is set
		if (account != null && account.getPrimaryContact() == null)
			setPrimaryAccount = true;
	}

	public String execute() throws Exception {
		loadPermissions();

		if (!permissions.isLoggedIn()) {
			return LOGIN;
		}

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.EditUsers);

		if (account == null) {
			// This would happen if I'm looking at my own account, but not a
			// user yet
			account = accountDAO.find(permissions.getAccountId());
		}
		accountId = account.getId();

		// Make sure we can edit users in this account
		if (permissions.getAccountId() != accountId)
			permissions.tryPermission(OpPerms.AllOperators);

		if ("newUser".equalsIgnoreCase(button)) {
			if (user.getIsGroup().isTrue())
				sendActivationEmail = false;
			else
				sendActivationEmail = true;
			return SUCCESS;
		}

		if (user == null) {
			return SUCCESS;
		}

		if ("resetPassword".equals(button)) {
			// Seeding the time in the reset hash so that each one will be
			// guaranteed unique
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			userDAO.save(user);

			addActionMessage(AccountRecovery.sendRecoveryEmail(user));
		}

		if ("Save".equalsIgnoreCase(button)) {
			if (!isOK()) {
				userDAO.clear();
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
						userDAO.clear();
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
					account = accountDAO.find(accountId);
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
						user.getOwnedPermissions().remove(
								userAccessDAO.findByUserAndOpPerm(user.getId(), OpPerms.ContractorAdmin));
						userAccessDAO.remove((userAccessDAO.findByUserAndOpPerm(user.getId(), OpPerms.ContractorAdmin))
								.getId());
					}
				}

				if (!userPerms.contains(OpPerms.ContractorBilling)) {
					if (conBilling)
						user.addOwnedPermissions(OpPerms.ContractorBilling, permissions.getUserId());
				} else {
					if (!conBilling) {
						user.getOwnedPermissions().remove(
								userAccessDAO.findByUserAndOpPerm(user.getId(), OpPerms.ContractorBilling));
						userAccessDAO.remove((userAccessDAO
								.findByUserAndOpPerm(user.getId(), OpPerms.ContractorBilling)).getId());
					}
				}

				if (!userPerms.contains(OpPerms.ContractorSafety)) {
					if (conSafety)
						user.addOwnedPermissions(OpPerms.ContractorSafety, permissions.getUserId());
				} else {
					if (!conSafety) {
						user.getOwnedPermissions().remove(
								userAccessDAO.findByUserAndOpPerm(user.getId(), OpPerms.ContractorSafety));
						userAccessDAO
								.remove((userAccessDAO.findByUserAndOpPerm(user.getId(), OpPerms.ContractorSafety))
										.getId());
					}
				}

				if (!userPerms.contains(OpPerms.ContractorInsurance)) {
					if (conInsurance)
						user.addOwnedPermissions(OpPerms.ContractorInsurance, permissions.getUserId());
				} else {
					if (!conInsurance) {
						user.getOwnedPermissions().remove(
								userAccessDAO.findByUserAndOpPerm(user.getId(), OpPerms.ContractorInsurance));
						userAccessDAO.remove((userAccessDAO.findByUserAndOpPerm(user.getId(),
								OpPerms.ContractorInsurance)).getId());
					}
				}

				if (user.getOwnedPermissions().size() == 0 && user.isActiveB()) {
					addActionError("Please add a permission to this user");
					return SUCCESS;
				}

				// Send activation email if set
				if (sendActivationEmail && user.getId() == 0) {
					try {
						EmailBuilder emailBuilder = new EmailBuilder();
						emailBuilder.setFromAddress(permissions.getEmail());
						emailBuilder.setTemplate(5); // New User Welcome
						emailBuilder.setPermissions(permissions);
						emailBuilder.setUser(user);
						user.setResetHash(Strings
								.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
						userDAO.save(user);
						String confirmLink = "http://www.picsauditing.com/Login.action?usern=" + user.getUsername()
								+ "&key=" + user.getResetHash() + "&button=reset";
						emailBuilder.addToken("confirmLink", confirmLink);
						// Account id hasn't been set. Still null value before
						// saving
						emailBuilder.addToken("accountname", accountDAO.find(accountId).getName());
						emailBuilder.setFromAddress("info@picsauditing.com");
						EmailQueue emailQueue = emailBuilder.build();
						emailQueue.setPriority(100);
						EmailSender.send(emailQueue);
					} catch (Exception e) {
						addActionError(e.getMessage());
						return SUCCESS;
					}
					addActionMessage("Activation Email sent to " + user.getEmail());
				}
			}

			try {
				user = userDAO.save(user);
				addActionMessage("User saved successfully.");
				if (setPrimaryAccount && user != null && !user.isGroup() && user.getAccount() != null)
					user.getAccount().setPrimaryContact(user);
			} catch (ConstraintViolationException e) {
				addActionError("That Username is already in use.  Please select another.");
				return SUCCESS;
			} catch (DataIntegrityViolationException e) {
				addActionError("That Username is already in use.  Please select another.");
				return SUCCESS;
			}
		}

		if ("Delete".equalsIgnoreCase(button)) {
			permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
			String message = "Cannot remove users who performed some actions in the system. Please inactivate them.";
			if (!user.isGroup()) {
				// This user is a user (not a group)
				if (!userDAO.canRemoveUser("ContractorAudit", user.getId(), null)) {
					addActionError(message);
					return SUCCESS;
				}
				if (!userDAO.canRemoveUser("ContractorAuditOperator", user.getId(), null)) {
					addActionError(message);
					return SUCCESS;
				}
				if (!userDAO.canRemoveUser("AuditData", user.getId(), null)) {
					addActionError(message);
					return SUCCESS;
				}
				if (!userDAO.canRemoveUser("ContractorOperator", user.getId(), null)) {
					addActionError(message);
					return SUCCESS;
				}
				if (!userDAO.canRemoveUser("UserAccess", user.getId(), "t.grantedBy.id = :userID")) {
					addActionError(message);
					return SUCCESS;
				}
				if (user.getAccount().getPrimaryContact() != null
						&& user.getId() == user.getAccount().getPrimaryContact().getId()) {
					// Putting primary user check last so that primary users
					// aren't switched that can't be deleted
					addActionError("Cannot remove the primary user for " + user.getAccount().getName()
							+ ". Please switch the primary user of this account and then attempt to delete them.");
					return SUCCESS;
				}
			}

			userDAO.remove(user);
			addActionMessage("Successfully removed "
					+ (user.isGroup() ? "group: " + user.getName() : "user: " + user.getUsername()));
			user = null;
		}

		return SUCCESS;
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
		if (user.getUsername() == null || user.getUsername().length() < 3)
			addActionError("Please choose a Username at least 3 characters long.");

		String result = Strings.validUserName(user.getUsername().trim());
		if (!result.equals("valid"))
			addActionError(result);

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
		}

		return getActionErrors().size() == 0;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
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

	public List<User> getUserList() {
		if (userList == null)
			userList = userDAO.findByAccountID(accountId, isActive, isGroup);
		return userList;
	}

	public List<OpPerms> getGrantablePermissions() {
		List<OpPerms> list = new ArrayList<OpPerms>();
		for (UserAccess perm : permissions.getPermissions()) {
			// I can grant these permissions
			if (perm.getGrantFlag() != null && perm.getGrantFlag() == true)
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
		list = userDAO.findByAccountID(accountId, "Yes", "Yes");
		// This used to only add groups you were a member of,
		// but this doesn't work for admins trying to add groups they aren't
		// members of
		// for (User group : activeGroups) {
		// if (permissions.hasPermission(OpPerms.AllOperators) ||
		// permissions.getGroups().contains(group.getId()))
		// list.add(group);
		// }

		if (user.isGroup() && permissions.hasPermission(OpPerms.AllOperators)
				&& permissions.getAccountId() != accountId) {
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

			list = userDAO.findByAccountID(accountId, "Yes", "");

			for (UserGroup userGroup : user.getMembers()) {
				// but users, already in the group
				list.remove(userGroup.getUser());
			}
		}
		list.remove(user);
		return list;
	}

	public List<UserLoginLog> getRecentLogins() {
		UserLoginLogDAO loginLogDao = (UserLoginLogDAO) SpringUtils.getBean("UserLoginLogDAO");
		return loginLogDao.findRecentLogins(user.getId(), 10);
	}

	public List<OperatorAccount> getFacilities() {
		facilities = operatorDao.findWhere(true, "");

		return facilities;
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
}
