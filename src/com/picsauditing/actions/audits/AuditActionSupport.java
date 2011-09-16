package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ArrayListMultimap;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditActionSupport extends ContractorActionSupport {
	@Autowired
	protected AuditCategoryDataDAO catDataDao;
	@Autowired
	protected AuditDataDAO auditDataDao;
	@Autowired
	protected CertificateDAO certificateDao;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	protected ContractorAuditOperatorDAO caoDAO;
	@Autowired
	protected ContractorAuditOperatorWorkflowDAO caowDAO;
	@Autowired
	protected AuditCategoryRuleCache auditCategoryRuleCache;
	@Autowired
	private AuditDecisionTableDAO auditRuleDAO;
	@Autowired
	private ContractorAuditDAO conAuditDAO;

	protected int auditID = 0;
	protected int categoryID = 0;
	protected String descriptionOsMs;
	protected boolean systemEdit = false;
	protected boolean showVerified = false;

	protected ContractorAudit conAudit;

	private Map<Integer, AuditData> hasManual;
	protected Map<AuditCategory, AuditCatData> categories = null;
	protected ArrayListMultimap<Integer, WorkflowStep> caoSteps = ArrayListMultimap.create();
	protected ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap.create();

	private List<CategoryNode> categoryNodes;

	public String execute() throws Exception {
		this.findConAudit();
		return SUCCESS;
	}

	protected void findConAudit() throws RecordNotFoundException, NoRightsException {
		conAudit = auditDao.find(auditID);
		if (conAudit == null)
			throw new RecordNotFoundException("Audit " + this.auditID);

		contractor = conAudit.getContractorAccount();
		id = contractor.getId();
		if (permissions.isContractor() && id != permissions.getAccountId())
			throw new NoRightsException("Contractors can only view their own audits");

		if (!checkPermissionToView())
			throw new NoRightsException("No Rights to View this Contractor");

		if (!conAudit.isVisibleTo(permissions))
			throw new NoRightsException(conAudit.getAuditType().getName().toString());

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
			if (permissions.isOperatorCorporate() && !conAudit.getAuditType().isDesktop()) {
				auditCategoryRuleCache.initialize(auditRuleDAO);
				AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);

				Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
				if (permissions.isCorporate()) {
					for (Facility facility : getOperatorAccount().getOperatorFacilities()) {
						operators.add(facility.getOperator());
					}
				} else
					operators.add(getOperatorAccount());

				requiredCategories = builder.calculate(conAudit, operators);
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
		Map<Integer, AuditData> answers = auditDataDao.findAnswersForSafetyManual(
				conAudit.getContractorAccount().getId(), questionID);
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
			// Even though Resubmit is a valid Status for Flags, an audit in
			// Resubmit still needs to be completed and verified,
			// so we still need to show the % complete
			showVerified = true;
			if (!cao.getStatus().isSubmittedResubmitted() || cao.getStatus() == AuditStatus.Resubmit) {
				showVerified = false;
				break;
			}

		}
		return viewableCaos;
	}

	public Set<ContractorAuditOperatorPermission> getViewableCaops(ContractorAuditOperator cao) {
		Set<ContractorAuditOperatorPermission> caops = new HashSet<ContractorAuditOperatorPermission>();

		for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
			if (permissions.isOperatorCorporate()) {
				if (permissions.getVisibleAccounts().contains(caop.getOperator().getId()))
					caops.add(caop);
			} else {
				caops.add(caop);
			}
		}
		return caops;
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
			for (Iterator<Entry<AuditStatus, Collection<Integer>>> en = actionStatus.asMap().entrySet().iterator(); en.hasNext();) {
				if (!(en.next().getValue().size() > 1))
					en.remove();
			}
		}
	}

	public boolean canPerformAction(ContractorAuditOperator cao, WorkflowStep workflowStep) {
		if (cao.getPercentComplete() < 100) {
			if (cao.getPercentVerified() < 100)
				// This is confusing...We need to document this better
				return false;
			if (!cao.getStatus().isSubmitted())
				// Explain this in English...
				return false;
		}

		AuditType type = conAudit.getAuditType();
		AuditStatus newStatus = workflowStep.getNewStatus();

		if (newStatus.isComplete() && type.getWorkFlow().isHasSubmittedStep() && cao.getPercentVerified() < 100)
			return false;

		// admins can perform any action
		if (permissions.seesAllContractors()) {
			// Pics employees can't approve/reject policies from operators that
			// don't autoapprove
			if (newStatus.isApproved()) {
				if (type.getClassType().isPolicy() && !cao.getOperator().isAutoApproveInsurance())
					return false;
			}
			return true;
		}
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
			if (newStatus.isSubmitted())
				return true;
			// contractor can always move to resubmitted
			if (newStatus.isResubmitted())
				return true;
			// if Single Step Workflow (Pending to Complete)
			if (newStatus.isComplete() && workflowStep.getWorkflow().getId() == 1)
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
		AuditType type = conAudit.getAuditType();

		if (type.getClassType().isPolicy() && permissions.isAdmin())
			return true;
		if (conAudit.isExpired())
			return false;

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

		if (permissions.hasPermission(OpPerms.ImportPQF) && type.isPqf())
			return true;

		if (permissions.seesAllContractors())
			return true;

		if (permissions.isContractor()) {
			boolean canEdit = type.isCanContractorEdit();
			if (conAudit.getAuditType().getWorkFlow().getId() == 5
					|| conAudit.getAuditType().getWorkFlow().getId() == 3
					|| conAudit.getAuditType().getId() == AuditType.PQF_SUNCOR) {
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

			if (type.getClassType().isAudit() && !type.isAnnualAddendum()) {
				if (conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
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

	public boolean isShowVerifiedBar(ContractorAuditOperator cao) {
		if (conAudit.getAuditType().isAnnualAddendum()) {
			return false;
		} else {
			if (cao.getStatus().isSubmittedResubmitted())
				return true;
			else
				return false;
		}
	}

	public boolean isShowCompleteBar(ContractorAuditOperator cao) {
		if (conAudit.getAuditType().isAnnualAddendum()) {
			if (conAudit.hasCaoStatusBefore(AuditStatus.Submitted) || conAudit.hasCaoStatus(AuditStatus.Resubmit))
				return true;
			else
				return false;
		}
		if (cao.getStatus().before(AuditStatus.Complete))
			return true;
		else
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
		if (conAudit.getAuditType().getWorkFlow().isHasRequirements())
			return conAudit.hasCaoStatusAfter(AuditStatus.Incomplete);
		return false;
	}

	public boolean isCanSchedule() {
		if (conAudit.getAuditType().isScheduled() && (permissions.isContractor() || permissions.isAdmin()))
			return conAudit.hasCaoStatus(AuditStatus.Pending);
		return false;
	}

	protected void autoExpireOldAudits(ContractorAudit conAudit, AuditStatus status) {
		if (status.isSubmitted() && (conAudit.getAuditType().isDesktop() || conAudit.getAuditType().isImplementation())) {
			for (ContractorAudit ca : conAudit.getContractorAccount().getAudits()) {
				if (isMatchingOldAudit(conAudit, ca)) {
					// Expire the previous audit that is Pending or Submitted
					ca.setExpiresDate(new Date());
					conAuditDAO.save(ca);
				}
			}
		}
	}

	private boolean isMatchingOldAudit(ContractorAudit conAudit, ContractorAudit ca) {
		if (!ca.getAuditType().equals(conAudit.getAuditType()))
			return false;
		if (ca.getId() == conAudit.getId())
			return false;
		if (ca.isExpired())
			return false;
		if (ca.getEffectiveDate().after(conAudit.getEffectiveDate()))
			return false;
		if (ca.hasCaoStatus(AuditStatus.Complete))
			return false;
		return true;
	}

	protected void auditSetExpiresDate(ContractorAuditOperator cao, AuditStatus status) {
		if (status.isSubmittedResubmitted()) {
			if (cao.getAudit().getExpiresDate() == null)
				cao.getAudit().setExpiresDate(getAuditExpirationDate());
			else if (cao.getAudit().getAuditType().isRenewable())
				cao.getAudit().setExpiresDate(getAuditExpirationDate());
		}
		if (!cao.getAudit().getAuditType().getWorkFlow().isHasSubmittedStep())
			cao.getAudit().setExpiresDate(getAuditExpirationDate());
	}

	protected Date getAuditExpirationDate() {
		Integer months = conAudit.getAuditType().getMonthsToExpire();
		if (months == null) {
			// check months first, then do date if empty
			return DateBean.getMarchOfNextYear(new Date());
		} else if (months > 0) {
			if (conAudit.getAuditType().getClassType().isPqf())
				return DateBean.getMarchOfThatYear(DateBean.addMonths(new Date(), months));
			else
				return DateBean.addMonths(new Date(), months);
		} else {
			return null;
		}
	}

	protected void setCaoUpdatedNote(AuditStatus prevStatus, ContractorAuditOperator cao) {
		setCaoUpdatedNote(prevStatus, cao, null);
	}

	protected void setCaoUpdatedNote(AuditStatus prevStatus, ContractorAuditOperator cao, String noteBody) {
		if (prevStatus != cao.getStatus()) {
			// Stamping cao workflow
			ContractorAuditOperatorWorkflow caoW = new ContractorAuditOperatorWorkflow();
			Note newNote = new Note();
			newNote.setAccount(cao.getAudit().getContractorAccount());
			newNote.setAuditColumns(permissions);
			String summary = "Changed Status for " + cao.getAudit().getAuditType().getName().toString() + "("
					+ cao.getAudit().getId() + ") ";
			if (!Strings.isEmpty(cao.getAudit().getAuditFor()))
				summary += " for " + cao.getAudit().getAuditFor();
			summary += " from " + prevStatus + " to " + cao.getStatus();
			newNote.setSummary(summary);

			if (cao.getAudit().getAuditType().getClassType().isPolicy())
				newNote.setNoteCategory(NoteCategory.Insurance);
			else
				newNote.setNoteCategory(NoteCategory.Audits);

			newNote.setViewableBy(cao.getOperator());

			if (noteBody == null)
				noteBody = summary;
			newNote.setBody(noteBody);
			caoW.setNotes(noteBody);
			noteDAO.save(newNote);

			caoW.setCao(cao);
			caoW.setAuditColumns(permissions);
			caoW.setPreviousStatus(prevStatus);
			caoW.setStatus(cao.getStatus());
			caoDAO.save(caoW);
		}
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

	public boolean matchesType(int categoryId, OshaType oa) {
		if (OshaTypeConverter.getTypeFromCategory(categoryId) == null || oa == null)
			return false;
		if (oa == OshaTypeConverter.getTypeFromCategory(categoryId))
			return true;
		return false;
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

	public void setNoteDAO(NoteDAO noteDAO) {
		this.noteDAO = noteDAO;
	}

	public void setCaoDAO(ContractorAuditOperatorDAO caoDAO) {
		this.caoDAO = caoDAO;
	}

	class CategoryNode {
		public AuditCategory category;
		public List<CategoryNode> subCategories;
		public int total;
		public int answered;
		public int verified;
		public boolean override = false;

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
				node.override = getCategories().get(cat).isOverride();
				if (conAudit.getAuditType().getClassType().isIm()) {
					node.total = (int) getCategories().get(cat).getScorePossible();
					node.verified = (int) (getCategories().get(cat).getScore());
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
				node.override = getCategories().get(cat).isOverride();
				node.subCategories = createCategoryNodes(cat.getSubCategories(), true);
				nodes.add(node);
			}
		}
		return nodes;
	}

	public void setCaowDAO(ContractorAuditOperatorWorkflowDAO caowDAO) {
		this.caowDAO = caowDAO;
	}

	public boolean isEveryCAOCompleteOrHigher() {
		boolean allComplete = true;
		for (ContractorAuditOperator cao : conAudit.getViewableOperators(permissions)) {
			if (!cao.getStatus().after(AuditStatus.Resubmitted))
				allComplete = false;
		}

		return allComplete;
	}

	public boolean isCanEditCategory(AuditCategory category) throws RecordNotFoundException, NoRightsException {
		if (permissions.isContractor() && category.getAuditType().getId() == 100 && category.getParent() != null)
			return false;

		if (conAudit == null) {
			findConAudit();
		}

		if (!conAudit.getAuditType().getClassType().isPolicy())
			return true;

		if (conAudit.getOperatorsVisible().size() == 1
				&& conAudit.getOperatorsVisible().get(0).hasCaop(permissions.getAccountId()))
			return true;

		if (category.isPolicyInformationCategory() || category.isPolicyLimitsCategory()) {
			if (conAudit.hasCaoStatusAfter(AuditStatus.Pending) && !permissions.isAdmin())
				return false;
		}

		return true;
	}
}
