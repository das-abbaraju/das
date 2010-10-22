package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.Indexer;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.UserSwitch;
import com.picsauditing.mail.Subscription;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ProfileEdit extends PicsActionSupport implements Preparable {
	protected User u;
	protected UserDAO dao;
	protected ContractorAccountDAO accountDao;
	protected UserSwitchDAO userSwitchDao;
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	protected String password1;
	protected String password2;
	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();
	protected String url;
	private Indexer indexer;
	
	private boolean goEmailSub = false;

	public ProfileEdit(UserDAO dao, ContractorAccountDAO accountDao, UserSwitchDAO userSwitchDao,
			EmailSubscriptionDAO emailSubscriptionDAO, Indexer indexer) {
		this.dao = dao;
		this.accountDao = accountDao;
		this.userSwitchDao = userSwitchDao;
		this.emailSubscriptionDAO = emailSubscriptionDAO;
		this.indexer = indexer;
	}

	public void prepare() throws Exception {
		getPermissions();
		u = dao.find(permissions.getUserId());
	}

	public String execute() throws Exception {
		loadPermissions();

		if (!permissions.isLoggedIn()) {
			redirect("Login.action?button=logout&msg=Your session has timed out. Please log back in");
			return LOGIN;
		}

		if (!permissions.hasPermission(OpPerms.EditProfile)) {
			addActionError("This user does not have access to Edit their Profile. Please contact your Administrator");
			return BLANK;
		}

		// u = dao.find(permissions.getUserId());

		if (button != null) {
			if (button.equals("Save Profile")) {
				permissions.tryPermission(OpPerms.EditProfile, OpType.Edit);

				dao.clear();

				if (dao.duplicateUsername(u.getUsername(), u.getId())) {
					addActionError("Another user is already using the username: " + u.getUsername());
					return SUCCESS;
				}

				String result = Strings.validUserName(u.getUsername().trim());
				if (!result.equals("valid")) {
					addActionError(result);
					return SUCCESS;
				}

				if (!Strings.isEmpty(password2)) {
					if (!password1.equals(password2))
						addActionError("Passwords don't match");

					if (!Strings.isEmpty(u.getEmail()) && !Strings.isValidEmail(u.getEmail()))
						addActionError("Please enter a valid email address. This is our main way of communicating with you so it must be valid.");

					if (getActionErrors().size() > 0)
						return SUCCESS;
					int maxHistory = 0;
					// u.getAccount().getPasswordPreferences().getMaxHistory()
					// TODO: Check is addPasswordToHistory is still needed
					u.addPasswordToHistory(password1, maxHistory);
					u.setEncryptedPassword(password1);
					if (!Strings.isEmpty(url) && u.isForcePasswordReset())
						redirect(url);

					u.setForcePasswordReset(false);
					permissions.setForcePasswordReset(false);
				}
				u.setPhoneIndex(Strings.stripPhoneNumber(u.getPhone()));
				permissions.setTimeZone(u);
				ActionContext.getContext().getSession().put("permissions", permissions);
				u = dao.save(u);
				indexer.runSingle(u, "users");

				addActionMessage("Your profile was saved successfully");
			}
		}

		return SUCCESS;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public List<UserSwitch> getSwitchTos() {
		return userSwitchDao.findByUserId(u.getId());
	}

	public List<UserLoginLog> getRecentLogins() {
		UserLoginLogDAO loginLogDao = (UserLoginLogDAO) SpringUtils.getBean("UserLoginLogDAO");
		return loginLogDao.findRecentLogins(u.getId(), 10);
	}

	public List<EmailSubscription> getEList() {
		if (eList.size() == 0) {
			if (!permissions.isCorporate()) {
				List<EmailSubscription> userEmail = emailSubscriptionDAO.findByUserId(permissions.getUserId());
				Map<Subscription, EmailSubscription> eMap = new HashMap<Subscription, EmailSubscription>();
				for (EmailSubscription emailSubscription : userEmail) {
					eMap.put(emailSubscription.getSubscription(), emailSubscription);
				}

				for (Subscription subscription : requiredSubscriptionList(permissions)) {
					EmailSubscription eSubscription = eMap.get(subscription);
					if (eSubscription == null) {
						eSubscription = new EmailSubscription();
						eSubscription.setSubscription(subscription);
					}
					eList.add(eSubscription);
				}
			}
		}
		return eList;
	}

	public List<Subscription> requiredSubscriptionList(Permissions permissions) {
		List<Subscription> subList = new ArrayList<Subscription>();
		for (Subscription subscription : Subscription.values()) {
			if (subscription.getRequiredPerms() == null || permissions.hasPermission(subscription.getRequiredPerms())) {
				if (permissions.isOperatorCorporate() && subscription.isRequiredForOperator()) {
					subList.add(subscription);
				} else if (permissions.isContractor() && subscription.isRequiredForContractor()) {
					subList.add(subscription);
				} else if (subscription.isRequiredForOperator() && subscription.isRequiredForContractor())
					subList.add(subscription);
			}
		}
		return subList;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setGoEmailSub(boolean goEmailSub) {
		this.goEmailSub = goEmailSub;
	}

	public boolean isGoEmailSub() {
		return goEmailSub;
	}

}
