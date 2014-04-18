package com.picsauditing.jpa.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PICS
 *
 */
@SuppressWarnings("serial")
//@Entity
//@Table(name = "contractor_audit")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorAudit /*extends AbstractIndexableTable*/ {
    private Logger logger = LoggerFactory.getLogger(ContractorAudit.class);

    private AuditType auditType;
    private ContractorAccount contractorAccount;
//    private Employee employee;
//    private Date expiresDate;
//    private Date effectiveDate;
//    private User auditor;
//    private User closingAuditor;
//    private Date assignedDate;
//    private Date paidDate;
//    private OperatorAccount requestingOpAccount;
    private int score;
//    private boolean manuallyAdded;
//    private boolean needsIndexing = true;
//    private String auditFor;
//    private Date lastRecalculation;
//
//    private Date contractorConfirm;
//    private Date auditorConfirm;
//    private Date scheduledDate;
//    private String auditLocation;
//    private String contractorContact;
//    private String address;
//    private String address2;
//    private String city;
//    private String countrySubdivision;
//    private String zip;
//    private String country;
//    private float latitude;
//    private float longitude;
//    private String phone;
//    private String phone2;
//    private Integer ruleID;
//    private Date slaDate;
//
//    private List<AuditCatData> categories = new ArrayList<AuditCatData>();
//    private List<AuditData> data = new ArrayList<AuditData>();
    private List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();
//    private Map<AuditStatus, Integer> caoStats = null;
//    private ContractorAudit previousAudit;
//
//    @ManyToOne
//    @JoinColumn(name = "auditTypeID")
    public AuditType getAuditType() {
        return auditType;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

//    @ManyToOne
//    @JoinColumn(name = "conID")
    public ContractorAccount getContractorAccount() {
        return contractorAccount;
    }

    public void setContractorAccount(ContractorAccount contractor) {
        this.contractorAccount = contractor;
    }

//    @ManyToOne
//    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(name = "employeeID")
//    public Employee getEmployee() {
//        return employee;
//    }
//
//    public void setEmployee(Employee employee) {
//        this.employee = employee;
//    }
//
//    @OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
    public List<ContractorAuditOperator> getOperators() {
        return operators;
    }

//    // TODO replace old uses of getOperators with sorted one
//    @Transient
//    public List<ContractorAuditOperator> getSortedOperators() {
//        List<ContractorAuditOperator> caos = operators;
//        Collections.sort(caos, getComparator());
//        return caos;
//    }
//
    public void setOperators(List<ContractorAuditOperator> operators) {
        this.operators = operators;
    }

//    // TODO Should we pass in permissions to this? See ConInsureGuard.java
//    // execute() lines 54-67
//    @Transient
//    public List<ContractorAuditOperator> getOperatorsVisible() {
//        return new Grepper<ContractorAuditOperator>() {
//
//            public boolean check(ContractorAuditOperator t) {
//                return t.isVisible();
//            }
//        }.grep(this.operators);
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.ExpirationDate, importance = FieldImportance.Average)
//    public Date getExpiresDate() {
//        return expiresDate;
//    }
//
//    public void setExpiresDate(Date expiresDate) {
//        this.expiresDate = expiresDate;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.Date, importance = FieldImportance.Average)
//    public Date getEffectiveDate() {
//        return effectiveDate;
//    }
//
//    public void setEffectiveDate(Date effectiveDate) {
//        this.effectiveDate = effectiveDate;
//    }
//
//    @Transient
//    public boolean isExpired() {
//        if (expiresDate == null)
//            return false;
//        return expiresDate.before(new Date());
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "auditorID")
//    public User getAuditor() {
//        return auditor;
//    }
//
//    public void setAuditor(User auditor) {
//        this.auditor = auditor;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "closingAuditorID")
//    public User getClosingAuditor() {
//        return closingAuditor;
//    }
//
//    public void setClosingAuditor(User closingAuditor) {
//        this.closingAuditor = closingAuditor;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "previousAuditID")
//    public ContractorAudit getPreviousAudit() {
//        return previousAudit;
//    }
//
//    public void setPreviousAudit(ContractorAudit previousAudit) {
//        this.previousAudit = previousAudit;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.Date)
//    public Date getAssignedDate() {
//        return assignedDate;
//    }
//
//    public void setAssignedDate(Date assignedDate) {
//        this.assignedDate = assignedDate;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.DateTime)
//    public Date getScheduledDate() {
//        return scheduledDate;
//    }
//
//    public void setScheduledDate(Date scheduledDate) {
//        this.scheduledDate = scheduledDate;
//    }
//
//    @Temporal(TemporalType.DATE)
//    public Date getPaidDate() {
//        return paidDate;
//    }
//
//    public void setPaidDate(Date paidDate) {
//        this.paidDate = paidDate;
//    }
//
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllOperators)
//    public Date getSlaDate() {
//        return slaDate;
//    }
//
//    public void setSlaDate(Date slaDate) {
//        this.slaDate = slaDate;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "requestedByOpID")
//    public OperatorAccount getRequestingOpAccount() {
//        return requestingOpAccount;
//    }
//
//    public void setRequestingOpAccount(OperatorAccount requestingOpAccount) {
//        this.requestingOpAccount = requestingOpAccount;
//    }
//
//    @Transient
//    public boolean isConductedOnsite() {
//        // we should save auditLocation as a boolean in the DB
//        if (auditLocation == null)
//            return false;
//        if (auditLocation.equals("Web"))
//            return false;
//        return true;
//    }
//
//    @ReportField(type = FieldType.String)
//    public String getAuditLocation() {
//        return auditLocation;
//    }
//
//    public void setAuditLocation(String auditLocation) {
//        this.auditLocation = auditLocation;
//    }
//
    /**
     * We may need to move this over to CAO someday
     *
     * @return
     */
