package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.OperatorAccount")
@Table(name = "operators")
public class OperatorAccount extends Account {

//    public static final int AI = 19344;
//    public static final int BASF = 6115;
//    public static final int CEDA_CANADA = 29228;
//    public static final int CEDA_USA = 29230;
//    public static final int CINTAS = 17144;
//    public static final int CINTAS_CANADA = 17302;
//    public static final int OLDCASTLE = 20481;
//    public static final int PicsConsortium = 4;
//    public static final int PICSPSM = 8;
//    public static final int SUNCOR = 10566;
//    public static final int IMPERIAL_OIL = 37472;
//    public static final int SALES = 23325;
//    public static final int TESORO = 1436;
//    public static final int VALSPAR_GARLAND = 23335;
//
    private OperatorAccount parent;

    private OperatorAccount inheritFlagCriteria;
//    private OperatorAccount inheritInsuranceCriteria;
//
//    private String doContractorsPay = "Yes";
    private YesNo canSeeInsurance = YesNo.No;
//    private User insuranceAuditor;
//    private YesNo isUserManualUploaded = YesNo.No;
//    private YesNo approvesRelationships = YesNo.No;
//    private boolean verifiedByPics = true;
    private OshaType oshaType = OshaType.OSHA;
    private boolean primaryCorporate = false;
//    private boolean autoApproveInsurance = false;
//    private String requiredTags;
//    private BigDecimal activationFee;
//    private BigDecimal discountPercent = BigDecimal.ZERO;
//    private Date discountExpiration;
//    private boolean inPicsConsortium = false;
//    private String salesForceID;
//    private boolean requiresEmployeeGuard;
//
    private List<Facility> corporateFacilities = new ArrayList<>();
    private List<Facility> operatorFacilities = new ArrayList<>();
//    private List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
//    private List<OperatorAccount> operatorChildren = new ArrayList<OperatorAccount>();
//    private List<OperatorAccount> childOperators = new ArrayList<OperatorAccount>();
//    private List<OperatorAccount> parentOperators = new ArrayList<OperatorAccount>();
//    private List<OperatorTag> tags = new ArrayList<OperatorTag>();
//    private List<OperatorForm> operatorForms = new ArrayList<OperatorForm>();
    private List<FlagCriteriaOperator> flagCriteria = new ArrayList<FlagCriteriaOperator>();
//    private List<JobSite> jobSites = new ArrayList<JobSite>();
//    private List<OperatorCompetency> competencies = new ArrayList<OperatorCompetency>();
//    private Set<Integer> visibleAuditTypes = null;
//    private List<Facility> linkedClients = new ArrayList<Facility>();
//    private List<Facility> linkedGeneralContractors = new ArrayList<Facility>();
//
//    public OperatorAccount() {
//        this.type = Account.OPERATOR_ACCOUNT_TYPE;
//        this.onsiteServices = true;
//        this.offsiteServices = true;
//        this.materialSupplier = true;
//        this.transportationServices = true;
//    }
//
//    public OperatorAccount(String name) {
//        this.name = name;
//        this.type = Account.OPERATOR_ACCOUNT_TYPE;
//        this.onsiteServices = true;
//        this.offsiteServices = true;
//        this.materialSupplier = true;
//        this.transportationServices = true;
//    }
//
//    @Transient
//    public String getFullName() {
//        if (Strings.isNullOrEmpty(dbaName)) {
//            return name;
//        }
//
//        return dbaName;
//    }
//
//    /**
//     * Yes, No, Multiple
//     */
//    @Column(nullable = false, length = 8)
//    @ReportField()
//    public String getDoContractorsPay() {
//        return this.doContractorsPay;
//    }
//
//    public void setDoContractorsPay(String doContractorsPay) {
//        this.doContractorsPay = doContractorsPay;
//    }
//
    @Type(type = "com.picsauditing.flagcalculator.entities.EnumMapperWithEmptyStrings", parameters = {@org.hibernate.annotations.Parameter(name = "enumClass", value = "com.picsauditing.flagcalculator.entities.YesNo")})
    @Column(name = "canSeeInsurance", nullable = false)
    @Enumerated(EnumType.STRING)
    public YesNo getCanSeeInsurance() {
        if (this.canSeeInsurance == null) {
            this.canSeeInsurance = YesNo.No;
        }
        return this.canSeeInsurance;
    }

