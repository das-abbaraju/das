package com.picsauditing.actions.auditType;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

public class ManageQuestion extends ManageSubCategory {

	protected AuditQuestionDAO auditQuestionDao;

	public ManageQuestion(AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao,
			AuditSubCategoryDAO auditSubCategoryDao,
			AuditQuestionDAO auditQuestionDao) {
		super(auditTypeDao, auditCategoryDao, auditSubCategoryDao);
		this.auditQuestionDao = auditQuestionDao;
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load(auditQuestionDao.find(id));
		}
	}

	@Override
	protected void loadParent(int id) {
		super.load(id);
	}
	
	protected void load(AuditQuestion o) {
		this.question = o;
		load(question.getSubCategory());
	}
	
	public void save() {
		if (question != null) {
			question.setLastModified(new java.util.Date());
			question.setDateCreated(new java.util.Date());
			question = auditQuestionDao.save(question);
			id = question.getSubCategory().getId();
		}
	}
	
	protected void delete() {
		try {
			// TODO check to see if AuditData exists for this question first
//			if (subCategory.getQuestions().size() > 0) {
//				message = "Can't delete - Questions still exist";
//				return;
//			}
			
			id = question.getSubCategory().getId();
			auditQuestionDao.remove(question.getQuestionID());
		} catch (Exception e) {
			message = "Error - " + e.getMessage();
		}
	}	

	public String[] getQuestionTypes() {
		return AuditQuestion.TYPE_ARRAY;
	}
	
}
