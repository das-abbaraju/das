package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.PICS.AuditBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.log.PicsLogger;

/**
 * Properly add/remove all necessary audits for a given contractor.
 * 
 * This class has a lot more public methods than I would normally include.
 * However the processing here must be much more transparent to enable proper
 * troubleshooting. See AuditBuilderDebugger.
 */
public class AuditBuilderController {

	private User user = null;
	private ContractorAccount contractor = null;

	private List<AuditTypeRule> rules = null;
	private Map<AuditType, AuditTypeDetail> requiredAuditTypes = null;
	private AuditBuilder builder = new AuditBuilder();
	private Map<AuditType, List<AuditCategoryRule>> categoryRuleCache;

	private ContractorAuditDAO cAuditDAO;
	private AuditDataDAO auditDataDAO;
	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	private AuditDecisionTableDAO auditDecisionTableDAO;
	private ContractorTagDAO contractorTagDAO;

	public AuditBuilderController(ContractorAuditDAO cAuditDAO, AuditDataDAO auditDataDAO,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO, AuditDecisionTableDAO auditDecisionTableDAO,
			ContractorTagDAO contractorTagDAO) {
		this.cAuditDAO = cAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
		this.auditDecisionTableDAO = auditDecisionTableDAO;
		this.contractorTagDAO = contractorTagDAO;
	}

	public void setup(ContractorAccount con, User user) {
		this.contractor = con;
		if (user == null)
			user = new User(User.SYSTEM);
		this.user = user;
	}

	public void buildAudits(ContractorAccount con, User user) {
		setup(con, user);
		PicsLogger.addRuntimeRule("BuildAudits");
		PicsLogger.start("BuildAudits", " conID=" + contractor.getId());

		getAuditTypeRules();

		List<ContractorAudit> currentAudits = contractor.getAudits();
		/* Add audits not already there */
		int year = DateBean.getCurrentYear();
		for (AuditType auditType : getRequiredAuditTypeSet()) {
			if (auditType.isAnnualAddendum()) {
				addAnnualAddendum(currentAudits, year - 1, auditType);
				addAnnualAddendum(currentAudits, year - 2, auditType);
				addAnnualAddendum(currentAudits, year - 3, auditType);
			} else {
				boolean found = false;
				for (ContractorAudit conAudit : currentAudits) {
					if (conAudit.getAuditType().equals(auditType)) {
						// We found a matching audit for this requirement
						// Now determine if it will be good enough
						if (auditType.isRenewable()) {
							// This audit should not be renewed but we already
							// have one
							found = true;
						} else {
							if (!conAudit.getAuditStatus().isExpired() && !conAudit.willExpireSoon())
								// The audit is still valid for at least another
								// 60 days
								found = true;
						}
					}
				}

				if (!found) {
					PicsLogger.log("Adding: " + auditType.getId() + auditType.getAuditName());
					ContractorAudit pendingToInsert = createAudit(auditType);
					cAuditDAO.save(pendingToInsert);
					currentAudits.add(pendingToInsert);
				}
			}
		}

		/* Remove unneeded audits */
		Iterator<ContractorAudit> iter = currentAudits.iterator();
		while (iter.hasNext()) {
			ContractorAudit conAudit = iter.next();
			PicsLogger.log("checking to see if we still need existing " + conAudit.getAuditType().getAuditName()
					+ " - #" + conAudit.getId());
			if (conAudit.getAuditStatus().equals(AuditStatus.Pending) && conAudit.getPercentComplete() == 0
					&& !conAudit.isManuallyAdded()) {
				// This auto audit hasn't been started yet, double check to make
				// sure it's still needed

				if (!getRequiredAuditTypeSet().contains(conAudit.getAuditType())) {
					if (conAudit.getData().size() == 0) {
						PicsLogger.log("removing unneeded audit " + conAudit.getAuditType().getAuditName());
						// TODO try removing the audits from the list if we can
						// ContractorAudit removeMe = new ContractorAudit();
						// removeMe.setId(auditID);
						// contractor.getAudits().remove(removeMe);
						cAuditDAO.remove(conAudit.getId());
						iter.remove();
					}
				}
			}
		}

		/** Generate Categories and CAOs **/
		for (ContractorAudit conAudit : currentAudits) {
			fillAuditCategories(conAudit);
			fillAuditOperators(conAudit);
		}
		PicsLogger.stop();
	}

