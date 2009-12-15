package com.picsauditing.access;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Locale;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.YesNo;

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
	private Set<Integer> groups = new HashSet<Integer>();
	private Set<UserAccess> permissions = new TreeSet<UserAccess>();
	private Set<Integer> canSeeAudits = new HashSet<Integer>();
	private Set<Integer> canEditAudits = new HashSet<Integer>();
	private Set<Integer> corporateParent = new HashSet<Integer>();
	private Set<Integer> operatorChildren = new HashSet<Integer>();
	private Set<Integer> visibleCAOs = new HashSet<Integer>();
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
	private Set<String> accountCountries = new HashSet<String>();
	private Locale locale;

	private int adminID;
	private boolean approvesRelationships = false;
	private boolean active = false;
	private boolean accountActive = false;

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
		locale = null;
		accountID = 0;
		accountName = "";
		accountType = "";
		accountActive = false;
		accountCountries.clear();
		approvesRelationships = false;

		adminID = 0;
		topAccountID = 0;

		permissions.clear();
		groups.clear();
		canSeeAudits.clear();
		canEditAudits.clear();
		visibleCAOs.clear();
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
			active = user.isActiveB();
			username = user.getUsername();
			name = user.getName();
			email = user.getEmail();
			phone = user.getPhone();
			fax = user.getFax();
			locale = Locale.valueOf(ActionContext.getContext().getLocale());

			setTimeZone(user);

			setAccountPerms(user);

		} catch (Exception ex) {
			// All or nothing, if something went wrong, then clear it all
			clear();
			throw ex;
		}
	}

	public void setTimeZone(User user) {
		timezone = TimeZone.getTimeZone(user.getTimezone());
	}

	public void setAccountPerms(User user) throws Exception {
		try {
			accountID = user.getAccount().getId();
			topAccountID = accountID;
			accountType = user.getAccount().getType();
			accountName = user.getAccount().getName();
			accountActive = user.getAccount().isActiveB();

			if (isContractor()) {
				ContractorAccount contractor = (ContractorAccount) user.getAccount();
				for (ContractorOperator co : contractor.getOperators()) {
					if (co.getOperatorAccount().getCountry() != null)
						accountCountries.add(co.getOperatorAccount().getCountry().getIsoCode());
				}
			}

			if (isOperatorCorporate()) {
				OperatorAccount operator = (OperatorAccount) user.getAccount();

				if (operator.getCountry() != null)
					accountCountries.add(operator.getCountry().getIsoCode());

				if (isOperator()) {
					if (operator.getParent() != null)
						topAccountID = operator.getParent().getId();

					approvesRelationships = YesNo.Yes.equals(operator.getApprovesRelationships());
					for (Facility facility : operator.getCorporateFacilities()) {
						corporateParent.add(facility.getCorporate().getId());
						if (facility.getCorporate().isPrimaryCorporate()) {
							topAccountID = facility.getCorporate().getId();
						}
					}

					if (operator.getCanSeeInsurance().isTrue())
						visibleCAOs.add(operator.getInheritInsuranceCriteria().getId());

					loadAuditTypes(operator);
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
							visibleCAOs.add(facility.getOperator().getInheritInsuranceCriteria().getId());

						/*
						 * NOTE!!! There is a big hole here with this logic If
						 * corporate has two operators A & B A uses PQF only B
						 * uses PQF and Desktop Another Operator C uses PQF and
						 * Desktop Contractor signs up for operators A & C
						 * Corporate will be able to incorrectly see the desktop
						 * for that Contractor
						 * 
						 * One solution would be to add CAOs for each audit and
						 * operator This would allow us to restrict permissions
						 * to view each audit for given operator or corporate
						 * account. This could be useful if we want to
						 * eventually sell access for an operator to each audit
						 */
						loadAuditTypes(facility.getOperator());
					}
				}
			}
			permissions = user.getPermissions();

			for (UserGroup u : user.getGroups())
				groups.add(u.getGroup().getId());

		} catch (Exception ex) {
			// All or nothing, if something went wrong, then clear it all
			clear();
			throw ex;
		}
	}

	private void loadAuditTypes(OperatorAccount operator) {
		for (AuditOperator auditOperator : operator.getVisibleAudits()) {
			canSeeAudits.add(auditOperator.getAuditType().getId());
			if (auditOperator.isCanEdit())
				canEditAudits.add(auditOperator.getAuditType().getId());
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

	public Set<Integer> getGroups() {
		return groups;
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

	public boolean isAccountActive() {
		return accountActive;
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

	public Set<String> getAccountCountries() {
		return accountCountries;
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
			if (opPerm == perm.getOpPerm()) {
				if (oType == OpType.Edit)
					return isTrue(perm.getEditFlag());
				else if (oType == OpType.Delete)
					return isTrue(perm.getDeleteFlag());
				else if (oType == OpType.Grant)
					return isTrue(perm.getGrantFlag());
				// Default to OpType.View
				return isTrue(perm.getViewFlag());
			}
		}
		return false;
	}

	private boolean isTrue(Boolean value) {
		if (value == null)
			return false;
		return value;
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
		response.sendRedirect("Login.action?button=logout&msg=Your session has timed out. Please log back in");
		return false;
	}

	public boolean loginRequired(javax.servlet.http.HttpServletResponse response) throws IOException {
		return this.loginRequired(response, "");
	}

	public boolean loginRequired(javax.servlet.http.HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		String url = request.getRequestURI();
		if (request.getQueryString() != null)
			url += "?" + request.getQueryString();
		return this.loginRequired(response, url);
	}

	public boolean hasGroup(Integer group) {
		return groups.contains(group);
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

	/**
	 * @return
	 */
	public boolean isAuditor() {
		return this.hasGroup(11);
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

	public Set<UserAccess> getPermissions() {
		return permissions;
	}

	@Deprecated
	public boolean canSeeAudit(int auditType) {
		if (isContractor())
			return true;
		if (isPicsEmployee())
			return true;

		// For Operators and corporate
		if (canSeeAudits != null)
			return canSeeAudits.contains(new Integer(auditType));
		return false;
	}

	public boolean canSeeAudit(AuditType auditType) {
		if (isContractor())
			return auditType.isCanContractorView();
		if (isPicsEmployee())
			return true;

		// For Operators and corporate
		if (canSeeAudits != null)
			return canSeeAudits.contains(auditType.getId());
		return false;
	}

	public Set<Integer> getCanSeeAudit() {
		if (canSeeAudits == null)
			canSeeAudits = new HashSet<Integer>();
		return canSeeAudits;
	}

	public Set<Integer> getCanEditAudits() {
		if (canEditAudits == null)
			canEditAudits = new HashSet<Integer>();
		return canEditAudits;
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

	public Set<Integer> getVisibleCAOs() {
		return visibleCAOs;
	}

	public Set<Integer> getCanSeeAudits() {
		return canSeeAudits;
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
}
