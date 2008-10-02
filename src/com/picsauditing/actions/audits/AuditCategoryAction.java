package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.Inputs;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OshaLogDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaLogYear;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.SpringUtils;

/**
 * Viewing audit Data including one or more categories and their subcategories
 * and data This handles View, Edit, and Verify all at the same time
 * 
 * @author Trevor
 * 
 */
public class AuditCategoryAction extends AuditActionSupport {

	protected int catDataID = 0;
	protected String mode = null;
	static private String VIEW = "View";
	static private String EDIT = "Edit";
	protected boolean viewBlanks = true;
	protected boolean onlyReq = false;
	protected int catID;

	protected AuditCatData previousCategory = null;
	protected AuditCatData nextCategory = null;
	protected AuditCatData currentCategory = null;

	private AuditPercentCalculator auditPercentCalculator;
	private AuditCategoryDAO auditCategoryDAO;

	public AuditCategoryAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator, AuditCategoryDAO auditCategoryDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditCategoryDAO = auditCategoryDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		if(catID > 0) {
			AuditCategory auditCategory = auditCategoryDAO.find(catID);
			for(AuditSubCategory auditSubCategory : auditCategory.getSubCategories()){
				for(AuditQuestion auditQuestion : auditSubCategory.getQuestions()) {
				}
			}
			categories = new ArrayList<AuditCatData>();
			AuditCatData catData = new AuditCatData();
			catData.setCategory(auditCategory);
			catData.setApplies(YesNo.Yes);
			categories.add(catData);
			mode = EDIT;
			return SUCCESS;
		}
		
		this.findConAudit();

		getCategories();

		Map<Integer, AuditData> answers = null;
		if (catDataID > 0) {
			for (AuditCatData catData : categories) {
				if (catData.getId() == catDataID) {
					answers = auditDataDao.findByCategory(auditID, catData.getCategory());
					fillAnswers(catData, answers);
					currentCategory = catData;

					if (mode == null && catData.getRequiredCompleted() < catData.getNumRequired()) {
						mode = EDIT;
					}

				} else {
					if (catData.isAppliesB()) {
						if (currentCategory == null)
							previousCategory = catData;
						else if (nextCategory == null)
							nextCategory = catData;
					}
				}
			}
			if (mode == null && conAudit.getAuditStatus().equals(AuditStatus.Pending))
				mode = EDIT;
			if (mode == null && conAudit.getAuditStatus().equals(AuditStatus.Submitted))
				mode = EDIT;

		} else {
			// When we want to show all categories
			answers = auditDataDao.findAnswers(auditID);
			for (AuditCatData catData : categories) {
				fillAnswers(catData, answers);
			}
		}

		if (catDataID == 0)
			viewBlanks = false;

		if (mode == null)
			mode = VIEW;
		if (mode.equals(EDIT) && !isCanEdit())
			mode = VIEW;

		if (currentCategory != null) {
			if (currentCategory.getCategory().getId() == AuditCategory.OSHA) {
				boolean hasOshaCorporate = false;
				int percentComplete = 0;
				for (OshaLog osha : this.contractor.getOshas()) {
					if (osha.isCorporate()) {
						hasOshaCorporate = true;
						// Calculate percent complete too
						auditPercentCalculator.percentOshaComplete(osha, currentCategory);
					}
				}

				if (mode.equals(EDIT) && !hasOshaCorporate) {
					OshaLog oshaCorporate = new OshaLog();
					oshaCorporate.setContractorAccount(contractor);
					oshaCorporate.setType(OshaType.OSHA);
					oshaCorporate.setYear1(new OshaLogYear());
					oshaCorporate.getYear1().setApplicable(true);
					oshaCorporate.setYear2(new OshaLogYear());
					oshaCorporate.getYear2().setApplicable(true);
					oshaCorporate.setYear3(new OshaLogYear());
					oshaCorporate.getYear3().setApplicable(true);
					OshaLogDAO dao = (OshaLogDAO) SpringUtils.getBean("OshaLogDAO");
					dao.save(oshaCorporate);
					contractor.getOshas().add(oshaCorporate);

					currentCategory.setRequiredCompleted(0);
					currentCategory.setNumRequired(6);
					currentCategory.setPercentCompleted(0);
					catDataDao.save(currentCategory);
				}
			} else {
				auditPercentCalculator.updatePercentageCompleted(currentCategory);
			}
		}
		auditPercentCalculator.percentCalculateComplete(conAudit);
		return SUCCESS;
	}

	private void fillAnswers(AuditCatData catData, Map<Integer, AuditData> answers) {
		for (AuditSubCategory subCategory : catData.getCategory().getSubCategories()) {
			for (AuditQuestion question : subCategory.getQuestions()) {
				if (answers.containsKey(question.getQuestionID())) {
					question.setAnswer(answers.get(question.getQuestionID()));
				}
				if (mode != null && mode.equals(EDIT)) {
					AuditQuestion dependsOnQuestion = question.getDependsOnQuestion();
					if (dependsOnQuestion != null && dependsOnQuestion.getQuestionID() > 0) {

						if (!dependsOnQuestion.getSubCategory().getCategory().equals(catData.getCategory())
								&& !answers.containsKey(dependsOnQuestion.getQuestionID())) {
							// Get answer and add to answer map no matter what
							answers.put(dependsOnQuestion.getQuestionID(), auditDataDao.findAnswerToQuestion(
									this.auditID, dependsOnQuestion.getQuestionID()));
						}
						dependsOnQuestion.setAnswer(answers.get(dependsOnQuestion.getQuestionID()));
					}
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

	public boolean isOnlyReq() {
		return onlyReq;
	}

	public void setOnlyReq(boolean onlyReq) {
		this.onlyReq = onlyReq;
	}

	public void setCatID(int catID) {
		this.catID = catID;
	}
}
