package com.picsauditing.model.entities;

@SuppressWarnings("serial")
//@Entity
//@Table(name = "contractor_operator")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorOperator extends BaseTable implements java.io.Serializable {
    private OperatorAccount operatorAccount;
    private ContractorAccount contractorAccount;
//    private ApprovalStatus workStatus = ApprovalStatus.P;
//    private FlagColor flagColor;
//    private FlagColor baselineFlag;
//    private Integer baselineApprover;
//    private Date baselineApproved;
//    private FlagColor forceFlag;
//    private Date flagLastUpdated;
//    private Date forceEnd;
//    private Date forceBegin;
//    private User forcedBy;
//    private WaitingOn waitingOn = WaitingOn.None;
//    private Date processCompletion;
//    private String relationshipType;
//    private Set<FlagData> flagDatas = new HashSet<FlagData>();
//    private Set<FlagDataOverride> overrides = new HashSet<FlagDataOverride>();
//    private String flagDetail;
//    private String baselineFlagDetail;
//    private Date lastStepToGreenDate;
//    // Registration Requests
//    private User requestedBy;
//    private String requestedByOther;
//    private Date deadline; // TODO Rename as registrationDeadline
//    private String reasonForRegistration;
//
//    @ManyToOne
//    @JoinColumn(name = "opID", nullable = false, updatable = false)
    public OperatorAccount getOperatorAccount() {
        return operatorAccount;
    }

    public void setOperatorAccount(OperatorAccount operator) {
        this.operatorAccount = operator;
    }

//    @ManyToOne
//    @JoinColumn(name = "conID", nullable = false, updatable = false)
    public ContractorAccount getContractorAccount() {
        return contractorAccount;
    }

    public void setContractorAccount(ContractorAccount contractor) {
        this.contractorAccount = contractor;
    }

