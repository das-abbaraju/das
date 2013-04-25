package com.picsauditing.actions.contractors;

import java.util.*;

import javax.naming.NoPermissionException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.EventType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.NoteFactory;

@SuppressWarnings("serial")
public class ContractorFacilities extends ContractorActionSupport {
    @Autowired
    private ContractorOperatorDAO contractorOperatorDAO;
    @Autowired
    private ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
    @Autowired
    private OperatorAccountDAO operatorDao;
    @Autowired
    private ContractorAccountDAO accountDao;
    @Autowired
    private NoteDAO noteDAO;
    @Autowired
    private FacilityChanger facilityChanger;
    @Autowired
    private BillingCalculatorSingle billingService;

    public Boolean competitorAnswer;
    private ContractorType type = null;
    private String msg = null;
    private String search;
    private OperatorAccount operator = null;

    private List<ContractorOperator> currentOperators = null;
    private List<OperatorAccount> searchResults = null;

    private Database database; // for injecting for unit tests

    public ContractorFacilities() {
        this.noteCategory = NoteCategory.OperatorChanges;
        this.currentStep = ContractorRegistrationStep.Clients;
    }

    @Override
    public String execute() throws Exception {
        this.subHeading = getText("ContractorFacilities.title");
        limitedView = true;
        findContractor();

        addFacilitiesBasedOnRegistrationRequest();

        if (permissions.isOperator()) {
            throw new NoPermissionException(getText("ContractorFacilities.error.OperatorCannotView"));
        }

        if (permissions.isContractor()) {
            contractor.setViewedFacilities(new Date());
            accountDao.save(contractor);
        }

        if (contractor.getNonCorporateOperators().size() == 1 && contractor.getStatus().isPending()) {
            contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
            accountDao.save(contractor);
        }

        currentOperators = contractorOperatorDAO.findByContractor(id, permissions);

        return SUCCESS;
    }

    public String add() throws Exception {
        prepareFacilityChanger();

        if (contractor.meetsOperatorRequirements(operator)) {
            contractor.setRenew(true);
            facilityChanger.add();

            reviewCategories(EventType.Locations);

            if (contractor.getNonCorporateOperators().size() == 1 && contractor.getStatus().isPending())
                contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());

            billingService.calculateContractorInvoiceFees(contractor);
            contractor.syncBalance();
            recalculate();
        } else {
            addActionError(getText("ContractorFacilities.error.ServiceMismatch"));
        }

