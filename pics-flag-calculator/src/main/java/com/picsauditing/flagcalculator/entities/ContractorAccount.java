package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.Table;
import java.util.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorAccount")
@Table(name = "contractor_info")
// Cache is only on the operator account now, if this works.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorAccount extends Account {
    private boolean soleProprietor;
    private AccountLevel accountLevel = AccountLevel.Full;
    private Set<FlagCriteriaContractor> flagCriteria = new HashSet<>();
    private List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
    private List<ContractorOperator> operators = new ArrayList<>();
    private List<ContractorTag> operatorTags = new ArrayList<ContractorTag>();
    private Set<ContractorTrade> trades = new HashSet<>();

    @OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @Where(clause = "expiresDate > NOW() OR expiresDate IS NULL")
    public List<ContractorAudit> getAudits() {
        return this.audits;
    }

    public void setAudits(List<ContractorAudit> audits) {
        this.audits = audits;
    }

    @OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    public List<ContractorOperator> getOperators() {
        return this.operators;
    }

    public void setOperators(List<ContractorOperator> operators) {
        this.operators = operators;
    }

    @OneToMany(mappedBy = "contractor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    public List<ContractorTag> getOperatorTags() {
        return operatorTags;
    }

    public void setOperatorTags(List<ContractorTag> operatorTags) {
        this.operatorTags = operatorTags;
    }

    @OneToMany(mappedBy = "contractor")
    public Set<ContractorTrade> getTrades() {
        return trades;
    }

    public void setTrades(Set<ContractorTrade> trades) {
        this.trades = trades;
    }

    @OneToMany(mappedBy = "contractor", cascade = {CascadeType.ALL})
    public Set<FlagCriteriaContractor> getFlagCriteria() {
        return flagCriteria;
    }

    public void setFlagCriteria(Set<FlagCriteriaContractor> flagCriteria) {
        this.flagCriteria = flagCriteria;
    }

    public boolean getSoleProprietor() {
        return soleProprietor;
    }

    public void setSoleProprietor(boolean soleProprietor) {
        this.soleProprietor = soleProprietor;
    }

    @Enumerated(EnumType.STRING)
    public AccountLevel getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(AccountLevel accountLevel) {
        this.accountLevel = accountLevel;
    }
}
