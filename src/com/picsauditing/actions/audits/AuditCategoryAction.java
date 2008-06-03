package com.picsauditing.actions.audits;

import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;

/**
 * This class isn't being used yet, but it's designed to view audit cat/section/data
 * @author Trevor
 *
 */
public class AuditCategoryAction extends AuditActionSupport {
	protected int catDataID = 0;
	protected List<AuditCatData> categories;

	protected AuditCategoryDataDAO catDataDAO;
	
	public AuditCategoryAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditDataDAO auditDataDao,
			AuditCategoryDataDAO auditCategoryDataDAO) {
		super(accountDao, auditDao, auditDataDao);
		this.catDataDAO = auditCategoryDataDAO;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();
		
		categories = catDataDAO.findByAudit(conAudit, permissions);
		
		Map<Integer, AuditData> answers = null;
		if (catDataID > 0) {
			for(AuditCatData catData : categories) {
				if (catData.getId() == catDataID) {
					answers = auditDataDao.findByCategory(auditID, catData.getCategory());
					fillAnswers(catData, answers);
				}
			}
		}
		if (answers == null) {
			answers = auditDataDao.findAnswers(auditID);
			for(AuditCatData catData : categories) {
				fillAnswers(catData, answers);
			}
		}
		
		return SUCCESS;
	}
	
	private void fillAnswers(AuditCatData catData, Map<Integer, AuditData> answers) {
		if (answers.size() == 0)
			return;
		for(AuditSubCategory subCategory : catData.getCategory().getSubCategories()) {
			for(AuditQuestion question : subCategory.getQuestions()) {
				if (answers.containsKey(question.getQuestionID())) {
					question.setAnswer(answers.get(question.getQuestionID()));
				}
			}
		}
	}

	public int getCatDataID() {
		return catDataID;
	}

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	public List<AuditCatData> getCategories() {
		return categories;
	}
}
