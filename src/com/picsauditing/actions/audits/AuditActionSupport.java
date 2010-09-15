package com.picsauditing.actions.audits;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class AuditActionSupport extends ContractorActionSupport {

	protected int auditID = 0;
	protected int categoryID = 0;
	protected ContractorAudit conAudit;
	protected AuditCategoryDataDAO catDataDao;
	protected AuditDataDAO auditDataDao;
	protected String descriptionOsMs;
	private Map<Integer, AuditData> hasManual;
	private List<AuditCategoryRule> rules = null;
	protected Map<AuditCategory, AuditCatData> categories = null;

	public AuditActionSupport(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao) {
		super(accountDao, auditDao);
		this.catDataDao = catDataDao;
		this.auditDataDao = auditDataDao;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		return SUCCESS;
	}

	protected void findConAudit() throws Exception {
		conAudit = auditDao.find(auditID);
		if (conAudit == null)
			throw new RecordNotFoundException("Audit " + this.auditID);

		contractor = conAudit.getContractorAccount();
		id = contractor.getId();
		if (permissions.isContractor() && id != permissions.getAccountId())
			throw new Exception("Contractors can only view their own audits");

		if (!checkPermissionToView())
			throw new NoRightsException("No Rights to View this Contractor");

		if (!conAudit.isVisibleTo(permissions))
			throw new NoRightsException(conAudit.getAuditType().getAuditName());
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int id) {
		this.auditID = id;
	}

	public ContractorAudit getConAudit() {
		return conAudit;
	}

	public boolean isSinglePageAudit() {
		return getCategories().size() == 1;
	}

	public Map<AuditCategory, AuditCatData> getCategories() {
		if (categories == null) {
			Set<AuditCategory> requiredCategories = null;
			if (permissions.isOperatorCorporate()) {
				AuditBuilder builder = new AuditBuilder();
				Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
				if (permissions.isCorporate()) {
					for (Facility facility : getOperatorAccount().getOperatorFacilities()) {
						operators.add(facility.getOperator());
					}
				} else
					operators.add(getOperatorAccount());

				AuditCategoriesDetail auditCategoryDetail = 
					builder.getDetail(conAudit.getAuditType(), getRules(), operators);
				requiredCategories = auditCategoryDetail.categories;
			}

			categories = conAudit.getApplicableCategories(permissions, requiredCategories);
		}
		return categories;
	}

	public boolean isHasSafetyManual() {
		hasManual = getDataForSafetyManual();
		if (hasManual == null || hasManual.size() == 0)
			return false;
		return true;
	}

	public Map<Integer, AuditData> getDataForSafetyManual() {
		int questionID = AuditQuestion.MANUAL_PQF;
		if (conAudit.getAuditType().getId() == AuditType.BPIISNCASEMGMT) {
			questionID = 3477;
		}
		Map<Integer, AuditData> answers = auditDataDao
				.findAnswersForSafetyManual(conAudit.getContractorAccount()
						.getId(), questionID);
		if (answers == null || answers.size() == 0)
			return null;
		return answers;
	}

	public Map<Integer, AuditData> getSafetyManualLink() {
		if (hasManual != null)
			return hasManual;
		else
			hasManual = getDataForSafetyManual();
		return hasManual;
	}

	/**
	 * 
	 * @return true if the current users is an operator and there is a visible
	 *         cao belonging to another operator
	 */
	public boolean isAuditWithOtherOperators() {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			// This logic is somewhat complex so here's an example:
			// BASF Freeport Hub has access to many operators
			// who use either BASF Corporate and BASF Catalyst insurance
			// requirements
			// If this contractor policy is visible (needed) for
			// Paramount,
			// then the policy is locked down.
			// One potential flaw is that if the other CAO happens to be
			// BASF Canada,
			// which is not part of the Freeport Hub, then the policy
			// will be locked for BASF Freeport.
			if (!cao.isVisibleTo(permissions))
				return true;
		}

		return false;
	}

	public String getDescriptionOsMs() {
		String descriptionText = "OSHA Recordable";
		for (OshaAudit osha : conAudit.getOshas())
			if (osha.getType().equals(OshaType.MSHA))
				descriptionText = "MSHA Reportable";
			else
				descriptionText = "OSHA Recordable";
		return descriptionText;
	}

	protected List<AuditCategoryRule> getRules() {
		if (rules == null) {
			AuditDecisionTableDAO auditRulesDAO = (AuditDecisionTableDAO) SpringUtils
					.getBean("AuditDecisionTableDAO");
			rules = auditRulesDAO.getApplicableCategoryRules(conAudit
					.getContractorAccount(), conAudit.getAuditType());
		}
		return rules;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public Map<AuditCategory,Integer> calculatePercentComplete() {
		Map<AuditCategory,Integer> percentComplete = new HashMap<AuditCategory,Integer>();
		for(AuditCategory auditCategory : getCategories().keySet()) {
			if(auditCategory.getParent()  == null) {
				int percent = 0;
				for(AuditCategory childCategory : auditCategory.getChildren()) {
					percent += getCategories().get(childCategory).getPercentCompleted();
				}
				percentComplete.put(auditCategory, percent);
			}
		}
		return percentComplete; 
	}

	public Map<AuditCategory,Integer> calculatePercentVerified() {
		Map<AuditCategory,Integer> percentVerified = new HashMap<AuditCategory,Integer>();
		for(AuditCategory auditCategory : getCategories().keySet()) {
			if(auditCategory.getParent()  == null) {
				int percent = 0;
				for(AuditCategory childCategory : auditCategory.getChildren()) {
					percent += getCategories().get(childCategory).getPercentVerified();
				}
				percentVerified.put(auditCategory, percent);
			}
		}
		
		return percentVerified; 
	}
}
