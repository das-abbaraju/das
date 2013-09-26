package com.picsauditing.service.audit;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.audits.CaoSave;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.*;

public class CaoAutoAdvancer {
    public static ContractorAuditOperatorWorkflow advanceAlreadyVerifiedPqfCao(AuditStatus newStatus, ContractorAuditOperator cao, Permissions permissions) {
        ContractorAuditOperatorWorkflow caow = null;
        if (newStatus.isSubmittedResubmitted() && cao.getAudit().pqfIsOkayToChangeCaoStatus(cao)) {
            caow = cao.changeStatus(AuditStatus.Complete, permissions);
            if (caow != null) {
                caow.setNotes("Auto completed based previously completed verification");
                caow.setAuditColumns(new User(User.SYSTEM));
                cao.getCaoWorkflow().add(caow);
            }
        }

        return caow;
    }

    public static ContractorAuditOperatorWorkflow advanceCaoStatus(ContractorAudit conAudit, ContractorAuditOperator cao, int percentComplete, int percentVerified) {
        ContractorAuditOperator caoWithStatus = null;
        ContractorAuditOperatorWorkflow caoW = null;
        if (cao.getStatus().isPending()) {
            if (conAudit.getAuditType().isPicsPqf() && percentComplete == 100) {
                if (percentVerified == 100)
                    caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Complete);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Submitted);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Resubmitted);
            } else if (conAudit.getAuditType().isDesktop()) {
                caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Complete);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Submitted);
            }
        }

        if (caoWithStatus != null) {
            caoW = new ContractorAuditOperatorWorkflow();

            caoW.setCao(cao);
            // TODO: I18N
            caoW.setNotes("Advancing status because 100 percent complete.");
            caoW.setPreviousStatus(cao.getStatus());
            caoW.setAuditColumns(new User(User.SYSTEM));
            caoW.setStatus(caoWithStatus.getStatus());
            cao.getCaoWorkflow().add(caoW);

            cao.changeStatus(caoWithStatus.getStatus(), null);
        }
        return caoW;
    }

    public static void advanceAllCaoStatuses(ContractorAudit conAudit, ContractorAuditOperatorDAO caoDAO){
        for (ContractorAuditOperator cao : conAudit.getOperators()) {
            ContractorAuditOperatorWorkflow caow = CaoAutoAdvancer.advanceCaoStatus(conAudit, cao, cao.getPercentComplete(), cao.getPercentVerified());
            if (caow != null) {
                caoDAO.save(caow);
            }
        }
    }

    private static ContractorAuditOperator findCaoWithStatus(ContractorAudit conAudit, AuditStatus auditStatus) {
        for (ContractorAuditOperator cao : conAudit.getOperators()) {
            if (cao.getStatus().equals(auditStatus))
                return cao;
        }
        return null;
    }
}
