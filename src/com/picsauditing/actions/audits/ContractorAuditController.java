package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
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
	protected boolean previewAudit;
	protected AuditCatData categoryData;
	private AuditCategoryDAO auditCategoryDAO;
	private AuditPercentCalculator auditPercentCalculator;
	private AuditBuilderController auditBuilder;
	private OshaAuditDAO oshaAuditDAO;
	protected int caoID;
	protected boolean previewCat = false;
	// Policy verification (next/first buttons)
	private boolean policy;

	public ContractorAuditController(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, CertificateDAO certificateDao,
			AuditCategoryDAO auditCategoryDAO,
			AuditPercentCalculator auditPercentCalculator,
			OshaAuditDAO oshaAuditDAO, AuditBuilderController auditBuilder, AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.oshaAuditDAO = oshaAuditDAO;
		this.auditBuilder = auditBuilder;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (auditID > 0)
			this.findConAudit();

		if (button != null) {
			if (categoryID > 0 && permissions.isPicsEmployee()) {
				if ("IncludeCategory".equals(button)) {
					for (AuditCatData data : conAudit.getCategories()) {
						if (data.getCategory().getId() == categoryID) {
							data.setApplies(true);
							data.setOverride(true);
							auditDao.save(data);
							break;
						}
					}
					conAudit.setLastRecalculation(null);
					contractor.incrementRecalculation();
					return SUCCESS;
				}
				
				if ("UnincludeCategory".equals(button)) {
					for (AuditCatData data : conAudit.getCategories()) {
						if (data.getCategory().getId() == categoryID) {
							data.setApplies(false);
							data.setOverride(true);
							auditDao.save(data);
							break;
						}
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
				this.redirect("Audit.action?auditID="+conAudit.getId());
				return SUCCESS;
			}

			// Preview the Category from the manage audit type page
			if ("PreviewCategory".equals(button)) {
				if (auditID == 0 && categoryID > 0) {
					previewCat = true;
					AuditCategory auditCategory = auditCategoryDAO
							.find(categoryID);
					for (AuditCategory auditSubCategory : auditCategory
							.getChildren()) {
						for (AuditQuestion auditQuestion : auditSubCategory
								.getQuestions()) {
						}
					}
					categories = new HashMap<AuditCategory, AuditCatData>();
					categoryData = new AuditCatData();
					categoryData.setCategory(auditCategory);
					categoryData.setApplies(true);
					categories.put(auditCategory, categoryData);
					mode = VIEW;
					return SUCCESS;
				}
			}

			if (categoryID > 0) {
				AuditCategory auditCategory = auditCategoryDAO.find(categoryID);
				Set<Integer> questionIDs = new HashSet<Integer>();
				categoryData = getCategories().get(auditCategory);
				if(categoryData == null) {
					for(AuditCatData catdata : getCategories().values()) {
						categoryData = catdata;
						break;
					}
				}
				for (AuditCategory childCategory : categoryData.getCategory()
						.getChildren()) {
					for (AuditQuestion question : childCategory.getQuestions()) {
						questionIDs.add(question.getId());
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

			return SUCCESS;
		}

		if (conAudit != null)
			getValidSteps();

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

	public boolean isViewBlanks() {
		return viewBlanks;
	}

	public void setViewBlanks(boolean viewBlanks) {
		this.viewBlanks = viewBlanks;
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

	public boolean isCanVerifyAudit() {
		if(!permissions.isAuditor() || !permissions.hasGroup(959))
			return false;
		
		if (!conAudit.getAuditType().getWorkFlow().isHasSubmittedStep())
			return false;
		
		if(conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
			return true;
		
		return false;
	}

	public boolean isCanVerifyPqf() {
		if(!permissions.hasPermission(OpPerms.AuditVerification))
			return false;
		if(!conAudit.getAuditType().isPqf() || !conAudit.getAuditType().isAnnualAddendum())
			return false;
		
		if (conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
			return true;

		return false;
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
}
