package com.picsauditing.actions.audits;

import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.Inputs;
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
	static private String VERIFY = "Verify";
	protected boolean viewBlanks = true;

	protected AuditCatData previousCategory = null;
	protected AuditCatData nextCategory = null;
	protected AuditCatData currentCategory = null;

	private AuditPercentCalculator auditPercentCalculator;
	
	public AuditCategoryAction(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditPercentCalculator = auditPercentCalculator;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		getCategories();

		Map<Integer, AuditData> answers = null;
		if (catDataID > 0) {
			for (AuditCatData catData : categories) {
				if (catData.getId() == catDataID) {
					answers = auditDataDao.findByCategory(auditID, catData
							.getCategory());
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
			if (mode == null && conAudit.getAuditStatus().equals(AuditStatus.Pending))
				mode = EDIT;
			if (mode == null && conAudit.getAuditStatus().equals(AuditStatus.Submitted))
				mode = VERIFY;
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
		if (mode.equals(VERIFY) && !isCanVerify())
			mode = VIEW;

		if (mode.equals(EDIT)
				&& currentCategory.getCategory().getId() == AuditCategory.OSHA) {
			if (!isOshaCorporate()) {
				OshaLog oshaCorporate = new OshaLog();
				oshaCorporate.setContractorAccount(contractor);
				oshaCorporate.setType(OshaType.OSHA);
				oshaCorporate.setYear1(new OshaLogYear());
				oshaCorporate.setYear2(new OshaLogYear());
				oshaCorporate.setYear3(new OshaLogYear());
				OshaLogDAO dao = (OshaLogDAO) SpringUtils.getBean("OshaLogDAO");
				dao.save(oshaCorporate);
				contractor.getOshas().add(oshaCorporate);
			}
		}
		auditPercentCalculator.updatePercentageCompleted(currentCategory);
		auditPercentCalculator.percentCalculateComplete(conAudit);
		return SUCCESS;
	}

	private void fillAnswers(AuditCatData catData,
			Map<Integer, AuditData> answers) {
		if (answers.size() == 0)
			return;
		for (AuditSubCategory subCategory : catData.getCategory()
				.getSubCategories()) {
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

	private boolean isOshaCorporate() {
		for (OshaLog osha : this.contractor.getOshas()) {
			if (osha.isCorporate())
				return true;
		}
		return false;
	}
}
