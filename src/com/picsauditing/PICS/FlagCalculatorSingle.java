package com.picsauditing.PICS;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;

/**
 * Determine the Flag color for a single contractor at a given facility. This
 * doesn't persist any data nor does it query the database for the required
 * data.
 * 
 * @author Trevor
 */
public class FlagCalculatorSingle {
	private boolean debug = false;
	private boolean answerOnly = true;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private List<ContractorAudit> conAudits;
	private Map<Integer, Map<String, AuditData>> auditAnswers;

	/**
	 * 1) Check to see all required audits are there 2) OSHA Data 3)
	 * AuditQuestions
	 * 
	 * @param contractor
	 *            The contractor jpa entity to calculate the flag for
	 * @param operator
	 *            The operator jpa entity to calculate the contractor flag for
	 * @param conAudits
	 *            A list of audits for this contractor that may be required by
	 *            this operator
	 * @param auditAnswers
	 * @return
	 */
	public FlagColor calculate() {
		debug("FlagCalculator.calculate(" + contractor.getId() + "," + operator.getId() + ")");
		debug(" answerOnly=" + answerOnly);

		// Start positive and see if there are any violations
		FlagColor flagColor = FlagColor.Green;
		FlagColor overrideColor = null;

		// First see if there are any forced flags for this operator
		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().equals(operator)) {
				// Found the operator, is it forced?
				debug(" isForcedFlag=" + co.isForcedFlag() + " " + co.getForceFlag());
				if (co.isForcedFlag())
					overrideColor = co.getForceFlag();
			}
		}
		if (answerOnly && overrideColor != null)
			// Things can't get worse, just exit
			return overrideColor;

		debug(" flagColor=" + flagColor);

		for (AuditOperator audit : operator.getAudits()) {
			// Always start with Green
			audit.setContractorFlag(FlagColor.Green);
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() && audit.getRequiredForFlag() != null) {
				debug(" -- " + audit.getAuditType().getAuditName() + " - " + audit.getRequiredForFlag().toString());
				// The contractor requires this audit, make sure they have an
				// active one
				audit.setContractorFlag(audit.getRequiredForFlag());

				int annualAuditCount = 0;
				for (ContractorAudit conAudit : conAudits) {
					boolean statusOK = false;
					boolean typeOK = false;
					if (conAudit.getAuditStatus() == AuditStatus.Active)
						statusOK = true;
					if (conAudit.getAuditStatus() == AuditStatus.Resubmitted)
						statusOK = true;
					if (conAudit.getAuditStatus() == AuditStatus.Exempt)
						statusOK = true;
					if (conAudit.getAuditStatus() == AuditStatus.Submitted
							&& audit.getRequiredAuditStatus() == AuditStatus.Submitted)
						statusOK = true;
					if (conAudit.getAuditType().equals(audit.getAuditType()))
						typeOK = true;
					if (conAudit.getAuditType().getAuditTypeID() == AuditType.NCMS
							&& audit.getAuditType().getAuditTypeID() == AuditType.DESKTOP)
						typeOK = true;

					if (statusOK && typeOK) {
						// We found a matching "valid" audit for this 
						// contractor audit requirement
						debug(" ---- found");

						if (audit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM) {
							// We actually require THREE annual addendums 
							// before we consider this requirement complete
							annualAuditCount++;
						} else {
							audit.setContractorFlag(FlagColor.Green);
						}
					}
				}
				if (audit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM && annualAuditCount >= 3) {
					// Make sure we have atleast three annual addendums
					audit.setContractorFlag(FlagColor.Green);
				}
				// If an active audit doesn't exist, then set
				// the contractor's flag to the required color
				flagColor = setFlagColor(flagColor, audit.getContractorFlag());
			}
			if (answerOnly && flagColor.equals(FlagColor.Red))
				// Things can't get worse, just exit
				return flagColor;
		}

		debug(" flagColor=" + flagColor);

		// Initialize the flag color to the default Green
		for( OshaType oshaType : contractor.getOshas().keySet() ) {
			Map<String, OshaAudit> theseOshas = contractor.getOshas().get(oshaType);
			
			if( theseOshas != null ) {
				for (OshaAudit oa : theseOshas.values() ) {
					oa.setFlagColor(FlagColor.Green);
				}
			}
		}
		for (FlagOshaCriteria criteria : operator.getFlagOshaCriteria()) {
			if (criteria.isRequired()) {
				debug(" -- osha " + criteria.getFlagColor()); // Red or Amber

				if (contractor.getOshas() != null 
						&& contractor.getOshas().get(OshaType.OSHA) != null) {
					for (String key : contractor.getOshas().get(OshaType.OSHA).keySet()) {
						OshaAudit osha = contractor.getOshas().get(OshaType.OSHA).get(key);
						if ((key.equals(OshaAudit.AVG) && criteria.getLwcr().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getLwcr().isTimeAverage())) {
							if (criteria.getLwcr().isFlagged(osha.getLostWorkCasesRate()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getTrir().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getTrir().isTimeAverage())) {
							if (criteria.getTrir().isFlagged(osha.getRecordableTotalRate()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getFatalities().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getFatalities().isTimeAverage())) {
							if (criteria.getFatalities().isFlagged(osha.getFatalities()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
						}
						flagColor = setFlagColor(flagColor, osha.getFlagColor());
					}
				}

				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			}
		}
		debug(" flagColor=" + flagColor);

		for (Map<String, AuditData> tempMap : auditAnswers.values()) {
			for (AuditData data : tempMap.values()) {
				// The flag colors should always start Green, but sometimes they
				// are still set from the previous operator's loop
				data.setFlagColor(FlagColor.Green);
			}
		}

		// For each operator criteria, get the contractor's
		// answer and see if it triggers the flag color
		for (FlagQuestionCriteria criteria : operator.getFlagQuestionCriteria()) {
			if (criteria.getChecked().equals(YesNo.Yes)) {
				// This question is required by the operator
				Map<String, AuditData> answerMap = auditAnswers.get(criteria.getAuditQuestion().getId());
				if (answerMap != null && answerMap.size() > 0) {
					// The contractor has answered this question so it needs
					// to be correct
					if (answerMap.size() == 1) {
						AuditData data = null;
						for (AuditData data2 : answerMap.values())
							data = data2;

						flagColor = flagData(flagColor, criteria, data);
					} else {
						// We have multiple answers, this could be EMR
						for (AuditData data : answerMap.values()) {
							data.setFlagColor(null);
						}
						MultiYearScope scope = criteria.getMultiYearScope();
						if (MultiYearScope.LastYearOnly.equals(scope)) {
							AuditData data = null;

							// Get the most recent year
							int mostRecentYear = 0;
							for (String yearString : answerMap.keySet()) {
								try {
									int year = Integer.parseInt(yearString);
									if (year > mostRecentYear) {
										mostRecentYear = year;
										data = answerMap.get(yearString);
									}
								} catch (Exception e) {
									System.out.println("Ignoring answer with year key: " + yearString);
								}
							}
							flagColor = flagData(flagColor, criteria, data);

						} else if (MultiYearScope.AllThreeYears.equals(scope)) {
							for (AuditData data : answerMap.values()) {
								flagColor = flagData(flagColor, criteria, data);
							}

						} else if (MultiYearScope.ThreeYearAverage.equals(scope)) {
							AuditData.addAverageData(answerMap);
							AuditData data = answerMap.get(OshaAudit.AVG);
							flagColor = flagData(flagColor, criteria, data);

						} else {
							// This shouldn't happen
							System.out.println("We have more than answer for "
									+ criteria.getAuditQuestion().getQuestion() + " and scope '" + scope
									+ "' is not defined");
							return FlagColor.Red;
						}
					}
				}
				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			} // if criteria.isChecked...
		} // for
		debug(" flagColor=" + flagColor);

		// Calculate the insurance certificate flags colors
		if (operator.getCanSeeInsurance().equals(YesNo.Yes)) {
			FlagColor certFlagColor = null;

			for (Certificate certificate : contractor.getCertificates()) {
				if (certificate.getOperatorAccount().equals(operator)) {
					debug(" -- certificate" + certificate.getType() + " " + certificate.getOperatorAccount().getName());
					certFlagColor = setFlagColor(certFlagColor, certificate.getFlagColor());

					if (answerOnly && certFlagColor != null && certFlagColor.equals(FlagColor.Red))
						// Things can't get worse, just exit
						return certFlagColor;
				}
			}

			if (certFlagColor == null) {
				certFlagColor = FlagColor.Red;
				if (!answerOnly) {
					// Display the "No Approved Certificates" on the screen
					Certificate certificate = new Certificate();
					certificate.setFlagColor(FlagColor.Red);
					certificate.setType("No Approved Certificates");
					certificate.setOperatorAccount(operator);
					certificate.setContractorAccount(contractor);
					certificate.setStatus(""); // This has no status because
					// it's not a real cert
					contractor.getCertificates().add(certificate);
				}
			}

			flagColor = setFlagColor(flagColor, certFlagColor);
			debug(" flagColor=" + flagColor);
		}

		debug(" flagColor=" + flagColor);

		if (overrideColor != null)
			return overrideColor;

		return flagColor;
	}

	private FlagColor flagData(FlagColor flagColor, FlagQuestionCriteria criteria, AuditData data) {
		if (data != null && data.getAnswer() != null && data.getAnswer().length() > 0) {
			// The contractor has answered this question
			// so it needs
			// to be correct
			boolean isFlagged = false;

			if (criteria.isValidationRequired() && !data.isVerified()) {
				isFlagged = true;
			}

			if (criteria.isFlagged(data.getAnswer())) {
				isFlagged = true;
			}

			if (isFlagged) {
				data.setFlagColor(setFlagColor(data.getFlagColor(), criteria.getFlagColor()));
				flagColor = setFlagColor(flagColor, criteria.getFlagColor());
			} else {
				data.setFlagColor(FlagColor.Green);
			}
		}
		return flagColor;
	}

	public WaitingOn calculateWaitingOn() {
		debug("FlagCalculator.calculateWaitingOn(" + contractor.getId() + "," + operator.getId() + ")");

		ContractorOperator co = null;
		// First see if there are any forced flags for this operator
		for (ContractorOperator co2 : contractor.getOperators()) {
			if (co2.getOperatorAccount().equals(operator)) {
				co = co2;
				break;
			}
		}
		if (co == null)
			return WaitingOn.None; // This contractor is not associated with
		// this operator, so nothing to do now

		if (!contractor.isActiveB())
			return WaitingOn.Contractor; // This contractor is delinquent

		// Operator Relationship Approval
		if (YesNo.Yes.equals(operator.getApprovesRelationships())) {
			if (co.getWorkStatus().equals("P"))
				// Operator needs to approve/reject this contractor
				return WaitingOn.Operator;
			if (co.getWorkStatus().equals("N"))
				// Operator has already rejected this
				// contractor, and there's nothing else
				// they can do
				return WaitingOn.None;
		}

		// Billing
		if (contractor.isPaymentOverdue())
			return WaitingOn.Contractor; // The contractor has an unpaid
		// invoice due

		// If waiting on contractor, immediately exit, otherwise track the other
		// parties
		boolean waitingOnPics = false;
		boolean waitingOnOperator = false;

		// PQF, Desktop & Office Audits
		for (AuditOperator audit : operator.getAudits()) {
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() && audit.getRequiredForFlag() != null) {

				for (ContractorAudit conAudit : conAudits) {
					if (conAudit.getAuditType().equals(audit.getAuditType())
							&& (conAudit.getAuditStatus().equals(AuditStatus.Pending) || (conAudit.getAuditStatus()
									.equals(AuditStatus.Submitted) && audit.getRequiredAuditStatus().equals(
									AuditStatus.Active)))) {
						// We found a matching pending or submitted audit for
						// this contractor
						// Whose fault is it??
						debug(" ---- found");
						if (conAudit.getAuditType().isPqf()) {
							if (conAudit.getAuditStatus().equals(AuditStatus.Pending))
								return WaitingOn.Contractor; // The
							// contractor
							// still needs
							// to submit
							// their PQF
							if (conAudit.getPercentVerified() > 0)
								return WaitingOn.Contractor; // The
							// contractor
							// needs to send
							// us updated
							// information
							// (EMR, OSHA,
							// etc)

							// This PQF must be submitted and not verified yet
							// at all, so it's PICS' fault
							waitingOnPics = true;
						}
						if (conAudit.getAuditType().getAuditTypeID() == AuditType.OFFICE)
							return WaitingOn.Contractor; // The contractor
						// either needs to
						// schedule the
						// audit or close
						// out RQs
						if (conAudit.getAuditType().getAuditTypeID() == AuditType.DESKTOP) {
							if (conAudit.getAuditStatus().equals(AuditStatus.Submitted))
								return WaitingOn.Contractor; // The
							// contractor
							// needs to
							// close out RQs
							// This desktop still hasn't been performed by PICS
							waitingOnPics = true;
						}

					}
				}
			}
		}

		// Certificates
		if (operator.getCanSeeInsurance().equals(YesNo.Yes)) {

			int count = 0;
			for (Certificate certificate : contractor.getCertificates()) {
				if (certificate.getOperatorAccount().equals(operator)) {
					debug(" -- certificate" + certificate.getType() + " " + certificate.getOperatorAccount().getName());
					count++;
					if (certificate.getStatus().equals("Rejected"))
						// The contractor should upload a new cert
						return WaitingOn.Contractor;
					if (certificate.getStatus().equals("Expired"))
						// The contractor should upload a new cert
						return WaitingOn.Contractor;
					
					// These next two sections may need to change as InsureGuard 
					// gets more options regarding who can/should do each process
					if (certificate.getStatus().equals("Pending"))
						waitingOnOperator = true;
					if (certificate.getVerified().equals(YesNo.No))
						waitingOnPics = true;
				}
			}

			if (count == 0)
				// The contractor hasn't uploaded any certificates for this operator
				return WaitingOn.Contractor;
		}

		// Conclusion
		if (waitingOnPics)
			return WaitingOn.PICS;
		if (waitingOnOperator)
			// only show the operator if contractor and pics are all done
			return WaitingOn.Operator;

		// If everything is done, then quit with waiting on = no one
		return WaitingOn.None;
	}

	/**
	 * Set the flag color, but only let it get worse Green to Red but not
	 * reverse
	 * 
	 * @param oldColor
	 * @param newColor
	 * @return
	 */
	static private FlagColor setFlagColor(FlagColor oldColor, FlagColor newColor) {
		if (newColor == null)
			// Don't change anything
			return oldColor;

		if (oldColor == null) {
			System.out.println("WARNING: oldColor == null");
			// Now we've changed this because of insurance
			// oldColor = FlagColor.Green;
			return newColor;
		}

		// If 2-Red is greater (worse) than 0-Green, then change the color
		if (newColor.ordinal() > oldColor.ordinal())
			oldColor = newColor;
		return oldColor;
	}

	protected void debug(String message) {
		if (!debug)
			return;
		Date now = new Date();
		System.out.println(now.toString() + message);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public List<ContractorAudit> getConAudits() {
		return conAudits;
	}

	public void setConAudits(List<ContractorAudit> conAudits) {
		this.conAudits = conAudits;
	}

	public Map<Integer, Map<String, AuditData>> getAuditAnswers() {
		return auditAnswers;
	}

	public void setAuditAnswers(Map<Integer, Map<String, AuditData>> auditAnswers) {
		this.auditAnswers = auditAnswers;
	}

	public boolean isAnswerOnly() {
		return answerOnly;
	}

	public void setAnswerOnly(boolean answerOnly) {
		this.answerOnly = answerOnly;
	}

}
