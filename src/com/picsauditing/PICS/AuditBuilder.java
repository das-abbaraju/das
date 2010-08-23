package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.log.PicsLogger;

/**
 * Properly add/remove all necessary audits for a given contractor
 * 
 * @author Trevor
 * 
 */
public class AuditBuilder {

	private boolean fillAuditCategories = true;
	private User user = null;
	private ContractorAccount contractor = null;

	private ContractorAuditDAO cAuditDAO;
	private AuditDataDAO auditDataDAO;
	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	private AuditTypeDAO auditTypeDAO;
	private AuditDecisionTableDAO auditDecisionTableDAO;

	public AuditBuilder(ContractorAuditDAO cAuditDAO, AuditDataDAO auditDataDAO,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO, AuditTypeDAO auditTypeDAO,
			AuditDecisionTableDAO auditDecisionTableDAO) {
		this.cAuditDAO = cAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
		this.auditTypeDAO = auditTypeDAO;
		this.auditDecisionTableDAO = auditDecisionTableDAO;
	}

	public void buildAudits(ContractorAccount con) {
		this.contractor = con;
		PicsLogger.addRuntimeRule("BuildAudits");
		PicsLogger.start("BuildAudits", " conID=" + contractor.getId());
		List<ContractorAudit> currentAudits = contractor.getAudits();

		if (contractor.isAcceptsBids()) {
			int year = DateBean.getCurrentYear();
			AuditType auditType = auditTypeDAO.find(AuditType.ANNUALADDENDUM);
			addAnnualAddendum(currentAudits, year - 1, auditType);
			addAnnualAddendum(currentAudits, year - 2, auditType);
			addAnnualAddendum(currentAudits, year - 3, auditType);
			return;
		}

		List<AuditStatus> okStatuses = new ArrayList<AuditStatus>();
		okStatuses.add(AuditStatus.Active);
		okStatuses.add(AuditStatus.Pending);
		okStatuses.add(AuditStatus.Submitted);
		okStatuses.add(AuditStatus.Exempt);
		okStatuses.add(AuditStatus.Resubmitted);
		okStatuses.add(AuditStatus.Incomplete);

		/** *** Welcome Call *** */
		if (DateBean.getDateDifference(contractor.getCreationDate()) > -90) {
			// Create the welcome call for all accounts created in the past 90
			// days
			boolean needsWelcome = true;
			for (ContractorAudit conAudit : currentAudits) {
				if (conAudit.getAuditType().getId() == AuditType.WELCOME) {
					needsWelcome = false;
					break;
				}
			}
			if (needsWelcome) {
				ContractorAudit welcomeCall = createAudit(AuditType.WELCOME);
				welcomeCall.setExpiresDate(DateBean.addMonths(new Date(), 3));
				cAuditDAO.save(welcomeCall);
				currentAudits.add(welcomeCall);
			}
		}

		/** *** PQF *** */
		// Find the PQF audit for this contractor
		// Only ever create ONE PQF audit
		ContractorAudit pqfAudit = null;
		for (ContractorAudit conAudit : currentAudits) {
			if (conAudit.getAuditType().isPqf()) {
				if (conAudit.getAuditStatus().equals(AuditStatus.Expired)) {
					// This should never happen...but just in case
					conAudit.changeStatus(AuditStatus.Pending, user);
					cAuditDAO.save(conAudit);
				}
				pqfAudit = conAudit;
				break;
			}
		}
		if (pqfAudit == null) {
			PicsLogger.log("adding PQF");
			pqfAudit = createAudit(AuditType.PQF);
			cAuditDAO.save(pqfAudit);
			currentAudits.add(pqfAudit);
		}

		// Get the answer for DOT employees from Pqfdata
		AuditData oqEmployees = auditDataDAO.findAnswerToQuestion(pqfAudit.getId(), AuditQuestion.OQ_EMPLOYEES); // TODO we don't need this here anymore
		AuditData hasCOR = auditDataDAO.findAnswerToQuestion(pqfAudit.getId(), 2954);

		/** Add other Audits and Policy Types **/
		// Get a distinct list of AuditTypes that attached operators require
		// Note: this is a lot of iterating over JPA Entities, my assumption
		// is that the operators and auditTypes will be really cached well
		Set<AuditType> auditTypeList = new HashSet<AuditType>();

		List<AuditTypeRule> rules = auditDecisionTableDAO.getApplicableAuditRules(contractor);
		Set<AuditQuestion> questionAnswersNeeded = new HashSet<AuditQuestion>();
		Set<OperatorTag> tagsNeeded = new HashSet<OperatorTag>();
		for (AuditTypeRule auditTypeRule : rules) {
			if (auditTypeRule.getQuestion() != null)
				questionAnswersNeeded.add(auditTypeRule.getQuestion());
			if (auditTypeRule.getTag() != null)
				tagsNeeded.add(auditTypeRule.getTag());
		}
		// TODO get the answers and tags that are Needed and prune the rules

		{
			Set<AuditType> allCandidateAuditTypes = new HashSet<AuditType>();
			for (AuditTypeRule rule : rules) {
				if (rule.getAuditType() != null)
					allCandidateAuditTypes.add(rule.getAuditType());
			}

			for (AuditType auditType : allCandidateAuditTypes) {
				if (isApplicable(rules, auditType)) {
					auditTypeList.add(auditType);
				}
			}
		}

		// Checking to see if the supplement COR or BPIISNCaseMgmt should be
		// required for this contractor
		ContractorAudit corAudit = null;
		ContractorAudit BpIisnSpecific = null;
		ContractorAudit HSECompetency = null;
		for (ContractorAudit audit : currentAudits) {
			if (auditTypeList.contains(audit.getAuditType())) {
				if (audit.getAuditType().getId() == AuditType.BPIISNSPECIFIC)
					BpIisnSpecific = audit;
				else if (audit.getAuditType().getId() == 99)
					HSECompetency = audit;
				else if (audit.getAuditType().getId() == AuditType.COR && hasCOR != null
						&& "Yes".equals(hasCOR.getAnswer()))
					corAudit = audit;
			}
		}

		int year = DateBean.getCurrentYear();
		for (AuditType auditType : auditTypeList) {
			if (auditType.isAnnualAddendum()) {
				addAnnualAddendum(currentAudits, year - 1, auditType);
				addAnnualAddendum(currentAudits, year - 2, auditType);
				addAnnualAddendum(currentAudits, year - 3, auditType);
			} else {
				boolean found = false;
				// For each audit this contractor SHOULD have
				// Figure out if the contractor currently has an
				// active audit that isn't scheduled to expire soon
				for (ContractorAudit conAudit : currentAudits) {
					if (conAudit.getAuditType().equals(auditType)) {
						// We found a matching audit for this requirement
						// Now determine if it will be good enough
						if (auditType.isRenewable()) {
							// This audit should not be renewed but we already
							// have one
							found = true;
							if (conAudit.getAuditStatus().equals(AuditStatus.Expired)) {
								// This should never happen...but just in case
								conAudit.changeStatus(AuditStatus.Pending, user);
								cAuditDAO.save(conAudit);
							}
						} else {
							if (okStatuses.contains(conAudit.getAuditStatus()) && !conAudit.willExpireSoon())
								// The audit is still valid for at least another
								// 60 days
								found = true;
						}
					}
				}

				if (!found) {
					// The audit wasn't found, figure out if we should
					// create it now or wait until later
					boolean insertNow = true;
					switch (auditType.getId()) {
					case AuditType.DESKTOP:
						if (!pqfAudit.getAuditStatus().isActive()) {
							insertNow = false;
							break;
						}
						// If the contractor has answered Yes to the COR
						// question
						// don't create a Desktop Audit
						if (hasCOR != null && "Yes".equals(hasCOR.getAnswer()))
							insertNow = false;
						break;
					case AuditType.OFFICE:
						if (!pqfAudit.getAuditStatus().isActiveSubmitted())
							insertNow = false;
						break;
					case AuditType.DA:
						if (!pqfAudit.getAuditStatus().isActiveSubmitted() || oqEmployees == null
								|| !"Yes".equals(oqEmployees.getAnswer()))
							insertNow = false;
						break;
					case AuditType.COR:
						if (hasCOR == null || !"Yes".equals(hasCOR.getAnswer()))
							insertNow = false;
						break;
					case AuditType.SUPPLEMENTCOR:
						if (corAudit == null)
							insertNow = false;
						else if (!corAudit.getAuditStatus().isActive())
							insertNow = false;
						break;
					case AuditType.BPIISNCASEMGMT:
						if (BpIisnSpecific != null && !BpIisnSpecific.getAuditStatus().isActiveResubmittedExempt())
							insertNow = false;
						break;
					case 100:
						if (!HSECompetency.getAuditStatus().isActiveResubmittedExempt())
							insertNow = false;
						break;
					default:
						break;
					}

					if (insertNow) {
						PicsLogger.log("Adding: " + auditType.getId() + auditType.getAuditName());
						ContractorAudit pendingToInsert = createAudit(auditType);
						cAuditDAO.save(pendingToInsert);
						currentAudits.add(pendingToInsert);

					} else
						PicsLogger.log("Skipping: " + auditType.getId() + auditType.getAuditName());
				}
			}
		}

		/** ** Remove unneeded audits *** */
		// We can't delete until we're done reading all data from DB (lazy
		// loading)
		Set<Integer> auditsToRemove = new HashSet<Integer>();

		// You have to use while iterator instead of a for loop because we're
		// removing an entry from the list
		Iterator<ContractorAudit> iter = currentAudits.iterator();
		while (iter.hasNext()) {
			ContractorAudit conAudit = iter.next();
			PicsLogger.log("checking to see if we still need existing " + conAudit.getAuditType().getAuditName()
					+ " - #" + conAudit.getId());
			if (conAudit.getAuditStatus().equals(AuditStatus.Pending) && conAudit.getPercentComplete() == 0
					&& !conAudit.isManuallyAdded()) {
				// This auto audit hasn't been started yet, double check to make
				// sure it's still needed
				boolean needed = false;

				if (conAudit.getAuditType().isPqf() || conAudit.getAuditType().getId() == AuditType.WELCOME
						|| conAudit.getAuditType().isAnnualAddendum())
					needed = true;

				for (AuditType auditType : auditTypeList) {
					if (conAudit.getAuditType().equals(auditType)) {
						if (conAudit.getAuditType().getId() == AuditType.DA
								&& (oqEmployees == null || !"Yes".equals(oqEmployees.getAnswer()))) {
							needed = false;
						} else
							needed = true;
					}
				}

				if (!needed) {
					if (conAudit.getData().size() == 0) {
						PicsLogger.log("removing unneeded audit " + conAudit.getAuditType().getAuditName());
						auditsToRemove.add(conAudit.getId());
						iter.remove();
					}
				}
			}
		}

		for (Integer auditID : auditsToRemove) {
			cAuditDAO.remove(auditID);
			// TODO try removing the audits from the list if we can
			// ContractorAudit removeMe = new ContractorAudit();
			// removeMe.setId(auditID);
			// contractor.getAudits().remove(removeMe);
			fillAuditCategories = false;
		}

		if (fillAuditCategories) {
			/** Generate Categories * */
			for (ContractorAudit conAudit : currentAudits) {
				fillAuditCategories(conAudit, false);
				fillAuditOperators(conAudit);
			}
		}
		PicsLogger.stop();
	}

