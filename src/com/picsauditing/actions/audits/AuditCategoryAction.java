package com.picsauditing.actions.audits;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.Inputs;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.YesNo;

/**
 * This class isn't being used yet, but it's designed to view audit
 * cat/section/data
 * 
 * @author Trevor
 * 
 */
public class AuditCategoryAction extends AuditActionSupport {
	protected int catDataID = 0;
	protected String mode = "View";
	protected boolean viewBlanks = true;
	protected List<AuditCatData> categories;

	protected AuditCatData previousCategory = null;
	protected AuditCatData nextCategory = null;
	protected AuditCatData currentCategory = null;

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
			for (AuditCatData catData : categories) {
				if (catData.getId() == catDataID) {
					answers = auditDataDao.findByCategory(auditID, catData.getCategory());
					fillAnswers(catData, answers);
					currentCategory = catData;
				} else {
					if (!catData.getApplies().equals(YesNo.No)) {
						if (currentCategory == null)
							previousCategory = catData;
						else if (nextCategory == null)
							nextCategory = catData;
					}
				}
			}
		}
		if (answers == null) {
			answers = auditDataDao.findAnswers(auditID);
			for (AuditCatData catData : categories) {
				fillAnswers(catData, answers);
			}
		}

		if (catDataID == 0)
			viewBlanks = false;

		return SUCCESS;
	}

	private void fillAnswers(AuditCatData catData, Map<Integer, AuditData> answers) {
		if (answers.size() == 0)
			return;
		for (AuditSubCategory subCategory : catData.getCategory().getSubCategories()) {
			for (AuditQuestion question : subCategory.getQuestions()) {
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

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isViewBlanks() {
		return viewBlanks;
	}

	public void setViewBlanks(boolean viewBlanks) {
		this.viewBlanks = viewBlanks;
	}

	public TreeMap<String, String> getStateList() {
		return State.getStates(true);
	}

	public String[] getCountryList() {
		return Inputs.COUNTRY_ARRAY;
	}

	public AuditCatData getPreviousCategory() {
		return previousCategory;
	}

	public AuditCatData getNextCategory() {
		return nextCategory;
	}

	public AuditCatData getCurrentCategory() {
		return currentCategory;
	}

}
