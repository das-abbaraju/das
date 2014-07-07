package com.picsauditing.service;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AuditService {

	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private NoteDAO noteDAO;

    public void checkSla(ContractorAccount contractor, Permissions permissions) {
        setSlaManualAudit(contractor, permissions);
    }

    private void setSlaManualAudit(ContractorAccount contractor, Permissions permissions) {
        for (ContractorAudit audit : contractor.getAudits()) {
            if (audit.getAuditType().isDesktop() &&
                    audit.hasCaoStatus(AuditStatus.Pending) && audit.getCurrentOperators().size() > 0) {
                Date beginDate = pqfAndSafetyManualSlaStartDate(contractor, audit);
                if (beginDate == null) {
                    if (audit.getSlaDate() != null) {
                        audit.setSlaDate(null);
                        auditDataDAO.save(audit);

                        Note note = new Note();
                        note.setAccount(audit.getContractorAccount());
                        note.setAuditColumns(permissions);
                        note.setSummary("Manual Audit #" + audit.getId() + "'s SLA is no longer valid and has been cleared");
                        note.setNoteCategory(NoteCategory.Audits);
                        note.setViewableById(Account.PicsID);
                        noteDAO.save(note);
                    }
                    return;
                }

                Calendar date = Calendar.getInstance();
                date.setTime(beginDate);
                date.add(Calendar.DATE, 14);
                beginDate = DateBean.setToEndOfDay(date.getTime());

                if (audit.getSlaDate() != null && audit.getSlaDate().after(beginDate))
                    return;

                Date previousManualExpirationDate = previousManualAudit(contractor, audit);
                if (previousManualExpirationDate != null) {
                    date.setTime(previousManualExpirationDate);
                    date.add(Calendar.DATE, -30);
                    previousManualExpirationDate = DateBean.setToEndOfDay(date.getTime());
                }

                if (previousManualExpirationDate != null && previousManualExpirationDate.after(beginDate)) {
                    beginDate = previousManualExpirationDate;
                }

                date = Calendar.getInstance();
                date.add(Calendar.DATE, 14);
                Date minimumDate = DateBean.setToEndOfDay(date.getTime());
                if (minimumDate.after(beginDate)) {
                    beginDate = minimumDate;
                }

                audit.setSlaDate(beginDate);
                auditDataDAO.save(audit);

                Note note = new Note();
                note.setAccount(audit.getContractorAccount());
                note.setAuditColumns(permissions);
                note.setSummary("Manual Audit #" + audit.getId() + " has a new SLA of " + beginDate.toString());
                note.setNoteCategory(NoteCategory.Audits);
                note.setViewableById(Account.PicsID);
                noteDAO.save(note);
            } else if (shouldResetSla(audit)) {
                audit.setSlaDate(null);
                auditDataDAO.save(audit);

                Note note = new Note();
                note.setAccount(audit.getContractorAccount());
                note.setAuditColumns(permissions);
                note.setSummary("Manual Audit #" + audit.getId() + " is not visible to any operators and has had its SLA cleared.");
                note.setNoteCategory(NoteCategory.Audits);
                note.setViewableById(Account.PicsID);
                noteDAO.save(note);
            }
        }
    }

    private Date pqfAndSafetyManualSlaStartDate(ContractorAccount contractor, ContractorAudit manualAudit) {
        boolean pqfRequirementsMet = false;
        boolean safetyManualRequirementsMet = false;
        Date caoDate = null;
        Date safetyManualDate = null;

        for (ContractorAudit audit : contractor.getAudits()) {
            if (audit.getAuditType().isPicsPqf()) {
                caoDate = findMaxCompleteDate(audit, manualAudit);
                if (caoDate != null) {
                    pqfRequirementsMet = true;

                    safetyManualDate = findSafetyManualVerificationDate(audit);
                    if (safetyManualDate != null)
                        safetyManualRequirementsMet = true;
                }
                break;
            }
        }

        if (pqfRequirementsMet && safetyManualRequirementsMet) {
            if (safetyManualDate.after(caoDate))
                return safetyManualDate;
            else
                return caoDate;
        }

        return null;
    }

    private boolean shouldResetSla(ContractorAudit audit) {
        if (!audit.getAuditType().isDesktop())
            return false;
        if (audit.getCurrentOperators().size() == 0)
            return true;

        for (ContractorAuditOperator cao:audit.getOperatorsVisible()) {
            if (cao.getCaoPermissions().size() != 0)
                return false;
        }
        return true;
    }

    private Date previousManualAudit(ContractorAccount contractor, ContractorAudit manualAudit) {
        for (ContractorAudit audit : contractor.getAudits()) {
            if (audit.getAuditType().isDesktop() && audit.getId() != manualAudit.getId() && audit.getExpiresDate() != null) {
                return audit.getExpiresDate();
            }
        }
        return null;
    }

    private Date findSafetyManualVerificationDate(ContractorAudit audit) {
        for (AuditData data : audit.getData()) {
            if (data.getQuestion().getId() == AuditQuestion.MANUAL_PQF) {
                if (data.isAnswered() && data.isVerified()) {
                    return data.getDateVerified();
                }
            }
        }

        return null;
    }

    private Date findMaxCompleteDate(ContractorAudit audit, ContractorAudit manualAudit) {
        Date completeDate = null;

        Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
        for (ContractorAuditOperator cao: manualAudit.getOperators()) {
            if (cao.isVisible()) {
                for (ContractorAuditOperatorPermission caop:cao.getCaoPermissions()) {
                    operators.add(caop.getOperator());
                }
            }
        }

        for (ContractorAuditOperator cao : audit.getOperators()) {
            if (hasMatchingCompletedOperator(cao, operators)) {
                Date checkDate = (cao.getStatusChangedDate() != null) ? cao.getStatusChangedDate() : cao.getUpdateDate();
                if (completeDate == null || (checkDate != null && completeDate.before(checkDate))) {
                    completeDate = checkDate;
                }
            }
        }
        return completeDate;
    }

    private boolean hasMatchingCompletedOperator(ContractorAuditOperator cao, Set<OperatorAccount> operators) {
        if (!cao.isVisible() || !cao.getStatus().equals(AuditStatus.Complete)) {
            return false;
        }

        for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
            if (operators.contains(caop.getOperator())) {
                return true;
            }
        }

        return false;
    }
}
