package com.picsauditing.access;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.LocaleController;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

/**
 * This is the main class that is stored for each user containing information if
 * they are logged in, which groups they're in, and what permission(s) they have
 * 
 * Warning: this class is stored in the session Make sure you keep the footprint
 * very small
 */
public class Permissions implements Serializable {

	private static final long serialVersionUID = -3120292424348289561L;
	protected static final int TWENTY_FOUR_HOURS = 24 * 60 * 60;

	private int userID;
	private boolean loggedIn = false;
	private boolean forcePasswordReset = false;
	
	@Deprecated
	private Map<Integer, String> groups = new HashMap<Integer, String>();
	
	private Set<Integer> allInheritedGroupIds = new HashSet<Integer>();
	private Set<UserAccess> permissions = new HashSet<UserAccess>();
	private boolean canSeeInsurance = false;
	private Set<Integer> corporateParent = new HashSet<Integer>();
	private Set<Integer> operatorChildren = new HashSet<Integer>();
	private Set<Integer> visibleAuditTypes = new HashSet<Integer>();
	private Set<Integer> linkedClients = new HashSet<Integer>();
	private Set<Integer> linkedGeneralContractors = new HashSet<Integer>();

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
	private boolean generalContractor = false;
	private boolean gcFree = false;
	private AccountStatus accountStatus = AccountStatus.Pending;
	private long sessionCookieTimeoutInSeconds;
	private int rememberMeTimeInSeconds;

	private int shadowedUserID;
	private String shadowedUserName;
	
	// These are transient because they should not be in the session, and only used
	// because these are necessary for object construction
	private transient HierarchyBuilder hierarchyBuilder;
	private transient FeatureToggle featureToggle;

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

		generalContractor = false;
		gcFree = false;

		adminID = 0;
		topAccountID = 0;

		shadowedUserID = 0;
		shadowedUserName = "";

		permissions.clear();
		groups.clear();
		allInheritedGroupIds.clear();
		visibleAuditTypes.clear();
		corporateParent.clear();
		operatorChildren.clear();
		linkedClients.clear();
		linkedGeneralContractors.clear();
		sessionCookieTimeoutInSeconds = 0;
		rememberMeTimeInSeconds = -1;
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
			LocaleController.setLocaleOfNearestSupported(this);

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
			generalContractor = user.getAccount().isGeneralContractor();
			sessionCookieTimeoutInSeconds = user.getAccount().getSessionTimeout() * 60;
			if (user.getAccount().isRememberMeTimeEnabled()) {
				rememberMeTimeInSeconds = user.getAccount().getRememberMeTimeInDays() * TWENTY_FOUR_HOURS;
			} else {
				rememberMeTimeInSeconds = -1;
			}