        return JSON;
    }

    public String generalContractorOperators() {
        if (operator != null) {
            searchResults = operator.getLinkedClientSites();

            for (OperatorAccount operatorAccount : contractor.getOperatorAccounts()) {
                searchResults.remove(operatorAccount);
            }

            addAlertMessage(getTextParameterized("RegistrationAddClientSite.GeneralContractorsHelp",
                    ("Yes".equals(operator.getDoContractorsPay()) ? 1 : 0), operator.getName()));
        }

        return "search";
    }

    public String load() throws Exception {
        findContractor();
        currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
        return "load";
    }

    public String remove() throws Exception {
        prepareFacilityChanger();

        facilityChanger.remove();

        if (contractor.getNonCorporateOperators().size() == 0 && contractor.getStatus().isPending()) {
            contractor.setRequestedBy(null);
        } else if (contractor.getNonCorporateOperators().size() == 1 && contractor.getStatus().isPending()) {
            contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
            recalculate();
        }

        return JSON;
    }

    public String search() throws Exception {
        limitedView = true;
        findContractor();

        searchResults = new ArrayList<>();

        if (Strings.isNotEmpty(search)) {
            String sql = nameAndLocationQuery();

            List<BasicDynaBean> data = database().select(sql, false);
            if (data != null) {
                processSearchResults(data);

                currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
                Iterator<OperatorAccount> iterator = searchResults.iterator();
                while (iterator.hasNext()) {
                    OperatorAccount searchResult = iterator.next();
                    for (ContractorOperator currentOperator : currentOperators) {
                        if (currentOperator.getOperatorAccount().getId() == searchResult.getId()) {
                            iterator.remove();
                        }
                    }
                }
            }
        } else if (contractor.getNonCorporateOperators().size() == 0) {
            // Only turn on smart facility suggest for US and Canada
            List<BasicDynaBean> data = SmartFacilitySuggest.getFirstFacility(contractor, permissions);
            processSearchResults(data);
            addActionMessage(getText("ContractorFacilities.message.FacilitiesBasedLocation"));
        } else {
            if (!permissions.isCorporate()) {
                int limit = 10;
                List<BasicDynaBean> data = SmartFacilitySuggest.getSimilarOperators(contractor, limit);
                processSearchResults(data);

                addActionMessage(getText("ContractorFacilities.message.FacilitiesBasedSelection"));
            } else {
                // Corporate users should only see the operators under
                // their umbrella
                OperatorAccount op = operatorDao.find(permissions.getAccountId());
                for (Facility f : op.getOperatorFacilities()) {
                    if (!contractor.getOperatorAccounts().contains(f.getOperator()) && f.getOperator().getStatus().isActive()) {
                        searchResults.add(f.getOperator());
                    }
                }
            }
        }

        return "search";
    }

    private String nameAndLocationQuery() {
        SelectSQL sql = new SelectSQL("accounts a");
        sql.addField("a.id opID");
        sql.addField("a.status");
        sql.addField("a.onsiteServices");
        sql.addField("a.offsiteServices");
        sql.addField("a.materialSupplier");
        sql.addField("a.transportationServices");

        sql.addJoin("LEFT JOIN app_translation country_translation ON country_translation.msgKey = " +
                "CONCAT('Country.', a.country)");
        sql.addJoin("LEFT JOIN app_translation country_subdivision_translation ON " +
                "country_subdivision_translation.msgKey = CONCAT('CountrySubdivision.', a.countrySubdivision)");

        List<String> searchFields = new ArrayList<>();
        searchFields.add("a.nameIndex");
        searchFields.add("a.name");
        searchFields.add("a.dbaName");
        searchFields.add("a.city");
        searchFields.add("a.country");
        searchFields.add("a.countrySubdivision");
        searchFields.add("country_translation.msgValue");
        searchFields.add("country_subdivision_translation.msgValue");

        StringBuilder whereClause = new StringBuilder();
        boolean first = true;
        for (String searchField : searchFields) {
            sql.addField(searchField);

            if (!first) {
                whereClause.append(" OR ");
            }

            whereClause.append(searchField).append(" LIKE '%").append(search).append("%'");
            first = false;
        }

        sql.addWhere(whereClause.toString());
        sql.addWhere("a.status IN ('Active'" + (contractor.isDemo() ? ",'Demo','Pending'" : "") + ")");
        sql.addWhere("a.type = 'Operator'");

        sql.addGroupBy("a.id");

        return sql.toString();
    }

    // for injecting for unit tests
    private Database database() {
        if (database == null) {
            return new Database();
        }

        return database;
    }

    public String searchShowAll() throws Exception {
        findContractor();

        searchResults = new ArrayList<OperatorAccount>();
        Database db = database();

        SelectSQL showAll = new SelectSQL("accounts a");
        showAll.addField("a.id opID");
        showAll.addField("a.name");
        showAll.addField("a.dbaName");
        showAll.addField("a.city");
        showAll.addField("a.countrySubdivision");
        showAll.addField("a.country");
        showAll.addField("a.status");
        showAll.addField("a.onsiteServices");
        showAll.addField("a.offsiteServices");
        showAll.addField("a.materialSupplier");
        showAll.addField("a.transportationServices");
        showAll.addWhere("a.type = 'Operator'");
        showAll.addWhere("a.status = 'Active'");
        showAll.addWhere("a.id NOT IN (SELECT genID from generalContractors WHERE subID = " + contractor.getId() + " )");
        showAll.addOrderBy("a.name");

        List<BasicDynaBean> data = db.select(showAll.toString(), true);
        processSearchResults(data);

        return "search";
    }

    public String setRequestedBy() {
        if (operator.getId() > 0) {
            contractor.setRequestedBy(operator);

            if (contractor.getAccountLevel().isBidOnly() && !contractor.getRequestedBy().isAcceptsBids()) {
                contractor.setAccountLevel(AccountLevel.BidOnly);
                contractor.setRenew(true);
                billingService.calculateContractorInvoiceFees(contractor);
                contractor.syncBalance();
            }

            accountDao.save(contractor);
        }

        return JSON;
    }

    public String switchToTrialAccount() {
        contractor.setAccountLevel(AccountLevel.BidOnly);
        contractor.setRenew(false);
        billingService.calculateContractorInvoiceFees(contractor);
        contractor.syncBalance();
        accountDao.save(contractor);

        return BLANK;
    }

    @SuppressWarnings("unchecked")
    public String validateBidOnly() {
        json.put("isBidOnlyOperator", operatorDao.find(operator.getId()).isAcceptsBids());
        json.put("isBidOnlyContractor", accountDao.find(contractor.getId()).getAccountLevel().isBidOnly());
        return JSON;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<ContractorOperator> getCurrentOperators() {
        return currentOperators;
    }

    public List<OperatorAccount> getSearchResults() {
        return searchResults;
    }

    public OperatorAccount getOperator() {
        return operator;
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ContractorType getType() {
        return type;
    }

    public void setType(ContractorType type) {
        this.type = type;
    }

    public Boolean getCompetitorAnswer() {
        return competitorAnswer;
    }

    public void setCompetitorAnswer(Boolean competitorAnswer) {
        this.competitorAnswer = competitorAnswer;
    }

    public boolean isTrialContractor() {
        // Enforcing that bid only contractors should not be associated with an
        // operator which does not accept bid only
        for (ContractorOperator co : contractor.getOperators()) {
            if (!co.getOperatorAccount().isAcceptsBids()) {
                return false;
            }
        }
        // All current Operators accept bid only

        // This is called after the co has been created and set. So no need to
        // check current operator. Current operator should be in list already.
        if (contractor.getStatus().isPending() && contractor.getAccountLevel().isFull()) {
            return true;
        }

        return false;
    }

    public int getTypeCount(OperatorAccount op) {
        int count = 0;

        if (contractor.isOnsiteServices() && op.isOnsiteServices())
            count++;
        if (contractor.isOffsiteServices() && op.isOffsiteServices())
            count++;
        if (contractor.isMaterialSupplier() && op.isMaterialSupplier())
            count++;

        return count;
    }

    public boolean isNeedsGeneralContractorModal(OperatorAccount operator) {
        if (operator.isGeneralContractor()) {
            List<OperatorAccount> linkedClientSites = operator.getLinkedClientSites();

            for (OperatorAccount contractorOperator : contractor.getOperatorAccounts()) {
                linkedClientSites.remove(contractorOperator);
            }

            return linkedClientSites.size() > 0;
        }

        return false;
    }

    private void addFacilitiesBasedOnRegistrationRequest() throws Exception {
        // Get request off of the session
        Object request = ActionContext.getContext().getSession().get("requestID");

        int requestID = 0;
        if (request != null) {
            requestID = (Integer) request;
        }

        if (requestID > 0) {
            // Clear session variable
            ActionContext.getContext().getSession().remove("requestID");
            ContractorRegistrationRequest crr = contractorRegistrationRequestDAO.find(requestID);
            contractor.setRequestedBy(crr.getRequestedBy());

            facilityChanger.setContractor(contractor);
            facilityChanger.setOperator(crr.getRequestedBy().getId());
            facilityChanger.setPermissions(permissions);
            facilityChanger.add();

            // add in tags
            String ids = crr.getOperatorTags();
            if (ids == null)
                ids = "";
            StringTokenizer st = new StringTokenizer(ids, ", ");
            while (st.hasMoreElements()) {
                int tagId = Integer.parseInt(st.nextToken());
                if (tagId > 0) {
                    ContractorTag cTag = new ContractorTag();
                    cTag.setContractor(contractor);
                    cTag.setTag(new OperatorTag());
                    cTag.getTag().setId(tagId);
                    cTag.setAuditColumns(permissions);
                    cTag.setCreatedBy(crr.getRequestedByUser());
                    cTag.setUpdatedBy(crr.getRequestedByUser());
                    contractor.getOperatorTags().add(cTag);
                    contractor.incrementRecalculation(10);
                    accountDao.save(contractor);
                    noteDAO.save(NoteFactory.generateNoteForTaggingContractor(cTag, permissions));
                }
            }

            billingService.calculateContractorInvoiceFees(contractor);
            contractor.syncBalance();

            accountDao.save(contractor);
        }
    }

    private void processSearchResults(List<BasicDynaBean> data) {
        for (BasicDynaBean d : data) {
            OperatorAccount o = new OperatorAccount();

            o.setId(Integer.parseInt(d.get("opID").toString()));
            o.setName(d.get("name").toString());
            if (d.get("dbaName") != null) {
                o.setDbaName(d.get("dbaName").toString());
            }

            if (d.get("city") != null) {
                o.setCity(d.get("city").toString());
            }

            if (d.get("countrySubdivision") != null) {
                o.setCountrySubdivision(new CountrySubdivision(d.get("countrySubdivision").toString()));
            }

            if (d.get("country") != null) {
                o.setCountry(new Country(d.get("country").toString()));
            }

            o.setStatus(AccountStatus.valueOf(d.get("status").toString()));

            o.setOnsiteServices(1 == (Integer) d.get("onsiteServices"));
            o.setOffsiteServices(1 == (Integer) d.get("offsiteServices"));
            o.setMaterialSupplier(1 == (Integer) d.get("materialSupplier"));
            o.setTransportationServices(1 == (Integer) d.get("transportationServices"));

            if (contractor.isOnsiteServices() && o.isOnsiteServices() || contractor.isOffsiteServices()
                    && o.isOffsiteServices() || contractor.isMaterialSupplier() && o.isMaterialSupplier()
                    || contractor.isTransportationServices() && o.isTransportationServices())
                searchResults.add(o);
        }
    }

    private void recalculate() throws Exception {
        findContractor();
        billingService.calculateContractorInvoiceFees(contractor);
        contractor.syncBalance();
        accountDao.save(contractor);
    }

    private void prepareFacilityChanger() {
        facilityChanger.setContractor(contractor);
        facilityChanger.setOperator(operator.getId());
        facilityChanger.setPermissions(permissions);
    }
}
