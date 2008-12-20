package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.YesNo;

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
	protected boolean onlyReq = false;
	protected int catID;

	protected AuditCatData previousCategory = null;
	protected AuditCatData nextCategory = null;
	protected AuditCatData currentCategory = null;

	protected ContractorAudit previousAudit = null;
	protected ContractorAudit nextAudit = null;

	protected OshaAudit averageOshas = null;
	
	private AuditPercentCalculator auditPercentCalculator;
	private AuditCategoryDAO auditCategoryDAO;
	private OshaAuditDAO oshaAuditDAO;
	
	protected static Map<Integer, OshaType> typeMapping = new HashMap<Integer, OshaType>();
	
	static {
		typeMapping.put(AuditCategory.OSHA_AUDIT, OshaType.OSHA);
		typeMapping.put(AuditCategory.MSHA, OshaType.MSHA);
		typeMapping.put(AuditCategory.CANADIAN_STATISTICS, OshaType.COHS);
	}

	public AuditCategoryAction(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao,
			AuditPercentCalculator auditPercentCalculator,
			AuditCategoryDAO auditCategoryDAO, OshaAuditDAO oshaAuditDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditCategoryDAO = auditCategoryDAO;
		this.oshaAuditDAO = oshaAuditDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		if (auditID == 0 && catID > 0) {
			// Just Preview the Audit
			AuditCategory auditCategory = auditCategoryDAO.find(catID);
			for (AuditSubCategory auditSubCategory : auditCategory
					.getSubCategories()) {
				for (AuditQuestion auditQuestion : auditSubCategory
						.getQuestions()) {
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
		if (catDataID > 0 || catID > 0) {
			for (AuditCatData catData : categories) {
				// We can open audits using either the catID or the catDataID
				if (catData.getId() == catDataID
						|| catData.getCategory().getId() == catID) {
					// Set the other one that isn't set
					catDataID = catData.getId();
					catID = catData.getCategory().getId();

					answers = auditDataDao.findByCategory(auditID, catData
							.getCategory());
					fillAnswers(catData, answers);
					currentCategory = catData;

					if (mode == null
							&& catData.getRequiredCompleted() < catData
									.getNumRequired()) {
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
			if (currentCategory == null) {
				if (catID == 0)
					throw new Exception("Failed to find category for audit");
				
				// Create a new Category for this catID
				AuditCategory auditCategory = auditCategoryDAO.find(catID);
				currentCategory = new AuditCatData();
				currentCategory.setAudit(conAudit);
				currentCategory.setCategory(auditCategory);
				currentCategory = this.catDataDao.save(currentCategory);
				
				categories = null;
				getCategories();
			}
			
			if (mode == null
					&& conAudit.getAuditStatus().equals(AuditStatus.Pending))
				mode = EDIT;
			if (mode == null
					&& conAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
				// Add the verify mode back if needed
				// if(isCanVerify())
				// mode = VERIFY;
				// else
				mode = EDIT;
			}

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

		
		if (currentCategory != null) {

			
			if (typeMapping.get( currentCategory.getCategory().getId() ) != null ) {
				boolean hasOshaCorporate = false;
				int percentComplete = 0;
				for (OshaAudit osha : conAudit.getOshas()) {
					if (osha.isCorporate() && matchesType( currentCategory.getCategory().getId(), osha.getType() ) ) {
						hasOshaCorporate = true;
						// Calculate percent complete too
						auditPercentCalculator.percentOshaComplete(osha,
								currentCategory);
					}
				}

				if (mode.equals(EDIT) && !hasOshaCorporate) {
					OshaAudit oshaAudit = new OshaAudit();
					oshaAudit.setConAudit(conAudit);
					oshaAudit.setCorporate(true);
					oshaAudit.setType(typeMapping.get(currentCategory.getCategory().getId()));
					oshaAuditDAO.save(oshaAudit);
					conAudit.getOshas().add(oshaAudit);

					currentCategory.setNumRequired(2);
					catDataDao.save(currentCategory);
				}
			} else {
				auditPercentCalculator
						.updatePercentageCompleted(currentCategory);
			}
		}
		auditPercentCalculator.percentCalculateComplete(conAudit);

		ContractorAudit twoYearsAgo = null;
		
		if (conAudit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM) {

			String auditFor = conAudit.getAuditFor();

			if (auditFor != null && auditFor.length() > 0) {
				int auditYear = 0;

				try {
					auditYear = Integer.parseInt(auditFor);
				} catch (Exception ignoreIt) {}
				
				if( auditYear != 0 )
				{
					for( ContractorAudit ca : getActiveAudits() ) {

						if( ca.getAuditType().getAuditTypeID() == conAudit.getAuditType().getAuditTypeID() ) {
						
							String caAuditFor = ca.getAuditFor();
							int caAuditYear = 0;

							try {
								caAuditYear = Integer.parseInt(caAuditFor);

								if( caAuditYear == auditYear - 2 ) {
									twoYearsAgo = ca;
								}
								if( caAuditYear == auditYear - 1 ) {
									previousAudit = ca;
								}
								if( caAuditYear == auditYear + 1 ) {
									nextAudit = ca;
								}
								
								if( previousAudit != null && nextAudit != null ) {  //no sense continuing the loop if we already found them
									break;
								}
							
							} catch (Exception ignoreIt) {}
						}
					}
				}
			}
		}
		
		
		return SUCCESS;
	}

	private void fillAnswers(AuditCatData catData,
			Map<Integer, AuditData> answers) {
		for (AuditSubCategory subCategory : catData.getCategory()
				.getSubCategories()) {
			List<Integer> officeLocQuestions = new ArrayList<Integer>();
			if (subCategory.getCategory().getId() == AuditCategory.SERVICES_PERFORMED) {
				for (AuditQuestion question : subCategory.getQuestions()) {
					officeLocQuestions.add(question.getId());
				}
			}

			for (AuditQuestion question : subCategory.getQuestions()) {
				Map<Integer, AuditData> officeLocationAnswer = auditDataDao
						.findAnswers(auditID, officeLocQuestions);
				if (question.getQuestionType().equals("Office Location")
						&& !officeLocationAnswer.containsKey(question
								.getId())) {
					AuditData auditData = new AuditData();
					auditData.setAudit(conAudit);
					auditData.setQuestion(question);
					auditData.setAnswer("No");
					auditDataDao.save(auditData);
					answers.put(question.getId(), auditData);
				}

				if (answers.containsKey(question.getId())) {
					question.setAnswer(answers.get(question.getId()));
				}
				if (mode != null && mode.equals(EDIT)) {
					AuditQuestion dependsOnQuestion = question
							.getDependsOnQuestion();
					if (dependsOnQuestion != null
							&& dependsOnQuestion.getId() > 0) {

						if (!dependsOnQuestion.getSubCategory().getCategory()
								.equals(catData.getCategory())
								&& !answers.containsKey(dependsOnQuestion
										.getId())) {
							// Get answer and add to answer map no matter what
							answers.put(dependsOnQuestion.getId(),
									auditDataDao.findAnswerToQuestion(
											this.auditID, dependsOnQuestion
													.getId()));
						}
						dependsOnQuestion.setAnswer(answers
								.get(dependsOnQuestion.getId()));
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

	public ContractorAudit getPreviousAudit() {
		return previousAudit;
	}

	public void setPreviousAudit(ContractorAudit previousAudit) {
		this.previousAudit = previousAudit;
	}

	public ContractorAudit getNextAudit() {
		return nextAudit;
	}

	public void setNextAudit(ContractorAudit nextAudit) {
		this.nextAudit = nextAudit;
	}


	public OshaAudit getAverageOsha(OshaType oshaType) {
		OshaAudit response = null;
		
		Map<String, OshaAudit> temp = contractor.getOshas().get(oshaType);
		
		if( temp != null ) {
			response = temp.get(OshaAudit.AVG);
		}
		
		return response;
	}
	
	public boolean matchesType( int categoryId, OshaType oa ) {
		if( typeMapping.get(categoryId) == null || oa == null) return false;

		if( oa == typeMapping.get(categoryId) ) return true;
		
		return false;
	}
	
}