	private boolean isApplicable(List<AuditTypeRule> rules, AuditType auditType) {
		for (AuditTypeRule rule : rules) {
			if (rule.getAuditType() == null || rule.getAuditType().equals(auditType)) {
				return rule.isInclude();
			}
		}
		return false;
	}

	private boolean isApplicable(List<AuditCategoryRule> rules, AuditCategory auditCategory) {
		for (AuditCategoryRule rule : rules) {
			if (rule.getAuditCategory() == null || rule.getAuditCategory().equals(auditCategory)) {
				return rule.isInclude();
			}
		}
		return false;
	}

	/**
	 * For each audit (policy), get a list of operators who have InsureGUARD and
	 * automatically require this policy, based on riskLevel
	 * 
	 * @param conAudit
	 */
	private void fillAuditOperators(ContractorAudit conAudit) {
		if (!conAudit.getAuditType().getClassType().isPolicy())
			return;

		PicsLogger.start("AuditOperators", conAudit.getAuditType().getAuditName());

		PicsLogger.log("Get a distinct set of (inherited) operators that are active and require insurance.");
		Set<OperatorAccount> operatorSet = new HashSet<OperatorAccount>();
		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			if (co.getOperatorAccount().getStatus().isActiveDemo()
					&& co.getOperatorAccount().getCanSeeInsurance().isTrue())
				operatorSet.add(co.getOperatorAccount().getInheritInsurance());
		}

