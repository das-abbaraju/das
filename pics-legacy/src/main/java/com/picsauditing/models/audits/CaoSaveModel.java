package com.picsauditing.models.audits;

import java.util.List;
import java.util.Locale;

import com.picsauditing.dao.BaseTableDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.audit.AuditPeriodService;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;

/**
 * This class acts as a service layer for CaoSave.java. All business logic for
 * that action class should be moved into here.
 * 
 */
public class CaoSaveModel {
	@Autowired
	protected AuditPercentCalculator auditPercentCalculator;
    @Autowired
    protected AuditPeriodService auditPeriodService;
    @Autowired
    protected BasicDAO dao;

    private User systemUser = new User(User.SYSTEM);

	TranslationService translationService = TranslationServiceFactory.getTranslationService();

	public String generateNote(List<AuditData> auditDataList) {
		String note = "";
		if (!CollectionUtils.isEmpty(auditDataList)) {

			for (AuditData auditData : auditDataList) {
				note += addAuditDataComment(auditData);
			}
		}
		return note;
	}

	public String addAuditDataComment(AuditData auditData) {
		if (auditData.isVerified() || Strings.isEmpty(auditData.getComment())) {
			return "";
		}

		String commentHeader = "";
		int categoryId = auditData.getQuestion().getCategory().getId();
		OshaType oshaType = OshaAudit.convertCategoryToOshaType(categoryId);

		if (categoryId == AuditCategory.EMR) {
			commentHeader = "EMR : ";
		} else if (OshaAudit.isSafetyStatisticsCategory(categoryId) && oshaType != null) {
			commentHeader = translationService.getText(oshaType.getI18nKey(), Locale.ENGLISH) + " : ";
		} else {
			commentHeader = "Comment : ";
		}

		return commentHeader + auditData.getComment() + "\n";
	}

	public void unverifySafetyManualQuestionInPqf(ContractorAudit audit, AuditStatus newStatus) {
		if (audit.getAuditType().isPicsPqf() && newStatus.isIncomplete()) {
			for (AuditData data : audit.getData()) {
				if (data.getQuestion().getId() == AuditQuestion.MANUAL_PQF) {
					data.setVerified(false);
					data.setAuditor(null);
					break;
				}
			}
		}
	}

    public void updateParentAuditOnCompleteIncomplete(ContractorAudit audit, AuditStatus newStatus) {
        if (!newStatus.isComplete() && !newStatus.isIncomplete())
            return;

        List<ContractorAudit> audits = audit.getContractorAccount().getAudits();
        ContractorAudit temp = audit;

        while (temp != null) {
            AuditType parentAuditType = temp.getAuditType().getParent();
            if (parentAuditType != null) {
                String auditFor = auditPeriodService.getParentAuditFor(parentAuditType, temp.getAuditFor());
                if (auditFor != null) {
                    ContractorAudit parentAudit = auditPeriodService.findAudit(audits, parentAuditType, auditFor);
                    if (parentAudit != null) {
                        auditPercentCalculator.percentCalculateComplete(parentAudit, true);
                        if (newStatus.isIncomplete() && parentAudit.hasCaoStatus(AuditStatus.Complete))
                            moveCompletedCaosToImcomplete(parentAudit);
                        temp = parentAudit;
                    } else
                        break;
                } else
                    break;
            } else
                break;
        }
    }

    private void moveCompletedCaosToImcomplete(ContractorAudit audit) {
        for (ContractorAuditOperator cao:audit.getOperatorsVisible()) {
            if (cao.getStatus().isComplete()) {
                ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Incomplete, null);
                if (caow != null) {
                    caow.setNotes("Moved due to associated audit changed");
                    caow.setAuditColumns(systemUser);
                    dao.save(caow);
                }
            }
        }
    }
}
