package com.picsauditing.actions.operators;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.model.operators.FacilitiesEditModel;
import com.picsauditing.model.operators.FacilitiesEditStatus;
import com.picsauditing.provisioning.ProductSubscriptionService;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.service.contractor.ContractorOperatorService;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.NoPermissionException;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class FacilitiesEdit extends OperatorActionSupport {

    @Autowired
    private AccountUserDAO accountUserDAO;
    @Autowired
    private FacilitiesDAO facilitiesDAO;
    @Autowired
    private OperatorFormDAO formDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserSwitchDAO userSwitchDAO;
    @Autowired
    private FacilitiesEditModel facilitiesEditModel;
    @Autowired
    protected CountrySubdivisionDAO countrySubdivisionDAO;
    @Autowired
    private AccountStatusChanges accountStatusChanges;
    @Autowired
    protected ContractorOperatorService contractorOperatorService;
	@Autowired
	private ProductSubscriptionService productSubscriptionService;

    private String createType;
    private List<Integer> facilities;
    private List<Integer> clients = new ArrayList<Integer>();
    private Set<OperatorAccount> relatedFacilities = null;
    private int nameId;
    private String name;
    private Map<UserAccountRole, List<AccountUser>> managers;

    private AccountUser accountUser;
    private AccountUser salesRep = null;
    private AccountUser accountRep = null;
    private Country country;
    private CountrySubdivision countrySubdivision;
    private int contactID;
    private boolean autoApproveRelationships;
    private String timeoutDays;
    private String sessionTimeout;

    public List<OperatorAccount> notChildOperatorList;
    public List<OperatorAccount> childOperatorList;

    private List<OperatorAccount> notSelectedClients;
    private List<OperatorAccount> selectedClients;

    private final Logger logger = LoggerFactory.getLogger(FacilitiesEdit.class);

    public String execute() throws Exception {
        findOperator();

        id = operator.getId();

        facilities = new ArrayList<>();
        if (operator.isCorporate()) {
            for (Facility fac : operator.getOperatorFacilities()) {
                facilities.add(fac.getOperator().getId());
            }
        }

        subHeading = getText("FacilitiesEdit.Edit", new Object[]{getText("global." + operator.getType())});

        if (operator.getPrimaryContact() == null) {
			addAlertMessage(getText("FacilitiesEdit.error.AddPrimaryContact"));
		}

        notChildOperatorList = getOperatorsNotMyChildrenOrMyself();
        childOperatorList = operator.getChildOperators();
        timeoutDays = "" + operator.getRememberMeTimeInDays();
        sessionTimeout = "" + operator.getSessionTimeout();

        loadSelectedClients();

        return SUCCESS;
    }

    public String create() throws NoRightsException {
        if ("Corporate".equals(getCreateType())) {
            if (!isCanEditCorp()) {
                throw new NoRightsException(OpPerms.ManageOperators, OpType.Edit);
            }
        } else {
            setCreateType("Operator");
            if (!isCanEditOp()) {
                throw new NoRightsException(OpPerms.ManageOperators, OpType.Edit);
            }
        }

        operator = new OperatorAccount();
        operator.setType(getCreateType());
        operator.setCountry(new Country(permissions.getCountry()));

        timeoutDays = "" + operator.getRememberMeTimeInDays();
        sessionTimeout = "" + operator.getSessionTimeout();

        return SUCCESS;
    }

    public String removeSalesRepresentative() {
        accountUserDAO.remove(accountUser);
        addActionMessage("Successfully Removed Sales Representative");
        return REDIRECT;
    }

    /*
        My thinking here is that we can have two kinds of generalized AccountUsers - the kind where there's only
        one current one (AccountReps, CSRs) and the kind where there are multiple current reps, possibly having to
        total 100% ownership. For each specific kind, we'll have a specific web controller method that marshals the
        specific resource. Then we just pass that argument to the model to handle it.

        For example, manageAccountRepresentative knows to pass accountRep to the model domain service
     */
    public String manageAccountRepresentative() {
        FacilitiesEditStatus status = facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, accountRep);
        if (!status.isOk) {
            addActionError(status.notOkErrorMessage);
        } else {
            addActionMessage(status.isOkMessage);
        }
        return REDIRECT;
    }

    public String addSalesRepresentative() {
        FacilitiesEditStatus status = facilitiesEditModel.addOneToManyAccountUser(permissions, operator, salesRep);
        if (!status.isOk) {
            addActionError(status.notOkErrorMessage);
        } else {
            addActionMessage(status.isOkMessage);
        }
    	return REDIRECT;
    }

    public String saveSalesRepresentative() {
        operatorDao.save(operator);
        addActionMessage("Successfully Saved Sales Representative");
        return REDIRECT;
    }

    public String copySalesRepresentativeToChildAccounts() throws Exception {
        facilitiesEditModel.copyOneToManyAccountUserToChildAccounts(operator, accountUser);
        addActionMessage("Successfully Copied to all child operators");
        return REDIRECT;
    }

    public String copyAccountMangerToChildAccounts() throws Exception {
        facilitiesEditModel.copySingleCurrentAccountUserToChildAccounts(permissions, operator, accountUser);
        addActionMessage("Successfully Copied to all child operators");
        return REDIRECT;
    }

    public String save() {
        if (operator.getId() == 0) {
            operator.setType(getCreateType());
        }

        List<String> errors = validateAccount(operator);
        if (errors.size() > 0) {
            for (String error : errors) {
                addActionError(error);
            }

            if (operator.getId() > 0) {
                operatorDao.clear();
                operator = operatorDao.find(operator.getId());
                return REDIRECT;
            }

            return SUCCESS;
        }

        if (permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)) {
            if (autoApproveRelationships != operator.isAutoApproveRelationships()) {
                if (!operator.isAutoApproveRelationships() && autoApproveRelationships) {
                    approveAllRelationships();
                }

                operator.setAutoApproveRelationships(autoApproveRelationships);
            }

            if (operator.isCorporate()) {
                if (facilities != null) {
                    List<OperatorAccount> newFacilities = new ArrayList<OperatorAccount>();

                    for (int operatorID : facilities) {
                        OperatorAccount opAccount = new OperatorAccount();
                        opAccount.setId(operatorID);
                        newFacilities.add(opAccount);
                    }

                    // Easier to just remove all existing facilities and persist all the new ones
                    // if we have already validated the Facility?
                    Iterator<Facility> facList = operator.getOperatorFacilities().iterator();
                    while (facList.hasNext()) {
                        Facility opFacilities = facList.next();
                        if (newFacilities.contains(opFacilities.getOperator())) {
                            newFacilities.remove(opFacilities.getOperator());
                        } else {
                            facilitiesDAO.remove(opFacilities);

                            if (operator.equals(opFacilities.getOperator().getParent())) {
                                opFacilities.getOperator().setParent(null);
                                operatorDao.save(opFacilities.getOperator());
                            }

                            facList.remove();
                        }
                    }

                    for (OperatorAccount opAccount : newFacilities) {
                        opAccount = operatorDao.find(opAccount.getId());
                        if (opAccount != null) {
                            Facility facility = new Facility();
                            facility.setCorporate(operator);
                            facility.setOperator(opAccount);
                            facility.setAuditColumns(permissions);
                            facilitiesDAO.save(facility);

                            operator.getOperatorFacilities().add(facility);

                            if (opAccount.getParent() == null) {
                                opAccount.setParent(operator);
                                operatorDao.save(opAccount);
                            }
                        }
                    }

                    if (operator.getParent() != null && newFacilities.size() > 0) {
                        linkChildOperatorsToAllParentAccounts(newFacilities);
                    }
                }
            }

            saveLinkedClientsForGeneralContractor();

            operator.setAuditColumns(permissions);
            operator.setNameIndex();

            if (operator.getId() == 0) {
                operator.setNaics(new Naics());
                operator.getNaics().setCode("0");
                operator.setInheritFlagCriteria(operator);
                operator.setInheritInsuranceCriteria(operator);

                // Save so we can get the id and then update the NOLOAD
                // with
                // a unique id
                operatorDao.save(operator);

                operator.setQbListID("NOLOAD" + operator.getId());
                operator.setQbListCAID("NOLOAD" + operator.getId());
            }
        }

        facilitiesEditModel.addPicsGlobal(operator, permissions);

        if (!operator.isInPicsConsortium()) {
            facilitiesEditModel.addPicsCountry(operator, permissions);
        }

        if (contactID > 0
                && (operator.getPrimaryContact() == null || contactID != operator.getPrimaryContact().getId())) {
            operator.setPrimaryContact(userDAO.find(contactID));
        }

        operator = saveClientSite();

		employeeGUARDProductSubscriptionNotification(operator);

        addActionMessage(getText("FacilitiesEdit.SuccessfullySaved", new Object[]{operator.getName()}));

        return REDIRECT;
    }

	private void employeeGUARDProductSubscriptionNotification(final OperatorAccount operatorAccount) {
		int operatorId = operatorAccount.getId();

    /*
      operatorAccount.isRequiresEmployeeGuard()
      TODO:This property is not required whenever this registration module or OperatorAccount entity is refactored
    * whether user has EG or not is checked for somewhere else and not in operatoraccount entity
    *
    * */

		if (operatorAccount.isRequiresEmployeeGuard()) {
			productSubscriptionService.addEmployeeGUARD(operatorId);
		} else {
			productSubscriptionService.removeEmployeeGUARD(operatorId);
		}

  }

    private OperatorAccount saveClientSite() {
    	if (operator.getStatus().isDeactivated()) {
    		String deactivationReason = permissions.getName() + " has deactivated this account.";
    		accountStatusChanges.deactivateClientSite(operator, permissions,
                    AccountStatusChanges.OPERATOR_MANUALLY_DEACTIVATED_REASON,
                    deactivationReason);
    	}

    	operator.setNeedsIndexing(true);
        return operatorDao.save(operator);
    }

    public String ajaxAutoApproveRelationshipModal() throws Exception {
        if (!AjaxUtils.isAjax(ServletActionContext.getRequest())) {
            throw new RuntimeException("forward 404");
        }

        return "AutoApproveRelationshipModal";
    }

    private void approveAllRelationships() {
        for (ContractorOperator co : operator.getContractorOperators()) {
            if (co.getWorkStatus().isPending() || co.getWorkStatus().isNo()) {
                co.setWorkStatus(ApprovalStatus.Y);
                contractorOperatorService.cascadeWorkStatusToParent(co);
            }
        }
    }

    public String delete() throws NoPermissionException, Exception {
        findOperator();

        if (operator.isOperator()) {
            permissions.tryPermission(OpPerms.ManageOperators, OpType.Delete);
        } else if (operator.isCorporate()) {
            permissions.tryPermission(OpPerms.ManageCorporate, OpType.Delete);
        } else {
            throw new NoPermissionException("Delete Account");
        }

        operator.setStatus(AccountStatus.Deleted);
        save();
        clearMessages();

        return setUrlForRedirect("ReportAccountList.action");
    }

    public List<OperatorAccount> getOperatorsNotMyChildrenOrMyself() throws Exception {
        // find all operators
        List<OperatorAccount> tmpOperatorList;
        tmpOperatorList = operatorDao.findWhere(true, "status IN ('Active','Demo','Pending')");

        // remove operators that are children of the current operator
        tmpOperatorList.removeAll(operator.getChildOperators());
        tmpOperatorList.remove(operator);

        // return the list of operators not associated with the current operator
        return tmpOperatorList;
    }

    public List<User> getUserList() throws Exception {
        return facilitiesEditModel.getAllPossibleAccountUsers();
    }

    public List<Integer> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Integer> facilities) {
        this.facilities = facilities;
    }

    public List<Integer> getClients() {
        return clients;
    }

    public void setClients(List<Integer> clients) {
        this.clients = clients;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeoutDays() { return timeoutDays; }

    public void setTimeoutDays(String timeoutDays) { this.timeoutDays = timeoutDays; }

    public String getSessionTimeout() { return sessionTimeout; }

    public void setSessionTimeout(String sessionTimeout) { this.sessionTimeout = sessionTimeout; }

    public Set<OperatorAccount> getRelatedFacilities() {
        if (relatedFacilities == null) {
            relatedFacilities = new TreeSet<OperatorAccount>();
            // Add myself
            relatedFacilities.add(operator);
            if (operator.getId() > 0) {
                // Add all my parents
                if (operator.getCorporateFacilities() != null) {
                    for (Facility parent : operator.getCorporateFacilities()) {
						relatedFacilities.add(parent.getCorporate());
					}
                }
                if (operator.getInheritFlagCriteria() != null) {
					relatedFacilities.add(operator.getInheritFlagCriteria());
				}
                if (operator.getInheritInsuranceCriteria() != null) {
					relatedFacilities.add(operator.getInheritInsuranceCriteria());
				}
            }
        }

        return relatedFacilities;
    }

    public List<OperatorForm> getOperatorForms() {
        return formDAO.findByopID(this.id);
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }

    public UserAccountRole[] getRoleList() {
        return UserAccountRole.values();
    }

    /**
     * This is the account user that is used when a user clicks on the "Remove"
     * button, or when copying a specific AccountUser to all the Children of the
     * parent Operator.
     */
    public AccountUser getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(AccountUser accountUser) {
        this.accountUser = accountUser;
    }

    public AccountUser getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(AccountUser salesRep) {
        this.salesRep = salesRep;
    }

    /**
     * This represents either the Sales Representative or the Account Manager
     * that is being added to the Operator (for commissions).
     */
    public AccountUser getAccountRep() {
        return accountRep;
    }

    public void setAccountRep(AccountUser accountRep) {
        this.accountRep = accountRep;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CountrySubdivision getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(CountrySubdivision countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public List<User> getPrimaryOperatorContactUsers() {
        Set<User> primaryContactSet = new HashSet<User>();

        primaryContactSet.addAll(userDAO.findByAccountID(operator.getId(), "Yes", "No"));

        // Include users that can switch to groups
        Set<User> groupSet = new HashSet<User>();
        groupSet.addAll(userDAO.findByAccountID(operator.getId(), "Yes", "Yes"));

        Set<User> switchToSet = new HashSet<User>();
        // Adding users that can switch to users on account
        for (User u : primaryContactSet) {
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		}
        // Adding users that can switch to groups on account
        for (User u : groupSet) {
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		}

        // Adding all SwitchTo users to primary contacts
        try {
            primaryContactSet.addAll(switchToSet);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        addParentPrimaryOperatorContactUsers(primaryContactSet);

        List<User> userList = new ArrayList<User>();
        userList.addAll(primaryContactSet);

        return userList;
    }

    private void addParentPrimaryOperatorContactUsers(Set<User> primaryContactSet) {
        OperatorAccount parent = operator.getParent();
        Set<OperatorAccount> operatorsAlreadyCovered = new HashSet<OperatorAccount>();
        operatorsAlreadyCovered.add(operator);

        while (parent != null && !operatorsAlreadyCovered.contains(parent)) {
            primaryContactSet.addAll(userDAO.findByAccountID(parent.getId(), "Yes", "No"));
            operatorsAlreadyCovered.add(parent);
            parent = parent.getParent();
        }
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     * @param autoApproveRelationships the autoApproveRelationships to set
     */
    public void setAutoApproveRelationships(boolean autoApproveRelationships) {
        this.autoApproveRelationships = autoApproveRelationships;
    }

    /**
     * @return the autoApproveRelationships
     * @throws Exception
     * @throws RecordNotFoundException
     */
    public boolean getAutoApproveRelationships() throws RecordNotFoundException, Exception {
        if (operator == null) {
			findOperator();
		}

        return operator.isAutoApproveRelationships();
    }

    public List<AccountUser> getAccountManagers() {
        List<AccountUser> list = new ArrayList<AccountUser>();

        for (AccountUser au : operator.getAccountUsers()) {
            if (au.isCurrent() && au.getRole().isAccountManager()) {
                list.add(au);
            }
        }

        return list;
    }

    public List<AccountUser> getSalesReps() {
        List<AccountUser> list = new ArrayList<AccountUser>();

        for (AccountUser au : operator.getAccountUsers()) {
            if (au.isCurrent() && au.getRole().isSalesRep()) {
                list.add(au);
            }
        }

        return list;
    }

    public Map<UserAccountRole, List<AccountUser>> getPreviousManagers() {
        if (managers == null) {
            List<AccountUser> aus = operator.getAccountUsers();
            List<AccountUser> ams = new ArrayList<AccountUser>();
            List<AccountUser> srs = new ArrayList<AccountUser>();

            for (AccountUser au : aus) {
                if (au.getEndDate().before(new Date())) {
                    if (au.getRole().equals(UserAccountRole.PICSAccountRep)) {
						ams.add(au);
					} else {
						srs.add(au);
					}
                }
            }

            managers = new HashMap<UserAccountRole, List<AccountUser>>();
            if (ams.size() > 0) {
				managers.put(UserAccountRole.PICSAccountRep, ams);
			}
            if (srs.size() > 0) {
				managers.put(UserAccountRole.PICSSalesRep, srs);
			}
        }

        return managers;
    }

    public boolean isCanEditCorp() {
        return permissions.hasPermission(OpPerms.ManageCorporate, OpType.Edit);
    }

    public boolean isCanEditOp() {
        return permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit);
    }

    public boolean isCanDeleteCorp() {
        return permissions.hasPermission(OpPerms.ManageCorporate, OpType.Delete);
    }

    public boolean isCanDeleteOp() {
        return permissions.hasPermission(OpPerms.ManageOperators, OpType.Delete);
    }

    public List<OperatorAccount> getNotSelectedClients() {
        return notSelectedClients;
    }

    public void setNotSelectedClients(List<OperatorAccount> notSelectedClients) {
        this.notSelectedClients = notSelectedClients;
    }

    public List<OperatorAccount> getSelectedClients() {
        return selectedClients;
    }

    public void setSelectedClients(List<OperatorAccount> selectedClients) {
        this.selectedClients = selectedClients;
    }

    public int getPendingAndNotApprovedRelationshipCount() throws RecordNotFoundException, Exception {
        if (operator == null) {
			findOperator();
		}

        int pendingAndNotApprovedCount = dao.getCount(ContractorOperator.class,
                "operatorAccount.id = " + operator.getId() + " AND (workStatus = 'P' OR workStatus = 'N')");
        return pendingAndNotApprovedCount;
    }

    private void loadSelectedClients() {
        notSelectedClients = operatorDao.findWhere(false, "a.status IN ('Active'"
                + (operator.getStatus().isDemo() ? ", 'Demo'" : "") + ") AND a.id != " + operator.getId());
        selectedClients = operator.getLinkedClientSites();

        notSelectedClients.removeAll(selectedClients);
        notSelectedClients.removeAll(childOperatorList);
    }

    // TODO: This should be converted to Struts2 Validation
    private List<String> validateAccount(OperatorAccount operator) {
        List<String> errorMessages = new ArrayList<String>();
        if (Strings.isEmpty(operator.getName())) {
			errorMessages.add(getText("FacilitiesEdit.PleaseFillInCompanyName"));
		} else if (operator.getName().length() < 2) {
			errorMessages.add(getText("FacilitiesEdit.NameAtLeast2Chars"));
		}

        if (operator.isRememberMeTimeEnabled()) {
            try {
                operator.setRememberMeTimeInDays(Integer.parseInt(timeoutDays));
            } catch (NumberFormatException e) {
                errorMessages.add(getText("FacilitiesEdit.RememberMeInteger"));
            }
        }

        try {
            operator.setSessionTimeout(Integer.parseInt(sessionTimeout));
        } catch (Exception e) {
            errorMessages.add(getText("FacilitiesEdit.SessionTimeoutInteger"));
        }

        if (facilities == null) {
            facilities = new ArrayList<Integer>();
            for (Facility fac : operator.getOperatorFacilities()) {
                facilities.add(fac.getOperator().getId());
            }
        } else {
            int n = operator.getId();
            if (facilities.contains(operator.getId())) {
                errorMessages.add(getTextParameterized("FacilitiesEdit.CyclicalRelationship", operator.getId(),
                        Strings.implode(facilities)));
            }
        }

        validateCountryAndSubdivision(operator, errorMessages);

        if (operator.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0
                || operator.getDiscountPercent().compareTo(BigDecimal.ONE) > 0) {
            errorMessages.add(getText("FacilitiesEdit.EnterValidRange"));
        }

        if (operator.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0 && operator.getDiscountExpiration() == null) {
            errorMessages.add(getText("FacilitiesEdit.DiscountExpirationDateRequired"));
        }

        if (operator.isRememberMeTimeEnabled() && operator.getRememberMeTimeInDays() <= 0) {
            errorMessages.add(getText("FacilitiesEdit.RememberMeMustBePositive"));
        }

        return errorMessages;
    }

    private void validateCountryAndSubdivision(OperatorAccount operator, List<String> errorMessages) {
        if (country == null || country.getIsoCode().isEmpty()) {
            errorMessages.add(getText("FacilitiesEdit.SelectCountry"));
        } else {
            country = countryDAO.findbyISO(country.getIsoCode()); // reload
            operator.setCountry(country);

            if (country.isHasCountrySubdivisions()) {
                if ((countrySubdivision == null || countrySubdivision.getIsoCode().isEmpty())) {
                    errorMessages.add(getText("FacilitiesEdit.PleaseFillInCountrySubdivision"));
                } else {
                    CountrySubdivision contractorCountrySubdivision = countrySubdivisionDAO.find(countrySubdivision.toString());
                    operator.setCountrySubdivision(contractorCountrySubdivision);
                }
            } else {
                operator.setCountrySubdivision(null);
            }
        }
    }

    // Insure that all newly added facilities get linked to all parent accounts.
    // i.e. if F1 -> Hub -> US -> Corporate
    // then F1 needs to be linked to US and Corporate
    private void linkChildOperatorsToAllParentAccounts(List<OperatorAccount> newFacilities) {
        List<OperatorAccount> parents = new ArrayList<OperatorAccount>();
        findParentAccounts(operator, parents);
        for (OperatorAccount child : newFacilities) {
            for (OperatorAccount parent : parents) {
                // add the link into facilities, if it doesn't already exist.
                Facility facility = facilitiesDAO.findByCorpOp(parent.getId(), child.getId());
                if (facility == null) {
                    facility = new Facility();
                    facility.setCorporate(parent);
                    facility.setOperator(child);
                    facility.setAuditColumns(permissions);
                    facilitiesDAO.save(facility);
                }
            }
        }
    }

    // Recursively find all the parents of this operator.
    private void findParentAccounts(OperatorAccount currentOperator, List<OperatorAccount> parents) {
        if (currentOperator.getParent() == null || parents.contains(currentOperator.getParent())) {
            return;
        } else {
            parents.add(currentOperator.getParent());
            findParentAccounts(currentOperator.getParent(), parents);
        }
    }

    private void saveLinkedClientsForGeneralContractor() {
        if (operator.isGeneralContractor()) {
            // get list of existing linked clients
            // get list of selected clients
            // remove all existing linked clients that aren't in selected
            // clients
            Iterator<Facility> linkedClientIterator = operator.getLinkedClients().iterator();
            while (linkedClientIterator.hasNext()) {
                Facility linkedClientFacility = linkedClientIterator.next();
                OperatorAccount linkedClient = linkedClientFacility.getCorporate();
                if (!clients.contains(linkedClient.getId())) {
                    linkedClientIterator.remove();
                    facilitiesDAO.remove(linkedClientFacility);
                }
            }
            // remove all selected clients that are already existing
            // TODO make sure this isn't removing based on the index
            for (OperatorAccount existingLinkedClient : operator.getLinkedClientSites()) {
                if (clients.contains(existingLinkedClient.getId())) {
                    clients.remove((Integer) existingLinkedClient.getId());
                }
            }
            // add all remaining selected clients
            for (Integer clientToAdd : clients) {
                OperatorAccount linkedClient = new OperatorAccount();
                linkedClient.setId(clientToAdd);

                Facility linkedClientFacility = new Facility();
                linkedClientFacility.setCorporate(linkedClient);
                linkedClientFacility.setOperator(operator);
                linkedClientFacility.setType("GeneralContractor");
                linkedClientFacility.setAuditColumns(permissions);

                facilitiesDAO.save(linkedClientFacility);
            }
        } else {
            for (Facility facility : operator.getLinkedClients()) {
                facilitiesDAO.remove(facility);
            }

            operator.getLinkedClients().clear();
        }
    }

    public List<OperatorAccount> getNotChildOperatorList() throws Exception {
        notChildOperatorList = getOperatorsNotMyChildrenOrMyself();
        return notChildOperatorList;
    }

    public List<OperatorAccount> getChildOperatorList() {
        childOperatorList = operator.getChildOperators();
        return childOperatorList;
    }

}
