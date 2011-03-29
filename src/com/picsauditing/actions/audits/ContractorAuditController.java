package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.icu.util.Calendar;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.util.AnswerMap;

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
	protected AuditCatData categoryData;
	private AuditCategoryDAO auditCategoryDAO;
	private AuditPercentCalculator auditPercentCalculator;
	protected int caoID;
	protected boolean previewCat = false;
	// Policy verification (next/first buttons)
	private boolean policy;

	public ContractorAuditController(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, CertificateDAO certificateDao,
			AuditCategoryDAO auditCategoryDAO, AuditPercentCalculator auditPercentCalculator,
			AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditPercentCalculator = auditPercentCalculator;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		ActionContext.getContext().getSession().remove("auditID");

		if (auditID > 0)
			this.findConAudit();

		if (button != null) {
			if (categoryID > 0 && permissions.isPicsEmployee()) {
				AuditCategory auditCategory = auditCategoryDAO.find(categoryID);
				if ("IncludeCategory".equals(button)) {
					AuditCatData auditCatData = getCategories().get(auditCategory);
					if (auditCatData != null) {
						auditCatData.setApplies(true);
						auditCatData.setOverride(true);
						auditDao.save(auditCatData);
					}
					conAudit.setLastRecalculation(null);
					contractor.incrementRecalculation();
					return SUCCESS;
				}

				if ("UnincludeCategory".equals(button)) {
					AuditCatData auditCatData = getCategories().get(auditCategory);
					if (auditCatData != null) {
						auditCatData.setApplies(false);
						auditCatData.setOverride(true);
						auditDao.save(auditCatData);
					}
					conAudit.setLastRecalculation(null);
					contractor.incrementRecalculation();
					return SUCCESS;
				}
			}

			if ("Recalculate".equals(button)) {
				auditPercentCalculator.percentCalculateComplete(conAudit, true);
				conAudit.setLastRecalculation(new Date());
				auditDao.save(conAudit);
				this.redirect("Audit.action?auditID=" + conAudit.getId());
				return SUCCESS;
			}

			if ("SubmitRemind".equals(button)) {
				for (ContractorAuditOperator cao : conAudit.getOperators()) {
					// We looking for pending
					if (cao.isVisible()) {
						if (cao.getStatus() == AuditStatus.Pending || cao.getStatus() == AuditStatus.Incomplete
								|| cao.getStatus() == AuditStatus.Resubmit) {
							if (cao.getPercentComplete() == 100) {
								json.put("remind", "Please submit your audits when finished.");
								break;
							}
						}
					}
				}
				return JSON;
			}

			// Preview the Category from the manage audit type page
			if ("PreviewCategory".equals(button)) {
				if (auditID == 0 && categoryID > 0) {

					previewCat = true;
					AuditCategory auditCategory = auditCategoryDAO.find(categoryID);
					conAudit = new ContractorAudit();
					conAudit.setAuditType(auditCategory.getAuditType());
					if (auditCategory.getAuditType().isAnnualAddendum()) {
						conAudit.setAuditFor(new Date().getYear() + "");
					}

					categories = new HashMap<AuditCategory, AuditCatData>();
					categoryData = new AuditCatData();
					categoryData.setCategory(auditCategory);
					categoryData.setApplies(true);
					categories.put(auditCategory, categoryData);
					if (mode == null)
						mode = EDIT;
					return SUCCESS;
				}
			}

			if (categoryID > 0) {
				AuditCategory auditCategory = auditCategoryDAO.find(categoryID);
				Set<Integer> questionIDs = new HashSet<Integer>();
				categoryData = getCategories().get(auditCategory);
				if (categoryData == null) {
					for (AuditCatData catdata : getCategories().values()) {
						categoryData = catdata;
						break;
					}
				}
				for (AuditCategory childCategory : categoryData.getCategory().getChildren()) {
					for (AuditQuestion question : childCategory.getQuestions()) {
						questionIDs.add(question.getId());
						if (question.getRequiredQuestion() != null)
							questionIDs.add(question.getRequiredQuestion().getId());
						if (question.getVisibleQuestion() != null)
							questionIDs.add(question.getVisibleQuestion().getId());
					}
				}
				// Get a map of all answers in this audit
				List<AuditData> requiredAnswers = new ArrayList<AuditData>();
				for (AuditData answer : conAudit.getData())
					if (questionIDs.contains(answer.getQuestion().getId()))
						requiredAnswers.add(answer);
				answerMap = new AnswerMap(requiredAnswers);
				if (mode == null && isCanEditAudit())
					mode = EDIT;
			} else {
				// When we want to show all categories
				answerMap = auditDataDao.findAnswers(auditID);
			}

			if (mode == null)
				mode = VIEW;
			if (mode.equals(EDIT) && !isCanEditAudit())
				mode = VIEW;
			if (mode.equals(VERIFY) && !isCanVerifyAudit())
				mode = VIEW;

			return SUCCESS;
		}

		if (conAudit != null){
			getValidSteps();
			if(conAudit.getOperators().size()==0)
				addAlertMessage("This audit has no valid CAOs and cannot be seen by external users.  As we do retain the audit data, the audit is still viewable by internal users");
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
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 1461);
		}
		if (conAudit.getAuditType().getId() == 3) {
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 2432);
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

	public boolean isViewBlanks() {
		return viewBlanks;
	}

	public void setViewBlanks(boolean viewBlanks) {
		this.viewBlanks = viewBlanks;
	}

	public OshaAudit getAverageOsha(OshaType oshaType) {
		return contractor.getOshaOrganizer().getOshaAudit(oshaType, MultiYearScope.ThreeYearAverage);
	}

	public boolean isCanEditCategory(AuditCategory category) {
		if (permissions.isContractor() && category.getAuditType().getId() == 100 && category.getParent() != null)
			return false;

		if (!conAudit.getAuditType().getClassType().isPolicy())
			return true;

		if (conAudit.getOperatorsVisible().size() == 1
				&& conAudit.getOperatorsVisible().get(0).hasCaop(permissions.getAccountId()))
			return true;

		if (category.getName().equals("Policy Information") || category.getName().equals("Policy Limits")) {
			if (conAudit.hasCaoStatusAfter(AuditStatus.Pending) && !permissions.isAdmin())
				return false;
		}

		return true;
	}

	public int getCaoID() {
		return caoID;
	}

	public void setCaoID(int caoID) {
		this.caoID = caoID;
	}

	public boolean isPolicy() {
		return policy;
	}

	public void setPolicy(boolean policy) {
		this.policy = policy;
	}

	public boolean isPreviewCat() {
		return previewCat;
	}

	public void setPreviewCat(boolean previewCat) {
		this.previewCat = previewCat;
	}

	public boolean isInvoiceOverdue() {
		if (!conAudit.isExpired()
				&& !conAudit.hasCaoStatus(AuditStatus.Complete)
				&& (!conAudit.getAuditType().isCanContractorEdit() && conAudit.getAuditType().getEditPermission() == null)
				|| conAudit.getAuditType().isAnnualAddendum() || conAudit.getAuditType().isPqf()) {

			for (Invoice i : this.getContractor().getInvoices()) {
				if (i.getStatus().equals(TransactionStatus.Unpaid)) {
					for (InvoiceItem ii : i.getItems()) {
						if ("Membership".equals(ii.getInvoiceFee().getFeeClass())
								&& !ii.getInvoiceFee().isBidonly()
								&& !ii.getInvoiceFee().isPqfonly()
								&& (ii.getInvoiceFee().getAmount().equals(ii.getAmount()) || i.getTotalAmount()
										.intValue() > 450))
							return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isWillExpireSoon() {
		return !conAudit.isExpired() && !conAudit.hasCaoStatus(AuditStatus.Complete)
				&& !conAudit.getAuditType().isCanContractorEdit()
				&& conAudit.getAuditType().getEditPermission() == null && !conAudit.getContractorAccount().isRenew();
	}
}
