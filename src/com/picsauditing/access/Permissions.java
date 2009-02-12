package com.picsauditing.access;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.SpringUtils;

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
	private Set<UserAccess> permissions = new HashSet<UserAccess>();
	private Set<Integer> canSeeAudits = new HashSet<Integer>();
	private Set<Integer> corporateParent = new HashSet<Integer>();
	private Set<Integer> operatorChildren = new HashSet<Integer>();
	private String username;
	private String name;
	private int accountID;
	private String accountName;
	private String accountType;
	private String email;
	private int adminID;
	private boolean approvesRelationships = false;
	private boolean active = false;
	private boolean accountActive = false;

	public void login(User user) throws Exception {
		try {
			clear();
			userID = user.getId();
			if (userID == 0)
				throw new Exception("Missing User");
			
			loggedIn = true;
			active = user.isActive();
			username = user.getUsername();
			name = user.getName();
			accountID = user.getAccount().getId();
			accountType = user.getAccount().getType();
			accountName = user.getAccount().getName();
			accountActive = user.getAccount().isActiveB();
			email = user.getEmail();
			
			if (isOperator() || isCorporate()) {
				OperatorAccount operator = (OperatorAccount) user.getAccount();
				if (isOperator()) {
					approvesRelationships = YesNo.Yes.equals(operator.getApprovesRelationships());
					for (Facility facility : operator.getCorporateFacilities())
						corporateParent.add(facility.getCorporate().getId());
				}
				if (isCorporate()) {
					for (Facility facility : operator.getOperatorFacilities())
						operatorChildren.add(facility.getOperator().getId());
				}
			}
			permissions = user.getPermissions();
			
			List<UserGroup> temp = user.getGroups();
			for (UserGroup u : temp)
				groups.add(u.getGroup().getId());
			
		} catch (Exception ex) {
			// All or nothing, if something went wrong, then clear it all
			clear();
			throw ex;
		}
	}

	public void clear() {
		userID = 0;
		loggedIn = false;
		active = false;
		accountActive = false;
		username = "";
		name = "";
		email = "";
		accountID = 0;
		accountName = "";
		accountType = "";
		permissions.clear();
		groups.clear();
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
	 * @deprecated use seesAllContractors Now
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
	 * @deprecated use isPicsEmployee Now
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

	public void setCanSeeAudit(Set<Integer> auditIds) {
		canSeeAudits = auditIds;
	}

	public boolean isApprovesRelationships() {
		return approvesRelationships;
	}

	public void setApprovesRelationships(boolean approvesRelationships) {
		this.approvesRelationships = approvesRelationships;
	}

	public Set<Integer> getCorporateParent() {
		return corporateParent;
	}

	public void setCorporateParent(Set<Integer> corporateParent) {
		this.corporateParent = corporateParent;
	}
	
	public Set<Integer> getOperatorChildren() {
		return operatorChildren;
	}

	public void setOperatorChildren(Set<Integer> operatorChildren) {
		this.operatorChildren = operatorChildren;
	}
	
	
	public Set<Integer> getVisibleAccounts() {
		Set<Integer> visibleAccounts = new HashSet<Integer>();
		visibleAccounts.add(accountID);
		if (isCorporate())
			visibleAccounts.addAll(operatorChildren);
		if (isOperator())
			visibleAccounts.addAll(corporateParent);
		return visibleAccounts;
	}

	public boolean isActive() {
		return active;
	}
}
