package com.picsauditing.access;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.strutsutil.AjaxUtils;

/**
 * This is the main class that is stored for each user containing information if
 * they are logged in, which groups they're in, and what permission(s) they have
 * 
 * Warning: this class is stored in the session Make sure you keep the footprint
 * very small
 */
public class Permissions implements Serializable {

	private static final long serialVersionUID = -3120292424348289561L;

	private int userID;
	private boolean loggedIn = false;
	private boolean forcePasswordReset = false;
	private Set<Integer> groups = new HashSet<Integer>();
	private Set<Integer> inheritedGroups = new HashSet<Integer>();
	private Set<UserAccess> permissions = new HashSet<UserAccess>();
	private boolean canSeeInsurance = false;
	private Set<Integer> corporateParent = new HashSet<Integer>();
	private Set<Integer> operatorChildren = new HashSet<Integer>();
	private Set<Integer> visibleAuditTypes = new HashSet<Integer>();

	private String username;
	private String name;
	private int accountID;
	private int topAccountID;
	private String accountName;
	private String accountType;
	private String email;
	private String phone;
	private String fax;
	private TimeZone timezone = null;
	private Locale locale;
	private String country;

	private int adminID;
	private boolean approvesRelationships = false;
	private boolean requiresOQ = false;
	private boolean requiresCompetencyReview = false;
	private boolean active = false;
	private boolean gcContractor = false;
	private boolean gcOperator = false;
	private AccountStatus accountStatus = AccountStatus.Pending;

	private int shadowedUserID;
	private String shadowedUserName;

	public void clear() {
		userID = 0;
		loggedIn = false;
		active = false;
		username = "";
		name = "";
		email = "";
		phone = "";
		fax = "";
		timezone = null;
		locale = Locale.ENGLISH;
		accountID = 0;
		accountName = "";
		accountType = "";
		country = "";
		accountStatus = AccountStatus.Pending;
		approvesRelationships = false;
		requiresOQ = false;
		requiresCompetencyReview = false;
		canSeeInsurance = false;

		gcContractor = false;
		gcOperator = false;

		adminID = 0;
		topAccountID = 0;

		shadowedUserID = 0;
		shadowedUserName = "";

		permissions.clear();
		groups.clear();
		inheritedGroups.clear();
		visibleAuditTypes.clear();
		corporateParent.clear();
		operatorChildren.clear();
	}

	public void login(User user) throws Exception {
		try {
			clear();
			userID = user.getId();
			if (userID == 0)
				throw new Exception("Missing User");

			loggedIn = true;
			forcePasswordReset = user.isForcePasswordReset();
			active = user.isActiveB();
			username = user.getUsername();
			name = user.getName();
			email = user.getEmail();
			phone = user.getPhone();
			fax = user.getFax();
			locale = user.getLocale();
			shadowedUserID = (user.getShadowedUser() != null ? user.getShadowedUser().getId() : userID);
			shadowedUserName = (user.getShadowedUser() != null ? user.getShadowedUser().getName() : username);
			if (user.getAccount().getCountry() != null)
				country = user.getAccount().getCountry().getIsoCode();
			else
				country = "";

			setTimeZone(user);

			setAccountPerms(user);

		} catch (Exception ex) {
			// All or nothing, if something went wrong, then clear it all
			clear();
			throw ex;
		}
	}

	public void setTimeZone(User user) {
		timezone = user.getTimezone();
	}

	public void setAccountPerms(User user) throws Exception {
		try {
			accountID = user.getAccount().getId();
			topAccountID = accountID;
			accountType = user.getAccount().getType();
			accountName = user.getAccount().getName();
			accountStatus = user.getAccount().getStatus();
			requiresOQ = user.getAccount().isRequiresOQ();
			requiresCompetencyReview = user.getAccount().isRequiresCompetencyReview();

			if (isOperatorCorporate()) {
				OperatorAccount operator = (OperatorAccount) user.getAccount();
				visibleAuditTypes = operator.getVisibleAuditTypes();

				approvesRelationships = YesNo.Yes.equals(operator.getApprovesRelationships());

				if (isOperator()) {
					if (operator.getParent() != null)
						topAccountID = operator.getParent().getId();

					for (Facility facility : operator.getCorporateFacilities()) {
						corporateParent.add(facility.getCorporate().getId());
						if (facility.getCorporate().isPrimaryCorporate()) {
							topAccountID = facility.getCorporate().getId();
						}
					}

					if (operator.getCanSeeInsurance().isTrue())
						canSeeInsurance = true;
				}
				if (isCorporate()) {
					// Supporting Hub Accounts to See other Connected Corporate
					// Accounts
					if (operator.getParent() != null) {
						operatorChildren.add(operator.getParent().getId());
					}
					for (Facility facility : operator.getOperatorFacilities()) {
						operatorChildren.add(facility.getOperator().getId());

						if (facility.getOperator().getCanSeeInsurance().isTrue())
							canSeeInsurance = true;
					}
				}

				if (operator.isGeneralContractor())
					gcOperator = true;
			}

			for (com.picsauditing.jpa.entities.UserAccess ua : user.getPermissions()) {
				permissions.add(new UserAccess(ua));
			}

			if (isContractor()) {
				UserAccess conProfileEdit = new UserAccess();
				conProfileEdit.setOpPerm(OpPerms.EditProfile);
				conProfileEdit.setEditFlag(true);
				permissions.add(conProfileEdit);
			}

			for (UserGroup u : user.getGroups()) {
				if (u.getGroup().isGroup())
					groups.add(u.getGroup().getId());
			}

			for (UserGroup u : user.getGroups()) {
				addInheritedUserGroup(inheritedGroups, u);
			}

		} catch (Exception ex) {
			// All or nothing, if something went wrong, then clear it all
			clear();
			throw ex;
		}
	}

