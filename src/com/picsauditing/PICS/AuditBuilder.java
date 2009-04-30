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
import java.util.Vector;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.AnswerMap;
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

	private AuditTypeDAO auditTypeDAO;
	private ContractorAccountDAO contractorDAO;
	private ContractorAuditDAO cAuditDAO;
	private AuditDataDAO auditDataDAO;
	private AuditCategoryDAO auditCategoryDAO;
	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	private AuditCategoryDataDAO auditCategoryDataDAO;

	public AuditBuilder(AuditTypeDAO auditTypeDAO, ContractorAccountDAO contractorDAO, ContractorAuditDAO cAuditDAO,
			AuditDataDAO auditDataDAO, AuditCategoryDAO auditCategoryDAO,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO, AuditCategoryDataDAO auditCategoryDataDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.contractorDAO = contractorDAO;
		this.cAuditDAO = cAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.auditCategoryDAO = auditCategoryDAO;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
		this.auditCategoryDataDAO = auditCategoryDataDAO;
	}

	/**
	 * Create new/remove unneeded audits for a given contractor
	 * 
	 * @param conID
	 */
	public void buildAudits(int conID) throws Exception {
		ContractorAccount contractor = contractorDAO.find(conID);
		buildAudits(contractor);
	}

	public void buildAudits(ContractorAccount contractor) {
		PicsLogger.start("BuildAudits", " conID=" + contractor.getId());
		List<ContractorAudit> currentAudits = contractor.getAudits();

		List<AuditStatus> okStatuses = new ArrayList<AuditStatus>();
		okStatuses.add(AuditStatus.Active);
		okStatuses.add(AuditStatus.Pending);
		okStatuses.add(AuditStatus.Submitted);
		okStatuses.add(AuditStatus.Exempt);
		okStatuses.add(AuditStatus.Resubmitted);

		List<Integer> requiresSafetyManual = new ArrayList<Integer>();
		requiresSafetyManual.add(AuditType.DESKTOP);
		requiresSafetyManual.add(AuditType.OFFICE);
		// I think Jesse said to go
		// ahead and create the
		// office audit right away
		requiresSafetyManual.add(AuditType.DA);

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
				ContractorAudit welcomeCall = cAuditDAO.addPending(AuditType.WELCOME, contractor);
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
			pqfAudit = cAuditDAO.addPending(AuditType.PQF, contractor);
			currentAudits.add(pqfAudit);
		}

		/** Add other Audits and Policy Types **/
		// Get a distinct list of AuditTypes that attached operators require
		// Note: this is a lot of iterating over JPA Entities, my assumption
		// is that the operators and auditTypes will be really cached well
		Set<AuditType> auditTypeList = new HashSet<AuditType>();
		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().isActiveB()
					&& (co.getOperatorAccount().getApprovesRelationships().equals(YesNo.No) || co.getWorkStatus()
							.equals("Y"))) {
				for (AuditOperator ao : co.getOperatorAccount().getInheritAudits().getAudits()) {
					if (ao.isRequiredFor(contractor) && !ao.getAuditType().getClassType().isPolicy()) {
						PicsLogger
								.log(co.getOperatorAccount().getName() + " needs " + ao.getAuditType().getAuditName());
						auditTypeList.add(ao.getAuditType());
					}
				}
				if (co.getOperatorAccount().getCanSeeInsurance().equals(YesNo.Yes)) {
					// If the operator uses insurance, then look to see if any audits are enabled
					for (AuditOperator ao : co.getOperatorAccount().getInheritInsurance().getAudits()) {
						if (ao.isRequiredFor(contractor) && ao.getAuditType().getClassType().isPolicy()) {
							PicsLogger.log(co.getOperatorAccount().getName() + " needs "
									+ ao.getAuditType().getAuditName());
							auditTypeList.add(ao.getAuditType());
						}
					}
				}
			}
		}

		int year = DateBean.getCurrentYear();
		for (AuditType auditType : auditTypeList) {
			if (auditType.getId() == AuditType.DA && !"Yes".equals(contractor.getOqEmployees())) {
				// Don't add the D&A audit because this contractor
				// doesn't have employees subject OQ requirements
				// Note: we could also just add a place holder "Exempt" audit
				// just as well
				continue;
			}

			if (auditType.getId() == AuditType.ANNUALADDENDUM) {
				List<ContractorAudit> cList = contractor.getAudits();
				addAnnualAddendum(cList, contractor, year - 1, auditType, currentAudits);
				addAnnualAddendum(cList, contractor, year - 2, auditType, currentAudits);
				addAnnualAddendum(cList, contractor, year - 3, auditType, currentAudits);
			} else {
				boolean found = false;
				// For each audit this contractor SHOULD have
				// Figure out if the contractor currently has an
				// active audit that isn't scheduled to expire soon
				for (ContractorAudit conAudit : currentAudits) {
					if (conAudit.getAuditType().equals(auditType)
							|| (AuditType.NCMS == conAudit.getAuditType().getId() && auditType.isDesktop())) {
						// We found a matching audit for this requirement
						// Now determine if it will be good enough
						if (auditType.isRenewable()) {
							if (okStatuses.contains(conAudit.getAuditStatus()) && !conAudit.willExpireSoon())
								// The audit is still valid for at least another 60 days
								found = true;
						} else {
							// This audit should not be renewed but we already have one
							found = true;
						}
					}
				}

				if (!found) {
					// The audit wasn't found, figure out if we should
					// create it now or wait until later
					boolean insertNow = true;
					if (pqfAudit.getAuditStatus().equals(AuditStatus.Pending)) {
						// The current PQF is stilling pending, does this audit
						// require a PDF? (Desktop, Office, or D&A)
						if (requiresSafetyManual.contains(auditType.getId()))
							insertNow = false;
					}

					if (auditType.getId() == AuditType.DESKTOP
							&& pqfAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
						// The current PQF has been submitted, but we need to know
						// if the Safety Manual is there first before creating the desktop
						// TODO add field to the contractorAccount
						AuditData safetyManual = auditDataDAO.findAnswerToQuestion(pqfAudit.getId(),
								AuditQuestion.MANUAL_PQF);
						if (safetyManual == null || !safetyManual.isVerified())
							insertNow = false;
					}
					if (insertNow) {
						System.out.println("Adding: " + auditType.getId() + auditType.getAuditName());
						ContractorAudit pendingToInsert = cAuditDAO.addPending(auditType, contractor);
						currentAudits.add(pendingToInsert);

					} else
						System.out.println("Skipping: " + auditType.getId() + auditType.getAuditName());
				}
			}
		}

		/** ** Remove unneeded audits *** */
		// We can't delete until we're done reading all data from DB (lazy
		// loading)
		Set<Integer> auditsToRemove = new HashSet<Integer>();

		// You have to use while iterator instead of a for loop
		// because we're removing an entry from the list
		Iterator<ContractorAudit> iter = currentAudits.iterator();
		while (iter.hasNext()) {
			ContractorAudit conAudit = iter.next();
			if (conAudit.getAuditStatus().equals(AuditStatus.Pending) && conAudit.getPercentComplete() == 0
					&& !conAudit.isManuallyAdded()) {
				// This auto audit hasn't been started yet, double check to make
				// sure
				// it's still needed
				boolean needed = false;

				if (conAudit.getAuditType().isPqf() || conAudit.getAuditType().getId() == AuditType.WELCOME
						|| conAudit.getAuditType().isAnnualAddendum())
					needed = true;

				// this doesn't handle one rare case: when a
				// contractor changes their OQ employees from Yes to No
				// Since this would probably never happen, we won't add it
				for (AuditType auditType : auditTypeList) {
					if (conAudit.getAuditType().equals(auditType)) {
						needed = true;
					}
				}

				if (!needed) {
					if (conAudit.getData().size() == 0) {
						auditsToRemove.add(conAudit.getId());
						iter.remove();
					}
				}
			}
		}

		for (Integer auditID : auditsToRemove) {
			cAuditDAO.remove(auditID);
			fillAuditCategories = false;
		}

		if (fillAuditCategories) {
			/** Generate Categories * */
			for (ContractorAudit conAudit : currentAudits) {
				fillAuditOperators(contractor, conAudit);
				fillAuditCategories(conAudit);
			}
		}
		PicsLogger.stop();
	}

	/**
	 * For each audit (policy), get a list of operators who have InsureGuard and automatically require this policy,
	 * based on riskLevel
	 * 
	 * @param conAudit
	 */
	private void fillAuditOperators(ContractorAccount contractor, ContractorAudit conAudit) {
		if (!AuditTypeClass.Policy.equals(conAudit.getAuditType().getClassType()))
			return;

		PicsLogger.start("AuditOperators", conAudit.getAuditType().getAuditName());

		PicsLogger.log("Get a distinct set of (inherited) operators that are active and require insurance.");
		Set<OperatorAccount> operatorSet = new HashSet<OperatorAccount>();
		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().isActiveB() && co.getOperatorAccount().getCanSeeInsurance().equals(YesNo.Yes))
				operatorSet.add(co.getOperatorAccount().getInheritInsuranceCriteria());
		}

		for (OperatorAccount operator : operatorSet) {
			// For this auditType (General Liability) and
			// this contractor's associated operator (BP Cherry Point)

			boolean visible = false;
			boolean required = false;

			// conAudit.getRequestingOpAccount()
			// NOTE!!! I've removed the requesting op account functionality
			// I don't think it's very common for an operator to need specific/custom policies only for that facility
			// If a facility needs "Pollution" and the contractor adds it,
			// then other operators should be able to see it as well that subscribe to Pollution insurance

			PicsLogger.log(operator.getName() + " subscribes to InsureGuard");
			for (AuditOperator ao : operator.getAudits()) {
				if (conAudit.getAuditType().equals(ao.getAuditType()) && ao.isCanSee()) {
					visible = true;
					if (ao.getMinRiskLevel() > 0 && ao.getMinRiskLevel() <= contractor.getRiskLevel().ordinal())
						required = true;
					PicsLogger.log(contractor.getName() + " can see " + (required ? "required " : " ")
							+ ao.getAuditType().getAuditName());
					break;
				}
			}

			// Now find the existing cao record for this operator (if one exists)
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
					if (required) {
						// This cao is always required so add it if it doesn't exist
						// and then calculate the recommended status
						cao.setStatus(CaoStatus.Pending);
						cao.setRecommendedStatus(CaoStatus.Pending);
					} else {
						// This cao might be required (if the operator manually requested it)
						cao.setStatus(CaoStatus.NotApplicable);
						cao.setRecommendedStatus(CaoStatus.NotApplicable);
					}
				}
				if (CaoStatus.NotApplicable.equals(cao.getStatus())) {
					// This operator has specifically stated they don't need this policy
					cao.setRecommendedStatus(CaoStatus.NotApplicable);
				}

			} else if (cao != null) {
				// This existing cao is either no longer needed by the operator (ie audit/operator matrix)
				// or it has one or more parents that have already made a decision on this policy

				// Remove the cao if it's Awaiting
				if (cao.getStatus().equals(CaoStatus.Pending)) {
					PicsLogger.log("Removing unneeded ContractorAuditOperator " + cao.getId());
					conAudit.getOperators().remove(cao);
					contractorAuditOperatorDAO.remove(cao);
					cao = null;
				} else {
					cao.setRecommendedStatus(CaoStatus.NotApplicable);
				}
			}
			if (cao != null) {
				// If we still have a "dirty" cao record, then save it
				contractorAuditOperatorDAO.save(cao);
			}
		}

		PicsLogger.stop();
	}

	/**
	 * Determine which categories should be on a given audit and add ones that aren't there and remove ones that
	 * shouldn't be there
	 * 
	 * @param conAudit
	 */
	public void fillAuditCategories(ContractorAudit conAudit) {
		if (conAudit.getAuditType().isPqf()) {
			// Only Active and Pending PQFs should be recalculated
			if (conAudit.getAuditStatus().equals(AuditStatus.Submitted))
				return;
			if (conAudit.getAuditStatus().equals(AuditStatus.Resubmitted))
				return;
			if (conAudit.getAuditStatus().equals(AuditStatus.Expired))
				return;
			if (conAudit.getAuditStatus().equals(AuditStatus.Exempt))
				return;
		} else {
			// Other Audits should only consider Pending
			if (!conAudit.getAuditStatus().isPending()
					&& conAudit.getAuditType().getClassType() == AuditTypeClass.Audit)
				return;
		}

		PicsLogger.start("AuditCategories", "auditID=" + conAudit.getId() + " type="
				+ conAudit.getAuditType().getAuditName());
		// set of audit categories to be included in the audit
		Set<AuditCategory> categories = new HashSet<AuditCategory>();
		Set<AuditCategory> naCategories = new HashSet<AuditCategory>();

		if (conAudit.getAuditType().isPqf()) {
			List<AuditCategory> pqfCategories = auditCategoryDAO.findPqfCategories(conAudit.getContractorAccount());
			categories.addAll(pqfCategories);

			List<AuditCategory> allCategories = auditCategoryDAO.findByAuditTypeID(conAudit.getAuditType().getId());
			for (AuditCategory category : allCategories) {
				if (!categories.contains(category))
					naCategories.add(category);
			}
		} else if (conAudit.getAuditType().isAnnualAddendum()) {

			Map<Integer, Integer> dependencies = new HashMap<Integer, Integer>();
			dependencies.put(AuditCategory.OSHA_AUDIT, 2064);
			dependencies.put(AuditCategory.MSHA, 2065);
			dependencies.put(AuditCategory.CANADIAN_STATISTICS, 2066);
			dependencies.put(AuditCategory.EMR, 2033);
			dependencies.put(AuditCategory.LOSS_RUN, 2033);
			int auditID = conAudit.getId();

			AnswerMap answers = null;
			answers = auditDataDAO.findAnswers(auditID, new Vector<Integer>(dependencies.values()));

			for (AuditCategory cat : conAudit.getAuditType().getCategories()) {

				boolean include = false;

				if (answers != null && dependencies.get(cat.getId()) != null) {
					AuditData answer = null;
					try {
						int questionID = dependencies.get(cat.getId());
						answer = answers.get(questionID);
					} catch (NullPointerException ignoreNulls) {
					}

					if (answer == null)
						include = false;
					else {

						if (answer.getQuestion().getId() == 2033) {

							if ("No".equals(answer.getAnswer()) && cat.getId() == AuditCategory.LOSS_RUN) {
								include = true;
							} else if ("Yes".equals(answer.getAnswer()) && cat.getId() == AuditCategory.EMR) {
								include = true;
							}
						} else {
							if ("Yes".equals(answer.getAnswer())) {
								include = true;
							}
						}
					}

				} else if (dependencies.get(cat.getId()) == null) {
					include = true;
				}

				if (include) {
					categories.add(cat);
				}

			}
			Iterator<AuditCatData> iterator = conAudit.getCategories().iterator();
			while (iterator.hasNext()) {
				AuditCatData auditCatData = iterator.next();
				if (removeCategory("No", answers.get(2064), auditCatData, AuditCategory.OSHA_AUDIT)
						|| removeCategory("No", answers.get(2065), auditCatData, AuditCategory.MSHA)
						|| removeCategory("No", answers.get(2066), auditCatData, AuditCategory.CANADIAN_STATISTICS)
						|| removeCategory("No", answers.get(2033), auditCatData, AuditCategory.EMR)
						|| removeCategory("Yes", answers.get(2033), auditCatData, AuditCategory.LOSS_RUN)) {
					iterator.remove();
					auditCategoryDataDAO.remove(auditCatData.getId());
				}
			}
		}

		else if (conAudit.getAuditType().getId() == AuditType.DESKTOP) {
			Date currentAuditDate = null;
			int pqfAuditID = 0;
			for (ContractorAudit audits : conAudit.getContractorAccount().getAudits()) {
				if (audits.getAuditType().isPqf()
						&& (audits.getAuditStatus().equals(AuditStatus.Active) || audits.getAuditStatus().equals(
								AuditStatus.Submitted))) {
					// Found a Submitted/Active PQF
					if (currentAuditDate == null || audits.getCompletedDate().after(currentAuditDate)) {
						// TODO we can only have 1 pqf anway so remove this logic
						// Found the most recent one
						currentAuditDate = audits.getCompletedDate();
						pqfAuditID = audits.getId();
					}
				}
			}
			if (pqfAuditID > 0) {
				List<AuditCategory> desktopCategories = auditCategoryDAO.findDesktopCategories(pqfAuditID);
				categories.addAll(desktopCategories);
			}

			List<AuditCategory> allCategories = auditCategoryDAO.findByAuditTypeID(conAudit.getAuditType().getId());
			PicsLogger.log("Categories to be included:");
			for (AuditCategory category : categories)
				PicsLogger.log("  " + category.getId() + " " + category.getCategory());

			for (AuditCategory category : allCategories) {
				if (!categories.contains(category)) {
					PicsLogger.log("Don't include  " + category.getCategory());
					naCategories.add(category);
				} else
					PicsLogger.log("Include  " + category.getCategory());
			}
		} else {
			categories.addAll(conAudit.getAuditType().getCategories());
		}

		PicsLogger.log("Categories to be included: " + categories.size());
		PicsLogger.log("Categories to be not included: " + naCategories.size());

		// Now we know which categories should be there, figure out which ones
		// actually are and make adjustments
		for (AuditCatData catData : conAudit.getCategories()) {
			if (!catData.isOverride()) {
				if (categories.contains(catData.getCategory())) {
					PicsLogger.log(catData.getCategory().getCategory() + " should be Yes, was " + catData.getApplies());
					catData.setApplies(YesNo.Yes);
				} else {
					PicsLogger.log(catData.getCategory().getCategory() + " should be No, was " + catData.getApplies());
					catData.setApplies(YesNo.No);
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
			catData.setApplies(YesNo.Yes);
			catData.setOverride(false);
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
			catData.setApplies(YesNo.No);
			catData.setOverride(false);
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
	 * Business engine designed to find audits that are about to expire and rebuild them
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

	public void addAnnualAddendum(List<ContractorAudit> cList, ContractorAccount contractor, int year,
			AuditType auditType, List<ContractorAudit> currentAudits) {
		boolean found = false;
		for (ContractorAudit cAudit : cList) {
			if (cAudit.getAuditType().getId() == AuditType.ANNUALADDENDUM
					&& year == Integer.parseInt(cAudit.getAuditFor())) {
				if (cAudit.getAuditStatus().equals(AuditStatus.Expired))
					// this should never happen actually...but just incase
					cAudit.changeStatus(AuditStatus.Pending, user);
				found = true;
			}
		}
		if (!found) {
			Calendar startDate = Calendar.getInstance();
			startDate.set(year, 11, 31);
			System.out.println("Adding: " + auditType.getId() + auditType.getAuditName());
			currentAudits.add(cAuditDAO.addPending(auditType, contractor, Integer.toString(year), startDate.getTime()));
		}
	}

	public boolean removeCategory(String answer, AuditData auditData, AuditCatData auditCatData, int categoryID) {
		if (auditData != null) {
			if (answer.equals(auditData.getAnswer()) && auditCatData.getCategory().getId() == categoryID)
				return true;
		}
		return false;
	}
}