	public List<AuditTypeRule> getAuditTypeRules() {
		if (rules == null) {
			rules = auditDecisionTableDAO.getApplicableAuditRules(contractor);
			Set<Integer> questionAnswersNeeded = new HashSet<Integer>();
			Set<Integer> tagsNeeded = new HashSet<Integer>();
			for (AuditTypeRule rule : rules) {
				if (rule.getQuestion() != null)
					questionAnswersNeeded.add(rule.getQuestion().getId());
				if (rule.getTag() != null)
					tagsNeeded.add(rule.getTag().getId());
			}
			if (questionAnswersNeeded.size() > 0) {
				// Find out the answers to the needed questions and remove any
				// rules that don't apply
				Map<Integer, AuditData> contractorAnswers = auditDataDAO.findAnswersByContractor(contractor.getId(),
						questionAnswersNeeded);
				Iterator<AuditTypeRule> iterator = rules.iterator();
				while (iterator.hasNext()) {
					AuditTypeRule auditTypeRule = iterator.next();
					if (auditTypeRule.getQuestion() != null) {
						if (!auditTypeRule.isMatchingAnswer(contractorAnswers.get(auditTypeRule.getQuestion().getId()))) {
							iterator.remove();
						}
					}
				}
			}
			if (tagsNeeded.size() > 0) {
				// Find out which tags this contractor is using from the set of
				// potential operator tags and remove any rules that don't apply
				List<ContractorTag> contractorTags = contractorTagDAO.getContractorTags(contractor.getId(), tagsNeeded);
				Set<OperatorTag> opTags = new HashSet<OperatorTag>();
				for (ContractorTag contractorTag : contractorTags) {
					opTags.add(contractorTag.getTag());
				}
				Iterator<AuditTypeRule> iterator = rules.iterator();
				while (iterator.hasNext()) {
					AuditTypeRule auditTypeRule = iterator.next();
					if (auditTypeRule.getTag() != null && !opTags.contains(auditTypeRule.getTag())) {
						iterator.remove();
					}
				}
			}

		}
		return rules;
	}

	private Set<AuditType> getRequiredAuditTypeSet() {
		return getRequiredAuditTypes().keySet();
	}

	public Map<AuditType, AuditTypeDetail> getRequiredAuditTypes() {
		if (requiredAuditTypes == null) {
			requiredAuditTypes = builder.calculateRequiredAuditTypes(getAuditTypeRules(), contractor
					.getOperatorAccounts());
		}
		return requiredAuditTypes;
	}

	/**
	 * Determine which categories should be on a given audit and add ones that
	 * aren't there and remove ones that shouldn't be there
	 * 
	 * @param conAudit
	 */
	public void fillAuditCategories(ContractorAudit conAudit) {
		PicsLogger.start("AuditCategories", "auditID=" + conAudit.getId() + " type="
				+ conAudit.getAuditType().getAuditName());

		AuditCategoriesDetail detail = getAuditCategoryDetail(conAudit);

		Set<AuditCategory> categoriesNeeded = detail.categories;

		for (AuditCategory category : conAudit.getAuditType().getCategories()) {

			AuditCatData catData = getCatData(conAudit, category);
			if (catData.isOverride()) {
				// TODO What about the overrides? What does it mean to override
				// (show/hide) a category with more than one CAO.
			} else {
				catData.setApplies(categoriesNeeded.contains(catData.getCategory()));
			}
		}
		// Save all auditCatData rows at once
		cAuditDAO.save(conAudit);
		PicsLogger.stop();
	}