		for (OperatorAccount operator : operatorSet) {
			PicsLogger.log(operator.getName() + " subscribes to InsureGUARD");
			// For this auditType (General Liability) and
			// this contractor's associated operator (BP Cherry Point)

			boolean visible = false;
			boolean required = false;

			// conAudit.getRequestingOpAccount()
			// NOTE!!! I've removed the requesting op account functionality
			// I don't think it's very common for an operator to need
			// specific/custom policies only for that facility
			// If a facility needs "Pollution" and the contractor adds it,
			// then other operators should be able to see it as well that
			// subscribe to Pollution insurance

			for (AuditOperator ao : operator.getAudits()) {
				if (conAudit.getAuditType().equals(ao.getAuditType()) && ao.isCanSee()) {
					visible = true;
					if (ao.isRequiredFor(contractor))
						required = true;
					PicsLogger.log(contractor.getName() + " can see " + (required ? "required " : " ")
							+ ao.getAuditType().getAuditName());
					break;
				}
			}

			// Now find the existing cao record for this operator (if one
			// exists)
			ContractorAuditOperator cao = null;
			for (ContractorAuditOperator cao2 : conAudit.getOperators()) {
				if (cao2.getOperator().equals(operator)) {
					cao = cao2;
					break;
				}
			}

			if (visible) {
				if (cao == null) {
					// If we don't have one, then add it
					PicsLogger.log("Adding missing cao");
					cao = new ContractorAuditOperator();
					cao.setAudit(conAudit);
					cao.setOperator(operator);
					cao.setAuditColumns(user);
					conAudit.getOperators().add(cao);
					cao.setStatus(CaoStatus.Pending);
				}

				if (required) {
					// This cao is always required so add it if it doesn't
					// exist and then calculate the recommended status
					cao.setVisible(true);
				} else if (!conAudit.isManuallyAdded()) {
					// This cao might be required (if the operator manually
					// requested it)
					if (cao.getStatus().isPending())
						cao.setVisible(false);
				}

				if (CaoStatus.NotApplicable.equals(cao.getStatus())) {
					// This operator has specifically stated they don't need
					// this policy
					cao.setVisible(false);
				}

			} else if (cao != null) {
				// This existing cao is either no longer needed by the operator
				// (ie audit/operator matrix)
				// or it has one or more parents that have already made a
				// decision on this policy

				// Remove the cao if it's Awaiting
				if (cao.getStatus().equals(CaoStatus.Pending)) {
					PicsLogger.log("Removing unneeded ContractorAuditOperator " + cao.getId());
					conAudit.getOperators().remove(cao);
					contractorAuditOperatorDAO.remove(cao);
					cao = null;
				} else {
					cao.setVisible(false);
				}
			}

			if (cao != null) {
				// If we still have a "dirty" cao record, then save it
				contractorAuditOperatorDAO.save(cao);
			}
		}

