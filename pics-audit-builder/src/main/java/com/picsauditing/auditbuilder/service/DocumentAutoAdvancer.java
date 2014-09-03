package com.picsauditing.auditbuilder.service;

import com.picsauditing.auditbuilder.entities.*;

public class DocumentAutoAdvancer {
    public static ContractorDocumentOperatorWorkflow advanceCaoStatus(ContractorDocument conAudit, ContractorDocumentOperator cao, int percentComplete, int percentVerified) {
        ContractorDocumentOperator caoWithStatus = null;
        ContractorDocumentOperatorWorkflow caoW = null;
        if (cao.getStatus().isPending()) {
            if (conAudit.getAuditType().getId() == AuditType.PQF && percentComplete == 100) {
                if (percentVerified == 100)
                    caoWithStatus = findCaoWithStatus(conAudit, DocumentStatus.Complete);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, DocumentStatus.Submitted);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, DocumentStatus.Resubmitted);
            } else if (conAudit.getAuditType().getId() == AuditType.MANUAL_AUDIT) {
                caoWithStatus = findCaoWithStatus(conAudit, DocumentStatus.Complete);
                if (caoWithStatus == null)
                    caoWithStatus = findCaoWithStatus(conAudit, DocumentStatus.Submitted);
            }
        }

        if (caoWithStatus != null) {
            caoW = new ContractorDocumentOperatorWorkflow();

            caoW.setCao(cao);
            // TODO: I18N
            caoW.setNotes("Advancing status because 100 percent complete.");
            caoW.setPreviousStatus(cao.getStatus());
            caoW.setAuditColumns(new User(User.SYSTEM));
            caoW.setStatus(caoWithStatus.getStatus());
            cao.getCaoWorkflow().add(caoW);

            DocumentUtilityService.changeStatus(cao, caoWithStatus.getStatus());
        }
        return caoW;
    }

    private static ContractorDocumentOperator findCaoWithStatus(ContractorDocument conAudit, DocumentStatus documentStatus) {
        for (ContractorDocumentOperator cao : conAudit.getOperators()) {
            if (cao.getStatus().equals(documentStatus))
                return cao;
        }
        return null;
    }
}