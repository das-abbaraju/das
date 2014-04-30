package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorAuditOperator")
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable {

    private ContractorAudit audit;
    private OperatorAccount operator;
    private AuditStatus status = AuditStatus.Pending;
    private boolean visible = true;
    private List<ContractorAuditOperatorPermission> caoPermissions = new ArrayList<ContractorAuditOperatorPermission>();

    @ManyToOne
    @JoinColumn(name = "auditID", nullable = false, updatable = false)
    public ContractorAudit getAudit() {
        return audit;
    }

    public void setAudit(ContractorAudit audit) {
        this.audit = audit;
    }

    /**
     * @return The "inherited" operator/corporate/division that is associated
     *         with this CAO
     */
    @ManyToOne
    @JoinColumn(name = "opID", nullable = false, updatable = false)
    public OperatorAccount getOperator() {
        return operator;
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public AuditStatus getStatus() {
        return status;
    }

    /**
     * Don't use this! Use changeStatus instead
     *
     * @deprecated
     * @param auditStatus
     * @see ContractorAuditOperator.changeStatus()
     */
    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @OneToMany(mappedBy = "cao", cascade = { CascadeType.ALL })
    public List<ContractorAuditOperatorPermission> getCaoPermissions() {
        return caoPermissions;
    }

    public void setCaoPermissions(List<ContractorAuditOperatorPermission> caoPermissions) {
        this.caoPermissions = caoPermissions;
    }
}