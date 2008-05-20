package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditBuilder {
	@Autowired
	AuditTypeDAO auditTypeDAO;
	@Autowired
	ContractorAccountDAO contractorDAO;
	@Autowired
	ContractorAuditDAO cAuditDAO;

	/**
	 * Create new/remove unneeded audits for a given contractor
	 * @param conID
	 */
	public void buildAudits(int conID) {
		ContractorAccount contractor = contractorDAO.find(conID);

		List<ContractorAudit> currentAudits = contractor.getAudits();
		
		List<AuditStatus> okStatuses = new ArrayList<AuditStatus>();
		okStatuses.add(AuditStatus.Active);
		okStatuses.add(AuditStatus.Pending);
		okStatuses.add(AuditStatus.Submitted);
		okStatuses.add(AuditStatus.Exempt);
		
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
		List<AuditType> list = auditTypeDAO.findWhere("t IN (SELECT auditType " +
				"FROM AuditOperator WHERE operatorAccount IN (" +
					"SELECT operatorAccount FROM ContractorOperator " +
					"WHERE contractorAccount.id = "+conID+
				"))");
		for(AuditType auditType : list) {
			boolean canCreate = true;
			if (pqfAudit.getAuditStatus().equals(AuditStatus.Pending)) {
				// The current PQF is stilling pending
				if (AuditType.DESKTOP == auditType.getAuditTypeID())
					canCreate = false;
				if (AuditType.OFFICE == auditType.getAuditTypeID())
					canCreate = false;
				if (AuditType.DA == auditType.getAuditTypeID())
					canCreate = false;
			}
			
			if (canCreate) {
				System.out.println("AuditType: "+ auditType.getAuditTypeID() + auditType.getAuditName());
				boolean found = false;
				for(ContractorAudit conAudit : contractor.getAudits()) {
					if (okStatuses.contains(conAudit.getAuditStatus())) {
						// The contractor audit is not expired
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
				if (!found) {
					System.out.println(" is missing");
					currentAudits.add(cAuditDAO.addPending(auditType, contractor));
				}
			} else
				System.out.println("Skipping: "+ auditType.getAuditTypeID() + auditType.getAuditName());
		}
		
		/**** Remove unneeded audits ****/
		for(ContractorAudit conAudit : currentAudits) {
			if (conAudit.getAuditStatus().equals(AuditStatus.Pending) && conAudit.getPercentComplete() == 0) {
				// This audit hasn't been started yet, double check to make sure it's still needed
				boolean needed = false;
				
				for(AuditType auditType : list) {
					if (conAudit.getAuditType().equals(auditType)) {
						needed = true;
					}
				}
				
				if (!needed) {
					cAuditDAO.remove(conAudit);
					currentAudits.remove(conAudit);
				}
			}
		}
		
	}
}
