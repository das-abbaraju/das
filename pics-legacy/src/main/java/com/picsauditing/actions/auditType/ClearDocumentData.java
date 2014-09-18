package com.picsauditing.actions.auditType;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SuppressWarnings("serial")
public class ClearDocumentData extends PicsActionSupport {

    @Autowired
    AuditTypeDAO auditTypeDAO;
    @Autowired
    AuditQuestionDAO auditQuestionDAO;

    private String slug;

	@Override
	public String execute() throws Exception {
        permissions.tryPermission(OpPerms.ManageAudits);
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);
		return SUCCESS;
	}

    public String clear() throws Exception {
        permissions.tryPermission(OpPerms.ManageAudits);
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);

        List<AuditType> auditTypes = auditTypeDAO.findBySlug(AuditType.class, slug);

        for (AuditType auditType : auditTypes) {
            deleteAllData(auditType);

            auditTypeDAO.deleteData(AuditCategoryRule.class, "auditType.id = " + auditType.getId());
            auditTypeDAO.deleteData(AuditTypeRule.class, "auditType.id = " + auditType.getId());

            auditTypeDAO.remove(auditType);
            addActionMessage("Succeeded to delete");
        }

        return SUCCESS;
    }

    private void deleteAllData(AuditType auditType) {
        for (AuditCategory category : auditType.getCategories()) {
            deleteAllCategoryData(category);

            auditTypeDAO.deleteData(AuditCategoryRule.class, "auditCategory.id = " + category.getId());
            auditTypeDAO.deleteData(AuditTypeRule.class, "auditCategory.id = " + category.getId());

            auditTypeDAO.remove(category);
        }
        auditType.getCategories().clear();
    }

    private void deleteAllCategoryData(AuditCategory auditCategory) {
        for (AuditCategory category : auditCategory.getSubCategories()) {
            deleteAllCategoryData(category);

            auditTypeDAO.deleteData(AuditCategoryRule.class, "auditCategory.id = " + category.getId());
            auditTypeDAO.deleteData(AuditTypeRule.class, "auditCategory.id = " + category.getId());

            auditTypeDAO.remove(category);
        }

        for (AuditQuestion auditQuestion : auditCategory.getQuestions()) {
            auditTypeDAO.deleteData(AuditCategoryRule.class, "question.id = " + auditQuestion.getId());
            auditTypeDAO.deleteData(AuditTypeRule.class, "question.id = " + auditQuestion.getId());
            AuditExtractOption extractOption = auditQuestionDAO.findAuditExtractOptionByQuestionId(auditQuestion.getId());

            if (extractOption != null) {
                dao.remove(extractOption);
            }

            auditTypeDAO.remove(auditQuestion);
        }
        auditCategory.getQuestions().clear();
        auditCategory.getSubCategories().clear();
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}