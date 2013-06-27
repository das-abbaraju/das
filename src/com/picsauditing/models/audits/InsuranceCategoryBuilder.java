package com.picsauditing.models.audits;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.*;

import java.util.Calendar;
import java.util.Collections;

public class InsuranceCategoryBuilder {
    private static final String QUESTION1 = "Upload a Certificate of Insurance or other supporting documentation for this policy.";
    private static final String QUESTION2 = "This insurance policy complies with all additional ";
    private static final String CERTIFICATE_COLUMN_HEADER = "Certificate";

    public static AuditCategory build(AuditTypeDAO typeDAO, AuditType insuranceType, Permissions permissions, OperatorAccount operator) {
        AuditCategory parent = findParentCategory(insuranceType);

        AuditCategory insuranceCategory = new AuditCategory();
        insuranceCategory.setAuditColumns(permissions);
        insuranceCategory.setName(operator.getName());
        insuranceCategory.setParent(parent);
        insuranceCategory.setAuditType(insuranceType);
        insuranceType.getCategories().add(insuranceCategory);
        sortInsuranceCategories(insuranceType);

        Calendar effectiveDate = Calendar.getInstance();
        effectiveDate.set(2001, Calendar.JANUARY, 1);
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.set(4000, Calendar.JANUARY, 1);

        insuranceCategory.setEffectiveDate(effectiveDate.getTime());
        insuranceCategory.setExpirationDate(expirationDate.getTime());

        AuditQuestion uploadQuestion = new AuditQuestion();
        uploadQuestion.setNumber(1);
        uploadQuestion.setAuditColumns(permissions);
        uploadQuestion.setName(QUESTION1);
        uploadQuestion.setCategory(insuranceCategory);
        uploadQuestion.setQuestionType(AuditQuestion.TYPE_FILE_CERTIFICATE);
        uploadQuestion.setRequired(true);
        uploadQuestion.setEffectiveDate(effectiveDate.getTime());
        uploadQuestion.setExpirationDate(expirationDate.getTime());
        uploadQuestion.setColumnHeader(CERTIFICATE_COLUMN_HEADER);

        AuditQuestion confirmationQuestion = new AuditQuestion();
        confirmationQuestion.setNumber(2);
        confirmationQuestion.setAuditColumns(permissions);
        confirmationQuestion.setName(QUESTION2 + operator.getName() + " requirements.");
        confirmationQuestion.setCategory(insuranceCategory);
        confirmationQuestion.setQuestionType("Yes/No");
        confirmationQuestion.setRequired(true);
        confirmationQuestion.setEffectiveDate(effectiveDate.getTime());
        confirmationQuestion.setExpirationDate(expirationDate.getTime());
        confirmationQuestion.setColumnHeader(CERTIFICATE_COLUMN_HEADER);
        typeDAO.save(insuranceCategory);
        typeDAO.save(insuranceType);
        typeDAO.save(uploadQuestion);
        typeDAO.save(confirmationQuestion);

        AuditCategoryRule rule = new AuditCategoryRule();
        rule.setAuditType(insuranceType);
        rule.setAuditCategory(insuranceCategory);
        rule.setRootCategory(false);
        rule.setOperatorAccount(operator);
        rule.setAuditColumns(permissions);
        rule.setEffectiveDate(Calendar.getInstance().getTime());
        rule.setExpirationDate(BaseHistory.END_OF_TIME);
        rule.calculatePriority();
        typeDAO.save(rule);

        return insuranceCategory;
    }

    private static AuditCategory findParentCategory(AuditType insuranceType) {
        AuditCategory parent = null;
        for (AuditCategory topLevelCategory : insuranceType.getTopCategories()) {
            if (topLevelCategory.getName().toString().startsWith(insuranceType.getName().toString())) {
                parent = topLevelCategory;
                break;
            }
        }
        return parent;
    }

    public static void sortInsuranceCategories(AuditType auditType) {
        Collections.sort(auditType.getCategories(), AuditCategory.INSURANCE_POLICY_COMPARATOR);
        int num = 1;
        for (AuditCategory currentCategory : auditType.getCategories()) {
            if (currentCategory.getParent() != null) {
                currentCategory.setNumber(num);
                num++;
            }
        }
    }
}