		Iterator<ContractorAuditOperator> iter = conAudit.getOperators().iterator();
		while (iter.hasNext()) {
			ContractorAuditOperator cao = iter.next();
			if (!operatorSet.contains(cao.getOperator())) {
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

	/**
	 * Determine which categories should be on a given audit and add ones that
	 * aren't there and remove ones that shouldn't be there
	 * 
	 * @param conAudit
	 */
	public void fillAuditCategories(ContractorAudit conAudit, boolean forceRecalculation) {
		// If Bidding Contractor recalculate only for Annual Update
		if (!conAudit.getAuditType().isAnnualAddendum() && conAudit.getContractorAccount().isAcceptsBids()) {
			return;
		}

		// TODO Ask what this is
		/*
		 * if (!forceRecalculation) { if (conAudit.getAuditType().isPqf()) { //
		 * Only Active and Pending PQFs should be recalculated if
		 * (conAudit.getAuditStatus().isSubmitted()) return; if
		 * (conAudit.getAuditStatus().isResubmitted()) return; if
		 * (conAudit.getAuditStatus().isExpired()) return; if
		 * (conAudit.getAuditStatus().isExempt()) return; } else if
		 * (conAudit.getAuditType().isAnnualAddendum()) { if
		 * (!conAudit.getAuditStatus().isPending() &&
		 * !conAudit.getAuditStatus().isIncomplete()) return; } else { // Other
		 * Audits should only consider Pending if
		 * (!conAudit.getAuditStatus().isPending() &&
		 * conAudit.getAuditType().getClassType() == AuditTypeClass.Audit)
		 * return; } }
		 */

		PicsLogger.start("AuditCategories", "auditID=" + conAudit.getId() + " type="
				+ conAudit.getAuditType().getAuditName());
		// set of audit categories to be included in the audit
		Set<AuditCategory> categories = new HashSet<AuditCategory>();
		Set<AuditCategory> naCategories = new HashSet<AuditCategory>();

		List<AuditCategoryRule> categoryRules = auditDecisionTableDAO.getApplicableCategoryRules(conAudit);

		// TODO If the auditType is a Desktop, then make sure the PQF is
		// ActiveSubmitted. Maybe we should add this to the ruleSet

		// TODO Fill in the tag and questions
		// Map<Integer, Integer> dependencies = new HashMap<Integer, Integer>();
		// dependencies.put(AuditCategory.OSHA_AUDIT, 2064);
		// dependencies.put(AuditCategory.MSHA, 2065);
		// dependencies.put(AuditCategory.CANADIAN_STATISTICS, 2066);
		// dependencies.put(AuditCategory.EMR, 2033);
		// dependencies.put(AuditCategory.LOSS_RUN, 2033);
		// dependencies.put(AuditCategory.WCB, 2967);
		// dependencies.put(AuditCategory.CITATIONS, 3546);
		// int auditID = conAudit.getId();
		//
		// AnswerMap answers = null;
		// answers = auditDataDAO.findAnswers(auditID, new
		// Vector<Integer>(dependencies.values()));

		for (AuditCategory category : conAudit.getAuditType().getCategories()) {
			// TODO include the subcategories too (recursively)
			if (isApplicable(categoryRules, category))
				categories.add(category);
			else
				naCategories.add(category);
		}

		PicsLogger.log("Categories to be included: " + categories.size());
		PicsLogger.log("Categories to be not included: " + naCategories.size());

		// Now we know which categories should be there, figure out which ones
		// actually are and make adjustments
		for (AuditCatData catData : conAudit.getCategories()) {
			// TODO What about the overrides? What does it mean to override
			// (show/hide) a category with more than one CAO.
			if (!catData.isOverride()) {
				if (categories.contains(catData.getCategory())) {
					PicsLogger.log(catData.getCategory().getName() + " should be true, was " + catData.isApplies());
					catData.setApplies(true);
				} else {
					PicsLogger.log(catData.getCategory().getName() + " should be false, was " + catData.isApplies());
					catData.setApplies(false);
				}
			}
			// This category is already there, remove it so we don't add it
			// later
			categories.remove(catData.getCategory());
			naCategories.remove(catData.getCategory());
		}

		// Add all remaining applicable categories
		PicsLogger.log("Adding Categories to be included: " + categories.size());
		for (AuditCategory category : categories) {
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
		}
		// Add all remaining N/A categories
		PicsLogger.log("Adding Categories to be not included: " + naCategories.size());
		for (AuditCategory category : naCategories) {
			AuditCatData catData = new AuditCatData();
			catData.setCategory(category);
			catData.setAudit(conAudit);
			catData.setApplies(false);
			catData.setOverride(false);
			catData.setAuditColumns(new User(User.SYSTEM));
			if (category.getNumRequired() == 0)
				catData.setNumRequired(1);
			else
				catData.setNumRequired(category.getNumRequired());
			conAudit.getCategories().add(catData);
		}
		cAuditDAO.save(conAudit);
		PicsLogger.stop();
	}

	/**
	 * Business engine designed to find audits that are about to expire and
	 * rebuild them
	 */
	public void addAuditRenewals() {
		List<ContractorAccount> contractors = cAuditDAO.findContractorsWithExpiringAudits();
		for (ContractorAccount contractor : contractors) {
			try {
				buildAudits(contractor);
			} catch (Exception e) {
				System.out.println("ERROR!! AuditBuiler.addAuditRenewals() " + e.getMessage());
			}
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setFillAuditCategories(boolean fillAuditCategories) {
		this.fillAuditCategories = fillAuditCategories;
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

	private ContractorAudit createAudit(int auditTypeID) {
		AuditType auditType = new AuditType();
		auditType.setId(auditTypeID);
		return createAudit(auditType);
	}

}
