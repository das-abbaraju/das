package com.picsauditing.actions.contractors.risk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.toggle.FeatureToggle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorEditRiskLevel extends ContractorActionSupport implements Preparable {
    private static final Logger logger = LoggerFactory.getLogger(ContractorEditRiskLevel.class);

	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private EmailSender emailSender;
    @Autowired
    private BillingService billingService;
    @Autowired
    private EmailBuilder emailBuilder;

    protected boolean safetySensitive;
	protected LowMedHigh safetyRisk;
	protected LowMedHigh productRisk;
	protected LowMedHigh transportationRisk;
	private EmailQueue emailQueue;



	public ContractorEditRiskLevel() {
		noteCategory = NoteCategory.RiskRanking;
		subHeading = "Contractor Risk Levels";
	}

	@Override
	public void prepare() throws Exception {
		int id = getParameter("id");
		if (id > 0)
			contractor = contractorAccountDao.find(id);
	}

	@Override
	public String execute() throws Exception {
		checkPermissions();

		return SUCCESS;
	}

	protected void checkPermissions() throws Exception {
		if (!contractor.getStatus().isDemo())
			tryPermissions(OpPerms.RiskRank);
	}

	public String save() throws Exception {
		checkPermissions();

		String userName = userDAO.find(permissions.getUserId()).getName();
		List<String> noteSummary = new ArrayList<String>();
		LowMedHigh oldSafety = contractor.getSafetyRisk();
		LowMedHigh oldProduct = contractor.getProductRisk();
		LowMedHigh oldTransportation = contractor.getTransportationRisk();
        boolean oldSafetySensitive = contractor.isSafetySensitive();
		boolean needsPqfReset = false;

        if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_SAFETY_SENSITIVE_ENABLED)) {
            if (contractor.getAccountLevel().isListOnly()) {
                if (safetySensitive) {
                    addActionError("You cannot change a List Only contractor to Safety Sensitive. " +
                            "Please change this contractor's Account Level to a Full Account and then change the sensitivity.");
                    return SUCCESS;
                }
            }

            if (oldSafetySensitive != safetySensitive) {
                String oldSensitivityNote = "Safety Sensitive";
                String newSensitivityNote = "Safety Sensitive";
                if (!oldSafetySensitive) {
                    oldSensitivityNote = "Not " + oldSensitivityNote;
                }
                if (!safetySensitive) {
                    newSensitivityNote = "Not " + newSensitivityNote;
                }
                noteSummary.add("changed from " + oldSensitivityNote + " to "
                        + newSensitivityNote);
                contractor.setSafetySensitive(safetySensitive);
                flagClearCache();
                needsPqfReset = true;

                // TODO: if downgrade, need to send email to Billing. Need new email template.
                // checkSafetyStatus(oldSafety, safetyRisk);

                resetPqf();
                billingService.syncBalance(contractor);
                contractorAccountDao.save(contractor);
                addActionMessage("Successfully updated Safety Sensitivity");
            }
        }
        else {
            if (contractor.getAccountLevel().isListOnly()) {
                if (!isListOnlyEligibleForNewProductRisk(productRisk)) {
                    addActionError("You cannot change a List Only contractor's Product Risk to " + productRisk.toString()
                            + ". Please change this contractor's Account Level to a Full Account "
                            + "and then change the Product Risk.");
                    return SUCCESS;
                }
                if (!isListOnlyEligibleForNewSafetyRisk(safetyRisk)) {
                    addActionError("You cannot change a List Only contractor's Safety Risk to " + productRisk.toString()
                            + ". Please change this contractor's Account Level to a Full Account "
                            + "and then change the Safety Risk.");
                    return SUCCESS;
                }
            }

            if (safetyRisk != null && !oldSafety.equals(safetyRisk)) {
                noteSummary.add("changed the safety risk level from " + oldSafety.toString() + " to "
                        + safetyRisk.toString());
                contractor.setSafetyRisk(safetyRisk);
                contractor.setSafetyRiskVerified(new Date());
                flagClearCache();

                if (isRiskChanged(oldSafety, safetyRisk))
                    needsPqfReset = true;

                checkSafetyStatus(oldSafety, safetyRisk);
            }

            if (productRisk != null && !oldProduct.equals(productRisk)) {
                noteSummary.add("changed the product risk level from " + oldProduct.toString() + " to "
                        + productRisk.toString());
                contractor.setProductRisk(productRisk);
                contractor.setProductRiskVerified(new Date());
                flagClearCache();

                if (isRiskChanged(oldProduct, productRisk))
                    needsPqfReset = true;
            } else if (oldProduct == null && productRisk != null) {
                // Add a product risk if it doesn't exist...?
                noteSummary.add("set product risk level to " + productRisk.toString());
                contractor.setProductRisk(productRisk);
                flagClearCache();
            }

            if (transportationRisk != null && !oldTransportation.equals(transportationRisk)) {
                noteSummary.add("changed the transportation risk level from " + oldTransportation.toString() + " to "
                        + transportationRisk.toString());
                contractor.setTransportationRisk(transportationRisk);
                contractor.setTransportationRiskVerified(new Date());
                flagClearCache();
            } else if (oldTransportation == null && transportationRisk != null) {
                noteSummary.add("set transportation risk level to " + transportationRisk.toString());
                contractor.setTransportationRisk(transportationRisk);
                flagClearCache();
            }

            if (!safetyRisk.equals(oldSafety) || (productRisk != null && !productRisk.equals(oldProduct))
                    || (transportationRisk != null && !transportationRisk.equals(oldTransportation))) {
                if (needsPqfReset) {
                    resetPqf();
                }

                billingService.syncBalance(contractor);
                contractorAccountDao.save(contractor);
                addActionMessage("Successfully updated Risk Level" + (noteSummary.size() > 1 ? "s" : ""));
            }
        }

        if (noteSummary.size() > 0) {
            Note note = new Note();
            note.setAccount(contractor);
            note.setAuditColumns(permissions);
            note.setSummary(userName + " " + Strings.implode(noteSummary, " and "));
            note.setNoteCategory(NoteCategory.General);
            note.setCanContractorView(false);
            note.setViewableById(Account.EVERYONE);
            noteDAO.save(note);
		}

		return SUCCESS;
	}

	private void checkSafetyStatus(LowMedHigh oldSafety, LowMedHigh newSafety) {
		if (newSafety.ordinal() < oldSafety.ordinal())
			buildAndSendBillingRiskDowngradeEmail(oldSafety, newSafety);
	}

	private void buildAndSendBillingRiskDowngradeEmail(LowMedHigh currentRisk, LowMedHigh newRisk) {
		emailBuilder.clear();
		emailBuilder.setTemplate(EmailTemplate.RISK_LEVEL_DOWNGRADED_EMAIL_TEMPLATE);
		emailBuilder.setFromAddress(EmailAddressUtils.PICS_IT_TEAM_EMAIL);
		emailBuilder.setToAddresses(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));
		emailBuilder.addToken("contractor", contractor);
		emailBuilder.addToken("currentSafetyRisk", currentRisk);
		emailBuilder.addToken("newSafetyRisk", newRisk);

		try {
			emailQueue = emailBuilder.build();
			emailQueue.setHighPriority();
			emailQueue.setSubjectViewableById(Account.PicsID);
			emailQueue.setBodyViewableById(Account.PicsID);
			emailSender.send(emailQueue);
		} catch (Exception e) {
			logger.error("Cannot send email to  {} ({})\n{}", new Object[] { contractor.getName(), contractor.getId(),
					e });
		}
	}

	private boolean isRiskChanged(LowMedHigh oldSafetyRisk, LowMedHigh newSafetyRisk) {
		if ((newSafetyRisk.equals(LowMedHigh.Med) || newSafetyRisk.equals(LowMedHigh.High))
				&& (oldSafetyRisk.equals(LowMedHigh.None) || oldSafetyRisk.equals(LowMedHigh.Low)))
			return true;
		return false;
	}

	private void resetPqf() {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().isPicsPqf()) {
				resetCaos(audit);
				auditBuilder.recalculateCategories(audit);
				dao.save(audit);
			}
		}
	}

	private void resetCaos(ContractorAudit audit) {
		for (ContractorAuditOperator cao : audit.getOperators()) {
			if (cao.isVisible() && cao.getStatus().after(AuditStatus.Pending)) {
				ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Resubmit, permissions);
				if (caow != null) {
					caow.setNotes(getText("ContractorEditRiskLevel.statuschanged"));
					dao.save(caow);
				}
			}
		}
	}

    public boolean isSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(boolean safetySensitive) {
        this.safetySensitive = safetySensitive;
    }

    public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

	public LowMedHigh getTransportationRisk() {
		return transportationRisk;
	}

	public void setTransportationRisk(LowMedHigh transportationRisk) {
		this.transportationRisk = transportationRisk;
	}

	private boolean isListOnlyEligibleForNewProductRisk(LowMedHigh newProductRisk) {
		// Low Risk Material Supplier Only
		if (contractor.isMaterialSupplierOnly() && newProductRisk.equals(LowMedHigh.Low))
			return true;
		return false;
	}

	private boolean isListOnlyEligibleForNewSafetyRisk(LowMedHigh newSafetyRisk) {
		// Low Safety Risk Offsite Services
		if (contractor.isOffsiteServices() && !contractor.isOnsiteServices() && newSafetyRisk.equals(LowMedHigh.Low))
			return true;
		return false;
	}

}
