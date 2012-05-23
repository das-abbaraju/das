package com.picsauditing.actions.audits;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also allows users to change the status of an
 * audit.
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
	protected int caoID;
	protected boolean previewCat = false;
	protected Map<ContractorAuditOperator, String> problems = new TreeMap<ContractorAuditOperator, String>();
	// Policy verification (next/first buttons)
	private boolean policy;

	private int auditTypeId;
	private AuditType auditType;
	private Set<Integer> operatorIds = new TreeSet<Integer>();
	private List<OperatorAccount> operators;
	private List<AuditCategory> cats = new ArrayList<AuditCategory>();

	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	// Import PQF
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditDecisionTableDAO auditDecisionTableDAO;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (auditID > 0)
			this.findConAudit();

		if ("ViewAll".equals(mode) && auditTypeId != 0) {
			contractor.setName("Sample");
			conAudit = new ContractorAudit();
			conAudit.setContractorAccount(contractor);
			conAudit.setContractorAccount(contractor);
			auditType = auditTypeDAO.find(auditTypeId);
			conAudit.setAuditType(auditType);
			contractor.getAudits().add(conAudit);

			ContractorOperator co = new ContractorOperator();
			co.setContractorAccount(contractor);

			operators = new ArrayList<OperatorAccount>();
			for (int id : operatorIds) {
				OperatorAccount operator = (OperatorAccount) auditTypeDAO.findWhere(OperatorAccount.class,
						"id = " + id, 1).get(0);
				operators.add(operator);

				co.setOperatorAccount(operator);
				contractor.getOperators().add(co);
			}

			fillAuditCategories();

			for (AuditCategory auditCategory : cats) {
				addDefaultCategory(auditCategory);
			}
			return SUCCESS;
		}

		if (button != null) {
			if (categoryID > 0 && permissions.isPicsEmployee()) {
				AuditCategory auditCategory = (AuditCategory) catDataDao.find(AuditCategory.class, categoryID);
				if ("IncludeCategory".equals(button)) {
					AuditCatData auditCatData = addManuallyAddedCategory(auditCategory);
					auditDao.save(auditCatData);

					conAudit.setLastRecalculation(null);
					contractor.incrementRecalculation();
					return SUCCESS;
				}

				if ("UnincludeCategory".equals(button)) {
					AuditCatData auditCatData = hideCategory(auditCategory);
					auditDao.save(auditCatData);
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
							if (cao.getPercentComplete() == 100 && CollectionUtils.isNotEmpty(contractor.getTrades())) {
								json.put("remind", getText("Audit.PleaseSubmitWhenFinished"));
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
					AuditCategory auditCategory = (AuditCategory) catDataDao.find(AuditCategory.class, categoryID);
					conAudit = new ContractorAudit();
					conAudit.setAuditType(auditCategory.getAuditType());
					if (auditCategory.getAuditType().isAnnualAddendum()) {
						conAudit.setAuditFor(Calendar.getInstance().get(Calendar.YEAR) + "");
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
				AuditCategory auditCategory = (AuditCategory) catDataDao.find(AuditCategory.class, categoryID);
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

			checkMode();
			return SUCCESS;
		}

		if (conAudit != null) {
			getValidSteps();
			if (conAudit.getAuditType().getClassType().isPolicy() && conAudit.hasCaoStatus(AuditStatus.Incomplete)) {
				for (ContractorAuditOperatorWorkflow caow : caowDAO.findbyAuditStatus(conAudit.getId(),
						AuditStatus.Incomplete)) {
					if (caow.getCao().isVisible()) {
						if (permissions.isAdmin()
								|| (permissions.isContractor() && permissions.getAccountId() == conAudit
										.getContractorAccount().getId())) {
							problems.put(caow.getCao(), caow.getMappedNote());
						} else if (getViewableOperators(permissions).contains(caow.getCao())) {
							problems.put(caow.getCao(), caow.getMappedNote());
						}
					}
				}
			}
			
			String message = determineMessageForUser();			
			if (!Strings.isEmpty(message))
				addAlertMessage(message);
		}

		return SUCCESS;
	}

	private String determineMessageForUser() {
		String message = "";
		if (CollectionUtils.isEmpty(contractor.getTrades())) {
			// using Integer.toString() because the autoboxed int primitive gets a comma in the translated message, which is incorrect
			// because it is used in a URL.
			String noTradesSelectedMessage = getTextParameterized("Audit.Error.ContractorMissingTrades", Integer.toString(contractor.getId()));
			if (!atLeastOneCompleteVisibleCao()) {  
				message = noTradesSelectedMessage;
			}
			else {
				addActionError(noTradesSelectedMessage);	
			}
		}
		
		if (conAudit.getOperators().size() == 0)
			message = "This audit has no valid CAOs and cannot be seen by external users.  As we do retain the audit data, the audit is still viewable by internal users";
		if (conAudit.hasOnlyInvisibleCaos())
			message = "This audit has no visible CAOs and cannot be seen by external users.  Data is not deleted when facilities are disassociated, but it is no longer visible to external users.";
		
		return message;
	}

	/*
	 * Only use this for default categories. See also addManuallyAddedCategory()
	 */
	private void addDefaultCategory(AuditCategory category) {
		AuditCatData catData = new AuditCatData();
		catData.setCategory(category);
		catData.setAudit(conAudit);
		catData.setApplies(true);
		catData.setOverride(false);
		catData.setNumRequired(category.getNumRequired());
		conAudit.getCategories().add(catData);

		for (AuditCategory subCat : category.getSubCategories()) {
			addDefaultCategory(subCat);
		}
	}

	/*
	 * Only use this for manually added categories. See also addCatData()
	 */
	private AuditCatData addManuallyAddedCategory(AuditCategory auditCategory) {
		AuditCatData auditCatData = getCategories().get(auditCategory);
		if (auditCatData == null) {
			auditCatData = new AuditCatData();
			auditCatData.setAuditColumns(permissions);
			auditCatData.setAudit(conAudit);
			conAudit.getCategories().add(auditCatData);
		}
		auditCatData.setApplies(true);
		auditCatData.setOverride(true); // a.k.a. "Manually added"
		for (AuditCategory childCategory : auditCatData.getCategory().getChildren()) {
			if (auditCategory.getId() != childCategory.getId()) {
				addManuallyAddedCategory(childCategory);
			}
		}
		return auditCatData;
	}

	/*
	 * Hides a category (and it's sub-categories). The cetagory might be one that was manually added, or it might be one
	 * that is there by default. Either way, we'll mark it as "override" to show that it was manually manipulated.
	 */
	private AuditCatData hideCategory(AuditCategory auditCategory) {
		AuditCatData auditCatData = getCategories().get(auditCategory);
		if (auditCatData != null) {
			auditCatData.setApplies(false);
			auditCatData.setOverride(true);
			for (AuditCategory childCategory : auditCatData.getCategory().getChildren()) {
				if (auditCategory.getId() != childCategory.getId()) {
					hideCategory(childCategory);
				}
			}
		}
		return auditCatData;
	}

	private void fillAuditCategories() {
		// We're doing this step first so categories that get added or removed
		// manually can be caught in the next block

		if (auditType.getId() == AuditType.SHELL_COMPETENCY_REVIEW) {
			AuditCategory category = new AuditCategory();
			TranslatableString t = new TranslatableString();
			t.putTranslation("en", "Previewing Categories is not supported for this audit", false);
			category.setName(t);
			cats.add(category);
			return;
		}

		auditCategoryRuleCache.initialize(auditDecisionTableDAO);
		AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);

		Set<AuditCategory> requiredCategories = builder.calculate(conAudit, operators);
		for (AuditCategory category : auditType.getTopCategories()) {
			addCategory(cats, category, requiredCategories);
		}
	}

	private void addCategory(List<AuditCategory> list, AuditCategory category, Set<AuditCategory> requiredCategories) {
		if (requiredCategories.contains(category)) {
			list.add(category);
			category.setSubCategories(new ArrayList<AuditCategory>());
			for (AuditCategory subCategory : auditType.getCategories()) {
				if (category.equals(subCategory.getParent())) {
					addCategory(category.getSubCategories(), subCategory, requiredCategories);
				}
			}
		}
	}

	public String importPQFYes() throws Exception {
		int importAuditID = auditID;
		checkMode();

		if (auditID > 0) {
			findConAudit();
			return redirect("CreateImportPQFAudit.action?id=" + contractor.getId());
		} else {
			addActionError("Missing Audit ID");
		}

		return redirect("Audit.action?auditID=" + importAuditID);
	}

	public String importPQFNo() throws Exception {
		if (auditID > 0) {
			findConAudit();

			contractor.setCompetitorMembership(false);
			contractorAccountDao.save(contractor);
		}

		checkMode();
		return redirect("Audit.action?auditID=" + auditID);
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

	public boolean isHasPermissionsToSeeAuditLinks() {
		if (isContractorAdminOrSafety()) {
			return true;
		}

		if (permissions.isPicsEmployee()) {
			return true;
		}

		return false;
	}

	private boolean isContractorAdminOrSafety() {
		if (permissions.isContractor()) {
			return (permissions.hasPermission(OpPerms.ContractorAdmin) || permissions
					.hasPermission(OpPerms.ContractorSafety));
		}

		return false;
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

			if (contractor.getOperators().size() <= 9) {
				for (Invoice i : this.getContractor().getInvoices()) {
					if (i.isOverdue()) {
						for (InvoiceItem ii : i.getItems()) {
							if ("Membership".equals(ii.getInvoiceFee().getFeeClass())
									&& !ii.getInvoiceFee().isBidonly()
									&& !ii.getInvoiceFee().isPqfonly()
									&& (contractor.getCountry().getAmount(ii.getInvoiceFee()).equals(ii.getAmount()) || i
											.getTotalAmount().intValue() > 450))
								return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean isWillExpireSoon() {
		return !conAudit.isExpired() && !conAudit.hasCaoStatus(AuditStatus.Complete)
				&& !conAudit.getAuditType().isCanContractorEdit()
				&& conAudit.getAuditType().getEditPermission() == null && !conAudit.getContractorAccount().isRenew()
				&& conAudit.getContractorAccount().willExpireSoon(30) && permissions.isPicsEmployee();
	}

	/**
	 * This yes/no question only appears on the PQF audit itself. If they answer yes to this, generate an Import Fee
	 * invoice. <br/>
	 * <br/>
	 * Check whether the contractor answered the Competitor Membership question. If there is no answer, show this
	 * message. If they've answered yes but the Import PQF audit/fee doesn't exist, show this message.
	 * 
	 * @return true if they should see the upsell message
	 */
	public boolean isNeedsImportPQFQuestion() {
		if (!conAudit.getAuditType().isPqf() || conAudit.hasCaoStatusAfter(AuditStatus.Pending))
			return false;

		ContractorAccount con = conAudit.getContractorAccount();
		if (con.getCompetitorMembership() != null) {
			// They answered yes to this question
			if (con.getCompetitorMembership()) {
				// TODO: We might need to clean this logic up later. The import fee is a one time charge. If there is a
				// contractor fee with the Import Fee class, then I'm assuming that they've been charged for this
				// before.
				if (con.getFees().get(FeeClass.ImportFee) != null
						&& !con.getFees().get(FeeClass.ImportFee).getNewLevel().isFree())
					return false;

				for (ContractorAudit importAudit : con.getAudits()) {
					if (importAudit.getAuditType().getId() == AuditType.IMPORT_PQF
							&& importAudit.hasCaoStatusBefore(AuditStatus.NotApplicable))
						return false;
				}
			} else
				return false;
		}

		for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
			if (cao.getPercentComplete() > 50)
				return false;
		}

		return true;
	}

	public BigDecimal getImportPQFFeeAmount() throws Exception {
		findContractor();
		InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 1);
		return contractor.getCountry().getAmount(fee);
	}
	
	

	private void checkMode() {
		if (mode == null)
			mode = VIEW;
		if (mode.equals(EDIT) && !isCanEditAudit())
			mode = VIEW;
		if (mode.equals(VERIFY) && !isCanVerifyAudit())
			mode = VIEW;
	}

	public Map<ContractorAuditOperator, String> getProblems() {
		return problems;
	}

	public void setProblems(Map<ContractorAuditOperator, String> problems) {
		this.problems = problems;
	}

	public int getAuditTypeId() {
		return auditTypeId;
	}

	public void setAuditTypeId(int auditTypeId) {
		this.auditTypeId = auditTypeId;
	}

	public Set<Integer> getOperatorIds() {
		return operatorIds;
	}

	public void setOperatorIds(Set<Integer> operatorIds) {
		this.operatorIds = operatorIds;
	}

}
