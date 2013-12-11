package com.picsauditing.actions.audits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.picsauditing.access.OpType;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.models.audits.AuditEditModel;
import com.picsauditing.util.EmailAddressUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ArrayListMultimap;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.ContractorAuditCategories;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.util.Strings;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class AuditActionSupport extends ContractorActionSupport {
    @Autowired
	protected AuditCategoryDataDAO catDataDao;
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
	private ContractorAuditDAO conAuditDAO;
	@Autowired
	private AuditEditModel auditEditModel;
	@Autowired
	private EmailSender emailSender;


	protected int auditID = 0;
	protected int categoryID = 0;
	protected String descriptionOsMs;
	protected boolean systemEdit = false;
	protected boolean showVerified = false;
	protected boolean showCaoTable = true;
	protected boolean showUploadRequirementsBanner = false;
	boolean refreshAudit = false;

	protected ContractorAudit conAudit;

	private Map<Integer, AuditData> hasManual;
	protected Map<AuditCategory, AuditCatData> categories = null;
	protected ArrayListMultimap<Integer, WorkflowStep> caoSteps = ArrayListMultimap.create();
	protected ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap.create();

	protected List<CategoryNode> categoryNodes;

	String professionalLabel = "";

	public String execute() throws Exception {
		this.findConAudit();

		return SUCCESS;
	}

	public String emailReminder() throws Exception {
		this.findConAudit();

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(EmailTemplate.REMINDER_EMAIL_TEMPLATE);
		emailBuilder.setPermissions(permissions);
		emailBuilder.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME);
		emailBuilder.setContractor(contractor, OpPerms.ContractorSafety);
		emailBuilder.setConAudit(conAudit);
		EmailQueue email = emailBuilder.build();
		email.setSubjectViewableById(Account.EVERYONE);
		email.setBodyViewableById(Account.EVERYONE);
		emailSender.send(email);
		String note = "PQF/Annual Update reminder email sent to " + emailBuilder.getSentTo();
		addNote(contractor, note, NoteCategory.Audits);

		addActionMessage("The PQF/Annual Update reminder email was sent and the contractor notes were stamped");
		return SUCCESS;
	}

	protected void findConAudit() throws RecordNotFoundException, NoRightsException {
		conAudit = auditDao.find(auditID);
		if (conAudit == null) {
			throw new RecordNotFoundException("Audit " + this.auditID);
		}

		if (conAudit != null) {
			checkContractorAuditPermissions(conAudit);
			professionalLabel = getProfessionalLabelText(conAudit.getAuditType().getAssigneeLabel());
			calculateRefreshAudit();
			showUploadRequirementsBanner = getShowUploadRequirementsBanner(conAudit);
		}
	}

	String getProfessionalLabelText(String assigneeLabel) {
		if (Strings.isEmpty(assigneeLabel)) {
			return getText("Assignee.SafetyProfessional");
		} else {
			return getText("Assignee." + assigneeLabel);
		}
	}

	protected void checkContractorAuditPermissions(ContractorAudit conAudit) throws NoRightsException {
		contractor = conAudit.getContractorAccount();
		id = contractor.getId();

		if (permissions.isContractor() && id != permissions.getAccountId()) {
			throw new NoRightsException("Contractors can only view their own audits");
		}

		if (!checkPermissionToView()) {
			throw new NoRightsException("No Rights to View this Contractor");
		}

		if (!conAudit.isVisibleTo(permissions)) {
			throw new NoRightsException(conAudit.getAuditType().getName());
		}
	}

	boolean getShowUploadRequirementsBanner(ContractorAudit conAudit) {
		List<Integer> list = Arrays.asList(2, 3, 5, 6, 17, 29, 72, 82, 96, 100, 176, 313);
		return list.contains(conAudit.getAuditType().getId());
	}

	void calculateRefreshAudit() {
		if (conAudit.getAuditType().getWorkFlow().isUseStateForEdit()) {
			refreshAudit = true;
		}

		if (conAudit.getAuditType().getClassType().isPolicy() && !conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)) {
			refreshAudit = true;
		}
	}

	public boolean isShowEmailReminder() {
		if (!permissions.isAdmin())
			return false;

		if ((conAudit.getAuditType().isPicsPqf() || conAudit.getAuditType().isAnnualAddendum())
				&& conAudit.hasCaoStatus(AuditStatus.Pending))
			return true;
		return false;
	}

	public boolean isRefreshAudit() {
		return refreshAudit;
	}

	public void setRefreshAudit(boolean refreshAudit) {
		this.refreshAudit = refreshAudit;
	}

	public boolean isShowUploadRequirementsBanner() {
		return showUploadRequirementsBanner;
	}

	public void setShowUploadRequirementsBanner(boolean showUploadRequirementsBanner) {
		this.showUploadRequirementsBanner = showUploadRequirementsBanner;
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
				AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);

				List<OperatorAccount> contractorOperators = new ArrayList<OperatorAccount>();
				for (ContractorOperator co:conAudit.getContractorAccount().getOperators()) {
					contractorOperators.add(co.getOperatorAccount());
				}
				Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
				if (permissions.isCorporate()) {
					for (Facility facility : getOperatorAccount().getOperatorFacilities()) {
						if (contractorOperators.contains(facility.getOperator()))
							operators.add(facility.getOperator());
					}
				} else {
					operators.add(getOperatorAccount());
				}

				requiredCategories = builder.calculate(conAudit, operators);
			}

			categories = ContractorAuditCategories.getApplicableCategories(permissions, requiredCategories, conAudit.getCategories());
		}
		return categories;
	}

	public boolean isHasSafetyManual() {
		hasManual = getDataForSafetyManual();
		if (hasManual == null || hasManual.size() == 0) {
			return false;
		}
		return true;
	}

	public Map<Integer, AuditData> getDataForSafetyManual() {
		int questionID = AuditQuestion.MANUAL_PQF;
		if (conAudit.getAuditType().getId() == AuditType.BPIISNCASEMGMT) {
			questionID = 3477;
		}
		Map<Integer, AuditData> answers = auditDataDAO.findAnswersForSafetyManual(conAudit.getContractorAccount()
				.getId(), questionID);
		if (answers == null || answers.size() == 0) {
			return null;
		}
		return answers;
	}

	public Map<Integer, AuditData> getSafetyManualLink() {
		if (hasManual != null) {
			return hasManual;
		} else {
			hasManual = getDataForSafetyManual();
		}
		return hasManual;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public List<ContractorAuditOperator> getViewableOperators(Permissions permissions) {
		List<ContractorAuditOperator> viewableCaos = new ArrayList<ContractorAuditOperator>();
		if (systemEdit && !permissions.isOperatorCorporate()) {
			viewableCaos = conAudit.getSortedOperators();
		} else {
			viewableCaos = conAudit.getViewableOperators(permissions);
		}
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
				if (permissions.getVisibleAccounts().contains(caop.getOperator().getId())) {
					caops.add(caop);
				}
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
			for (Iterator<Entry<AuditStatus, Collection<Integer>>> en = actionStatus.asMap().entrySet().iterator(); en
					.hasNext();) {
				if (!(en.next().getValue().size() > 1)) {
					en.remove();
				}
			}
		}
	}

	public boolean canPerformAction(ContractorAuditOperator cao, WorkflowStep workflowStep) {
		if (cao.getPercentComplete() < 100) {
			if (cao.getPercentVerified() < 100) {
				// This is confusing...We need to document this better
				return false;
			}
			if (!cao.getStatus().isSubmitted()) {
				// Explain this in English...
				return false;
			}
		}

		AuditType type = conAudit.getAuditType();
		AuditStatus newStatus = workflowStep.getNewStatus();

		if (newStatus.isComplete() && type.getWorkFlow().isHasSubmittedStep() && cao.getPercentVerified() < 100) {
			return false;
		}

		// admins can perform any action
		if (permissions.seesAllContractors()) {
			return true;
		}
		// operator and corporate can also perform any action if they have
		// permission
		if (permissions.isOperatorCorporate()) {
			if (type.getWorkFlow().isUseStateForEdit()) {
				AuditEditModel model = new AuditEditModel();
				return model.isCanEditAudit(conAudit, permissions);
			}
			if (type.getEditPermission() != null) {
				return permissions.hasPermission(type.getEditPermission());
			} else if (type.getEditAudit() != null) {
                Set<Integer> groupIds = permissions.getAllInheritedGroupIds();
                return groupIds.contains(type.getEditAudit().getId());
            }
		}
		// contractor can perform only submits and complete for pqf specific's
		// if they can edit that audit
		if (permissions.isContractor() && type.isCanContractorEdit()) {
            if (type.getWorkFlow().isUseStateForEdit()) {
                AuditEditModel model = new AuditEditModel();
                return model.isCanEditAudit(conAudit, permissions);
            }
			if (newStatus.isSubmitted()) {
				return true;
			}
			// contractor can always move to resubmitted
			if (newStatus.isResubmitted()) {
				return true;
			}
			// if Single Step Workflow (Pending to Complete)
			if (newStatus.isComplete() && workflowStep.getWorkflow().getId() == 1) {
				return true;
			}
		}
		// Auditor for this audit can perform all actions
		if (conAudit.getAuditor() != null && conAudit.getAuditor().getId() == permissions.getUserId()) {
			return true;
		}
		return false;
	}

	public List<WorkflowStep> getCurrentCaoStep(int caoID) {
		if (caoSteps == null || caoSteps.isEmpty()) {
			getValidSteps();
		}

		return caoSteps.get(caoID);
	}

	public boolean displayMultiStatusDropDown() {
        if (!permissions.hasGroup(User.GROUP_CSR))
            return false;
        if (CollectionUtils.isEmpty(contractor.getTrades()))
            return false;
        return (actionStatus.size() > 0);
    }

	public boolean displayButton(ContractorAuditOperator cao, WorkflowStep step) {
		if (cao != null && step != null) {
			if (conAudit.getAuditType().isCorIecWaState() && !permissions.isAdmin()
					&& step.getNewStatus().isResubmitted()) {
				return false;
			}

			if (!canContractorSubmitPQF(step)) {
				return false;
			}
		}

		return true;
	}

	public String getProfessionalLabel() {
		return professionalLabel;
	}

	private boolean canContractorSubmitPQF(WorkflowStep step) {
		if (step.getNewStatus().isSubmitted() && !permissions.hasGroup(10)
				&& CollectionUtils.isEmpty(contractor.getTrades())) {
			return false;
		}

		return true;
	}

	protected boolean atLeastOneCompleteVisibleCao() {
		List<ContractorAuditOperator> visibleCaos = getViewableOperators(permissions);
		for (ContractorAuditOperator cao : visibleCaos) {
			if (cao.isReadyToBeSubmitted()) {
				return true;
			}
		}

		return false;
	}

	public Collection<AuditStatus> getValidStatuses(int caoID) {
		Collection<AuditStatus> validStatuses = new HashSet<AuditStatus>();
		for (ContractorAuditOperator cao : conAudit.getSortedOperators()) {
			if (cao.getId() == caoID) {
				for (WorkflowStep wfs : cao.getAudit().getAuditType().getWorkFlow().getSteps()) {
					validStatuses.add(wfs.getNewStatus());
				}
			}
		}
		return validStatuses;
	}

	public Collection<AuditSubStatus> getAuditSubStatuses() {
		return new ArrayList<AuditSubStatus>(Arrays.asList(AuditSubStatus.values()));
	}

	public boolean isUserPermittedToAssignAudit() {
		if (!conAudit.getAuditType().isHasAuditor())
			return false;
		if (conAudit.isExpired())
			return false;
		if (conAudit.hasCaoStatus(AuditStatus.Complete))
			return false;
		if (permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit)
				&& (conAudit.getAuditType().getAssignAudit() == null
				|| permissions.getAllInheritedGroupIds().contains(conAudit.getAuditType().getAssignAudit().getId())))
			return true;
		return false;
	}

	public boolean isCanEditAudit() {
		return auditEditModel.isCanEditAudit(conAudit, permissions);
	}

	public boolean isShowCaoTable() {
		return showCaoTable;
	}

	public void setShowCaoTable(boolean showCaoTable) {
		this.showCaoTable = showCaoTable;
	}

	public ArrayListMultimap<AuditStatus, Integer> getActionStatus() {
		return actionStatus;
	}

	public boolean isCanSystemEdit() {
		if (permissions.hasPermission(OpPerms.AuditEdit) && permissions.isAdmin()) {
			return true;
		}

		if (conAudit.getAuditType().getClassType().isPolicy()) {
			if (conAudit.getAuditor() != null && (conAudit.getAuditor().getId() == permissions.getUserId())) {
				return true;
			}
		}

		return false;
	}

	public boolean isCanEditCao() {
		if (permissions.hasPermission(OpPerms.CaoEdit)) {
			return true;
		}
		return false;
	}

	public boolean isCanEditCao(ContractorAuditOperator cao) {
		if (isCanEditCao()) {
			if (permissions.isAdmin()) {
				return true;
			}
			if (permissions.isOperatorCorporate() && conAudit.getAuditType().getClassType().isPolicy()) {
				return !cao.getOperator().isInPicsConsortium();
			}
		}
		return false;
	}

	public boolean isShowVerifiedBar(ContractorAuditOperator cao) {
		if (conAudit.getAuditType().isAnnualAddendum()) {
			return false;
		} else {
			if (cao.getStatus().isSubmittedResubmitted()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean isShowCompleteBar(ContractorAuditOperator cao) {
		if (cao.getStatus().before(AuditStatus.Complete)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isCanVerifyAudit() {
		if (!permissions.isAuditor() && !permissions.hasGroup(959)) {
			return false;
		}

		if (!conAudit.getAuditType().getWorkFlow().isHasSubmittedStep()) {
			return false;
		}

		if (conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)) {
			return true;
		}

		return false;
	}

	public boolean isCanVerifyPqf() {
		if (!permissions.hasPermission(OpPerms.AuditVerification)) {
			return false;
		}

		if (!conAudit.getAuditType().isPicsPqf() && !conAudit.getAuditType().isAnnualAddendum()) {
			return false;
		}

		if (conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)) {
			return true;
		}

		return false;
	}

	public boolean isCanPreview() {
		return conAudit.hasCaoStatus(AuditStatus.Pending);
	}

	public boolean isCanViewRequirements() {
		if (conAudit.getAuditType().getWorkFlow().isHasRequirements()) {

			if (conAudit.getAuditType().getId() == AuditType.COR) {
				return conAudit.hasCaoStatusAfter(AuditStatus.Pending);
			} else if (conAudit.getAuditType().getId() == AuditType.SSIP) {
				return conAudit.hasCaoStatusAfter(AuditStatus.Resubmit);
			} else {
				return conAudit.hasCaoStatusAfter(AuditStatus.Incomplete);
			}
		}

		return false;
	}

	public boolean isCanSchedule() {
		if (conAudit.getAuditType().isScheduled() && (permissions.isContractor() || permissions.isAdmin())) {
			return conAudit.hasCaoStatus(AuditStatus.Pending);
		}
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
		if (!ca.getAuditType().equals(conAudit.getAuditType())) {
			return false;
		}
		if (ca.getId() == conAudit.getId()) {
			return false;
		}
		if (ca.isExpired()) {
			return false;
		}
		if (ca.getEffectiveDate().after(conAudit.getEffectiveDate())) {
			return false;
		}
		if (ca.hasCaoStatus(AuditStatus.Complete)) {
			return false;
		}
		return true;
	}

	protected void auditSetExpiresDate(ContractorAuditOperator cao, AuditStatus status) {
		if (cao.getAudit().getAuditType().isWCB()) {
			return;
		}

		if (status.isSubmittedResubmitted()) {
			if (cao.getAudit().getExpiresDate() == null) {
				cao.getAudit().setExpiresDate(getAuditExpirationDate());
			} else if (cao.getAudit().getAuditType().isRenewable()) {
				cao.getAudit().setExpiresDate(getAuditExpirationDate());
			}
		}
		if (!cao.getAudit().getAuditType().getWorkFlow().isHasSubmittedStep()) {
			AuditType auditType = cao.getAudit().getAuditType();
			if (cao.getAudit().getExpiresDate() == null || auditType.isRenewable()) {
				Date expirationDate = getAuditExpirationDate();
				if (!auditType.isRenewable() && auditType.getClassType().isPqf()) {
					for (ContractorAudit conAudit : cao.getAudit().getContractorAccount().getAudits()) {
						if (conAudit.getAuditType().getId() == auditType.getId() && conAudit.getExpiresDate() != null) {
							if (conAudit.getExpiresDate().compareTo(expirationDate) <= 0) {
								expirationDate = DateBean.setToEndOfDay(DateBean.getMarchOfNextYear(conAudit
										.getExpiresDate()));
							}
						}
					}
				}

				cao.getAudit().setExpiresDate(expirationDate);
			}
		}
	}

	protected Date getAuditExpirationDate() {
		Integer months = conAudit.getAuditType().getMonthsToExpire();
		if (months == null) {
			// check months first, then do date if empty
			return DateBean.setToEndOfDay(DateBean.getMarchOfNextYear(new Date()));
		} else if (months > 0) {
			if (conAudit.getAuditType().getClassType().isPqf()) {
				return DateBean.setToEndOfDay(DateBean.getMarchOfThatYear(DateBean.addMonths(new Date(), months)));
			} else {
				return DateBean.setToEndOfDay(DateBean.addMonths(new Date(), months));
			}
		} else {
			return null;
		}
	}

	protected void updateCaoWorkflow(AuditStatus prevStatus, ContractorAuditOperator cao, String noteBody) {
		if (prevStatus != cao.getStatus()) {
			// Stamping cao workflow
			ContractorAuditOperatorWorkflow caoW = new ContractorAuditOperatorWorkflow();
			Note newNote = new Note();
			newNote.setAccount(cao.getAudit().getContractorAccount());
			newNote.setAuditColumns(permissions);
			String summary = "Changed Status for " + cao.getAudit().getAuditType().getName().toString() + "("
					+ cao.getAudit().getId() + ") ";
			if (!Strings.isEmpty(cao.getAudit().getAuditFor())) {
				summary += " for " + cao.getAudit().getAuditFor();
			}
			summary += " from " + prevStatus + " to " + cao.getStatus();
			newNote.setSummary(summary);

			if (cao.getAudit().getAuditType().getClassType().isPolicy()) {
				newNote.setNoteCategory(NoteCategory.Insurance);
			} else {
				newNote.setNoteCategory(NoteCategory.Audits);
			}

			newNote.setViewableBy(cao.getOperator());

			if (!Strings.isEmpty(noteBody)) {
				caoW.setNotes(noteBody);

				// since the note is stored in a separate table, forget about
				// doing extra work to give the
				// user a correct view to edit the Reason Codes in the note,
				// just throw in the mapped note.
				newNote.setBody(caoW.getMappedNote());
				noteDAO.save(newNote);
			}

			caoW.setCao(cao);
			caoW.setAuditColumns(permissions);
			caoW.setPreviousStatus(prevStatus);
			caoW.setStatus(cao.getStatus());
			caoDAO.save(caoW);
		}
	}

	public boolean isAppliesSubCategory(AuditCategory auditCategory) {
		if (categories.get(auditCategory).isApplies()) {
			return true;
		}
		if (!categories.get(auditCategory.getParent()).isApplies()) {
			return true;
		}
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

	public boolean isSystemEdit() {
//        if (permissions.hasPermission(OpPerms.CaoEdit)) {
//            return true;
//        }
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
			if (total == 0 || percent > 100) {
				return 100;
			}
			return percent;
		}

		public float getPercentVerified() {
			int percent = (int) ((verified * 1f / total) * 100);
			if (total == 0 || percent > 100) {
				return 100;
			}
			return percent;
		}
	}

	public Set<AuditCategory> getCategoriesFromCategoryNodes(List<CategoryNode> nodes) {
		Set<AuditCategory> categories = new HashSet<AuditCategory>();
		for (CategoryNode node : nodes) {
			categories.add(node.category);
		}
		return categories;
	}

	public List<CategoryNode> getCategoryNodes() {
		if (categoryNodes == null) {
			categoryNodes = createCategoryNodes(conAudit.getAuditType().getTopCategories());
		}

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
			if (!cao.getStatus().after(AuditStatus.Resubmitted)) {
				allComplete = false;
			}
		}

		return allComplete;
	}

	/**
	 * This method is used to determine if a user has the ability to edit a
	 * category.
	 *
	 * @param category
	 *
	 * @return
	 */
	public boolean isCanEditCategory(AuditCategory category) throws RecordNotFoundException, NoRightsException {
        if (conAudit == null) {
            findConAudit();
        }

        if (conAudit == null) {
            return false;
        }

        return auditEditModel.isCanEditCategory(category, conAudit, permissions, auditCategoryRuleCache);
	}

	public boolean isHasClosingAuditor() {
		if (conAudit.getAuditType().isPicsPqf() ||
				conAudit.getAuditType().isAnnualAddendum() ||
				conAudit.getAuditType().getClassType().isPolicy())
			return false;
		return conAudit.getAuditType().isHasAuditor();
	}
	
	public String getScoreLastUpdated() {
		List<AuditData> sortedList = conAudit.getData();
		Collections.sort(sortedList, new Comparator<AuditData>(){
			@Override
			public int compare(AuditData a1, AuditData a2) {
				return a2.getUpdateDate().compareTo(a1.getUpdateDate());
			}
		});

		Date lastUpdate = sortedList.get(0).getUpdateDate();

		return new SimpleDateFormat("yyyy-MM-dd").format(lastUpdate);
	}

}
