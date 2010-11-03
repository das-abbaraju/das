package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class AuditActionSupport extends ContractorActionSupport {

	protected int auditID = 0;
	protected int categoryID = 0;
	protected String descriptionOsMs;
	protected boolean systemEdit = false;
	
	protected ContractorAudit conAudit;
	protected AuditCategoryDataDAO catDataDao;
	protected AuditDataDAO auditDataDao;
	protected CertificateDAO certificateDao;
	
	private Map<Integer, AuditData> hasManual;
	private List<AuditCategoryRule> rules = null;
	protected Map<AuditCategory, AuditCatData> categories = null;
	protected ArrayListMultimap<Integer, WorkflowStep> caoSteps = ArrayListMultimap.create();
	protected ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap
			.create();

	private List<CategoryNode> categoryNodes;

	public AuditActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, CertificateDAO certificateDao) {
		super(accountDao, auditDao);
		this.catDataDao = catDataDao;
		this.auditDataDao = auditDataDao;
		this.certificateDao = certificateDao;
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
					for (Facility facility : getOperatorAccount()
							.getOperatorFacilities()) {
						operators.add(facility.getOperator());
					}
				} else
					operators.add(getOperatorAccount());

				AuditCategoriesDetail auditCategoryDetail = builder.getDetail(
						conAudit.getAuditType(), getRules(), operators);
				requiredCategories = auditCategoryDetail.categories;
			}

			categories = conAudit.getApplicableCategories(permissions,
					requiredCategories);
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
	
	public List<ContractorAuditOperator> getViewableOperators(Permissions permissions){
		if(systemEdit)
			return conAudit.getSortedOperators();			
		else return conAudit.getViewableOperators(permissions);
	}

	public void getValidSteps() {
		List<AuditStatus> occ = new ArrayList<AuditStatus>();
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible() && cao.isVisibleTo(permissions)) {
				for (WorkflowStep workflowStep : conAudit.getAuditType()
						.getWorkFlow().getSteps()) {
					if (workflowStep.getOldStatus() == cao.getStatus()) {
						if(canPerformAction(cao, workflowStep))
							caoSteps.put(cao.getId(), workflowStep);
					}
				}
			}
		}
		if (!caoSteps.isEmpty()) {
			for (Entry<Integer, WorkflowStep> en : caoSteps.entries()) {
				if (occ.contains(en.getValue().getNewStatus()))
					actionStatus.put(en.getValue().getNewStatus(), en.getKey());
				else
					occ.add(en.getValue().getNewStatus());
			}
		}
	}
	
	public boolean canPerformAction(ContractorAuditOperator cao, WorkflowStep workflowStep) {
		if(cao.getPercentComplete() < 100)
			return false;
		
		AuditType type = cao.getAudit().getAuditType();

		if(workflowStep.getNewStatus().isComplete() 
				&& type.getWorkFlow().isHasSubmittedStep() && cao.getPercentVerified() < 100)
			return false;
		// admins can perform any action
		if(permissions.seesAllContractors())
			return true;
		// operator and corporate can also perform any action if they have permission
		if(permissions.isOperatorCorporate()) {
			if(type.getEditPermission() != null) {
			 return permissions.hasPermission(type.getEditPermission());
			}
		}
		// contractor can perform only submits and complete for pqf specific's if they can edit that audit
		if(permissions.isContractor() && type.isCanContractorEdit()) {
			if (!conAudit.getContractorAccount()
					.isPaymentMethodStatusValid()
					&& conAudit.getContractorAccount().isMustPayB())
				return false;
			if(workflowStep.getNewStatus().isSubmitted())
				return true;
			if(workflowStep.getNewStatus().isResubmitted() && conAudit.isAboutToExpire())
				return true;
			if(workflowStep.getNewStatus().isComplete() 
					&& workflowStep.getWorkflow().getId() == 1) // if Single Step Workflow (Pending to Complete)
				return true;
		}
		return false;	
	}
	
	public List<WorkflowStep> getCurrentCaoStep(int caoID) {
		if (caoSteps == null)
			getValidSteps();
		return caoSteps.get(caoID);
	}
	
	public List<AuditStatus> getValidStatuses(int caoID){
		List<AuditStatus> validStatuses = new ArrayList<AuditStatus>();
		for(ContractorAuditOperator cao : conAudit.getSortedOperators()){
			if(cao.getId() == caoID){
				for(WorkflowStep wfs : cao.getAudit().getAuditType().getWorkFlow().getSteps()){
					validStatuses.add(wfs.getNewStatus());
				}
			}
		}
		return validStatuses;
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
			if (permissions.hasPermission(type.getEditPermission())
					&& !isAuditWithOtherOperators())
				return true;
		}

		return false;
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

	public ArrayListMultimap<AuditStatus, Integer> getActionStatus() {
		return actionStatus;
	}

	public boolean isCanPreview() {
		return conAudit.hasCaoStatus(AuditStatus.Pending);
	}

	public boolean isCanViewRequirements() {
		if (conAudit.getAuditType().getWorkFlow().getId() == Workflow.AUDIT_REQUIREMENTS_WORKFLOW)
			return conAudit.hasCaoStatusAfter(AuditStatus.Incomplete);
		return false;
	}

	public boolean isCanSchedule() {
		if (conAudit.getAuditType().isScheduled()
				&& (permissions.isContractor() || permissions.isAdmin()))
			return conAudit.hasCaoStatus(AuditStatus.Pending);
		return false;
	}

	public boolean isAppliesSubCategory(AuditCategory auditCategory) {
		if (categories.get(auditCategory).isApplies())
			return true;
		if (!categories.get(auditCategory.getParent()).isApplies())
			return true;
		return false;
	}

	public Certificate getCertificate(AuditData data) {
		try {
			int certID = Integer.parseInt(data.getAnswer());
			return certificateDao.find(certID);
		} catch(NumberFormatException nfe) {
			return null;
		}
	}

	class CategoryNode {
		public AuditCategory category;
		public List<CategoryNode> subCategories;
		public int total;
		public int answered;

		public float getPercent() {
			if (total == 0)
				return 100;
			return (int) ((answered * 1f / total) * 100);
		}
	}

	public List<CategoryNode> getCategoryNodes() {
		if (categoryNodes == null)
			categoryNodes = createCategoryNodes(conAudit.getAuditType()
					.getTopCategories());

		return categoryNodes;
	}

	private List<CategoryNode> createCategoryNodes(List<AuditCategory> cats) {
		return createCategoryNodes(cats, false);
	}

	private List<CategoryNode> createCategoryNodes(List<AuditCategory> cats,
			boolean addAll) {
		List<CategoryNode> nodes = new ArrayList<CategoryNode>();
		for (AuditCategory cat : cats) {
			if (addAll
					|| (getCategories().get(cat) != null && getCategories()
							.get(cat).isApplies())) {
				CategoryNode node = new CategoryNode();
				node.category = cat;
				if (conAudit.getAuditType().getClassType().isIm()) {
					node.total = getCategories().get(cat).getScoreCount();
					node.answered = (int) (getCategories().get(cat).getScore() * getCategories()
							.get(cat).getScoreCount());
				} else {
					node.total = getCategories().get(cat).getNumRequired();
					node.answered = getCategories().get(cat)
							.getRequiredCompleted();
				}
				node.subCategories = createCategoryNodes(
						cat.getSubCategories(), addAll);
				for (CategoryNode n : node.subCategories) {
					node.total += n.total;
					node.answered += n.answered;
				}
				nodes.add(node);
			}
		}

		return nodes;
	}

	public List<CategoryNode> getNotApplicableCategoryNodes() {
		List<CategoryNode> nodes = new ArrayList<CategoryNode>();
		for (AuditCategory cat : conAudit.getAuditType().getTopCategories()) {
			if (getCategories().get(cat) != null
					&& !getCategories().get(cat).isApplies()) {
				CategoryNode node = new CategoryNode();
				node.category = cat;
				node.subCategories = createCategoryNodes(
						cat.getSubCategories(), true);
				nodes.add(node);
			}
		}
		return nodes;
	}

	public boolean isSystemEdit() {
		return systemEdit;
	}

	public void setSystemEdit(boolean systemEdit) {
		this.systemEdit = systemEdit;
	}
}
