package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.actions.converters.OshaTypeConverter;
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
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.log.PicsLogger;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also
 * allows users to change the status of an audit.
 */
@SuppressWarnings("serial")
public class ContractorAuditController extends AuditActionSupport {

	protected String mode = null;
	static private String VIEW = "View";
	static private String EDIT = "Edit";
	static private String VERIFY = "Verify";
	protected boolean viewBlanks = true;
	protected boolean onlyReq = false;
	protected AnswerMap answerMap = null;
	protected boolean previewAudit;
	protected AuditCatData categoryData;
	private AuditCategoryDAO auditCategoryDAO;
	private AuditPercentCalculator auditPercentCalculator;
	private OshaAuditDAO oshaAuditDAO;

	public ContractorAuditController(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditCategoryDAO auditCategoryDAO,
			AuditPercentCalculator auditPercentCalculator,
			OshaAuditDAO oshaAuditDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.oshaAuditDAO = oshaAuditDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findConAudit();

		if (button != null) {
			if (categoryID > 0 && permissions.isPicsEmployee()) {
				if ("IncludeCategory".equals(button)) {
					for (AuditCatData data : conAudit.getCategories()) {
						if (data.getId() == categoryID) {
							data.setApplies(true);
							data.setOverride(true);
						}
					}
					if ("UnincludeCategory".equals(button)) {
						for (AuditCatData data : conAudit.getCategories()) {
							if (data.getId() == categoryID) {
								data.setApplies(false);
								data.setOverride(true);
							}
						}
					}
				}
				if ("recalculate".equals(button)) {

				}
				// Preview the Category from the manage audit type page
				if ("PreviewCategory".equals(button)) {
					if (auditID == 0 && categoryID > 0) {
						AuditCategory auditCategory = auditCategoryDAO
								.find(categoryID);
						for (AuditCategory auditSubCategory : auditCategory
								.getSubCategories()) {
							for (AuditQuestion auditQuestion : auditSubCategory
									.getQuestions()) {
							}
						}
						categories = new ArrayList<AuditCatData>();
						AuditCatData catData = new AuditCatData();
						catData.setCategory(auditCategory);
						catData.setApplies(true);
						categories.add(catData);
						mode = EDIT;
						PicsLogger.stop();
						return SUCCESS;
					}
				}
			}

			if (categoryID > 0) {
				categoryData = catDataDao.findAuditCatData(auditID, categoryID);
				for (AuditCatData catData : getCategories()) {
					// We can open audits using either the catID or the
					// catDataID
					if (catData.equals(categoryData)) {
						List<Integer> questionIDs = new ArrayList<Integer>();
						for (AuditCategory subCategory : catData.getCategory()
								.getSubCategories()) {
							for (AuditQuestion question : subCategory
									.getQuestions()) {
								questionIDs.add(question.getId());
							}
						}
						// Get a map of all answers in this audit
						answerMap = auditDataDao.findAnswers(catData.getAudit()
								.getId(), questionIDs);
					}
				}
				if (mode == null && isCanEditAudit())
					mode = EDIT;
			} else {
				// When we want to show all categories
				answerMap = auditDataDao.findAnswers(auditID);
			}

			if (mode == null)
				mode = VIEW;
			if (mode.equals(EDIT) && !isCanCloseAudit())
				mode = VIEW;
			if (mode.equals(VERIFY) && !isCanVerifyAudit())
				mode = VIEW;

			if (categoryData != null) {
				if (OshaTypeConverter.getTypeFromCategory(categoryData
						.getCategory().getId()) != null) {
					boolean hasOshaCorporate = false;
					for (OshaAudit osha : conAudit.getOshas()) {
						if (osha.isCorporate()
								&& matchesType(categoryData.getCategory()
										.getId(), osha.getType())) {
							hasOshaCorporate = true;
							// Calculate percent complete too
							auditPercentCalculator.percentOshaComplete(osha,
									categoryData);
						}
					}

					if (mode.equals(EDIT) && !hasOshaCorporate) {
						OshaAudit oshaAudit = new OshaAudit();
						oshaAudit.setConAudit(conAudit);
						oshaAudit.setCorporate(true);
						oshaAudit.setType(OshaTypeConverter
								.getTypeFromCategory(categoryData.getCategory()
										.getId()));
						oshaAuditDAO.save(oshaAudit);
						conAudit.getOshas().add(oshaAudit);

						categoryData.setNumRequired(2);
						catDataDao.save(categoryData);
					}
				}
			}

		}
		return SUCCESS;
	}

	public List<MenuComponent> getAuditMenu() {
		List<MenuComponent> menu = super.getAuditMenu();

		if (conAudit != null) {
			for (MenuComponent comp : menu) {
				if (comp.getAuditId() == conAudit.getId()) {
					comp.setCurrent(true);
					break;
				}

				if (comp.getChildren() != null) {
					for (MenuComponent child : comp.getChildren()) {
						if (child.getAuditId() == conAudit.getId()) {
							child.setCurrent(true);
							return menu;
						}
					}
				}
			}
		}

		return menu;
	}

	public String getAuditorNotes() {
		AuditData auditData = null;
		if (conAudit.getAuditType().isDesktop()) {
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(),
					1461);
		}
		if (conAudit.getAuditType().getId() == 3) {
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(),
					2432);
		}
		if (auditData != null)
			return auditData.getAnswer();

		return null;
	}

	public AnswerMap getAnswerMap() {
		return answerMap;
	}

	public void setAnswerMap(AnswerMap answerMap) {
		this.answerMap = answerMap;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isOnlyReq() {
		return onlyReq;
	}

	public void setOnlyReq(boolean onlyReq) {
		this.onlyReq = onlyReq;
	}

	public AuditCatData getCategoryData() {
		return categoryData;
	}

	public OshaAudit getAverageOsha(OshaType oshaType) {
		return contractor.getOshaOrganizer().getOshaAudit(oshaType,
				MultiYearScope.ThreeYearAverage);
	}

	public boolean matchesType(int categoryId, OshaType oa) {
		if (OshaTypeConverter.getTypeFromCategory(categoryId) == null
				|| oa == null)
			return false;
		if (oa == OshaTypeConverter.getTypeFromCategory(categoryId))
			return true;
		return false;
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
}
