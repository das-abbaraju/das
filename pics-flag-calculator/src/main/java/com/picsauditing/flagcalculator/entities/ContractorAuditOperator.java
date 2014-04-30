package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorAuditOperator")
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable /*implements Comparable<ContractorAuditOperator>*/ {

    private ContractorAudit audit;
    private OperatorAccount operator;
    private AuditStatus status = AuditStatus.Pending;
//    private Date statusChangedDate;
//    private int percentComplete;
//    private int percentVerified;
    private boolean visible = true;
//    private FlagColor flag = null;
    private List<ContractorAuditOperatorPermission> caoPermissions = new ArrayList<ContractorAuditOperatorPermission>();
//    private List<ContractorAuditOperatorWorkflow> caoWorkflow = new ArrayList<ContractorAuditOperatorWorkflow>();
//    private AuditSubStatus auditSubStatus;

    @ManyToOne
    @JoinColumn(name = "auditID", nullable = false, updatable = false)
    public ContractorAudit getAudit() {
        return audit;
    }

    public void setAudit(ContractorAudit audit) {
        this.audit = audit;
    }

//    /**
//     * @return The "inherited" operator/corporate/division that is associated
//     *         with this CAO
//     */
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

//    @Transient
//    public ContractorAuditOperatorWorkflow changeStatus(AuditStatus auditStatus, Permissions permissions) {
//        if (auditStatus.equals(status))
//            return null;
//
//        ContractorAuditOperatorWorkflow caow = new ContractorAuditOperatorWorkflow();
//        caow.setCao(this);
//        caow.setPreviousStatus(status);
//        caow.setStatus(auditStatus);
//        caow.setAuditColumns(permissions);
//
//        setAuditColumns(permissions);
//        setStatusChangedDate(new Date());
//        this.status = auditStatus;
//
//        if (status != AuditStatus.Incomplete) {
//            auditSubStatus = null;
//        }
//
//        if (audit.getAuditType().isPicsPqf() || audit.getAuditType().isAnnualAddendum())
//            return caow;
//
//        if (auditStatus.isPending())
//            return caow;
//
//        if (audit.getEffectiveDate() == null)
//            audit.setEffectiveDate(new Date());
//
//        return caow;
//    }
//
//    @Transient
//    public Date getEffectiveDate() {
//        if (status.isPending() && audit.getAuditor() != null)
//            return audit.getAssignedDate();
//
//        return statusChangedDate;
//    }
//
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

//    @Transient
//    public boolean isTopCaowUserNote() {
//        if (CollectionUtils.isNotEmpty(caoWorkflow)) {
//            List<ContractorAuditOperatorWorkflow> sortedCoaws = new ArrayList<ContractorAuditOperatorWorkflow>(caoWorkflow);
//            Collections.sort(sortedCoaws, new Comparator<ContractorAuditOperatorWorkflow>() {
//                @Override
//                public int compare(ContractorAuditOperatorWorkflow caow1,
//                                   ContractorAuditOperatorWorkflow coaw2) {
//                    if (caow1 != null && caow1.getCreationDate().before(coaw2.getCreationDate()))
//                        return 1;
//                    else if (caow1 != null && caow1.getCreationDate().after(coaw2.getCreationDate()))
//                        return -1;
//
//                    return 0;
//                }
//            });
//
//            ContractorAuditOperatorWorkflow caow = sortedCoaws.get(0);
//            if (caow.getStatus().equals(caow.getPreviousStatus()) && !Strings.isEmpty(caow.getNotes())) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    @Enumerated(EnumType.STRING)
//    public FlagColor getFlag() {
//        return flag;
//    }
//
//    public void setFlag(FlagColor flag) {
//        this.flag = flag;
//    }
//
//    @ReportField(type = FieldType.DateTime)
//    public Date getStatusChangedDate() {
//        return statusChangedDate;
//    }
//
//    public void setStatusChangedDate(Date statusChangedDate) {
//        this.statusChangedDate = statusChangedDate;
//    }
//
//    @ReportField(type = FieldType.Integer)
//    public int getPercentComplete() {
//        return percentComplete;
//    }
//
//    public void setPercentComplete(int percentComplete) {
//        this.percentComplete = percentComplete;
//    }
//
//    @ReportField(type = FieldType.Integer)
//    public int getPercentVerified() {
//        return percentVerified;
//    }
//
//    public void setPercentVerified(int percentVerified) {
//        this.percentVerified = percentVerified;
//    }
//
//    @Transient
//    public int getPercent() {
//        if (status.isPending())
//            return this.percentComplete;
//
//        if (status.isSubmittedResubmitted())
//            return this.percentVerified;
//
//        return 100;
//    }
//
    @OneToMany(mappedBy = "cao", cascade = { CascadeType.ALL })
    public List<ContractorAuditOperatorPermission> getCaoPermissions() {
        return caoPermissions;
    }

    public void setCaoPermissions(List<ContractorAuditOperatorPermission> caoPermissions) {
        this.caoPermissions = caoPermissions;
    }

//    @OneToMany(mappedBy = "cao", cascade = { CascadeType.ALL })
//    public List<ContractorAuditOperatorWorkflow> getCaoWorkflow() {
//        return caoWorkflow;
//    }
//
//    public void setCaoWorkflow(List<ContractorAuditOperatorWorkflow> caoWorkflow) {
//        this.caoWorkflow = caoWorkflow;
//    }
//
//    @Enumerated(EnumType.STRING)
//    @ReportField(type = FieldType.AuditSubStatus)
//    public AuditSubStatus getAuditSubStatus() {
//        return auditSubStatus;
//    }
//
//    public void setAuditSubStatus(AuditSubStatus auditSubStatus) {
//        this.auditSubStatus = auditSubStatus;
//    }
//
//    @Transient
//    public boolean isVisibleTo(Permissions permissions) {
//        if (!this.visible)
//            return false;
//
//        if (permissions.isContractor() || permissions.isPicsEmployee())
//            return true;
//
//        if (operator.getId() == permissions.getAccountId())
//            return true;
//
//        if (permissions.isOperatorCorporate() && !getAudit().getAuditType().isCanOperatorView())
//            return false;
//
//        if (permissions.isCorporate()) {
//            for (ContractorAuditOperatorPermission caop : getCaoPermissions()) {
//                for (Integer ids : permissions.getOperatorChildren()) {
//                    if (caop.getOperator().getId() == ids)
//                        return true;
//                }
//            }
//        }
//
//        return hasCaop(permissions.getAccountId());
//    }
//
//    @Transient
//    public boolean isReadyToBeSubmitted() {
//        return ((this.status == AuditStatus.Pending
//                || this.status == AuditStatus.Incomplete
//                || this.status == AuditStatus.Resubmit)
//                && this.percentComplete == 100);
//    }
//
//    @Transient
//    public boolean hasOnlyCaop(int opID) {
//        if (getCaoPermissions().size() == 1) {
//            return hasCaop(opID);
//        }
//        return false;
//    }
//
//    @Override
//    public int compareTo(ContractorAuditOperator o) {
//        return this.getOperator().getName().compareTo(o.getOperator().getName());
//    }
}