	public int getUserId() {
		return userID;
	}

	public String getUserIdString() {
		return Integer.toString(userID);
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isForcePasswordReset() {
		return forcePasswordReset;
	}

	public void setForcePasswordReset(boolean forcePasswordReset) {
		this.forcePasswordReset = forcePasswordReset;
	}

	public Set<Integer> getGroups() {
		return groups;
	}

	public Set<Integer> getInheritedGroups() {
		return inheritedGroups;
	}

	public String getUsername() {
		return username;
	}

	public int getAccountId() {
		return accountID;
	}

	public String getAccountIdString() {
		return Integer.toString(accountID);
	}

	public int getTopAccountID() {
		return topAccountID;
	}

	public String getAccountType() {
		return accountType;
	}

	public String getAccountName() {
		return accountName;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getFax() {
		return fax;
	}

	public int getAdminID() {
		return adminID;
	}

	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}

	public String getName() {
		return name;
	}

	/**
	 * This gets the shadowed user from the User object, if it's set. Otherwise
	 * this returns the user's own id
	 * 
	 * @return user ID or shadowed user ID
	 */
	public int getShadowedUserID() {
		return shadowedUserID;
	}

	public String getShadowedUserName() {
		return shadowedUserName;
	}

	/**
	 * Does this user have 'oType' access to 'opPerm'
	 * 
	 * @param opPerm
	 *            OSHA, ContractorDetails, UserAdmin, etc
	 * @param oType
	 *            View, Edit, Delete, or Grant
	 * @return
	 */
	public boolean hasPermission(OpPerms opPerm, OpType oType) {
		for (UserAccess perm : permissions) {
			if (opPerm.isForContractor() && this.isContractor() && perm.getOpPerm() == OpPerms.ContractorAdmin)
				return true;
			if (opPerm == perm.getOpPerm()) {
				if (oType == OpType.Edit)
					return perm.isEditFlag();
				else if (oType == OpType.Delete)
					return perm.isDeleteFlag();
				else if (oType == OpType.Grant)
					return perm.isGrantFlag();
				// Default to OpType.View
				return perm.isViewFlag();
			}
		}
		return false;
	}

	public boolean hasPermission(OpPerms opPerm) {
		return this.hasPermission(opPerm, OpType.View);
	}

	public void tryPermission(OpPerms opPerm, OpType oType) throws NoRightsException {
		if (this.hasPermission(opPerm, oType))
			return;
		throw new NoRightsException(opPerm, oType);
	}

	public void tryPermission(OpPerms opPerm) throws NoRightsException {
		this.tryPermission(opPerm, OpType.View);
	}

	public boolean loginRequired(javax.servlet.http.HttpServletResponse response, String returnURL) throws IOException {
		if (this.loggedIn)
			return true;
		if (returnURL != null && returnURL.length() > 0) {
			Cookie fromCookie = new Cookie("from", returnURL);
			fromCookie.setMaxAge(3600);
			response.addCookie(fromCookie);
		}
		Cookie c = new Cookie("PICSCookiesEnabled", "true");
		c.setMaxAge(60);
		ServletActionContext.getResponse().addCookie(c);
		return false;
	}

	public boolean loginRequired(javax.servlet.http.HttpServletResponse response) throws IOException {
		return this.loginRequired(response, "");
	}

	public boolean loginRequired(javax.servlet.http.HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		if (AjaxUtils.isAjax(request)) {
			return this.loginRequired(response);
		} else {
			String url = request.getRequestURI();
			if (request.getQueryString() != null)
				url += "?" + request.getQueryString();
			return this.loginRequired(response, url);
		}
	}

	public boolean hasGroup(Integer group) {
		return groups.contains(group);
	}

	public boolean hasInheritedGroup(Integer group) {
		return inheritedGroups.contains(group);
	}

	public boolean isContractor() {
		return "Contractor".equals(this.accountType);
	}

	public boolean isCorporate() {
		return "Corporate".equals(this.accountType);
	}

	public boolean isOperator() {
		return "Operator".equals(this.accountType);
	}

	public boolean isAssessment() {
		return "Assessment".equals(this.accountType);
	}

	/**
	 * True if operator or corporate
	 * 
	 * @return
	 */
	public boolean isOperatorCorporate() {
		return isOperator() || isCorporate();
	}

	/**
	 * @return
	 */
	public boolean isAdmin() {
		// return this.hasGroup(10);
		return seesAllContractors();
	}

	public boolean seesAllContractors() {
		return this.hasPermission(OpPerms.AllContractors);
	}

	public boolean isDeveloperEnvironment() {
		return this.hasPermission(OpPerms.DevelopmentEnvironment);
	}

	/**
	 * @return
	 */
	public boolean isAuditor() {
		return hasGroup(11);
	}

	public boolean isMarketing() {
		return hasGroup(10801);
	}

	public boolean isIndependentAuditor() {
		return hasGroup(11265);
	}

	public boolean isPicsEmployee() {
		return (Account.PicsID == this.accountID);
	}

	/**
	 * Is the logged in user an non-PICS employee auditor?
	 * 
	 * @return
	 */
	public boolean isOnlyAuditor() {
		if (!isPicsEmployee())
			return false;
		if (isAdmin())
			return false;
		return this.hasGroup(11);
	}

	public boolean isInsuranceOnlyContractorUser() {
		return (isContractor() && hasPermission(OpPerms.ContractorInsurance) && !hasPermission(OpPerms.ContractorAdmin)
				&& !hasPermission(OpPerms.ContractorBilling) && !hasPermission(OpPerms.ContractorSafety));
	}

	public Set<UserAccess> getPermissions() {
		return permissions;
	}

	public boolean isApprovesRelationships() {
		return approvesRelationships;
	}

	public Set<Integer> getCorporateParent() {
		return corporateParent;
	}

	public Set<Integer> getOperatorChildren() {
		return operatorChildren;
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public Set<Integer> getVisibleAccounts() {
		Set<Integer> visibleAccounts = new HashSet<Integer>();
		visibleAccounts.add(accountID);
		if (isCorporate())
			visibleAccounts.addAll(operatorChildren);
		if (isOperator()) {
			visibleAccounts.add(topAccountID);
			visibleAccounts.addAll(corporateParent);
		}
		return visibleAccounts;
	}

	public boolean isActive() {
		return active;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	/**
	 * user.getAccount().getCountry().getIsoCode()
	 * 
	 * @return
	 */
	public String getCountry() {
		return country;
	}

	public boolean isRequiresCompetencyReview() {
		return requiresCompetencyReview;
	}

	public boolean isRequiresOQ() {
		return requiresOQ;
	}

	public boolean isCanSeeInsurance() {
		return canSeeInsurance;
	}

	public boolean isGcContractor() {
		return gcContractor;
	}
	
	public boolean isGcOperator() {
		return gcOperator;
	}

	public boolean isGeneralContractor() {
		return gcContractor || gcOperator;
	}

	public boolean canSeeAudit(AuditType auditType) {
		if (isContractor())
			return auditType.isCanContractorView();
		if (isPicsEmployee())
			return true;
		if (isOperatorCorporate()) {
			if (!auditType.isCanOperatorView())
				return false;
			return getVisibleAuditTypes().contains(auditType.getId());
		}
		return false;
	}

	/**
	 * 
	 * @return Map of AuditTypeID to OperatorID (aka governing body)
	 */
	public Set<Integer> getVisibleAuditTypes() {
		return visibleAuditTypes;
	}

	public boolean isCanAddRuleForOperator(OperatorAccount operator) {
		if (this.hasPermission(OpPerms.AuditRuleAdmin))
			return true;

		if (operator != null) {
			if (this.isPicsEmployee() && (operator.isDemo() || operator.getStatus().isPending()))
				return true;

			for (AccountUser accUser : operator.getAccountUsers()) {
				if (accUser.getUser().getId() == this.getUserId())
					return true;
			}

			for (OperatorAccount child : operator.getOperatorChildren()) {
				for (AccountUser childAccUser : child.getAccountUsers()) {
					if (childAccUser.getUser().getId() == this.getUserId())
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * Translators need the ability to switch accounts and retain the ability to
	 * do translations
	 */
	public void setTranslatorOn() {
		UserAccess ua = new UserAccess();
		ua.setOpPerm(OpPerms.Translator);
		ua.setEditFlag(true);
		ua.setDeleteFlag(true);

		this.permissions.add(ua);
	}

	private void addInheritedUserGroup(Set<Integer> inheritedGroups, UserGroup userGroup) {
		if (userGroup.getGroup() != null && userGroup.getGroup().getGroups() != null) {
			for (UserGroup group : userGroup.getGroup().getGroups()) {
				if (group.getGroup().isGroup()) {
					inheritedGroups.add(group.getGroup().getId());
					addInheritedUserGroup(inheritedGroups, group);
				}
			}
		}

		return;
	}
}
