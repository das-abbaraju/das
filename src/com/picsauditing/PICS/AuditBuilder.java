package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.YesNo;

/**
 * Properly add/remove all necessary audits for a given contractor
 * @author Trevor
 *
 */
public class AuditBuilder {
	private boolean debug = false;
	private boolean fillAuditCategories = true;
	
	private AuditTypeDAO auditTypeDAO;
	private ContractorAccountDAO contractorDAO;
	private ContractorAuditDAO cAuditDAO;
	private AuditDataDAO auditDataDAO;
	private AuditCategoryDAO auditCategoryDAO;

	public AuditBuilder(AuditTypeDAO auditTypeDAO, ContractorAccountDAO contractorDAO, ContractorAuditDAO cAuditDAO,
			AuditDataDAO auditDataDAO, AuditCategoryDAO auditCategoryDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.contractorDAO = contractorDAO;
		this.cAuditDAO = cAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.auditCategoryDAO = auditCategoryDAO;
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
		List<ContractorAudit> currentAudits = contractor.getAudits();

		List<AuditStatus> okStatuses = new ArrayList<AuditStatus>();
		okStatuses.add(AuditStatus.Active);
		okStatuses.add(AuditStatus.Pending);
		okStatuses.add(AuditStatus.Submitted);
		okStatuses.add(AuditStatus.Exempt);

		List<Integer> requiresSafetyManual = new ArrayList<Integer>();
		requiresSafetyManual.add(AuditType.DESKTOP);
		requiresSafetyManual.add(AuditType.OFFICE); // I think Jesse said to go ahead and create the office audit right away
		requiresSafetyManual.add(AuditType.DA);

		/** *** Welcome Call *** */
		if (DateBean.getDateDifference(contractor.getDateCreated()) > -90) {
			// Create the welcome call for all accounts created in the past 90
			// days
			boolean needsWelcome = true;
			for (ContractorAudit conAudit : currentAudits) {
				if (conAudit.getAuditType().getAuditTypeID() == AuditType.WELCOME) {
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
		ContractorAudit pqfAudit = null;
		for (ContractorAudit conAudit : currentAudits) {
			if (conAudit.getAuditType().isPqf()) {
				if (okStatuses.contains(conAudit.getAuditStatus())) {
					// Contractor already has a PQF, don't add it
					// TODO: handle multiple PQFs (Pending/Active) at the same
					// time
					pqfAudit = conAudit;
					break;
				}
			}
		}
		if (pqfAudit == null) {
			pqfAudit = cAuditDAO.addPending(AuditType.PQF, contractor);
			currentAudits.add(pqfAudit);
		}

		/** * Add other Audits ** */
		// Get a distinct list of AuditTypes that attached operators require
		List<AuditType> list = auditTypeDAO.findWhere("t IN (SELECT auditType "
				+ "FROM AuditOperator WHERE canSee=1 AND minRiskLevel BETWEEN 1 AND "
				+ contractor.getRiskLevel().ordinal() + " " + "AND operatorAccount IN ("
				+ "SELECT operatorAccount FROM ContractorOperator " + "WHERE contractorAccount.id = "
				+ contractor.getId() + "))");
		int year = DateBean.getCurrentYear();
		for (AuditType auditType : list) {
			if (auditType.getAuditTypeID() == AuditType.DA
					&& !"Yes".equals(contractor.getOqEmployees())) {
				// Don't add the D&A audit because this contractor 
				// doesn't have employees subject OQ requirements
				// Note: we could also just add a place holder "Exempt" audit just as well
				continue;
			}
			
			if(auditType.getAuditTypeID() == AuditType.ANNUALADDENDUM) {
				List<ContractorAudit> cList = contractor.getAudits();
				addAnnualAddendum(cList, contractor, year-1, auditType, currentAudits);	
				addAnnualAddendum(cList, contractor, year-2, auditType, currentAudits);	
				addAnnualAddendum(cList, contractor, year-3, auditType, currentAudits);	
			}
			else {
				boolean found = false;
				// For each audit this contractor SHOULD have
				// Figure out if the contractor currently has an
				// active audit that isn't scheduled to expire soon
				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (okStatuses.contains(conAudit.getAuditStatus())) {
						// The contractor audit is not expired
						int daysToExpiration = 0;
						if (conAudit.getExpiresDate() == null)
							// The expiration is null so put the days to
							// expiration
							// really big
							daysToExpiration = 1000;
						else
							daysToExpiration = DateBean.getDateDifference(conAudit.getExpiresDate());

						if (daysToExpiration > 60) {
							// The audit is still valid for atleast another 60
							// days
							if (conAudit.getAuditType().equals(auditType)) {
								// We found a matching audit type
								found = true;
							}
							if (AuditType.NCMS == conAudit.getAuditType().getAuditTypeID()
									&& AuditType.DESKTOP == auditType.getAuditTypeID()) {
								// We needed a desktop and found an NCMS audit
								found = true;
							}
						}
					}
				}

				if (!found) {
					// The audit wasn't found, figure out if we should create it
					// now
					// or wait until later
					boolean insertNow = true;
					if (pqfAudit.getAuditStatus().equals(AuditStatus.Pending)) {
						// The current PQF is stilling pending, does this audit
						// require a PDF?
						if (requiresSafetyManual.contains(auditType.getAuditTypeID()))
							insertNow = false;
					}
					if (auditType.getAuditTypeID() == AuditType.DESKTOP
							&& pqfAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
						// The current PQF has been submitted, but we need to
						// know
						// if the Safety Manual is there first before creating
						// the
						// desktop
						AuditData safetyManual = auditDataDAO.findAnswerToQuestion(pqfAudit.getId(),
								AuditQuestion.MANUAL_PQF);
						if (safetyManual == null || !safetyManual.isVerified())
							insertNow = false;
					}
					if (insertNow) {
						System.out.println("Adding: " + auditType.getAuditTypeID() + auditType.getAuditName());
						currentAudits.add(cAuditDAO.addPending(auditType, contractor));
					} else
						System.out.println("Skipping: " + auditType.getAuditTypeID() + auditType.getAuditName());
				}
			}
		}	

		/** ** Remove unneeded audits *** */
		// We can't clear until we're done reading all data from DB (lazy loading)
		// But we can't delete until we do the clear
		// so create a list, then delete
		Set<Integer> auditsToRemove = new HashSet<Integer>();
		
		// You have to use while iterator instead of a for loop
		// because we're removing an entry from the list
		Iterator<ContractorAudit> iter = currentAudits.iterator();
		while (iter.hasNext()) {
			ContractorAudit conAudit = iter.next();
			if (conAudit.getAuditStatus().equals(AuditStatus.Pending) && conAudit.getPercentComplete() == 0 && !conAudit.isManuallyAdded()) {
				// This audit hasn't been started yet, double check to make sure
				// it's still needed
				boolean needed = false;

				if (conAudit.getAuditType().isPqf())
					needed = true;
				if (conAudit.getAuditType().getAuditTypeID() == AuditType.WELCOME)
					needed = true;

				// this doesn't handle one rare case: when a 
				// contractor changes their OQ employees from Yes to No
				// Since this would probably never happen, we won't add it
				for (AuditType auditType : list) {
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
			cAuditDAO.clear();
			cAuditDAO.remove(auditID);
			fillAuditCategories = false;
		}

		if (fillAuditCategories) {
			/** Generate Categories * */
			for (ContractorAudit conAudit : currentAudits) {
				fillAuditCategories(conAudit);
			}
		}
	}

	/**
	 * Determine which categories should be on a given audit 
	 * and add ones that aren't there and remove ones that shouldn't be there
	 * @param conAudit
	 */
	public void fillAuditCategories(ContractorAudit conAudit) {
		// set of audit categories to be included in the audit
		Set<AuditCategory> categories = new HashSet<AuditCategory>();
		Set<AuditCategory> naCategories = new HashSet<AuditCategory>();
		
		if (conAudit.getAuditType().isPqf()) {
			List<AuditCategory> pqfCategories = auditCategoryDAO.findPqfCategories(conAudit.getContractorAccount());
			categories.addAll(pqfCategories);
			
			List<AuditCategory> allCategories = auditCategoryDAO.findByAuditTypeID(conAudit.getAuditType().getAuditTypeID());
			for(AuditCategory category : allCategories) {
				if (!categories.contains(category))
					naCategories.add(category);
			}
		}
		else if (conAudit.getAuditType().getAuditTypeID() == AuditType.DESKTOP) {
			Date currentAuditDate = null;
			int pqfAuditID = 0;
			for (ContractorAudit audits : conAudit.getContractorAccount().getAudits()) {
				if (audits.getAuditType().isPqf()
						&& (audits.getAuditStatus().equals(AuditStatus.Active) || audits.getAuditStatus().equals(
								AuditStatus.Submitted))) {
					 // Found a Submitted/Active PQF
					if (currentAuditDate == null || audits.getCompletedDate().after(currentAuditDate)) {
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
			
			List<AuditCategory> allCategories = auditCategoryDAO.findByAuditTypeID(conAudit.getAuditType().getAuditTypeID());
			debug("Categories to be included:");
			for(AuditCategory category : categories)
				debug("  " + category.getId() + " " + category.getCategory());
			
			for(AuditCategory category : allCategories) {
				if (!categories.contains(category)) {
					debug("Don't include  " + category.getCategory());
					naCategories.add(category);
				} else
					debug("Include  " + category.getCategory());
			}
		} else {
			categories.addAll(conAudit.getAuditType().getCategories());
		}

		debug("Categories to be included: "+categories.size());
		debug("Categories to be not included: "+naCategories.size());

		// Now we know which categories should be there, figure out which ones actually are and make adjustments
		for(AuditCatData catData : conAudit.getCategories()) {
			if (!catData.isOverride()) {
				if (categories.contains(catData.getCategory())) {
					debug(catData.getCategory().getCategory() + " should be Yes, was "+catData.getApplies());
					catData.setApplies(YesNo.Yes);
				} else {
					debug(catData.getCategory().getCategory() + " should be No, was "+catData.getApplies());
					catData.setApplies(YesNo.No);
				}
			}
			// This category is already there, remove it so we don't add it later
			categories.remove(catData.getCategory());
			naCategories.remove(catData.getCategory());
		}
		
		// Add all remaining applicable categories
		debug("Adding Categories to be included: "+categories.size());
		for(AuditCategory category : categories) {
			AuditCatData catData = new AuditCatData();
			catData.setCategory(category);
			catData.setAudit(conAudit);
			catData.setApplies(YesNo.Yes);
			catData.setOverride(false);
			conAudit.getCategories().add(catData);
		}
		// Add all remaining N/A categories
		debug("Adding Categories to be not included: "+naCategories.size());
		for(AuditCategory category : naCategories) {
			AuditCatData catData = new AuditCatData();
			catData.setCategory(category);
			catData.setAudit(conAudit);
			catData.setApplies(YesNo.No);
			catData.setOverride(false);
			conAudit.getCategories().add(catData);
		}
		cAuditDAO.save(conAudit);
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
	
	private void debug(String message) {
		if (debug)
			System.out.println("Debug AuditBuilder: " + message);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setFillAuditCategories(boolean fillAuditCategories) {
		this.fillAuditCategories = fillAuditCategories;
	}
	
	public void addAnnualAddendum(List<ContractorAudit> cList, ContractorAccount contractor, int year, AuditType auditType, List<ContractorAudit> currentAudits) {
		boolean found = false;
		for (ContractorAudit cAudit : cList) {
			if (cAudit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM
					&& year == Integer.parseInt(cAudit.getAuditFor())) {
				if (cAudit.getAuditStatus().equals(AuditStatus.Expired))
					// this should never happen actually...but just incase
					cAudit.setAuditStatus(AuditStatus.Pending);
				found = true;
			}
		}
		if (!found) {
			Calendar startDate = Calendar.getInstance();
			startDate.set(year, 11, 31);
			System.out.println("Adding: " + auditType.getAuditTypeID() + auditType.getAuditName());
			currentAudits.add(cAuditDAO.addPending(auditType, contractor, Integer.toString(year), startDate.getTime()));
		}
	}
}