//    @ReportField(type = FieldType.Integer, importance = FieldImportance.Average)
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Child tables

//    @OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
//    public List<AuditCatData> getCategories() {
////		// TODO clean up categories
////		for (AuditCatData auditCatData : categories) {
////			if (!auditCatData.getCategory().getAuditType().equals(auditType)) {
////			}
////		}
//        return categories;
//    }
//
//    public void setCategories(List<AuditCatData> categories) {
//        this.categories = categories;
//    }
//
//    @OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
//    public List<AuditData> getData() {
//        return data;
//    }
//
//    public void setData(List<AuditData> data) {
//        this.data = data;
//    }
//
//    // TRANSIENT ///////////////////////////////
//
//    /**
//     *
//     *
//     * @return True if audit expires this year and it's before March 1
//     * @see willExpireSoon() they are basically the same thing
//     */
//    @Transient
//    public boolean isAboutToExpire() {
//        if (expiresDate == null)
//            return false;
//
//        if (DateBean.getDateDifference(expiresDate) <= 60)
//            return true;
//
//        return false;
//    }
//
//    /**
//     *
//     * @see isAboutToExpire() they are basically the same thing, but we decided
//     *      to keep both since isAboutToExpire accounts for the year which is
//     *      important when dealing with PQF class audits
//     */
//    @Transient
//    public boolean willExpireSoon() {
//        int daysToExpiration = 0;
//        if (getExpiresDate() == null)
//            daysToExpiration = 1000;
//        else
//            daysToExpiration = DateBean.getDateDifference(expiresDate);
//
//        if (auditType.getClassType() == AuditTypeClass.Policy) {
//            return daysToExpiration <= 15;
//        } else if (auditType.getId() == AuditType.COR) {
//            return daysToExpiration <= 180;
//        } else if (auditType.getId() == AuditType.SSIP) {
//            return daysToExpiration <= 30;
//        } else {
//            return daysToExpiration <= 90;
//        }
//    }
//
//    @Transient
//    public Date getValidDate() {
//        if (auditType.isAnnualAddendum())
//            return effectiveDate;
//        if (hasCaoStatusAfter(AuditStatus.Incomplete)) {
//            if (effectiveDate == null)
//                return new Date();
//            else
//                return effectiveDate;
//        } else
//            return new Date();
//    }
//
//    @Transient
//    public Date getEffectiveDateLabel() {
//        if (auditType.isPicsPqf()) {
//            // We normally don't call getEffectiveDateLabel() for PQF
//            return new Date();
//        }
//
//        if (auditType.isAnnualAddendum()) {
//            // We normally don't call getEffectiveDateLabel() for Annual Update
//            Calendar cal = Calendar.getInstance();
//            cal.set(Integer.parseInt(auditFor), Calendar.JANUARY, 1);
//            return cal.getTime();
//        }
//
//        if (auditType.isWCB()) {
//            if (!Strings.isEmpty(auditFor)) {
//                return DateBean.parseDate("01/01/" + auditFor);
//            }
//        }
//
//        if (effectiveDate != null) {
//            return effectiveDate;
//        }
//
//        return creationDate;
//    }
//
//    @Transient
//    public List<ContractorAuditOperator> getCurrentOperators() {
//        List<ContractorAuditOperator> currentCaos = new ArrayList<ContractorAuditOperator>();
//
//        for (ContractorAuditOperator cao : getOperators()) {
//            // Check to see if the CAO is visible
//            if (cao.isVisible()) {
//                // BASF Corporate still needs insurance
//                for (ContractorOperator co : getContractorAccount().getNonCorporateOperators()) {
//                    // Iterate over gencon tables
//                    // co.getOperatorAccount() == BASF Abbotsford that's
//                    // attached to Ancon Marine
//                    if (cao.hasCaop(co.getOperatorAccount().getId())) {
//                        currentCaos.add(cao);
//                        break;
//                    }
//                }
//            }
//        }
//
//        Collections.sort(currentCaos, getComparator());
//
//        return currentCaos;
//    }
//
//    @Transient
//    public List<ContractorAuditOperator> getViewableOperators(Permissions permissions) {
//        List<ContractorAuditOperator> currentCaos = new ArrayList<ContractorAuditOperator>();
//
//        for (ContractorAuditOperator cao : getOperators()) {
//            if (cao.isVisibleTo(permissions)) {
//                currentCaos.add(cao);
//            }
//        }
//
//        Collections.sort(currentCaos, getComparator());
//        return currentCaos;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.Date)
//    public Date getContractorConfirm() {
//        return contractorConfirm;
//    }
//
//    public void setContractorConfirm(Date contractorConfirm) {
//        this.contractorConfirm = contractorConfirm;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.Date)
//    public Date getAuditorConfirm() {
//        return auditorConfirm;
//    }
//
//    public void setAuditorConfirm(Date auditorConfirm) {
//        this.auditorConfirm = auditorConfirm;
//    }
//
//    public boolean isManuallyAdded() {
//        return manuallyAdded;
//    }
//
//    public void setManuallyAdded(boolean manuallyAdded) {
//        this.manuallyAdded = manuallyAdded;
//    }
//
//    /**
//     * Who, what, or when is this audit for? Examples: OSHA/EMR for "2005" IM
//     * for "John Doe"
//     *
//     * @return
//     */
//    @ReportField(type = FieldType.String, importance = FieldImportance.Average)
//    public String getAuditFor() {
//        return auditFor;
//    }
//
//    public void setAuditFor(String auditFor) {
//        this.auditFor = auditFor;
//    }
//
//    public Date getLastRecalculation() {
//        return lastRecalculation;
//    }
//
//    public void setLastRecalculation(Date lastRecalculation) {
//        this.lastRecalculation = lastRecalculation;
//    }
//
//    @Transient
//    public String getFullAddress() {
//        if (Strings.isEmpty(address))
//            return contractorAccount.getFullAddress();
//
//        // We may want to extract this out and create a String address formatter
//        StringBuffer full = new StringBuffer();
//        full.append(address);
//        if (!Strings.isEmpty(address2))
//            full.append(" ").append(address2);
//        if (!Strings.isEmpty(city))
//            full.append(", ").append(city);
//        if (!Strings.isEmpty(countrySubdivision))
//            full.append(", ").append(countrySubdivision);
//        if (!Strings.isEmpty(country) && !country.equals("US") && !country.startsWith("United"))
//            full.append(", ").append(country);
//        if (!Strings.isEmpty(zip))
//            full.append(" ").append(zip);
//
//        return full.toString();
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getAddress2() {
//        return address2;
//    }
//
//    public void setAddress2(String address2) {
//        this.address2 = address2;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getCountrySubdivision() {
//        return countrySubdivision;
//    }
//
//    public void setCountrySubdivision(String countrySubdivision) {
//        this.countrySubdivision = countrySubdivision;
//    }
//
//    public String getZip() {
//        return zip;
//    }
//
//    public void setZip(String zip) {
//        this.zip = zip;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getPhone2() {
//        return phone2;
//    }
//
//    public void setPhone2(String phone2) {
//        this.phone2 = phone2;
//    }
//
//    public String getContractorContact() {
//        return contractorContact;
//    }
//
//    public void setContractorContact(String contractorContact) {
//        this.contractorContact = contractorContact;
//    }
//
//    @Transient
//    public Location getLocation() {
//        return new Location(latitude, longitude);
//    }
//
//    public float getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(float latitude) {
//        this.latitude = latitude;
//    }
//
//    public float getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(float longitude) {
//        this.longitude = longitude;
//    }
//
//    public Integer getRuleID() {
//        return ruleID;
//    }
//
//    public void setRuleID(Integer ruleID) {
//        this.ruleID = ruleID;
//    }
//
//    private static Comparator<ContractorAuditOperator> getComparator() {
//        return new Comparator<ContractorAuditOperator>() {
//
//            public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
//                if (o1.getOperator().getId() < 10)
//                    return new Integer(o1.getOperator().getId()).compareTo(new Integer(o2.getOperator().getId()));
//                return o1.getOperator().getName().compareTo(o2.getOperator().getName());
//            }
//        };
//    }
//
//    @Transient
//    public String getPrintableScore() {
//        String tempScore = "";
//
//        if (score <= 0)
//            tempScore = "None";
//        if (score < 50)
//            tempScore = "Red";
//        else if (score < 100)
//            tempScore = "Yellow";
//        else
//            tempScore = "Green";
//
//        return tempScore;
//    }
//
//    @Transient
//    public int getAuditorPayment() {
//        if (auditType.isDesktop())
//            return 75;
//        if (auditType.getId() == AuditType.IMPLEMENTATION_AUDIT) {
//            if (isConductedOnsite())
//                return 225;
//            else
//                return 175;
//        }
//        return 0;
//    }
//
//    public void setConductedOnsite(boolean conductedOnsite) {
//        auditLocation = conductedOnsite ? "Onsite" : "Web";
//    }
//
//    @Transient
//    public String getIndexType() {
//        return "AU";
//    }
//
//    @Transient
//    public boolean isCategoryApplicable(int catID) {
//        for (AuditCatData acd : this.categories) {
//            if (acd.getCategory().getId() == catID && acd.isApplies())
//                return true;
//        }
//        return false;
//    }
//
//    @Transient
//    public ContractorAuditOperator getCao(OperatorAccount operator) {
//        return getCao(operator.getOperatorHeirarchy());
//    }
//
//    @Transient
//    public ContractorAuditOperator getCao(List<Integer> sortedCaoOperatorCandidates) {
//        for (Integer parent : sortedCaoOperatorCandidates) {
//            for (ContractorAuditOperator cao : this.operators) {
//                if (cao.getOperator().getId() == parent)
//                    return cao;
//            }
//        }
//        for (ContractorAuditOperator cao : this.operators) {
//            if (cao.getOperator().getId() == OperatorAccount.PicsConsortium)
//                return cao;
//        }
//        return null;
//    }
//
//    @Transient
    public boolean hasCaoStatus(AuditStatus auditStatus) {
        for (ContractorAuditOperator cao : this.operators) {
            if (cao.isVisible() && cao.getStatus().equals(auditStatus))
                return true;
        }
        return false;
    }

//    @Transient
//    public boolean hasCaoStatusBefore(AuditStatus auditStatus) {
//        for (ContractorAuditOperator cao : this.operators) {
//            if (cao.isVisible() && cao.getStatus().before(auditStatus))
//                return true;
//        }
//        return false;
//    }
//
//    @Transient
    public boolean hasCaoStatusAfter(AuditStatus auditStatus) {
        return hasCaoStatusAfter(auditStatus, false);
    }

//    @Transient
    public boolean hasCaoStatusAfter(AuditStatus auditStatus, boolean ignoreNotApplicable) {
        for (ContractorAuditOperator cao : this.operators) {
            if (ignoreNotApplicable && cao.getStatus().equals(AuditStatus.NotApplicable))
                continue;
            if (cao.isVisible() && cao.getStatus().after(auditStatus)) {
                return true;
            }
        }
        return false;
    }

//    @Transient
//    public boolean isVisibleTo(Permissions permissions) {
//        if (permissions.isContractor())
//            return getAuditType().isCanContractorView();
//
//        if (permissions.isPicsEmployee())
//            return true;
//
//        for (ContractorAuditOperator cao : getOperators()) {
//            if (cao.isVisibleTo(permissions)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean hasOnlyInvisibleCaos() {
//        if (this.operators.size() > 0 && this.getOperatorsVisible().size() == 0) {
//            return true;
//        }
//
//        if (this.operators.isEmpty()) {
//            return true;
//        }
//
//        return false;
//    }
//
//    @Transient
//    public Set<AuditCategory> getVisibleCategories() {
//        Set<AuditCategory> visibleCategories = new HashSet<AuditCategory>();
//        for (AuditCatData categoryData : this.getCategories()) {
//            // Find all applicable categories
//            if (categoryData.isApplies()) {
//                visibleCategories.add(categoryData.getCategory());
//            }
//        }
//
//        // If the ancestors aren't applicable, then remove category
//        Iterator<AuditCategory> iterator = visibleCategories.iterator();
//        while (iterator.hasNext()) {
//            AuditCategory category = iterator.next();
//            AuditCategory parent = category.getParent();
//
//            // Breadcrumbs in case we have a cyclical relationship somewhere
//            Set<Integer> alreadyProcessed = new HashSet<Integer>();
//            alreadyProcessed.add(category.getId());
//
//            while (parent != null) {
//                if (alreadyProcessed.contains(parent.getId())) {
//                    logger.warn("Audit Categories {} have a cyclical relationship! Please check the configuration.",
//                            Strings.implode(alreadyProcessed));
//                    break;
//                }
//
//                if (!visibleCategories.contains(parent)) {
//                    iterator.remove();
//                    break;
//                }
//
//                alreadyProcessed.add(parent.getId());
//                parent = parent.getParent();
//            }
//        }
//
//        return visibleCategories;
//    }
//
//    @Transient
//    public Map<AuditStatus, Integer> getCaoStats(Permissions permissions) {
//        if (caoStats == null) {
//            caoStats = new TreeMap<AuditStatus, Integer>();
//            for (ContractorAuditOperator cao : operators) {
//                if (cao.isVisibleTo(permissions)) {
//                    if (caoStats.get(cao.getStatus()) == null)
//                        caoStats.put(cao.getStatus(), 1);
//                    else
//                        caoStats.put(cao.getStatus(), caoStats.get(cao.getStatus()) + 1);
//                }
//            }
//        }
//        return caoStats;
//    }
//
//    @Transient
//    public Integer getIndependentClosingAuditor(User u) {
//        if (u != null) {
//            if (u.getId() == 10600) // Mike Casey
//                return 38048; // Chad Frost
//            else if (u.getId() == 910) // Dennis Dooley
//                return 38050; // George Megress
//            else if (u.getId() == 902) // John McCaughey
//                return 1029; // Mina Mina
//            else
//                return u.getId();
//        }
//        return null;
//    }
//
//    @Override
//    @Transient
//    public boolean isNeedsIndexing() {
//        return needsIndexing;
//    }
//
//    @Override
//    @Transient
//    public void setNeedsIndexing(boolean needsIndexing) {
//        this.needsIndexing = needsIndexing;
//    }
//
//    @Transient
//    public String getReturnType() {
//        return "audit";
//    }
//
//    @Transient
//    public String getSearchText() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(this.getReturnType()).append('|').append("Audit");
//        sb.append('|').append(this.id).append('|').append(this.auditType.name);
//        if (auditFor != null) {
//            sb.append(" ").append(this.auditFor);
//        }
//        sb.append('|').append(this.contractorAccount.name).append('|');
//
//        if (isExpired()) {
//            sb.append("expired");
//        } else {
//            sb.append("effective");
//        }
//
//        sb.append("\n");
//        return sb.toString();
//    }
//
//    @Transient
//    public String getViewLink() {
//        return "Audit.action?auditID=" + this.id;
//    }
//
//    @Transient
//    public boolean willExpireWithinTwoWeeks() {
//        if (this.getExpiresDate() == null)
//            return false;
//
//        Calendar twoWeeksFromNow = Calendar.getInstance();
//        twoWeeksFromNow.add(Calendar.WEEK_OF_YEAR, 2);
//
//        return getExpiresDate().before(twoWeeksFromNow.getTime()) && getExpiresDate().after(new Date());
//    }
//
//    @Transient
//    public boolean expiredUpToAWeekAgo() {
//        if (this.getExpiresDate() == null)
//            return false;
//
//        Calendar oneWeekAgo = Calendar.getInstance();
//        oneWeekAgo.add(Calendar.WEEK_OF_YEAR, -1);
//
//        return getExpiresDate().before(new Date()) && getExpiresDate().after(oneWeekAgo.getTime());
//    }
//
//    @Transient
//    public boolean isExpiringSoon() {
//        return (willExpireWithinTwoWeeks() || expiredUpToAWeekAgo());
//    }
//
//    @Transient
//    public boolean isExpiringRenewableAudit() {
//        return getAuditType().isRenewable() && isExpiringSoon();
//    }
//
//    @Transient
//    public boolean isRemoved() {
//        return false;
//    }
//
//    @Transient
//    public boolean isHasOpenRequirements() {
//        if (getAuditType().getWorkFlow().isHasRequirements() && hasCaoStatus(AuditStatus.Submitted)) {
//            return true;
//        }
//
//        return false;
//    }
//
//    @Transient
//    public boolean pqfIsOkayToChangeCaoStatus(ContractorAuditOperator cao) {
//        if (auditType.isPicsPqf() && cao.getPercentVerified() == 100) {
//            for (AuditData data : getData()) {
//                if (data.getQuestion().getId() == AuditQuestion.MANUAL_PQF && data.isUnverified()) {
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//
//    @Transient
//    public boolean isDataExpectedAnswer(int questionId, String expected) {
//        for (AuditData answer : data) {
//            if (answer.getQuestion().getId() == questionId) {
//                return Strings.isEqualNullSafe(answer.getAnswer(), expected);
//            }
//        }
//        return false;
//    }
//
//    public static ContractorAuditBuilder builder() {
//        return new ContractorAuditBuilder();
//    }
//
//    @Transient
//    public int getAuditYear() {
//        int year = 0;
//        if (getAuditFor() != null) {
//            try {
//                year = Integer.parseInt(getAuditFor());
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        return year;
//    }
}