    public void setCanSeeInsurance(YesNo canSeeInsurance) {
        this.canSeeInsurance = canSeeInsurance;
    }

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "insuranceAuditor_id")
//    public User getInsuranceAuditor() {
//        return this.insuranceAuditor;
//    }
//
//    public void setInsuranceAuditor(User user) {
//        this.insuranceAuditor = user;
//    }
//
//    @Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {@Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo")})
//    @Column(name = "isUserManualUploaded", nullable = false)
//    @Enumerated(EnumType.STRING)
//    @ReportField()
//    public YesNo getIsUserManualUploaded() {
//        return this.isUserManualUploaded;
//    }
//
//    public void setIsUserManualUploaded(YesNo isUserManualUploaded) {
//        this.isUserManualUploaded = isUserManualUploaded;
//    }
//
//    @Column(name = "verifiedByPics", nullable = false)
//    @Enumerated(EnumType.ORDINAL)
//    public boolean isVerifiedByPics() {
//        return verifiedByPics;
//    }
//
//    public void setVerifiedByPics(boolean verifiedByPics) {
//        this.verifiedByPics = verifiedByPics;
//    }
//
    @Enumerated(EnumType.STRING)
    public OshaType getOshaType() {
        return oshaType;
    }

    public void setOshaType(OshaType oshaType) {
        this.oshaType = oshaType;
    }

    public boolean isPrimaryCorporate() {
        return primaryCorporate;
    }

    public void setPrimaryCorporate(boolean primaryCorporate) {
        this.primaryCorporate = primaryCorporate;
    }

//    @ReportField(type = FieldType.Boolean)
//    public boolean isAutoApproveInsurance() {
//        return autoApproveInsurance;
//    }
//
//    public void setAutoApproveInsurance(boolean autoApproveInsurance) {
//        this.autoApproveInsurance = autoApproveInsurance;
//    }
//
//    @Transient
//    public boolean isHasDiscount() {
//        return getDiscountPercent().compareTo(BigDecimal.ZERO) > 0 && getDiscountExpiration() != null
//                && new Date().before(getDiscountExpiration());
//    }
//
//    @Transient
//    public OperatorAccount getInheritedDiscountPercentOperator() {
//        // check direct parents
//        OperatorAccount parent = getParent();
//        while (parent != null && parent.getId() != this.getId()) {
//            if (parent.isHasDiscount()) {
//                return parent;
//            }
//
//            parent = parent.getParent();
//        }
//
//        // check corporate associations
//        for (Facility f : getCorporateFacilities()) {
//            if (f.getCorporate().isHasDiscount()) {
//                return f.getCorporate();
//            }
//        }
//
//        return null;
//    }
//
//    @ReportField()
//    public String getRequiredTags() {
//        return requiredTags;
//    }
//
//    public void setRequiredTags(String requiredTags) {
//        this.requiredTags = requiredTags;
//    }
//
//    public BigDecimal getActivationFee() {
//        return activationFee;
//    }
//
//    public void setActivationFee(BigDecimal activationFee) {
//        this.activationFee = activationFee;
//    }
//
//    @Column(nullable = false)
//    public BigDecimal getDiscountPercent() {
//        return discountPercent;
//    }
//
//    public void setDiscountPercent(BigDecimal discountPercent) {
//        this.discountPercent = discountPercent;
//    }
//
//    @Transient
//    public BigDecimal getScaledDiscountPercent() {
//        return getDiscountPercent().multiply(new BigDecimal(100));
//    }
//
//    @Transient
//    public void setScaledDiscountPercent(BigDecimal discountPercent) {
//        if (discountPercent == null) {
//            discountPercent = BigDecimal.ZERO;
//        }
//        discountPercent = discountPercent.divide(new BigDecimal(100));
//        setDiscountPercent(discountPercent);
//    }
//
//    @Temporal(TemporalType.DATE)
//    @ReportField(type = FieldType.Date)
//    public Date getDiscountExpiration() {
//        return discountExpiration;
//    }
//
//    public void setDiscountExpiration(Date discountExpiration) {
//        this.discountExpiration = discountExpiration;
//    }
//
//    @Transient
//    public boolean isAcceptsList() {
//        return this.isDescendantOf(OperatorAccount.SUNCOR) || this.isDescendantOf(OperatorAccount.CINTAS)
//                || this.isOrIsDescendantOf(OperatorAccount.CEDA_CANADA)
//                || this.isOrIsDescendantOf(OperatorAccount.CEDA_USA);
//    }
//
    @ManyToOne
    @JoinColumn(name = "inheritFlagCriteria")
    public OperatorAccount getInheritFlagCriteria() {
        return inheritFlagCriteria;
    }

    public void setInheritFlagCriteria(OperatorAccount inheritFlagCriteria) {
        this.inheritFlagCriteria = inheritFlagCriteria;
    }

