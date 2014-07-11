package com.picsauditing.auditbuilder.service;

import com.picsauditing.auditbuilder.entities.*;

public class CaoAutoAdvancer {
    public static ContractorAuditOperatorWorkflow advanceCaoStatus(ContractorAudit conAudit, ContractorAuditOperator cao, int percentComplete, int percentVerified) {
        ContractorAuditOperator caoWithStatus = null;
        ContractorAuditOperatorWorkflow caoW = null;
        if (cao.getStatus().isPending()) {
            if (conAudit.getAuditType().getId() == AuditType.PQF && percentComplete == 100) {
                if (percentVerified == 100)
                    caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Complete);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Submitted);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Resubmitted);
            } else if (conAudit.getAuditType().getId() == AuditType.MANUAL_AUDIT) {
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

            AuditService.changeStatus(cao, caoWithStatus.getStatus());
        }
        return caoW;
    }

    private static ContractorAuditOperator findCaoWithStatus(ContractorAudit conAudit, AuditStatus auditStatus) {
        for (ContractorAuditOperator cao : conAudit.getOperators()) {
            if (cao.getStatus().equals(auditStatus))
                return cao;
        }
        return null;
    }
}