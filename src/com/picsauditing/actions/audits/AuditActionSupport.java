package com.picsauditing.actions.audits;

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
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class AuditActionSupport extends ContractorActionSupport {

	protected int auditID = 0;
	protected ContractorAudit conAudit;
	protected AuditCategoryDataDAO catDataDao;
	protected AuditDataDAO auditDataDao;
	protected List<AuditCatData> categories;
	protected String descriptionOsMs;
	private Map<Integer, AuditData> hasManual;
	private List<AuditCategoryRule> rules = null;

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

	protected void canSeeAudit() throws NoRightsException {
		if (permissions.isPicsEmployee())
			return;
		if (permissions.isOperator() || permissions.isCorporate()) {
			if (!permissions.getCanSeeAudit().contains(
					conAudit.getAuditType().getId()))
				throw new NoRightsException(conAudit.getAuditType()
						.getAuditName());
		}
		if (permissions.isContractor()) {
			if (!conAudit.getAuditType().isCanContractorView())
				throw new NoRightsException(conAudit.getAuditType()
						.getAuditName());
		}
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

		canSeeAudit();
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

	public boolean isSingleCat() {
		return getCategories().size() == 1;
	}

	public List<AuditCatData> getCategories() {
		if (categories != null)
			return categories;
		Set<AuditCategory> requiredCategories = null;
		if (permissions.isOperatorCorporate()) {
			AuditBuilder builder = new AuditBuilder();
			Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
			OperatorAccount opAccount = (OperatorAccount) getUser()
					.getAccount();
			operators.add(opAccount);
			AuditCategoriesDetail auditCategoryDetail = builder.getDetail(
					conAudit.getAuditType(), getRules(), operators);
			requiredCategories = auditCategoryDetail.categories;
		}

		return conAudit.getApplicableCategories(permissions, requiredCategories) ;
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
			if ((type.isAnnualAddendum() || type.getId() == 99)) {
				// contractors can't modify annual updates that are already
				// verified or submitted
				if (conAudit.hasCaoStatusAfter(AuditStatus.Submitted))
					return false;
			}
			return type.isCanContractorEdit();
		}

		if (type.getEditPermission() != null)
			return permissions.hasPermission(type.getEditPermission());

		return false;
	}

	public boolean isCanSubmitAudit() {
		if (!isCanEditAudit())
			return false;

		for (ContractorAuditOperator cao : conAudit.getCurrentOperators()) {
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
		}
		return false;
	}

	public boolean isCanVerifyAudit() {
		if (!conAudit.getAuditType().getWorkFlow().isHasSubmittedStep())
			return false;

		if (conAudit.getAuditType().isPqf()
				&& conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
			if (permissions.isAuditor())
				return true;

		return false;
	}

	/**
	 * Can the current user submit this audit in its current state?
	 * 
	 * @return
	 */
	public boolean isCanCloseAudit() {
		if (permissions.isContractor())
			return false;
		if (!isCanEditAudit())
			return false;

		for (ContractorAuditOperator cao : conAudit.getCurrentOperators()) {
			if (cao.canVerifyCao()) {
				return true;
			}
		}
		if (!conAudit.getAuditType().getWorkFlow().isHasSubmittedStep())
			return false;

		return false;
	}

	@Deprecated
	/**
	 * 
	 * @return true if the current users is an operator and there is a visible
	 *         cao belonging to another operator
	 */
	public boolean isPolicyWithOtherOperators() {
		return isAuditWithOtherOperators();
	}

	/**
	 * 
	 * @return true if the current users is an operator and there is a visible
	 *         cao belonging to another operator
	 */
	public boolean isAuditWithOtherOperators() {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (!permissions.getVisibleCAOs().contains(
					cao.getOperator().getId())) {
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
				return true;
			}
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
}
