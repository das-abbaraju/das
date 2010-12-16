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
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.AnswerMap;
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
	private ContractorTagDAO contractorTagDAO;
	private AuditCategoryRuleCache auditCategoryRuleCache;
	private AuditTypeRuleCache auditTypeRuleCache;
	private AuditTypeDAO auditTypeDao;
	private ContractorAuditDAO conAuditDao;

	public AuditBuilderController(ContractorAuditDAO cAuditDAO, AuditDataDAO auditDataDAO,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO, ContractorTagDAO contractorTagDAO,
			AuditCategoryRuleCache auditCategoryRuleCache, AuditTypeRuleCache auditTypeRuleCache,
			AuditTypeDAO auditTypeDao, ContractorAuditDAO conAuditDao) {
		this.cAuditDAO = cAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
		this.contractorTagDAO = contractorTagDAO;
		this.auditCategoryRuleCache = auditCategoryRuleCache;
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditTypeDao = auditTypeDao;
		this.conAuditDao = conAuditDao;
	}

	public void setup(ContractorAccount con, User user) {
		this.contractor = con;
		if (user == null)
			user = new User(User.SYSTEM);
		this.user = user;
	}

	public void buildAudits(ContractorAccount con, User user) {
		setup(con, user);
		// PicsLogger.addRuntimeRule("BuildAudits");
		PicsLogger.start("BuildAudits", " conID=" + contractor.getId());
		getAuditTypeRules();

		/* Add audits not already there */
		int year = DateBean.getCurrentYear();
		for (AuditType auditType : getRequiredAuditTypeSet()) {
			if (auditType.isAnnualAddendum()) {
				auditType = auditTypeDao.find(auditType.getId());
				addAnnualAddendum(contractor.getAudits(), year - 1, auditType);
				addAnnualAddendum(contractor.getAudits(), year - 2, auditType);
				addAnnualAddendum(contractor.getAudits(), year - 3, auditType);
			} else {
				boolean found = false;
				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (conAudit.getAuditType().equals(auditType)) {
						// We found a matching audit for this requirement
						// Now determine if it will be good enough
						if (auditType.isRenewable()) {
							// This audit should not be renewed but we already
							// have one
							found = true;
						} else if (auditType.getId() == AuditType.WELCOME) {
							// we should never add another welcome call audit
							found = true;
						} else {
							if (!conAudit.isExpired() && !conAudit.willExpireSoon())
								// The audit is still valid for at least another
								// 60 days
								found = true;
						}
					}
				}

				if (!found) {
					PicsLogger.log("Adding: " + auditType.getId() + auditType.getAuditName());
					// need to reconnect object (auditType is from Cache)
					auditType = auditTypeDao.find(auditType.getId());
					ContractorAudit pendingToInsert = createAudit(auditType);
					contractor.getAudits().add(pendingToInsert);
				}
			}
		}

		/* Remove unneeded audits */
		Iterator<ContractorAudit> iter = contractor.getAudits().iterator();
		while (iter.hasNext()) {
			ContractorAudit conAudit = iter.next();
			PicsLogger.log("checking to see if we still need existing " + conAudit.getAuditType().getAuditName()
					+ " - #" + conAudit.getId());
			if (!conAudit.isManuallyAdded() && !getRequiredAuditTypeSet().contains(conAudit.getAuditType())) {
				boolean needed = false;
				// Never delete the PQF
				if (conAudit.getAuditType().isPqf())
					needed = true;
				for (ContractorAuditOperator cao : conAudit.getOperators()) {
					if (cao.getStatus().after(AuditStatus.Pending))
						needed = true;
					else if (cao.getPercentComplete() > 0)
						needed = true;
				}

				if (!needed && conAudit.getScheduledDate() != null) {
					System.out.println("WARNING: Leaving unneeded scheduled audit " + conAudit.getId());
					needed = true;
				}

				if (!needed && conAudit.getData().size() == 0) {
					PicsLogger.log("removing unneeded audit " + conAudit.getAuditType().getAuditName());
					iter.remove();
					cAuditDAO.remove(conAudit);
				}
			}
		}

		/** Generate Categories and CAOs **/
		for (ContractorAudit conAudit : contractor.getAudits()) {
			fillAuditOperators(conAudit);
			fillAuditCategories(conAudit);
			for (ContractorAuditOperator cao : conAudit.getOperators()) {
				fillAuditOperatorPermissions(cao);
			}
		}
		PicsLogger.stop();

		conAuditDao.save(contractor);
	}

	public List<AuditTypeRule> getAuditTypeRules() {
		if (rules == null) {
			rules = new ArrayList<AuditTypeRule>();
			List<AuditTypeRule> applicableAuditRules = auditTypeRuleCache.getApplicableAuditRules(contractor);
			List<AuditRule> pruneRules = pruneRules(applicableAuditRules, null);
			for (AuditRule rule : pruneRules) {
				rules.add((AuditTypeRule) rule);
			}
		}
		return rules;
	}

	private Set<AuditType> getRequiredAuditTypeSet() {
		return getRequiredAuditTypes().keySet();
	}

	public List<AuditRule> pruneRules(List<? extends AuditRule> rules, ContractorAudit conAudit) {
		Set<Integer> contractorAnswersNeeded = new HashSet<Integer>();
		Set<Integer> auditAnswersNeeded = new HashSet<Integer>();
		Set<Integer> tagsNeeded = new HashSet<Integer>();
		for (AuditRule rule : rules) {
			if (rule.getQuestion() != null) {
				if (conAudit != null && conAudit.getAuditType().equals(rule.getQuestion().getAuditType()))
					auditAnswersNeeded.add(rule.getQuestion().getId());
				else
					contractorAnswersNeeded.add(rule.getQuestion().getId());
			}
			if (rule.getTag() != null)
				tagsNeeded.add(rule.getTag().getId());
		}

		Map<Integer, AuditData> contractorAnswers = new HashMap<Integer, AuditData>();
		if (contractorAnswersNeeded.size() > 0) {
			contractorAnswers = auditDataDAO.findAnswersByContractor(contractor.getId(), contractorAnswersNeeded);
		}
		if (auditAnswersNeeded.size() > 0) {
			List<AuditData> requiredAnswers = new ArrayList<AuditData>();
			for (AuditData answer : conAudit.getData())
				if (auditAnswersNeeded.contains(answer.getQuestion().getId()))
					requiredAnswers.add(answer);
			AnswerMap answerMap = new AnswerMap(requiredAnswers);
			for (Integer questionID : auditAnswersNeeded) {
				contractorAnswers.put(questionID, answerMap.get(questionID));
			}
		}
		Set<OperatorTag> opTags = new HashSet<OperatorTag>();
		if (tagsNeeded.size() > 0) {
			List<ContractorTag> contractorTags = contractorTagDAO.getContractorTags(contractor.getId(), tagsNeeded);
			for (ContractorTag contractorTag : contractorTags) {
				opTags.add(contractorTag.getTag());
			}
		}

		List<AuditRule> list = new ArrayList<AuditRule>();
		for (AuditRule rule : rules) {
			boolean valid = true;

			if (rule.getQuestion() != null && !rule.isMatchingAnswer(contractorAnswers.get(rule.getQuestion().getId()))) {
				valid = false;
			}

			if (rule.getTag() != null && !opTags.contains(rule.getTag())) {
				valid = false;
			}

			if (rule.getClass().equals(AuditTypeRule.class)) {
				AuditTypeRule auditTypeRule = (AuditTypeRule) rule;
				if (auditTypeRule.getAuditType() != null && auditTypeRule.getAuditType().getId() == AuditType.WELCOME) {
					if (DateBean.getDateDifference(contractor.getCreationDate()) < -90)
						valid = false;
				}
				if (auditTypeRule.isManuallyAdded() || (auditTypeRule.getDependentAuditType() != null)) {
					valid = false;
					for (ContractorAudit audit : contractor.getAudits()) {
						if(auditTypeRule.isManuallyAdded()) {
							if(auditTypeRule.getAuditType().equals(audit.getAuditType())) {
								valid = true;
								break;
							}
						}
						else if (!audit.isExpired() && audit.getAuditType().equals(auditTypeRule.getDependentAuditType())) {
							if (audit.hasCaoStatusAfter(auditTypeRule.getDependentAuditStatus()))
								valid = true;
						}
					}
				}
			}

			if (valid)
				list.add(rule);
		}

		return list;
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
		if (detail == null) {
			PicsLogger.log("missing detail for " + conAudit.getAuditType());
			PicsLogger.stop();
			return;
		}

		Set<AuditCategory> categoriesNeeded = detail.categories;
		for (AuditCatData auditCatData : conAudit.getCategories()) {
			if (auditCatData.getCategory().getParent() == null) {
				if (conAudit.getAuditType().isDesktop() && conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)) {
					// this is to ensure that we don't add new categories or
					// remove the
					// existing ones except the override categories for a manual
					// audit
					// after is it being submitted
					if (auditCatData.isApplies())
						categoriesNeeded.add(auditCatData.getCategory());
					else
						categoriesNeeded.remove(auditCatData.getCategory());
				} else {
					if (auditCatData.isOverride()) {
						if (auditCatData.isApplies())
							categoriesNeeded.add(auditCatData.getCategory());
						else
							categoriesNeeded.remove(auditCatData.getCategory());
					}
				}
			}
		}

		for (AuditCategory category : conAudit.getAuditType().getCategories()) {

			AuditCatData catData = getCatData(conAudit, category);
			if (catData.isOverride()) {
				// (show/hide) a category with more than one CAO.
			} else {
				boolean categoryApplies = categoriesNeeded.contains(catData.getCategory());
				if (categoryApplies) {
					// Making sure the top level parent applies for
					// subcategories when adding it to the AuditCatData
					categoryApplies = categoriesNeeded.contains(catData.getCategory().getTopParent());
				}
				catData.setApplies(categoryApplies);
				if ((OshaTypeConverter.getTypeFromCategory(catData.getCategory().getId()) != null) && categoryApplies) {
					addOshaLog(catData);
				}
			}
		}
		// Save all auditCatData rows at once
		// cAuditDAO.save(conAudit);
		PicsLogger.stop();
	}

	public AuditCategoriesDetail getAuditCategoryDetail(ContractorAudit conAudit) {
		if (contractor == null)
			contractor = conAudit.getContractorAccount();
		AuditTypeDetail auditTypeDetail = getRequiredAuditTypes().get(conAudit.getAuditType());
		if (auditTypeDetail == null)
			return null;

		List<AuditCategoryRule> temp = getCategoryRules(conAudit.getContractorAccount(), conAudit.getAuditType());
		List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
		for (AuditRule rule : pruneRules(temp, conAudit)) {
			rules.add((AuditCategoryRule) rule);
		}

		return builder.getDetail(conAudit.getAuditType(), rules, auditTypeDetail.operators);
	}

	private List<AuditCategoryRule> getCategoryRules(ContractorAccount contractor, AuditType auditType) {
		if (categoryRuleCache == null) {
			// The first time this runs, get all the applicable rules for all
			// the required AuditTypes and then divide them up into their
			// AuditType specific rule sets
			categoryRuleCache = new HashMap<AuditType, List<AuditCategoryRule>>();
			List<AuditCategoryRule> list = auditCategoryRuleCache.getApplicableCategoryRules(contractor,
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
			categoryRuleCache.put(auditType, auditCategoryRuleCache.getApplicableCategoryRules(contractor, auditType));
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
		catData.setNumRequired(category.getNumRequired());
		conAudit.getCategories().add(catData);
		return catData;
	}

	/**
	 * For each audit (policy), get a list of operators and create Contractor
	 * Audit Operator
	 * 
	 * @param conAudit
	 * @param governingBodies
	 */
	private void fillAuditOperators(ContractorAudit conAudit) {
		PicsLogger.start("AuditOperators", conAudit.getAuditType().getAuditName());

		PicsLogger.log("Get a distinct set of (inherited) operators that are active and require a auditOperator.");

		if (conAudit.getAuditType().isDesktop() && conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
			return;

		AuditCategoriesDetail detail = getAuditCategoryDetail(conAudit);

		if (detail == null) {
			// Make sure that the caos' visibility is set correctly
			for (ContractorAuditOperator cao : conAudit.getOperators()) {
				cao.setVisible(false);
			}
			return;
		}

		if (detail.governingBodies.contains(null)) {
			PicsLogger.log("Replacing null governing body with PICS Global account");
			OperatorAccount operator = new OperatorAccount();
			// PICS Consortium
			operator.setId(4);
			operator.setName("PICS Global");
			detail.governingBodies.add(operator);
			detail.governingBodies.remove(null);
		}

		// Make sure that the caos' visibility is set correctly
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			boolean contains = contains(detail.governingBodies, cao.getOperator());
			if (contains != cao.isVisible()) {
				cao.setVisible(contains);
				// contractorAuditOperatorDAO.save(cao);
			}
		}

		// Add CAOs that don't yet exist
		for (OperatorAccount operator : detail.governingBodies) {

			PicsLogger.log("Evaluating CAO for " + operator.getName());

			// Now find the existing cao record for this operator (if one
			// exists)
			if (!findOperator(conAudit, operator)) {
				// If we don't have one, then add it
				PicsLogger.log("Adding missing cao");
				ContractorAuditOperator cao = new ContractorAuditOperator();
				cao.setAudit(conAudit);
				cao.setOperator(operator);
				cao.setAuditColumns(user);
				cao.setStatusChangedDate(new Date());
				cao.setStatus(cao.getAudit().getAuditType().getWorkFlow().getFirstStep().getNewStatus());
				conAudit.getOperators().add(cao);
				// contractorAuditOperatorDAO.save(cao);
			}
		}

		PicsLogger.stop();
	}

	private boolean contains(Set<OperatorAccount> set, OperatorAccount operator) {
		for (OperatorAccount operatorAccount : set) {
			if (operatorAccount.getId() == operator.getId())
				return true;
		}
		return false;
	}

	private void fillAuditOperatorPermissions(ContractorAuditOperator cao) {
		// For now we have decided to hard code Welcome Call audit to not be
		// viewable for operators.
		// We might add a field later if required
		if (cao.getAudit().getAuditType().getId() == AuditType.WELCOME)
			return;
		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();

		if (cao.getAudit().getRequestingOpAccount() != null) {
			// Warning, this only works for operator sites, not corporate
			// accounts
			operators.add(cao.getAudit().getRequestingOpAccount());
		} else if (cao.getAudit().getAuditType().isDesktop() && cao.getAudit().hasCaoStatus(AuditStatus.Complete)) {
			for (ContractorOperator co : cao.getAudit().getContractorAccount().getOperators()) {
				if (co.getOperatorAccount().getOperatorHeirarchy().contains(cao.getOperator().getId())) {
					operators.add(co.getOperatorAccount());
				}
			}
		} else {
			AuditCategoriesDetail detail = getAuditCategoryDetail(cao.getAudit());
			if (detail != null) {
				for (OperatorAccount opAccount : detail.operators.keySet()) {
					AuditCategoryRule auditCategoryRule = detail.operators.get(opAccount);
					if ((auditCategoryRule == null || auditCategoryRule.getOperatorAccount() == null)
							&& (cao.getOperator().getId() == OperatorAccount.PicsConsortium)) {
						operators.add(opAccount);
					} else if (auditCategoryRule.getOperatorAccount() != null
							&& cao.getOperator().equals(auditCategoryRule.getOperatorAccount())) {
						operators.add(opAccount);
					} else if (auditCategoryRule.getOperatorAccount() != null
							&& cao.getAudit().getAuditType().isDesktop()
							&& cao.getOperator().getId() == OperatorAccount.PicsConsortium
							&& Account.PICS_CORPORATE.contains(auditCategoryRule.getOperatorAccount().getId())) {
						// TODO clean this audit builder CAOP stuff
						operators.add(opAccount);
					}
				}
			}
		}
		// Remove first
		Iterator<ContractorAuditOperatorPermission> caopIter = cao.getCaoPermissions().iterator();
		while (caopIter.hasNext()) {
			ContractorAuditOperatorPermission caop = caopIter.next();
			if (operators.contains(caop.getOperator())) {
				// It's already there, do nothing
				operators.remove(caop.getOperator());
			} else {
				// Delete the caop and remove from cao.getCaoPermissions()
				caopIter.remove();
				contractorAuditOperatorDAO.remove(caop);
			}
		}
		for (OperatorAccount operator : operators) {
			// Insert the remaining operators
			ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
			caop.setCao(cao);
			caop.setOperator(operator);
			cao.getCaoPermissions().add(caop);
		}
		// contractorAuditOperatorDAO.save(cao);
	}

	private boolean findOperator(ContractorAudit conAudit, OperatorAccount operator) {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getOperator().equals(operator)) {
				return true;
			}
		}
		return false;
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
				cAuditDAO.save(contractor);
			} catch (Exception e) {
				System.out.println("ERROR!! AuditBuiler.addAuditRenewals() " + e.getMessage());
			}
		}
	}

	public void addAnnualAddendum(List<ContractorAudit> currentAudits, int year, AuditType auditType) {
		for (ContractorAudit cAudit : currentAudits) {
			if (cAudit.getAuditType().isAnnualAddendum() && year == Integer.parseInt(cAudit.getAuditFor())) {
				// Do nothing. It's already here
				return;
			}
		}
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

	private void addOshaLog(AuditCatData catData) {
		boolean hasOshaCorporate = false;
		for (OshaAudit osha : catData.getAudit().getOshas()) {
			if (osha.isCorporate() && osha.getType().equals(OshaTypeConverter.getTypeFromCategory(catData.getCategory().getId()))) {
				hasOshaCorporate = true;
			}
		}

		if (!hasOshaCorporate) {
			OshaAudit oshaAudit = new OshaAudit();
			oshaAudit.setConAudit(catData.getAudit());
			oshaAudit.setCorporate(true);
			oshaAudit.setType(OshaTypeConverter.getTypeFromCategory(catData.getCategory().getId()));
			catData.getAudit().getOshas().add(oshaAudit);
			catData.setNumRequired(2);
		}
	}
}