//    @ManyToOne
//    @JoinColumn(name = "inheritInsuranceCriteria")
//    public OperatorAccount getInheritInsuranceCriteria() {
//        return inheritInsuranceCriteria;
//    }
//
//    public void setInheritInsuranceCriteria(OperatorAccount inheritInsuranceCriteria) {
//        this.inheritInsuranceCriteria = inheritInsuranceCriteria;
//    }
//
//    @OneToMany(mappedBy = "account")
//    public List<OperatorForm> getOperatorForms() {
//        return operatorForms;
//    }
//
//    public void setOperatorForms(List<OperatorForm> operatorForms) {
//        this.operatorForms = operatorForms;
//    }
//
//    @Transient
//    public List<OperatorForm> getInsuranceForms() {
//        ArrayList<OperatorForm> forms = new ArrayList<OperatorForm>();
//
//        for (OperatorForm f : operatorForms) {
//            if ("Insurance".equals(f.getFormType())) {
//                forms.add(f);
//            }
//        }
//
//        return forms;
//    }
//
    @OneToMany(mappedBy = "operator", orphanRemoval = true)
    @Where(clause = "type IS NULL")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public List<Facility> getCorporateFacilities() {
        return corporateFacilities;
    }

    public void setCorporateFacilities(List<Facility> corporateFacilities) {
        this.corporateFacilities = corporateFacilities;
    }

//    /**
//     * @return a list of all "associated" operator accounts associated via the
//     *         facilities intersection table for example, BASF would contain
//     *         BASF Port Arthur but not BASF Freeport Hub
//     * @see getOperatorAccounts()
//     */
    @OneToMany(mappedBy = "corporate")
    @Where(clause = "type IS NULL")
    public List<Facility> getOperatorFacilities() {
        return operatorFacilities;
    }

    public void setOperatorFacilities(List<Facility> operatorFacilities) {
        this.operatorFacilities = operatorFacilities;
    }

//    /**
//     * @return a list of all the "direct" child operators/corporates mapped
//     *         through operator.parentID for example, BASF would contain BASF
//     *         Freeport Hub, but not BASF Port Arthur
//     * @see getOperatorFacilities()
//     */
//    @OneToMany(mappedBy = "parent")
//    public List<OperatorAccount> getOperatorChildren() {
//        return operatorChildren;
//    }
//
//    public void setOperatorChildren(List<OperatorAccount> operatorChildren) {
//        this.operatorChildren = operatorChildren;
//    }
//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentID", nullable = true)
    public OperatorAccount getParent() {
        return parent;
    }

    public void setParent(OperatorAccount parent) {
        this.parent = parent;
    }

//    /**
//     * @return a list of contractors linked to this operator
//     */
//    @OneToMany(mappedBy = "operatorAccount")
//    public List<ContractorOperator> getContractorOperators() {
//        return contractorOperators;
//    }
//
//    public void setContractorOperators(List<ContractorOperator> contractorOperators) {
//        this.contractorOperators = contractorOperators;
//    }
//
//    @OneToMany(mappedBy = "operator", cascade = {CascadeType.ALL})
//    public List<OperatorTag> getTags() {
//        return tags;
//    }
//
//    @Transient
//    public List<OperatorTag> getInheritedTags() {
//        List<OperatorTag> tags = new ArrayList<>();
//        tags.addAll(this.getTags());
//
//        for (OperatorAccount parent: this.getParentOperators()) {
//            for (OperatorTag tag: parent.getTags()) {
//                if (tag.isInheritable()) {
//                    tags.add(tag);
//                }
//            }
//        }
//
//        return tags;
//    }
//
//    public void setTags(List<OperatorTag> value) {
//        this.tags = value;
//    }
//
    @OneToMany(mappedBy = "operator", cascade = {CascadeType.ALL})
    public List<FlagCriteriaOperator> getFlagCriteria() {
        return flagCriteria;
    }

    public void setFlagCriteria(List<FlagCriteriaOperator> flagCriteria) {
        this.flagCriteria = flagCriteria;
    }

