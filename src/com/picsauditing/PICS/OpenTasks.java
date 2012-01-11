package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.LocaleController;

@SuppressWarnings("serial")
public class OpenTasks extends TranslationActionSupport {
	private boolean hasImportPQF = false;
	private boolean importPQFComplete = false;
	private boolean openReq = false;

	/**
	 * Restricts Open Tasks based on user permissions and viewed contractor account.
	 * 
	 * @param contractor
	 * @param user
	 * @return
	 */
	public List<String> getOpenTasks(ContractorAccount contractor, User user) {
		ArrayList<String> openTasks = new ArrayList<String>();

		hasImportPQF = false;
		importPQFComplete = false;
		openReq = false;

		// Adding a note to agree to terms of updated Contractor Agreement
		Permissions permissions = new Permissions();
		try {
			permissions.login(user);
			LocaleController.setLocaleOfNearestSupported(permissions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// check if trades need review
		if (contractor.getTrades().size() == 0) {
			openTasks.add(getTextParameterized("ContractorWidget.message.NoTradesSelected", contractor.getId()));
		}
		if (contractor.isNeedsTradesUpdated()) {
			openTasks.add(getTextParameterized("ContractorWidget.message.NeedsTradesUpdated", contractor.getId()));
		}

		if (!contractor.isAgreementInEffect()
				&& (permissions.hasPermission(OpPerms.ContractorBilling)
						|| permissions.hasPermission(OpPerms.ContractorAdmin) || permissions
						.hasPermission(OpPerms.ContractorSafety))) {
			openTasks.add(getTextParameterized("ContractorWidget.message.UpdatedAgreement", contractor.getId()));
		}

		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
				if (audit.hasCaoStatusBefore(AuditStatus.Submitted))
					openTasks.add(getTextParameterized("ContractorWidget.message.ImportAndSubmitPQF", audit.getId()));

				hasImportPQF = true;
				importPQFComplete = audit.hasCaoStatus(AuditStatus.Complete);
			}
		}

		if (permissions.hasPermission(OpPerms.ContractorAdmin) || user.getAccount().isAdmin()) {
			if (contractor.getUsers().size() == 1 && !contractor.getSoleProprietor()
					&& DateBean.getDateDifference(contractor.getCreationDate()) > -180) {
				openTasks.add(getText("ContractorWidget.message.RequiresTwoUsers"));
			}

			if (contractor.getAccountLevel().isBidOnly()) {
				openTasks.add(getTextParameterized("ContractorWidget.message.BidOnlyUpdgrade", contractor
						.getPaymentExpires(), contractor.getId()));
			}
		}

		if (permissions.hasPermission(OpPerms.ContractorBilling) || user.getAccount().isAdmin()) {
			String billingStatus = contractor.getBillingStatus();
			if ("Upgrade".equals(billingStatus)
					|| ("Renewal".equals(billingStatus) && contractor.getAccountLevel().isBidOnly())) {
				openTasks.add(getTextParameterized("ContractorWidget.message.GenerateInvoice"
						+ ((user.getAccount().isAdmin()) ? ".IsAdmin" : ""), contractor.getId()));
			}

			if (contractor.getBalance().compareTo(BigDecimal.ZERO) > 0) {
				for (Invoice invoice : contractor.getInvoices()) {
					if (invoice.getStatus().isUnpaid()) {
						openTasks.add(getTextParameterized("ContractorWidget.message.OpenInvoiceReminder"
								+ ((user.getAccount().isAdmin() ? ".IsAdmin" : "")), invoice.getId(), invoice
								.getBalance(), invoice.getDueDate()));
					}
				}
			}

			if (!contractor.isPaymentMethodStatusValid() && contractor.isMustPayB()) {
				openTasks.add(getTextParameterized("ContractorWidget.message.UpdatePaymentMethod"
						+ ((user.getAccount().isAdmin() ? ".IsAdmin" : "")), contractor.getId()));
			}
		}

		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (conAudit.getAuditType().isCanContractorView() && !conAudit.isExpired()) {
				if (isOpenTaskNeeded(conAudit, user, permissions)) {
					addAuditOpenTasks(conAudit, openTasks, user, permissions);
				}
			}
		}

