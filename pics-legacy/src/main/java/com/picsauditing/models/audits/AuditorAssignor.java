package com.picsauditing.models.audits;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.*;

public class AuditorAssignor {
    public static void assignClosingAuditor(User closingAuditor, ContractorAudit audit, Note note, Permissions permissions) {
        if (closingAuditor != null) {
            note.setSummary("Assigned " + closingAuditor.getName() + " as Closing Auditor for "
                    + audit.getAuditType().getName().toString());
        } else {
            note.setSummary("Unassigned closing auditor for " + audit.getAuditType().getName().toString());
        }

        audit.setAuditColumns(permissions);

        note.setAccount(audit.getContractorAccount());
        note.setAuditColumns(permissions);
        note.setNoteCategory(NoteCategory.Audits);
        if (audit.getAuditType().getAccount() != null) {
            note.setViewableBy(audit.getAuditType().getAccount());
        }
        else {
            note.setViewableById(Account.EVERYONE);
        }

        audit.setClosingAuditor(closingAuditor);
    }
}