	public AuditCategoriesDetail getAuditCategoryDetail(ContractorAudit conAudit) {
		
		List<AuditCategoryRule> originalList = getCategoryRules(conAudit.getContractorAccount(), conAudit
				.getAuditType());
		List<AuditCategoryRule> categoryRulesToUse = new ArrayList<AuditCategoryRule>();

		Set<Integer> questionAnswersNeeded = new HashSet<Integer>();
		Set<Integer> tagsNeeded = new HashSet<Integer>();
		for (AuditCategoryRule rule : originalList) {
			if (rule.getQuestion() != null) {
				questionAnswersNeeded.add(rule.getQuestion().getId());
			}
			if (rule.getTag() != null) {
				tagsNeeded.add(rule.getTag().getId());
			}
		}
		for (AuditCategoryRule rule : originalList) {
			if ((rule.getQuestion() == null || questionAnswersNeeded.contains(rule.getQuestion().getId()))
					&& (rule.getTag() == null || tagsNeeded.contains(rule.getTag().getId()))) {
				// TODO work out the logic for pruning rule lists based on
				// questions and tags
			}
			categoryRulesToUse.add(rule);
		}
		
		AuditTypeDetail auditTypeDetail = getRequiredAuditTypes().get(conAudit.getAuditType());
		return builder.getDetail(conAudit.getAuditType(), categoryRulesToUse, auditTypeDetail);
	}

	private List<AuditCategoryRule> getCategoryRules(ContractorAccount contractor, AuditType auditType) {
		if (categoryRuleCache == null) {
			// The first time this runs, get all the applicable rules for all
			// the required AuditTypes and then divide them up into their
			// AuditType specific rule sets
			categoryRuleCache = new HashMap<AuditType, List<AuditCategoryRule>>();
			List<AuditCategoryRule> list = auditDecisionTableDAO.getApplicableCategoryRules(contractor,
					getRequiredAuditTypeSet());
			for (AuditType aType : getRequiredAuditTypeSet()) {
				List<AuditCategoryRule> listForThis = new ArrayList<AuditCategoryRule>();
				categoryRuleCache.put(aType, listForThis);
				for (AuditCategoryRule rule : list) {
					if (rule.getAuditType() == null || rule.getAuditType().equals(aType))
						listForThis.add(rule);
				}
			}
		}
		if (categoryRuleCache.get(auditType) == null) {
			// Probably won't need this but if we're missing the specific
			// auditType the go and query it now
			categoryRuleCache.put(auditType, auditDecisionTableDAO.getApplicableCategoryRules(contractor, auditType));
		}
		return categoryRuleCache.get(auditType);
	}

	private AuditCatData getCatData(ContractorAudit conAudit, AuditCategory category) {
		for (AuditCatData catData : conAudit.getCategories()) {
			if (catData.getCategory().equals(category))
				return catData;
		}

		// We didn't find a catData record, so let's create one now
		AuditCatData catData = new AuditCatData();
		catData.setCategory(category);
		catData.setAudit(conAudit);
		catData.setApplies(true);
		catData.setOverride(false);
		catData.setAuditColumns(new User(User.SYSTEM));
		if (category.getNumRequired() == 0)
			catData.setNumRequired(1);
		else
			catData.setNumRequired(category.getNumRequired());
		conAudit.getCategories().add(catData);
		return catData;
	}

