package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorOperator")
@Table(name = "contractor_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorOperator extends BaseTable implements java.io.Serializable {
    private OperatorAccount operatorAccount;
    private ContractorAccount contractorAccount;
    private FlagColor flagColor;
    private FlagColor baselineFlag;
    private FlagColor forceFlag;
    private Date flagLastUpdated;
    private Date forceEnd;
    private Date forceBegin;
    private Set<FlagData> flagDatas = new HashSet<>();
    private String flagDetail;
    private String baselineFlagDetail;

    @ManyToOne
    @JoinColumn(name = "opID", nullable = false, updatable = false)
    public OperatorAccount getOperatorAccount() {
        return operatorAccount;
    }

    public void setOperatorAccount(OperatorAccount operator) {
        this.operatorAccount = operator;
    }

    @ManyToOne
    @JoinColumn(name = "conID", nullable = false, updatable = false)
    public ContractorAccount getContractorAccount() {
        return contractorAccount;
    }

    public void setContractorAccount(ContractorAccount contractor) {
        this.contractorAccount = contractor;
    }

    @Enumerated(EnumType.STRING)
    public FlagColor getBaselineFlag() {
        return baselineFlag;
    }

    public void setBaselineFlag(FlagColor baselineFlag) {
        this.baselineFlag = baselineFlag;
    }

    @Enumerated(EnumType.STRING)
    public FlagColor getForceFlag() {
        return forceFlag;
    }

    public void setForceFlag(FlagColor forceFlag) {
        this.forceFlag = forceFlag;
    }

    @Temporal(TemporalType.DATE)
    public Date getForceEnd() {
        return forceEnd;
    }

    public void setForceEnd(Date forceEnd) {
        this.forceEnd = forceEnd;
    }

    public Date getForceBegin() {
        return forceBegin;
    }

    public void setForceBegin(Date forceBegin) {
        this.forceBegin = forceBegin;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "flag", nullable = false)
    public FlagColor getFlagColor() {
        return flagColor;
    }

    public void setFlagColor(FlagColor flagColor) {
        this.flagColor = flagColor;
    }

    public Date getFlagLastUpdated() {
        return flagLastUpdated;
    }

    public void setFlagLastUpdated(Date flagLastUpdated) {
        this.flagLastUpdated = flagLastUpdated;
    }

    public String getFlagDetail() {
        return flagDetail;
    }

    public void setFlagDetail(String flagDetail) {
        this.flagDetail = flagDetail;
    }

    public String getBaselineFlagDetail() {
        return baselineFlagDetail;
    }

    public void setBaselineFlagDetail(String baselineFlagDetail) {
        this.baselineFlagDetail = baselineFlagDetail;
    }

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "contractorOperator")
    public Set<FlagData> getFlagDatas() {
        return flagDatas;
    }

    public void setFlagDatas(Set<FlagData> flagDatas) {
        this.flagDatas = flagDatas;
    }
}