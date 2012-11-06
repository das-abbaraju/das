package com.picsauditing.models.audits;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Strings;

/**
 * This class acts as a service layer for CaoSave.java. All business logic for
 * that action class should be moved into here.
 * 
 */
public class CaoSaveModel {
	@Autowired
	protected AuditPercentCalculator auditPercentCalculator;

	I18nCache i18nCache = I18nCache.getInstance();

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
		String comment = "";

		if (!auditData.isVerified() && !Strings.isEmpty(auditData.getComment())) {
			String commentHeader = "";
			int categoryId = auditData.getQuestion().getCategory().getId();

			if (categoryId == AuditCategory.EMR) {
				commentHeader = "EMR : ";
			} else if (OshaAudit.isSafetyStatisticsCategory(categoryId)) {
				OshaType oshaType = OshaAudit.convertCategoryToOshaType(categoryId);
				commentHeader = i18nCache.getText(oshaType.getI18nKey(), Locale.ENGLISH) + " : ";
			} else {
				commentHeader = "Comment : ";
			}

			comment += commentHeader + auditData.getComment() + "\n";
		}

		return comment;
	}
	
	public void updatePqfOnIncomplete(ContractorAudit audit, AuditStatus newStatus) {
		if (audit.getAuditType().isPqf() && newStatus.isIncomplete()) {
			for (AuditData data:audit.getData()) {
				if (data.getQuestion().getId() == AuditQuestion.MANUAL_PQF) {
					data.setVerified(false);
					data.setAuditor(null);
					auditPercentCalculator.percentCalculateComplete(audit, true);
					break;
				}
			}
		}
	}
	
	public void updatePqfOnSubmittedResubmitter(ContractorAudit audit, AuditStatus newStatus) {
		if (newStatus.isSubmittedResubmitted() && audit.getAuditType().isPqf()) {
			auditPercentCalculator.percentCalculateComplete(audit, true);
		}
	}
}