	/**
	 * For each audit (policy), get a list of operators who have InsureGUARD and
	 * automatically require this policy, based on riskLevel
	 * 
	 * @param conAudit
	 * @param governingBodies
	 */
	private void fillAuditOperators(ContractorAudit conAudit) {
		PicsLogger.start("AuditOperators", conAudit.getAuditType().getAuditName());

		PicsLogger.log("Get a distinct set of (inherited) operators that are active and require insurance.");

		AuditCategoriesDetail detail = getAuditCategoryDetail(conAudit);

		// Add CAOs that don't yet exist
		for (OperatorAccount operator : detail.governingBodies) {
			PicsLogger.log("Evaluating CAO for " + operator.getName());

			// Now find the existing cao record for this operator (if one
			// exists)
			boolean exists = false;
			for (ContractorAuditOperator cao : conAudit.getOperators()) {
				if (cao.getOperator().equals(operator)) {
					exists = true;
		
				}
			}
			if (!exists) {
				// If we don't have one, then add it
				PicsLogger.log("Adding missing cao");
				ContractorAuditOperator cao = new ContractorAuditOperator();
				cao.setAudit(conAudit);
				cao.setOperator(operator);
				cao.setAuditColumns(user);
				conAudit.getOperators().add(cao);
				cao.setStatus(CaoStatus.Pending);
				contractorAuditOperatorDAO.save(cao);
			}
		}

		// Remove unneeded CAOs
		Iterator<ContractorAuditOperator> iter = conAudit.getOperators().iterator();
		while (iter.hasNext()) {
			ContractorAuditOperator cao = iter.next();
			if (!detail.governingBodies.contains(cao.getOperator())) {
				if (cao.getStatus().isTemporary()) {
					contractorAuditOperatorDAO.remove(cao);
					iter.remove();
				} else if (cao.isVisible()) {
					cao.setVisible(false);
					contractorAuditOperatorDAO.save(cao);
				}
			}
		}

		PicsLogger.stop();
	}

	public void fillAuditCategories(AuditData auditData) {
		fillAuditCategories(auditData.getAudit());
	}

	/**
	 * Business engine designed to find audits that are about to expire and
	 * rebuild them
	 */
	public void addAuditRenewals() {
		List<ContractorAccount> contractors = cAuditDAO.findContractorsWithExpiringAudits();
		for (ContractorAccount contractor : contractors) {
			try {
				buildAudits(contractor, null);
			} catch (Exception e) {
				System.out.println("ERROR!! AuditBuiler.addAuditRenewals() " + e.getMessage());
			}
		}
	}

	public void addAnnualAddendum(List<ContractorAudit> currentAudits, int year, AuditType auditType) {
		boolean found = false;
		for (ContractorAudit cAudit : currentAudits) {
			if (cAudit.getAuditType().isAnnualAddendum() && year == Integer.parseInt(cAudit.getAuditFor())) {
				if (cAudit.getAuditStatus().equals(AuditStatus.Expired))
					// this should never happen actually...but just incase
					cAudit.changeStatus(AuditStatus.Pending, user);
				found = true;
			}
		}
		if (!found) {
			Calendar startDate = Calendar.getInstance();
			startDate.set(year, Calendar.DECEMBER, 31);
			PicsLogger.log("Adding: " + auditType.getId() + auditType.getAuditName());
			ContractorAudit annualAudit = createAudit(auditType);
			annualAudit.setAuditFor(Integer.toString(year));
			annualAudit.setCreationDate(startDate.getTime());
			Date dateToExpire = DateBean.addMonths(startDate.getTime(), auditType.getMonthsToExpire());
			annualAudit.setExpiresDate(dateToExpire);
			cAuditDAO.save(annualAudit);
			currentAudits.add(annualAudit);
		}
	}

	public boolean removeCategory(String answer, AuditData auditData, AuditCatData auditCatData, int categoryID) {
		if (auditData != null) {
			if (answer.equals(auditData.getAnswer()) && auditCatData.getCategory().getId() == categoryID)
				return true;
		}
		return false;
	}

	private ContractorAudit createAudit(AuditType auditType) {
		ContractorAudit audit = new ContractorAudit();
		audit.setContractorAccount(contractor);
		audit.setAuditType(auditType);
		if (user != null)
			audit.setAuditColumns(user);
		else
			audit.setAuditColumns(new User(User.SYSTEM));
		return audit;
	}

}
