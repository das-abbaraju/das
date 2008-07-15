package com.picsauditing.actions.auditType;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

public class ManageQuestion extends ManageSubCategory {

	protected AuditQuestionDAO auditQuestionDao = null;
	protected AuditQuestion question = null;

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

	protected void load(AuditQuestion o) {
		this.question = o;
		load(question.getSubCategory());
	}
	
	
	public String save() {
		if( question != null ) {
			auditQuestionDao.save(question);
		}
		return SUCCESS;
	}

	
}
