package com.picsauditing.flags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.WaitingOn;

/**
 * This class is not being called from anywhere within the code base, so it is being deprecated.
 */
@Deprecated
public class WaitingOnCalculator {

	public WaitingOn calculateWaitingOn(ContractorOperator co) {

		ContractorAccount contractor = co.getContractorAccount();
		OperatorAccount operator = co.getOperatorAccount();

		if (!contractor.isMaterialSupplierOnly() && contractor.getSafetyRisk() == null)
			return WaitingOn.Contractor;

		if (contractor.isMaterialSupplier() && contractor.getProductRisk() == null)
			return WaitingOn.Contractor;

		if (!contractor.getStatus().isActiveOrDemo())
			return WaitingOn.Contractor; // This contractor is delinquent

		// If Bid Only Account
		if (contractor.getAccountLevel().isBidOnly()) {
			return WaitingOn.Operator;
		}

		// Operator Relationship Approval
		if (!operator.isAutoApproveRelationships()) {
			if (co.isWorkStatusPending())
				// Operator needs to approve/reject this contractor
				return WaitingOn.Operator;
			if (co.isWorkStatusRejected())
				// Operator has already rejected this
				// contractor, and there's nothing else
				// they can do
				return WaitingOn.None;
		}

		// Billing
		if (contractor.isPaymentOverdue())
			return WaitingOn.Contractor; // The contractor has an unpaid
		// invoice due

		// If waiting on contractor, immediately exit, otherwise track the
		// other parties
		boolean waitingOnPics = false;
		boolean waitingOnOperator = false;

		Map<FlagCriteria, List<FlagCriteriaOperator>> operatorCriteria = null;
		for (FlagCriteria key : operatorCriteria.keySet()) {
			FlagCriteriaOperator fOperator = operatorCriteria.get(key).get(0);
			if (!fOperator.getFlag().equals(FlagColor.Green)) {
				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (key.getAuditType().equals(conAudit.getAuditType())) {
						if (!conAudit.isExpired()) {
							// There could be multiple audits for the same
							// operator
							for (ContractorAuditOperator cao : getCaosForOperator(conAudit, operator)) {
								if (cao.getStatus().before(AuditStatus.Submitted)) {
									if (conAudit.getAuditType().isCanContractorEdit())
										return WaitingOn.Contractor;
									OpPerms editPerm = conAudit.getAuditType().getEditPermission();
									if (conAudit.getAuditType().getEditPermission() != null) {
										if (editPerm.isForOperator())
											waitingOnOperator = true;
										else
											waitingOnPics = true;
									} else
										// Assuming that a null permission means
										// "Only PICS" can edit
										waitingOnPics = true;
								} else {
									AuditStatus requiredStatus = key.getRequiredStatus();

									if (cao.getStatus().before(requiredStatus)) {
										if (cao.getStatus().isComplete()) {
											waitingOnOperator = true;
										} else if (conAudit.getAuditType().getId() == AuditType.OFFICE) {
											// either needs to schedule the
											// audit or
											// close out RQs
											return WaitingOn.Contractor;
										} else if (conAudit.getAuditType().getId() == AuditType.DESKTOP
												&& cao.getStatus().isSubmitted()) {
											// contractor needs to close out RQs
											return WaitingOn.Contractor;
										} else {
											waitingOnPics = true;
										}
									}
								}
							}
						}
					}
				} // for
			}
		}
		if (waitingOnPics)
			return WaitingOn.PICS;
		if (waitingOnOperator)
			// only show the operator if contractor and pics are all done
			return WaitingOn.Operator;

		return WaitingOn.None;
	}

	/**
	 * 
	 * @param conAudit
	 * @param operator
	 * @return Usually just a single matching cao record for the given operator
	 */
	private List<ContractorAuditOperator> getCaosForOperator(ContractorAudit conAudit, OperatorAccount operator) {
		List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
				if (caop.getOperator().equals(operator) && cao.isVisible())
					caos.add(cao);
			}
		}

		if (caos.size() > 1) {
			Logger logger = LoggerFactory.getLogger(WaitingOnCalculator.class);
			logger.warn("WARNING: Found {} matching caos for {} on auditID = {}", new Object[] {caos.size(),operator.toString(),conAudit.getId()});
		}

		return caos;
	}

}