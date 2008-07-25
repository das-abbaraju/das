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
			question = auditQuestionDao.save(question);
			load(question);
		}
	}
	
	protected void delete() {
		try {
//			if (subCategory.getQuestions().size() > 0) {
//				message = "Can't delete - Questions still exist";
//				return;
//			}
			
			auditQuestionDao.remove(question.getQuestionID());
			question = null;
		} catch (Exception e) {
			message = "Error - " + e.getMessage();
		}
	}	

	public String[] getQuestionTypes() {
		return AuditQuestion.TYPE_ARRAY;
	}
	
}
