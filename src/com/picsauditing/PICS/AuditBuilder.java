package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.pqf.CategoryBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditBuilder {
	AuditTypeDAO auditTypeDAO;
	ContractorAccountDAO contractorDAO;
	ContractorAuditDAO cAuditDAO;
	AuditDataDAO auditDataDAO;
	
	public AuditBuilder(AuditTypeDAO auditTypeDAO, ContractorAccountDAO contractorDAO, 
			ContractorAuditDAO cAuditDAO, AuditDataDAO auditDataDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.contractorDAO = contractorDAO;
		this.cAuditDAO = cAuditDAO;
		this.auditDataDAO = auditDataDAO;
	}

	/**
	 * Create new/remove unneeded audits for a given contractor
	 * @param conID
	 */
	public void buildAudits(int conID) throws Exception {
		ContractorAccount contractor = contractorDAO.find(conID);
		buildAudits(contractor);
	}
	
	public void buildAudits(ContractorAccount contractor) throws Exception {
		List<ContractorAudit> currentAudits = contractor.getAudits();
		
		
		List<AuditStatus> okStatuses = new ArrayList<AuditStatus>();
		okStatuses.add(AuditStatus.Active);
		okStatuses.add(AuditStatus.Pending);
		okStatuses.add(AuditStatus.Submitted);
		okStatuses.add(AuditStatus.Exempt);

		List<Integer> requiresSafetyManual = new ArrayList<Integer>();
		requiresSafetyManual.add(AuditType.DESKTOP);
		requiresSafetyManual.add(AuditType.OFFICE);
		requiresSafetyManual.add(AuditType.DA);

		/***** Welcome Call ****/
		if (DateBean.getDateDifference(contractor.getDateCreated()) > -90) {
			// Create the welcome call for all accounts created in the past 90 days
			boolean needsWelcome = true;
			for(ContractorAudit conAudit : currentAudits) {
				if (conAudit.getAuditType().getAuditTypeID() == AuditType.WELCOME) {
					needsWelcome = false;
				}
			}
			if (needsWelcome) {
				ContractorAudit welcomeCall = cAuditDAO.addPending(AuditType.WELCOME, contractor);
				currentAudits.add(welcomeCall);
			}
		}
		
		/***** PQF ****/
		ContractorAudit pqfAudit = null;
		for(ContractorAudit conAudit : currentAudits) {
			if (conAudit.getAuditType().isPqf()) {
				if (okStatuses.contains(conAudit.getAuditStatus())) {
					// Contractor already has a PQF, don't add it
					// TODO: handle multiple PQFs (Pending/Active) at the same time
					pqfAudit = conAudit;
				}
			}
		}
		if (pqfAudit == null) {
			pqfAudit = cAuditDAO.addPending(AuditType.PQF, contractor);
			currentAudits.add(pqfAudit);
		}
		
		/*** Add other Audits ***/
		// Get a distinct list of AuditTypes that attached operators require
		List<AuditType> list = auditTypeDAO.findWhere("t IN (SELECT auditType " +
				"FROM AuditOperator WHERE canSee=1 AND minRiskLevel BETWEEN 1 AND "+contractor.getRiskLevel().ordinal()+" " +
						"AND operatorAccount IN (" +
					"SELECT operatorAccount FROM ContractorOperator " +
					"WHERE contractorAccount.id = "+contractor.getId()+
				"))");
		for(AuditType auditType : list) {
			
			// For each audit this contractor SHOULD have
			// Figure out if the contractor currently has an  
			// active audit that isn't scheduled to expire soon
			boolean found = false;
			for(ContractorAudit conAudit : contractor.getAudits()) {
				if (okStatuses.contains(conAudit.getAuditStatus())) {
					// The contractor audit is not expired
					int daysToExpiration = 0;
					if (conAudit.getExpiresDate() == null)
						// The expiration is null so put the days to expiration really big
						daysToExpiration = 1000;
					else
						daysToExpiration = DateBean.getDateDifference(conAudit.getExpiresDate());
					
					if (daysToExpiration > 60) {
						// The audit is still valid for atleast another 60 days
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
				// The audit wasn't found, figure out if we should create it now or wait until later
				boolean insertNow = true;
				if (pqfAudit.getAuditStatus().equals(AuditStatus.Pending)) {
					// The current PQF is stilling pending, does this audit require a PDF?
					if (requiresSafetyManual.contains(auditType.getAuditTypeID()))
						insertNow = false;
				}
				if (pqfAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
					// The current PQF has been submitted, but we need to know if the Safety Manual is there first
					AuditData safetyManual = auditDataDAO.findAnswerToQuestion(pqfAudit.getId(), AuditQuestion.MANUAL_PQF);
					if (safetyManual == null || !safetyManual.isVerified())
						insertNow = false;
				}
				
				if (insertNow) {
					System.out.println("Adding: "+ auditType.getAuditTypeID() + auditType.getAuditName());
					currentAudits.add(cAuditDAO.addPending(auditType, contractor));
				} else
					System.out.println("Skipping: "+ auditType.getAuditTypeID() + auditType.getAuditName());
			}
		}

		/**** Remove unneeded audits ****/
		for(ContractorAudit conAudit : currentAudits) {
			if (conAudit.getAuditStatus().equals(AuditStatus.Pending) && conAudit.getPercentComplete() == 0) {
				// This audit hasn't been started yet, double check to make sure it's still needed
				boolean needed = false;
				
				if (conAudit.getAuditType().isPqf())
					needed = true;
				if (conAudit.getAuditType().getAuditTypeID() == AuditType.WELCOME)
					needed = true;
				
				for(AuditType auditType : list) {
					if (conAudit.getAuditType().equals(auditType)) {
						needed = true;
					}
				}
				
				if (!needed) {
					if (conAudit.getData().size() == 0) {
						cAuditDAO.clear();
						cAuditDAO.remove(conAudit.getId());
						currentAudits.remove(conAudit);
					}
				}
			}
		}
		
		/** Generate Categories **/
		for(ContractorAudit conAudit : currentAudits) {
			if (conAudit.getAuditType().isPqf()
				|| conAudit.getAuditType().getAuditTypeID() == AuditType.DESKTOP
				|| true) {
				CategoryBean categoryBean = new CategoryBean();
				categoryBean.generateDynamicCategories(conAudit);
			}
		}
	}
	
	/**
	 * Business engine designed to find audits that are about to expire and rebuild them
	 */
	public void addAuditRenewals() {
		List<ContractorAccount> contractors = cAuditDAO.findContractorsWithExpiringAudits();
		for(ContractorAccount contractor : contractors) {
			try {
				buildAudits(contractor);
			} catch (Exception e) {
				System.out.println("ERROR!! AuditBuiler.addAuditRenewals() " + e.getMessage());
			}
		}
	}
}