			if (isOperatorCorporate()) {
				OperatorAccount operator = (OperatorAccount) user.getAccount();
				visibleAuditTypes = operator.getVisibleAuditTypes();

				approvesRelationships = !operator.isAutoApproveRelationships()
						|| user.hasPermission(OpPerms.ViewUnApproved);

				gcFree = operator.isGeneralContractorFree();

				if (generalContractor || gcFree) {
					for (OperatorAccount linkedClient : operator.getLinkedClientSites()) {
						linkedClients.add(linkedClient.getId());
					}
				}

				if (operator.getLinkedGeneralContractorOperatorAccounts().size() > 0) {
					for (OperatorAccount generalContractor : operator.getLinkedGeneralContractorOperatorAccounts()) {
						linkedGeneralContractors.add(generalContractor.getId());
					}
				}

				if (isOperator()) {
					if (operator.getParent() != null)
						topAccountID = operator.getParent().getId();

					for (Facility facility : operator.getCorporateFacilities()) {
						corporateParent.add(facility.getCorporate().getId());
						if (facility.getCorporate().isPrimaryCorporate()) {
							topAccountID = facility.getCorporate().getId();
						}
					}

					if (operator.getCanSeeInsurance().isTrue()) {
						canSeeInsurance = true;
					}
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
			
			populateGroupHierarchyMap();
			populateGroupMap(user);
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

	@Deprecated
	public Set<Integer> getGroupIds() {
		return groups.keySet();
	}

	@Deprecated
	public Collection<String> getGroupNames() {
		return groups.values();
	}
	
	public Set<Integer> getAllInheritedGroupIds() {
		return allInheritedGroupIds;
	}

	public String getUsername() {
		return username;
	}

	public int getAccountId() {
		return accountID;
	}

	public String getAccountIdString() {
		return Integer.toString(getAccountId());
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

	public long getSessionCookieTimeoutInSeconds() {
		return sessionCookieTimeoutInSeconds;
	}

	public int getRememberMeTimeInSeconds() {
		return rememberMeTimeInSeconds;
	}

	public void setRememberMeTimeInSeconds(int rememberMeTimeInSeconds) {
		this.rememberMeTimeInSeconds = rememberMeTimeInSeconds;
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
			if (opPerm.isForContractor() && isContractor() && perm.getOpPerm() == OpPerms.ContractorAdmin)
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

	public boolean has(OpPerms opPerm) {
		return hasPermission(opPerm, OpType.View);
	}

	public boolean has(OpPerms opPerm, OpType oType) {
		return hasPermission(opPerm, oType);
	}

	public boolean hasPermission(OpPerms opPerm) {
		return hasPermission(opPerm, OpType.View);
	}

	public void tryPermission(OpPerms opPerm, OpType oType) throws NoRightsException {
		if (hasPermission(opPerm, oType))
			return;

		throw new NoRightsException(opPerm, oType);
	}

	public void tryPermission(OpPerms opPerm) throws NoRightsException {
		tryPermission(opPerm, OpType.View);
	}

	public boolean loginRequired(HttpServletResponse response, String returnURL) throws IOException {
		if (loggedIn)
			return true;

		addReturnToCookieIfGoodUrl(response, returnURL);

		Cookie c = new Cookie("PICSCookiesEnabled", "true");
		c.setMaxAge(60);
		ServletActionContext.getResponse().addCookie(c);

		return false;
	}

	private void addReturnToCookieIfGoodUrl(HttpServletResponse response, String returnURL) {
		if (returnUrlIsOk(returnURL)) {
			// PICS-7659: "/Home.action causing a loop - replace "
			Cookie fromCookie = new Cookie("from", returnURL.replaceAll("\"", ""));
			fromCookie.setMaxAge(3600);
			response.addCookie(fromCookie);
		}
	}

	private boolean returnUrlIsOk(String returnURL) {
		boolean isOk = !returnURL
				.matches("(?iu).*(xml|json|ajax|widget|autocomplete|csv|import|external|download|upload).*\\.action(\\?.*)*");
		return returnURL != null
				&& returnURL.length() > 0
				&& isOk;
	}

	public boolean loginRequired(HttpServletResponse response) throws IOException {
		return loginRequired(response, "");
	}

	public boolean loginRequired(HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		if (AjaxUtils.isAjax(request)) {
			return loginRequired(response);
		} else {
			String url = request.getRequestURI();
			if (request.getQueryString() != null)
				url += "?" + request.getQueryString();

			return loginRequired(response, url);
		}
	}
	
	public boolean belongsToGroups() {
		if (featureToggle != null && featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_PERRMISSION_GROUPS)) {
			return CollectionUtils.isNotEmpty(allInheritedGroupIds);
		}
		
		return belongsToGroupsOld();
	}

	@Deprecated
	private boolean belongsToGroupsOld() {
		return MapUtils.isNotEmpty(groups);
	}

	public boolean hasGroup(Integer groupId) {
		if (featureToggle != null && featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_PERRMISSION_GROUPS)) {
			return CollectionUtils.isNotEmpty(allInheritedGroupIds) ? allInheritedGroupIds.contains(groupId) : false;
		}
		
		return hasGroupOld(groupId);
	}
	
	@Deprecated
	public boolean hasGroupOld(Integer groupId) {
		if (MapUtils.isNotEmpty(groups)) {
			return false;
		} else {
			return groups.containsKey(groupId);
		}		
	}

	public boolean isContractor() {
		return "Contractor".equals(accountType);
	}

	public boolean isCorporate() {
		return "Corporate".equals(accountType);
	}

	public boolean isOperator() {
		return "Operator".equals(accountType);
	}

	public boolean isAssessment() {
		return "Assessment".equals(accountType);
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
		// return hasGroup(10);
		return seesAllContractors();
	}

	public boolean isRestApi(){
		return hasPermission(OpPerms.RestApi);
	}

	public boolean seesAllContractors() {
		return hasPermission(OpPerms.AllContractors);
	}

	public boolean isDeveloperEnvironment() {
		return hasPermission(OpPerms.DevelopmentEnvironment);
	}

	/**
	 * @return
	 */
	public boolean isAuditor() {
		// FIXME This should be checking for a permission, not a group -- and most certainly not by the group's ID number as it haapens to been assigned in one particular database instance
		return hasGroup(11);
	}

	public boolean isMarketing() {
		// FIXME This should be checking for a permission, not a group -- and most certainly not by the group's ID number as it haapens to been assigned in one particular database instance
		return hasGroup(10801);
	}

	public boolean isIndependentAuditor() {
		// FIXME This should be checking for a permission, not a group -- and most certainly not by the group's ID number as it haapens to been assigned in one particular database instance
		return hasGroup(11265);
	}

	public boolean isPicsEmployee() {
		return (Account.PicsID == accountID);
	}

	public boolean isSecurity() {
		// FIXME This should be checking for a permission, not a group -- and most certainly not by the group's ID number as it haapens to been assigned in one particular database instance
		return hasGroup(68908);
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

		return hasGroup(User.GROUP_AUDITOR);
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

		if (isCorporate()) {
			visibleAccounts.addAll(operatorChildren);
		}

		if (isOperator()) {
			visibleAccounts.add(topAccountID);
			visibleAccounts.addAll(corporateParent);
		}

		if (isGeneralContractor()) {
			visibleAccounts.addAll(linkedClients);
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

	public boolean isGeneralContractor() {
		return generalContractor || gcFree;
	}

	public boolean isGeneralContractorFree() {
		return gcFree;
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

	public Set<Integer> getLinkedClients() {
		return linkedClients;
	}

	public Set<Integer> getLinkedGeneralContractors() {
		return linkedGeneralContractors;
	}

	public void setSessionCookieTimeoutInSeconds(int seconds) {
		this.sessionCookieTimeoutInSeconds = seconds;
	}

	public boolean isCanAddRuleForOperator(OperatorAccount operator) {
		if (hasPermission(OpPerms.AuditRuleAdmin))
			return true;

		if (operator != null) {
			if (isPicsEmployee() && (operator.isDemo() || operator.getStatus().isPending()))
				return true;

			for (AccountUser accUser : operator.getAccountUsers()) {
				if (accUser.getUser().getId() == getUserId())
					return true;
			}

			for (OperatorAccount child : operator.getOperatorChildren()) {
				for (AccountUser childAccUser : child.getAccountUsers()) {
					if (childAccUser.getUser().getId() == getUserId())
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

		permissions.add(ua);
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	public void setHierarchyBuilder(HierarchyBuilder hierarchyBuilder) {
		this.hierarchyBuilder = hierarchyBuilder;
	}
	
	public void setFeatureToggle(FeatureToggle featureToggle) {
		this.featureToggle = featureToggle;
	}
	
	private void populateGroupHierarchyMap() {
		if (hierarchyBuilder == null) {
			throw new IllegalStateException("You must set the HierarchyBuilder.");
		}
		
		allInheritedGroupIds = hierarchyBuilder.retrieveAllEntityIdsInHierarchy(userID);
	}
	
	/**
	 * Leaving this method in for backwards compatibility.
	 * 
	 * This method does not return all the users in the Group Hierarchy. Please use the
	 * method populateGroupHierarchy instead.
	 * 
	 * @param user User that is logging into the system
	 */
	@Deprecated
	private void populateGroupMap(User user) {
		for (UserGroup u : user.getGroups()) {
			if (u.getGroup().isGroup()) {
				groups.put(u.getGroup().getId(), u.getGroup().getName());
			}
		}
	}
}
