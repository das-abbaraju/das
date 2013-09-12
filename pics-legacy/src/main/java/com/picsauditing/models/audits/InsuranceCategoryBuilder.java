package com.picsauditing.models.audits;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.i18n.EntityTranslationHelper;

import java.util.Calendar;
import java.util.Collections;

public class InsuranceCategoryBuilder {
    private static final String QUESTION1 = "Upload a Certificate of Insurance or other supporting documentation for this policy.";
    private static final String QUESTION2 = "This insurance policy complies with all additional ";
    private static final String CERTIFICATE_COLUMN_HEADER = "Certificate";

    private AuditTypeDAO typeDAO;
    private AuditType insuranceType;
    private Permissions permissions;
    private OperatorAccount operator;

    private InsuranceCategoryBuilder(){};

    public AuditCategory build(AuditTypeDAO typeDAO,
            AuditType insuranceType,
            Permissions permissions,
            OperatorAccount operator) {

        this.typeDAO = typeDAO;
        this.insuranceType = insuranceType;
        this.permissions = permissions;
        this.operator = operator;

        AuditCategory insuranceCategory = createInsuranceCategory();

        AuditQuestion uploadQuestion = createUploadQuestion(insuranceCategory);

        AuditQuestion confirmationQuestion = createConfirmationQuestion(insuranceCategory);

        AuditCategoryRule rule = createAuditCategoryRule(insuranceCategory);

        save(insuranceCategory, uploadQuestion, confirmationQuestion, rule);

        generateTranslationKeys(insuranceCategory, uploadQuestion, confirmationQuestion);

        return insuranceCategory;
    }

    private void generateTranslationKeys(AuditCategory insuranceCategory, AuditQuestion uploadQuestion, AuditQuestion confirmationQuestion) {
        EntityTranslationHelper.saveRequiredTranslationsForAuditCategory(insuranceCategory, permissions);
        EntityTranslationHelper.saveRequiredTranslationsForAuditQuestion(confirmationQuestion, permissions);
        EntityTranslationHelper.saveRequiredTranslationsForAuditQuestion(uploadQuestion, permissions);
    }

    private void save(AuditCategory insuranceCategory, AuditQuestion uploadQuestion, AuditQuestion confirmationQuestion, AuditCategoryRule rule) {
        typeDAO.save(insuranceCategory);
        typeDAO.save(insuranceType);
        typeDAO.save(uploadQuestion);
        typeDAO.save(confirmationQuestion);
        typeDAO.save(rule);
    }

    private AuditCategoryRule createAuditCategoryRule(AuditCategory insuranceCategory) {
        AuditCategoryRule rule = new AuditCategoryRule();
        rule.setAuditType(insuranceType);
        rule.setAuditCategory(insuranceCategory);
        rule.setRootCategory(false);
        rule.setOperatorAccount(operator);
        rule.setAuditColumns(permissions);
        rule.setEffectiveDate(Calendar.getInstance().getTime());
        rule.setExpirationDate(BaseHistory.END_OF_TIME);
        rule.calculatePriority();
        return rule;
    }


    private AuditQuestion createUploadQuestion(AuditCategory insuranceCategory) {
        AuditQuestion uploadQuestion = new AuditQuestion();
        uploadQuestion.setNumber(1);
        uploadQuestion.setName(QUESTION1);
        uploadQuestion.setQuestionType(AuditQuestion.TYPE_FILE_CERTIFICATE);

        setRestOfQuestionAttributes(insuranceCategory, uploadQuestion);
        return uploadQuestion;
    }

    private AuditQuestion createConfirmationQuestion(AuditCategory insuranceCategory) {
        AuditQuestion confirmationQuestion = new AuditQuestion();
        confirmationQuestion.setNumber(2);
        confirmationQuestion.setName(QUESTION2 + operator.getName() + " requirements.");
        confirmationQuestion.setQuestionType("Yes/No");

        setRestOfQuestionAttributes(insuranceCategory, confirmationQuestion);
        return confirmationQuestion;
    }

    private void setRestOfQuestionAttributes(AuditCategory insuranceCategory, AuditQuestion uploadQuestion) {
        uploadQuestion.setAuditColumns(permissions);
        uploadQuestion.setCategory(insuranceCategory);
        insuranceCategory.getQuestions().add(uploadQuestion);
        uploadQuestion.setRequired(true);
        uploadQuestion.setEffectiveDate(BaseHistory.BEGINING_OF_TIME);
        uploadQuestion.setExpirationDate(BaseHistory.END_OF_TIME);
        uploadQuestion.setColumnHeader(CERTIFICATE_COLUMN_HEADER);
    }

    private AuditCategory createInsuranceCategory() {
        AuditCategory parent = findParentCategory(insuranceType);
        AuditCategory insuranceCategory = new AuditCategory();
        insuranceCategory.setAuditColumns(permissions);
        insuranceCategory.setName(operator.getName());
        insuranceCategory.setParent(parent);
        insuranceCategory.setAuditType(insuranceType);
        insuranceType.getCategories().add(insuranceCategory);
        sortInsuranceCategories();


        insuranceCategory.setEffectiveDate(BaseHistory.BEGINING_OF_TIME);
        insuranceCategory.setExpirationDate(BaseHistory.END_OF_TIME);
        return insuranceCategory;
    }

    private AuditCategory findParentCategory(AuditType insuranceType) {
        AuditCategory parent = null;
        for (AuditCategory topLevelCategory : insuranceType.getTopCategories()) {
            if (topLevelCategory.getName().toString().startsWith(insuranceType.getName().toString())) {
                parent = topLevelCategory;
                break;
            }
        }
        return parent;
    }

    public void sortInsuranceCategories() {
        Collections.sort(insuranceType.getCategories(), AuditCategory.INSURANCE_POLICY_COMPARATOR);
        int num = 1;
        for (AuditCategory currentCategory : insuranceType.getCategories()) {
            if (currentCategory.getParent() != null) {
                currentCategory.setNumber(num);
                num++;
            }
        }
    }

    public static InsuranceCategoryBuilder builder() {
        return new InsuranceCategoryBuilder();
    }
}