//    /**
//     * Default to P and then be status or rejected
//     *
//     * @return P=Pending, Y=Yes, N=No
//     */
//    @Enumerated(EnumType.STRING)
//    @ReportField(type = FieldType.ApprovalStatus, importance = FieldImportance.Average)
//    public ApprovalStatus getWorkStatus() {
//        return workStatus;
//    }
//
//    public void setWorkStatus(ApprovalStatus workStatus) {
//        this.workStatus = workStatus;
//    }
//
//    public void setForcedWorkStatus(ApprovalStatus workStatus) {
//        if (workStatus.isYes())
//            this.workStatus = ApprovalStatus.YF;
//        else if (workStatus.isNo())
//            this.workStatus = ApprovalStatus.NF;
//        else
//            setWorkStatus(workStatus);
//    }
//
//    @Enumerated(EnumType.STRING)
//    @ReportField(type = FieldType.FlagColor, importance = FieldImportance.Average)
//    public FlagColor getBaselineFlag() {
//        return baselineFlag;
//    }
//
//    public void setBaselineFlag(FlagColor baselineFlag) {
//        this.baselineFlag = baselineFlag;
//    }
//
//    public Integer getBaselineApprover() {
//        return baselineApprover;
//    }
//
//    public void setBaselineApprover(Integer baselineApprover) {
//        this.baselineApprover = baselineApprover;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    public Date getBaselineApproved() {
//        return baselineApproved;
//    }
//
//    public void setBaselineApproved(Date baselineApproved) {
//        this.baselineApproved = baselineApproved;
//    }
//
//    public void resetBaseline(Permissions permissions) {
//        baselineFlag = flagColor;
//        baselineFlagDetail = flagDetail;
//        baselineApproved = new Date();
//        if (permissions != null && permissions.getUserId() > 0)
//            baselineApprover = permissions.getUserId();
//        else
//            baselineApprover = User.SYSTEM;
//
//        for (FlagData fd : this.getFlagDatas())
//            fd.resetBaseline();
//    }
//
//    @Enumerated(EnumType.STRING)
//    @ReportField(type = FieldType.FlagColor)
//    public FlagColor getForceFlag() {
//        return forceFlag;
//    }
//
//    public void setForceFlag(FlagColor forceFlag) {
//        this.forceFlag = forceFlag;
//    }
//
//    @Temporal(TemporalType.DATE)
//    @ReportField(type = FieldType.Date, importance = FieldImportance.Average)
//    public Date getForceEnd() {
//        return forceEnd;
//    }
//
//    public void setForceEnd(Date forceEnd) {
//        this.forceEnd = forceEnd;
//    }
//
//    @ReportField(type = FieldType.Date)
//    public Date getForceBegin() {
//        return forceBegin;
//    }
//
//    public void setForceBegin(Date forceBegin) {
//        this.forceBegin = forceBegin;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "forcedBy")
//    public User getForcedBy() {
//        return forcedBy;
//    }
//
//    public void setForcedBy(User forcedBy) {
//        this.forcedBy = forcedBy;
//    }
//
//    @Temporal(TemporalType.DATE)
//    @ReportField(type = FieldType.Date)
//    public Date getProcessCompletion() {
//        return processCompletion;
//    }
//
//    public void setProcessCompletion(Date processCompletion) {
//        this.processCompletion = processCompletion;
//    }
//
//    @Transient
//    public boolean isForcedFlag() {
//        if (forceFlag == null || forceEnd == null) {
//            return false;
//        }
//
//        // We have a forced flag, but make sure it's still in effect
//        if (forceEnd.before(new Date())) {
//            return false;
//        }
//        return true;
//    }
//
//    @Transient
//    public void removeForceFlag() {
//        forceEnd = null;
//        forceFlag = null;
//        forceBegin = null;
//        forcedBy = null;
//    }
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "flag", nullable = false)
//    @ReportField(type = FieldType.FlagColor, i18nKeyPrefix = "FlagColor", importance = FieldImportance.Required)
//    public FlagColor getFlagColor() {
//        return flagColor;
//    }
//
//    public void setFlagColor(FlagColor flagColor) {
//        this.flagColor = flagColor;
//    }
//
//    @ReportField(type = FieldType.Date, importance = FieldImportance.Required)
//    public Date getFlagLastUpdated() {
//        return flagLastUpdated;
//    }
//
//    public void setFlagLastUpdated(Date flagLastUpdated) {
//        this.flagLastUpdated = flagLastUpdated;
//    }
//
//    @Enumerated(EnumType.ORDINAL)
//    @Column(name = "waitingOn", nullable = false)
//    @ReportField(type = FieldType.WaitingOn, i18nKeyPrefix = "WaitingOn", importance = FieldImportance.Average)
//    public WaitingOn getWaitingOn() {
//        return waitingOn;
//    }
//
//    public void setWaitingOn(WaitingOn waitingOn) {
//        this.waitingOn = waitingOn;
//    }
//
//    public String getRelationshipType() {
//        return relationshipType;
//    }
//
//    public void setRelationshipType(String relationshipType) {
//        this.relationshipType = relationshipType;
//    }
//
//    public String getFlagDetail() {
//        return flagDetail;
//    }
//
//    public void setFlagDetail(String flagDetail) {
//        this.flagDetail = flagDetail;
//    }
//
//    public String getBaselineFlagDetail() {
//        return baselineFlagDetail;
//    }
//
//    public void setBaselineFlagDetail(String baselineFlagDetail) {
//        this.baselineFlagDetail = baselineFlagDetail;
//    }
//
//    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "contractorOperator")
//    public Set<FlagData> getFlagDatas() {
//        return flagDatas;
//    }
//
//    public void setFlagDatas(Set<FlagData> flagDatas) {
//        this.flagDatas = flagDatas;
//    }
//
//    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "contractorOperator")
//    public Set<FlagDataOverride> getOverrides() {
//        return overrides;
//    }
//
//    public void setOverrides(Set<FlagDataOverride> overrides) {
//        this.overrides = overrides;
//    }
//
//    @Transient
//    public ContractorOperator getForceOverallFlag() {
//        if (isForcedFlag())
//            return this;
//        if (getOperatorAccount().getCorporateFacilities().size() > 0) {
//            for (Facility facility : getOperatorAccount().getCorporateFacilities()) {
//                for (ContractorOperator conOper : contractorAccount.getOperators()) {
//                    if (facility.getCorporate().equals(conOper.getOperatorAccount()) && conOper.isForcedFlag())
//                        return conOper;
//                }
//            }
//        }
//        return null;
//    }
//
//    @ReportField(type = FieldType.Date)
//    public Date getLastStepToGreenDate() {
//        return lastStepToGreenDate;
//    }
//
//    public void setLastStepToGreenDate(Date lastStepToGreenDate) {
//        this.lastStepToGreenDate = lastStepToGreenDate;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "requestedByUserID", nullable = true)
//    public User getRequestedBy() {
//        return requestedBy;
//    }
//
//    public void setRequestedBy(User requestedBy) {
//        this.requestedBy = requestedBy;
//    }
//
//    @Column(name = "requestedByUser", nullable = true)
//    public String getRequestedByOther() {
//        return requestedByOther;
//    }
//
//    public void setRequestedByOther(String requestedByOther) {
//        this.requestedByOther = requestedByOther;
//    }
//
//    @Transient
//    public String getRequestedByName() {
//        if (getRequestedBy() != null) {
//            return getRequestedBy().getName();
//        }
//
//        if (!Strings.isEmpty(getRequestedByOther())) {
//            return getRequestedByOther();
//        }
//
//        return null;
//    }
//
//    public Date getDeadline() {
//        return deadline;
//    }
//
//    public void setDeadline(Date deadline) {
//        this.deadline = deadline;
//    }
//
//    public String getReasonForRegistration() {
//        return reasonForRegistration;
//    }
//
//    public void setReasonForRegistration(String reasonForRegistration) {
//        this.reasonForRegistration = reasonForRegistration;
//    }
//
//    @Transient
//    public void setDefaultWorkStatus() {
//        if (operatorAccount.getParent() != null) {
//            ContractorOperator contractorCorporate = contractorAccount.getContractorOperatorForOperator(operatorAccount.getParent());
//            if (contractorCorporate != null) {
//                if (contractorCorporate.getWorkStatus().isNoForced())
//                    workStatus = ApprovalStatus.N;
//                if (contractorCorporate.getWorkStatus().isYesForced())
//                    workStatus = ApprovalStatus.Y;
//            }
//        }
//    }
//
//    public static ContractorOperatorBuilder builder() {
//        return new ContractorOperatorBuilder();
//    }
//
//    public List<ContractorOperator> getChildOperatorAccountsWithStatus(ContractorAccount contractorAccount, ApprovalStatus... status) {
//        List<ContractorOperator> found = new ArrayList<>();
//        List<ApprovalStatus> statuses = Arrays.asList(status);
//        for (OperatorAccount o : this.getOperatorAccount().getChildOperators()) {
//            for (ContractorOperator co : o.getContractorOperators()) {
//                if (co.getContractorAccount().equals(contractorAccount) && statuses.contains(co.getWorkStatus())) {
//                    found.add(co);
//                }
//            }
//        }
//
//        return found;
//    }
//
//    public boolean isWorkingStatus(ApprovalStatus status) {
//        return getWorkStatus() == status;
//    }
}
