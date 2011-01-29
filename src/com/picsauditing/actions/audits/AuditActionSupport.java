package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
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
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowStep;

@SuppressWarnings("serial")
public class AuditActionSupport extends ContractorActionSupport {

	protected int auditID = 0;
	protected int categoryID = 0;
	protected String descriptionOsMs;
	protected boolean systemEdit = false;
	protected boolean showVerified = false;

	protected ContractorAudit conAudit;
	protected AuditCategoryDataDAO catDataDao;
	protected AuditDataDAO auditDataDao;
	protected CertificateDAO certificateDao;
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	private Map<Integer, AuditData> hasManual;
	private List<AuditCategoryRule> rules = null;
	protected Map<AuditCategory, AuditCatData> categories = null;
	protected ArrayListMultimap<Integer, WorkflowStep> caoSteps = ArrayListMultimap.create();
	protected ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap.create();

	private List<CategoryNode> categoryNodes;

	public AuditActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, CertificateDAO certificateDao,
			AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDao, auditDao);
		this.catDataDao = catDataDao;
		this.auditDataDao = auditDataDao;
		this.certificateDao = certificateDao;
		this.auditCategoryRuleCache = auditCategoryRuleCache;
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
		return getCategories(conAudit, false);
	}

	public Map<AuditCategory, AuditCatData> getCategories(ContractorAudit conAudit, boolean reload) {
		if (categories == null || reload) {
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

				AuditCategoriesDetail auditCategoryDetail = builder.getDetail(conAudit.getAuditType(),
						getRules(conAudit, reload), operators);
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
		Map<Integer, AuditData> answers = auditDataDao.findAnswersForSafetyManual(conAudit.getContractorAccount()
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

	protected List<AuditCategoryRule> getRules(ContractorAudit conAudit) {
		return getRules(conAudit, false);
	}

	protected List<AuditCategoryRule> getRules(ContractorAudit conAudit, boolean reload) {
		if (rules == null || reload) {
			rules = auditCategoryRuleCache.getApplicableCategoryRules(conAudit.getContractorAccount(),
					conAudit.getAuditType());
		}
		return rules;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public List<ContractorAuditOperator> getViewableOperators(Permissions permissions) {
		List<ContractorAuditOperator> viewableCaos = new ArrayList<ContractorAuditOperator>();
		if (systemEdit && !permissions.isOperatorCorporate())
			viewableCaos = conAudit.getSortedOperators();
		else
			viewableCaos = conAudit.getViewableOperators(permissions);
		for (ContractorAuditOperator cao : viewableCaos) {
			if (cao.getStatus().after(AuditStatus.Incomplete)) {
				showVerified = true;
				break;
			}
		}
		return viewableCaos;
	}

	public Set<OperatorAccount> getViewableCaops(ContractorAuditOperator cao) {
		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();

		for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
			if (permissions.isOperatorCorporate()) {
				if (permissions.getVisibleAccounts().contains(caop.getOperator().getId()))
					operators.add(caop.getOperator());
			} else {
				operators.add(caop.getOperator());
			}
		}
		return operators;
	}

	public void getValidSteps() {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible() && cao.isVisibleTo(permissions)) {
				for (WorkflowStep workflowStep : conAudit.getAuditType().getWorkFlow().getSteps()) {
					if (workflowStep.getOldStatus() == cao.getStatus()) {
						if (canPerformAction(cao, workflowStep)) {
							caoSteps.put(cao.getId(), workflowStep);
							actionStatus.put(workflowStep.getNewStatus(), cao.getId());
						}
					}
				}
			}
		}
		if (!actionStatus.isEmpty()) {
			for (Iterator<Entry<AuditStatus, Collection<Integer>>> en = actionStatus.asMap().entrySet().iterator(); en
					.hasNext();) {
				if (!(en.next().getValue().size() > 1))
					en.remove();
			}
		}
	}

	public boolean canPerformAction(ContractorAuditOperator cao, WorkflowStep workflowStep) {
		if (cao.getPercentComplete() < 100) {
			if (cao.getPercentVerified() < 100 || !cao.getStatus().isSubmitted())
				return false;
		}

		AuditType type = cao.getAudit().getAuditType();

		if (workflowStep.getNewStatus().isComplete() && type.getWorkFlow().isHasSubmittedStep()
				&& cao.getPercentVerified() < 100)
			return false;
		// admins can perform any action
		if (permissions.seesAllContractors())
			return true;
		// operator and corporate can also perform any action if they have
		// permission
		if (permissions.isOperatorCorporate()) {
			if (type.getEditPermission() != null) {
				return permissions.hasPermission(type.getEditPermission());
			}
		}
		// contractor can perform only submits and complete for pqf specific's
		// if they can edit that audit
		if (permissions.isContractor() && type.isCanContractorEdit()) {
			if (!conAudit.getContractorAccount().isPaymentMethodStatusValid()
					&& conAudit.getContractorAccount().isMustPayB() && !conAudit.getContractorAccount().isDemo())
				return false;
			if (workflowStep.getNewStatus().isSubmitted())
				return true;
			// contractor can always move to resubmitted
			if (workflowStep.getNewStatus().isResubmitted())
				return true;
			// if Single Step Workflow (Pending to Complete)
			if (workflowStep.getNewStatus().isComplete() && workflowStep.getWorkflow().getId() == 1)
				return true;
		}
		// Auditor for this audit can perform all actions
		if (conAudit.getAuditor() != null && conAudit.getAuditor().getId() == permissions.getUserId())
			return true;
		return false;
	}

	public List<WorkflowStep> getCurrentCaoStep(int caoID) {
		if (caoSteps == null || caoSteps.isEmpty())
			getValidSteps();
		return caoSteps.get(caoID);
	}

	public List<AuditStatus> getValidStatuses(int caoID) {
		List<AuditStatus> validStatuses = new ArrayList<AuditStatus>();
		for (ContractorAuditOperator cao : conAudit.getSortedOperators()) {
			if (cao.getId() == caoID) {
				for (WorkflowStep wfs : cao.getAudit().getAuditType().getWorkFlow().getSteps()) {
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
		if (type.isHasAuditor() && !type.isCanContractorEdit() && conAudit.getAuditor() != null
				&& permissions.getUserId() == conAudit.getAuditor().getId())
			return true;

		if (permissions.seesAllContractors())
			return true;

		if (permissions.isContractor()) {
			boolean canEdit = type.isCanContractorEdit();
			if (conAudit.getAuditType().getWorkFlow().getId() == 5
					|| conAudit.getAuditType().getWorkFlow().getId() == 3) {
				if (canEdit) {
					canEdit = false;
					for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
						if (cao.getStatus().before(AuditStatus.Submitted)) {
							canEdit = true;
							break;
						}
					}
				}
			}
			if (conAudit.getAuditType().getId() == 176) {
				if (conAudit.hasCaoStatusAfter(AuditStatus.Pending))
					canEdit = false;
			}
			return canEdit;
		}

		if (type.getEditPermission() != null) {
			if (permissions.hasPermission(type.getEditPermission())) {
				return true;
			}
		}

		return false;
	}

	public ArrayListMultimap<AuditStatus, Integer> getActionStatus() {
		return actionStatus;
	}

	public boolean isCanSystemEdit() {
		if (permissions.hasPermission(OpPerms.AuditEdit)) {
			return true;
		}

		if (conAudit.getAuditType().getClassType().isPolicy()) {
			if (conAudit.getAuditor() != null && (conAudit.getAuditor().getId() == permissions.getUserId()))
				return true;

			if (permissions.isOperatorCorporate())
				return true;
		}

		return false;
	}

	public boolean isCanEditCao() {
		if (permissions.hasPermission(OpPerms.CaoEdit))
			return true;
		return false;
	}

	public boolean isCanEditCao(ContractorAuditOperator cao) {
		if (isCanEditCao()) {
			if (permissions.isAdmin())
				return true;
			if (permissions.isOperatorCorporate() && conAudit.getAuditType().getClassType().isPolicy())
				return !cao.getOperator().isPicsCorporate();
		}
		return false;
	}

	public boolean isCanVerifyAudit() {
		if (!permissions.isAuditor() && !permissions.hasGroup(959))
			return false;

		if (!conAudit.getAuditType().getWorkFlow().isHasSubmittedStep())
			return false;

		if (conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
			return true;

		return false;
	}

	public boolean isCanVerifyPqf() {
		if (!permissions.hasPermission(OpPerms.AuditVerification))
			return false;
		if (!conAudit.getAuditType().isPqf() && !conAudit.getAuditType().isAnnualAddendum())
			return false;

		if (conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
			return true;

		return false;
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
		if (conAudit.getAuditType().isScheduled() && (permissions.isContractor() || permissions.isAdmin()))
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
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	class CategoryNode {
		public AuditCategory category;
		public List<CategoryNode> subCategories;
		public int total;
		public int answered;
		public int verified;

		public float getPercentComplete() {
			int percent = (int) ((answered * 1f / total) * 100);
			if (total == 0 || percent > 100)
				return 100;
			return percent;
		}

		public float getPercentVerified() {
			int percent = (int) ((verified * 1f / total) * 100);
			if (total == 0 || percent > 100)
				return 100;
			return percent;
		}
	}

	public List<CategoryNode> getCategoryNodes() {
		if (categoryNodes == null)
			categoryNodes = createCategoryNodes(conAudit.getAuditType().getTopCategories());

		return categoryNodes;
	}

	private List<CategoryNode> createCategoryNodes(List<AuditCategory> cats) {
		return createCategoryNodes(cats, false);
	}

	private List<CategoryNode> createCategoryNodes(List<AuditCategory> cats, boolean addAll) {
		List<CategoryNode> nodes = new ArrayList<CategoryNode>();
		for (AuditCategory cat : cats) {
			if (addAll || (getCategories().get(cat) != null && getCategories().get(cat).isApplies())) {
				CategoryNode node = new CategoryNode();
				node.category = cat;
				if (conAudit.getAuditType().getClassType().isIm()) {
					node.total = getCategories().get(cat).getScoreCount();
					node.answered = (int) (getCategories().get(cat).getScore() * getCategories().get(cat)
							.getScoreCount());
				} else {
					node.total = getCategories().get(cat).getNumRequired();
					node.answered = getCategories().get(cat).getRequiredCompleted();
					node.verified = getCategories().get(cat).getNumVerified();
				}
				node.subCategories = createCategoryNodes(cat.getSubCategories(), addAll);
				for (CategoryNode n : node.subCategories) {
					node.total += n.total;
					node.answered += n.answered;
					node.verified += n.verified;
				}
				nodes.add(node);
			}
		}

		return nodes;
	}

	public List<CategoryNode> getNotApplicableCategoryNodes() {
		List<CategoryNode> nodes = new ArrayList<CategoryNode>();
		for (AuditCategory cat : conAudit.getAuditType().getTopCategories()) {
			if (getCategories().get(cat) != null && !getCategories().get(cat).isApplies()) {
				CategoryNode node = new CategoryNode();
				node.category = cat;
				node.subCategories = createCategoryNodes(cat.getSubCategories(), true);
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

	public boolean isShowVerified() {
		return showVerified;
	}

	public void setShowVerified(boolean showVerified) {
		this.showVerified = showVerified;
	}

	public boolean matchesType(int categoryId, OshaType oa) {
		if (OshaTypeConverter.getTypeFromCategory(categoryId) == null || oa == null)
			return false;
		if (oa == OshaTypeConverter.getTypeFromCategory(categoryId))
			return true;
		return false;
	}
}