		if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
			if (contractor.getWebcam() != null && contractor.getWebcam().getTrackingNumber() != null
					&& contractor.getWebcam().getTrackingNumber().trim().length() > 0) {
				openTasks.add(getTextParameterized("ContractorWidget.message.WebcamHasShipped", contractor.getWebcam()
						.getTrackingNumber()));
			}
		}

		// OQ: Add unmapped employees
		if (contractor.isRequiresOQ()) {
			if (contractor.getAssessmentResultStages().size() > 0) {
				boolean unmapped = false;

				for (AssessmentResultStage stage : contractor.getAssessmentResultStages()) {
					if (stage.getPicsEmployee() == null) {
						unmapped = true;
						break;
					}
				}

				if (unmapped)
					if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
						openTasks.add(getText("ContractorWidget.message.AssessmentResultsNeedMatching"));
					}
			}
		}

		return openTasks;
	}

	private boolean addAuditOpenTasks(ContractorAudit conAudit, ArrayList<String> openTasks, User user,
			Permissions permissions) {
		String auditName = getText(conAudit.getAuditType().getI18nKey("name"));
		Object showAuditFor = (conAudit.getAuditFor() != null && !conAudit.getAuditFor().isEmpty()) ? 1 : 0;
		String auditFor = conAudit.getAuditFor();
		
		if (conAudit.getAuditType().getClassType().isPolicy()) {
			if (permissions.hasPermission(OpPerms.ContractorInsurance) || user.getAccount().isAdmin()) {
				if (conAudit.hasCaoStatus(AuditStatus.Incomplete)) {
					openTasks.add(getTextParameterized("ContractorWidget.message.FixPolicyIssues", conAudit
							.getId(), auditName));
				} else {
					openTasks.add(getTextParameterized("ContractorWidget.message.UploadAndSubmitPolicy",
							conAudit.getId(), auditName));
				}
			}
		} else if (conAudit.getAuditType().isRenewable() && conAudit.isAboutToExpire()) {
			if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
				openTasks.add(getTextParameterized("ContractorWidget.message.ResubmitPolicy", conAudit
						.getId(), auditName, showAuditFor, auditFor));
			}
		} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()
				&& (conAudit.getAuditType().getId() != AuditType.WA_STATE_VERIFICATION || (conAudit
						.getAuditType().getId() == AuditType.WA_STATE_VERIFICATION && conAudit
						.hasCaoStatusAfter(AuditStatus.Pending)))
				&& (conAudit.getAuditType().getId() != AuditType.SHELL_COMPETENCY_REVIEW)) {
			if (conAudit.hasCaoStatus(AuditStatus.Submitted)) {
				// Submitted
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
					Integer conAuditID = conAudit.getId();
					String text = getTextParameterized("ContractorWidget.message.OpenRequirements",
							conAuditID, auditName, showAuditFor, auditFor);
					if (conAudit.getAuditType().getId() == AuditType.COR) {
						text = getTextParameterized("ContractorWidget.message.OpenRequirementsCOR",
								conAudit.getId(), auditName, showAuditFor, auditFor);
					}
					if (!openReq) {
						text += "<br/>" + getText("ContractorWidget.message.OpenRequirementsNote");
						openReq = true;
						if (conAudit.getAuditType().isDesktop()) {
							text += "<br/>" + getText("ContractorWidget.message.ManualAuditNote");
						}
					}
					openTasks.add(text);
				}
			} else {
				// Pending
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
					String text = "";
					if (conAudit.getAuditType().getId() == AuditType.OFFICE
							&& conAudit.getScheduledDate() == null) {
						text = getTextParameterized(
								"ContractorWidget.message.ScheduleYourImplementationAudit", conAudit
										.getId(), auditName, showAuditFor, auditFor);
					} else {
						Integer showScheduledDate = (conAudit.getScheduledDate() != null) ? 1 : 0;
						Integer showAuditor = (conAudit.getAuditor() != null) ? 1 : 0;
						if (conAudit.getAuditType().getId() == AuditType.DESKTOP) {
							text = getTextParameterized(
									"ContractorWidget.message.UpcomingAuditConductedBy",

									conAudit.getId(), auditName, showAuditor,
									(conAudit.getAuditor() != null) ? conAudit.getAuditor().getName() : "",
									showScheduledDate, conAudit.getScheduledDate());
						} else if (conAudit.getAuditType().getId() == AuditType.COR) {
							text = getTextParameterized(
									"ContractorWidget.message.CompleteAndSubmitAudit",
									conAudit.getId(), auditName, showAuditFor, auditFor);
							text += "<br/>" + getTextParameterized("ContractorWidget.message.ReviewCORNote", conAudit.getCreationDate());
						} else {
							text = getTextParameterized(
									"ContractorWidget.message.PrepareForAnUpcomingAudit",

									conAudit.getId(), auditName, showAuditFor, auditFor, showScheduledDate,
									conAudit.getScheduledDate(), showAuditor,
									(conAudit.getAuditor() != null) ? conAudit.getAuditor().getName() : "");
							if (conAudit.getAuditType().isImplementation()) {
								text +=  "<br/>" + getText("ContractorWidget.message.ImplementationAuditNote");
							}
						}
					}
					openTasks.add(text);
				}
			}
		} else if (conAudit.getAuditType().isCanContractorEdit()
				&& conAudit.getAuditType().getId() != AuditType.IMPORT_PQF) {
			if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
				if (conAudit.getAuditType().isPqf() && hasImportPQF) {
					// Show a message for filling out the rest of the PQF if the Import PQF is COMPLETE
					if (importPQFComplete) {
						openTasks.add(getTextParameterized("ContractorWidget.message.PQFOtherRegistry",
								conAudit.getId()));
					}
				} else if (conAudit.getAuditType().getId() == AuditType.COR && conAudit.hasCaoStatus(AuditStatus.Submitted)) {
					String text = getTextParameterized("ContractorWidget.message.ReviewCOR",
							conAudit.getId(), auditName, showAuditFor, auditFor);
					text += "<br/>" + getTextParameterized("ContractorWidget.message.ReviewCORNote", conAudit.getCreationDate());
					openTasks.add(text);
				} else {
					openTasks.add(getTextParameterized("ContractorWidget.message.CompleteAndSubmitAudit",
							conAudit.getId(), auditName, showAuditFor, auditFor));
				}
			}
		} else if ((conAudit.getAuditType().getId() == AuditType.HSE_COMPETENCY ||
				conAudit.getAuditType().getId() == AuditType.HSE_COMPETENCY_REVIEW)
				&& conAudit.hasCaoStatus(AuditStatus.Resubmit)) {
			Integer conAuditID = conAudit.getId();
			String text = getTextParameterized("ContractorWidget.message.OpenRequirements", conAuditID,
					auditName, showAuditFor, auditFor);
			if (!openReq) {
				text += "<br/>" + getText("ContractorWidget.message.OpenRequirementsNote");
				openReq = true;
			}
			openTasks.add(text);
		}
		return openReq;
	}

	private boolean isOpenTaskNeeded(ContractorAudit conAudit, User user, Permissions permissions) {
		int needed = 0;

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
					if (conAudit.getAuditType().isCanContractorEdit()) {
						// Maybe use conAudit.isAboutToRenew() instead of
						// conAudit.getAuditType().isRenewable() && conAudit.isAboutToExpire()
						if (conAudit.getAuditType().getId() == AuditType.WA_STATE_VERIFICATION) {
							if (cao.getStatus().before(AuditStatus.Complete)) {
								needed++;
							}
						} else if (cao.getStatus().before(AuditStatus.Submitted)
								|| cao.getStatus() == AuditStatus.Resubmit
								|| (conAudit.getAuditType().isRenewable() && conAudit.isAboutToExpire() && !cao
										.getStatus().isComplete())) {
							needed++;
						}
					} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()) {
						if (cao.getStatus().before(AuditStatus.Complete)) {
							needed++;
						}
					}
					if (conAudit.getAuditType().getId() == AuditType.COR && conAudit.hasCaoStatus(AuditStatus.Submitted)) {
						needed++;
						}

				}
				if (permissions.hasPermission(OpPerms.ContractorInsurance) || user.getAccount().isAdmin()) {
					if (cao.getStatus().before(AuditStatus.Submitted)) {
						needed++;
					}
				}
			}
		}
		return needed > 0;
	}
}