//    @OneToMany(mappedBy = "operator", cascade = {CascadeType.REMOVE})
//    public List<JobSite> getJobSites() {
//        return jobSites;
//    }
//
//    public void setJobSites(List<JobSite> jobSites) {
//        this.jobSites = jobSites;
//    }
//
//    @OneToMany(mappedBy = "operator")
//    public List<OperatorCompetency> getCompetencies() {
//        return competencies;
//    }
//
//    public void setCompetencies(List<OperatorCompetency> competencies) {
//        this.competencies = competencies;
//    }
//
//    @OneToMany(mappedBy = "operator")
//    @Where(clause = "type = 'GeneralContractor'")
//    public List<Facility> getLinkedClients() {
//        return linkedClients;
//    }
//
//    public void setLinkedClients(List<Facility> linkedClients) {
//        this.linkedClients = linkedClients;
//    }
//
//    @Transient
//    public List<OperatorAccount> getLinkedClientSites() {
//        List<OperatorAccount> linkedClientSites = new ArrayList<OperatorAccount>();
//        for (Facility facility : getLinkedClients()) {
//            OperatorAccount linkedClientSite = facility.getCorporate();
//
//            if (linkedClientSite.getStatus().isActive()
//                    || (this.status.isDemo() && linkedClientSite.getStatus().isDemo())) {
//                linkedClientSites.add(linkedClientSite);
//            }
//        }
//
//        return linkedClientSites;
//    }
//
//    @OneToMany(mappedBy = "corporate")
//    @Where(clause = "type = 'GeneralContractor'")
//    public List<Facility> getLinkedGeneralContractors() {
//        return linkedGeneralContractors;
//    }
//
//    public void setLinkedGeneralContractors(List<Facility> linkedGeneralContractors) {
//        this.linkedGeneralContractors = linkedGeneralContractors;
//    }
//
//    @Transient
//    public List<OperatorAccount> getLinkedGeneralContractorOperatorAccounts() {
//        List<OperatorAccount> linkedGeneralContractorOperatorAccounts = new ArrayList<OperatorAccount>();
//        for (Facility facility : getLinkedGeneralContractors()) {
//            OperatorAccount linkedGeneralContractor = facility.getOperator();
//
//            if (linkedGeneralContractor.getStatus().isActive()
//                    || (this.status.isDemo() && linkedGeneralContractor.getStatus().isDemo())) {
//                linkedGeneralContractorOperatorAccounts.add(linkedGeneralContractor);
//            }
//        }
//
//        return linkedGeneralContractorOperatorAccounts;
//    }
//
//    @Transient
//    public boolean isRequiresClientSiteOrGeneralContractorSelection() {
//        return !getLinkedClientSites().isEmpty();
//    }
//
//    @Transient
//    public List<OperatorAccount> getClientSitesOrGeneralContractors() {
//        if (isGeneralContractor()) {
//            return getLinkedClientSites();
//        } else {
//            return getLinkedGeneralContractorOperatorAccounts();
//        }
//    }
//
//    @ManyToMany(targetEntity = OperatorAccount.class, cascade = {CascadeType.ALL})
//    @JoinTable(name = "facilities", joinColumns = @JoinColumn(name = "corporateID"), inverseJoinColumns = @JoinColumn(name = "opID"))
//    public List<OperatorAccount> getChildOperators() {
//        return childOperators;
//    }
//
//    public void setChildOperators(List<OperatorAccount> childOperators) {
//        this.childOperators = childOperators;
//    }
//
//    @ManyToMany(targetEntity = OperatorAccount.class, cascade = {CascadeType.ALL})
//    @JoinTable(name = "facilities", joinColumns = @JoinColumn(name = "opID"), inverseJoinColumns = @JoinColumn(name = "corporateID"))
//    public List<OperatorAccount> getParentOperators() {
//        return parentOperators;
//    }
//
//    public void setParentOperators(List<OperatorAccount> parentOperators) {
//        this.parentOperators = parentOperators;
//    }
//
//    @Transient
//    public OperatorAccount getTopAccount() {
//        OperatorAccount topAccount = this;
//        if (this.getParent() != null) {
//            topAccount = this.getParent();
//        }
//
//        for (Facility facility : getCorporateFacilities()) {
//            if (facility.getCorporate().isPrimaryCorporate()) {
//                topAccount = facility.getCorporate();
//                break;
//            }
//        }
//        return topAccount;
//    }
//
//    @Transient
//    public boolean isInsureguardSubscriber() {
//        return canSeeInsurance.equals(YesNo.Yes);
//    }
//
//    @Transient
//    public List<Integer> getOperatorHeirarchy() {
//        return getOperatorHeirarchy(true);
//    }
//
//    @Transient
//    public List<Integer> getOperatorHeirarchy(boolean includePicsConsortium) {
//        List<Integer> list = new ArrayList<Integer>();
//        // Add myself
//        list.add(this.id);
//
//        OperatorAccount topAccount = getTopAccount();
//        for (Facility facility : getCorporateFacilities()) {
//            if (!facility.getCorporate().equals(topAccount)) {
//                // Add parent's that aren't my primary parent
//                if (includePicsConsortium || !facility.getCorporate().inPicsConsortium) {
//                    list.add(facility.getCorporate().getId());
//                }
//            }
//        }
//        if (!topAccount.equals(this)) {
//            // Add my parent
//            list.add(topAccount.getId());
//        }
//        return list;
//    }
//
//    /**
//     * Please use sparingly!! This does a call to a Spring loaded DAO
//     *
//     * @return Set of AuditTypeIDs
//     */
//    @Transient
//    public Set<Integer> getVisibleAuditTypes() {
//        if (visibleAuditTypes == null) {
//            // This isn't pretty but it works
//            AuditDecisionTableDAO dao = (AuditDecisionTableDAO) SpringUtils.getBean("AuditDecisionTableDAO");
//            visibleAuditTypes = dao.getAuditTypes(this);
//        }
//        return visibleAuditTypes;
//    }
//
//    public void setVisibleAuditTypes(Set<Integer> visibleAuditTypes) {
//        this.visibleAuditTypes = visibleAuditTypes;
//    }
//
//    public boolean isInPicsConsortium() {
//        return inPicsConsortium;
//    }
//
//    public void setInPicsConsortium(boolean inPicsConsortium) {
//        this.inPicsConsortium = inPicsConsortium;
//    }
//
//    @ReportField()
//    public String getSalesForceID() {
//        return salesForceID;
//    }
//
//    public void setSalesForceID(String salesForceID) {
//        this.salesForceID = salesForceID;
//    }
//
//    public boolean isRequiresEmployeeGuard() {
//        return requiresEmployeeGuard;
//    }
//
//    public void setRequiresEmployeeGuard(boolean requiresEmployeeGuard) {
//        this.requiresEmployeeGuard = requiresEmployeeGuard;
//    }
//
//    @Transient
//    @Override
//    public String getAutocompleteItem() {
//        return "[" + id + "] " + name;
//    }
//
//    @Transient
//    public int getRulePriorityLevel() {
//        switch (id) {
//            case 4:
//                // PICS Global
//                return 1;
//            case 5:
//                // PICS US
//            case 6:
//                // PICS Canada
//            case 7:
//                // PICS UAE
//            case 8:
//                // PICS PSM
//                return 2;
//        }
//
//        if (isPrimaryCorporate()) {
//            return 3;
//        }
//
//        // Hubs and Divisions
//        if (isCorporate()) {
//            return 4;
//        }
//
//        // All other operators
//        return 5;
//    }
//
//    @Transient
//    public boolean isGeneralContractorFree() {
//        return isGeneralContractor() && "No".equals(getDoContractorsPay());
//    }
//
//    @Transient
//    public boolean hasTagCategory(OperatorTagCategory category) {
//        if (category != null) {
//            for (OperatorTag tag : getTags()) {
//                if (category == tag.getCategory()) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    public FlagCriteria getFlagCriteria(int flagCriteriaId) {
//        for (FlagCriteriaOperator flag : getFlagCriteria()) {
//            if (flag.getCriteria().getId() == flagCriteriaId) {
//                return flag.getCriteria();
//            }
//        }
//        return null;
//    }
//
//    public static OperatorAccountBuilder builder() {
//        return new OperatorAccountBuilder();
//    }
//
//    // named get/set for convenient ognl reference from JSPs
//    @Transient
//    public User getCurrentAccountRepresentative() {
//        AccountUser accountUser = getCurrentAccountUserOfRole(UserAccountRole.PICSAccountRep);
//        if (accountUser != null) {
//            return accountUser.getUser();
//        }
//        return null;
//    }
//
//    @Transient
//    public void setCurrentAccountRepresentative(User newAccountRep, int createdById) {
//        expireCurrentAccountUserOfRole(UserAccountRole.PICSAccountRep);
//        addNewCurrentAccountUserOfRole(newAccountRep, UserAccountRole.PICSAccountRep, createdById);
//    }
//
//    @Transient
//    public AccountUser getCurrentAccountRepresentativeAccountUser() {
//        return getCurrentAccountUserOfRole(UserAccountRole.PICSAccountRep);
//    }
//
//    @Transient
//    public boolean hasCompetencyRequiringDocumentation() {
//        if (getCompetencies() != null && !getCompetencies().isEmpty()) {
//            for (OperatorCompetency operatorCompetency : getCompetencies()) {
//                if (operatorCompetency.isRequiresDocumentation()) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
}