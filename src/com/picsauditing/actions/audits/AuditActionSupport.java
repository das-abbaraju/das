package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WorkflowStep;
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
	protected Map<Integer, WorkflowStep> caoSteps = null;
	protected Map<AuditStatus, List<Integer>> actionStatus = new HashMap<AuditStatus, List<Integer>>();
	private Map<WorkflowStep, Integer> allActionMap = new HashMap<WorkflowStep, Integer>();

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
	public void getValidSteps(){
		List<AuditStatus> occ = new ArrayList<AuditStatus>();
		if(caoSteps ==null)
			caoSteps = new HashMap<Integer, WorkflowStep>();
		for(ContractorAuditOperator cao : conAudit.getOperators()){
			if(cao.isVisible() && cao.isVisibleTo(permissions)){
				for (WorkflowStep workflowStep : conAudit.getAuditType().getWorkFlow().getSteps()) {
					if(workflowStep.getOldStatus() == cao.getStatus()){
						if(workflowStep.getNewStatus() == AuditStatus.Submitted){
							if(!isCanSubmitAudit(cao))
								continue;
						}
						if(workflowStep.getNewStatus() == AuditStatus.Complete){
							if(!isCanCloseAudit(cao))
								continue;
						}
						caoSteps.put(cao.getId(), workflowStep);
					}
				}
			}
		}
		if(!caoSteps.isEmpty()){
			// change map to multimap
			for (Entry<Integer, WorkflowStep> en : caoSteps.entrySet()) {
				if(occ.contains(en.getValue().getNewStatus())){
						addToActionStatus(en.getValue().getNewStatus(), en.getKey());
				} else {
					occ.add(en.getValue().getNewStatus());
				}
			}
		}
	}
	
	public Integer addToActionStatus(AuditStatus as, Integer value){
		List<Integer> l = actionStatus.get(as);
		if(l==null)
			actionStatus.put(as, l = new ArrayList<Integer>());
		l.add(value);
		
		return value;
	}
	
	public WorkflowStep getCurrentCaoStep(int caoID){
		return caoSteps.get(caoID);
	}

	public boolean isCanEditAudit() {
		if (conAudit.isExpired())
			return false;
	
		AuditType type = conAudit.getAuditType();
	
		if (type.getClassType().isPolicy()) {
			// we don't want the contractors to edit the effective dates on the
			// old policy
			if (conAudit.willExpireSoon()) {
				if (conAudit.hasCaoStatusAfter(AuditStatus.Submitted))
					return false;
			}
		}
	
		// Auditors can edit their assigned audits
		if (type.isHasAuditor() && !type.isCanContractorEdit()
				&& conAudit.getAuditor() != null
				&& permissions.getUserId() == conAudit.getAuditor().getId())
			return true;
	
		if (permissions.seesAllContractors())
			return true;
	
		if (permissions.isContractor()) {
			if (conAudit.getAuditType().getWorkFlow().getId() == 5
					|| conAudit.getAuditType().getWorkFlow().getId() == 3) {
				if (conAudit.hasCaoStatusAfter(AuditStatus.Resubmitted))
					return false;
			}
			return type.isCanContractorEdit();
		}
		
		if (type.getEditPermission() != null) {
			if(permissions.hasPermission(type.getEditPermission()) && !isAuditWithOtherOperators())
					return true;
		}
	
		return false;
	}

	public boolean isCanSubmitAudit(ContractorAuditOperator cao) {
		if (!isCanEditAudit())
			return false;
	
		if (cao.canSubmitCao()) {
			if (permissions.isContractor()) {
				if (!conAudit.getContractorAccount()
						.isPaymentMethodStatusValid()
						&& conAudit.getContractorAccount().isMustPayB())
					return false;
			}
			return true;
		} else if (conAudit.getAuditType().isRenewable()) {
			if (permissions.isContractor()) {
				// We don't allow admins to resubmit audits (only
				// contractors)
				if (conAudit.isAboutToExpire())
					return true;
			}
		}
		return false;
	}

	public boolean isCanExempt() {
		if(permissions.isAdmin())
			return true;
		return false;
	}

	/**
	 * Can the current user submit this audit in its current state?
	 * 
	 * @return
	 */
	public boolean isCanCloseAudit(ContractorAuditOperator cao) {
		if (permissions.isContractor())
			return false;
		if (!isCanEditAudit())
			return false;
	
		if (cao.canVerifyCao()) {
			return true;
		}
		if (!conAudit.getAuditType().getWorkFlow().isHasSubmittedStep())
			return false;
	
		return false;
	}

	public Map<AuditStatus, List<Integer>> getActionStatus() {
		return actionStatus;
	}
}
