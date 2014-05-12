package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorAudit")
@Table(name = "contractor_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorAudit extends BaseTable {
    private AuditType auditType;
    private ContractorAccount contractorAccount;
    private Date expiresDate;
    private int score;
    private String auditFor;

    private List<AuditCatData> categories = new ArrayList<>();
    private List<AuditData> data = new ArrayList<>();
    private List<ContractorAuditOperator> operators = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "auditTypeID")
    public AuditType getAuditType() {
        return auditType;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

    @ManyToOne
    @JoinColumn(name = "conID")
    public ContractorAccount getContractorAccount() {
        return contractorAccount;
    }

    public void setContractorAccount(ContractorAccount contractor) {
        this.contractorAccount = contractor;
    }

    @OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
    public List<ContractorAuditOperator> getOperators() {
        return operators;
    }

    public void setOperators(List<ContractorAuditOperator> operators) {
        this.operators = operators;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }

    /**
     * We may need to move this over to CAO someday
     *
     * @return
     */
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Child tables

    @OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
    public List<AuditCatData> getCategories() {
		for (AuditCatData auditCatData : categories) {
			if (!auditCatData.getCategory().getAuditType().equals(auditType)) {
			}
		}
        return categories;
    }

    public void setCategories(List<AuditCatData> categories) {
        this.categories = categories;
    }

    @OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
    public List<AuditData> getData() {
        return data;
    }

    public void setData(List<AuditData> data) {
        this.data = data;
    }

    /**
     * Who, what, or when is this audit for? Examples: OSHA/EMR for "2005" IM
     * for "John Doe"
     *
     * @return
     */
    public String getAuditFor() {
        return auditFor;
    }

    public void setAuditFor(String auditFor) {
        this.auditFor = auditFor;
    }
}